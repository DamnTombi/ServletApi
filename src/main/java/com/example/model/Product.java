package com.example.model;

import java.io.Serializable;

public class Product implements Serializable {
    private long id;
    private String name;
    private double cost;

    public Product() {
    }

    public Product(long productId, String productName, double productPrice) {
        this.id = productId;
        this.name = productName;
        this.cost = productPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long productId) {
        this.id = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String productName) {
        this.name = productName;
    }

    public double getPrice() {
        return cost;
    }

    public void setPrice(double productPrice) {
        this.cost = productPrice;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", productName=" + name + ", productPrice=" + cost + '}';
    }


}
