package com.example.servlet;

import com.example.model.Order;
import com.example.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;  // ЗМІНИТИ
import jakarta.servlet.annotation.WebServlet;  // ЗМІНИТИ
import jakarta.servlet.http.HttpServlet;  // ЗМІНИТИ
import jakarta.servlet.http.HttpServletRequest;  // ЗМІНИТИ
import jakarta.servlet.http.HttpServletResponse;  // ЗМІНИТИ

import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet("/orders/*")
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            Order order = objectMapper.readValue(body, Order.class);
            Order created = orderService.createOrder(order);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");

            objectMapper.writeValue(resp.getWriter(), created);

        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Invalid request data: " + e.getMessage() + "\"}");
        }
    }

    public void handleGetAllOrders(HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), orderService.getAllOrders());
    }

    public void handleGetOrderById(String path, HttpServletResponse resp)
            throws IOException {
        try {
            String idStr = path.substring(1);
            Long id = Long.parseLong(idStr);

            Order order = orderService.getOrderById(id);

            if (order == null) {
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), order);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"Order with id " + id + " not found\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Invalid order ID format\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            handleGetAllOrders(resp);
        } else {
            handleGetOrderById(path, resp);
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Order ID is required in the URL path\"}");
            return;
        }
        try {
            String idStr = path.substring(1);
            Long id = Long.parseLong(idStr);

            String requestBody = req.getReader().lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            Order updatedOrder = objectMapper.readValue(requestBody, Order.class);
            Order result = orderService.updateOrder(id, updatedOrder);
            if (result == null) {
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), result);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"Order with id " + id + " not found\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Invalid order ID format\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Invalid request data: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Order ID is required in the URL path\"}");
            return;
        }
        try {
            String idStr = path.substring(1);
            Long id = Long.parseLong(idStr);

            boolean deleted = orderService.deleteOrder(id);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.setContentType("application/json");
                resp.getWriter().write("{\"error\": \"Order with id " + id + " not found\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\": \"Invalid order ID format\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        resp.setHeader("Access-Control-Allow-Headers", "Content-type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
