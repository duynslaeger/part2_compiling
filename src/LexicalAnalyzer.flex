import java.util.ArrayList; 
import java.util.List;


%%//

%class LexicalAnalyzer
%unicode
%line
%column	
%type Symbol
%standalone

//Declare exclusive states
%xstate YYINITIAL, LONG_COMMENT_STATE_DOUBLE, SHORT_COMMENT_STATE 

%{   
%}

// Return value of the program
%eofval{
    if ( yystate() == LONG_COMMENT_STATE_DOUBLE ) {
        System.out.println("The comment with '' is not closed.");}
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}


// Extended Regular Expressions, inspired by the solution of exercise 2.22 from TP3.

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric   = {Alpha}|{Numeric}

Integer        = (([1-9][0-9]*)|0)
Real           = {Integer} // NUMBER
Identifier     = {Alpha}{AlphaNumeric}* //VARNAME

WhiteSpace     = \r|\n|\r\n | [ \t\f]
EndOfLine      = "\r"?"\n"


%% // Identification of the tokens

<YYINITIAL> {
    "''"        {yybegin(LONG_COMMENT_STATE_DOUBLE);}
    "**"        {yybegin(SHORT_COMMENT_STATE);}
    "begin"     {return new Symbol(LexicalUnit.BEG, yyline, yycolumn,"begin");}
    "end"       {return new Symbol(LexicalUnit.END, yyline, yycolumn,"end");}
    "..."       {return new Symbol(LexicalUnit.DOTS, yyline, yycolumn,"...");}
    ":="        {return new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn,":=");}
    "("         {return new Symbol(LexicalUnit.LPAREN, yyline, yycolumn,"(");}
    ")"         {return new Symbol(LexicalUnit.RPAREN, yyline, yycolumn,")");}
    "-"         {return new Symbol(LexicalUnit.MINUS, yyline, yycolumn,"-");}
    "+"         {return new Symbol(LexicalUnit.PLUS, yyline, yycolumn,"+");}
    "*"         {return new Symbol(LexicalUnit.TIMES, yyline, yycolumn,"*");}
    "/"         {return new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn,"/");}
    "if"        {return new Symbol(LexicalUnit.IF, yyline, yycolumn,"if");}
    "then"      {return new Symbol(LexicalUnit.THEN, yyline, yycolumn,"then");}
    "else"      {return new Symbol(LexicalUnit.ELSE, yyline, yycolumn,"else");}
    "and"       {return new Symbol(LexicalUnit.AND, yyline, yycolumn,"and");}
    "or"        {return new Symbol(LexicalUnit.OR, yyline, yycolumn,"or");}
    "{"         {return new Symbol(LexicalUnit.LBRACK, yyline, yycolumn,"{");}
    "}"         {return new Symbol(LexicalUnit.RBRACK, yyline, yycolumn,"}");}
    "="         {return new Symbol(LexicalUnit.EQUAL, yyline, yycolumn,"=");}
    "<"         {return new Symbol(LexicalUnit.SMALLER, yyline, yycolumn,"<");}
    "while"     {return new Symbol(LexicalUnit.WHILE, yyline, yycolumn,"while");}
    "do"        {return new Symbol(LexicalUnit.DO, yyline, yycolumn,"do");}
    "print"     {return new Symbol(LexicalUnit.PRINT, yyline, yycolumn,"print");}
    "read"      {return new Symbol(LexicalUnit.READ, yyline, yycolumn,"read");}
    "eos"       {return new Symbol(LexicalUnit.EOS, yyline, yycolumn,"eos");}

    {Identifier}    {return new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext());}
    {Real}          {return new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext());}
    {WhiteSpace}    {}
    .               {System.out.println((yytext()).toString()+ " is a detected unknown symbol.");} // Ignore other characters
}


<LONG_COMMENT_STATE_DOUBLE>{
    {AlphaNumeric}{WhiteSpace}?"''"             {yybegin(YYINITIAL);}
    .                                           {}
    {WhiteSpace}                                {}
}


<SHORT_COMMENT_STATE>{
    {EndOfLine}     {yybegin(YYINITIAL);}
    .               {} //Ignore everything on the line after the **
}
