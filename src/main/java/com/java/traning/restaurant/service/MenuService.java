package com.java.traning.restaurant.service;

import com.java.traning.restaurant.dao.MenuItemDao;
import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.MenuItem;

import java.util.List;
import java.util.Optional;

public class MenuService {
  private final MenuItemDao menuItemDao;

  public MenuService(MenuItemDao menuItemDao) {
    this.menuItemDao = menuItemDao;
  }

  public List<MenuItem> getAllAvailable() throws RestaurantException {
    return menuItemDao.findAllAvailable();
  }

  public Optional<MenuItem> getById(int id) throws RestaurantException {
    return menuItemDao.findById(id);
  }
}
