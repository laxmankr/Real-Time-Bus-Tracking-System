package com.example.laxman.bussuvidha;

public class UserInformation {
    public String email;
    public String name;
    public String address;
    public String phone;
    public String busName;
    public String busRoute;

    UserInformation(){ }

    UserInformation(String email, String name, String address, String phone,String busName,String busRoute){
        this.email=email;
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.busName=busName;
        this.busRoute=busRoute;
    }
    public String getUserName(){
        return this.name;
    }
    public String getUserAddress(){
        return this.address;
    }
    public String getUserPhone(){
        return this.phone;
    }



}
