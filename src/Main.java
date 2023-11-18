import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Main{
    /**
     *
     * The parser
     *
     * @param args  The argument(s) given to the program
     * @throws IOException java.io.IOException if an I/O-Error occurs
     * @throws FileNotFoundException java.io.FileNotFoundException if the specified file does not exist
     *
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, SecurityException{
        // Display the usage when the number of arguments is wrong (should be 1)
        if(!(args.length == 1 || args.length == 3)){
            System.out.println("Usage:  java -jar part2.jar sourceFile.pmp\n"
                             + "or\t java -jar part2.jar -wt sourceFile.tex sourceFile.pmp");
            System.exit(0);
        }

        // Initialisation of useful parameters
        Boolean tex = false;
        String filePath;
        String texPath = null;

        if(args[0].equals("-wt")){
            tex = true;
            texPath = args[1];
            filePath = args[2];
        }
        else{
            filePath = args[0];
        }

        // Open the file given in argument
        FileReader source = new FileReader(filePath);

        /**
         * The parser
         */
        final Parser parser = new Parser(source);
        ParseTree parseTree = parser.startParsing();
        
        if(tex){
            try {
                FileWriter ouputTex = new FileWriter("../more/LaTexTrees/"+texPath);
                ouputTex.write(parseTree.toLaTeX());
                ouputTex.close();
            }catch (IOException e){
                e.printStackTrace();    
            }
        }

    }
}
