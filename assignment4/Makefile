# Makefile for miniJava static analyzer.
#
JFLAGS = -g
JC = javac
JCC = javacc

.SUFFIXES: .jj .java .class

.PRECIOUS: %.java

.jj.java:
	$(JCC) $*.jj

.java.class:
	$(JC) $(JFLAGS) $*.java

all: AstParser.class Ast.class TestReachability.class 

clean:	
	rm *.class AstParser*.java Token.java TokenMgrError.java ParseException.java SimpleCharStream.java
