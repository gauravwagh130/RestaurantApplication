package com.java.traning.restaurant.controller;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.CartItem;
import com.java.traning.restaurant.model.MenuItem;
import com.java.traning.restaurant.service.CartService;
import com.java.traning.restaurant.service.MenuService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CartController {
  private final CartService cartService;
  private final MenuService menuService;

  public CartController(CartService cartService, MenuService menuService) {
    this.cartService = cartService;
    this.menuService = menuService;
  }

  public void cartMenu(Scanner sc) {
    boolean cont = true;
    while (cont) {
      showCart();
      System.out.println("a) Update quantity  b) Remove item  c) Back");
      System.out.print("Choose: ");
      String opt = sc.nextLine().trim();
      try {
        switch (opt) {
          case "a":
            System.out.print("Enter menu item id: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Enter new quantity (0 to remove): ");
            int q = Integer.parseInt(sc.nextLine().trim());
            cartService.updateItemQuantity(id, q);
            break;
          case "b":
            System.out.print("Enter menu item id to remove: ");
            int rid = Integer.parseInt(sc.nextLine().trim());
            cartService.removeItem(rid);
            break;
          case "c":
            cont = false;
            break;
          default:
            System.out.println("Invalid option.");
        }
      } catch (NumberFormatException nfe) {
        System.out.println("Please enter a valid number.");
      } catch (RestaurantException e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  public void showCart() {
    List<CartItem> items = cartService.listItems();
    System.out.println("\n--- YOUR CART ---");
    if (items.isEmpty()) {
      System.out.println("Cart is empty.");
      return;
    }
    for (CartItem c : items) {
      MenuItem m = c.getMenuItem();
      System.out.printf("%d) %s — ₹%s × %d = ₹%s\n",
        m.getId(), m.getName(), c.getUnitPrice(), c.getQuantity(), c.getLineTotal());
    }
    System.out.println("Subtotal: ₹" + cartService.getSubtotal());
  }
}
