package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.GroceryItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductAdminServlet extends HttpServlet {
    private static final String ITEMS_FILE_PATH = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/items.txt";

    @Override
    public void init() throws ServletException {
        System.out.println("ProductAdminServlet initialized with ITEMS_FILE_PATH: " + ITEMS_FILE_PATH);
        // Ensure the items.txt file exists
        File file = new File(ITEMS_FILE_PATH);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                System.out.println("ProductAdminServlet - Created items.txt file at: " + ITEMS_FILE_PATH);
            } catch (IOException e) {
                System.err.println("ProductAdminServlet - Failed to create items.txt file: " + e.getMessage());
                throw new ServletException("Failed to initialize ProductAdminServlet due to file creation error", e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("ProductAdminServlet - doGet invoked with request URI: " + request.getRequestURI());
        System.out.println("ProductAdminServlet - Context Path: " + request.getContextPath());
        System.out.println("ProductAdminServlet - Servlet Path: " + request.getServletPath());
        System.out.println("ProductAdminServlet - Query String: " + request.getQueryString());

        // Check for error parameter to prevent redirect loop
        String error = request.getParameter("error");
        if ("missingData".equals(error)) {
            System.out.println("ProductAdminServlet - Error: missingData detected. Rendering error message in productDashboard.jsp.");
            request.setAttribute("errorMessage", "Failed to load products. Please try again.");
            request.setAttribute("products", new ArrayList<GroceryItem>());
            request.getRequestDispatcher("/adminPages/productDashboard.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminEmail") == null) {
            System.out.println("ProductAdminServlet - No admin session found. Redirecting to admin login page.");
            response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=notLoggedIn");
            return;
        }

        // Check if the user has the correct role (Super Admin or Product Admin)
        String adminRole = (String) session.getAttribute("adminRole");
        if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "product".equalsIgnoreCase(adminRole))) {
            System.out.println("ProductAdminServlet - Unauthorized access. Redirecting to AdminServlet.");
            response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");
        System.out.println("ProductAdminServlet - Action: " + action);

        if ("info".equals(action)) {
            // Handle navigation to productDashboardInfo.jsp for editing
            String productID = request.getParameter("productID");
            if (productID == null) {
                System.out.println("ProductAdminServlet - Missing productID parameter for info action.");
                response.sendRedirect(request.getContextPath() + "/ProductAdminServlet?error=invalidParameters");
                return;
            }

            // Read all products
            List<GroceryItem> products = FileUtil.readItems(ITEMS_FILE_PATH);
            if (products == null) {
                products = new ArrayList<>();
                System.out.println("ProductAdminServlet - Failed to read products for info action. Using empty list.");
            }

            // Find the product
            GroceryItem product = products.stream()
                    .filter(item -> String.valueOf(item.getProductID()).equals(productID))
                    .findFirst()
                    .orElse(null);

            if (product == null) {
                System.out.println("ProductAdminServlet - Product not found: " + productID);
                response.sendRedirect(request.getContextPath() + "/ProductAdminServlet?error=productNotFound");
                return;
            }

            // Set attributes for productDashboardInfo.jsp
            request.setAttribute("product", product);
            System.out.println("ProductAdminServlet - Forwarding to /adminPages/productDashboardInfo.jsp with productID: " + productID);
            request.getRequestDispatcher("/adminPages/productDashboardInfo.jsp").forward(request, response);
            return;
        } else if ("add".equals(action)) {
            // Handle navigation to productDashboardInfo.jsp for adding a new product
            List<GroceryItem> products = FileUtil.readItems(ITEMS_FILE_PATH);
            if (products == null) {
                products = new ArrayList<>();
                System.out.println("ProductAdminServlet - Failed to read products for add action. Using empty list.");
            }

            // Find the next available product ID
            int nextProductID = findNextProductID(products);
            request.setAttribute("nextProductID", String.valueOf(nextProductID));
            System.out.println("ProductAdminServlet - Forwarding to /adminPages/productDashboardInfo.jsp for adding new product with nextProductID: " + nextProductID);
            request.getRequestDispatcher("/adminPages/productDashboardInfo.jsp").forward(request, response);
            return;
        } else if ("delete".equals(action)) {
            // Handle product deletion
            String productID = request.getParameter("productID");
            if (productID == null) {
                System.out.println("ProductAdminServlet - Missing productID parameter for delete action.");
                response.sendRedirect(request.getContextPath() + "/ProductAdminServlet?error=invalidParameters");
                return;
            }

            synchronized (this) {
                List<GroceryItem> products = FileUtil.readItems(ITEMS_FILE_PATH);
                if (products == null) {
                    products = new ArrayList<>();
                    System.out.println("ProductAdminServlet - Failed to read products for delete action. Using empty list.");
                }

                // Remove the product
                products.removeIf(item -> String.valueOf(item.getProductID()).equals(productID));
                try {
                    FileUtil.writeItems(ITEMS_FILE_PATH, new ArrayList<>(products));
                    System.out.println("ProductAdminServlet - Deleted product with productID: " + productID);
                } catch (IOException e) {
                    System.err.println("ProductAdminServlet - Error writing to items.txt during delete: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/ProductAdminServlet?error=deleteFailed");
                    return;
                }
            }

            // Redirect back to the dashboard
            response.sendRedirect(request.getContextPath() + "/ProductAdminServlet");
            return;
        }

        // Default action: Load all products for the dashboard
        List<GroceryItem> products;
        try {
            products = FileUtil.readItems(ITEMS_FILE_PATH);
            if (products == null) {
                products = new ArrayList<>();
                System.out.println("ProductAdminServlet - Failed to read products. Using empty list.");
            } else {
                Collections.sort(products, Comparator.comparingInt(GroceryItem::getProductID));
                System.out.println("ProductAdminServlet - Loaded " + products.size() + " products from items.txt");
            }
        } catch (Exception e) {
            System.err.println("ProductAdminServlet - Unexpected error reading items.txt: " + e.getMessage());
            products = new ArrayList<>();
            request.setAttribute("errorMessage", "Failed to load products due to an unexpected error.");
        }
        System.out.println("ProductAdminServlet - Setting products attribute with size: " + products.size()); // Debug
        request.setAttribute("products", products);
        // Forward to productDashboard.jsp
        System.out.println("ProductAdminServlet - Forwarding to /adminPages/productDashboard.jsp");
        request.getRequestDispatcher("/adminPages/productDashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminEmail") == null) {
            System.out.println("ProductAdminServlet - No admin session found. Redirecting to admin login page.");
            response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=notLoggedIn");
            return;
        }

        String action = request.getParameter("action");
        if ("add".equals(action) || "update".equals(action)) {
            String productID = request.getParameter("productID");
            String productCategory = request.getParameter("productCategory");
            String productName = request.getParameter("productName");
            String productPriceStr = request.getParameter("productPrice");
            String productImageLink = request.getParameter("productImageLink");
            String quantityStr = request.getParameter("quantity");
            String description = request.getParameter("description");

            // Validate parameters
            if (productID == null || productCategory == null || productName == null || productPriceStr == null ||
                    productImageLink == null || quantityStr == null || description == null) {
                System.out.println("ProductAdminServlet - Missing parameters for " + action + " action.");
                response.sendRedirect(request.getContextPath() + "/ProductAdminServlet?error=invalidParameters");
                return;
            }

            try {
                int productIDInt = Integer.parseInt(productID);
                double productPrice = Double.parseDouble(productPriceStr);
                int quantity = Integer.parseInt(quantityStr);

                try {
                    synchronized (this) {
                        List<GroceryItem> products = FileUtil.readItems(ITEMS_FILE_PATH);
                        if (products == null) {
                            products = new ArrayList<>();
                            System.out.println("ProductAdminServlet - Failed to read products for " + action + " action. Using empty list.");
                        }

                        if ("add".equals(action)) {
                            // Add new product
                            GroceryItem newProduct = new GroceryItem(productIDInt, productCategory, productName, productPrice, productImageLink, quantity, description);
                            products.add(newProduct);
                            System.out.println("ProductAdminServlet - Added new product with productID: " + productIDInt);
                        } else {
                            // Update existing product
                            for (GroceryItem item : products) {
                                if (item.getProductID() == productIDInt) {
                                    item.setProductCategory(productCategory);
                                    item.setProductName(productName);
                                    item.setProductPrice(productPrice);
                                    item.setProductImageLink(productImageLink);
                                    item.setQuantity(quantity);
                                    item.setDescription(description);
                                    System.out.println("ProductAdminServlet - Updated product with productID: " + productIDInt);
                                    break;
                                }
                            }
                        }

                        // Write updated products back to file
                        FileUtil.writeItems(ITEMS_FILE_PATH, new ArrayList<>(products));
                    }

                    // Redirect back to the dashboard
                    response.sendRedirect(request.getContextPath() + "/ProductAdminServlet");
                } catch (IOException e) {
                    System.err.println("ProductAdminServlet - Error writing to items.txt: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/ProductAdminServlet?error=saveFailed");
                }
            } catch (NumberFormatException e) {
                System.out.println("ProductAdminServlet - Invalid number format in parameters: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/ProductAdminServlet?error=invalidParameters");
            }
        } else {
            System.out.println("ProductAdminServlet - Invalid action in doPost: " + action);
            response.sendRedirect(request.getContextPath() + "/ProductAdminServlet?error=invalidAction");
        }
    }

    // Find the lowest available product number
    private int findNextProductID(List<GroceryItem> items) {
        if (items.isEmpty()) {
            return 1; // If no items exist, start with 1
        }

        // Sort items by productID to check for gaps
        Collections.sort(items, Comparator.comparingInt(GroceryItem::getProductID));

        // Check for gaps in product numbers
        int expectedID = 1;
        for (GroceryItem item : items) {
            int currentID = item.getProductID();
            if (currentID != expectedID) {
                return expectedID; // Found a gap, return the first available number
            }
            expectedID++;
        }

        // No gaps found, return the next number after the highest ID
        return expectedID;
    }
}