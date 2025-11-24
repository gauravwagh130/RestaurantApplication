package com.java.traning.restaurant.dao;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.Order;
import com.java.traning.restaurant.model.OrderItem;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface OrderDao {
  int insertOrder(Order order) throws RestaurantException;
  int insertOrder(Connection conn, Order order) throws RestaurantException;
  void insertOrderItems(int orderId, List<OrderItem> items) throws RestaurantException;
  void insertOrderItems(Connection conn, int orderId, List<OrderItem> items) throws RestaurantException;
  Optional<Order> findById(int orderId) throws RestaurantException;
  Optional<Order> findById(Connection conn, int orderId) throws RestaurantException;
}


