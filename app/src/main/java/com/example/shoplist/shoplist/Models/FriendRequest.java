package com.example.shoplist.shoplist.Models;

public class FriendRequest {

    public String SenderRequest;
    public String ReceiverRequest;
    public String StatusRequest;

    public FriendRequest(String SenderRequest, String ReceiverRequest, String StatusRequest){
        this.SenderRequest = SenderRequest;
        this.ReceiverRequest = ReceiverRequest;
        this.StatusRequest = StatusRequest;
    }


}
