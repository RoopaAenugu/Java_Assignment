package com.vehicle;

public interface Vehicle {
    void  start();
    void  stop();
    void  accelerate();
    String   color(String color);
    void   brake();
    float  speed(float speed);
    int  numberOfWheels( int wheelsCount);
    float  cost(float price);
    String  brand(String brand);
}
