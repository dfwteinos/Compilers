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

# Translator to Java:

Inside this dir, I've implemented a parser generator for a LALR() language, supporting string operations. The language supports the concatenation (+) operator over strings, function definitions and calls, conditionals(if-else i.e every "if" must be followed by an "else"), and the following logical expressions:

*    is-prefix-of (string1 prefix string2) : Whether string1 is a prefix of string2.
*    is-suffix-of (string1 suffix string2) : Whether string1 is a suffix of string2.

All values in the language are strings.

 ## Compile & Run:

In order to compile this program, the user have only to type:

`make compile`
`make execute`

 And then he can type any expression he wants to. Our program prints a fully working Java program. 
 
 Some examples:
 
 ## Example 1):

**Input:**

 ```
name()  {
    "John"
}

surname() {
    "Doe"
}

fullname(first_name, sep, last_name) {
    first_name + sep + last_name
}

name()
surname()
fullname(name(), " ", surname())
```

**Output (Java):**
```
public class Main {
    public static void main(String[] args) {
        System.out.println(name());
        System.out.println(surname());
        System.out.println(fullname(name(), " ", surname()));
    }
    
    public static String name() {
        return "John";
    }
    
    public static String surname() {
        return "Doe";
    }
    
    public static String fullname(String first_name, String sep, String last_name) {
        return first_name + sep + last_name;
    }
}
 ```
 
## Example 2):

**Input**:

```
name() {
    "John"
}


repeat(x) {
    x + x
}

cond_repeat(c, x) {
    if (c prefix "yes")
        if("yes" prefix c)
            repeat(x)
        else
            x
    else
        x
}

cond_repeat("yes", name())
cond_repeat("no", "Jane")
```

**Output**:

```
import java.lang.*;

public class Main {
        public static void main(String[] args) {
                System.out.println(cond_repeat("yes", name()));
                System.out.println(cond_repeat("no", "Jane"));
        }

        public static String cond_repeat(String c, String x) {
                 return ("yes".startsWith(c)) ? (c.startsWith("yes")) ? repeat(x) : x : x;
        }

        public static String repeat(String x) {
                 return x.concat(x);
        }

        public static String name() {
                 return "John";
        }

}
```

## Example 3):

**Input**:

```
findLangType(langName) {
    if ("Java" prefix langName)
        if(langName prefix "Java")
            "Static"
        else
            if("script" suffix langName)
                "Dynamic"
            else
                "Unknown"
    else
        if ("script" suffix langName)
            "Probably Dynamic"
        else
            "Unknown"
}

findLangType("Java")
findLangType("Javascript")
findLangType("Typescript")
```

**Output**:

```
import java.lang.*;

public class Main {
        public static void main(String[] args) {
                System.out.println(findLangType("Java"));
                System.out.println(findLangType("Javascript"));
                System.out.println(findLangType("Typescript"));
        }

        public static String findLangType(String langName) {
                 return (langName.startsWith("Java")) ? ("Java".startsWith(langName)) ? "Static" : (langName.endsWith("script")) ? "Dynamic" : "Unknown" : (langName.endsWith("script")) ? "Probably Dynamic " : "Unknown";
        }

}

```

# Further Informations:

*This project is part of the course: Compilers(K31) , Spring of 2021. University of Athens, DiT.*
