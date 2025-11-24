package com.java.traning.restaurant.dao;

import com.java.traning.restaurant.dao.OrderDao;
import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.Order;
import com.java.traning.restaurant.model.OrderItem;
import com.java.traning.restaurant.util.ConnectionHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDaoImp implements OrderDao {

  private static final String INSERT_ORDER_SQL = "INSERT INTO orders (subtotal, discount_amount, tax_amount, total, status) VALUES (?, ?, ?, ?, ?)";
  private static final String INSERT_ORDER_ITEM_SQL = "INSERT INTO order_items (order_id, menu_item_id, unit_price, quantity, line_total) VALUES (?, ?, ?, ?, ?)";
  private static final String SELECT_ORDER_SQL = "SELECT id, created_at, subtotal, discount_amount, tax_amount, total, status FROM orders WHERE id = ?";
  private static final String SELECT_ORDER_ITEMS_SQL = "SELECT oi.id, oi.menu_item_id, mi.name as menu_name, oi.unit_price, oi.quantity, oi.line_total FROM order_items oi JOIN menu_items mi ON oi.menu_item_id = mi.id WHERE oi.order_id = ?";

  @Override
  public int insertOrder(Order order) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      try {
        conn.setAutoCommit(false);
        int orderId = insertOrder(conn, order);
        insertOrderItems(conn, orderId, order.getItems());
        conn.commit();
        return orderId;
      } catch (Exception e) {
        try { conn.rollback(); } catch (SQLException ex) { /* log if needed */ }
        throw new RestaurantException("Error inserting order", e);
      } finally {
        try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
      }
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error obtaining DB connection", e);
    }
  }

  @Override
  public int insertOrder(Connection conn, Order order) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(INSERT_ORDER_SQL, Statement.RETURN_GENERATED_KEYS)) {
      ps.setBigDecimal(1, order.getSubtotal());
      ps.setBigDecimal(2, order.getDiscountAmount());
      ps.setBigDecimal(3, order.getTaxAmount());
      ps.setBigDecimal(4, order.getTotal());
      ps.setString(5, order.getStatus());
      int affected = ps.executeUpdate();
      if (affected == 0) throw new RestaurantException("Inserting order failed, no rows affected");
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
          return keys.getInt(1);
        } else {
          throw new RestaurantException("Inserting order failed, no ID obtained");
        }
      }
    } catch (SQLException e) {
      throw new RestaurantException("Error inserting order", e);
    }
  }

  @Override
  public void insertOrderItems(int orderId, List<OrderItem> items) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(INSERT_ORDER_ITEM_SQL)) {
        for (OrderItem it : items) {
          ps.setInt(1, orderId);
          ps.setInt(2, it.getMenuItemId());
          ps.setBigDecimal(3, it.getUnitPrice());
          ps.setInt(4, it.getQuantity());
          ps.setBigDecimal(5, it.getLineTotal());
          ps.addBatch();
        }
        ps.executeBatch();
      }
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error inserting order items", e);
    }
  }

  @Override
  public void insertOrderItems(Connection conn, int orderId, List<OrderItem> items) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(INSERT_ORDER_ITEM_SQL)) {
      for (OrderItem it : items) {
        ps.setInt(1, orderId);
        ps.setInt(2, it.getMenuItemId());
        ps.setBigDecimal(3, it.getUnitPrice());
        ps.setInt(4, it.getQuantity());
        ps.setBigDecimal(5, it.getLineTotal());
        ps.addBatch();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RestaurantException("Error inserting order items using provided connection", e);
    }
  }

  @Override
  public Optional<Order> findById(int orderId) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      return findById(conn, orderId);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error obtaining DB connection", e);
    }
  }

  @Override
  public Optional<Order> findById(Connection conn, int orderId) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(SELECT_ORDER_SQL)) {
      ps.setInt(1, orderId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return Optional.empty();
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setSubtotal(rs.getBigDecimal("subtotal"));
        order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        order.setTaxAmount(rs.getBigDecimal("tax_amount"));
        order.setTotal(rs.getBigDecimal("total"));
        order.setStatus(rs.getString("status"));

        // load order items
        try (PreparedStatement psItems = conn.prepareStatement(SELECT_ORDER_ITEMS_SQL)) {
          psItems.setInt(1, orderId);
          try (ResultSet rsItems = psItems.executeQuery()) {
            List<OrderItem> items = new ArrayList<>();
            while (rsItems.next()) {
              OrderItem it = new OrderItem();
              it.setId(rsItems.getInt("id"));
              it.setMenuItemId(rsItems.getInt("menu_item_id"));
              it.setMenuItemName(rsItems.getString("menu_name"));
              it.setUnitPrice(rsItems.getBigDecimal("unit_price"));
              it.setQuantity(rsItems.getInt("quantity"));
              it.setLineTotal(rsItems.getBigDecimal("line_total"));
              items.add(it);
            }
            order.setItems(items);
          }
        }
        return Optional.of(order);
      }
    } catch (SQLException e) {
      throw new RestaurantException("Error fetching order by id", e);
    }
  }
}
