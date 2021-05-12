class A {

    int k;
    boolean b;
    A aa;

    public int foo() {
        return this.foo();
    }

    public int foo2(){}
}


class B extends A {

    int k2;
    int k;

    public int bar() {
        boolean k;
        return k + this.foo();
    }

    public int foo(){

    }
}

a1 = new A();

a1 ->   [_________]
        [__int k__] 0
        [boolean b] 4
        [_A aa____] 5
        [_________] 13

A.k : 0
a.b : 4
A.aa: 5

A.Foo: 0
A.foo2: 8
B.bar:16

B.k2: 13
B.k:  17

B ->    [_0_] -> B.foo h A.foo(?) dn 8a ektupwsoume to B.foo, mono to A.foo, pou einai sthn idia 8esh me to B.foo pou kanei override to A.foo.
        [_8_] -> A.foo2
        [_16] -> B.bar