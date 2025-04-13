package servlet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.GroceryItem;

public class DealServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Manually specify product IDs for the deals
    private static final int[] DEAL_PRODUCT_IDS = {1, 2, 3, 4,48,41};

    // Absolute path to items.txt
    private static final String ITEMS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/items.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<GroceryItem> dealProducts = new ArrayList<>();

        // Read items.txt and fetch products matching the deal IDs
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 7) {
                    System.err.println("Skipping malformed line in items.txt: " + line);
                    continue;
                }

                try {
                    int productID = Integer.parseInt(parts[0].trim());
                    // Check if this product ID is in the deals array
                    for (int dealId : DEAL_PRODUCT_IDS) {
                        if (productID == dealId) {
                            String productCategory = parts[1].trim();
                            String productName = parts[2].trim();
                            double productPrice = Double.parseDouble(parts[3].trim());
                            String productImageLink = parts[4].trim();
                            int quantity = Integer.parseInt(parts[5].trim());
                            String description = parts[6].trim();

                            // Create a GroceryItem object using the constructor
                            GroceryItem item = new GroceryItem(productID, productCategory, productName, productPrice, productImageLink, quantity, description);
                            dealProducts.add(item);
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Skipping line due to number format error in items.txt: " + line);
                    continue;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ServletException("File not found: " + ITEMS_FILE, e);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new ServletException("Permission denied when accessing file: " + ITEMS_FILE, e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServletException("Error reading items.txt at path: " + ITEMS_FILE, e);
        }

        // Set the deal products as a request attribute
        request.setAttribute("dealProducts", dealProducts);

        // Do NOT forward back to index.jsp, as it causes a loop
        // Let the including JSP (index.jsp) continue rendering
    }
}