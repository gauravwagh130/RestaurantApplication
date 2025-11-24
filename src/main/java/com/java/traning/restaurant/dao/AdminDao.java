package com.java.traning.restaurant.dao;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.Admin;

import java.sql.Connection;
import java.util.Optional;

public interface AdminDao {

  Optional<Admin> findByUsername(String username) throws RestaurantException;
  Optional<Admin> findByUsername(Connection conn, String username) throws RestaurantException;
}
