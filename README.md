In this repository we explore the fundamental concepts and techniques behind a compiler. :scroll:

# LL(1) Calculator Parser:

In this directory, I've implemented a basic LL(1) Parser for simple operations, such as `'+'` , `'-'` , `'**'` (meaning: `'^'`).

I've implemented 2 possible approaches for this assignment. One is by using exclusively Recursion `../RecEval` and the other by utilizing the help of the Stack data structure `../StackEval`. Both of these approaches are producing the **same** result.

At first, they gave us an non-LL(1) grammar. Ofcourse, we removed **ambiguity**, we've added **precedence** between operators, also we removed **left recursion** and we've applied **left factoring** where it was needed.
We had the following grammar:


 ```
 1. exp   -> term exp2
 
 2. exp2  -> + term exp2
 3.        | - term exp2
 4.        | ε
 
 5. term  -> factor term2
 
 6. term2 -> ** factor term2
 7.        | ε
 
 8. factor -> num
 9.        | (exp)
 
 10. num  -> digit post
 
 11. post -> num  
 12.       | ε
 
 13. digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
 ```
 
 We also have the **lookahead table** in the comments of `LL(1)-Calculator-Parser/(RecEval|StackEval)/CalculatorParser.java`.
 
 ## Compile & Run:
 
 In order to compile this program, the user has only to type:
 
 *    `make`
 *    `make run`
 
 And then he can type any expression he wants to,as long as it respects the rules of our LL(1) grammar.
 
 Ofcourse, our programm can evaluate any legal expression, no matter how complicated it is.
 We have already some used cases in `test_cases.txt` file.

 Last but not least, whenever the user has finished, he can simply type: `make clean`.
