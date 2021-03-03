# vim: set ts=4 sts=4 sw=4 et:
package Local::Project;

#
# Initialization and config functions for NavDB project scripts
#

use warnings;
use strict;
use English;
use Carp;
use Local::Utils qw(:all);
use Local::ConfigOptions;
use File::Temp qw(tempfile);
use File::Spec::Functions;
use Local::Utils qw(:all);
use Local::ConfigOptions;
use Local::Postgres qw(:all);

our $PROJECT_CONFIG;
our $SQL_CONFIG;
our $DB_NAME;
our $DB_OWNER;
our $DFLT_PROJECT_CONFIG_FILE;
our $PROJECT_CONFIG_FILE;

# Load conf/navdb.conf; returns a ConfigOptions object
sub do_load_project_config() {
    my $filename = $PROJECT_CONFIG_FILE || "$CONF_DIR/navdb.conf";
    my $conf = Local::ConfigOptions->new ($filename);
    if (my $fh = IO::File->new ($filename)) {
        log_info "reading environment from $filename";
        my $failed;
        while (defined ($_ = $fh->getline)) {
            /^\s*(?:#.*)?$/o and next;
            my $loc = "$filename($.)";
            /^([A-Z][A-Z0-9_]+)\s*=\s*(.*?)\s*$/o and do {
                my ($key, $val) = ($1, $2);
                $val =~ s/^"(.*)"$/$1/g or $val =~ s/^'(.*)'$/$1/g;
                $conf->set ($key, $val, $loc);
                log_debug qq(  $key="$val");
                next;
            };
            log_error "$loc: syntax error";
            $failed = 1;
        };
        $fh->close or die "close(): $!\n";
        $failed and die "$filename: failed to load environment\n";
    }
    else {
        # Bail out if config file name was explicitly supplied on the command line,
        # in this case file must exist.
        #
        # Otherwise, if we are working with the default file name, ignore
        # the missing file.
        if (defined $PROJECT_CONFIG_FILE or not $!{ENOENT}) {
            die "$filename: $!\n";
        };
        log_debug "environment file \`$CONF_DIR/navdb.conf' not loaded: $!";
    };
    return $conf;
}

# Load sql/config.sql; returns a ConfigOptions object
# We read that file by passing it through "psql" in order to get the ":VAR" references
# expanded.
sub do_load_sql_config() {
    my %keys;

    # parse config.sql looking for lines of the form "\set VAR VALUE"; save
    # variable names in @keys.
    my $config_sql = catfile ($SQL_DIR, "config.sql");
    log_info ("reading internal configuration from $config_sql");
    my $conf = Local::ConfigOptions->new ($config_sql);
    open FH, $config_sql or die "$config_sql: $!\n";
    while (<FH>) {
        /^\s*(?:--.*)?$/o and next;
        /^\s*\\set\s+([a-zA-Z_][a-zA-Z0-0_]+)\s+(.*?)\s*$/o and do {
            $keys{$1} = undef;
            next;
        };
    };
    close FH or die "close(): $!\n";
    my @keys = keys(%keys);

    # use psql to evaluate these variables and save the results in %CONFIG hash:
    # - generate a temporary sql script that contains the original config.sql
    #   followed "\echo VAR :VAR" for each variable in @keys
    # - run that temp script through "psql" and parse the output of "\echo"
    # - save the values in %CONFIG hash
    my ($fh, $filename) = tempfile ("${PROGNAME}_XXXXXX", DIR => File::Spec->tmpdir(), SUFFIX => '.sql', UNLINK => 1);
    chmod 0644, $filename or die "$filename: $!\n";
    $fh->print ("\\i config.sql\n");
    for (@keys) {
        $fh->print ("\\echo $_ :$_\n");
    };
    $fh->flush;
    my $cmd = chdir_cmd ($SQL_DIR, postgres_cmd_psql ("-f", native_path ($filename)));
    log_shell_cmd $cmd;
    my @lines = `$cmd`;
    $? == 0 or die "$config_sql: failed to read internal configuration\n";
    for (@lines) {
        /^(\S+)\s+(.*?)\s*$/o and $conf->set ($1, $2, $config_sql);
    };
    $fh->close or die "close(): $!\n";

    for (@keys) {
        log_debug sprintf ("  \%-30s = [%s]", $_, scalar ($conf->get ($_)));
    };

    # save a few important variables in globals
    return $conf;
};

# Usage: project_init($prog_1, $prog_2, ...)
# Initialize the script (to be called from command line modules)
#
# The optional arguments is a list of postgres client programs to be searched
# for during intiialization, such as "pg_dump".
#
# Note that "psql" will always be searched for, you don't have to specify it here.
# 
my $INITIALIZED;
sub project_init(@) {
    # load conf/navdb.conf
    $PROJECT_CONFIG = do_load_project_config;
    # find psql, etc -- required for "load_sql_config" below
    postgres_init ($PROJECT_CONFIG);
    # load sql/config.sql
    $SQL_CONFIG = do_load_sql_config;
    my $sql_config = $SQL_CONFIG->filename;
    # Locate required postgres tools
    postgres_find_progs (@_);
    # check postgres & postgis versions
    do {
        my $postgres_ver_min = $SQL_CONFIG->get("navdb_min_pg_version") or confess "$sql_config: navdb_min_pg_version not defined!";
        my $postgis_ver_min = $SQL_CONFIG->get("navdb_min_postgis_version") or confess "$sql_config: navdb_min_postgis_version not defined!";
        postgres_check_ver $postgres_ver_min, $postgis_ver_min;
    };
    # set global vars
    $DB_OWNER = $SQL_CONFIG->get("navdb_owner") or confess "$sql_config: navdb_owner not defined!\n";
    unless (defined $DB_NAME) {
        $DB_NAME = ($PROJECT_CONFIG->get ("DB_NAME") || "navdb");
    };
    log_info "database name is \`$DB_NAME'";
    # done
    $INITIALIZED = 1;
}

# Make sure "project_init" has been called
sub ensure_init() {
    $INITIALIZED or confess "project_init must be called first!";
};

# Return true if NAVDB database exists
sub db_exists() {
    ensure_init;
    return postgres_db_exists $DB_NAME;
};

# Terminate all existing client connections to the database
sub kill_db_sessions() {
    ensure_init;
    postgres_kill_sessions ($DB_NAME);
}

# Usage: psql_superuser_cmd (@PSQL_ARGS);
sub psql_superuser_cmd(@) {
    ensure_init;
    my $cmd = postgres_cmd_psql (@_);
    return $cmd;
}

# Usage: psql_owner_script ($SCRIPT_FILE, @PSQL_EXTRA_ARGS)
sub psql_owner_script($@) {
    ensure_init;
    my $script_file = shift;

    log_debug "executing $script_file as user $DB_OWNER";

    # Create a temporary SQL script that contains
    #   set session authorization navdb;
    #   set role navdb;
    #   \i $script_file
    my ($fh, $filename) = tempfile ("${PROGNAME}_XXXXXX", DIR => File::Spec->tmpdir(), SUFFIX => '.sql', UNLINK => 1);
    chmod 0644, $filename or die "$filename: $!\n";
    $fh->print ("set session authorization $DB_OWNER;\n");
    $fh->print ("set role $DB_OWNER;\n");
    $fh->print ("\\i :__script_file__\n");
    $fh->flush or die "close(): $!\n";
    $fh->close;

    # Command for executing the temp script
    my $cmd = postgres_cmd_psql ("--dbname=$DB_NAME", "-f", native_path ($filename), "-v", "__script_file__=" . native_path ($script_file), @_);
    return $cmd;
}

# Usage: $locale_name = navdb_locale()
#
# Returns the name of the locale that should be used by PostgreSQL server when creating the database.
# Such names are different between Windows and Linux. This function just returns the value
# of "navdb_locale_unix" or "navdb_locale_windows" variables from "config.sql", depending on whether
# it looks like the server is running Window sor UNIX.
# 
do {
    my $navdb_locale;
    sub navdb_locale() {
        if (not defined ($navdb_locale)) {
            # I can't figure out how to determine what OS the server is running, other than this hack.
            # On Linux we have locale names like "en_US.utf8" in pg_collation table, on Windows we don't.
            my $cmd = chdir_cmd ("/", psql_superuser_cmd ("-c", "select collname from pg_collation where collname ~* '^en_US.utf-?8\$'"));
            log_shell_cmd $cmd;
            my $out = trim (`$cmd`);
            my $loc = $SQL_CONFIG->filename;
            # Looks like Linux
            if ($out) {
                $navdb_locale = $SQL_CONFIG->get("navdb_locale_unix") or confess "$loc: navdb_locale_unix not defined!";
            }
            # Looks like Windows
            else {
                $navdb_locale = $SQL_CONFIG->get ("navdb_locale_windows") or confess "$loc: navdb_locale_windows not defined!";
            }
        }
        return $navdb_locale;
    }
};

BEGIN {
  use Exporter qw();
  our @ISA = qw(Exporter);
  our @EXPORT_OK = qw(
    $DB_NAME
    $DB_OWNER
    $PROJECT_CONFIG
    $SQL_CONFIG
    $PROJECT_CONFIG_FILE
    $DFLT_PROJECT_CONFIG_FILE
    project_init
    db_exists
    kill_db_sessions
    psql_superuser_cmd
    psql_owner_script
    navdb_locale
  );
  our %EXPORT_TAGS = (all => [@EXPORT_OK]);
};

1;

