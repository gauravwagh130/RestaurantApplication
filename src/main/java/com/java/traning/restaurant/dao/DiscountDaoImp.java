package com.java.traning.restaurant.dao;

import com.java.traning.restaurant.dao.DiscountDao;
import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.Discount;
import com.java.traning.restaurant.util.ConnectionHelper;

import java.sql.*;
import java.util.Optional;

/**
 * JDBC implementation of DiscountDao.
 *
 * Notes:
 * - Convenience methods open and close their own connection.
 * - Transactional overloads accept a Connection supplied by the caller and do not close it.
 */
public class DiscountDaoImp implements DiscountDao {

  private static final String SELECT_BY_CODE = "SELECT code, type, value, min_subtotal, active, description FROM discounts WHERE code = ?";
  // MySQL-style upsert. If you use a different DB adapt accordingly.
  private static final String UPSERT_SQL = "INSERT INTO discounts (code, type, value, min_subtotal, active, description) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE type = VALUES(type), value = VALUES(value), min_subtotal = VALUES(min_subtotal), active = VALUES(active), description = VALUES(description)";

  @Override
  public Optional<Discount> findByCode(String code) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      return findByCode(conn, code);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error obtaining DB connection", e);
    }
  }

  @Override
  public Optional<Discount> findByCode(Connection conn, String code) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_CODE)) {
      ps.setString(1, code);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Discount d = mapRowToDiscount(rs);
          return Optional.of(d);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RestaurantException("Error fetching discount by code", e);
    }
  }

  @Override
  public void upsertDiscount(Discount discount) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      upsertDiscount(conn, discount);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error obtaining DB connection", e);
    }
  }

  @Override
  public void upsertDiscount(Connection conn, Discount discount) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(UPSERT_SQL)) {
      ps.setString(1, discount.getCode());
      ps.setString(2, discount.getType().name());
      ps.setBigDecimal(3, discount.getValue());
      ps.setBigDecimal(4, discount.getMinSubtotal());
      ps.setBoolean(5, discount.isActive());
      ps.setString(6, discount.getDescription());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RestaurantException("Error upserting discount", e);
    }
  }

  private Discount mapRowToDiscount(ResultSet rs) throws SQLException {
    Discount d = new Discount();
    d.setCode(rs.getString("code"));
    String type = rs.getString("type");
    if (type != null) {
      d.setType(Discount.Type.valueOf(type));
    }
    d.setValue(rs.getBigDecimal("value"));
    d.setMinSubtotal(rs.getBigDecimal("min_subtotal"));
    d.setActive(rs.getBoolean("active"));
    d.setDescription(rs.getString("description"));
    return d;
  }
}
