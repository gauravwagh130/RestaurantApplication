package com.java.traning.restaurant.app;

import com.java.traning.restaurant.controller.AdminController;
import com.java.traning.restaurant.dao.AdminDao;
import com.java.traning.restaurant.dao.AdminDaoImp;
import com.java.traning.restaurant.model.Admin;
import com.java.traning.restaurant.service.AdminService;

import com.java.traning.restaurant.controller.CartController;
import com.java.traning.restaurant.controller.CheckoutController;
import com.java.traning.restaurant.controller.MenuController;
import com.java.traning.restaurant.dao.MenuItemDaoImp;
import com.java.traning.restaurant.dao.OrderDaoImp;

import com.java.traning.restaurant.dao.DiscountDaoImp;
import com.java.traning.restaurant.service.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

public class RestaurantApplication {

  public static void main(String[] args) {

    MenuItemDaoImp menuItemDao = new MenuItemDaoImp();
    OrderDaoImp orderDao = new OrderDaoImp();
    DiscountDaoImp discountDao = new DiscountDaoImp();
    MenuService menuService = new MenuService(menuItemDao);
    CartService cartService = new CartService();
    DiscountService discountService = new DiscountService(discountDao);
    BillingService billingService = new BillingService(new BigDecimal("0.05"), 2);
    OrderService orderService = new OrderService(menuItemDao, orderDao, discountDao, discountService, billingService);
    AdminDao adminDao = new AdminDaoImp();
    AdminService adminService = new AdminService(adminDao);
    AdminController adminController = new AdminController(adminService, menuItemDao);
    MenuController menuController = new MenuController(menuService, cartService);
    CartController cartController = new CartController(cartService, menuService);
    CheckoutController checkoutController = new CheckoutController(orderService, discountService, cartService, billingService);

    Scanner sc = new Scanner(System.in);
    boolean running = true;

    while (running) {
      System.out.println("\n===== RESTAURANT =====");
      System.out.println("1. View menu");
      System.out.println("2. Add item to cart");
      System.out.println("3. View / Edit cart");
      System.out.println("4. Checkout");
      System.out.println("5. Exit");
      System.out.println("6. Admin login");
      System.out.print("Choose: ");
      String choice = sc.nextLine().trim();

      try {
        switch (choice) {
          case "1":
            menuController.showMenu();
            break;
          case "2":
            menuController.addItemFlow(sc);
            break;
          case "3":
            cartController.cartMenu(sc);
            break;
          case "4":
            checkoutController.checkoutFlow(sc);
            break;
          case "5":
            running = false;
            break;
          case "6":
            Optional<Admin> adminOpt = adminController.loginFlow(sc);
            if (adminOpt.isPresent()) {
              adminController.adminMenu(sc, adminOpt.get());
            }
            break;
          default:
            System.out.println("Invalid choice. Try again.");
        }
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
    }

    sc.close();
    System.out.println("Goodbye!");
  }
}
