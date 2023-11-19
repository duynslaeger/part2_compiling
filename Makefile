all:
	jflex src/LexicalAnalyzer.flex
	javac -d more -cp src/ src/Main.java
	jar cfe dist/part2.jar Main -C more .

# We try the two different ways of running the code in the tests
tests:
	cd dist/ && java -jar part2.jar -wt euclidTest.tex ../test/euclid.pmp
	cd dist/ && java -jar part2.jar -wt readNprintCODE_Test.tex ../test/readNprintCODE.pmp
	java -jar dist/part2.jar test/calculus.pmp
	java -jar dist/part2.jar test/nestedif.pmp
	java -jar dist/part2.jar test/nestedElse.pmp
	java -jar dist/part2.jar test/nestedDoubleElse.pmp
	cd dist/ && java -jar part2.jar -wt condAND.tex ../test/condAND.pmp
	java -jar dist/part2.jar test/condOR.pmp
	