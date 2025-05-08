package servlet;

import model.FileUtil;
import model.GroceryItem;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

public class StockAdminServlet extends HttpServlet {
    private static final int LOW_STOCK_THRESHOLD = 250; // Define the threshold for low stock
    private static final String ITEMS_FILE_PATH = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/items.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminNumber") == null || session.getAttribute("adminEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=sessionExpired");
            return;
        }

        // Check if the user has the correct role (Super Admin or Stock Admin)
        String adminRole = (String) session.getAttribute("adminRole");
        if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "stock".equalsIgnoreCase(adminRole))) {
            response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
            return;
        }

        // Read items from items.txt
        System.out.println("Reading items from: " + ITEMS_FILE_PATH);
        ArrayList<GroceryItem> allItems;
        try {
            allItems = FileUtil.readItems(ITEMS_FILE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error reading items: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        // Filter items with low stock
        ArrayList<GroceryItem> lowStockItems = new ArrayList<>();
        for (GroceryItem item : allItems) {
            if (item.getQuantity() < LOW_STOCK_THRESHOLD) {
                lowStockItems.add(item);
            }
        }

        // Set the low stock items as a request attribute
        request.setAttribute("lowStockItems", lowStockItems);

        // Forward to stockDashboard.jsp in adminPages directory
        request.getRequestDispatcher("/adminPages/stockDashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if the admin is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminNumber") == null || session.getAttribute("adminEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=sessionExpired");
            return;
        }

        // Check if the user has the correct role
        String adminRole = (String) session.getAttribute("adminRole");
        if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "stock".equalsIgnoreCase(adminRole))) {
            response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
            return;
        }

        // Get the action parameter
        String action = request.getParameter("action");
        if ("updateStock".equals(action)) {
            // Get the product ID and new stock count
            String productID = request.getParameter("productID");
            String stockCountStr = request.getParameter("stockCount");

            // Validate input
            if (productID == null || stockCountStr == null || productID.trim().isEmpty() || stockCountStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/StockAdminServlet?error=invalidInput");
                return;
            }

            int newStockCount;
            try {
                newStockCount = Integer.parseInt(stockCountStr);
                if (newStockCount < 0) {
                    response.sendRedirect(request.getContextPath() + "/StockAdminServlet?error=invalidStockCount");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/StockAdminServlet?error=invalidStockCount");
                return;
            }

            // Read items from items.txt
            System.out.println("Reading items from: " + ITEMS_FILE_PATH);
            ArrayList<GroceryItem> allItems;
            try {
                allItems = FileUtil.readItems(ITEMS_FILE_PATH);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/StockAdminServlet?error=readError");
                return;
            }

            // Update the stock count for the specified product
            boolean itemFound = false;
            for (GroceryItem item : allItems) {
                if (String.valueOf(item.getProductID()).equals(productID)) {
                    item.setQuantity(newStockCount);
                    itemFound = true;
                    break;
                }
            }

            if (!itemFound) {
                response.sendRedirect(request.getContextPath() + "/StockAdminServlet?error=itemNotFound");
                return;
            }

            // Save the updated items back to items.txt
            try {
                System.out.println("Writing items to: " + ITEMS_FILE_PATH);
                FileUtil.writeItems(ITEMS_FILE_PATH, allItems);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/StockAdminServlet?error=writeError");
                return;
            }

            // Redirect back to StockServlet to refresh the dashboard
            response.sendRedirect(request.getContextPath() + "/StockAdminServlet?success=stockUpdated");
        } else {
            response.sendRedirect(request.getContextPath() + "/StockAdminServlet?error=invalidAction");
        }
    }
}