/*                                    Lookahead Table:

*---------------------------------------------------------------------------------------------
*           |  '+' , '-' |    '**'    |    '0' .. '9'    |    '('     |     ')'     |   '$'   |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*   exp2    | term, exp2 |    error   |      error       |    error   |      ε      |    ε    |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*   term2   |     ε      |factor,term2|      error       |    error   |      ε      |    ε    |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*   factor  |   error    |   error    |       num        |    exp )   |    error    |  error  |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*    num    |   error    |   error    |   digit , post   |   error    |    error    |  error  |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------   
*           |            |            |                  |            |             |         |
*   digit   |   error    |   error    |    (nothing)     |   error    |    error    |  error  |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*   post    |     ε      |     ε      |       num        |   error    |      ε      |    ε    |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*   exp     |    error   |    error   |   term , exp2    |term , exp2 |    error    |  error  |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*  term     |    error   |    error   |  factor , term2  |factor,term2|    error    |  error  |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*/

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.*;
import java.io.IOException;
import java.lang.*;

public class CalculatorParser {

    /* We'll keep a 'pointer' of our tokens in the expression */
    /* e.g: String exp = '5+3-2' , if token = 0, exp.charAt(token) = '5' */ 
    public static int token = 0;

    /*  A variable to keep the length of our string */
    public static int eos;

    /* Here, we'll keep the given expression(s) */
    public static String expression;

    /* A string in order to verify our expression */
    public static String verify_exp = "";

    /* The current token */
    public static String cur_token;

    /* Accumulative variable in order to detect 'forbbiden' numbers, such as: 01, 02, ... , 0number(s). */
    public static int forb;

    /* Stack for multiple ** in our program */
    Stack<Integer> powers = new Stack<Integer>();    

    /* A list with all the numbers, in order to check if a token is a 'number' */
    public static List<String> numbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    public int eval() throws IOException, ParseError{

        /* Initialize standard input stream */
        final Scanner sc = new Scanner(System.in);
        
        /* Read the expression from standard input */
        expression = sc.nextLine();
        
        /* Remove all the blank spaces and/or tabs in our expression */
        expression = expression.replaceAll("\\s", "");

        /* The length of the expression */
        eos = expression.length();

        /* The final value of our expression */
        int val = exp();

        /* Close the input stream scanner */
        sc.close();

        if(expression.equals(verify_exp)){

            return val;
        }

        throw new ParseError();

    }


/*=================================================================*/
/*           Recursive Functions == (Non) Terminal Rules           */
/*=================================================================*/

    /* # 1 */
    public static int exp() throws IOException, ParseError{

        CalculatorParser.cur_token = nextToken();

        if( CalculatorParser.cur_token.equals("(") || equalsNumbers() ){

            int v1 = term();
            int v2 = exp2(v1);
            return v2;

        }

        throw new ParseError();
    }

    /* # 2 */
    public static int exp2(int v0) throws IOException, ParseError{

        if( equalStrings("+") || equalStrings("-")){

            String op;
            if(equalStrings("+")){
                op = "+";
            }
            
            else op = "-";

            CalculatorParser.verify_exp += CalculatorParser.cur_token;
            CalculatorParser.cur_token = nextToken();

            int v1 = term();
            int v2;

            if (op.equals("+")){
                v2 = v0 + v1;
            }

            else v2 = v0 - v1;

            int v3 = exp2(v2);

            return v3;

        }
        
        else if(equalStrings(")")|| eof()){
            return v0;
        }

        else throw new ParseError();
    }

    /* #5 */
    public static int term() throws IOException, ParseError{

        if( CalculatorParser.cur_token.equals("(") || equalsNumbers() ){
            
            int v1 = factor();
            int v2 = term2(v1);
            return v2;

        }

        throw new ParseError();
    }

    /* # 6 */
    public static int term2(int v0) throws IOException, ParseError {

        if( equalStrings("*") ){

            CalculatorParser.verify_exp += CalculatorParser.cur_token;
            CalculatorParser.cur_token = nextToken();

            if( !eof()){
                if( ("*").equals(CalculatorParser.cur_token)){
                    CalculatorParser.verify_exp += CalculatorParser.cur_token;
                    CalculatorParser.cur_token = nextToken();
                }
            }

            int v1 = factor();
            int v2 = term2(v1);
            return pow(v0,v2);
        }

        else if(equalStrings("+") || equalStrings("-") || equalStrings(")") || eof()){
            return v0;
        }
        else throw new ParseError();
    }

    /* #8 */
    public static int factor() throws IOException, ParseError{
        
        if( CalculatorParser.cur_token.equals("(") ){

            CalculatorParser.verify_exp += CalculatorParser.cur_token;

            int v0 = exp();
            CalculatorParser.verify_exp += CalculatorParser.cur_token;
            CalculatorParser.cur_token = nextToken();
            
            return v0;
        }

        if(equalsNumbers()){

            String numS =  num();
            int num = Integer.parseInt(numS);
            return num;

        }

        throw new ParseError();
    }

    /* # 10 */
    public static String num() throws IOException, ParseError {
         
        if(equalsNumbers()){

            String s1 = digit();
            String s2 = post();
            String s3 = s1 + s2; 

            return s3;
        }

        throw new ParseError();
    }

    /* # 11 */
    public static String post() throws IOException, ParseError{

        if(equalsNumbers()){
            return( num());
        }

        else if( equalStrings("+") || equalStrings("-") || equalStrings("*") || equalStrings(")") || eof() ){
            if(equalStrings("*")){

                CalculatorParser.verify_exp += CalculatorParser.cur_token;
                CalculatorParser.cur_token = nextToken();
            }
            return "";
        }

        else throw new ParseError();
    }

    /* # 13 */
    public static String digit() throws IOException, ParseError {

        if(equalsNumbers()){

            CalculatorParser.verify_exp += CalculatorParser.cur_token;
            String s = CalculatorParser.cur_token;


            /* Checking if we have any forbidden number */

            /* First case: If the first input is 01, 02, 03, ... , 0. */
            if(CalculatorParser.token==2){
                
                int tok = CalculatorParser.token - 2;
                String value = String.valueOf(CalculatorParser.expression.charAt(tok));
                if(value.equals("0")){
                    throw new ParseError();
                }
            }

            /* Second case: If one expression of our input, has some values such as: 1+01, 30**001, etc.. */
            else if(CalculatorParser.token>2){
                
                if(!equalStrings("0")){

                    int tok = CalculatorParser.token - 2;
                    String value = String.valueOf(CalculatorParser.expression.charAt(tok));
                    CalculatorParser.cur_token = value;

                    if(equalsNumbers()){
                        if(!(checkForbiden(CalculatorParser.token-1))){
                            throw new ParseError();
                        }
                    }
                }
            }

            CalculatorParser.cur_token = nextToken();
            return s;
        }
    
        else throw new ParseError();
    }




/*=================================================================*/
/*                  Auxiliarry Functions                           */
/*=================================================================*/




    /* Function to determinate if 2 strings are the same */
    public static boolean equalStrings(final String string){

        return(CalculatorParser.cur_token.equals(string));
    }

    /* Function to return us the next token of our string */
    public static String nextToken(){
        
        if(!eof()){
            int cur = CalculatorParser.token;
            CalculatorParser.token++;
            String value = String.valueOf(CalculatorParser.expression.charAt(cur));
            return value;
        }
        
        else {
            return "$";
        }
    }

    /* Function to return us true if we are at the end of string, otherwise false */
    public static boolean eof(){
        return(CalculatorParser.token == CalculatorParser.eos);
    }

    /* Function to determinate if the current token is equal to a number */
    public static boolean equalsNumbers(){

        for(int i=0; i < CalculatorParser.numbers.size(); i++){

            if( (CalculatorParser.cur_token).equals(CalculatorParser.numbers.get(i)) ){
                return true;
            }
        }
        return false;
    }   

    /* Iterative function in order to check if a number is "forbidden" */
    public static boolean checkForbiden(int counter){

        counter--;
        String value = String.valueOf(CalculatorParser.expression.charAt(counter));

        while(value.equals("0")){
            counter--;
            value = String.valueOf(CalculatorParser.expression.charAt(counter));
        }

        CalculatorParser.cur_token = value;
        if(equalsNumbers()){
            return true;
        }

        return false;
    }

    /* Function to compute the power of a digit to another */
    /* Took it from course's instructors */
    public static int pow(int base, int exponent){
        
        if(exponent < 0)
            return 0;
        if(exponent==0)
            return 1;
        if(exponent==1)   
            return base;
        
        if(exponent % 2 == 0 )
            return pow(base*base, exponent/2);
    
        else
            return base * pow(base*base, exponent/2);
    }
   