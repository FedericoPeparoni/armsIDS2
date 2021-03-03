sqldatadir = $(sqldir)/data

CLEANFILES = *.tmp .keep

if LOAD_DATA
INCLUDED_DATA_FILE = $(AERO_DATA_FILE)
else
INCLUDED_DATA_FILE =
endif

.keep:
	touch $@

EXTRA_DIST = $(INCLUDED_DATA_FILE) download.sh compress.sh
sqldata_DATA = $(INCLUDED_DATA_FILE) .keep

