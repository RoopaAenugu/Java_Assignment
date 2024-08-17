package com.exception;

public class ExceptionExample {
    static int a=4;
    static int b=0;
    static int c;
    public static void main(String[] args) {

        try{
            c=a/b;
            System.out.println(c);

        }
        catch(ArithmeticException e){
            System.out.println(e);
        }
    }
}
