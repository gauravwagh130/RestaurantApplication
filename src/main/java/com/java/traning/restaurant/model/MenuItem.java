package com.java.traning.restaurant.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

public class MenuItem {
  private int id;
  private String name;
  private String description;
  private BigDecimal price;
  private int stock;
  private boolean available;
  private Timestamp createdAt;

  public MenuItem() {}

  // convenience constructor (optional)
  public MenuItem(int id, String name, BigDecimal price, int stock, boolean available) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.stock = stock;
    this.available = available;
  }

  // getters & setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }

  public int getStock() { return stock; }
  public void setStock(int stock) { this.stock = stock; }

  // IMPORTANT: boolean getter uses 'is' by convention
  public boolean isAvailable() { return available; }
  public void setAvailable(boolean available) { this.available = available; }

  public Timestamp getCreatedAt() { return createdAt; }
  public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MenuItem)) return false;
    MenuItem menuItem = (MenuItem) o;
    return id == menuItem.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "MenuItem{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", price=" + price +
      ", stock=" + stock +
      ", available=" + available +
      '}';
  }
}
