import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;


public class Parser {

    private final LexicalAnalyzer lexer;
    private Symbol token;
    private LexicalUnit tokenLexUnit;
    private ArrayList<Integer> derivationList;

    public Parser(FileReader source){
        lexer = new LexicalAnalyzer(source);
    }

    /**
     * Selects the next token matched by the lexer
     * and stores its type (Lexical Unit) in tokenLexUnit
     */
    private void nextToken(){
        try{
            token = lexer.nextToken();
            tokenLexUnit = token.getType();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Verifies if the matched lexical unit corresponds
     * to the expected one. If not, an error is raised.
     * @param expected (the expected lexical unit)
     * @return the root of a ParseTree
     */
    private ParseTree matchTest(LexicalUnit expected){
        if(!(tokenLexUnit.equals(expected))){
            syntaxError(token, expected);
        }
        return new ParseTree(token);
    }

    /**
     * Starts the parsing, initialize the parseTree.
     * Once the parsing is finished, it prints the left most derivation
     * @return the parsing tree of the leftmost derivation
     */
    private ParseTree startParsing(){
        ParseTree parseTree = Program();
        System.out.println(derivationList.toString());
        return parseTree;
    }

    /** 
     * Using this function means we are in the state Program. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Program as root and the
     * rule derived as children.
     */
    private ParseTree Program(){
        ArrayList<ParseTree> children = new ArrayList<>();
        nextToken();
        switch (tokenLexUnit) {
            case BEG:
                derivationList.add(1);
                children.add(matchTest(LexicalUnit.BEG));
                nextToken();
                children.add(Code());
                // nextToken();
                children.add(matchTest(LexicalUnit.END));
                break;
        
            default:
                syntaxError(token, LexicalUnit.BEG);
                break;
        }
        return new ParseTree(new Symbol("<Program>>"), children);
    }

    /** 
     * Using this function means we are in the state Code. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Code as root and the
     * rule derived as children.
     */
    private ParseTree Code(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case IF:
            case BEG:
            case WHILE:
            case PRINT:
            case READ:
            case VARNAME:
                derivationList.add(3);
                children.add(InstList());
                break;

            case END:
                derivationList.add(2);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "IF", "BEG", "WHILE", "PRINT", "READ", "VARNAME", "END"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<Code>"), children);
    }


    /** 
     * Using this function means we are in the state InstList. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with InstList as root and the
     * rule derived as children.
     */
    private ParseTree InstList(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case IF:
            case BEG:
            case WHILE:
            case PRINT:
            case READ:
            case VARNAME:
                derivationList.add(4);
                children.add(Instruction());
                children.add(InstFactorized());
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "IF", "BEG", "WHILE", "PRINT", "READ", "VARNAME"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<InstList>"), children);
    }


    /** 
     * Using this function means we are in the state ParseTree. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with ParseTree as root and the
     * rule derived as children.
     */
    private ParseTree InstFactorized(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case END:
                derivationList.add(6);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            case DOTS:
                derivationList.add(5);
                children.add(new ParseTree(token)); // Here, token is a ...
                nextToken();
                children.add(InstList());
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "End", "DOTS"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<InstFactorized>"), children);
    }


    /** 
     * Using this function means we are in the state Instruction. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Instruction as root and the
     * rule derived as children.
     */
    private ParseTree Instruction(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case IF:
                derivationList.add(8);
                children.add(If());
                break;
            case BEG:
                derivationList.add(12);
                children.add(new ParseTree(token)); // Here token is begin
                nextToken();
                children.add(InstList());
                children.add(matchTest(LexicalUnit.END));
                nextToken();
                break;
            case WHILE:
                derivationList.add(9);
                children.add(While());
                break;
            case PRINT:
                derivationList.add(10);
                children.add(Print());
                break;
            case READ:
                derivationList.add(11);
                children.add(Read());
                break;
            case VARNAME:
                derivationList.add(7);
                children.add(Assign());
                break;
        
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "IF", "BEG", "WHILE", "PRINT", "READ", "VARNAME"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<Instruction>"), children);
    }


    /** 
     * Using this function means we are in the state Assign. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Assign as root and the
     * rule derived as children.
     */
    private ParseTree Assign(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case VARNAME:
                derivationList.add(13);
                children.add(new ParseTree(token)); // Here token is [VarName]
                nextToken();
                children.add(matchTest(LexicalUnit.ASSIGN));
                nextToken();
                children.add(ExprArith());
                break;
        
            default:
                syntaxError(token,LexicalUnit.VARNAME);
                break;
        }
        return new ParseTree(new Symbol("<Assign>"), children);
    }


    /** 
     * Using this function means we are in the state ExprArith. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with ExprArith as root and the
     * rule derived as children.
     */
    private ParseTree ExprArith(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case VARNAME:
            case NUMBER:
            case MINUSUNARY:
            case LPAREN:
                derivationList.add(14);
                children.add(ExpGenProdDiv());
                children.add(EAprime());
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "VARNAME", "NUMBER", "MINUSUNARY", "LPAREN"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<ExprArith>"), children);
    }


    /** 
     * Using this function means we are in the state EAprime. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with EAprime as root and the
     * rule derived as children.
     */
    private ParseTree EAprime(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case PLUS:
                derivationList.add(15);
                children.add(new ParseTree(token)); // Here token is +
                nextToken();
                children.add(ExpGenProdDiv());
                children.add(EAprime());
                break;
            case MINUS:
                derivationList.add(16);
                children.add(new ParseTree(token)); // Here token is -
                nextToken();
                children.add(ExpGenProdDiv());
                children.add(EAprime());
                break;
            case EQUAL:
            case SMALLER:
            case TIMES:
            case DIVIDE:
            case ELSE:
            case AND:
            case OR:
            case END:
            case DOTS:
            case RPAREN:
            case RBRACK:
            case THEN:
            case DO:
                derivationList.add(17);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "EQUAL", "SMALLER", "PLUS", "MINUS", "TIMES", "DIVIDE", "ELSE", "AND", "OR", "END", "DOTS", "RPAREN", "RBRACK", "THEN", "DO"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<EA'>"), children);
    }


    /** 
     * Using this function means we are in the state ExpGenProdDiv. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with ExpGenProdDiv as root and the
     * rule derived as children.
     */
    private ParseTree ExpGenProdDiv(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case VARNAME:
            case NUMBER:
            case MINUSUNARY:
            case LPAREN:
                derivationList.add(18);
                children.add(InterExp());
                children.add(ExpProdDiv());
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "VARNAME", "NUMBER", "MINUSUNARY", "LPAREN"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<ExpGenProdDiv>"), children);
    }


    /** 
     * Using this function means we are in the state ExpProdDiv. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with ExpProdDiv as root and the
     * rule derived as children.
     */
    private ParseTree ExpProdDiv(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case TIMES:
                derivationList.add(19);
                children.add(new ParseTree(token)); // Here token is *
                nextToken();
                children.add(InterExp());
                children.add(ExpProdDiv());
                break;
            case DIVIDE:
                derivationList.add(20);
                children.add(new ParseTree(token)); // Here token is /
                nextToken();
                children.add(InterExp());
                children.add(ExpProdDiv());
                break;
            case EQUAL:
            case SMALLER:
            case PLUS:
            case MINUS:
            case ELSE:
            case AND:
            case OR:
            case END:
            case DOTS:
            case RPAREN:
            case RBRACK:
            case THEN:
            case DO:
                derivationList.add(21);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "EQUAL", "SMALLER", "PLUS", "MINUS", "TIMES", "DIVIDE", "ELSE", "AND", "OR", "END", "DOTS", "RPAREN", "RBRACK", "THEN", "DO"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<ExpProdDiv>"), children);
    }


    /** 
     * Using this function means we are in the state InterExp. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with InterExp as root and the
     * rule derived as children.
     */
    private ParseTree InterExp(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case VARNAME:
            case NUMBER:
                derivationList.add(22);
                children.add(Atom());
                break;
            case MINUSUNARY:
                derivationList.add(24);
                children.add(new ParseTree(token)); // Here token is - (unary)
                nextToken();
                children.add(ExprArith());
                break;
            case LPAREN:
                derivationList.add(23);
                children.add(new ParseTree(token)); // Here token is (
                nextToken();
                children.add(ExprArith());
                children.add(matchTest(LexicalUnit.RPAREN));
                nextToken();
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "VARNAME", "NUMBER", "MINUSUNARY", "LPAREN"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<InterExp>"), children);
    }


    /** 
     * Using this function means we are in the state Atom. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Atom as root and the
     * rule derived as children.
     */
    private ParseTree Atom(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case VARNAME:
                derivationList.add(25);
                children.add(new ParseTree(token)); // Here token is a VarName
                nextToken();
                break;
            case NUMBER:
                derivationList.add(26);
                children.add(new ParseTree(token)); // Here token is a Number
                nextToken();
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "VARNAME", "NUMBER"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<Atom>"), children);
    }

    /** 
     * Using this function means we are in the state If. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with If as root and the
     * rule derived as children.
     */
    private ParseTree If(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case IF:
                derivationList.add(27);
                children.add(new ParseTree(token)); // Here token is if
                nextToken();
                children.add(Cond());
                children.add(matchTest(LexicalUnit.THEN));
                nextToken();
                children.add(Instruction());
                children.add(IfFactorized());
                break;
            default:
                syntaxError(token, LexicalUnit.IF);
                break;
        }
        return new ParseTree(new Symbol("<If>"), children);
    }


    /** 
     * Using this function means we are in the state IfFactorized. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with IfFactorized as root and the
     * rule derived as children.
     */
    private ParseTree IfFactorized(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case ELSE:
                derivationList.add(28);
                children.add(new ParseTree(token)); // Here token is else
                nextToken();
                children.add(Instruction());
                break;
            case END:
            case DOTS:
                derivationList.add(29);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "ELSE", "END", "DOTS"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<IfFactorized>"), children);
    }


    /** 
     * Using this function means we are in the state Cond. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Cond as root and the
     * rule derived as children.
     */
    private ParseTree Cond(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case VARNAME:
            case NUMBER:
            case MINUSUNARY:
            case LPAREN:
            case LBRACK:
                derivationList.add(30);
                children.add(CondAND());
                children.add(CondPrime());
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "VARNAME", "NUMBER", "MINUSUNARY", "LPAREN", "LBRACK"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<Cond>"), children);
    }


    /** 
     * Using this function means we are in the state CondPrime. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with CondPrime as root and the
     * rule derived as children.
     */
    private ParseTree CondPrime(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case OR:
                derivationList.add(31);
                children.add(new ParseTree(token)); // Here token is or
                nextToken();
                children.add(CondAND());
                children.add(CondPrime());
                break;
            case RBRACK:
            case THEN:
            case DO:
                derivationList.add(32);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "OR", "RBRACK", "THEN", "DO"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<Cond'>"), children);
    }


    /** 
     * Using this function means we are in the state CondAND. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with CondAND as root and the
     * rule derived as children.
     */
    private ParseTree CondAND(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case VARNAME:
            case NUMBER:
            case MINUSUNARY:
            case LPAREN:
            case LBRACK:
                derivationList.add(33);
                children.add(CondInter());
                children.add(CondANDprime());
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "VARNAME", "NUMBER", "MINUSUNARY", "LPAREN", "LBRACK"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<CondAND>"), children);
    }


    /** 
     * Using this function means we are in the state CondANDprime. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with CondANDprime as root and the
     * rule derived as children.
     */
    private ParseTree CondANDprime(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case AND:
                derivationList.add(34);
                children.add(new ParseTree(token)); // Here token is and
                nextToken();
                children.add(CondInter());
                children.add(CondANDprime());
                break;
            case OR:
            case RBRACK:
            case THEN:
            case DO:
                derivationList.add(35);
                children.add(new ParseTree(new Symbol("$\\varepsilon$")));
                break;
            
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "AND", "OR", "RBRACK", "THEN", "DO"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<CondAND'>"), children);
    }


    /** 
     * Using this function means we are in the state CondInter. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with CondInter as root and the
     * rule derived as children.
     */
    private ParseTree CondInter(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case LBRACK:
                derivationList.add(36);
                children.add(new ParseTree(token)); // Here token is {
                nextToken();
                children.add(Cond());
                children.add(matchTest(LexicalUnit.RBRACK));
                nextToken();
                break;
            case VARNAME:
            case NUMBER:
            case MINUSUNARY:
            case LPAREN:
                derivationList.add(37);
                children.add(SimpleCond());
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "VARNAME", "NUMBER", "MINUSUNARY", "LPAREN", "LBRACK"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<CondInter>"), children);
    }


    /** 
     * Using this function means we are in the state SimpleCond. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with SimpleCond as root and the
     * rule derived as children.
     */
    private ParseTree SimpleCond(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case VARNAME:
            case NUMBER:
            case MINUSUNARY:
            case LPAREN:
                derivationList.add(38);
                children.add(ExprArith());
                children.add(Comp());
                children.add(ExprArith());
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "VARNAME", "NUMBER", "MINUSUNARY", "LPAREN"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<SimpleCond>"), children);
    }


    /** 
     * Using this function means we are in the state Comp. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Comp as root and the
     * rule derived as children.
     */
    private ParseTree Comp(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case EQUAL:
                derivationList.add(39);
                children.add(new ParseTree(token)); // Here token is =
                nextToken();
                break;
            case SMALLER:
                derivationList.add(40);
                children.add(new ParseTree(token)); // Here token is <
                nextToken();
                break;
            default:
                ArrayList<String> expected_list = new ArrayList<>();
                Collections.addAll(expected_list, "EQUAL", "SMALLER"); 
                syntaxError(token, expected_list);
                break;
        }
        return new ParseTree(new Symbol("<Comp>"), children);
    }


    /** 
     * Using this function means we are in the state While. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with While as root and the
     * rule derived as children.
     */
    private ParseTree While(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case WHILE:
                derivationList.add(41);
                children.add(new ParseTree(token)); // Here token is while
                nextToken();
                children.add(Cond());
                children.add(matchTest(LexicalUnit.DO));
                children.add(Instruction());
                break;
            default:
                syntaxError(token, LexicalUnit.WHILE);
                break;
        }
        return new ParseTree(new Symbol("<While>"), children);
    }


    /** 
     * Using this function means we are in the state Print. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Print as root and the
     * rule derived as children.
     */
    private ParseTree Print(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case PRINT:
                derivationList.add(42);
                children.add(new ParseTree(token)); // Here token is print
                nextToken();
                children.add(matchTest(LexicalUnit.LPAREN));
                nextToken();
                children.add(matchTest(LexicalUnit.VARNAME));
                nextToken();
                children.add(matchTest(LexicalUnit.RPAREN));
                nextToken();
                break;
            default:
                syntaxError(token, LexicalUnit.PRINT);
                break;
        }
        return new ParseTree(new Symbol("<Print>"), children);
    }
    

    /** 
     * Using this function means we are in the state Read. 
     * The function applies the correct derivation according to the token matched
     * in the action table.
     * @return a ParseTree with Read as root and the
     * rule derived as children.
     */
    private ParseTree Read(){
        ArrayList<ParseTree> children = new ArrayList<>();
        switch (tokenLexUnit) {
            case READ:
                derivationList.add(43);
                children.add(new ParseTree(token)); // Here token is read
                nextToken();
                children.add(matchTest(LexicalUnit.LPAREN));
                nextToken();
                children.add(matchTest(LexicalUnit.VARNAME));
                nextToken();
                children.add(matchTest(LexicalUnit.RPAREN));
                nextToken();
                break;
            default:
                syntaxError(token, LexicalUnit.READ);
                break;
        }
        return new ParseTree(new Symbol("<Read>"), children);
    }


    /**
     * Launches an error and exits the code. 
     * @param token the token that generated the error.
     */
    private void syntaxError(Symbol token, LexicalUnit expected){
        System.err.println("Syntax Error occured when reading the token : " + token.getValue()+" at line : " + token.getLine()+". Lexical Unit "+expected+" was expected, but "+token.getType()+" was found." );
        System.exit(1);
    }


    /**
     * Launches an error and exits the code. 
     * @param token the token that generated the error.
     */
    private void syntaxError(Symbol token, ArrayList<String> expected){
        System.err.println("Syntax Error occured when reading the token : " + token.getValue()+" at line : " + token.getLine()+". One of the following Lexical Unit "+expected+" was expected, but "+token.getType()+" was found." );
        System.exit(1);
    }

}