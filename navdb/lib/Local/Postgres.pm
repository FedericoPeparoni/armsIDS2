# vim:set ts=4 sts=4 sw=4 et:
package Local::Postgres;

#
# Library for finding and calling Postgres client programs, such
# as "psql".
#

use warnings;
use strict;

use Carp;
use English;
use File::Basename;
use Local::Utils qw(:all);
use Local::ShellQuote qw(:all);
use Local::Versions qw(:all);

my $PGUSER;
my $OS_USER;
my $RUNUSER;
my $POSTGRES_BIN_DIR;

# All client tools this llibrary supports.
# 
# Keys are program names, values are hashrefs with 2 keys each:
#   {
#     $exe => "/path/to/executable/file",
#     $ver => <program version>
#   }
# 
# This hash is initialized as necessary by the "do_find_prog" function below.
my %PG_PROGS = (
    psql => undef,
    pg_dump => undef,
    pg_restore => undef
);

#
# This function searches for a Postgres installation and modifies
# the $PATH if necessary. It returns true if $PATH was modified.
#
do {
    my $guessed;
    sub do_guess_postgres_bindir() {
        my $path_modified;
        if (not $guessed and not $POSTGRES_BIN_DIR) {
            my $guess = sub {
                my @patterns;
                # On Linux they tend to be under /usr/pgsql-X.Y
                push @patterns, qw(/usr/pgsql-[0-9]*);
                # On Windows they tend to be under C:\Program Files\PostgreSQL\X.Y or similar
                if ($OSNAME eq 'cygwin' or $OSNAME eq 'msys') {
                    my $root_prefix = $OSNAME eq 'cygwin' ? "/cygdrive" : "";
                    my @drive_list = qw(c d);
                    for my $drive (@drive_list) {
                        push @patterns, "$root_prefix/$drive/Program Files/PostgreSQL/[0-9]*",
                                        "$root_prefix/$drive/Program Files (x86)/PostgreSQL/[0-9]*";
                    }
                }
                # search these directories; prefer higher versions over lower versions, so that
                # if both /usr/pgsql-9.2 and /usr/pgsq-9.3 are available, pick the latest one.
                for my $pattern (@patterns) {
                    # sort directories in descending order
                    my @sorted_dirs = sort { versioncmp (basename ($a), basename ($b)) * -1 } glob ($pattern);
                    for my $dir (@sorted_dirs) {
                        my $bindir = File::Spec->catfile ($dir, "bin");
                        if (stat $bindir and -d $bindir) {
                            my $file = File::Spec->catfile ($bindir, "psql");
                            if (which ($file)) {
                                return $bindir;
                            }
                        }
                    }
                };
                # not found
                return undef;
            };
            my $guessed_dir = &$guess;
            if ($guessed_dir) {
                prepend_to_search_path ($guessed_dir);
                $path_modified = 1;
            }
            $guessed = 1;
        };
        return $path_modified;
    }
};

# Usage: do_find_prog("psql")
# Finds and validates the given Postgres program.
sub do_find_prog ($) {
    # e.g., "psql"
    my $prog = shift;
    defined $prog or confess "invalid argument";
    exists $PG_PROGS{$prog} or confess "unsupported PG command \`$prog'";

    # this is the first time we are trying to find this program
    unless (defined $PG_PROGS{$prog}) {

        # Find the full path to program
        my $exe = which ($prog);

        # Couldn't find it => try to add more possible locations to $PATH and try again
        if (not $exe and do_guess_postgres_bindir()) {
            $exe = which ($prog);
        };
        # still couldn't find it => bail out
        if (not $exe) {
            die "unable to find $prog in $ENV{PATH}\n";
        };
        $exe = $prog;
        
        # Call that program with "--version" argument and extract its version number
        my $cmd = shell_quote ($exe, "--version");
        log_shell_cmd $cmd;
        my $ver_line = `$cmd`;
        my $ver;
        if ($ver_line and $ver_line =~ /^.*\s([0-9]+\.[0-9]+\.[0-9]+)\s*$/go) {
            $ver = $1;
        };
        if (not defined $ver or not length $ver) {
            die "unable to determine version of $exe\n";
        };
        log_info ("found $prog $ver: \`$exe'");

        # record program's full path and version in the $PG_PROGS hash
        $PG_PROGS{$prog} = {
            exe => $exe,
            ver => $ver,
        };
    }
    return $PG_PROGS{$prog}->{exe};
}

# Execute a postgres command by running it via /usr/sbin/runuser if necessary.
# This allows us to connect to the server without knowing the superuser's password,
# but only on Linux and typically when Postgres server is installed on the same
# host as this script. This is because Linux installations typically authenticate
# local connections using "ident" method. See Postgres user manual for details.
sub do_cmd($@) {
    my $prog = shift;
    local $_;
    my $exe = do_find_prog ($prog);
    my $cmd;
    if ($RUNUSER) {
        my $sub_cmd = shell_quote ($exe, @_);
        $cmd = shell_quote ($RUNUSER, "-c", $sub_cmd, $OS_USER);
    }
    else {
        $cmd = shell_quote ($exe, @_);
    };
    return $cmd;
}

# Shortcut for running psql with a few arguments always set
sub postgres_cmd_psql(@) {
    return do_cmd ("psql", (qw(--no-password --no-psqlrc --tuples-only --quiet --no-align -v ON_ERROR_STOP=1), @_));
}

# Shortcut for calling pg_dump
sub postgres_cmd_pg_dump(@) {
    return do_cmd ("pg_dump", (qw(--no-password), @_));
}

# Shortcut for calling pg_restore
sub postgres_cmd_pg_restore(@) {
    return do_cmd ("pg_restore", (qw(--no-password), @_));
}


# Usage: postgres_check_ver ($MIN_PG_VER, $MIN_POSTGIS_VER)
# Ensure that:
#   - psql & friends' version is >= $MIN_PG_VER
#   - the (remote) server version is also >= $MIN_PG_VERSION
#   - PostGIS is available on the server side and its version is >= $MIN_POSTGIS_VER
do {
    my $checked;
    sub postgres_check_ver($$) {
        unless ($checked) {
            my ($min_pg_ver, $min_postgis_ver) = @_;
            local $_;
            do_find_prog ("psql");
            for (keys (%PG_PROGS)) {
                if (defined ($PG_PROGS{$_})) {
                    if (versioncmp ($min_pg_ver, $PG_PROGS{$_}->{ver}) > 0) {
                        die "$PG_PROGS{$_}->{exe} version $PG_PROGS{$_}->{ver} is too old, minimum $min_pg_ver is required\n";
                    };
                };
            };
            my $cmd = chdir_cmd ("/tmp", postgres_cmd_psql ("-t", "-A", "-F", " ", "-c", "select default_version from pg_available_extensions where name ~* '^postgis\$'"));
            log_shell_cmd $cmd;
            my $postgis_ver = trim (`$cmd`);
            if (not defined $postgis_ver or not length ($postgis_ver)) {
                die "unable to determine PostGIS version\n";
            };
            log_info "found PostGIS $postgis_ver";
            if (versioncmp ($min_postgis_ver, $postgis_ver) > 0) {
                die "PostGIS version $postgis_ver is too old, minimum $min_postgis_ver is required\n";
            };
            $checked = 1;
        }
    }
};

# Return true if the database exists
sub postgres_db_exists($) {
  my $db_name = shift;
  my $psql_cmd = postgres_cmd_psql ("-t", "-l");
  my $cmd = chdir_cmd ("/", "$psql_cmd | cut -s -f1 -d '|' | awk '{print \$1}'");
  log_shell_cmd $cmd;
  grep { /^\Q$db_name\E$/ } (`$cmd`) and return 1;
  return undef;
}

# Terminate existing DB sessions to the database
sub postgres_kill_sessions($) {
    my $db_name = shift;
    my $sql = "select pg_terminate_backend(pid) from pg_stat_activity where datname='$db_name' and pid <> pg_backend_pid()";
    my $cmd = chdir_cmd ("/", postgres_cmd_psql ("-c", $sql)) . " >/dev/null";
    shell $cmd;
}

# Find (and remember) the given postgres client programs, such as "pg_dump"
sub postgres_find_progs(@) {
    for my $prog (@_) {
        do_find_prog ($prog)
    }
};

#
# Usage: postgres_init ($CONFIG)
#
# Initialize this library: find Postgres programs, find out connection parameters, etc.
#
# The argument is an instance of Local::ConfigOptions that contains the contents
# of navdb.conf file. This function makes use of the following configuration options:
#
#   POSTGRES_BIN_DIR        - directory containing Postgres client programs, such as "psql"
#   MATCH_OS_USER           - 0, 1 or empty: controls whether to switch user context in Linux
#   RUNUSER                 - Full path of the "runuser" program
#   PG*                     - Standard Postgres environment variables
#
sub postgres_init($) {
    my $conf = shift;
    my $conf_file = $conf->filename;

    # Postgres user, database and password
    $PGUSER = $conf->get("PGUSER") || "postgres";
    my $PGDATABASE = $conf->get("PGDATABASE") || $PGUSER;
    my $PGPASSWORD = $conf->get("PGPASSWORD"); #|| $PGUSER;

    # OS-level user account that corresponds to $PGUSER
    $OS_USER = $conf->get("OS_USER") || $PGUSER;
    my $current_user = (getpwuid ($EUID) or die "unable to determine current user id\n")[0];

    # check if we need to use "runuser" to switch user context when executing psql, etc.
    $RUNUSER = undef;

    # Run postgres programs as OS-level user "postgres" via /usr/sbin/runuser etc?
    my $match_os_user = trim ($conf->get("MATCH_OS_USER"));
    defined $match_os_user or $match_os_user = "";

    # "MATCH_OS_USER" is "1" or left unspecified: determine "runuser" executable name/path
    if ($match_os_user or length ($match_os_user) == 0) {
        # "runuser" executable defined in configuration -- make sure it's legit
        my ($runuser, $loc) = $conf->get ("RUNUSER");
        if (defined $runuser and length ($runuser)) {
            $RUNUSER = $runuser;
            stat $runuser or die "$loc $RUNUSER: $!\n";
            -f _ or die "$loc: $RUNUSER: not a regular file\n";
            -x _ or die "$loc: $RUNUSER: permission denied\n";
        }
        # "runuser" not defined in configuration -- try to detect it (Linux only)
        elsif ($OSNAME eq "linux") {
            for my $file (qw(/sbin/runuser /usr/sbin/runuser)) {
                if (stat ($file) and -f _ and -x _) {
                    $RUNUSER = $file;
                    last;
                }
            };
            # couldn't detect it, but MATCH_OS_USER was set to true -- error
            if (not defined ($RUNUSER) and $match_os_user) {
                die "can't find /usr/sbin/runuser\n";
            };
            # detected it, but we are not root -- error
            if (defined ($RUNUSER) and $EUID != 0) {
                die "this script must be run by user \`root'\n";
            };
        }
        # runuser not defined in configuration, this isn't Linux => just disable "runuser"
        else {
            if ($match_os_user) {
                log_warning "$conf_file: MATCH_OS_USER not supported on this platform (ignored)";
            };
            log_debug "os user account matching disabled";
            $RUNUSER = undef;
        };
    }
    # MATCH_OS_USER is "0": disable "runuser"
    else {
        $RUNUSER = undef;
    };

    # Check whether $OS_USER exists at the OS level
    if (defined ($RUNUSER)) {
        if (not $OS_USER or not getpwnam ($OS_USER)) {
            $RUNUSER = undef;
            $OS_USER = undef;
        }
    }
    else {
        $OS_USER = undef;
    };
    if (defined ($RUNUSER)) {
        log_debug "connections to database will be made as OS user \`$OS_USER' via \`$RUNUSER'";
    }

    # reset Postgres environment
    for (grep /^PG/o, keys (%ENV)) {
        delete $ENV{$_};
    };
    for (grep /^PG/o, ($conf->keys)) {
        my $val = $conf->get($_);
        $ENV{$_} = $val;
    };
    $ENV{PGOPTIONS} = ($ENV{PGOPTIONS} || '') . " --client-min-messages=WARNING";
    $ENV{PGUSER} = $PGUSER;
    $ENV{PGDATABASE} = $PGDATABASE;
    $ENV{PGPASSWORD} = $PGPASSWORD;
    log_debug "PG environment:";
    for (grep /^PG/o, sort (keys (%ENV))) {
        if (/PASSWORD/io) {
            log_debug "  $_=<hidden>"
        }
        else {
            my $val = $ENV{$_};
            log_debug "  $_=\"" . (defined $val ? $val : "") . "\"";
        }
    };

    # Add directory containing Postgres programs to $ENV{PATH}
    $POSTGRES_BIN_DIR = $conf->get ("POSTGRES_BIN_DIR");
    if (defined ($POSTGRES_BIN_DIR)) {
        stat $POSTGRES_BIN_DIR or die "$POSTGRES_BIN_DIR: $!\n";
        -d _ or die "$POSTGRES_BIN_DIR: not a directory\n";
        prepend_to_search_path ($POSTGRES_BIN_DIR);
    };

    # make sure "psql" exists
    do_find_prog "psql";

    # Make srue we can connect to server
    log_info "testing connection to database";
    shell (chdir_cmd ("/tmp", postgres_cmd_psql ("-l") . " >/dev/null"));
    $? == 0 or die "unable to connect to Postgres database\n";
}

BEGIN {
  use Exporter qw();
  our @ISA = qw(Exporter);
  our @EXPORT_OK = qw(
    postgres_init
    postgres_find_progs
    postgres_check_ver
    postgres_cmd_psql
    postgres_cmd_pg_dump
    postgres_cmd_pg_restore
    postgres_db_exists
    postgres_kill_sessions
  );
  our %EXPORT_TAGS = (all => [@EXPORT_OK]);
};

1;

