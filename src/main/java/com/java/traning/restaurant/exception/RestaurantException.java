package com.java.traning.restaurant.exception;

/**
 * Simple custom exception for the restaurant app.
 */
public class RestaurantException extends Exception {

  public RestaurantException() {
    super();
  }

  public RestaurantException(String message) {
    super(message);
  }

  public RestaurantException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestaurantException(Throwable cause) {
    super(cause);
  }
}
