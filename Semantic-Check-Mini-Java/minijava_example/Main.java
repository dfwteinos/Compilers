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
        if(args.length != 1){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }

        FileInputStream fis = null;
        try{

            fis = new FileInputStream(args[0]);
            MiniJavaParser parser = new MiniJavaParser(fis);

            Goal root = parser.Goal();

            System.err.println("Program parsed successfully.");  

            //Initialize visitor.
            MyVisitor eval = new MyVisitor();

            //Accept the first visitor
            root.accept(eval, null);

            //Print the first scope of classes
            eval.printClassNames();

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


class MyVisitor extends GJDepthFirst<String, Void>{

    //Initialize symbol table.
    public classTable hMap;

    //Keeping track of the current class
    public String curClass;

    //Keeping track of the current function
    public String curFunc;

    // Symbol table functions //
    public MyVisitor() {
        hMap = new classTable();
    }

    public classTable getTable(){
        return hMap;
    }

    public void updateClass(String c){
        curClass = c;
    }

    public void updateFunction(String f){
        curFunc = f;
    }

    public void printClassNames(){


        Set <String> keys = hMap.lhm.keySet();
        // System.out.println(keys[0]);
        // System.out.println(hMap.get(keys(1)));
        // System.out.println(hMap.get("Factorial"));
            for( String key : keys) {
                System.out.println(key + "--" + hMap.lhm.get(key));
                SymbolTable curTable = hMap.lhm.get(key);

                Set <String> kleidia = curTable.lhm.keySet();
                System.out.println(kleidia);
                STPtr deepTable = curTable.lhm.get("ComputeFac");

                if(deepTable!=null){

                    Set <String> keyz = deepTable.nextScope.lhm.keySet();
                    System.out.println(keyz);
                    // for (String kie: keyz) {
                        // System.out.println(kie + "--" + deepTable.nextScope.lhm.get(kie));
                    // }    
                }
            }
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        hMap.insert(classname);
        updateClass(classname);

        super.visit(n, argu);

        System.out.println();

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        hMap.insert(classname);
        updateClass(classname);
        updateFunction(null);

        super.visit(n, argu);

        System.out.println();

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        hMap.insert(classname);
        updateClass(classname);
        updateFunction(null);

        super.visit(n, argu);

        System.out.println();

        return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        
        //Arg list of function -> method( ..arglist.. )
        String argumentList = n.f4.present() ? n.f4.accept(this, null) : "";
        
        //Identifier of the expression
        String expr   = n.f10.accept(this, null);
        System.out.println(expr);

        //Type and Name of Function
        String myType = n.f1.accept(this, null);
        String myName = n.f2.accept(this, null);

        //Insert Method in the SymbolTable of current class
        hMap.insertSymbol(curClass, myName, myType);
        updateFunction(myName);

        //Insert arguments of the method in the method -> ST(...)
        insertMethodVariables(argumentList, myName);

        System.out.println(myType + " " + myName + " -- " + argumentList);
        
        super.visit(n, argu);

        //Check if the returning type is the same as the type of function
        hMap.methodReturnTypeCheck(myName, expr);

        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
        String ret = n.f0.accept(this, null);

        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        return type + " " + name;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, Void argu) throws Exception {
        
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);

        System.out.println(type + " " + name);
        if(curFunc!=null)
            hMap.insertMethodVariables(curFunc, name, type);
        else 
            hMap.insertSymbol(curClass, name, type);

        return null;
    }


    /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | Clause()
    */
    @Override
    public String visit(Expression n, Void argu) throws Exception {
        String expr = n.f0.accept(this, argu);
        return expr;
    }


    @Override
    public String visit(ArrayType n, Void argu) {
        return "int[]";
    }

    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    public String visit(IntegerType n, Void argu) {
        return "int";
    }

    @Override
    public String visit(Identifier n, Void argu) {
        return n.f0.toString();
    }

    public void insertMethodVariables(String arg_list, String fName) throws Exception {

        //If we have one or more arguments for a method
        //Put them into the symbol table of function's scope
        if (arg_list.length() > 0) {

            String [] arrSplitVar = arg_list.split(", ");
            String [] arrTypeName;

            //For each argument, put it into the symbol table of the method
            for(int i = 0; i < arrSplitVar.length; i++) {

                arrTypeName = arrSplitVar[i].split(" ");

                //For each pair of <type, name> 
                hMap.insertMethodVariables(fName, arrTypeName[1], arrTypeName[0]);
            }
        }
    }


}
