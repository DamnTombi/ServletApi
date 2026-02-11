package com.example.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private long id;
    private Date date;
    private double cost;
    private List<Product> products;

    public Order() {
        this.products = new ArrayList<Product>();
    }

    public Order(Long orderId, Date orderDate, List<Product> productList) {
        this.id = orderId;
        this.date = orderDate;
        this.products = (products != null) ? products : new ArrayList<>();
        this.cost = calculateCost();
    }

    private double calculateCost() {
        if (products == null || products.isEmpty()) {
            return 0.0;
        }
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    public void addProduct(Product product) {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        this.products.add(product);
        this.cost = calculateCost();
    }


    public long getId() {
        return id;
    }

    public void setId(long orderId) {
        this.id = orderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date orderDate) {
        this.date = orderDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double orderCost) {
        this.cost = orderCost;
    }

    public List<Product> getProduct() {
        return products;
    }

    public void setProductList(List<Product> productList) {
        this.products = productList;
    }


    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + id +
                ", orderDate=" + date +
                ", orderCost=" + cost +
                ", productList=" + products +
                '}';
    }
}
