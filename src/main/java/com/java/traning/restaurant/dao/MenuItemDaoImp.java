package com.java.traning.restaurant.dao;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.util.ConnectionHelper;
import com.java.traning.restaurant.model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuItemDaoImp implements MenuItemDao {
  private static final String SELECT_AVAILABLE = "SELECT id, name, description, price, stock, available, created_at FROM menu_items WHERE available = TRUE";
  private static final String SELECT_BY_ID = "SELECT id, name, description, price, stock, available, created_at FROM menu_items WHERE id = ?";
  private static final String REDUCE_STOCK_SQL = "UPDATE menu_items SET stock = stock - ? WHERE id = ? AND stock >= ?";
  private static final String INCREASE_STOCK_SQL = "UPDATE menu_items SET stock = stock + ? WHERE id = ?";
  private static final String INSERT_SQL =
    "INSERT INTO menu_items(name, description, price, stock, available) VALUES (?,?,?,?,?)";

  @Override
  public List<MenuItem> findAllAvailable() throws RestaurantException{
    try (Connection conn = ConnectionHelper.getConnection()) {
      return findAllAvailable(conn);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error obtaining DB connection", e);
    }
  }

  @Override
  public List<MenuItem> findAllAvailable(Connection conn) throws RestaurantException {
    List<MenuItem> list = new ArrayList<>();
    try (PreparedStatement ps = conn.prepareStatement(SELECT_AVAILABLE);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()){
        list.add(mapRowToMenuItem(rs));
      }
      return list;
    } catch (SQLException e) {
      throw new RestaurantException("Error fetching available menu items", e);
    }
  }

  @Override
  public Optional<MenuItem> findById(int id) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      return findById(conn, id);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error obtaining DB connection", e);
    }
  }

  @Override
  public Optional<MenuItem> findById(Connection conn, int id) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapRowToMenuItem(rs));
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RestaurantException("Error fetching MenuItem by id", e);
    }
  }

  @Override
  public boolean reduceStock(int id, int qty) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      return reduceStock(conn, id, qty);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error obtaining DB connection", e);
    }
  }

  @Override
  public boolean reduceStock(Connection conn, int id, int qty) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(REDUCE_STOCK_SQL)) {
      ps.setInt(1, qty);
      ps.setInt(2, id);
      ps.setInt(3, qty);
      int affected = ps.executeUpdate();
      return affected == 1;
    } catch (SQLException e) {
      throw new RestaurantException("Error reducing stock", e);
    }
  }

  @Override
  public boolean increaseStock(int id, int qty) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      return increaseStock(conn, id, qty);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error obtaining DB connection", e);
    }
  }

  @Override
  public boolean increaseStock(Connection conn, int id, int qty) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(INCREASE_STOCK_SQL)) {
      ps.setInt(1, qty);
      ps.setInt(2, id);
      int affected = ps.executeUpdate();
      return affected == 1;
    } catch (SQLException e) {
      throw new RestaurantException("Error increasing stock", e);
    }
  }

  @Override
  public int insert(MenuItem item) throws RestaurantException {
    try (Connection conn = ConnectionHelper.getConnection()) {
      return insert(conn, item);
    } catch (SQLException | ClassNotFoundException e) {
      throw new RestaurantException("Error inserting menu item", e);
    }
  }

  @Override
  public int insert(Connection conn, MenuItem item) throws RestaurantException {
    try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, item.getName());
      ps.setString(2, item.getDescription());
      ps.setBigDecimal(3, item.getPrice());
      ps.setInt(4, item.getStock());
      ps.setBoolean(5, item.isAvailable());

      int affected = ps.executeUpdate();
      if (affected == 0) return -1;

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        } else {
          return -1;
        }
      }
    } catch (SQLException e) {
      throw new RestaurantException("Error inserting menu item", e);
    }
  }

  private MenuItem mapRowToMenuItem(ResultSet rs) throws SQLException {
    MenuItem m = new MenuItem();
    m.setId(rs.getInt("id"));
    m.setName(rs.getString("name"));
    m.setDescription(rs.getString("description"));
    m.setPrice(rs.getBigDecimal("price"));
    m.setStock(rs.getInt("stock"));
    m.setAvailable(rs.getBoolean("available"));
    Timestamp ts = rs.getTimestamp("created_at");
    m.setCreatedAt(ts);
    return m;
  }
}
