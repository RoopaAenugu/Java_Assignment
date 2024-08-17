package com.javatraining;

import java.util.Scanner;

public class NumberGuessingGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int randomNumber = (int) Math.ceil(Math.random() * 100);
        while (true) {
            System.out.println("enter value");
            int userInput = scanner.nextInt();
            if (userInput < randomNumber) {
                System.out.println("enter value is too low");
            } else if (userInput > randomNumber) {
                System.out.println("enter value is too high");
            } else {
                System.out.println("congratulations your value is correct");
                break;
            }
        }
    }
}
