SUBDIRS = . data

dist-hook:
	( cd "$(srcdir)" && find . -type f ! -path '*/.svn/*' ! -path './data/*' ; ) \
		| xargs -i $(INSTALL) -D '$(srcdir)/{}' $(distdir)/{}

install-data-local:
	( cd "$(srcdir)" && find . -type f ! -name 'Makefile*' ! -path '*/.svn/*' ! -path './data/*' ; ) \
		| xargs -i $(INSTALL) -m 0644 -D '$(srcdir)/{}' $(DESTDIR)$(sqldir)/{}

uninstall-local:
	( cd "$(srcdir)" && find . -type f ! -name 'Makefile*' ! -path '*/.svn/*' ! -path './data/*' ; ) \
		| xargs -i rm -f $(DESTDIR)$(sqldir)/{} ';'

