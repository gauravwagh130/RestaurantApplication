package com.java.traning.restaurant.service;

import com.java.traning.restaurant.model.CartItem;
import com.java.traning.restaurant.model.Discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BillingService {
  private final BigDecimal taxRate;
  private final int scale;
  public BillingService(BigDecimal taxRate, int scale) {
    this.taxRate = taxRate;
    this.scale = scale;
  }

  public BigDecimal computeSubtotal(List<CartItem> items) {
    BigDecimal sum = BigDecimal.ZERO;
    for (CartItem c : items) {
      if (c.getLineTotal() == null) c.computeLineTotal();
      sum = sum.add(c.getLineTotal());
    }
    return round(sum);
  }

  public BigDecimal computeTax(BigDecimal taxableAmount) {
    if (taxableAmount == null) return BigDecimal.ZERO;
    return round(taxableAmount.multiply(taxRate));
  }

  public OrderSummary calculateTotals(List<CartItem> items, Discount discount) {
    BigDecimal subtotal = computeSubtotal(items);
    BigDecimal discountAmount = BigDecimal.ZERO;
    if (discount != null) {
      if (discount.getType() == Discount.Type.PERCENT) {
        discountAmount = subtotal.multiply(discount.getValue()).divide(BigDecimal.valueOf(100));
      } else {
        discountAmount = discount.getValue();
      }
      if (discountAmount.compareTo(subtotal) > 0) discountAmount = subtotal;
      discountAmount = round(discountAmount);
    }
    BigDecimal taxable = subtotal.subtract(discountAmount);
    if (taxable.compareTo(BigDecimal.ZERO) < 0) taxable = BigDecimal.ZERO;
    BigDecimal tax = computeTax(taxable);
    BigDecimal total = round(taxable.add(tax));
    return new OrderSummary(subtotal, discountAmount, tax, total);
  }

  private BigDecimal round(BigDecimal v) {
    if (v == null) return BigDecimal.ZERO;
    return v.setScale(scale, RoundingMode.HALF_UP);
  }

  public static class OrderSummary {
    public final BigDecimal subtotal;
    public final BigDecimal discountAmount;
    public final BigDecimal taxAmount;
    public final BigDecimal total;

    public OrderSummary(BigDecimal subtotal, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal total) {
      this.subtotal = subtotal;
      this.discountAmount = discountAmount;
      this.taxAmount = taxAmount;
      this.total = total;
    }
  }
}
