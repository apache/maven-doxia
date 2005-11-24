# Copyright (c) 2001 Pixware. 
#
# This file is part of the Pixware apt package.
# For conditions of use and distribution, see the attached legal.txt file.

SOURCES=	RTFSink.java RTFConverter.java \
		Font.java FontMetrics.java \
		Serif.java SerifBold.java \
		SerifItalic.java SerifBoldItalic.java \
		SansSerif.java SansSerifBold.java \
		SansSerifItalic.java SansSerifBoldItalic.java \
		Monospace.java MonospaceBold.java \
		MonospaceItalic.java MonospaceBoldItalic.java \
		PBMReader.java WMFWriter.java

# ----------------------------------------------------------------------------
ps=:
JAVA_ROOT=../../../../..
CLASS_ROOT=$(JAVA_ROOT)/../class
CLASS_PATH_0=$(CLASS_ROOT)$(ps)$(JAVA_ROOT)$(ps).
CLASS_PATH=$(CLASS_PATH_0)$(ps)$(CLASSPATH)
CLASS_DIR=$(CLASS_ROOT)/fr/pixware/apt/convert/rtf

DEBUG=-g
###DEBUG=-O

JAVAC=javac -classpath $(CLASS_PATH) $(DEBUG) -deprecation -d $(CLASS_ROOT) 

JAVA=java -classpath $(CLASS_PATH)

.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $<

# ----------------------------------------------------------------------------
all: 
	$(JAVAC) $(SOURCES)

# ----------------------------------------------------------------------------
tconverter: manual graphics

manual:
	$(JAVA) fr.pixware.apt.convert.Driver \
	  out/manual.rtf \
	  in/manual/intro.txt \
	  in/manual/format.txt \
	  in/manual/install.txt \
	  in/manual/using.txt \
	  in/manual/changes.txt

graphics:
	$(JAVA) fr.pixware.apt.convert.Driver \
	  -rule gif ppm "convert %F %G" \
	  out/graphics.rtf in/graphics.txt

graphics2:
	$(JAVA) fr.pixware.apt.convert.Driver \
	  out/figure.rtf in/figure.txt

# ----------------------------------------------------------------------------
tinfo:
	-$(JAVA) fr.pixware.apt.convert.Driver -? rtf

tencoding:
	$(JAVA) fr.pixware.apt.convert.Driver -enc ISO8859_1 \
	  -pi rtf fontsize 14 \
	  out/iso8859-1.rtf in/char8.txt
	$(JAVA) fr.pixware.apt.convert.Driver -enc Cp1251 \
	  -pi rtf fontsize 14 \
	  out/cp1251.rtf in/char8.txt

# ----------------------------------------------------------------------------
tsink: section list nlist dlist table verbatim figure hrule header

section:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/section.txt out/section.rtf

list:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/list.txt out/list.rtf

nlist:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/nlist.txt out/nlist.rtf

dlist:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/dlist.txt out/dlist.rtf

table:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/table.txt out/table.rtf

verbatim:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/verbatim.txt out/verbatim.rtf

figure:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/figure.txt out/figure.rtf

hrule:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/hrule.txt out/hrule.rtf

header:
	$(JAVA) fr.pixware.apt.convert.rtf.RTFSink \
	  in/header.txt out/header.rtf

# ----------------------------------------------------------------------------
tpbm:
	$(JAVA) fr.pixware.apt.convert.rtf.PBMReader in/figure.ppm

# ----------------------------------------------------------------------------
clean:
	rm -f *~ *.class in/*~ out/*

distclean: clean
	rm -f $(CLASS_DIR)/*

docclean:

realclean: distclean docclean
