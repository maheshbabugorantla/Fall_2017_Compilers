import java.io.*;
import java.util.*;
import java.lang.Exception;

public class InfixToPostfix {

    private static boolean isOperator(String c)
    {
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("^") || c.equals("(") || c.equals(")");
    }

    private static boolean isLowerPrecedence(String op1, String op2)
    {
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
        String str = String.join("", words);
        //System.out.println("without spaces: " + str);

        String regex = "((?<=\\+)|(?=\\+))|((?<=\\*)|(?=\\*))|((?<=\\/)|(?=\\/))|((?<=\\-)|(?=\\-))";
        regex = regex + "|((?<=\\()|(?=\\())|((?<=\\))|(?=\\)))";

        return str.split(regex);
    }



    public static String convertStringToPostfix2(String infix) {

        String[] words = SplittingInfix(infix);

        System.out.println(String.join(",", words));

        Stack<String> stack = new Stack<String>();
        StringBuffer postfix = new StringBuffer(infix.length());

        String c;

        for (int i = 0; i < words.length; i++)
        {
            c = words[i];
            System.out.println("main c: " + c);

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
                        System.out.println("pushed1: " + popped);
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
                            System.out.println("popped2: " + pop + "c: " + c);
                            if (!c.equals("("))
                            {
                                System.out.println("pushed2: " + pop);
                                postfix.append(pop + " ");
                            } else {
                                c = pop;
                            }
                        }
                        System.out.println("pushed3: " + c);

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

        System.out.println(String.join(",", words));

        Stack<String> stack = new Stack<String>();
        StringBuffer postfix = new StringBuffer(infix.length());

        String c;

        for (int i = 0; i < words.length; i++)
        {
            c = words[i];
            System.out.println("main c: " + c);

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
                        System.out.println("pushed1: " + popped);
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
                            System.out.println("popped2: " + pop + "c: " + c);
                            if (!c.equals("("))
                            {
                                System.out.println("pushed2: " + pop);
                                postfix.append(pop + " ");
                            } else {
                                c = pop;
                            }
                        }
                        System.out.println("pushed3: " + c);

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

}