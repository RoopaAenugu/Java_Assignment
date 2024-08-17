package com.vehicle;

public class MainVehicle {
    public static void main(String[] args) {
        Vehicle myCar=new Car();
        System.out.println("car details");
        myCar.start();
        myCar.stop();
        myCar.accelerate();
        System.out.println("car color is: "+ myCar.color("red"));
        myCar.brake();
        System.out.println("car speed is: "+myCar.speed(80)+"km/h");
        System.out.println("car has "+ myCar.numberOfWheels(4)+" wheels");
        System.out.println("car cost is "+ myCar.cost(2000000));
        System.out.println("car brand is "+myCar.brand("BMW"));

        Vehicle myBike=new Bike();
        System.out.println("\n");
        System.out.println("Bike details");
        myBike.start();
        myBike.stop();
        myBike.accelerate();
        System.out.println("Bike color is: "+ myBike.color("red"));
        myBike.brake();
        System.out.println("Bike speed is: "+myBike.speed(40)+"km/h");
        System.out.println("Bike has "+ myBike.numberOfWheels(2)+" wheels");
        System.out.println("Bike cost is "+ myBike.cost(200000));
        System.out.println("Bike brand is "+myBike.brand("hero honda"));

        ElectricVehicle electricBike=new ElectricBike();
        System.out.println("\n");
        System.out.println("ElectricBike details");
        electricBike.start();
        electricBike.stop();
        electricBike.accelerate();
        System.out.println("ElectricBike color is: "+electricBike.color("black"));
        electricBike.brake();
        System.out.println("ElectricBike speed is: "+electricBike.speed(50));
        System.out.println("ElectricBike has: "+electricBike.numberOfWheels(2)+" wheels");
        System.out.println("ElectricBike cost is "+ electricBike.cost(400000));
        System.out.println("ElectricBike brand is "+electricBike.brand("Hero Electric Photon"));
        electricBike.batteryCharge();


        ElectricVehicle electricCar=new ElectricCar();
        System.out.println("\n");
        System.out.println("ElectricCar details");
        electricCar.start();
        electricCar.stop();
        electricCar.accelerate();
        System.out.println("ElectricCar color is: "+electricCar.color("white"));
        electricCar.brake();
        System.out.println("ElectricCar speed is: "+electricCar.speed(70));
        System.out.println("ElectricCar has: "+electricCar.numberOfWheels(4)+" wheels");
        System.out.println("ElectricCar cost is "+ electricCar.cost(400000));
        System.out.println("ElectricCar brand is "+electricCar.brand("Tesla"));
        electricCar.batteryCharge();


       // ElectricVehicle electricCar=new ElectricCar();
       // ElectricVehicle electricBike=new ElectricBike();

    }
}
