package com.java.traning.restaurant.controller;

import com.java.traning.restaurant.exception.RestaurantException;
import com.java.traning.restaurant.model.Order;
import com.java.traning.restaurant.model.OrderItem;
import com.java.traning.restaurant.service.BillingService;
import com.java.traning.restaurant.service.CartService;
import com.java.traning.restaurant.service.DiscountService;
import com.java.traning.restaurant.service.OrderService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CheckoutController {
  private final OrderService orderService;
  private final DiscountService discountService;
  private final CartService cartService;
  private final BillingService billingService;

  public CheckoutController(OrderService orderService, DiscountService discountService,
                            CartService cartService, BillingService billingService) {
    this.orderService = orderService;
    this.discountService = discountService;
    this.cartService = cartService;
    this.billingService = billingService;
  }

  public void checkoutFlow(Scanner sc) {
    try {
      if (cartService.isEmpty()) {
        System.out.println("Cart empty. Add items before checkout.");
        return;
      }
      System.out.print("Enter coupon code (or press Enter to skip): ");
      String coupon = sc.nextLine().trim();
      if (coupon.isEmpty()) coupon = null;

      // compute preview
      Optional<com.java.traning.restaurant.model.Discount> dOpt = Optional.empty();
      if (coupon != null) {
        dOpt = discountService.findByCode(coupon);
        if (dOpt.isPresent() && !discountService.isApplicable(dOpt.get(), cartService.getSubtotal())) {
          System.out.println("Coupon not applicable for current subtotal. It will be ignored.");
          dOpt = Optional.empty();
        }
      }
      BillingService.OrderSummary summary = billingService.calculateTotals(cartService.listItems(), dOpt.orElse(null));
      System.out.println("\n--- ORDER SUMMARY ---");
      System.out.println("Subtotal: ₹" + summary.subtotal);
      System.out.println("Discount: ₹" + summary.discountAmount);
      System.out.println("Tax: ₹" + summary.taxAmount);
      System.out.println("TOTAL: ₹" + summary.total);

      System.out.print("Confirm checkout? (y/n): ");
      String confirm = sc.nextLine().trim();
      if (!"y".equalsIgnoreCase(confirm)) {
        System.out.println("Checkout cancelled.");
        return;
      }

      int orderId = orderService.checkout(cartService, coupon);
      System.out.println("Order placed successfully. Order ID: " + orderId);
    } catch (RestaurantException e) {
      System.out.println("Checkout failed: " + e.getMessage());
    }
  }
}
