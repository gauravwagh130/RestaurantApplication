package com.java.traning.restaurant.dao;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.Discount;

import java.sql.Connection;
import java.util.Optional;

public interface DiscountDao {
  Optional<Discount> findByCode(String code) throws RestaurantException;
  Optional<Discount> findByCode(Connection conn, String code) throws RestaurantException;
  void upsertDiscount(Discount discount) throws RestaurantException;
  void upsertDiscount(Connection conn, Discount discount) throws RestaurantException;
}
