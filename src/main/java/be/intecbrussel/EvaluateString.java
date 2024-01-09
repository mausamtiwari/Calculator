package be.intecbrussel;

import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.Stack;

public class EvaluateString {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter a mathematical expression (q to quit): ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("q")) {
                break;
            }

            try {
                // Evaluate and print the result
                // The entered mathematical expression is passed to the evaluateExpression method.
                double result = evaluateExpression(userInput);
                System.out.println("Result: " + result);
            } catch (ArithmeticException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (EmptyStackException e) {
                System.out.println("Error: Close the parenthesis or a number after an operator expected.");
            }
        }
    }

    public static double evaluateExpression(String expression) {
        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {

            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // Current token is a number, push it to stack for numbers
            if (tokens[i] >= '0' && tokens[i] <= '9') {
                StringBuilder sb = new StringBuilder();

                // There may be more than one digit in number
                while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9')
                    sb.append(tokens[i++]);
                values.push(Double.parseDouble(sb.toString()));

                // right now the i points to the character next to the digit, since the for loop also increases
                // the i, we would skip one token position; we need to decrease the value of i by 1 to correct the offset.
                i--;
            }


            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(')
                ops.push(tokens[i]);


                // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop();

            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/' || tokens[i] == '%') {
                // While top of 'ops' has same or greater precedence to current token, which is an operator.
                // Apply operator on top of 'ops' to top two elements in values stack
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                // Push current token to 'ops'.
                ops.push(tokens[i]);

            } else if (tokens[i] != '+' || tokens[i] != '-' || tokens[i] != '*' || tokens[i] != '/' || tokens[i] != '%' || (tokens[i] <= '0' && tokens[i] >= '9')) {
                throw new IllegalArgumentException("Tokens not excepted.");
            }

        }

        // Entire expression has been parsed at this point, apply remaining ops to remaining values
        while (!ops.empty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));


        // Top of 'values' contains result, return it
        return values.pop();
    }


    // Returns true if 'op2' has higher or same precedence as 'op1', otherwise returns false.
    public static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;

        if ((op1 == '*' || op1 == '/' || op1 == '%') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    // A utility method to apply an operator 'op' on operands 'a' and 'b'. Return the result.
    public static double applyOp(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new ArithmeticException("Cannot divide by zero.");

                return a / b;
            case '%':
                if (b == 0)
                    throw new ArithmeticException("Cannot divide by zero.");

                return a % b;

        }
        return 0;
    }

}