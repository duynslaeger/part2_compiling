import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {


    private static List<Symbol> tokenList = new ArrayList<Symbol>();
    private static List<Symbol> variableList = new ArrayList<Symbol>();
    private static List<Object> visitedVariables = new ArrayList<Object>(); // used to only select the first occurence of variables (with the Symbol.getValue() fct and the list.contains() fct)

    
    /**
    * This method adds the symbol given in the parameters to a list of tokens
    * if the symbol is not already in the list of the visited variables,
    * and then add it on the list of visited variables
    *
    * @param  S  a symbol
    *
    */
    public static void addVarToLists(Symbol S){
        tokenList.add(S);
        if(!visitedVariables.contains(S.getValue())){
            visitedVariables.add(S.getValue());
            variableList.add(S);
        }
    }


    /**
    * This method prints the table of tokens from the input file.
    */
    public static void printTokenSeq(){
        tokenList.forEach( (lambda) -> System.out.println(lambda.toString()));
        System.out.println("\n");
    }


    /**
    * This method prints the table of variables from the input file.
    *
    */
    public static void printVariableTable(){
        System.out.println("Variables");
        variableList.forEach( (lambda) -> System.out.println(lambda.getValue() + " " + lambda.getLine()));
        System.out.println("\n");
    }

    /** 
     * A comparator used to sort the VariableList by the values in lexicographical order
    */
    public static class SymbolValueComparator implements Comparator<Symbol> {
        @Override
        public int compare(Symbol symbol1, Symbol symbol2) {
            // Use compareTo method to compare lexicographically the two symbols values
            return symbol1.getValue().toString().compareTo(symbol2.getValue().toString()); // /!\ Pourrait être amélioré, ici je fais un.toString pour ne pas avoir d'erreur mais je sais bien que les .getValue() retourneront des strings
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please give (only) one argument, i.e the path of the file to analyze");
            System.exit(1);
        }

        String inputFile = args[0];

        try {
            // Create a FileReader to read from the input file
            FileReader fileReader = new FileReader(inputFile);
            
            // Create a new instance of your generated lexer
            LexicalAnalyzer lexer = new LexicalAnalyzer(fileReader);

            // Tokenize the input file
            Symbol token;
            while (!(token = lexer.yylex()).getType().equals(LexicalUnit.EOS)){
                // Process the token here
                if(token.getType().equals(LexicalUnit.VARNAME)){
                    addVarToLists(token);
                }
                else{
                    tokenList.add(token);
                }
            }

            // Close the input file
            fileReader.close();
            printTokenSeq();
            Collections.sort(variableList, new SymbolValueComparator());
            printVariableTable();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + inputFile);
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file.");
        }
    }
}