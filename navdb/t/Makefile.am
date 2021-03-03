
check: run-tests

run-tests:
	$(PROVE) -I $(srcdir)/../lib $(srcdir)/*.t

dist-hook:
	cp $(srcdir)/*.t $(distdir)/

