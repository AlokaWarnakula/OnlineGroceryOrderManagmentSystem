package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.GroceryItem;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CartServlet extends HttpServlet {
    private static final String ITEMS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/items.txt";
    private static final String CART_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/cart.txt";
    private MergeServlet mergeServlet; // Instance of MergeServlet

    @Override
    public void init() throws ServletException {
        File dataDir = new File("/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data");
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                System.out.println("Created data directory: " + dataDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create data directory: " + dataDir.getAbsolutePath());
                throw new ServletException("Unable to create data directory for file operations");
            }
        } else if (!dataDir.canWrite()) {
            System.err.println("Data directory is not writable: " + dataDir.getAbsolutePath());
            throw new ServletException("Data directory is not writable for file operations");
        }

        System.out.println("ITEMS_FILE path: " + ITEMS_FILE);
        System.out.println("CART_FILE path: " + CART_FILE);
        mergeServlet = new MergeServlet(); // Initialize MergeServlet
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp?error=notLoggedIn");
            return;
        }

        ArrayList<GroceryItem> cart;
        synchronized (this) {
            cart = FileUtil.readItems(CART_FILE);
            if (cart == null) {
                cart = new ArrayList<>();
                System.out.println("Initialized empty cart for " + CART_FILE + " (file not found or invalid)");
            } else {
                System.out.println("Loaded cart from " + CART_FILE + ": " + cart);
            }
        }

        ArrayList<GroceryItem> items = FileUtil.readItems(ITEMS_FILE);
        if (items == null) {
            items = new ArrayList<>();
            System.out.println("Initialized empty items list for " + ITEMS_FILE + " (file not found or invalid)");
        }
        System.out.println("All loaded items: " + items);
        double totalPrice = cart.stream().mapToDouble(GroceryItem::getTotalPrice).sum();

        String action = request.getParameter("action");
        System.out.println("doGet action: " + action);
        if ("getCart".equals(action)) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            if (cart.isEmpty()) {
                out.write("{\"success\": true, \"message\": \"Cart is empty\", \"cart\": [], \"totalPrice\": 0.00}");
            } else {
                sendCartResponse(out, cart, totalPrice, items);
            }
            out.flush();
        } else {
            String category = request.getParameter("category");
            String minPriceStr = request.getParameter("minPrice");
            String maxPriceStr = request.getParameter("maxPrice");
            String name = request.getParameter("name");

            System.out.println("Parameters - category: " + category + ", minPrice: " + minPriceStr + ", maxPrice: " + maxPriceStr + ", name: " + name);

            // Default category to null (show all) if not specified or "All"
            if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("All")) {
                category = null; // Show all categories
                System.out.println("No specific category selected, showing all products");
            }
            final String finalCategory = category; // Create a final copy for lambda expression

            ArrayList<GroceryItem> filteredItems = new ArrayList<>(items);
            System.out.println("Initial items count: " + filteredItems.size());

            // Step 1: Filter by category if specified
            if (finalCategory != null && !finalCategory.trim().isEmpty()) {
                filteredItems = filteredItems.stream()
                        .filter(item -> {
                            boolean matches = item.getProductCategory().equalsIgnoreCase(finalCategory);
                            System.out.println("Checking category for item " + item.getProductName() + ": " + item.getProductCategory() + " == " + finalCategory + " -> " + matches);
                            return matches;
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
                System.out.println("After category filter, result size: " + filteredItems.size());
                System.out.println("Items after category filter: " + filteredItems);
            }

            // Step 2: Filter by name if specified
            if (name != null && !name.trim().isEmpty()) {
                filteredItems = filteredItems.stream()
                        .filter(item -> {
                            boolean matches = item.getProductName().toLowerCase().contains(name.toLowerCase());
                            System.out.println("Checking name for item " + item.getProductName() + ": contains " + name + " -> " + matches);
                            return matches;
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
                System.out.println("After name filter, result size: " + filteredItems.size());
                System.out.println("Items after name filter: " + filteredItems);
            }

            // Step 3: Filter by price range
            // Apply minPrice filter
            if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
                try {
                    double minPrice = Double.parseDouble(minPriceStr);
                    filteredItems = filteredItems.stream()
                            .filter(item -> {
                                boolean matches = item.getProductPrice() >= minPrice;
                                System.out.println("Checking minPrice for item " + item.getProductName() + ": " + item.getProductPrice() + " >= " + minPrice + " -> " + matches);
                                return matches;
                            })
                            .collect(Collectors.toCollection(ArrayList::new));
                    System.out.println("After minPrice filter (" + minPrice + "), result size: " + filteredItems.size());
                    System.out.println("Items after minPrice filter: " + filteredItems);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid minPrice: " + minPriceStr);
                }
            }

            // Apply maxPrice filter
            if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
                try {
                    double maxPrice = Double.parseDouble(maxPriceStr);
                    filteredItems = filteredItems.stream()
                            .filter(item -> {
                                boolean matches = item.getProductPrice() <= maxPrice;
                                System.out.println("Checking maxPrice for item " + item.getProductName() + ": " + item.getProductPrice() + " <= " + maxPrice + " -> " + matches);
                                return matches;
                            })
                            .collect(Collectors.toCollection(ArrayList::new));
                    System.out.println("After maxPrice filter (" + maxPrice + "), result size: " + filteredItems.size());
                    System.out.println("Items after maxPrice filter: " + filteredItems);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid maxPrice: " + maxPriceStr);
                }
            }

            // Call MergeServlet to sort the filtered items
            if (!filteredItems.isEmpty()) {
                mergeServlet.sortItems(filteredItems);
            }

            // Determine if we're showing search results or a specific category
            boolean isSearchResult = (name != null && !name.trim().isEmpty()) ||
                    (minPriceStr != null && !minPriceStr.trim().isEmpty()) ||
                    (maxPriceStr != null && !maxPriceStr.trim().isEmpty());

            request.setAttribute("items", filteredItems);
            request.setAttribute("cart", cart);
            request.setAttribute("totalPrice", totalPrice);
            request.setAttribute("category", category);
            request.setAttribute("isSearchResult", isSearchResult);
            System.out.println("Forwarding to cartIndex.jsp with items size: " + filteredItems.size() + ", isSearchResult: " + isSearchResult);
            request.getRequestDispatcher("/cartAndOrders/cartIndex.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.write("{\"success\": false, \"message\": \"Please log in to access the cart.\"}");
            out.flush();
            return;
        }

        System.out.println("Received POST request to CartServlet with action: " + request.getParameter("action"));
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        ArrayList<GroceryItem> cart;
        ArrayList<GroceryItem> items = FileUtil.readItems(ITEMS_FILE);
        if (items == null) {
            items = new ArrayList<>();
            System.out.println("Initialized empty items list for " + ITEMS_FILE + " (file not found or invalid)");
        }
        System.out.println("Loaded items from " + ITEMS_FILE + ": " + items);

        synchronized (this) {
            cart = FileUtil.readItems(CART_FILE);
            if (cart == null) {
                cart = new ArrayList<>();
                System.out.println("Initialized empty cart for " + CART_FILE);
            }
            System.out.println("Loaded cart before modification: " + cart);

            try {
                if ("add".equals(request.getParameter("action"))) {
                    System.out.println("Processing 'add' action with itemId: " + request.getParameter("itemId"));
                    int itemId = Integer.parseInt(request.getParameter("itemId"));
                    GroceryItem itemToAdd = items.stream().filter(item -> item.getProductID() == itemId).findFirst().orElse(null);
                    if (itemToAdd == null) {
                        System.out.println("Item not found for itemId: " + itemId);
                        out.write("{\"success\": false, \"message\": \"Item not found\"}");
                    } else if (itemToAdd.getQuantity() <= 0) {
                        System.out.println("Item out of stock: " + itemToAdd);
                        out.write("{\"success\": false, \"message\": \"Item is out of stock\"}");
                    } else {
                        GroceryItem existingItem = cart.stream().filter(item -> item.getProductID() == itemId).findFirst().orElse(null);
                        if (existingItem != null) {
                            out.write("{\"success\": false, \"message\": \"Item already in cart, use cart to increase quantity\"}");
                        } else {
                            GroceryItem cartItem = new GroceryItem(itemToAdd.getProductID(), itemToAdd.getProductCategory(),
                                    itemToAdd.getProductName(), itemToAdd.getProductPrice(), itemToAdd.getProductImageLink(),
                                    1, itemToAdd.getDescription());
                            cart.add(cartItem);
                            itemToAdd.setQuantity(itemToAdd.getQuantity() - 1);
                            FileUtil.writeItems(ITEMS_FILE, items);
                            FileUtil.writeItems(CART_FILE, cart);
                            System.out.println("Added new item to cart and updated stock: " + cartItem);
                            double totalPrice = cart.stream().mapToDouble(GroceryItem::getTotalPrice).sum();
                            sendCartResponse(out, cart, totalPrice, items);
                        }
                    }
                } else if ("update".equals(request.getParameter("action"))) {
                    System.out.println("Processing 'update' action with itemId: " + request.getParameter("itemId"));
                    int itemId = Integer.parseInt(request.getParameter("itemId"));
                    int change = Integer.parseInt(request.getParameter("change"));
                    GroceryItem item = cart.stream().filter(i -> i.getProductID() == itemId).findFirst().orElse(null);
                    if (item == null) {
                        System.out.println("Item not found in cart for itemId: " + itemId);
                        out.write("{\"success\": false, \"message\": \"Item not found in cart\"}");
                    } else {
                        int stockQuantity = items.stream()
                                .filter(i -> i.getProductID() == itemId)
                                .findFirst()
                                .map(GroceryItem::getQuantity)
                                .orElse(0);
                        int currentCartQuantity = item.getQuantity();
                        int totalAvailableStock = stockQuantity + currentCartQuantity;
                        int newQuantity = Math.max(0, Math.min(currentCartQuantity + change, totalAvailableStock));
                        int quantityChange = newQuantity - currentCartQuantity;
                        if (quantityChange != 0) {
                            GroceryItem stockItem = items.stream()
                                    .filter(i -> i.getProductID() == itemId)
                                    .findFirst()
                                    .orElse(null);
                            if (stockItem != null) {
                                stockItem.setQuantity(stockItem.getQuantity() - quantityChange);
                                FileUtil.writeItems(ITEMS_FILE, items);
                                System.out.println("Updated stock in " + ITEMS_FILE + ": " + items);
                            }
                            item.setQuantity(newQuantity);
                            if (newQuantity == 0) {
                                cart.remove(item);
                                System.out.println("Removed item from cart: " + item);
                            } else {
                                System.out.println("Updated item quantity in cart: " + item);
                            }
                            FileUtil.writeItems(CART_FILE, cart);
                            System.out.println("Wrote cart to " + CART_FILE + ": " + cart);
                        }
                        double totalPrice = cart.stream().mapToDouble(GroceryItem::getTotalPrice).sum();
                        sendCartResponse(out, cart, totalPrice, items);
                    }
                } else if ("remove".equals(request.getParameter("action"))) {
                    System.out.println("Processing 'remove' action with itemId: " + request.getParameter("itemId"));
                    int itemId = Integer.parseInt(request.getParameter("itemId"));
                    GroceryItem itemToRemove = cart.stream().filter(item -> item.getProductID() == itemId).findFirst().orElse(null);
                    if (itemToRemove != null) {
                        int quantityRemoved = itemToRemove.getQuantity();
                        cart.remove(itemToRemove);
                        GroceryItem stockItem = items.stream()
                                .filter(item -> item.getProductID() == itemId)
                                .findFirst()
                                .orElse(null);
                        if (stockItem != null) {
                            stockItem.setQuantity(stockItem.getQuantity() + quantityRemoved);
                            FileUtil.writeItems(ITEMS_FILE, items);
                            System.out.println("Updated stock in " + ITEMS_FILE + ": " + items);
                        }
                        FileUtil.writeItems(CART_FILE, cart);
                        System.out.println("Wrote cart to " + CART_FILE + ": " + cart);
                        double totalPrice = cart.stream().mapToDouble(GroceryItem::getTotalPrice).sum();
                        sendCartResponse(out, cart, totalPrice, items);
                    } else {
                        System.out.println("Item not found in cart for itemId: " + itemId);
                        out.write("{\"success\": false, \"message\": \"Item not found in cart\"}");
                    }
                } else {
                    System.out.println("Invalid action received: " + request.getParameter("action"));
                    out.write("{\"success\": false, \"message\": \"Invalid action\"}");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid item ID or change value: " + e.getMessage());
                out.write("{\"success\": false, \"message\": \"Invalid item ID or change value\"}");
            } catch (IOException e) {
                System.err.println("I/O error during cart operation: " + e.getMessage());
                out.write("{\"success\": false, \"message\": \"Failed to update cart due to I/O error\"}");
            }
        }
        out.flush();
    }

    private void sendCartResponse(PrintWriter out, ArrayList<GroceryItem> cart, double totalPrice, ArrayList<GroceryItem> items) {
        out.write("{\"success\": true, \"message\": \"Cart retrieved successfully\", \"cart\": [");
        for (int i = 0; i < cart.size(); i++) {
            GroceryItem cartItem = cart.get(i);
            int stockQuantity = items.stream()
                    .filter(item -> item.getProductID() == cartItem.getProductID())
                    .findFirst()
                    .map(GroceryItem::getQuantity)
                    .orElse(0);
            int totalAvailableStock = stockQuantity + cartItem.getQuantity();
            out.write(String.format("{\"productID\": %d, \"productName\": \"%s\", \"productPrice\": %.2f, " +
                            "\"productImageLink\": \"%s\", \"quantity\": %d, \"description\": \"%s\", \"stockQuantity\": %d, \"totalAvailableStock\": %d}",
                    cartItem.getProductID(), escapeJson(cartItem.getProductName()), cartItem.getProductPrice(),
                    escapeJson(cartItem.getProductImageLink()), cartItem.getQuantity(),
                    escapeJson(cartItem.getDescription() != null ? cartItem.getDescription() : ""),
                    stockQuantity, totalAvailableStock));
            if (i < cart.size() - 1) out.write(",");
        }
        out.write("], \"totalPrice\": " + String.format("%.2f", totalPrice) + "}");
    }

    private String escapeJson(String str) {
        return str != null ? str.replace("\"", "\\\"").replace("\n", "\\n") : "";
    }
}