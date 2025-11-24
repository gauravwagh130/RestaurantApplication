package com.java.traning.restaurant.controller;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.MenuItem;
import com.java.traning.restaurant.service.CartService;
import com.java.traning.restaurant.service.MenuService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuController {
  private final MenuService menuService;
  private final CartService cartService;

  public MenuController(MenuService menuService, CartService cartService) {
    this.menuService = menuService;
    this.cartService = cartService;
  }

  public void showMenu() {
    try {
      List<MenuItem> items = menuService.getAllAvailable();
      System.out.println("\n--- MENU ---");
      for (MenuItem m : items) {
        System.out.printf("%d) %s — ₹%s — stock: %d\n", m.getId(), m.getName(), m.getPrice(), m.getStock());
      }
      if (items.isEmpty()) System.out.println("Menu is empty.");
    } catch (RestaurantException e) {
      System.out.println("Could not load menu: " + e.getMessage());
    }
  }

  // interactive flow to add item to cart
  public void addItemFlow(Scanner sc) {
    try {
      showMenu();
      System.out.print("Enter menu item id to add: ");
      String idStr = sc.nextLine().trim();
      int id = Integer.parseInt(idStr);
      Optional<MenuItem> opt = menuService.getById(id);
      if (!opt.isPresent()) {
        System.out.println("Invalid item id.");
        return;
      }
      MenuItem menuItem = opt.get();

      System.out.print("Enter quantity: ");
      int qty = Integer.parseInt(sc.nextLine().trim());

      cartService.addItem(menuItem, qty);
      System.out.println("Added to cart: " + menuItem.getName() + " x " + qty);
    } catch (NumberFormatException nfe) {
      System.out.println("Please enter a valid number.");
    } catch (RestaurantException e) {
      System.out.println("Cannot add to cart: " + e.getMessage());
    }
  }
}
