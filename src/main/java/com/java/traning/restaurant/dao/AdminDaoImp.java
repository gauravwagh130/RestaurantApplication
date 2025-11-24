package com.java.traning.restaurant.dao;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.Admin;
import com.java.traning.restaurant.util.ConnectionHelper;

import java.sql.*;
import java.util.Optional;

public class AdminDaoImp implements AdminDao {
  private static final String SELECT_BY_USERNAME = "SELECT id, username, password_hash, display_name FROM admins WHERE username = ?";

  @Override
  public Optional<Admin> findByUsername(String username) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      return findByUsername(conn, username);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("DB error", e);
    }
  }

  @Override
  public Optional<Admin> findByUsername(Connection conn, String username) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Admin a = new Admin();
          a.setId(rs.getInt("id"));
          a.setUsername(rs.getString("username"));
          a.setPasswordHash(rs.getString("password_hash"));
          a.setDisplayName(rs.getString("display_name"));
          return Optional.of(a);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RestaurantException("DB error", e);
    }
  }
}
