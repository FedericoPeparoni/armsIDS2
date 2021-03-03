# vim:ts=8:sts=8:sw=8:noet:syn=make
SUBDIRS = sql lib/Local t conf

PERL_SRC_FILES = navdb.pl
bin_SCRIPTS = .reloc/navdb

EXTRA_DIST = $(PERL_SRC_FILES) deprecated-command.sh navdb.bash_completion.bash package.conf

all: $(bin_SCRIPTS)

.reloc/%: $(srcdir)/%.pl Makefile
	$(PERL) -I $(srcdir)/lib -c $<
	mkdir -p .reloc
	sed <$< >$@.tmp \
		-e 's,^.*###RELOC###[ ]*,,g' \
		-e 's,@sqldir\@,$(sqldir),g' \
		-e 's,@perlmoddir\@,$(perlmoddir),g' \
		-e 's,@confdir\@,$(confdir),g' \
		-e 's,@AERO_DATA_FILE\@,$(AERO_DATA_FILE),g' \
		-e 's,@INSTALLED\@,1,g' \
		-e 's,@PACKAGE_VERSION\@,$(PACKAGE_VERSION),g'
	chmod +x $@.tmp
	mv $@.tmp $@

clean-local:
	rm -rf .reloc

install-data-local:
	$(INSTALL) -d -m0775 $(DESTDIR)$(pkgstatedir)
	$(INSTALL) -d -m0775 $(DESTDIR)$(pkgdatadir)
	$(INSTALL) -d -m0775 $(DESTDIR)$(bashcompletiondir)
	$(INSTALL) $(srcdir)/navdb.bash_completion.bash $(DESTDIR)$(bashcompletiondir)/navdb.bash

install-exec-local:
	$(INSTALL) -m 0755 $(srcdir)/deprecated-command.sh $(DESTDIR)$(pkgdatadir)/
	$(LN_S) $(pkgdatadir)/deprecated-command.sh $(DESTDIR)$(bindir)/navdb_setup
	$(LN_S) $(pkgdatadir)/deprecated-command.sh $(DESTDIR)$(bindir)/navdb_dump
	$(LN_S) $(pkgdatadir)/deprecated-command.sh $(DESTDIR)$(bindir)/navdb_restore

uninstall-local:
	-rm -f $(DESTDIR)$(bindir)/navdb_setup
	-rm -f $(DESTDIR)$(bindir)/navdb_dump
	-rm -f $(DESTDIR)$(bindir)/navdb_restore
	-rm -f $(DESTDIR)$(pkgdatadir)/deprecated-command.sh
	-rm -f $(DESTDIR)$(bashcompletiondir)/navdb.bash

