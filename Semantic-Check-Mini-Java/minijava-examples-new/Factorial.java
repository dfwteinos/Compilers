class Factorial{
    public static void main(String[] a){
        int peos;
        System.out.println(new Fac().ComputeFac(10));
    }
}

class Fac {

    int x;
    int y;
    public int ComputeFac(int num){
        int num_aux;
        if (num < 1)
            num_aux = 1 ;
        else
            num_aux = num * (this.ComputeFac(num-1)) ;
        return num_aux ;
    }
}
