import java.util.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import java.io.IOException;

//In this data structure, we'll keep info about each class with its symbol table.
public class classTable {

    LinkedHashMap<String, SymbolTable> lhm;
    SymbolTable curTable;

    //Constructor
    public classTable(){

        lhm = new LinkedHashMap<String, SymbolTable>();
    }

    //Insert a className in the first SymbolTable
    public void insert(String cName) throws Exception{

        checkClassExistence(cName);
        curTable = new SymbolTable();
        lhm.put(cName, curTable);
    }

    //Insert a className in the first SymbolTable , and "connect" it with it superclass
    //Overloading
    public void insert(String cName, String superC) throws Exception {
    
        checkClassExistence(cName);
        curTable = new SymbolTable();
        curTable.extendsClass(superC);
        lhm.put(cName, curTable);
    }

    //Insert a symbol in a specific className symbol table
    public void insertSymbol(String cName, String fName, String type, int numArgs) throws Exception {

        checkExistence(fName, cName);
        curTable.insert(fName, type, numArgs);
        lhm.put(cName, curTable);
    }

    //Check if a class with the same name is already declared
    public void checkClassExistence(String classname) throws Exception {

        boolean exists = lhm.containsKey(classname);
        if (exists == true) {
            throw new Exception("Class [" + classname + "] is already declared.");
        }
    }

    //Check in the current scope of a class the uniqueness of a new symbol
    public void checkExistence(String name, String cName) throws Exception {
        boolean exists = curTable.lhm.containsKey(name);
        if(exists==true){
            throw new Exception("Variable or Method [" + name + "] is already defined in class " + cName + ".");
        }
    }

    //Check if a Superclass is declared, before another class extendeds it 
    public void checkExtendsExistence(String child, String superclass) throws Exception {

        boolean exists = lhm.containsKey(superclass);
        if(exists==false){
            throw new Exception("Class [" + superclass + "] must be defined before " + child);
        }
    }

    //Insert in: class[ST] -> [..|..|Func[ST] -> [var|..] ..]  
    public void insertMethodVariables(String fName, String vName, String type) throws Exception {

        curTable.insertMethodVariables(fName, vName, type);
    }

    //Check for Overloading and also do some type check, if it exists
    public void checkFuncOverloading(String curClass, String fName, int argLen) throws Exception {
    
        //Get superclass name
        String superClass = fetchSuperClassName(curClass);

        // If a superclass exists
        if(superClass!=null){

            System.out.println("Superclass is: " + superClass);

            //Get the super's function symbol table
            STPtr sfuncTable = new STPtr(argLen);
            sfuncTable.fetchSuperFunction(superClass, fName, this);
            System.out.println("pointer is: " + sfuncTable);
            
            //Get the inner's class function symbol table
            STPtr dfuncTable = lhm.get(curClass).lhm.get(fName);

            // System.out.println("emetos" + sfuncTable);
            if(sfuncTable.type!=null) { 
                checkFuncArgs(sfuncTable, dfuncTable, argLen, fName);
            }
       }
    }

    public void methodReturnTypeCheck(String className, String fName, String exprType) throws Exception{

        SymbolTable curFunction = lhm.get(className);
        curFunction.methodReturnTypeCheck(fName, exprType);
    }

    public String variableType(String className, String curFunc, String var) {
        
        String type = null;
        SymbolTable class_Table = lhm.get(className);
        //Check if it's a variable of inner class (field)
        if(class_Table.lhm.containsKey(var)){
            type = class_Table.variableType(var);
        }

        // else{
            //If not, check if it's a variable of inner function of this class
            // STPtr funcTable = class_Table.lhm.get(curFunc);
            // type = funcTable.nextScope.variableType(var);
        // }

        return type;
    }

    public int methodNumArgs(String className, String fName) {
        SymbolTable curFunction = lhm.get(className);
        int num_Args = curFunction.methodReturnNumArgs(fName);
        return num_Args;
    }


    //Compare the arguments of 2 identical function in case of overloading
    public void checkFuncArgs(STPtr func1, STPtr func2, int argLen, String fName) throws Exception{

        System.out.println("mpainei mesa" + argLen);

        //Functions must be the same type
        if( func1.type == func2.type ){

            System.out.println(func2.numArgs);
            System.out.println(func1.numArgs);
            //Function must also have the same number of args
            if(func1.numArgs != func2.numArgs){
                throw new Exception("Function(s) :[" + fName + "] have different number of arguments");
            }

            else {

                String type1;
                String type2;

                Set <String> keys1 = func1.nextScope.lhm.keySet();
                List<String> listKeys1 = new ArrayList<String>(keys1);

                Set <String> keys2 = func2.nextScope.lhm.keySet();
                List<String> listKeys2 = new ArrayList<String>(keys2);

                for(int i = 0; i < argLen; i++){

                    type1 = func1.nextScope.lhm.get(listKeys1.get(i)).type; 
                    type2 = func2.nextScope.lhm.get(listKeys2.get(i)).type; 

                    if( !(type1.equals(type2)) ){
                        throw new Exception("In function: [" + fName +"],  Arg number " + (i + 1)+ " can't be " + type1 + " and " + type2 + " at the same time.");
                    }
                }
            }
        }

        else {

            throw new Exception("Function: [" + fName + "], can't be " + func1.type + " and" + func2.type + " at the same time.");
        }

    }

    //Compare the arguments of 2 function call and function method
    public void checkFuncArgs(STPtr func1, String callArgs, int argLen, String fName) throws Exception{

        System.out.println("mpainei mesa2" + argLen);

        String type1;
        String type2;

        Set <String> keys1 = func1.nextScope.lhm.keySet();
        List<String> listKeys1 = new ArrayList<String>(keys1);

        String [] callArgsArray = callArgs.split(",");

        for(int i = 0; i < argLen; i++){

            type1 = func1.nextScope.lhm.get(listKeys1.get(i)).type; 
            type2 = callArgsArray[i]; 

            // if(type2.equals("this")){
                // type2 = className;
            // }

            if( !(type1.equals(type2)) ){

                String superC = fetchSuperClassName(type2);
                if(superC != null){

                    if(!(type1.equals(superC))){
                        throw new Exception("In function: [" + fName +"],  Arg number " + (i + 1)+ " can't be " + type1 + " and " + type2 + " at the same time.");
                    }
                    else
                        return;
                }
                throw new Exception("In function: [" + fName +"],  Arg number " + (i + 1)+ " can't be " + type1 + " and " + type2 + " at the same time.");
            }
        }
    }

    //If we have Class "X" extends "Y", get [Y] name back
    public String fetchSuperClassName(String dClass){
        
        String superC = null;

        if(lhm.containsKey(dClass)){
            SymbolTable itsClass = lhm.get(dClass);
            superC = itsClass.superC;
        }

        return superC;
    }
}

//Pointer to a symbol table
class STPtr {

    //Here we'll store the type of a variable/function
    String type;

    //Pointer to a next SymbolTable
    SymbolTable nextScope;

    //Here, the number of arguments will be stored, if we have a function
    int numArgs;

    //Constructor
    public STPtr(String type, int numArgs){

        this.type = type;
        this.numArgs = numArgs;
        nextScope = null;
    }

    //Copy Constructor
    public STPtr(int numArgs) {
        this.numArgs = numArgs;
        this.type = null;
        this.nextScope = null;
    }

    public void initializeSymbolTable(){
        if(nextScope == null){
            nextScope = new SymbolTable();
        }
    }

    public void checkExistence(String name, String fName) throws Exception {
        boolean exists = nextScope.lhm.containsKey(name);
        if(exists==true){
            throw new Exception("Variable [" + name + "] is already defined in method " + fName + ".");
        }
    }

    public void insert(String name, String type, String fName) throws Exception {

        initializeSymbolTable();
        checkExistence(name, fName);
        nextScope.insert(name, type, -1);
    }

    //Fetch a super-class function, go up as many level as it gets.
    public void fetchSuperFunction(String superC, String func, classTable cTable) throws Exception {

        // System.out.println("superclass is: " + superC);

        if(superC == null) {
            throw new Exception("Variable/method: " + func + " does not exist at any SuperClass");
        }

        SymbolTable classT = cTable.lhm.get(superC);
        STPtr funcPtr = classT.lhm.get(func);

        if(funcPtr!=null){

            System.out.println("peos peos peos" + func);
            this.type = funcPtr.type;
            this.nextScope = funcPtr.nextScope;
            this.numArgs = funcPtr.numArgs;
            return;
        }

        else    
            fetchSuperFunction(classT.superC, func, cTable);
    }


}

//The "real" symbol table. Here, we'll keep track of the variable's names
class SymbolTable {

    LinkedHashMap<String, STPtr> lhm;
    STPtr newPtr;
    String superC;

    public SymbolTable() {

        lhm = new LinkedHashMap<String, STPtr>();
        superC = null;
    }

    public void insert(String name, String type, int numArgs){

        newPtr = new STPtr(type, numArgs);
        lhm.put(name, newPtr);
    }

    public void extendsClass(String superclass){
        superC = superclass;
    }

    public void insertMethodVariables(String fName, String vName, String type) throws Exception {

        newPtr.insert(vName, type, fName);
        lhm.put(fName, newPtr);
    }

    public void methodReturnTypeCheck(String fName, String exprType) throws Exception{

        //Get the STPtr of current function
        STPtr curPtr = lhm.get(fName);
        
        //Get the type of the function
        String fType = curPtr.type;
        System.out.println(fType);

        //Check if we have type equality between function and returning type
        if(fType != exprType) {
            throw new Error("Type of method [" + fName + "] is: " + fType + " while returning type is: " + exprType);
        }
    }

    public String variableType(String var) {
        //Get the STPtr of current variable
        STPtr curPtr = lhm.get(var);

        //Get the type of variable
        String varType = curPtr.type;

        return varType;
    }

    public int methodReturnNumArgs(String fName) {
        
        //Get the STPtr of current function
        STPtr curPtr = lhm.get(fName);

        //Get the number of arguments of the function
        int numArgs = curPtr.numArgs;

        return numArgs;
    }

}