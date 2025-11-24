package com.java.traning.restaurant.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
  private int id;
  private Timestamp createdAt;
  private List<OrderItem> items = new ArrayList<>();
  private BigDecimal subtotal = BigDecimal.ZERO;
  private BigDecimal discountAmount = BigDecimal.ZERO;
  private BigDecimal taxAmount = BigDecimal.ZERO;
  private BigDecimal total = BigDecimal.ZERO;
  private String status; // CREATED, CONFIRMED, CANCELLED

  public Order() {}

  public Order(List<OrderItem> items) {
    this.items = items != null ? items : new ArrayList<>();
    computeSubtotal();
  }

  public void computeSubtotal() {
    subtotal = items.stream()
      .map(OrderItem::getLineTotal)
      .filter(x -> x != null)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public void computeTotal() {
    if (subtotal == null) computeSubtotal();
    BigDecimal afterDiscount = subtotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    if (afterDiscount.compareTo(BigDecimal.ZERO) < 0) afterDiscount = BigDecimal.ZERO;
    total = afterDiscount.add(taxAmount != null ? taxAmount : BigDecimal.ZERO);
  }

  // getters & setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public Timestamp getCreatedAt() { return createdAt; }
  public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; computeSubtotal(); }

  public BigDecimal getSubtotal() { return subtotal; }
  public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

  public BigDecimal getDiscountAmount() { return discountAmount; }
  public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

  public BigDecimal getTaxAmount() { return taxAmount; }
  public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

  public BigDecimal getTotal() { return total; }
  public void setTotal(BigDecimal total) { this.total = total; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  // convenience
  public void addItem(OrderItem item) {
    this.items.add(item);
    computeSubtotal();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order)) return false;
    Order order = (Order) o;
    return id == order.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Order{id=" + id + ", subtotal=" + subtotal + ", total=" + total + ", status=" + status + "}";
  }

}
