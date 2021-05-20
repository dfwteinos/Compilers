import syntaxtree.*;
import visitor.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.*;
import java.util.LinkedHashMap;

public class Main {

    //Initialize symbol table.
    public static classTable hMap = new classTable();
    public static void main(String[] args) throws Exception {

        if(args.length < 1){
            System.err.println("Usage: java Main [MainClassName] [file1] [file2] . . . [fileN]");
            System.exit(1);
        }

        FileInputStream fis = null;
        try{

            for(int i = 0; i < args.length; i++){

                System.out.println("File: " + args[i] + "\n") ;

                fis = new FileInputStream(args[i]);
                MiniJavaParser parser = new MiniJavaParser(fis);

                Goal root = parser.Goal();

                //Initialize first visitor.
                FirstVisitor evalF = new FirstVisitor();
                //Accept the first visitor
                root.accept(evalF, null);
                
                //Get the symbolTable which we fill'd up in the first visit
                classTable symbolTable = evalF.getClassTable();

                //Initialize the second visitor
                SecondVisitor evalS = new SecondVisitor(symbolTable);

                //Accept the second visitor
                root.accept(evalS, null);

                //PrintOffsets
                symbolTable.PrintOffsets();

                System.out.println();

            }

        }
        catch(ParseException ex){
            System.out.println(ex.getMessage());
        } 
        catch(FileNotFoundException ex){
            System.err.println(ex.getMessage());
        }
        finally{
            try{
                if(fis != null) fis.close();
            }
            catch(IOException ex){
                System.err.println(ex.getMessage());
            }
        }
    }
}