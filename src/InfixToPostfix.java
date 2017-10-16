import java.io.*;
import java.util.*;
import java.lang.StringBuilder;
import java.lang.String;
import java.lang.Exception;

public class InfixToPostfix {

    private static boolean isOperator(String c) {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("^") || c.equals("(") || c.equals(")");
    }

    private static boolean isLowerPrecedence(String op1, String op2) {
        switch (op1)
        {
            case "+":
            case "-":
                return !(op2.equals("+") || op2.equals("-"));

            case "*":
            case "/":
                return op2.equals("^") || op2.equals("(");

            case "^":
                return op2.equals("(");

            case "(":
                return true;

            default:
                return false;
        }
    }

    public static String[] SplittingInfix(String infix) {
        //System.out.println("expression: " + infix);
        String[] words = infix.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for(String word: words) {
            stringBuilder.append(word);
        }

        String str = stringBuilder.toString();
        //System.out.println("without spaces: " + str);

        String regex = "((?<=\\+)|(?=\\+))|((?<=\\*)|(?=\\*))|((?<=\\/)|(?=\\/))|((?<=\\-)|(?=\\-))";
        regex = regex + "|((?<=\\()|(?=\\())|((?<=\\))|(?=\\)))";

        return str.split(regex);
    }

    public static String convertStringToPostfix2(String infix) {

        String[] words = SplittingInfix(infix);

        // System.out.println(String.join(",", words));

        Stack<String> stack = new Stack<String>();
        StringBuffer postfix = new StringBuffer(infix.length());

        String c;

        for (int i = 0; i < words.length; i++)
        {
            c = words[i];
            // System.out.println("main c: " + c);

            if (!isOperator(c))
            {
                postfix.append(c + " ");
            }
            else
            {
                if (c.equals(")"))
                {
                    while (!stack.isEmpty() && !(stack.peek().equals("(")))
                    {
                        String popped = stack.pop();
                        // System.out.println("pushed1: " + popped);
                        postfix.append(popped + " ");
                    }
                    if (!stack.isEmpty())
                    {
                        stack.pop();
                    }
                }
                else
                {
                    if (!stack.isEmpty() && !isLowerPrecedence(c, stack.peek()))
                    {
                        stack.push(c);
                    }
                    else
                    {
                        while (!stack.isEmpty() && isLowerPrecedence(c, stack.peek()))
                        {
                            String pop = stack.pop();
                            // System.out.println("popped2: " + pop + "c: " + c);
                            if (!c.equals("("))
                            {
                                // System.out.println("pushed2: " + pop);
                                postfix.append(pop + " ");
                            } else {
                                c = pop;
                            }
                        }
                        // System.out.println("pushed3: " + c);

                        stack.push(c);
                    }

                }
            }
        }
        while (!stack.isEmpty()) {
            postfix.append(stack.pop() + " ");
        }


        return postfix.toString();
    }

    public static String convertStringToPostfix(String infix) {

        String[] words = SplittingInfix(infix);

        // System.out.println(String.join(",", words));

        Stack<String> stack = new Stack<String>();
        StringBuffer postfix = new StringBuffer(infix.length());

        String c;

        for (int i = 0; i < words.length; i++)
        {
            c = words[i];
            // System.out.println("main c: " + c);

            if (!isOperator(c))
            {
                postfix.append(c + " ");
            }
            else
            {
                if (c.equals(")"))
                {
                    while (!stack.isEmpty() && !(stack.peek().equals("(")))
                    {
                        String popped = stack.pop();
                        // System.out.println("pushed1: " + popped);
                        postfix.append(popped + " ");
                    }
                    if (!stack.isEmpty())
                    {
                        stack.pop();
                    }
                }
                else
                {
                    if (!stack.isEmpty() && !isLowerPrecedence(c, stack.peek()))
                    {
                        stack.push(c);
                    }
                    else
                    {
                        while (!stack.isEmpty() && isLowerPrecedence(c, stack.peek()))
                        {
                            String pop = stack.pop();
                            // System.out.println("popped2: " + pop + "c: " + c);
                            if (!c.equals("("))
                            {
                                // System.out.println("pushed2: " + pop);
                                postfix.append(pop + " ");
                            } else {
                                c = pop;
                            }
                        }
                        // System.out.println("pushed3: " + c);

                        stack.push(c);
                    }

                }
            }
        }
        while (!stack.isEmpty()) {
            postfix.append(stack.pop() + " ");
        }


        return postfix.toString();
    }

    public static int Prec(String ch)
    {
        switch (ch)
        {
            case "+":
            case "-":
                return 1;

            case "*":
            case "/":
                return 2;

            case "^":
                return 3;
        }
        return -1;
    }

    static String infixToPostfix(String infix)
    {
        // initializing empty String for result
        String result = new String("");

        String[] words = SplittingInfix(infix);
        //System.out.println(words.toString());

        //System.out.println("what to parse: " + String.join(",", words));

        Stack<String> stack = new Stack<String>();
        String c = "";

        for (int i = 0; i < words.length; i++)
        {
            c = words[i];

            // If the scanned character is an operand, add it to output.
            if (!isOperator(c))
                result += c + " ";

                // If the scanned character is an '(', push it to the stack.
            else if (c.equals("("))
                stack.push(c);

                //  If the scanned character is an ')', pop and output from the stack
                // until an '(' is encountered.
            else if (c.equals(")"))
            {
                while (!stack.isEmpty() && !(stack.peek().equals("(")))
                    result += stack.pop() + " ";

                if (!stack.isEmpty() && !(stack.peek().equals("(")))
                    return "Invalid Expression"; // invalid expression
                else
                    stack.pop();
            }
            else // an operator is encountered
            {
                while (!stack.isEmpty() && Prec(c) <= Prec(stack.peek()))
                    result += stack.pop() + " ";
                stack.push(c);
            }

        }

        // pop all the operators from the stack
        while (!stack.isEmpty())
            result += stack.pop() + " ";


        //System.out.println("second result: " + result);
        return result;
    }

}