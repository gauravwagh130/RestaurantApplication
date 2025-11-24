package com.java.traning.restaurant.model;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {
  private int id;
  private int orderId;
  private int menuItemId;
  private String menuItemName; // useful for receipts
  private BigDecimal unitPrice;
  private int quantity;
  private BigDecimal lineTotal;

  public OrderItem() {}

  public OrderItem(int menuItemId, String menuItemName, BigDecimal unitPrice, int quantity) {
    this.menuItemId = menuItemId;
    this.menuItemName = menuItemName;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
    computeLineTotal();
  }

  public void computeLineTotal() {
    if (unitPrice == null) unitPrice = BigDecimal.ZERO;
    this.lineTotal = unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));
  }

  // getters & setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public int getOrderId() { return orderId; }
  public void setOrderId(int orderId) { this.orderId = orderId; }

  public int getMenuItemId() { return menuItemId; }
  public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }

  public String getMenuItemName() { return menuItemName; }
  public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }

  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; computeLineTotal(); }

  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; computeLineTotal(); }

  public BigDecimal getLineTotal() { return lineTotal; }
  public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrderItem)) return false;
    OrderItem orderItem = (OrderItem) o;
    return id == orderItem.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "OrderItem{" +
      "menuItemName='" + menuItemName + '\'' +
      ", qty=" + quantity +
      ", lineTotal=" + lineTotal +
      '}';
  }
}
