package com.java.traning.restaurant.service;

import com.java.traning.restaurant.dao.DiscountDao;
import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.Discount;

import java.math.BigDecimal;
import java.util.Optional;

public class DiscountService {
  private final DiscountDao discountDao;

  public DiscountService(DiscountDao discountDao) {
    this.discountDao = discountDao;
  }

  public Optional<Discount> findByCode(String code) throws RestaurantException {
    if (code == null || code.isBlank()) return Optional.empty();
    return discountDao.findByCode(code.trim());
  }

  public boolean isApplicable(Discount discount, BigDecimal subtotal) {
    if (discount == null) return false;
    if (!discount.isActive()) return false;
    if (discount.getMinSubtotal() != null && subtotal.compareTo(discount.getMinSubtotal()) < 0) return false;
    return true;
  }

  public BigDecimal computeDiscountAmount(Discount discount, BigDecimal subtotal) {
    if (discount == null) return BigDecimal.ZERO;
    BigDecimal value = discount.getValue() == null ? BigDecimal.ZERO : discount.getValue();
    switch (discount.getType()) {
      case FLAT:
        return value.min(subtotal);
      case PERCENT:
        return subtotal.multiply(value).divide(BigDecimal.valueOf(100));
      default:
        return BigDecimal.ZERO;
    }
  }
}
