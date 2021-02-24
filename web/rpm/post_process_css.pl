#!/usr/bin/perl

# TFS bug 70450
# =============
#
# This script is called from (rpm/)build.sh
#
# This hack removes a hard-coded reference to an internet style sheet (and, indirectly, font files)
# from the main application css file. This URL is inserted by a bower component called
# bootswatch-dist. We can't reference Internet URL in production builds because
# some users don't have Internet access.
#
# This needs to be removed when this problem is properly resolved: we need
# to download all referenced Internet files and commit them in git, as well
# as fix the build scripts to match.

my $css = do { local $/ = undef ; <> };

$css =~ s#\@import\s+url\s*\(\s*["']https://fonts\.googleapis\.com/css\?family=Roboto:300,400,500,700["']\s*\)\s*(;)?##gs;
print $css;

