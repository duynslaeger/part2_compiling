all:
	jflex src/LexicalAnalyzer.flex
	javac -d more -cp src/ src/Main.java
	jar cfe dist/part2.jar Main -C more .

tests:
	cd dist/ && java -jar part2.jar -wt euclid.tex ../test/euclid.pmp
	cd dist/ && java -jar part2.jar -wt readNprintCODE.tex ../test/readNprintCODE.pmp
	cd dist/ && java -jar part2.jar -wt calculus.tex ../test/calculus.pmp
	cd dist/ && java -jar part2.jar -wt nestedif.tex ../test/nestedif.pmp
	cd dist/ && java -jar part2.jar -wt nestedElse.tex ../test/nestedElse.pmp
	cd dist/ && java -jar part2.jar -wt nestedDoubleElse.tex ../test/nestedDoubleElse.pmp
	cd dist/ && java -jar part2.jar -wt condAND.tex ../test/condAND.pmp
	cd dist/ && java -jar part2.jar -wt condOR.tex ../test/condOR.pmp
	
