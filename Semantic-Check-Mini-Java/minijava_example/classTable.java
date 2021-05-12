import java.util.*;

//In this data structure, we'll keep info about each class with its symbol table.
public class classTable {

    LinkedHashMap<String, SymbolTable> lhm;
    SymbolTable curTable;

    public classTable(){

        lhm = new LinkedHashMap<String, SymbolTable>();
    }

    public void insert(String cName){

        curTable = new SymbolTable();
        lhm.put(cName, curTable);
    }

    public void insertSymbol(String cName, String fName, String type) {

        curTable.insert(fName, type);
        lhm.put(cName, curTable);

    }

    public void insertMethodVariables(String fName, String vName, String type) {

        curTable.insertMethodVariables(fName, vName, type);
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

    public void insert(String name, String type) {

        nextScope = new SymbolTable();
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

    public void insertMethodVariables(String fName, String vName, String type) {

        newPtr.insert(vName, type);
        lhm.put(fName, newPtr);
    }

}