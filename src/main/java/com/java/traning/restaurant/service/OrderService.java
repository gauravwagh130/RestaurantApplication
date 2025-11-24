package com.java.traning.restaurant.service;

import com.java.traning.restaurant.dao.DiscountDao;
import com.java.traning.restaurant.dao.MenuItemDao;
import com.java.traning.restaurant.dao.OrderDao;
import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.*;
import com.java.traning.restaurant.util.ConnectionHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderService {
  private final MenuItemDao menuItemDao;
  private final OrderDao orderDao;
  private final DiscountDao discountDao;
  private final DiscountService discountService;
  private final BillingService billingService;

  public OrderService(MenuItemDao menuItemDao, OrderDao orderDao, DiscountDao discountDao,
                      DiscountService discountService, BillingService billingService) {
    this.menuItemDao = menuItemDao;
    this.orderDao = orderDao;
    this.discountDao = discountDao;
    this.discountService = discountService;
    this.billingService = billingService;
  }

  public int checkout(CartService cart, String discountCode) throws RestaurantException {
    if (cart == null || cart.isEmpty()) throw new RestaurantException("Cart is empty. Add items before checkout.");

    Connection conn = null;
    try {
      conn = ConnectionHelper.getConnection();
      conn.setAutoCommit(false);

      List<CartItem> cartItems = cart.listItems();
      for (CartItem c : cartItems) {
        int id = c.getMenuItem().getId();
        int qty = c.getQuantity();
        Optional<MenuItem> miOpt = menuItemDao.findById(conn, id);
        if (miOpt.isEmpty() || !miOpt.get().isAvailable()) {
          conn.rollback();
          throw new RestaurantException("Item not available: id=" + id);
        }
        boolean reduced = menuItemDao.reduceStock(conn, id, qty);
        if (!reduced) {
          conn.rollback();
          throw new RestaurantException("Insufficient stock for item id=" + id);
        }
      }


      Optional<Discount> discOpt = Optional.empty();
      if (discountCode != null && !discountCode.isBlank()) {
        discOpt = discountDao.findByCode(conn, discountCode.trim());
        if (discOpt.isPresent() && !discountService.isApplicable(discOpt.get(), cart.getSubtotal())) {
          discOpt = Optional.empty(); // not applicable
        }
      }
      Discount discount = discOpt.orElse(null);


      BillingService.OrderSummary summary = billingService.calculateTotals(cart.listItems(), discount);


      Order order = new Order();
      order.setSubtotal(summary.subtotal);
      order.setDiscountAmount(summary.discountAmount);
      order.setTaxAmount(summary.taxAmount);
      order.setTotal(summary.total);
      order.setStatus("CREATED");

      int orderId = orderDao.insertOrder(conn, order);
      List<OrderItem> items = cart.toOrderItems();
      orderDao.insertOrderItems(conn, orderId, items);

      conn.commit();
      cart.clear();
      return orderId;
    } catch (SQLException | ClassNotFoundException e) {
      try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
      throw new RestaurantException("Database error during checkout", e);
    } finally {
      if (conn != null) {
        try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
      }
    }
  }

  public Optional<Order> getOrder(int orderId) throws RestaurantException {
    return orderDao.findById(orderId);
  }
}
