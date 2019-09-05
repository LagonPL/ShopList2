package com.example.shoplist.shoplist.Models;

public class Item {

    private String title;
    private String amount;
    private String author;

    public Item(String title, String amount, String author) {
        this.title = title;
        this.amount = amount;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }
}
