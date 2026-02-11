package com.example.service;

import com.example.model.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {
    private final Map<Long, Order> orders = new ConcurrentHashMap<Long, Order>();
    AtomicLong idCounter = new AtomicLong(1);

    public Order createOrder(Order order) {
        if (order == null) {
            throw new NullPointerException("Order can't be null");
        }
        Long id = idCounter.getAndIncrement();
        order.setId(id);
        order.setDate(new Date());
        order.setProductList(order.getProduct());
        orders.put(id, order);
        return order;
    }

    public Order getOrderById(Long id) {
        try {
            return orders.get(id);
        } catch (Exception e) {
            System.out.println("No orders by " + id + "|" + e.getMessage());
            return null;
        }
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public Order updateOrder(Long Id, Order UpdateOrder) {
        if (UpdateOrder == null) {
            throw new NullPointerException("Updated order can't be null");
        }
        if (!orders.containsKey(Id)) {
            System.out.println("No orders by " + Id + "|" + UpdateOrder);
            return null;
        }
        Order existingOrder = orders.get(Id);
        existingOrder.setProductList(UpdateOrder.getProduct());
        existingOrder.setProductList(existingOrder.getProduct());
        return existingOrder;
    }

    public boolean deleteOrder(Long id) {
        try {
            orders.remove(id);
            System.out.println("Order " + id + " has been deleted");
        } catch (Exception e) {
            System.out.println("Cant delete order by " + id + "|" + e.getMessage());
        }
        return false;
    }

    public void clearAllOrders() {
        orders.clear();
        idCounter.set(1);
    }

    public int getOrdersCount() {
        return orders.size();
    }
}
