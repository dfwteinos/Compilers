import java.io.IOException;
import java.lang.*;

class Main {
    public static void main(String[] args){
        try{
            System.out.println(new CalculatorParser().eval());
        }
        catch (IOException | ParseError e){
            System.err.println(e.getMessage());
        }
    }
}