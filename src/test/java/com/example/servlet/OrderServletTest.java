package com.example.servlet;

import com.example.model.Order;
import com.example.model.Product;
import com.example.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServletTest {


    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OrderService orderService;

     OrderServlet orderServlet;

       private ObjectMapper objectMapper;
    private StringWriter stringWriter;
    private PrintWriter printWriter;


    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);


        var field = OrderServlet.class.getDeclaredField("orderService");
        field.setAccessible(true);
        field.set(orderServlet, orderService);

        var objectMapperField = OrderServlet.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(orderServlet, objectMapper);
    }


    @Test
    void testDoPost_CreateOrder_Success() throws Exception {
        Product product = new Product(1L, "Laptop", 1500.0);
        Order order = new Order(null, null, Arrays.asList(product));
        Order createdOrder = new Order(1L, new Date(), Arrays.asList(product));
        createdOrder.setCost(1500.0);

        String orderJson = objectMapper.writeValueAsString(order);

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(orderJson)));
        when(response.getWriter()).thenReturn(printWriter);
        when(orderService.createOrder(any(Order.class))).thenReturn(createdOrder);

        orderServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).setContentType("application/json");

        printWriter.flush();
        String responseJson = stringWriter.toString();
        Order responseOrder = objectMapper.readValue(responseJson, Order.class);

        assertEquals(1L, responseOrder.getId());
        assertEquals(1500.0, responseOrder.getCost());
        assertEquals(1, responseOrder.getProduct().size());
    }


    @Test
    void testDoPost_CreateOrder_BadRequest() throws Exception {
        String invalidJson = "{invalid json}";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));
        when(response.getWriter()).thenReturn(printWriter);

        orderServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json");
    }

    @Test
    void testDoGet_GetAllOrders_Success() throws Exception {
        Product product1 = new Product(1L, "Laptop", 1500.0);
        Product product2 = new Product(2L, "Mouse", 25.0);
        Order order1 = new Order(1L, new Date(), Arrays.asList(product1));
        Order order2 = new Order(2L, new Date(), Arrays.asList(product2));
        List<Order> orders = Arrays.asList(order1, order2);

        when(request.getPathInfo()).thenReturn(null);
        when(response.getWriter()).thenReturn(printWriter);
        when(orderService.getAllOrders()).thenReturn(orders);

        orderServlet.doGet(request, response);

        verify(response).setContentType("application/json");

        printWriter.flush();
        String responseJson = stringWriter.toString();
        Order[] responseOrders = objectMapper.readValue(responseJson, Order[].class);

        assertEquals(2, responseOrders.length);
    }


    @Test
    void testDoGet_GetOrderById_Success() throws Exception {
        Long orderId = 1L;
        Product product = new Product(1L, "Phone", 800.0);
        Order order = new Order(orderId, new Date(), Arrays.asList(product));
        order.setCost(800.0);

        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(response.getWriter()).thenReturn(printWriter);
        when(orderService.getOrderById(orderId)).thenReturn(order);

        orderServlet.doGet(request, response);

        verify(response).setContentType("application/json");

        printWriter.flush();
        String responseJson = stringWriter.toString();
        Order responseOrder = objectMapper.readValue(responseJson, Order.class);

        assertEquals(orderId, responseOrder.getId());
        assertEquals(800.0, responseOrder.getCost());
    }

    @Test
    void testDoGet_GetOrderById_NotFound() throws Exception {
        Long orderId = 999L;

        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(response.getWriter()).thenReturn(printWriter);
        when(orderService.getOrderById(orderId)).thenReturn(null);

        orderServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(response).setContentType("application/json");

        printWriter.flush();
        String responseJson = stringWriter.toString();
        assertTrue(responseJson.contains("Order not found"));
    }

    @Test
    void testDoGet_GetOrderById_InvalidIdFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");
        when(response.getWriter()).thenReturn(printWriter);

        orderServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json");
    }

    @Test
    void testDoPut_UpdateOrder_Success() throws Exception {
        Long orderId = 1L;
        Product product = new Product(1L, "Tablet", 500.0);
        Order updatedOrder = new Order(null, null, Arrays.asList(product));
        Order resultOrder = new Order(orderId, new Date(), Arrays.asList(product));
        resultOrder.setCost(500.0);

        String orderJson = objectMapper.writeValueAsString(updatedOrder);

        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(orderJson)));
        when(response.getWriter()).thenReturn(printWriter);
        when(orderService.updateOrder(eq(orderId), any(Order.class))).thenReturn(resultOrder);

        orderServlet.doPut(request, response);

        verify(response).setContentType("application/json");

        printWriter.flush();
        String responseJson = stringWriter.toString();
        Order responseOrder = objectMapper.readValue(responseJson, Order.class);

        assertEquals(orderId, responseOrder.getId());
        assertEquals(500.0, responseOrder.getCost());
    }


    @Test
    void testDoPut_UpdateOrder_NotFound() throws Exception {
        Long orderId = 999L;
        Product product = new Product(1L, "Tablet", 500.0);
        Order updatedOrder = new Order(null, null, Arrays.asList(product));

        String orderJson = objectMapper.writeValueAsString(updatedOrder);

        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(orderJson)));
        when(response.getWriter()).thenReturn(printWriter);
        when(orderService.updateOrder(eq(orderId), any(Order.class))).thenReturn(null);

        orderServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(response).setContentType("application/json");
    }

    @Test
    void testDoDelete_DeleteOrder_Success() throws Exception {
        Long orderId = 1L;

        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(orderService.deleteOrder(orderId)).thenReturn(true);

        orderServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDelete_DeleteOrder_NotFound() throws Exception {
        Long orderId = 999L;

        when(request.getPathInfo()).thenReturn("/" + orderId);
        when(response.getWriter()).thenReturn(printWriter);
        when(orderService.deleteOrder(orderId)).thenReturn(false);

        orderServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(response).setContentType("application/json");

        printWriter.flush();
        String responseJson = stringWriter.toString();
        assertTrue(responseJson.contains("Order not found"));
    }

    @Test
    void testDoDelete_DeleteOrder_NoId() throws Exception {
        when(request.getPathInfo()).thenReturn(null);
        when(response.getWriter()).thenReturn(printWriter);

        orderServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json");
    }
}