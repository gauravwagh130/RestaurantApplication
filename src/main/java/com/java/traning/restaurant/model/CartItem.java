package com.java.traning.restaurant.model;

import java.math.BigDecimal;
import java.util.Objects;

public class CartItem {
  private MenuItem menuItem;
  private int quantity;
  private BigDecimal unitPrice;   // copy of menuItem.price at time of adding
  private BigDecimal lineTotal;   // unitPrice * quantity

  public CartItem() {}

  public CartItem(MenuItem menuItem, int quantity) {
    this.menuItem = menuItem;
    this.quantity = quantity;
    this.unitPrice = menuItem != null ? menuItem.getPrice() : BigDecimal.ZERO;
    computeLineTotal();
  }

  public void computeLineTotal() {
    if (unitPrice == null) unitPrice = BigDecimal.ZERO;
    this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
  }

  // getters & setters
  public MenuItem getMenuItem() { return menuItem; }
  public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) {
    this.quantity = quantity;
    computeLineTotal();
  }

  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; computeLineTotal(); }

  public BigDecimal getLineTotal() { return lineTotal; }
  public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CartItem)) return false;
    CartItem cartItem = (CartItem) o;
    // equality by menuItem id (if menuItem null, fallback to object equality)
    return menuItem != null && cartItem.menuItem != null && menuItem.getId() == cartItem.menuItem.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(menuItem != null ? menuItem.getId() : 0);
  }

  @Override
  public String toString() {
    return "CartItem{" +
      "menuItem=" + (menuItem != null ? menuItem.getName() : "null") +
      ", qty=" + quantity +
      ", unitPrice=" + unitPrice +
      ", lineTotal=" + lineTotal +
      '}';
  }
}
