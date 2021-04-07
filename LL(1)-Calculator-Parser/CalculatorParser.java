/*                                    Lookahead Table:

* --------------------------------------------------------------------------------------------
*           |  '+' , '-' |    '**'    |    '0' .. '9'    |    '('     |     ')'     |   '$'   |
* --------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*   exp2    | term, exp2 |    error   |     error        |    error   |      ε      |    ε    |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*   term2   |     ε      |factor,term2|     error        |    error   |      ε      |    ε    |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*   factor  |   error    |   error    |      num         |    exp )   |    error    |  error  |
*           |            |            |                  |            |             |         |
*---------------------------------------------------------------------------------------------
*           |            |            |                  |            |             |         |
*    num    |   error    |   error    |  digit , post    |   error    |    error    |  error  |
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

    /* A list with all the numbers, in order to check if a token is a 'number' */
    public static List<String> numbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    /* The Binary Tree that we are going to save the tokens and then parse it in infix order */
    public static BinaryTree tree = new BinaryTree();

    public static void main(final String[] args){

        /* Initialize standard input stream */
        final Scanner sc = new Scanner(System.in);
        
        /* Read the expression from standard input */
        expression = sc.nextLine();
        
        /* Remove all the blank spaces and/or tabs in our expression */
        expression = expression.replaceAll("\\s", "");

        /* The length of the expression */
        eos = expression.length();

        

        if(exp() && expression.equals(verify_exp)){
            System.out.println("Value is:" + verify_exp);
        }

        else{
            System.out.println("Parse error");
        }

        /* Close the input stream scanner */
        sc.close();
    }

    /* # 1 */
    public static boolean exp(){

        CalculatorParser.cur_token = nextToken();

        if( CalculatorParser.cur_token.equals("(") ){
            return(term() && exp2() );
        }

        if(equalsNumbers()){
            return(term() && exp2());
        }

        return false;
    }

    /* # 2 */
    public static boolean exp2(){

        if( equalStrings("+") || equalStrings("-")){

            CalculatorParser.verify_exp += CalculatorParser.cur_token;
            CalculatorParser.cur_token = nextToken();
            return(term() && exp2());
        }
        
        else if(equalStrings(")")|| eof()){
            return true;
        }

        else return false;
    }

    /* #5 */
    public static boolean term(){

        if( CalculatorParser.cur_token.equals("(") ){
            return(factor() && term2() );
        }

        if(equalsNumbers()){
            return(factor() && term2());
        }

        return false;
    }

    /* # 6 */
    public static boolean term2(){

        if( equalStrings("*") ){

            CalculatorParser.verify_exp += CalculatorParser.cur_token;
            CalculatorParser.cur_token = nextToken();

            if( !eof()){
                if( ("*").equals(CalculatorParser.cur_token)){
                    CalculatorParser.verify_exp += CalculatorParser.cur_token;
                    CalculatorParser.cur_token = nextToken();
                }
            }

            return(factor() && term2());
        }

        else if(equalStrings("+") || equalStrings("-") || equalStrings(")") || eof()){
            return true;
        }
        else return false;
    }

    /* #8 */
    public static boolean factor(){
        
        if( CalculatorParser.cur_token.equals("(") ){

            CalculatorParser.verify_exp += CalculatorParser.cur_token;
            if (exp()){
                CalculatorParser.verify_exp += CalculatorParser.cur_token;
                CalculatorParser.cur_token = nextToken();
                return true;
            }
        }

        if(equalsNumbers()){
            return( num() );
        }

        return false;
    }

    /* # 10 */
    public static boolean num(){
         
        if(equalsNumbers()){
            return(digit() && post());
        }

        return false;
    }

    /* # 11 */
    public static boolean post(){

        if(equalsNumbers()){
            return( num());
        }

        else if( equalStrings("+") || equalStrings("-") || equalStrings("*") || equalStrings(")") || eof() ){
            if(equalStrings("*")){
                CalculatorParser.verify_exp += CalculatorParser.cur_token;
                CalculatorParser.cur_token = nextToken();
            }
            return true;
        }

        else return false;
    }

    /* # 13 */
    public static boolean digit(){

        if(equalsNumbers()){

            CalculatorParser.verify_exp += CalculatorParser.cur_token;

            /* Checking if we have any forbidden number */

            /* First case: If the first input is 01, 02, 03, ... , 0. */
            if(CalculatorParser.token==2){
                
                int tok = CalculatorParser.token - 2;
                String value = String.valueOf(CalculatorParser.expression.charAt(tok));
                if(value.equals("0")){
                    return false;
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
                            return false;
                        }
                    }
                }
            }

            CalculatorParser.cur_token = nextToken();

            return true;
        }
    
        else return false;
    }

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
}