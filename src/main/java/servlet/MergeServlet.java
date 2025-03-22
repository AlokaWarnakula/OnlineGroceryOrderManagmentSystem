package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import model.GroceryItem;

import java.util.ArrayList;

public class MergeServlet extends HttpServlet {

    // Enum to define sorting criteria (only NAME and PRICE)
    public enum SortCriterion {
        NAME,  // Sort by product name (alphabetically)
        PRICE  // Sort by price (ascending)
    }

    // Merge sort implementation with a single criterion
    public void mergeSort(ArrayList<GroceryItem> items, int left, int right, SortCriterion criterion) {
        System.out.println("MergeServlet: Starting mergeSort with left=" + left + ", right=" + right + ", items size=" + items.size() + ", criterion=" + criterion);
        if (left < right) {
            int mid = left + (right - left) / 2; // Avoid integer overflow
            System.out.println("MergeServlet: Splitting at mid=" + mid);
            mergeSort(items, left, mid, criterion);
            mergeSort(items, mid + 1, right, criterion);
            merge(items, left, mid, right, criterion);
        }
        System.out.println("MergeServlet: Finished mergeSort for range left=" + left + ", right=" + right);
    }

    private void merge(ArrayList<GroceryItem> items, int left, int mid, int right, SortCriterion criterion) {
        System.out.println("MergeServlet: Merging from left=" + left + ", mid=" + mid + ", right=" + right);
        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Create temporary arrays for left and right halves
        ArrayList<GroceryItem> leftArray = new ArrayList<>(n1);
        ArrayList<GroceryItem> rightArray = new ArrayList<>(n2);

        // Copy data to temporary arrays
        for (int i = 0; i < n1; i++) {
            leftArray.add(items.get(left + i));
        }
        for (int j = 0; j < n2; j++) {
            rightArray.add(items.get(mid + 1 + j));
        }

        // Merge the temporary arrays back into items
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            GroceryItem leftItem = leftArray.get(i);
            GroceryItem rightItem = rightArray.get(j);

            // Compare based on the single criterion
            int comparison = compareItems(leftItem, rightItem, criterion);
            System.out.println("MergeServlet: Comparison (" + criterion + ") - " + getItemKey(leftItem, criterion) + " vs " + getItemKey(rightItem, criterion) + " -> " + comparison);

            if (comparison <= 0) {
                items.set(k++, leftArray.get(i++));
            } else {
                items.set(k++, rightArray.get(j++));
            }
        }

        // Copy remaining elements of leftArray
        while (i < n1) {
            System.out.println("MergeServlet: Copying remaining left item at index " + i);
            items.set(k++, leftArray.get(i++));
        }

        // Copy remaining elements of rightArray
        while (j < n2) {
            System.out.println("MergeServlet: Copying remaining right item at index " + j);
            items.set(k++, rightArray.get(j++));
        }

        System.out.println("MergeServlet: Finished merging, items in range [" + left + "," + right + "]: " + items.subList(left, right + 1));
    }

    // Helper method to compare two GroceryItems based on a criterion
    private int compareItems(GroceryItem item1, GroceryItem item2, SortCriterion criterion) {
        switch (criterion) {
            case NAME:
                String name1 = item1.getProductName() != null ? item1.getProductName() : "";
                String name2 = item2.getProductName() != null ? item2.getProductName() : "";
                return name1.compareToIgnoreCase(name2);
            case PRICE:
                return Double.compare(item1.getProductPrice(), item2.getProductPrice());
            default:
                return 0; // Should not happen
        }
    }

    // Helper method to get the key for logging
    private String getItemKey(GroceryItem item, SortCriterion criterion) {
        switch (criterion) {
            case NAME:
                return item.getProductName() != null ? item.getProductName() : "null";
            case PRICE:
                return String.valueOf(item.getProductPrice());
            default:
                return "";
        }
    }

    // Public method to sort the items list with a single criterion
    public void sortItems(ArrayList<GroceryItem> items, SortCriterion criterion) {
        if (items == null || items.isEmpty()) {
            System.out.println("MergeServlet: Items list is null or empty, skipping sort");
            return;
        }
        System.out.println("MergeServlet: Applying Merge Sort with criterion=" + criterion);
        mergeSort(items, 0, items.size() - 1, criterion);
        System.out.println("MergeServlet: Items after Merge Sort: " + items);
    }

    // Default sort method (for backward compatibility)
    public void sortItems(ArrayList<GroceryItem> items) {
        sortItems(items, SortCriterion.NAME); // Default to sorting by name
    }
}