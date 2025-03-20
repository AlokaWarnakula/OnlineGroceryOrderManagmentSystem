package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import model.GroceryItem;

import java.util.ArrayList;

public class MergeServlet extends HttpServlet {

    // Merge sort start from here
    public void mergeSort(ArrayList<GroceryItem> items, int left, int right) {
        System.out.println("MergeServlet: Starting mergeSort with left=" + left + ", right=" + right + ", items size=" + items.size());
        if (left < right) {
            int mid = (left + right) / 2;
            System.out.println("MergeServlet: Splitting at mid=" + mid);
            mergeSort(items, left, mid);
            mergeSort(items, mid + 1, right);
            merge(items, left, mid, right);
        }
        System.out.println("MergeServlet: Finished mergeSort for range left=" + left + ", right=" + right);
    }

    private void merge(ArrayList<GroceryItem> items, int left, int mid, int right) {
        System.out.println("MergeServlet: Merging from left=" + left + ", mid=" + mid + ", right=" + right);
        ArrayList<GroceryItem> temp = new ArrayList<>(items.subList(left, right + 1));
        int i = 0, j = mid - left + 1, k = left;

        while (i < mid - left + 1 && j < temp.size()) {
            // Compare by category first
            int categoryComparison = temp.get(i).getProductCategory().compareToIgnoreCase(temp.get(j).getProductCategory());
            System.out.println("MergeServlet: Comparing categories - " + temp.get(i).getProductCategory() + " vs " + temp.get(j).getProductCategory() + " -> " + categoryComparison);
            if (categoryComparison < 0) {
                items.set(k++, temp.get(i++));
            } else if (categoryComparison > 0) {
                items.set(k++, temp.get(j++));
            } else {
                // If categories are the same, compare by price
                double price1 = temp.get(i).getProductPrice();
                double price2 = temp.get(j).getProductPrice();
                System.out.println("MergeServlet: Categories match, comparing prices - " + price1 + " vs " + price2);
                if (price1 <= price2) {
                    items.set(k++, temp.get(i++));
                } else {
                    items.set(k++, temp.get(j++));
                }
            }
        }

        while (i < mid - left + 1) {
            System.out.println("MergeServlet: Copying remaining left item at index " + i);
            items.set(k++, temp.get(i++));
        }
        while (j < temp.size()) {
            System.out.println("MergeServlet: Copying remaining right item at index " + j);
            items.set(k++, temp.get(j++));
        }
        System.out.println("MergeServlet: Finished merging, items in range [" + left + "," + right + "]: " + items.subList(left, right + 1));
    }

    // Public method to sort the items list
    public void sortItems(ArrayList<GroceryItem> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("MergeServlet: Items list is null or empty, skipping sort");
            return;
        }
        System.out.println("MergeServlet: Applying Merge Sort to sort by category and price");
        mergeSort(items, 0, items.size() - 1);
        System.out.println("MergeServlet: Items after Merge Sort: " + items);
    }
}