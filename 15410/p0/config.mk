#
# This is the "control file" for the Makefile.
#
# Make changes to this file, not to the Makefile.
#

#
# Object files that should be linked into the traceback library
# go here.
#
MY_TRACEBACK_OBJS = traceback.o help.o

#
# Specifies the method for acquiring and project updates. This should be
# "afs" for any andrew machine, "web" for non-andrew machines and
# "offline" for machines with no network access.
#
# "offline" is strongly not recommended as you may miss important project
# updates.
#
UPDATE_METHOD = afs

#
# Any test programs that use the traceback function go here
#
TEST_PROGS = simple_test evil_test voidstar_test alarming_test

#
# Any libs that are necessary for your test programs go here
#
# NOTE: You probably don't need to put anything here, but if
# you do it MUST be of the form "-lfoo".  Do NOT name source
# files or traceback-library object files in LIBS!!!
#
LIBS =
