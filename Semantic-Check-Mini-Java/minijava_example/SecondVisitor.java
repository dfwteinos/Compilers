import syntaxtree.*;
import visitor.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.*;
import java.util.LinkedHashMap;

class SecondVisitor extends GJDepthFirst<String, Void>{

    //Initialize symbol table.
    public classTable hMap;

    //Keeping track of the current class
    public String curClass;

    //Keeping track of the current function
    public String curFunc;

    // Symbol table functions //
    public SecondVisitor(classTable lhMap) {

        hMap = lhMap;
        System.out.println(hMap.lhm.keySet());
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

    public void updateCurrentData(String c, String f){
        curClass = c;
        curFunc = f;
    }

    public classTable getClassTable(){
        return hMap;
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
                System.out.println(curTable.superC);

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

        // hMap.insert(classname);
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

        // hMap.insert(classname);
        updateCurrentData(classname, null);
        SymbolTable cl = hMap.lhm.get(classname);
        System.out.println(cl.lhm.keySet());

        super.visit(n, argu);



        // System.out.println();

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
        String super_class = n.f3.accept(this, null);

        System.out.println("Class: " + classname);

        // hMap.checkExtendsExistence(classname, super_class);
        // hMap.insert(classname, super_class);
        updateCurrentData(classname, null);

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
        
        //Count the number of args
        String del = ",";
        int argLength = StringDelimiterLength(argumentList, del);

        //Type and Name of Function
        String myType = n.f1.accept(this, null);
        String myName = n.f2.accept(this, null);

        //FIRSTVISITOR MATERIAL
        //Insert Method in the SymbolTable of current class
        updateFunction(myName);

        //Insert arguments of the method in the method -> ST(...)

        //Check if we have function Overloading with a superclass
        //If we have Overloading, do type checking

        System.out.println(myType + " " + myName + " -- " + argumentList);
        
        //Check for return type between function and returning variable
        String expr = n.f10.accept(this, null);
        System.out.println("In methoddecl, expr is: " + expr);
        hMap.methodReturnTypeCheck(curClass, myName, expr);
        
        super.visit(n, argu);

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
    // @Override
    // public String visit(VarDeclaration n, Void argu) throws Exception {
        
    //     String type = n.f0.accept(this, null);
    //     String name = n.f1.accept(this, null);

    //     System.out.println(type + " " + name);
    //     // if(curFunc!=null)
    //         // hMap.insertMethodVariables(curFunc, name, type);
    //     // else 
    //         // hMap.insertSymbol(curClass, name, type, -1);

    //     return null;
    // }



    /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    // //TODO
    @Override
    public String visit(PrintStatement n, Void argu) throws Exception {

        String expr = n.f2.accept(this, argu);
        System.out.println("exw xusei" + expr);

        //TODO When I'll cover all possible outcomes for expression(!).
        if(!(expr.equals("int"))){
            throw new Exception("Can't print anything rather than [int] values");
        }

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
    // //TODO
    @Override
    public String visit(Expression n, Void argu) throws Exception {

        String expr = n.f0.accept(this, argu);
        System.out.println("expr is: " + expr);
        
        return expr;
    }

    /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
    // //TODO
    // @Override
    // public String visit(Clause n, Void argu) throws Exception {
    //     String clause = n.f0.accept(this, argu);
    //     return clause;
    // }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    @Override
    public String visit(AssignmentStatement n, Void argu) throws Exception {
        
        String id = n.f0.accept(this, argu);


        System.out.println("In AssignmentStatement, var is : "+ id);
        String idType = hMap.variableType(curClass, curFunc, id);
        if(idType == null) {
            idType = retrieveMethodVariableType(curClass, curFunc, id);
        }
        System.out.println("Type of variable is: "+ idType);
        String exprType = n.f2.accept(this, argu);
        System.out.println("Type of expr is: " + exprType);

        if(exprType.equals("this")){
            exprType = curClass;
        }

        if(!(idType.equals(exprType))){
            throw new Exception("Var: [" + id + "] is: "+ idType + " , and it's assigned as " + exprType + ".");
        }

        return null;
    }


    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(CompareExpression n, Void argu) throws Exception {
        
        String type1 = n.f0.accept(this, argu);
        String type2 = n.f2.accept(this, argu);

        if( !(type1.equals("int")) || !(type2.equals("int")) ){

            throw new Exception("Can't multiply: [" + type1 + "] with [" + type2 + "].");
        }
        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(PlusExpression n, Void argu) throws Exception {

        String type1 = n.f0.accept(this, argu);
        String type2 = n.f2.accept(this, argu);

        if( !(type1.equals("int")) || !(type2.equals("int")) ){

            throw new Exception("Can't multiply: [" + type1 + "] with [" + type2 + "].");
        }

        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(MinusExpression n, Void argu) throws Exception {
        String type1 = n.f0.accept(this, argu);
        String type2 = n.f2.accept(this, argu);

        if( !(type1.equals("int")) || !(type2.equals("int")) ){

            throw new Exception("Can't multiply: [" + type1 + "] with [" + type2 + "].");
        }

        return "int";
    }


    /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    @Override
    public String visit(TimesExpression n, Void argu) throws Exception {
    
        String type1 = n.f0.accept(this, argu);
        String type2 = n.f2.accept(this, argu);

        if( !(type1.equals("int")) || !(type2.equals("int")) ){

            throw new Exception("Can't multiply: [" + type1 + "] with [" + type2 + "].");
        }

        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    @Override
    public String visit(ArrayLookup n, Void argu) throws Exception {

        String typeOfArray = n.f0.accept(this, argu);
        if (!(typeOfArray.equals("int[]"))){
            throw new Exception("Can't handle simple int variable as array of int");
        }

        String typeOfLookupVar = n.f2.accept(this, argu);
        if( !(typeOfLookupVar.equals("int"))){

            throw new Exception("Lookup Variables must be int -> e.g: int x[int y]");
        }

        return typeOfLookupVar;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    @Override
    public String visit(MessageSend n, Void argu) throws Exception {
        
        String type = "";
        String expr = n.f0.accept(this, argu);
        String id = n.f2.accept(this, argu);
        int callArgLength;

        if(expr.equals("this")){

            //Retrieve the type of current method
            type = retrieveMethodType(curClass, id);
            //Retrieve the types of arguments of current method
            String argList = n.f4.accept(this, argu);
            
            //Compute the length of arguments in the call of method
            if(argList!=null){
                callArgLength = StringDelimiterLength(argList, ",");
            }
            else {
                callArgLength = 0;
            }

            System.out.println("TYPE IN MESSAGE SEND IS: " + type);
            System.out.println("expression list is: " + n.f4.accept(this, argu));

            //Retrieve the length of arguments in the declare of the function
            int methodArgLength = hMap.methodNumArgs(curClass, id);

            //If the #CallArgs != #DeclareArgs have different length, throw an Exception 
            if(callArgLength!=methodArgLength) {
                throw new Exception("Method: [" + id + "], has "+ methodArgLength + " args, but in function: [" + curFunc + "], it's called with " + callArgLength + " parameters.");
            }

            //Check for the order of types in fCall and fDeclare
            STPtr funcPtr = retrieveMethodSTPtr(curClass, id);
            if(callArgLength>0){
                hMap.checkFuncArgs(funcPtr, argList, callArgLength, id);
            }

        }

        else {
            type = retrieveMethodType(expr, id); 
        }

        return type;
    }

    /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    @Override
    public String visit(ExpressionList n, Void argu) throws Exception {
        
        String rest = n.f0.accept(this, null);
        if(n.f1 != null){
            rest += n.f1.accept(this, null);
        }

        return rest;
    }

    /**
    * f0 -> ( ExpressionTerm() )*
    */
    @Override
    public String visit(ExpressionTail n, Void argu) throws Exception {
  
        String t = "";

        for(Node node: n.f0.nodes){
            t += "," + node.accept(this, null);
        }

        return t;
    }

    /**
    * f0 -> ","
    * f1 -> Expression()
    */
    @Override
    public String visit(ExpressionTerm n, Void argu) throws Exception {
        
        return n.f1.accept(this, null);
    }

    /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
    @Override
    public String visit(PrimaryExpression n, Void argu) throws Exception {
        
        String prExpr = n.f0.accept(this, argu);
        System.out.println("prExpr is: " + prExpr);

        //Will handle it afterwards
        if(prExpr==null){
            return "int";
        }

        //We have an Identifier of smthing more complicated
        else if(!prExpr.equals("int") && !prExpr.equals("boolean") && !prExpr.equals("this") && !prExpr.equals("int[]")) {

            //If we have AllocationExpression case
            //Return the name of the class itself
            if(true == hMap.lhm.containsKey(prExpr)) {
                return prExpr;                
            }

            else {
                System.out.println("Cur function is: " + curFunc + " and curClass is: " + curClass);
                String type = retrieveMethodVariableType(curClass, curFunc, prExpr);
                return type;
            }
        }

        //Then we'll return int or boolean
        else
            return prExpr;

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

    /**
    * f0 -> "true"
    */
    @Override
    public String visit(TrueLiteral n, Void argu) throws Exception {
        return "boolean";
    }

    /**
    * f0 -> "false"
    */
    @Override
    public String visit(FalseLiteral n, Void argu) throws Exception {
        return "boolean";
    }

    @Override
    public String visit(Identifier n, Void argu) {
        return n.f0.toString();
    }

    @Override
    public String visit(ThisExpression n, Void argu) throws Exception {
        return "this";
    }

    /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    @Override
    public String visit(ArrayAllocationExpression n, Void argu) throws Exception {
        
        String type = n.f3.accept(this, argu);
        if(!(type.equals("int"))){
            throw new Exception("Can't allocate array with type of: " + type);
        }

        return "int[]";
    }


    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    @Override
    public String visit(AllocationExpression n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    @Override
    public String visit(BracketExpression n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    public String retrieveMethodType(String curClass, String id) {

        //Retrieve the symbol table of this class
        SymbolTable TableClass  = hMap.lhm.get(curClass);
            
        //Retrieve the cell of variable/method
        STPtr fieldCell = TableClass.lhm.get(id);
     
        //Retrieve the type of this very expression
        String type = fieldCell.type;

        return type;
    }

    public STPtr retrieveMethodSTPtr(String curClass, String id) {

        //Retrieve the symbol table of this class
        SymbolTable TableClass  = hMap.lhm.get(curClass);
                    
        //Retrieve the cell of variable/method
        STPtr fieldCell = TableClass.lhm.get(id);
        
        return fieldCell;
    }

    public String retrieveMethodVariableType(String currentClass, String method, String var) throws Exception {

        String type = null;
        
        //Retrieve the symbol table of this class
        SymbolTable TableClass  = hMap.lhm.get(currentClass);
            
        //Retrieve the cell of variable/method
        STPtr fieldCell = TableClass.lhm.get(method);
     
        System.out.println("test1");
        System.out.println(fieldCell.nextScope);
        //Retrieve the symbol table of this method 
        //Check if this variable exists in function's symbol table
        if(fieldCell.nextScope != null){

            System.out.println("test2");
            if(fieldCell.nextScope.lhm != null){

            System.out.println("test3");

                if(fieldCell.nextScope.lhm.containsKey(var)){

                    STPtr varCell = fieldCell.nextScope.lhm.get(var);

                    //Retrieve the type of the specific variable
                    type = varCell.type;
                    return type;

                }

            }

        }

        //If this variable does not exists in method's symbol table
        //Check if it's a class variable (field)
        System.out.println("test5");
        System.out.println(TableClass.lhm.keySet());


        if(TableClass.lhm.containsKey(var)){

            fieldCell = TableClass.lhm.get(var);
            type = fieldCell.type;
            return type;
        }

        //Last case, if the variable does not exist in the class variables
        //Check if it has a super-class
        //And if it is, check the variables
        String superC = hMap.fetchSuperClassName(currentClass);
        if(superC!=null){
            
            STPtr sVar = new STPtr(-1);
            sVar.fetchSuperFunction(superC, var, hMap);
            type = sVar.type;
        }


        System.out.println("test4");
        System.out.println("epitelous, type is: "+ type);

        if(type == null){
            throw new Exception("In class: [" + currentClass + "] and at method: [" + method + "], variable: [" + var + "] is undeclared.");
        }

        return type;
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

    //Return the length of function arguments
    public int StringDelimiterLength(String argList, String del){

        int len = 0;
        if(argList.length() > 0) {

            String [] arrSplitDel = argList.split(del);
            len = arrSplitDel.length;
        }
        return len;
    }

}