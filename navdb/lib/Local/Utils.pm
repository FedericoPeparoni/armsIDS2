# vim:set ts=2 sts=2 sw=2 et:

#
# Miscelaneous functions and utilities
#

package Local::Utils;
use warnings;
use strict;
use English;
use Carp;
use Config;
use File::Basename;
use File::Spec;
use IO::File;
use Local::ShellQuote qw(:all);

# This should be set to 1 if we are running the script that has been properly
# installed under "/usr/bin" etc, as part of an RPM package.
our $INSTALLED;

# "navdb"
our $PROGNAME = basename($0);

# Log level; this may be increased by the "-v" command line options to navdb
our $LOG_LEVEL = 2;

# Path of the "sql" directory; when the package is installed this will be /usr/share/navdb/sql
our $SQL_DIR = "sql";

# Path of the "conf" directory; when the package is installed this will be /etc/navdb
our $CONF_DIR = "conf";

# Terminal color codes
our ($TTY_BOLD, $TTY_FAINT, $TTY_RED, $TTY_BOLD_RED, $TTY_NORM);

# The subcommand, e.g. when calling "navd setup" this will be set to "setup"
our $SUBCOMMAND;

# The terminal file, "/dev/tty". We will use it to ask for user input,
# instead of STDIN to avoid mixing up user input with data (like load files)
# piped through STDIN.
our $TTY_IN;

# Set to true only if /dev/tty is available. This means it's OK to ask questions
# to user.
our $INTERACTIVE;

# Initialize ANSI terminal codes
BEGIN {
  if (-t 2 or $ENV{NAVDB_TTY_COLORS}) {
    $TTY_BOLD = "\e[1m";
    $TTY_FAINT = "\e[2m";
    $TTY_RED = "\e[0;31m";
    $TTY_BOLD_RED = "\e[1;31m";
    $TTY_NORM = "\e[0m";
  }
  else {
    $TTY_BOLD = $TTY_FAINT = $TTY_RED = $TTY_BOLD_RED = $TTY_NORM = "";
  };
};

# print out a log message to STDERR
sub do_log ($$@) {
  my $level = shift;
  if ($level <= $LOG_LEVEL) {
    my $prefix = shift;
    local $_;
    my $msg = join ('', grep { defined } @_);
    $msg =~ s/\s+$//;
    length ($msg) and print STDERR $prefix, $msg, ${TTY_NORM}, "\n";
  };
};

# print out an error message
sub log_error(@) {
  do_log 0, "${TTY_BOLD_RED}ERROR${TTY_NORM}: ${TTY_BOLD_RED}", @_;
}

# print out a warning message
sub log_warning(@) {
  do_log 1, "${TTY_BOLD_RED}WARNING${TTY_NORM}: ${TTY_BOLD_RED}", @_;
}

# print out a log message
sub log_notice(@) {
  do_log 2, '', @_;
}

# print out a log message
sub log_info(@) {
  do_log 3, '', @_;
}

# print out a log message
sub log_debug(@) {
  do_log 4, '', @_;
}

# log a shell command in verbose mode only
sub log_shell_cmd($) {
  my $cmd = shift;
  do_log 4, ${TTY_FAINT}, '% ', $cmd;
}

# Print out a command-line error and exit
sub cmdline_error(@) {
  log_error @_;
  if (defined $SUBCOMMAND) {
    log_error "type \`$PROGNAME $SUBCOMMAND --help' for more information\n";
  }
  else {
    log_error "type \`$PROGNAME --help' for more information\n";
  };
  exit 7;
}

# remove leading/trailing whitespace from a string
sub trim($) {
  my $s = shift;
  if (defined ($s)) {
    $s =~ s/^\s+//go;
    $s =~ s/\s+$//go;
  };
  return $s;
}

# run a (quoted) shell command and print it out in verbose mode
sub shell($) {
  my $cmd = shift;
  log_shell_cmd $cmd;
  system $cmd;
  return $?
}

# Wrap a command in a subshell that CD's to the given directory first.
# For example:
#   chdir_cmd ("/tmp", "echo hello")
# will return "( cd /tmp && echo hello ;  )"
sub chdir_cmd ($$) {
  my ($dir, $cmd) = @_;
  my $full_cmd = "( " . shell_quote ("cd", $dir) . " && " . $cmd . " ; )";
  return $full_cmd;
}

# Open /dev/tty for reading and set $INTERACTIVE to true if successful.
# We will use this file to ask user for input in "confirm" below.
# This is to avoid mixing up load data piped through STDIN and
# user's answers to "confirm".
#
# Returns true if a terminal is available for questions (i.e., calling
# "confirm" is safe").
sub interactive() {
  if (not defined $INTERACTIVE) {
    if ($TTY_IN = IO::File->new ("/dev/tty")) {
      $INTERACTIVE = 1;
    }
    else {
      $INTERACTIVE = 0;
    }
  };
  return $INTERACTIVE;
}

# Ask user to confirm (yes/no) a question. Returns:
#   1      -- if the answer was "yes"
#   0      -- if the answer was "no"
#   undef  -- EOF
sub confirm($) {
  my $prompt = shift;
  interactive or croak "no terminal available\n";
  my $done;
  local $_;
  while (not $done) {
    print STDERR $TTY_BOLD, $prompt, $TTY_NORM;
    $_ = $TTY_IN->getline;
    defined or last;
    /^yes$/io and return 1;
    /^no$/io and return 0;
    print STDERR "${TTY_RED}Please type \`yes' or \`no'${TTY_NORM}\n";
  };
  return undef;
}

#
# Usage: prepend_to_search_path("/some/dir");
#
# Prepend the given directory to the environment variable "PATH"
#
sub prepend_to_search_path($) {
    my $dir = shift;
    if (defined ($dir) and length ($dir)) {
        my $path_sep = $Config{path_sep};
        $ENV{PATH} = join ($path_sep, ($dir, (split(/(?:\Q$path_sep\E)+/o, $ENV{PATH} || ''))));
        log_debug "adjusted PATH=[$ENV{PATH}]";
    }
}

#
# Usage: $path = which ("prog");
#
# Finds the full path to the given executable program, or undef if not found.
#
# This function is here to avoid the dependency on File::Which
#
sub which($) {
  my $prog = shift;

  # Internal function to check the existance of $prog or $prg.exe and similar
  my $get_exe_file = sub {
    my $file = shift;
    # If $file exists, just return it as is.
    if (stat $file and -f _ and -x _) {
      return $file;
    };
    # Otherwise check $file.exe and $file.EXE
    if ($Config{_exe}) {
      my $exe = $Config{_exe};
      unless ($file =~ /\Q$exe\E$/oi) {
        my $exe_uc = uc ($Config{_exe});
        my $exe_file = $file . $exe;
        if (stat $exe_file and -f _ and -x _) {
          return $exe_file;
        };
        if ($exe ne $exe_uc) {
          $exe_file = $file . $exe_uc;
          if (stat $exe_file and -f _ and -x _) {
            return $exe_file;
          }
        }
      }
    };
    # not found
    return undef;
  };

  # Internal function that finds the executable file
  my $find_exe_file = sub {
    my $exe;

    # If $prog refers to a file that exists, just return it as is.
    if ($prog ne basename ($prog) and $exe = &{$get_exe_file} ($prog)) {
      return $exe;
    };
    
    # Otherwise, if $prog doesn't contain a "/"
    my $sep = File::Spec->catfile('', ''); # "/" on Unix, "\" on Windows
    if (not $prog =~ /\Q$sep\E/) {
      # Check each directory in $ENV{PATH}
      for my $dir (File::Spec->path()) {
        my $file = File::Spec->catfile ($dir, $prog);
        if ($exe = &{$get_exe_file} ($file)) {
          return $exe;
        };
      }
    };

    # Not found
    return undef;
  };

  # Find executable file
  my $exe_file = &$find_exe_file;
  # Prepend "./" if necessary
  if ($exe_file and not File::Spec->file_name_is_absolute ($exe_file)) {
    $exe_file = File::Spec->catfile (".", $exe_file);
  };
  return $exe_file;
};

#
# Usage: $filename = native_path ($FILENAME);
#
# Returns the native OS path of a filename. On true UNIX systems this simply
# returns the argument. In CYGWIN and MSYS environments (Windows) returns
# the output of `cygpath --windows $FILENAME'.
#
# This transformation is required when we need to pass file names to native
# programs, i.e., ones not built for CYGWIN/MSYS, such as "psql.exe".
#
sub native_path($) {
  my $filename = shift;
  if (defined ($filename)) {
    if ($OSNAME =~ m/^msys|cygwin$/o) {
      my $cmd = shell_quote ("cygpath", "--mixed", $filename);
      my $native_path = trim (`$cmd`);
      $? == 0 or confess "unable to translate filename to native format";
      return $native_path;
    }
  };
  return $filename;
}

BEGIN {
  use Exporter qw();
  our @ISA = qw(Exporter);
  our @EXPORT_OK = qw(
    $INSTALLED
    $PROGNAME
    $LOG_LEVEL
    $SQL_DIR
    $CONF_DIR
    $SUBCOMMAND
    log_error
    log_warning
    log_notice
    log_info
    log_debug
    log_shell_cmd
    cmdline_error
    trim
    shell
    chdir_cmd
    confirm
    interactive
    prepend_to_search_path
    which
    native_path
  );
  our %EXPORT_TAGS = (all => [@EXPORT_OK]);
};

1;

