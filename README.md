### `README.md` for `OnlineGroceryOrderSystem`

```markdown
# Online Grocery Order Management System

![Project Code: PGNO-278](https://img.shields.io/badge/Project-PGNO--278-blue)

The **Online Grocery Order Management System** is a web-based application designed to manage grocery orders efficiently. It allows users to browse products, add them to a cart, place orders, and manage their profiles. Admins can view order statistics, including total orders, delivered orders, and cancelled orders. The system includes features like a queue to process customer orders sequentially and a Merge Sort algorithm to sort products by category or price.

## Features
- **User Features**:
  - Browse products by category.
  - Add products to cart and proceed to checkout.
  - View and manage user profile.
  - Cancel orders (via `OrderCancel.jsp`).
- **Admin Features**:
  - View dashboard with order statistics (total orders, delivered, cancelled).
  - Manage admin profile with simplified UI.
- **System Features**:
  - Queue to process customer orders sequentially.
  - Merge Sort algorithm to sort products by category or price (via `MergeServlet.java`).
  - File-based storage for users, admins, products, carts, and orders.

## Project Structure
```
OnlineGroceryOrderSystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── model/
│   │   │   │   ├── Admin.java             # Class to represent an admin (with role)
│   │   │   │   ├── FileUtil.java          # Utility class for file operations
│   │   │   │   ├── GroceryItem.java       # Defines product attributes
│   │   │   │   └── User.java              # Defines user attributes
│   │   │   ├── servlet/
│   │   │   │   ├── AdminServlet.java      # Servlet for the main admin page
│   │   │   │   ├── CartServlet.java       # Manages cart operations
│   │   │   │   ├── LoginServlet.java      # Handles user and admin login
│   │   │   │   ├── LogoutServlet.java     # Handles user and admin logout
│   │   │   │   ├── MergeServlet.java      # Implements Merge Sort for sorting products
│   │   │   │   ├── OrderServlet.java      # Finalizes checkout and saves orders
│   │   │   │   ├── ProductDetailServlet.java # Fetches detailed info for a single product
│   │   │   │   ├── RegisterServlet.java   # Manages user registration
│   │   │   │   ├── SearchServlet.java     # Processes product search queries
│   │   │   │   ├── StockServlet.java      # Retrieves products by category
│   │   │   │   ├── UserProfileSearchServlet.java # Manages user profile search operations
│   │   │   │   └── UserProfileServlet.java # Manages user profile operations
│   │   │   └── resources/
│   │   └── webapp/
│   │       ├── adminPages/
│   │       │   ├── adminPage.jsp          # Main admin page with order statistics
│   │       │   └── adminSuccessful.jsp    # Success page after admin login
│   │       ├── cartAndOrders/
│   │       │   ├── cartIndex.jsp          # Shopping page displaying products by category
│   │       │   ├── checkOut.jsp           # Checkout page
│   │       │   ├── processCheckOut.jsp    # Page to process checkout
│   │       │   └── product-details.jsp    # Page for displaying individual product details
│   │       ├── css/
│   │       │   ├── cart.css               # Styles for cart-related pages
│   │       │   ├── checkOut.css           # Styles for checkout page
│   │       │   ├── index.css              # Styles for the main landing page
│   │       │   ├── login.css              # Styles for login page
│   │       │   ├── processCheckOut.css    # Styles for process checkout page
│   │       │   └── userProfile.css        # Styles for user profile page
│   │       ├── data/
│   │       │   ├── admins.txt             # Stores admin credentials (with role)
│   │       │   ├── cart.txt               # Stores cart data
│   │       │   ├── items.txt              # Stores product data
│   │       │   ├── loggedInUser.txt       # Stores the currently logged-in user
│   │       │   ├── orders.txt             # Stores order data
│   │       │   └── users.txt              # Stores user data
│   │       ├── js/
│   │       │   ├── cart.js                # JavaScript for cart functionality
│   │       │   ├── checkOut.js            # JavaScript for checkout functionality
│   │       │   ├── index.js               # JavaScript for the main landing page
│   │       │   └── userProfile.js         # JavaScript for user profile functionality
│   │       ├── userLogin/
│   │       │   ├── login.jsp              # Login page
│   │       │   ├── OrderCancel.jsp        # Cancel order page showing cart items
│   │       │   ├── Success.jsp            # Success page after login/registration
│   │       │   └── userProfile.jsp        # User profile page
│   │       ├── WEB-INF/
│   │       │   └── web.xml                # Deployment descriptor for servlet mappings
│   │       └── index.jsp                  # Main landing page
│   └── test/
│       └── java/
│           └── (unit tests, if any)
├── pom.xml                                    # Maven configuration file with dependencies
└── README.md                                  # Project documentation
```

## Prerequisites
- **Java**: JDK 11 or higher
- **Maven**: For dependency management and building the project
- **Servlet Container**: Apache Tomcat 10.x (or another Jakarta EE 9-compatible server)
- **IDE**: IntelliJ IDEA, Eclipse, or any IDE with Maven support (optional but recommended)

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/OnlineGroceryOrderSystem.git
   cd OnlineGroceryOrderSystem
   ```

2. **Build the Project**:
   - Ensure Maven is installed.
   - Run the following command to build the project and download dependencies:
     ```bash
     mvn clean install
     ```

3. **Configure the Servlet Container**:
   - Deploy the generated WAR file (`target/OnlineGroceryOrderSystem.war`) to your Tomcat server.
   - Alternatively, if using an IDE like IntelliJ IDEA, configure a Tomcat run configuration and point it to the project.

4. **Update File Paths**:
   - The project uses file-based storage in the `src/main/webapp/data/` directory.
   - Update the file paths in `AdminServlet.java`, `OrderServlet.java`, and other servlets to match your local environment. For example:
     ```java
     String basePath = "/path/to/your/project/OnlineGroceryOrderSystem/src/main/webapp/data/";
     ```

5. **Run the Application**:
   - Start your Tomcat server.
   - Access the application at `http://localhost:8080/OnlineGroceryOrderSystem`.

## Usage
### Admin Access
- **Login Credentials**:
  - Email: `admin1@gmail.admin`
  - Password: `admin123`
- **Dashboard**:
  - View total orders, delivered orders, and cancelled orders.
  - "Orders" count is based on the total number of orders in `orders.txt` (filtered by `orderStatus=pending` in the latest version).
  - "Cancelled" count is based on `orderStatus=cancelled` across both `orders.txt` and `deliveredOrders.txt`.
  - "Delivered" count is based on `deliveryStatus=delivered` in `deliveredOrders.txt`.

### User Access
- Register a new user via the registration page.
- Log in to browse products, add to cart, and place orders.
- Cancel orders via `OrderCancel.jsp`.

## Recent Updates
- **Standardized Spelling**:
  - Standardized on British spelling (`cancelled`) for all instances of "cancel" across the codebase (`AdminServlet.java`, `adminPage.jsp`, and guidance provided for `OrderServlet.java` and `FileUtil.java`).
- **Admin Dashboard Improvements**:
  - Simplified the admin profile container in `adminPage.jsp` to reduce the number of lines while maintaining styling.
  - Ensured the "Cancelled" count correctly displays 17 by fixing attribute name mismatches and spelling inconsistencies.
- **Explicit Use of `orderStatus`**:
  - Updated the "Orders" count in `AdminServlet.java` to explicitly use `orderStatus=pending` for consistency with the "Cancelled" count, which also uses `orderStatus`.

## Contributing
1. Fork the repository.
2. Create a new branch for your feature or bug fix:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your commit message here"
   ```
4. Push to your branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Create a pull request on GitHub.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact
For any inquiries, please contact [your-email@example.com](mailto:your-email@example.com).
```

