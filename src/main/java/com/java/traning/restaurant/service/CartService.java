package com.java.traning.restaurant.service;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.CartItem;
import com.java.traning.restaurant.model.MenuItem;
import com.java.traning.restaurant.model.OrderItem;

import java.math.BigDecimal;
import java.util.*;

public class CartService {
  private final Map<Integer, CartItem> items = new HashMap<>();

  public synchronized void addItem(MenuItem item, int qty) throws RestaurantException {
    if (item == null) throw new RestaurantException("MenuItem is null");
    if (qty <= 0) throw new RestaurantException("Quantity must be positive");
    if (!item.isAvailable()) throw new RestaurantException("Item is not available: " + item.getName());

    // Check against stock for the requested addition
    CartItem existing = items.get(item.getId());
    int existingQty = (existing == null) ? 0 : existing.getQuantity();
    int newQty = existingQty + qty;

    if (newQty > item.getStock()) {
      if (existingQty == 0) {
        throw new RestaurantException("Only " + item.getStock() + " units available. You entered " + qty + ".");
      } else {
        throw new RestaurantException("Only " + item.getStock() + " units available. Your cart already has "
          + existingQty + ", you can add at most " + (item.getStock() - existingQty) + " more.");
      }
    }

    if (existing == null) {
      existing = new CartItem(item, qty);
      items.put(item.getId(), existing);
    } else {
      existing.setQuantity(newQty);
    }
  }

  public synchronized void updateItemQuantity(int menuItemId, int qty) throws RestaurantException {
    if (qty < 0) throw new RestaurantException("Quantity cannot be negative");
    CartItem existing = items.get(menuItemId);
    if (existing == null) throw new RestaurantException("Item not in cart");
    if (qty == 0) {
      items.remove(menuItemId);
      return;
    }

    // Validate against stock of the MenuItem snapshot stored in CartItem
    MenuItem menuItem = existing.getMenuItem();
    if (qty > menuItem.getStock()) {
      throw new RestaurantException("Cannot set quantity higher than available stock (" + menuItem.getStock() + ").");
    }

    existing.setQuantity(qty);
  }

  public synchronized void removeItem(int menuItemId) {
    items.remove(menuItemId);
  }

  public synchronized List<CartItem> listItems() {
    return new ArrayList<>(items.values());
  }

  public synchronized BigDecimal getSubtotal() {
    BigDecimal sum = BigDecimal.ZERO;
    for (CartItem c : items.values()) {
      if (c.getLineTotal() == null) c.computeLineTotal();
      sum = sum.add(c.getLineTotal());
    }
    return sum;
  }

  public synchronized boolean isEmpty() {
    return items.isEmpty();
  }

  public synchronized void clear() {
    items.clear();
  }

  public synchronized List<OrderItem> toOrderItems() {
    List<OrderItem> list = new ArrayList<>();
    for (CartItem c : items.values()) {
      OrderItem oi = new OrderItem(c.getMenuItem().getId(), c.getMenuItem().getName(), c.getUnitPrice(), c.getQuantity());
      oi.computeLineTotal();
      list.add(oi);
    }
    return list;
  }
}
