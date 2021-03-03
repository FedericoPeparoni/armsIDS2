# NAVDB

```bash
# get help
navdb --help

# create or upgrade database
navdb setup

# destroy database
navdb drop

# create backup
navdb dump navdb.dat

# restore backup
navdb restore navdb.dat

# export world feature data in PSQL syntax
navdb export load_file.sql
#   same, but from staging schema
navdb export --staging load_file.sql

# import PSQL load file into world ("navdb") schema
navdb import load_file.sql
#   same, but into staging schema
navdb import --staging load_file.sql
```

This is a PostgreSQL-based aeronautical feature database package. It allows one
to create, upgrade, backup and restore the database. It comes with a single
script, ``navdb`` that supports multiple sub-commands, such as ``setup`` or
``dump``.

Type ``navdb --help`` for help on the available sub-commands.

See also [ChangeLog](ChangeLog.md), esp. for changes since the older verions
of NavDB included with AIM/CRONOS & ATFM.

## Installation (Linux)

The scripts included with NavDB require PostgreSQL server and client tools to
be installed on your CentOS 6/7 system. The NavDB RPMs do not enforce any
dependencies on Postgres because there are many different builds and versions
of Postgres available for CentOS. You have to make sure that Postgres is
installed before using NavDB.

__WARNING__: installing the latest and greatest version directly from this
project's repository may destabilize your system. Please test such upgrades
before installing them on production servers.

To install on CentOS 6/7 directly from IDS N.A. repositories:

```bash
sudo bash -c "source <(curl -fsSL http://ubidev04.idscorporation.ca/pub/repos/ids/navdb/bootstrap.sh)"
```

__WARNING__: the above command will create a new YUM repo file
``/etc/yum.repos.d/navdb.repo`` which may override any older
versions of navdb visible to yum via your other repositories, if
any.

Alternatively, you can download the RPMs manually here:
http://ubidev04.idscorporation.ca/pub/repos/ids/navdb/ .

Note that if you are working with AIM/Spatia/CRONOS or ABMS/Billing
environments, your YUM repositories may already contain NAVDB (as well as
PostgresSQL/pghelper packages) packages and you should install them using
something like:

```bash
yum install postgresql-server pghelper navdb navdb-data
```

## Installation (Windows)

We don't provide Windows installers, but the scripts in this package
should work from a Git checkout on a Windows machine. Pre-requisites:

- A reasonable Unix-like command-line environment, such as
  [Git Bash](https://git-scm.com/), [MinGW](http://www.mingw.org/) or
  [Cygwin](https://www.cygwin.com/), with the following command-line tools
  installed:
  - bash
  - perl
  - curl or wget
- PostgreSQL (client) installed on your Windows machine
- 7-Zip (for Git bash) or the ``xz`` package (for Cygwin and MinGW)

Once you have these pre-requiesites, you can download the latest tagged
source code from [here](http://ubidev04.idscorporation.ca/pub/repos/ids/navdb/tar/)
and unpack it:

```bash
tar xzf navdb-2.3.tar.gz
cd navdb-2.3
```

Alternatively, you can clone the repo from Git.

```bash
git clone https://git.idscorporation.ca/ids/navdb.git
cd navdb
```

Then create and possibly tweak the navdb.conf file:
```bash
cp conf/navdb.conf.example conf/navdb.conf
```

Then you can use the ``navdb.pl`` script from the top-level of the project:
```bash
./navdb.pl --help
```

## bash-completion

This package fully supports [bash-completion](https://github.com/scop/bash-completion),
on Linux which allows bash to auto-complete the arguments of many commands,
including ``navdb``. To install:

```bash
yum install bash-completion
```
After installing it you need to restart your terminal session.


## RPM packages
This project provides 2 RPM packages:
- ``navdb``: contains the navdb command and all SQL scripts, but no data
- ``navdb-data``: contains world-wide feature data based on EAD
  AIXM/baselines.

## Hacking

Additional information is available on the [Wiki](https://git.idscorporation.ca/ids/navdb/wikis/home)