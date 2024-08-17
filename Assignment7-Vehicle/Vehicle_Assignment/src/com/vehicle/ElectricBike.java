package com.vehicle;

public class ElectricBike extends ElectricVehicle {
    String color;
    float speed;
    int wheelsCount;
    float price;
    String brand;

    @Override
    void batteryCharge() {
        System.out.println("Electric Bike is charging.");
    }

    @Override
    public void start() {
        System.out.println("Electric Bike is starting.");

    }

    @Override
    public void stop() {
        System.out.println("Electric Bike is stopping.");

    }

    @Override
    public void accelerate() {
        System.out.println("Electric Bike is accelerating");

    }

    @Override
    public String color(String color) {
        this.color = color;
        return color;
    }

    @Override
    public void brake() {
        System.out.println("Electric Bike is braking");

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
        this.brand = brand;
        return brand;
    }
}
