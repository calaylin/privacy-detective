#
# Build file for the stuff!
#
# @author Jonathan Walsh (jdw74@drexel.edu)
#

SRC_BASE_DIR=src
SRCDIR=$(SRC_BASE_DIR)/upa
OUTDIR=classes
LIBDIR=lib
BINDIR=bin
DOCDIR=doc

JAVAC=javac
JAVADOC=javadoc
CP=-cp $(LIBDIR)/twitter4j-core-3.0.4.jar:$(LIBDIR)/mallet.jar:$(LIBDIR)/postgresql-9.2-1003.jdbc4.jar:$(LIBDIR)/topicModeling.jar:$(LIBDIR)/bsh.jar:$(LIBDIR)/grmm-deps.jar:$(LIBDIR)/jdom-1.0.jar:$(LIBDIR)/jgrapht-0.6.0.jar:$(LIBDIR)/junit-4.5.jar:$(LIBDIR)/jwnl-1.3.jar:$(LIBDIR)/mallet-deps.jar:$(LIBDIR)/mtj-0.9.9.jar:$(LIBDIR)/openjgraph.jar:$(LIBDIR)/trove-2.0.2.jar:$(LIBDIR)/opennlp-tools-1.5.3.jar:$(OUTDIR):$(LIBDIR)/jazzy-core-0.5.2.jar:$(LIBDIR)/langdetect.jar:$(LIBDIR)/jsonic-1.2.0.jar

FILL_PROGRAM=$(BINDIR)/run_fill.sh
COUNT_PROGRAM=$(BINDIR)/counts.sh
UI_PROGRAM=$(BINDIR)/ui.sh
FILE_OUT_PROGRAM=$(BINDIR)/tweet_files.sh
GRAPH_PROGRAM=$(BINDIR)/graph_generator.sh
SCORE_LOADER_PROGRAM=$(BINDIR)/score_loader.sh
MAIN_PROGRAM=$(BINDIR)/main_upa.sh
PROGS=$(FILL_PROGRAM) $(COUNT_PROGRAM) $(UI_PROGRAM) $(FILE_OUT_PROGRAM) $(GRAPH_PROGRAM) $(MAIN_PROGRAM) $(SCORE_LOADER_PROGRAM)

all: $(OUTDIR) $(PROGS) #docs
	$(JAVAC) $(CP) $(SRCDIR)/*.java -d $(OUTDIR)
	$(JAVAC) $(CP) $(SRCDIR)/ui/*.java -d $(OUTDIR)

clean:
	rm -rf $(OUTDIR) $(BINDIR) $(DOCDIR)

$(FILL_PROGRAM): $(BINDIR) Makefile
	echo -e "#!/bin/sh\n\njava $(CP) upa.TwitterFiller \$$1 \$$2" > $(FILL_PROGRAM)
	chmod +x $(FILL_PROGRAM)

$(COUNT_PROGRAM): $(BINDIR) Makefile
	echo -e "#!/bin/sh\n\njava $(CP) upa.Counts" > $(COUNT_PROGRAM)
	chmod +x $(COUNT_PROGRAM)

$(FILE_OUT_PROGRAM): $(BINDIR) Makefile
	echo -e "#!/bin/sh\n\njava $(CP) upa.WriteTweetsToFiles \$$*" > $(FILE_OUT_PROGRAM)
	chmod +x $(FILE_OUT_PROGRAM)

$(UI_PROGRAM): $(BINDIR) Makefile
	echo -e "#!/bin/sh\n\njava $(CP):$(OUTDIR)/ui upa.ui.ui" > $(UI_PROGRAM)
	chmod +x $(UI_PROGRAM)

$(GRAPH_PROGRAM): $(BINDIR) Makefile
	echo -e "#!/bin/sh\n\njava $(CP) upa.GraphGeneratorNG \$$*" > $(GRAPH_PROGRAM)
	chmod +x $(GRAPH_PROGRAM)

$(MAIN_PROGRAM): $(BINDIR) Makefile
	echo -e "#!/bin/sh\n\njava $(CP) upa.UPAMain \$$*" > $(MAIN_PROGRAM)
	chmod +x $(MAIN_PROGRAM)

$(SCORE_LOADER_PROGRAM): $(BINDIR) Makefile
	echo -e "#!/bin/sh\n\njava $(CP) upa.LoadUserScore \$$*" > $(SCORE_LOADER_PROGRAM)
	chmod +x $(SCORE_LOADER_PROGRAM)


$(OUTDIR):
	mkdir -p $@

$(BINDIR):
	mkdir -p $@

$(DOCDIR):
	mkdir -p $@

docs: $(DOCDIR)
	$(JAVADOC) -sourcepath $(SRC_BASE_DIR) -d $(DOCDIR) upa upa.ui
