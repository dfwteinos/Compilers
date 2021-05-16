class Example {
    public static void main(String[] args) {
    }
}

// class B extends A {
    // int i;

    // public int foo(int i, int j) { return i+j; }
    // public int foobar(boolean k){ return 1; }
// }


class A {
    int i;
    A a;

    public int foo(int i, int j) { return i+j; }
    public int bar(int k){ return 1; } 
}

class B extends A {
    int i;

    public int foo(int i, int j) { return i+j; }
    public int foobar(boolean k){ return 1; }
}