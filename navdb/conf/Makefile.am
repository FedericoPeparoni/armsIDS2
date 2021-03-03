EXTRA_DIST = navdb.conf.example

install-data-local:
	$(INSTALL) -m 0755 -d $(DESTDIR)$(confdir)
	$(INSTALL) -m 0640 $(srcdir)/navdb.conf.example $(DESTDIR)$(confdir)/navdb.conf

uninstall-local:
	-rm -f $(DESTDIR)$(confdir)/navdb.conf

