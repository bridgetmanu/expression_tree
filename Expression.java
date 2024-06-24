// Starter code for Project 1

// Change this to your NetID
package bsm180003;

import java.util.*;
import java.io.File;
import java.util.Deque;
import java.io.FileNotFoundException;
import java.util.Scanner;


/** Class to store a node of expression tree
 For each internal node, element contains a binary operator
 List of operators: +|*|-|/|%|^
 Other tokens: (|)sample 
 Each leaf node contains an operand (long integer)
 */

public class Expression
{
    public enum TokenType {  // NIL is a special token that can be used to mark bottom of stack
        PLUS, TIMES, MINUS, DIV, MOD, POWER, OPEN, CLOSE, NIL, NUMBER
    }

    public static class Token {
        TokenType token;
        int priority; // for precedence of operator
        Long number;  // used to store number of token = NUMBER
        String string;

        Token(TokenType op, int pri, String tok)
        {
            token = op;
            priority = pri;
            number = null;
            string = tok;
        }

        // Constructor for number.  To be called when other options have been exhausted.
        Token(String tok)
        {
            token = TokenType.NUMBER;
            number = Long.parseLong(tok);
            string = tok;
        }

        boolean isOperand()
        {
            return token == TokenType.NUMBER;
        }

        public long getValue()
        {
            return isOperand() ? number : 0;
        }

        public String toString()
        {
            return string;
        }
    }

    Token element;
    Expression left, right;


    /**
     * Create token corresponding to a string
     * @param tok is a string
     * tok is "+" | "*" | "-" | "/" | "%" | "^" | "(" | ")"| NUMBER
     * NUMBER is either "0" or "[-]?[1-9][0-9]*
     * @return The token
     */
    static Token getToken(String tok)
    {
        Token result;
        switch(tok)
        {
            /**
             * The higher the number, the higher the priority
             * "^"has the highest precedence of all operators
             * "*" has the same precedence as "%" and "/"
             * "+" has the same precedence as "-"
             * "(" & ")" overides operators & associativity
             * "[-]?[1-9][0-9]*" allows for negative #s and multiple digit #s
             */
            case "^":
                result = new Token(TokenType.POWER, 3, tok);
                break;
            case "*":
            result = new Token(TokenType.TIMES, 2, tok);
                break;
            case "/":
                result = new Token(TokenType.DIV, 2, tok);
                break;
            case "%":
                result = new Token(TokenType.MOD, 2, tok);
                break;
            case "+":
                result = new Token(TokenType.PLUS, 1, tok);
                break;
            case "-":
                result = new Token(TokenType.MINUS, 1, tok);
                break;
            case "(":
                result = new Token(TokenType.OPEN, 0, tok);
                break;
            case ")":
                result = new Token(TokenType.CLOSE, 0, tok);
                break;
            default:
                if (tok.matches("[-]?[1-9][0-9]*"))
                {
                    result = new Token(tok);
                }
                else {
                    result = new Token(tok);
                }
                break;
        }
        return result;
    }

    private Expression() {
        element = null;
    }

    private Expression(Token oper, Expression left, Expression right)
    {
        this.element = oper;
        this.left = left;
        this.right = right;
    }

    private Expression(Token num) {
        this.element = num;
        this.left = null;
        this.right = null;
    }

    /**
     * Given an infix expression, return expression tree
     * @param list a list of tokens corresponding to an infix expression
     * @return the expression tree corresponding to it
     */

    public static Expression infixToExpression(List<Token> exp)
    {
        Deque <Token> press = new ArrayDeque<Token>();
        ArrayDeque<Expression> Exp = new ArrayDeque<>();

/**
 * press: initialized stack for the operators
 * Exp: initialized stack for the operands
 * for loop iterates over each token
 * if expt is an operand, add to expression tree stack.
 * if it is "(", add to press stack
 * if the Token is a ")":
 * if stack is not  "(", move operators from press stack to exp stack
 * create an expression tree with operator as root
 * add the expression tree to the expression stack
 * remove "(" from press stack
 * while the stack is not empty, add the elements in there to the Exp stack
 * Create the final tree then pop
 */

        for(Token e: exp)
        {
            if (e.isOperand()) {
                Expression c = new Expression(e);
                Exp.push(c);
            }
            else if (e.token == TokenType.OPEN)
            {
                    press.push(e);
            }

            else if (e.token == TokenType.CLOSE)
            {
                while (!press.isEmpty() && press.peek().token != TokenType.OPEN)
                {
                    Expression t = null;
                    Token a = press.removeFirst();
                    Expression b = Exp.removeFirst();
                    Expression c = Exp.removeFirst();
                    t = new Expression(a, c, b);
                    Exp.push(t);
                }
                press.pop();

            }
            else
            {
                if (!press.isEmpty()) {
                    while (!press.isEmpty() && e.priority <= press.peek().priority && press.peek().token != TokenType.OPEN)
                    {
                        Expression t = null;
                        Token a = press.removeFirst();
                        Expression b = Exp.removeFirst();
                        Expression c = Exp.removeFirst();
                        t = new Expression(a, c, b);
                        Exp.push(t);
                    }
                    press.push(e);
                }
                else
                {
                    press.push(e);
                }
            }
        }

        while (!press.isEmpty() && !Exp.isEmpty())
        {
            Token a = press.removeFirst();
            Expression b = Exp.removeFirst(); // right expression
            Expression c = Exp.removeFirst(); // left expression
            Expression t = new Expression(a, c, b);
            Exp.push(t);
        }
        Expression tree = Exp.pop();
        return tree;
    }


    /**
     * Given an infix expression, return postfix expression
     * @param list a list of tokens corresponding to an infix expression
     * @return arraylist(list of tokens) of postfix expression corresponding to it
     */

    public static List<Token> infixToPostfix(List<Token> exp) {
        /**
         * initialize arraylist to store the postfix output
         * arraydeque as stack for the operators
         */
        ArrayList<Token> pfix = new ArrayList<Token>();

        Deque<Token> post = new ArrayDeque<>();

        for (Token expt : exp)
        {
            /**
             * for loop iterates over each token in the list
             * if expt is an operand, add to arraylist.
             * elif token is "(", push it to stack
             * elif the expt is a ")" & top of  stack is not  "(", move operators from stack to arraylist
             * then pop "(" from stack
             */

            if (expt.isOperand()) {
                pfix.add(expt);
            }
            else if (expt.token == TokenType.OPEN) {
                post.push(expt);
            }


            else if (expt.token == TokenType.CLOSE)
            {
                while (!post.isEmpty() && post.peek().token != TokenType.OPEN)
                {
                    pfix.add(post.pop());
                }
                post.pop();
            }

            /**
             * else statement for the other operators
             * if the top of the stack is not "(" and token has a lower priority than the operator on top of stack
             * pop operator in stack and add to arraylist
             * then add token to operator stack
             */

            else
            {
                while (!post.isEmpty() && expt.priority <= post.peekFirst().priority)
                {
                    Token a = post.pop();
                    pfix.add(a);
                }
                post.push(expt);
            }
        }

        /**
         * while loop make sure that remaining operators in stack are added to arraylist
         * while the stack is not empty, add the elements in there to the arraylist
         */

        while (!post.isEmpty())
        {
            pfix.add(post.pop());
        }
        return pfix;
    }

    /**
     * Given a postfix expression, it evaluates and return the value
     * @param tokens a list of tokens corresponding to a postfix expression
     * @return a value corresponding to it
     * @throws UnsupportedOperationException if a value is divided by zero
     */

    public static long evaluatePostfix(List<Token> exp)
    {
        Deque<Long> posteval = new ArrayDeque<Long>(); //

        for(Token t: exp) // iterates list given
        {
            /**
             * arraydeque stores tokens in stack as long
             * for loop iterates over each token in the list
             * if expt is an operand, add to stack.
             * if t is an operator, pop first two stack and solve then push it back to stack
             * evaluation:
             * pop out the first element on top of the stack
             * pop out the next first element on top of the stack
             * use switch statement: all are left-associated, so b has to be on the left for minus, mod, div, & power
             * if a is 0, it will be DNE, it will throw an exception
             * pop out the final operand in the stack which is the result
             */

            if (t.isOperand())
            {
                posteval.push(t.getValue());
            }
            else
            {
                long a = posteval.pop();
                long b = posteval.pop();

                switch (t.token)
                {
                    case TIMES:
                        posteval.push(a * b);
                        break;
                    case MINUS:
                        posteval.push(b - a);
                        break;
                    case MOD:
                        posteval.push(b % a);
                        break;
                    case PLUS:
                        posteval.push(b + a);
                        break;
                    case DIV:
                        posteval.push(b / a);
                        if (a == 0) throw new UnsupportedOperationException();
                        break;
                    case POWER:
                        posteval.push((long)Math.pow(b, a));
                        break;
                }
            }
        }
        long result = posteval.pop();
        return result;
    }

    /**
     * Given an expression tree, it evaluates and return the value
     * @param tree expression tree
     * @return a value corresponding to it
     * @throws UnsupportedOperationException if a value is divided by zero
     */

    public static long evaluateExpression(Expression tree)
    {
        Expression Tree = tree;
        long result = 0;
        if (Tree.left == null && Tree.right == null)
        {
            return Tree.element.getValue();
        }

        else
        {
            long leftval = evaluateExpression(Tree.left);
            long rightval = evaluateExpression(Tree.right);
            Token op = Tree.element;
            switch (op.token)
            {
                case PLUS:
                    result = leftval + rightval;
                    break;
                case MINUS:
                    result = leftval - rightval;
                    break;
                case TIMES:
                    result = leftval * rightval;
                    break;
                case MOD:
                    result = leftval % rightval;
                    break;
                case DIV:
                    result = leftval / rightval;
                    if (rightval == 0) throw new UnsupportedOperationException();
                    break;
                case POWER:
                    result = (long) Math.pow(leftval, rightval);
                    break;
            }
        }

        return result;
    }

    /**
     * Accepts input from command line and reads file to execute the code
     * @param String args
     * @throws FileNotFoundException if file path is incorrect
     */

    public static void main(String[] args) throws FileNotFoundException
    {
        Scanner in;

        if (args.length > 0)
        {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);


        }
        else
        {
            in = new Scanner(System.in);
        }

        File inputFile = new File("C:\\Users\\Bridget Manu\\IdeaProjects\\bsm180003\\src\\bsm180003\\p1testcase1.txt");
        in = new Scanner(inputFile);




        int count = 0;

        while(in.hasNext())
        {
            String s = in.nextLine();

            List<Token> infix = new LinkedList<>();
            Scanner sscan = new Scanner(s);
            int len = 0;
            while(sscan.hasNext())
            {
                infix.add(getToken(sscan.next()));
                len++;
            }

            if(len > 0)
            {
                count++;
                System.out.println("Expression number: " + count);
                System.out.println("Infix expression: " + infix);
                Expression exp = infixToExpression(infix);
                List<Token> post = infixToPostfix(infix);
                System.out.println("Postfix expression: " + post);
                long pval = evaluatePostfix(post);
                long eval = evaluateExpression(exp);
                System.out.println("Postfix eval: " + pval + " Exp eval: " + eval + "\n");
            }
        }
    }
}