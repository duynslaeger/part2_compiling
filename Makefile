all:
	jflex src/LexicalAnalyzer.flex
	javac -d more -cp src/ src/Main.java
	jar cfe dist/part2.jar Main -C more .

test:
	java -jar dist/part2.jar test/euclid.pmp
