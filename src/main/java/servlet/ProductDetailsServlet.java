package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.FileUtil;
import model.GroceryItem;

import java.io.IOException;
import java.util.ArrayList;

public class ProductDetailsServlet extends HttpServlet {
    private static final String ITEMS_FILE = "C:\\Users\\LENOVO\\IdeaProjects\\OnlineGroceryOrderManagmentSystem\\src\\main\\webapp\\data\\items.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdStr = request.getParameter("productId");
        if (productIdStr == null || productIdStr.isEmpty()) {
            request.setAttribute("error", "Product ID is required.");
            request.getRequestDispatcher("/cartAndOrders/product-details.jsp").forward(request, response);
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(productIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Product ID.");
            request.getRequestDispatcher("/cartAndOrders/product-details.jsp").forward(request, response);
            return;
        }

        // Load the latest items from items.txt
        ArrayList<GroceryItem> items = FileUtil.readItems(ITEMS_FILE);
        if (items == null || items.isEmpty()) {
            request.setAttribute("error", "No products available.");
            request.getRequestDispatcher("/cartAndOrders/product-details.jsp").forward(request, response);
            return;
        }

        // Find the item by productId
        GroceryItem item = items.stream()
                .filter(i -> i.getProductID() == productId)
                .findFirst()
                .orElse(null);

        if (item == null) {
            request.setAttribute("error", "Product not found.");
        } else {
            request.setAttribute("item", item);
            request.setAttribute("outOfStock", item.getQuantity() <= 0); // Optional, for consistency
        }

        request.getRequestDispatcher("/cartAndOrders/product-details.jsp").forward(request, response);
    }
}