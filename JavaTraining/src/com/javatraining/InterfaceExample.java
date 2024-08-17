package com.javatraining;
interface  Shape{
    void area();
}
class circle implements Shape{
    int radius=2;

    @Override
    public void area() {
        int i = (22 / 7) * radius * radius;
        System.out.println("Area of circle" + i);

    }
}
public class InterfaceExample {
    public static void main(String[] args) {
        Shape s= (Shape) new circle();
        s.area();
    }
}
