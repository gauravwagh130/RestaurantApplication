package com.java.traning.restaurant.dao;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.MenuItem;

import java.util.Optional;

import java.sql.Connection;
import java.util.List;

public interface MenuItemDao {
  List<MenuItem> findAllAvailable() throws RestaurantException;;

  List<MenuItem> findAllAvailable(Connection conn) throws RestaurantException;
  Optional<MenuItem> findById(int id) throws RestaurantException;
  Optional<MenuItem> findById(Connection conn, int id) throws RestaurantException;
  boolean reduceStock(int id, int qty) throws RestaurantException;
  boolean reduceStock(Connection conn, int id, int qty) throws RestaurantException;
  boolean increaseStock(int id, int qty) throws RestaurantException;
  boolean increaseStock(Connection conn, int id, int qty) throws RestaurantException;
  int insert(MenuItem item) throws RestaurantException;
  int insert(Connection conn, MenuItem item) throws RestaurantException;

}
