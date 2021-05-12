import java.util.*;
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
    public void insert(String cName){

        curTable = new SymbolTable();
        lhm.put(cName, curTable);
    }

    //Insert a symbol in a specific className symbol table
    public void insertSymbol(String cName, String fName, String type) throws Exception {

        checkExistence(fName, cName);
        curTable.insert(fName, type);
        lhm.put(cName, curTable);

    }

    //Check in the current scope of a class the uniqueness of a new symbol
    public void checkExistence(String name, String cName) throws Exception {
        boolean exists = curTable.lhm.containsKey(name);
        if(exists==true){
            throw new Exception("Variable or Method [" + name + "] is already defined in class " + cName);
        }
    }

    //Insert in: class[ST] -> [..|..|Func[ST] -> [var|..] ..]  
    public void insertMethodVariables(String fName, String vName, String type) throws Exception {

        curTable.insertMethodVariables(fName, vName, type);
    }

    public void methodReturnTypeCheck(String fName, String expr) throws Exception{

        curTable.methodReturnTypeCheck(fName, expr);
    }
}

//Pointer to a symbol table
class STPtr {

    //Here we'll store the type of a variable/function
    String type;

    //Pointer to a next SymbolTable
    SymbolTable nextScope;

    public STPtr(String type){

        this.type = type;
        nextScope = null;
    }

    public void initializeSymbolTable(){
        if(nextScope == null){
            nextScope = new SymbolTable();
        }
    }

    public void checkExistence(String name, String fName) throws Exception {
        boolean exists = nextScope.lhm.containsKey(name);
        if(exists==true){
            throw new Exception("Variable [" + name + "] is already defined in method " + fName);
        }
    }

    public void insert(String name, String type, String fName) throws Exception {

        initializeSymbolTable();
        checkExistence(name, fName);
        nextScope.insert(name, type);
    }
}

//The "real" symbol table. Here, we'll keep track of the variable's names
class SymbolTable {

    LinkedHashMap<String, STPtr> lhm;
    STPtr newPtr;

    public SymbolTable() {

        lhm = new LinkedHashMap<String, STPtr>();
    }

    public void insert(String name, String type){

        newPtr = new STPtr(type);
        lhm.put(name, newPtr);
    }

    public void insertMethodVariables(String fName, String vName, String type) throws Exception {

        newPtr.insert(vName, type, fName);
        lhm.put(fName, newPtr);
    }

    public void methodReturnTypeCheck(String fName, String expr) throws Exception{

        //Get the STPtr of current function
        STPtr curPtr = lhm.get(fName);
        
        //Get the type of the function
        String fType = curPtr.type;
        System.out.println(fType);

        //Get the STPtr of current expression
        STPtr exprCell = curPtr.nextScope.lhm.get(expr);

        //Get the type of the expression
        String exprType = exprCell.type;

        //Check if we have type equality between function and returning type
        if(fType != exprType) {
            throw new Error("Type of method is: " + fType + " while returning type is: " + exprType);
        }
    }

}