# navdb - ChangeLog
## [2.21] - 2020-01-09 - h.eskina
- New dataset w/patches: `navdb-data-ead-1901.it190103.p200109-8.3.8-pg92.sql`
  * Added missing airports 'SYGO' and 'TVSV'
  * Fixed route UM791
  
## [2.20] - 2019-09-12 - h.eskina
- New dataset w/patches: `navdb-data-ead-1901.it190103.p190912-8.3.8-pg92.sql`
  * Added missing airport HCEB
  
## [2.19] - 2019-08-15 - d.panech
- New dataset w/patches: `navdb-data-ead-1901.it190103.p190815-8.3.8-pg92.sql`
  * Added missing waypoints GAWASA, ETGOM to Kenya route UL432

## [2.18] - 2019-07-19
- New base dataset w/patches: `navdb-data-ead-1901.it190103.p190719-8.3.8-pg92.sql`
  * corrected routes for Kenya
- Rename patch directories for consistency

## [2.17] - 2019-07-15
- New base dataset w/patches: `navdb-data-ead-1901.it190103.p190715-8.3.8-pg92.sql`
  * re-apply additional south pole [-74 -90] to SAVF airspaces, lost in preivous version
- Rename patch directories for consistency

## [2.16] - 2019-07-03
- New base dataset w/patches: `navdb-data-ead-1901.it190103.p190703-8.3.8-pg92.sql`
  * corrected routes UP312,UA405,UM315,Um731,UR782
  
## [2.15] - 2019-06-20
- New base dataset w/patches: `navdb-data-ead-1901.it190103.p190620-8.3.8-pg92.sql`
  * added additional south pole [-74 -90] to SAVF airspaces

## [2.14] - 2019-06-14
- New base dataset w/patches: `navdb-data-ead-1901.it190103.p190614-8.3.8-pg92.sql`
  * 2 missing airports FYIM,FYUS (Namibia)
  * Corrected airway UL432
  * Corrected VOR 'NN' coordinates, re-calculated routes using segments with this VOR

## [2.13] - 2019-05-03
- New base dataset w/patches: `navdb-data-ead-1901.it190103.p190503-8.3.8-pg92.sql`
  * 44 missing airports with prefix `HJ` (South Sudan)

## [2.12] - 2019-03-21
- New naming convention for dataset files, includes an `.itYYMMDD`
  suffix, which reflects patches made by IDS Rome.
- New base dataset w/patches: `navdb-data-ead-1901.it190103.p190321-8.3.8-pg92.sql`
- Includes all (known) previous patches that still apply, such
  as the Falkland islands airspaces, North/South Sudan aurspace, missing
  airports etc.
- New command-line switch: `navdb --version`
- Include database name in prompts and significant log messages
- Renamed some files and directories in source tree
- Minor fixes to various helper scripts

## [2.11] - 2019-02-13
- New dataset patch `navdb-data-ead-1705.p190213-8.3.8-pg92.sql.xz`
  * 4 missing airports (Somalia)
  * Airspaces around Falkland islands
- New stored procedures for calculating geometry intersections more
  accurately (`ids__st_intersection.sql`)

## [2.10] - 2019-02-05
- New dataset patch `navdb-data-ead-1705.p190205-8.3.8-pg92.sql.xz`
- Added 23 missing aiports in Africa; evidence in
  `misc_scripts/2019-02-05-davlet_panech`

## [2.9] - 2018-10-25
- New dataset patch `navdb-data-ead-1705.p181025-8.3.8-pg92.sql`
- Added 15 airports that were missing, mostly in Africa
- Added waypoint AMBAS (Curacao)

## [2.8] - 2018-09-14
- New dataset patch `navdb-data-ead-1705.p180914-8.3.8-pg92.sql`, includes
  patches based on scripts in `misc_scripts/2018_09_13-pavel_samarin`
- Updated airways UM315, UP312, UT916

## [2.7] - 2018-09-10
- New dataset patch `navdb-data-ead-1705.p180910-8.3.8-pg92.sql`
  (May 2017 + patches)
- Patch that adds airspace HKNA (Nairobi FIR) sector B (type=FIR_P)

## [2.6] - 2018-05-16
- New dataset patch `navdb-data-ead-1705.p180516-8.3.8-pg92.sql`
  (May 2017 + patches)
- Dataset includes patch that adds 2 FIR_P airspaces for North and South
  Sudan, ident=HSS1 and HSS2

## [2.5] - 2017-10-18
- New dataset `navdb-data-ead-1705.p171018-8.3.8-pg92.sql` (May 2017 + patches)

## [2.4] - 2017-10-13
- Fixed permission errors when current directory is `/root`
- Fixed `restore` command errors in cygwin
- New dataset `navdb-data-ead-1705.p171013-8.3.8-pg92.sql` (May 2017 + patches)

## [2.3] - 2017-10-05
- Based off the navdb version from AIM/ATFM 8.3
- Included divergent upgrade scripts from AIM 7.x, AIM 8.x (Canada) and
  AIM 8.x (Italy)
- Data model version restarted at `9.0.0`
- RPM version restarted at `2.x` (epoch=2)
- New dataset `navdb-data-ead-1705.p171004-8.3.8-pg92.sql.xz` ; valid as of
  May 2017 + patches (from @c.corona).
- The `navdb-data` RPM is now smaller due to better compression (66M vs 87M)
- The `navdb-data` RPM in installed form is now _much_ smaller
  (~66M vs ~0.5G) because we now install the compressed .sql file,
  rather than uncompressed.
- `navdb setup`: the `--devel` option is now deprecated and ignored. User
  account is always created with password `aftn` on first install. You can
  change using standard PostgreSQL commands manually.
- New command-line interface described in the [README](README.md) file. The old
  scripts `navdb_{setup,dump,restore}` are still available, but deprecated.
- Removed RPM dependencies on "unusual" Perl modules. The scripts now depend
  _only_ on vanilla CentOS packages.
- Removed dependency on the AIM/CRONOS/ATFM `xbuild` command; integrated
  with GitLab CI
- New config file /etc/navdb/navd.conf with DB connection parameters
- Support for Windows (see README file)
- Support for remote databases
- Better logging; terminal colors in log messages
- Better naming conventions for the data package
- Support for [bash completion](https://github.com/scop/bash-completion)
- Allow overriding target database name in navdb.conf or command line
- Added unit tests

