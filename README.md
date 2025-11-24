# Restaurant Console Application

A simple console-based restaurant system built in Java (JDK 11+), using JDBC for persistence. Features:

* View menu items
* Add items to cart (with stock validation)
* View / edit cart
* Checkout (transactionally reduces stock, creates order and order items)
* Apply discount codes
* Admin area (login with username/password) to increase/decrease stock and add menu items

---

## Screenshot
<img width="673" height="609" alt="image" src="https://github.com/user-attachments/assets/b6ab98d1-45db-4727-9ec6-45772e8a85f8" />
<img width="709" height="685" alt="image" src="https://github.com/user-attachments/assets/36ab989a-07e1-4f61-b0df-c01b5e1f47ce" />



---

## Quick start

### Prerequisites

* Java JDK 11 or newer (tested with JDK 21)
* Maven (or Gradle) to build the project
* MySQL (or compatible RDBMS) with a database for the app
* JDBC driver on classpath (e.g. `mysql-connector-java`)

### Build

```bash
mvn clean package
```

### Run (from IDE or command line)

Make sure you have configured `ConnectionHelper` with your DB URL, username and password. Example `ConnectionHelper` uses `DriverManager.getConnection(url, user, pass)`.

Run the app:

```bash
java -jar target/your-artifact.jar
# or run RestaurantApplication.main() from your IDE
```

---

## Default Admin (for testing)

> Create an `admins` table and insert a test admin. This is a minimal example; **do not store plaintext passwords in production**.

```sql
CREATE TABLE admins (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(200),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO admins (username, password_hash, display_name) VALUES ('admin','admin123','Super Admin');
```

Login via the app: choose **6. Admin login** and provide `admin` / `admin123`.

---

## Database schema (essential tables)

*menu_items*

```sql
CREATE TABLE menu_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  available BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

*discounts* (simple structure)

```sql
CREATE TABLE discounts (
  code VARCHAR(64) PRIMARY KEY,
  type VARCHAR(10) NOT NULL, -- 'PERCENT' or 'FLAT'
  value DECIMAL(10,2) NOT NULL,
  min_subtotal DECIMAL(10,2),
  active BOOLEAN DEFAULT TRUE,
  description TEXT
);
```

*orders* and *order_items*

```sql
CREATE TABLE orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  subtotal DECIMAL(10,2),
  discount_amount DECIMAL(10,2),
  tax_amount DECIMAL(10,2),
  total DECIMAL(10,2),
  status VARCHAR(50)
);

CREATE TABLE order_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  menu_item_id INT NOT NULL,
  unit_price DECIMAL(10,2),
  quantity INT,
  line_total DECIMAL(10,2),
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);
```

---

## Important files / packages

* `com.java.traning.restaurant.app.RestaurantApplication` — main application entry (console UI loop)
* `com.java.traning.restaurant.controller` — controllers for menu, cart, checkout, admin
* `com.java.traning.restaurant.service` — service layer (business rules, billing, cart)
* `com.java.traning.restaurant.dao` — DAO interfaces and JDBC implementations
* `com.java.traning.restaurant.model` — domain models (`MenuItem`, `CartItem`, `Order`, `OrderItem`, `Discount`, `Admin`)
* `com.java.traning.restaurant.util.ConnectionHelper` — obtains JDBC `Connection`
* `com.java.traning.restaurant.exception.RestaurantException` — custom app exception

---

## How admin functionality is implemented (options)

This project includes a console-based admin flow (login with username/password) that allows:

* Increase/decrease stock by menu item id
* Add new menu items (requires `MenuItemDao.insert()` implementation)

Alternative approaches (later enhancements):

* Build a web admin panel (React + backend endpoints)
* Expose admin REST APIs secured with JWT or HTTP Basic over HTTPS
* Integrate SSO/LDAP for enterprise usage

---

## Security notes

* Do **not** store plaintext passwords in production. Use BCrypt/Argon2 for hashing and verify with secure libraries.
* Protect database credentials and use environment variables or config files outside version control.
* If you expose admin APIs, always require HTTPS and implement proper authentication and authorization.

---

## Testing

* Unit test billing calculations (percent vs flat discounts, rounding) — `BillingService`
* Integration test checkout flow against a test DB (start with known stock values and assert rollback on failure)

---

## Troubleshooting

* If you see `Optional.isEmpty()` warnings, ensure project language level is Java 11+.
* If JDBC `ClassNotFoundException` occurs, add the JDBC driver dependency (MySQL connector) to your `pom.xml`.
