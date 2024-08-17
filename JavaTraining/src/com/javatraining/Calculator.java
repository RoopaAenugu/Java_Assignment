package com.javatraining;

import java.math.BigInteger;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Choose your option:\n" +
                    "+. Addition\n" +
                    "-. Subtraction\n" +
                    "*. Multiplication\n" +
                    "/. Division\n" +
                    "c. Exit");

            BigInteger c = null;
            System.out.println("enter operator");
            char ch = sc.next().charAt(0);
            if(ch=='c'){
                break;
            }
            System.out.println("enter a value");
            BigInteger a = sc.nextBigInteger();
            System.out.println("enter b value");
            BigInteger b = sc.nextBigInteger();

            switch (ch) {
                case '+':
                    c = a.add(b);
                    break;
                case '-':
                    c = a.subtract(b);
                    break;
                case '*':
                    c = a.multiply(b);
                    break;
                case '/':
                    try {
                        c = a.divide(b);
                    }
                    catch (ArithmeticException e) {
                        System.out.println(e
                        );
                    }
                    break;
                default:
                    System.out.println("invalid operator");


            }
            System.out.println(c);
        }
    }

}
