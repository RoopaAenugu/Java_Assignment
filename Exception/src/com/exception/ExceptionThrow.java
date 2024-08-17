package com.exception;

public class ExceptionThrow {
    public static void main(String[] args) {
        ExceptionThrow ex=new ExceptionThrow();
        ex.test();

    }
    public  void test(){
        try{
            getEmp(-1);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public static void getEmp(int id) throws Exception{
        if(id<0){
            throw new Exception();
        }

    }
}
