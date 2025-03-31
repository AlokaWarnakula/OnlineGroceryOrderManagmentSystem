package model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileUtil {
    // Read GroceryItem list from a file (unchanged)
    public static ArrayList<GroceryItem> readItems(String filePath) {
        ArrayList<GroceryItem> items = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create new file: " + filePath);
                }
                System.out.println("Created file: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating file " + filePath + ": " + e.getMessage());
                return items;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // Skip empty lines

                String[] parts = line.split(",", 7); // Split into 7 parts max
                if (parts.length != 7) {
                    System.err.println("Invalid line in file " + filePath + ": " + line);
                    continue;
                }
                try {
                    int productID = Integer.parseInt(parts[0].trim());
                    String productCategory = parts[1].trim();
                    String productName = parts[2].trim();
                    double productPrice = Double.parseDouble(parts[3].trim());
                    String productImageLink = parts[4].trim();
                    int quantity = Integer.parseInt(parts[5].trim());
                    String description = parts[6].trim();

                    GroceryItem item = new GroceryItem(productID, productCategory, productName, productPrice, productImageLink, quantity, description);
                    items.add(item);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line + " in file " + filePath + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
        }
        System.out.println("Read " + items.size() + " items from " + filePath);
        return items;
    }

    // Write GroceryItem list to a file (unchanged)
    public static void writeItems(String filePath, ArrayList<GroceryItem> items) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create new file: " + filePath);
                }
                System.out.println("Created file: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating file " + filePath + ": " + e.getMessage());
                throw e;
            }
        }

        // Check write permissions
        if (!file.canWrite()) {
            throw new IOException("No write permission for file: " + filePath);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (GroceryItem item : items) {
                writer.write(String.format("%d,%s,%s,%.2f,%s,%d,%s\n",
                        item.getProductID(),
                        item.getProductCategory(),
                        item.getProductName(),
                        item.getProductPrice(),
                        item.getProductImageLink(),
                        item.getQuantity(),
                        item.getDescription() != null ? item.getDescription() : ""));
            }
            writer.flush(); // Ensure data is written to the file
            System.out.println("Successfully wrote " + items.size() + " items to " + filePath + " with content: " + items);
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
            throw e; // Propagate the exception to halt execution
        }
    }

    // Check if an order number is unique in the orders file (unchanged)
    public static boolean isOrderNumberUnique(String orderNumber, String filePath) {
        Set<String> orderNumbers = new HashSet<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No orders file found at " + filePath + "; order number " + orderNumber + " is unique.");
            return true;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("orderNumber=")) {
                    orderNumbers.add(line.split("=", 2)[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading order numbers from " + filePath + ": " + e.getMessage());
            return false;
        }
        boolean isUnique = !orderNumbers.contains(orderNumber);
        System.out.println("Order number " + orderNumber + " is " + (isUnique ? "unique" : "not unique") + " in " + filePath);
        return isUnique;
    }

    // Write an order to a file (original method, unchanged)
    public static void writeOrder(String filePath, String orderNumber, String userNumber, String fullName,
                                  String phoneNumber, String address, String deliveryMethod,
                                  String paymentMethod, String deliveryDate, String confirmationDate,
                                  String paymentStatus, String deliveryStatus, String orderStatus,
                                  String deliveredDate, ArrayList<GroceryItem> cartItems,
                                  double totalPrice) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            System.out.println("Created new order file at: " + file.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("--- Order Start: " + orderNumber + " ---\n");
            writer.write("orderNumber=" + orderNumber + "\n");
            writer.write("userNumber=" + userNumber + "\n");
            writer.write("name=" + (fullName != null ? fullName : "") + "\n");
            writer.write("phoneNum=" + (phoneNumber != null ? phoneNumber : "") + "\n");
            writer.write("address=" + (address != null ? address : "") + "\n");
            writer.write("deliveryMethod=" + (deliveryMethod != null ? deliveryMethod : "") + "\n");
            writer.write("paymentMethod=" + (paymentMethod != null ? paymentMethod : "") + "\n");
            writer.write("deliveryDate=" + (deliveryDate != null ? deliveryDate : "") + "\n");
            writer.write("confirmationDate=" + (confirmationDate != null ? confirmationDate : "") + "\n");
            writer.write("paymentStatus=" + (paymentStatus != null ? paymentStatus : "") + "\n");
            writer.write("deliveryStatus=" + (deliveryStatus != null ? deliveryStatus : "") + "\n");
            writer.write("orderStatus=" + (orderStatus != null ? orderStatus : "") + "\n");
            writer.write("deliveredDate=" + (deliveredDate != null ? deliveredDate : "") + "\n");
            writer.write("[products]\n");
            for (GroceryItem item : cartItems) {
                writer.write("productID=" + item.getProductID() + ", quantity=" + item.getQuantity() + "\n");
            }
            writer.write("[total]\n");
            writer.write("totalPrice=" + String.format("%.2f", totalPrice) + "\n");
            writer.write("--- Order End ---\n");
            writer.write("\n");
            System.out.println("Wrote order " + orderNumber + " to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing order to file " + filePath + ": " + e.getMessage());
            throw e;
        }
    }

    // Overloaded method to write an Order object directly to a file (new method)
    public static void writeOrder(String filePath, Order order) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            System.out.println("Created new order file at: " + file.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("--- Order Start: " + order.getOrderNumber() + " ---\n");
            writer.write("orderNumber=" + order.getOrderNumber() + "\n");
            writer.write("userNumber=" + order.getUserNumber() + "\n");
            writer.write("name=" + (order.getFullName() != null ? order.getFullName() : "") + "\n");
            writer.write("phoneNum=" + (order.getPhoneNum() != null ? order.getPhoneNum() : "") + "\n");
            writer.write("address=" + (order.getAddress() != null ? order.getAddress() : "") + "\n");
            writer.write("deliveryMethod=" + (order.getDeliveryMethod() != null ? order.getDeliveryMethod() : "") + "\n");
            writer.write("paymentMethod=" + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "") + "\n");
            writer.write("deliveryDate=" + (order.getDeliveryDate() != null ? order.getDeliveryDate() : "") + "\n");
            writer.write("confirmationDate=" + (order.getConfirmationDate() != null ? order.getConfirmationDate() : "") + "\n");
            writer.write("paymentStatus=" + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "") + "\n");
            writer.write("deliveryStatus=" + (order.getDeliveryStatus() != null ? order.getDeliveryStatus() : "") + "\n");
            writer.write("orderStatus=" + (order.getOrderStatus() != null ? order.getOrderStatus() : "") + "\n");
            writer.write("deliveredDate=" + (order.getDeliveredDate() != null ? order.getDeliveredDate() : "") + "\n");
            writer.write("[products]\n");
            for (String[] product : order.getProducts()) {
                writer.write("productID=" + product[0] + ", quantity=" + product[1] + "\n");
            }
            writer.write("[total]\n");
            writer.write("totalPrice=" + String.format("%.2f", order.getTotalPrice()) + "\n");
            writer.write("--- Order End ---\n");
            writer.write("\n");
            System.out.println("Wrote order " + order.getOrderNumber() + " to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing order to file " + filePath + ": " + e.getMessage());
            throw e;
        }
    }

    // Read all orders from a file (unchanged)
    public static List<Order> readAllOrders(String ordersFilePath) {
        List<Order> orders = new ArrayList<>();
        File file = new File(ordersFilePath);
        if (!file.exists()) {
            System.out.println("Orders file not found: " + ordersFilePath);
            return orders;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Order order = null;
            List<String[]> products = null;
            String currentSection = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("--- Order Start:")) {
                    order = new Order();
                    products = new ArrayList<>();
                } else if (line.startsWith("--- Order End ---")) {
                    if (order != null) {
                        order.setProducts(products);
                        orders.add(order);
                    }
                    order = null;
                    products = null;
                    currentSection = null;
                } else if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                } else if (order != null) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        switch (key) {
                            case "orderNumber":
                                order.setOrderNumber(value);
                                break;
                            case "userNumber":
                                order.setUserNumber(value);
                                break;
                            case "name":
                                order.setFullName(value);
                                break;
                            case "phoneNum":
                                order.setPhoneNum(value);
                                break;
                            case "address":
                                order.setAddress(value);
                                break;
                            case "deliveryMethod":
                                order.setDeliveryMethod(value);
                                break;
                            case "paymentMethod":
                                order.setPaymentMethod(value);
                                break;
                            case "deliveryDate":
                                order.setDeliveryDate(value.isEmpty() ? null : value);
                                break;
                            case "confirmationDate":
                                order.setConfirmationDate(value);
                                break;
                            case "paymentStatus":
                                order.setPaymentStatus(value);
                                break;
                            case "deliveryStatus":
                                order.setDeliveryStatus(value);
                                break;
                            case "orderStatus":
                                order.setOrderStatus(value);
                                break;
                            case "deliveredDate":
                                order.setDeliveredDate(value);
                                break;
                            case "totalPrice":
                                if ("total".equals(currentSection)) {
                                    order.setTotalPrice(Double.parseDouble(value));
                                }
                                break;
                            case "productID":
                                if ("products".equals(currentSection)) {
                                    String[] productParts = line.split(", quantity=");
                                    if (productParts.length == 2) {
                                        products.add(new String[]{productParts[0].split("=")[1].trim(), productParts[1].trim()});
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading orders file " + ordersFilePath + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in file " + ordersFilePath + ": " + e.getMessage());
        }
        System.out.println("Read " + orders.size() + " orders from " + ordersFilePath);
        return orders;
    }

    // Read all delivered orders from deliveredOrders.txt (unchanged)
    public static List<Order> readAllDeliveredOrders(String deliveredOrdersFilePath) {
        List<Order> orders = new ArrayList<>();
        File file = new File(deliveredOrdersFilePath);
        if (!file.exists()) {
            System.out.println("Delivered orders file not found: " + deliveredOrdersFilePath);
            return orders;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Order order = null;
            List<String[]> products = null;
            String currentSection = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("--- Order Start:")) {
                    order = new Order();
                    products = new ArrayList<>();
                } else if (line.startsWith("--- Order End ---")) {
                    if (order != null) {
                        order.setProducts(products);
                        orders.add(order);
                    }
                    order = null;
                    products = null;
                    currentSection = null;
                } else if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length() - 1);
                } else if (order != null) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        switch (key) {
                            case "orderNumber":
                                order.setOrderNumber(value);
                                break;
                            case "userNumber":
                                order.setUserNumber(value);
                                break;
                            case "name":
                                order.setFullName(value);
                                break;
                            case "phoneNum":
                                order.setPhoneNum(value);
                                break;
                            case "address":
                                order.setAddress(value);
                                break;
                            case "deliveryMethod":
                                order.setDeliveryMethod(value);
                                break;
                            case "paymentMethod":
                                order.setPaymentMethod(value);
                                break;
                            case "deliveryDate":
                                order.setDeliveryDate(value.isEmpty() ? null : value);
                                break;
                            case "confirmationDate":
                                order.setConfirmationDate(value);
                                break;
                            case "paymentStatus":
                                order.setPaymentStatus(value);
                                break;
                            case "deliveryStatus":
                                order.setDeliveryStatus(value);
                                break;
                            case "orderStatus":
                                order.setOrderStatus(value);
                                break;
                            case "deliveredDate":
                                order.setDeliveredDate(value);
                                break;
                            case "totalPrice":
                                if ("total".equals(currentSection)) {
                                    order.setTotalPrice(Double.parseDouble(value));
                                }
                                break;
                            case "productID":
                                if ("products".equals(currentSection)) {
                                    String[] productParts = line.split(", quantity=");
                                    if (productParts.length == 2) {
                                        products.add(new String[]{productParts[0].split("=")[1].trim(), productParts[1].trim()});
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading delivered orders file " + deliveredOrdersFilePath + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in file " + deliveredOrdersFilePath + ": " + e.getMessage());
        }
        System.out.println("Read " + orders.size() + " delivered orders from " + deliveredOrdersFilePath);
        return orders;
    }

    // Write a delivered order to deliveredOrders.txt (unchanged)
    public static void writeDeliveredOrder(String filePath, Order order) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            System.out.println("Created new delivered orders file at: " + file.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("--- Order Start: " + order.getOrderNumber() + " ---\n");
            writer.write("orderNumber=" + order.getOrderNumber() + "\n");
            writer.write("userNumber=" + order.getUserNumber() + "\n");
            writer.write("name=" + (order.getFullName() != null ? order.getFullName() : "") + "\n");
            writer.write("phoneNum=" + (order.getPhoneNum() != null ? order.getPhoneNum() : "") + "\n");
            writer.write("address=" + (order.getAddress() != null ? order.getAddress() : "") + "\n");
            writer.write("deliveryMethod=" + (order.getDeliveryMethod() != null ? order.getDeliveryMethod() : "") + "\n");
            writer.write("paymentMethod=" + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "") + "\n");
            writer.write("deliveryDate=" + (order.getDeliveryDate() != null ? order.getDeliveryDate() : "") + "\n");
            writer.write("confirmationDate=" + (order.getConfirmationDate() != null ? order.getConfirmationDate() : "") + "\n");
            writer.write("paymentStatus=" + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "") + "\n");
            writer.write("deliveryStatus=" + (order.getDeliveryStatus() != null ? order.getDeliveryStatus() : "") + "\n");
            writer.write("orderStatus=" + (order.getOrderStatus() != null ? order.getOrderStatus() : "") + "\n");
            writer.write("deliveredDate=" + (order.getDeliveredDate() != null ? order.getDeliveredDate() : "") + "\n");
            writer.write("[products]\n");
            for (String[] product : order.getProducts()) {
                writer.write("productID=" + product[0] + ", quantity=" + product[1] + "\n");
            }
            writer.write("[total]\n");
            writer.write("totalPrice=" + String.format("%.2f", order.getTotalPrice()) + "\n");
            writer.write("--- Order End ---\n");
            writer.write("\n");
            System.out.println("Wrote delivered order " + order.getOrderNumber() + " to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing delivered order to file " + filePath + ": " + e.getMessage());
            throw e;
        }
    }

    // Write all orders to orders.txt (unchanged)
    public static void writeAllOrders(String filePath, List<Order> orders) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            System.out.println("Created new orders file at: " + file.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (Order order : orders) {
                writer.write("--- Order Start: " + order.getOrderNumber() + " ---\n");
                writer.write("orderNumber=" + order.getOrderNumber() + "\n");
                writer.write("userNumber=" + order.getUserNumber() + "\n");
                writer.write("name=" + (order.getFullName() != null ? order.getFullName() : "") + "\n");
                writer.write("phoneNum=" + (order.getPhoneNum() != null ? order.getPhoneNum() : "") + "\n");
                writer.write("address=" + (order.getAddress() != null ? order.getAddress() : "") + "\n");
                writer.write("deliveryMethod=" + (order.getDeliveryMethod() != null ? order.getDeliveryMethod() : "") + "\n");
                writer.write("paymentMethod=" + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "") + "\n");
                writer.write("deliveryDate=" + (order.getDeliveryDate() != null ? order.getDeliveryDate() : "") + "\n");
                writer.write("confirmationDate=" + (order.getConfirmationDate() != null ? order.getConfirmationDate() : "") + "\n");
                writer.write("paymentStatus=" + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "") + "\n");
                writer.write("deliveryStatus=" + (order.getDeliveryStatus() != null ? order.getDeliveryStatus() : "") + "\n");
                writer.write("orderStatus=" + (order.getOrderStatus() != null ? order.getOrderStatus() : "") + "\n");
                writer.write("deliveredDate=" + (order.getDeliveredDate() != null ? order.getDeliveredDate() : "") + "\n");
                writer.write("[products]\n");
                for (String[] product : order.getProducts()) {
                    writer.write("productID=" + product[0] + ", quantity=" + product[1] + "\n");
                }
                writer.write("[total]\n");
                writer.write("totalPrice=" + String.format("%.2f", order.getTotalPrice()) + "\n");
                writer.write("--- Order End ---\n");
                writer.write("\n");
            }
            writer.flush();
            System.out.println("Successfully wrote " + orders.size() + " orders to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing orders to file " + filePath + ": " + e.getMessage());
            throw e;
        }
    }

    // Write all delivered orders to deliveredOrders.txt (unchanged)
    public static void writeAllDeliveredOrders(String filePath, List<Order> orders) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            System.out.println("Created new delivered orders file at: " + file.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (Order order : orders) {
                writer.write("--- Order Start: " + order.getOrderNumber() + " ---\n");
                writer.write("orderNumber=" + order.getOrderNumber() + "\n");
                writer.write("userNumber=" + order.getUserNumber() + "\n");
                writer.write("name=" + (order.getFullName() != null ? order.getFullName() : "") + "\n");
                writer.write("phoneNum=" + (order.getPhoneNum() != null ? order.getPhoneNum() : "") + "\n");
                writer.write("address=" + (order.getAddress() != null ? order.getAddress() : "") + "\n");
                writer.write("deliveryMethod=" + (order.getDeliveryMethod() != null ? order.getDeliveryMethod() : "") + "\n");
                writer.write("paymentMethod=" + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "") + "\n");
                writer.write("deliveryDate=" + (order.getDeliveryDate() != null ? order.getDeliveryDate() : "") + "\n");
                writer.write("confirmationDate=" + (order.getConfirmationDate() != null ? order.getConfirmationDate() : "") + "\n");
                writer.write("paymentStatus=" + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "") + "\n");
                writer.write("deliveryStatus=" + (order.getDeliveryStatus() != null ? order.getDeliveryStatus() : "") + "\n");
                writer.write("orderStatus=" + (order.getOrderStatus() != null ? order.getOrderStatus() : "") + "\n");
                writer.write("deliveredDate=" + (order.getDeliveredDate() != null ? order.getDeliveredDate() : "") + "\n");
                writer.write("[products]\n");
                for (String[] product : order.getProducts()) {
                    writer.write("productID=" + product[0] + ", quantity=" + product[1] + "\n");
                }
                writer.write("[total]\n");
                writer.write("totalPrice=" + String.format("%.2f", order.getTotalPrice()) + "\n");
                writer.write("--- Order End ---\n");
                writer.write("\n");
            }
            writer.flush();
            System.out.println("Successfully wrote " + orders.size() + " delivered orders to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing delivered orders to file " + filePath + ": " + e.getMessage());
            throw e;
        }
    }

    // Read all users from users.txt (unchanged)
    public static List<User> readUsers(String filePath) {
        List<User> users = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                System.out.println("Created users file: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating users file " + filePath + ": " + e.getMessage());
                return users;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            User user = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("--- User Start:")) {
                    user = new User(null, null, null, null, null, null, null);
                } else if (line.startsWith("--- User End ---")) {
                    if (user != null && user.getUsername() != null && user.getPassword() != null) {
                        users.add(user);
                    } else {
                        System.err.println("Incomplete user data in " + filePath + ": " + user);
                    }
                    user = null;
                } else if (user != null) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        switch (key) {
                            case "username":
                                user.setUsername(value);
                                break;
                            case "password":
                                user.setPassword(value);
                                break;
                            case "userNumber":
                                user.setUserNumber(value);
                                break;
                            case "fullName":
                                user.setFullName(value);
                                break;
                            case "email":
                                user.setEmail(value);
                                break;
                            case "phoneNumber":
                                user.setPhoneNumber(value);
                                break;
                            case "address":
                                user.setAddress(value);
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file " + filePath + ": " + e.getMessage());
            return null;
        }
        System.out.println("Read " + users.size() + " users from " + filePath);
        return users;
    }

    // Write all users to users.txt (unchanged)
    public static void writeUsers(String filePath, List<User> users) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            System.out.println("Created users file: " + file.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (User user : users) {
                writer.write("--- User Start: " + user.getUserNumber() + " ---\n");
                writer.write("username=" + (user.getUsername() != null ? user.getUsername() : "") + "\n");
                writer.write("password=" + (user.getPassword() != null ? user.getPassword() : "") + "\n");
                writer.write("userNumber=" + (user.getUserNumber() != null ? user.getUserNumber() : "") + "\n");
                writer.write("fullName=" + (user.getFullName() != null ? user.getFullName() : "") + "\n");
                writer.write("email=" + (user.getEmail() != null ? user.getEmail() : "") + "\n");
                writer.write("phoneNumber=" + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "") + "\n");
                writer.write("address=" + (user.getAddress() != null ? user.getAddress() : "") + "\n");
                writer.write("--- User End ---\n");
                writer.write("\n");
            }
            writer.flush();
            System.out.println("Successfully wrote " + users.size() + " users to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
            throw e;
        }
    }

    // Check if a user number is unique in users.txt (unchanged)
    public static boolean isUserNumberUnique(String userNumber, String filePath) {
        List<User> users = readUsers(filePath);
        if (users == null) {
            return true; // If file can't be read, assume unique
        }
        boolean isUnique = users.stream().noneMatch(user -> user.getUserNumber() != null && user.getUserNumber().equals(userNumber));
        System.out.println("User number " + userNumber + " is " + (isUnique ? "unique" : "not unique") + " in " + filePath);
        return isUnique;
    }

    // Read all admins from admins.txt (unchanged)
    public static List<Admin> readAdmins(String filePath) {
        List<Admin> admins = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                System.out.println("Created admins file: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating admins file " + filePath + ": " + e.getMessage());
                return admins;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Admin admin = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("--- Admin Start:") || line.startsWith("--- User Start:")) {
                    admin = new Admin(null, null, null, null);
                } else if (line.startsWith("--- Admin End ---") || line.startsWith("--- User End ---")) {
                    if (admin != null && admin.getEmail() != null && admin.getPassword() != null) {
                        admins.add(admin);
                    } else {
                        System.err.println("Incomplete admin data in " + filePath + ": " + admin);
                    }
                    admin = null;
                } else if (admin != null) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        switch (key) {
                            case "adminNumber":
                            case "userNumber": // Map userNumber to adminNumber for backward compatibility
                                admin.setAdminNumber(value);
                                break;
                            case "email":
                            case "username": // Map username to email for backward compatibility
                                admin.setEmail(value);
                                break;
                            case "password":
                                admin.setPassword(value);
                                break;
                            case "role":
                                admin.setRole(value);
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading admins file " + filePath + ": " + e.getMessage());
            return null;
        }
        System.out.println("Read " + admins.size() + " admins from " + filePath);
        return admins;
    }

    // Read an admin by email from admins.txt (unchanged)
    public static Admin readAdminByEmail(String email, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Admin admin = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--- Admin Start:") || line.startsWith("--- User Start:")) {
                    String adminNumber = null, adminEmail = null, password = null, role = null;
                    while ((line = reader.readLine()) != null &&
                            !(line.startsWith("--- Admin End ---") || line.startsWith("--- User End ---"))) {
                        if (line.startsWith("adminNumber=") || line.startsWith("userNumber=")) {
                            adminNumber = line.split("=", 2)[1];
                        } else if (line.startsWith("email=") || line.startsWith("username=")) {
                            adminEmail = line.split("=", 2)[1];
                        } else if (line.startsWith("password=")) {
                            password = line.split("=", 2)[1];
                        } else if (line.startsWith("role=")) {
                            role = line.split("=", 2)[1];
                        }
                    }
                    if (adminEmail != null && adminEmail.equals(email)) {
                        admin = new Admin(adminNumber, adminEmail, password, role);
                        break;
                    }
                }
            }
            return admin;
        } catch (IOException e) {
            System.out.println("Error reading admin from file: " + e.getMessage());
            return null;
        }
    }

    // Write logged-in user to loggedInUser.txt (unchanged)
    public static synchronized void writeLoggedInUser(String filePath, User user) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                System.out.println("Created loggedInUser file: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error creating loggedInUser file " + filePath + ": " + e.getMessage());
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            if (user != null) {
                writer.write("--- User Start: " + user.getUserNumber() + " ---\n");
                writer.write("username=" + (user.getUsername() != null ? user.getUsername() : "") + "\n");
                writer.write("password=" + (user.getPassword() != null ? user.getPassword() : "") + "\n");
                writer.write("userNumber=" + (user.getUserNumber() != null ? user.getUserNumber() : "") + "\n");
                writer.write("fullName=" + (user.getFullName() != null ? user.getFullName() : "") + "\n");
                writer.write("email=" + (user.getEmail() != null ? user.getEmail() : "") + "\n");
                writer.write("phoneNumber=" + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "") + "\n");
                writer.write("address=" + (user.getAddress() != null ? user.getAddress() : "") + "\n");
                writer.write("--- User End ---\n");
                System.out.println("Wrote logged-in user to " + filePath + ": " + user.getEmail());
            } else {
                System.err.println("Cannot write null user to " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Error writing to loggedInUser file " + filePath + ": " + e.getMessage());
        }
    }

    // Read logged-in user from loggedInUser.txt (unchanged)
    public static User readLoggedInUser(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No loggedInUser file found at " + filePath);
            return null;
        }

        User user = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("--- User Start:")) {
                    user = new User(null, null, null, null, null, null, null);
                } else if (line.startsWith("--- User End ---")) {
                    break; // Only one user should be in loggedInUser.txt
                } else if (user != null) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        switch (key) {
                            case "username":
                                user.setUsername(value);
                                break;
                            case "password":
                                user.setPassword(value);
                                break;
                            case "userNumber":
                                user.setUserNumber(value);
                                break;
                            case "fullName":
                                user.setFullName(value);
                                break;
                            case "email":
                                user.setEmail(value);
                                break;
                            case "phoneNumber":
                                user.setPhoneNumber(value);
                                break;
                            case "address":
                                user.setAddress(value);
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading loggedInUser file " + filePath + ": " + e.getMessage());
            return null;
        }

        if (user != null && user.getUserNumber() != null && user.getEmail() != null) {
            System.out.println("Read logged-in user from " + filePath + ": " + user.getEmail());
            return user;
        }
        System.err.println("Incomplete logged-in user data in " + filePath);
        return null;
    }

    // Clear loggedInUser.txt on logout (unchanged)
    public static synchronized void clearLoggedInUser(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                writer.write("");
                System.out.println("Cleared loggedInUser file: " + filePath);
            } catch (IOException e) {
                System.err.println("Error clearing loggedInUser file " + filePath + ": " + e.getMessage());
            }
        } else {
            System.out.println("loggedInUser file does not exist at " + filePath + "; nothing to clear.");
        }
    }

    // Order class for reading orders (unchanged)
    public static class Order {
        private String orderNumber;
        private String userNumber;
        private String fullName;
        private String phoneNum;
        private String address;
        private String deliveryMethod;
        private String paymentMethod;
        private String deliveryDate;
        private String confirmationDate;
        private String paymentStatus;
        private String deliveryStatus;
        private String orderStatus;
        private String deliveredDate;
        private List<String[]> products; // [productID, quantity]
        private double totalPrice;

        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public String getUserNumber() { return userNumber; }
        public void setUserNumber(String userNumber) { this.userNumber = userNumber; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhoneNum() { return phoneNum; }
        public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getDeliveryMethod() { return deliveryMethod; }
        public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getDeliveryDate() { return deliveryDate; }
        public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
        public String getConfirmationDate() { return confirmationDate; }
        public void setConfirmationDate(String confirmationDate) { this.confirmationDate = confirmationDate; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getDeliveryStatus() { return deliveryStatus; }
        public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
        public String getDeliveredDate() { return deliveredDate; }
        public void setDeliveredDate(String deliveredDate) { this.deliveredDate = deliveredDate; }
        public List<String[]> getProducts() { return products; }
        public void setProducts(List<String[]> products) { this.products = products; }
        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    }
}