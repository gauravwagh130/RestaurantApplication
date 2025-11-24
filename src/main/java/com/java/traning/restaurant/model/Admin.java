package com.java.traning.restaurant.model;

import java.util.Objects;

public class Admin {
  private int id;
  private String username;
  private String passwordHash;
  private String displayName;

  public Admin(){};

  public Admin(int id, String username, String passwordHash, String displayName) {
    this.id = id;
    this.username = username;
    this.passwordHash = passwordHash;
    this.displayName = displayName;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Admin admin = (Admin) o;
    return id == admin.id && Objects.equals(username, admin.username) && Objects.equals(passwordHash, admin.passwordHash) && Objects.equals(displayName, admin.displayName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, passwordHash, displayName);
  }

  @Override
  public String toString() {
    return "Admin{" +
      "id=" + id +
      ", username='" + username + '\'' +
      ", passwordHash='" + passwordHash + '\'' +
      ", displayName='" + displayName + '\'' +
      '}';
  }
}
