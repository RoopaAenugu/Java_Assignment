package com.vehicle;

public class Car implements Vehicle{
    String color;
    float speed;
     int wheelsCount;
     float price;
      String brand;


    @Override
    public void start() {
        System.out.println("Car start");

    }

    @Override
    public void stop() {
        System.out.println("Car stop");

    }

    @Override
    public void accelerate() {
        System.out.println("Car accelerate");


    }

    @Override
    public String color(String color) {
        this.color = color;
        return color;
    }

    @Override
    public void brake() {
        System.out.println("Car brake");

    }

    @Override
    public float speed(float speed) {
        this.speed = speed;
        return speed;

    }

    @Override
    public int numberOfWheels(int wheelsCount) {
        this.wheelsCount = wheelsCount;
        return wheelsCount;

    }

    @Override
    public float cost(float price) {
        this.price = price;
        return price;

    }

    @Override
    public String brand(String brand) {
        this.brand=brand;
        return brand;

    }
}
