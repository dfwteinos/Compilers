class Example {
    public static void main(String[] args) {
    }
}

class A {
    int i;
    boolean flag;
    int j;
    // A a;

    public int foo(){
        return 0;
    }
    public boolean fa(){
        return true;
    }    
    // public int foo(int i, int j) { return i+j; }
    // public int bar(int k){ return 1; } 
}

class B extends A {
    // int i;
    A type;
    int k;

    public int foo(){
        return 5;
    }
    public boolean bla(){
        return true;
    }

    // public int foo(int i, int j) { return i+j; }
    // public int foobar(boolean k){ return 1; }
}