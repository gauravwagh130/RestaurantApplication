package com.java.traning.restaurant.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Discount {
  public enum Type { PERCENT, FLAT }

  private String code;
  private Type type;
  private BigDecimal value;
  private BigDecimal minSubtotal;
  private boolean active;
  private String description;

  public Discount() {}

  public Discount(String code, Type type, BigDecimal value, BigDecimal minSubtotal, boolean active, String description) {
    this.code = code;
    this.type = type;
    this.value = value;
    this.minSubtotal = minSubtotal;
    this.active = active;
    this.description = description;
  }

  // getters & setters
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }

  public Type getType() { return type; }
  public void setType(Type type) { this.type = type; }

  public BigDecimal getValue() { return value; }
  public void setValue(BigDecimal value) { this.value = value; }

  public BigDecimal getMinSubtotal() { return minSubtotal; }
  public void setMinSubtotal(BigDecimal minSubtotal) { this.minSubtotal = minSubtotal; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Discount)) return false;
    Discount discount = (Discount) o;
    return Objects.equals(code, discount.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

  @Override
  public String toString() {
    return "Discount{" +
      "code='" + code + '\'' +
      ", type=" + type +
      ", value=" + value +
      ", minSubtotal=" + minSubtotal +
      ", active=" + active +
      '}';
  }
}
