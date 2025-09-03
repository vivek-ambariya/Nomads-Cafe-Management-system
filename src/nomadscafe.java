import datastructures.DataStructures;

import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import javax.mail.*;
import javax.mail.internet.*;

class Order {
    int orderId;
    int itemId;
    int quantity;
    String username;
    LocalDateTime timestamp;
    String status;

    public Order(int itemId, int quantity, String username) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.username = username;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    @Override
    public String toString() {
        return "OrderID: " + orderId +
                " | User: " + username +
                " | ItemID: " + itemId +
                " | Qty: " + quantity +
                " | Time: " + timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                " | Status: " + status;
    }
}
class MenuItem {
    int id;
    String name;
    String category;
    double price;
    boolean availability;
    int popularity;

    public MenuItem(int id, String name, String category, double price, boolean availability) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.availability = availability;
        this.popularity = 0;
    }

    @Override

    public String toString() {
        int availInt = availability ? 1 : 0;  // Convert boolean to 1 or 0
        return String.format("ID: %d | Name: %-20s | Category: %-15s | Price: %6.2f | availability: %d | Popularity: %d",
                id, name, category, price, availInt, popularity);
    }

}
class SpecialOffer {
    String type; // "HAPPY_HOUR", "CATEGORY"
    int discountPercent;
    boolean isActive = true;
}

class HappyHourOffer extends SpecialOffer {
    LocalTime startTime;
    LocalTime endTime;
    DataStructures.CustomHashSet<DayOfWeek> days;
}

class CategoryOffer extends SpecialOffer {
    String category;
}
class Cafe2 {
    static Connection con;
    static Statement st;
    static DataStructures.CustomLinkedList<Order> orderQueue = new DataStructures.CustomLinkedList<>();
    static Scanner scanner = new Scanner(System.in);
    static DataStructures.CustomHashMap<Integer, MenuItem> menuMap = new DataStructures.CustomHashMap<>();
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final String MANAGER_PASSWORD = "Cafe@123";

    // Email configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "nomadscafe247@gmail.com";
    private static final String EMAIL_PASSWORD = "ktiz jyym lndg gacf";
    private static final boolean USE_SSL = true;


    static List<HappyHourOffer> happyHourOffers = new ArrayList<>();
    static List<CategoryOffer> categoryOffers = new ArrayList<>();


    public static void main(String[] args) {
        try {
            String url = "jdbc:mysql://localhost:3306/vivek";
            String user = "root";
            String password = "";

            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            con.setAutoCommit(false);
            initializeDatabase();
            loadMenuItems();

            while (true) {
                System.out.println("\n===== Cafe Management System =====");
                System.out.println("1. Manager Mode");
                System.out.println("2. User Mode");
                System.out.println("3. Customer Loyalty Program");
                System.out.println("4. View Popular Items");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");

                int choice;
                while (true) {
                    try {
                        choice = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                        if (choice >= 1 && choice <= 5) {
                            break;
                        } else {
                            System.out.print("Please enter a number between 1 and 5: ");
                        }
                    } catch (InputMismatchException e) {
                        System.out.print("Invalid input. Please enter a number between 1 and 5: ");
                        scanner.nextLine(); // clear invalid input
                    }
                }
                switch (choice) {
                    case 1:
                        if (authenticateManager())
                            managerMode();
                        break;
                    case 2:
                        userMode();
                        break;
                    case 3:
                        loyaltyProgram();
                        break;
                    case 4:
                        viewPopularItems();
                        break;
                    case 5:
                        System.out.println("Exiting system...");
                        return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
                if (st != null) st.close();
                if (scanner != null) scanner.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static boolean authenticateManager() {
        System.out.print("Enter Manager Password: ");
        String inputPassword = scanner.nextLine();
        if (inputPassword.equals(MANAGER_PASSWORD)) {
            return true;
        } else {
            System.out.println("Incorrect password! Access denied.");
            return false;
        }
    }

    static void initializeDatabase() throws SQLException {
        String createMenuTable = "CREATE TABLE IF NOT EXISTS menu_items (" +
                "ID INT PRIMARY KEY AUTO_INCREMENT, " +
                "Name VARCHAR(100), " +
                "Category VARCHAR(100), " +
                "Price DOUBLE, " +
                "availability BOOLEAN, " +
                "Popularity INT DEFAULT 0)";
        st.executeUpdate(createMenuTable);

        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                "OrderID INT PRIMARY KEY AUTO_INCREMENT, " +
                "ItemID INT, " +
                "Quantity INT, " +
                "Username VARCHAR(100), " +
                "OrderTime DATETIME, " +
                "Status VARCHAR(50))";
        st.executeUpdate(createOrdersTable);

        String createLoyaltyTable = "CREATE TABLE IF NOT EXISTS loyalty_program (" +
                "Username VARCHAR(100) PRIMARY KEY, " +
                "Points INT DEFAULT 0, " +
                "LastVisit DATE)";
        st.executeUpdate(createLoyaltyTable);

        String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                "Username VARCHAR(100) PRIMARY KEY, " +
                "Email VARCHAR(100) UNIQUE, " +
                "Verified BOOLEAN DEFAULT false, " +
                "LastLogin TIMESTAMP)";
        st.executeUpdate(createUserTable);
    }
    static void userMode() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Get username
        String username;
        while (true) {
            System.out.print("Enter your name: ");
            username = scanner.nextLine().trim();
            if (!username.isEmpty()) {
                break;
            }
            System.out.println("Name cannot be empty. Please try again.");
        }

        // Get email - simple validation
        String email;
        while (true) {
            System.out.print("Enter your email: ");
            email = scanner.nextLine().trim();

            // Basic email validation
            if (email.contains("@") && email.contains(".") && email.length() > 5) {
                break;
            }
            System.out.println("Please enter a valid email (must contain @ and .)");
        }

        // Check if user exists
        String checkUserSql = "SELECT Verified FROM users WHERE Username = ? OR Email = ?";
        try (PreparedStatement ps = con.prepareStatement(checkUserSql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Existing user
                boolean verified = rs.getBoolean("Verified");
                if (verified) {
                    // Welcome back existing verified user
                    System.out.println("\n====================================");
                    System.out.println("   Welcome back, " + username + "!");
                    System.out.println("====================================");
                    updateLoyaltyPoints(username, 2); // Add welcome back points
                    userModeMenu(username);
                    return;
                } else {
                    // Existing but unverified user
                    System.out.println("Account not verified. Please verify your email.");
                }
            } else {
                // New user - create account
                String insertSql = "INSERT INTO users (Username, Email, Verified) VALUES (?, ?, false)";
                try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                    insertPs.setString(1, username);
                    insertPs.setString(2, email);
                    insertPs.executeUpdate();
                }
                System.out.println("New account created. Please verify your email.");
            }
        }

        // OTP verification for new/unverified users
        while (true) {
            if (verifyEmailWithOTP(username, email)) {
                // Successful verification
                System.out.println("\n====================================");
                System.out.println("   Welcome to Nomad's Cafe, " + username + "!");
                System.out.println("====================================");
                updateLoyaltyPoints(username, 5); // Add welcome points for new users
                break;
            }

            System.out.println("\nWould you like to try again? (yes/no)");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (!choice.equals("yes")) {
                return; // Exit if user doesn't want to try again
            }
        }

        userModeMenu(username);
    }

    static void userModeMenu(String username) throws SQLException {
        updateLoyaltyPoints(username, 5); // Welcome points

        while (true) {
            System.out.println("\n--- USER MODE: Welcome " + username + " ---");
            System.out.println("1. View Menu");
            System.out.println("2. Place Order");
            System.out.println("3. View Order Status");
            System.out.println("4. View Loyalty Points");
            System.out.println("5. View Current Offers");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choice: ");

            int choice;
            while (true) {
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    if (choice >= 1 && choice <= 6) {
                        break;
                    } else {
                        System.out.print("Please enter a number between 1 and 6: ");
                    }
                } catch (InputMismatchException e) {
                    System.out.print("Invalid input. Please enter a number between 1 and 6: ");
                    scanner.nextLine(); // clear invalid input
                }
            }

            switch (choice) {
                case 1:
                    displayMenu();
                    break;
                case 2:
                    placeOrder(username);
                    break;
                case 3:
                    viewOrderStatus(username);
                    break;
                case 4:
                    viewLoyaltyPoints(username);
                    break;
                case 5:
                    viewCurrentOffers();
                    break;
                case 6:
                    return;
            }
        }
    }

    static boolean verifyEmailWithOTP(String username, String email) throws SQLException {
        String otp = generateOTP();

        try {
            sendOTPEmail(email, otp);
            System.out.println("OTP sent to " + email);
        } catch (Exception e) {
            System.out.println("Failed to send OTP: " + e.getMessage());
            return false;
        }

        System.out.print("Enter the OTP sent to your email: ");
        String userOTP = scanner.nextLine();

        if (userOTP.equals(otp)) {
            // Update user as verified in database
            String updateSql = "UPDATE users SET Verified = true WHERE Username = ?";
            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setString(1, username);
                ps.executeUpdate();
            }
            System.out.println("Email verified successfully!");
            return true;
        } else {
            System.out.println("Invalid OTP.");
            return false;
        }
    }

    static String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    static void sendOTPEmail(String recipientEmail, String otp) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");


        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });


        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your OTP Code");

            message.setText(" HEY CUSTOMER...!! WELCOME TO THE NOMADS'S CAFE." +
                    "\nYour verification code is: " + otp);

            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (AuthenticationFailedException e) {
            System.err.println("Authentication failed. Check your email credentials.");
            System.err.println("Did you enable 'Less secure apps' or create an App Password?");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.err.println("Email sending failed. Full error details:");
            e.printStackTrace();
        }
    }
    static void sendOrderConfirmationEmail(String recipientEmail, String username,
                                           String itemName, int quantity,
                                           double basePrice, double totalDiscount,
                                           double finalPrice, List<String> freeItems) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Order at Nomad's Cafe");

            // Build the email content with proper discount information
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Dear ").append(username).append(",\n\n")
                    .append("Thank you for your order at Nomad's Cafe!\n\n")
                    .append("Order Details:\n");

            if (!itemName.isEmpty()) {
                emailContent.append("Item: ").append(itemName).append("\n")
                        .append("Quantity: ").append(quantity).append("\n")
                        .append("Base Price: Rs.").append(String.format("%.2f", basePrice)).append("\n");

                if (totalDiscount > 0) {
                    emailContent.append("Total Discount: Rs.").append(String.format("%.2f", totalDiscount)).append("\n")
                            .append("Discount Percentage: ").append(String.format("%.1f%%", (totalDiscount/basePrice)*100)).append("\n");
                }
            }

            if (!freeItems.isEmpty()) {
                emailContent.append("\nFree Items Included:\n");
                for (String freeItem : freeItems) {
                    emailContent.append("- ").append(freeItem).append("\n");
                }
            }

            emailContent.append("\nFinal Amount: Rs.").append(String.format("%.2f", finalPrice)).append("\n\n")
                    .append("Your order is being prepared with love by our team.\n\n")
                    .append("Greetings from all of us at Nomad's Cafe!\n")
                    .append("We appreciate your business and look forward to serving you again soon.\n\n")
                    .append("Warm regards,\n")
                    .append("The Nomad's Cafe Team\n\n")
                    .append("Did you know? You can earn loyalty points with every purchase....!\n")
                    .append("\nYEAH YOU HEARD IT TRUE...GO AND CHECK IT OUT");

            message.setText(emailContent.toString());
            Transport.send(message);
            System.out.println("Order confirmation email sent to " + recipientEmail);
        } catch (Exception e) {
            System.out.println("Failed to send order confirmation email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    static void displayMenu() {
        System.out.println("\n--- MENU (sorted by category) ---");
        menuMap.values().stream()
                .filter(item -> item.availability)
                .sorted(Comparator.comparing((MenuItem item) -> item.category)
                        .thenComparing(item -> item.name))
                .forEach(System.out::println);
    }
    static void placeOrder(String username) throws SQLException {
        displayMenu();

        // Check for free items from loyalty program
        boolean hasFreeCoffee = checkForFreeCoffee(username);
        boolean hasFreeMeal = checkForFreeMeal(username);

        // Regular order
        System.out.print("Enter Item ID to order (or 0 if only claiming free item): ");
        int itemId;
        while (true) {
            try {
                itemId = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (itemId == 0 || menuMap.containsKey(itemId)) {
                    break;
                } else {
                    System.out.print("Invalid Item ID! Please enter a valid ID: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a numeric ID: ");
                scanner.nextLine(); // clear invalid input
            }
        }

        int quantity = 0;
        MenuItem item = null;
        double orderValue = 0;
        int pointsEarned = 0; // Declare pointsEarned here

        if (itemId != 0) {
            item = menuMap.get(itemId);
            if (!item.availability) {
                System.out.println("This item is currently unavailable!");
                return;
            }

            System.out.print("Enter Quantity: ");
            while (true) {
                try {
                    quantity = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    if (quantity > 0) {
                        break;
                    } else {
                        System.out.print("Quantity must be positive. Please enter again: ");
                    }
                } catch (InputMismatchException e) {
                    System.out.print("Invalid input! Please enter a positive number: ");
                    scanner.nextLine(); // clear invalid input
                }
            }
            orderValue = item.price * quantity;
        }

        // Calculate the bill with discounts
        double basePrice = orderValue;
        double happyHourDiscount = calculateHappyHourDiscount(basePrice);
        double categoryDiscount = calculateCategoryDiscount(item != null ? item.category : "", basePrice);

        double loyaltyDiscount = calculateLoyaltyDiscount(username, basePrice);
        double totalDiscount = happyHourDiscount + categoryDiscount + loyaltyDiscount;
        double finalPrice = basePrice - totalDiscount;

        // Add free items to order
        List<String> freeItems = new ArrayList<>();
        if (hasFreeCoffee) {
            freeItems.add("Free Coffee");
            // Mark free coffee as used
            redeemFreeCoffee(username);
        }
        if (hasFreeMeal) {
            freeItems.add("Free Meal");
            // Mark free meal as used
            redeemFreeMeal(username);
        }

        // Generate and display bill
        generateBill(itemId, quantity, username, freeItems);

        if (item != null) {
            item.popularity += quantity;
            String updatePopularity = "UPDATE menu_items SET Popularity = ? WHERE ID = ?";
            try (PreparedStatement ps = con.prepareStatement(updatePopularity)) {
                ps.setInt(1, item.popularity);
                ps.setInt(2, itemId);
                ps.executeUpdate();
            }
        }

        if (itemId != 0 || !freeItems.isEmpty()) {
            Order newOrder = new Order(itemId, quantity, username);
            orderQueue.add(newOrder);
            processOrders();
            logOrderToFile(itemId, quantity, username, freeItems);

            if (itemId != 0) {
                // Calculate loyalty points based on final order value
                pointsEarned = calculateLoyaltyPoints(finalPrice); // Now pointsEarned is declared
                updateLoyaltyPoints(username, pointsEarned);
            }

            // Send order confirmation email with discount and free items details
            try {
                String emailSql = "SELECT Email FROM users WHERE Username = ?";
                try (PreparedStatement emailPs = con.prepareStatement(emailSql)) {
                    emailPs.setString(1, username);
                    ResultSet rs = emailPs.executeQuery();
                    if (rs.next()) {
                        String recipientEmail = rs.getString("Email");
                        sendOrderConfirmationEmail(recipientEmail, username,
                                item != null ? item.name : "",
                                quantity,
                                basePrice, totalDiscount,
                                finalPrice, freeItems);
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not send confirmation email: " + e.getMessage());
            }

            System.out.println("Order added to queue. Your loyalty points have been updated...!");
            if (itemId != 0) {
                System.out.println(pointsEarned + " points has been added for this order (value: Rs." + finalPrice + ")");
            }
        }
    }
    static void processOrders() throws SQLException {
        try {
            while (!orderQueue.isEmpty()) {
                Order order = orderQueue.poll();
                String sql = "INSERT INTO orders (ItemID, Quantity, Username, OrderTime, Status) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, order.itemId);
                    ps.setInt(2, order.quantity);
                    ps.setString(3, order.username);
                    ps.setTimestamp(4, Timestamp.valueOf(order.timestamp));
                    ps.setString(5, "PENDING");
                    ps.executeUpdate();
                }
            }
            con.commit();
            System.out.println("Orders successfully saved to database!");
        } catch (SQLException e) {
            con.rollback();
            System.out.println("Error saving orders: " + e.getMessage());
            throw e;
        }
    }
    static int calculateLoyaltyPoints(double orderValue) {
        if (orderValue > 200) {
            return 5; // 5 points for orders over Rs. 200
        } else if (orderValue > 150) {
            return 4; // 4 points for orders over Rs. 150
        } else if (orderValue > 100) {
            return 3; // 3 points for orders over Rs. 100
        } else if (orderValue > 50) {
            return 2; // 2 points for orders over Rs. 50
        } else {
            return 1; // 1 point for all other orders
        }
    }
    // Helper methods for free items
    static boolean checkForFreeCoffee(String username) throws SQLException {
        String sql = "SELECT Points FROM loyalty_program WHERE Username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("Points") >= 50;
        }
    }

    static boolean checkForFreeMeal(String username) throws SQLException {
        String sql = "SELECT Points FROM loyalty_program WHERE Username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("Points") >= 200;
        }
    }
    static double calculateLoyaltyDiscount(String username, double basePrice) throws SQLException {
        String sql = "SELECT Points FROM loyalty_program WHERE Username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int points = rs.getInt("Points");
                if (points >= 100) {
                    return basePrice * 0.20; // 20% discount for 100+ points
                }
            }
        }
        return 0;
    }
    static void redeemFreeCoffee(String username) throws SQLException {
        String sql = "UPDATE loyalty_program SET Points = Points - 50 WHERE Username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    static void redeemFreeMeal(String username) throws SQLException {
        String sql = "UPDATE loyalty_program SET Points = Points - 200 WHERE Username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    static void loyaltyProgram() throws SQLException {
        System.out.println("\n--- LOYALTY PROGRAM ---");
        System.out.print("Enter customer name: ");
        String username = scanner.nextLine();

        String sql = "SELECT Points, LastVisit FROM loyalty_program WHERE Username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int points = rs.getInt("Points");
                Date lastVisit = rs.getDate("LastVisit");
                System.out.printf("Customer: %s\nPoints: %d\nLast Visit: %s\n",
                        username, points, lastVisit.toString());

                System.out.println("\nRewards available:");
                if (points >= 50) System.out.println("- Free Coffee (50 points)");
                if (points >= 100) System.out.println("- 20% Discount (100 points)");
                if (points >= 200) System.out.println("- Free Meal (200 points)");
            } else {
                System.out.println("No record found for " + username);
            }
        }
    }

    static void updateLoyaltyPoints(String username, int pointsToAdd) throws SQLException {
        String checkSql = "SELECT Points FROM loyalty_program WHERE Username = ?";
        String updateSql = "UPDATE loyalty_program SET Points = ?, LastVisit = ? WHERE Username = ?";
        String insertSql = "INSERT INTO loyalty_program (Username, Points, LastVisit) VALUES (?, ?, ?)";

        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, username);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                int currentPoints = rs.getInt("Points");
                try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                    updatePs.setInt(1, currentPoints + pointsToAdd);
                    updatePs.setDate(2, Date.valueOf(LocalDate.now()));
                    updatePs.setString(3, username);
                    updatePs.executeUpdate();
                }
            } else {
                try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                    insertPs.setString(1, username);
                    insertPs.setInt(2, pointsToAdd);
                    insertPs.setDate(3, Date.valueOf(LocalDate.now()));
                    insertPs.executeUpdate();
                }
            }
        }
    }
    static void viewOrderStatus(String username) throws SQLException {
        System.out.println("\n--- YOUR ORDER STATUS ---");
        String sql = "SELECT o.*, m.Name FROM orders o JOIN menu_items m ON o.ItemID = m.ID " +
                "WHERE Username = ? ORDER BY OrderTime DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                System.out.printf("Order #%d | %-20s | Qty: %-3d | Rs.%-6.2f | %-8s | %s\n",
                        rs.getInt("OrderID"),
                        rs.getString("Name"),
                        rs.getInt("Quantity"),
                        menuMap.get(rs.getInt("ItemID")).price * rs.getInt("Quantity"),
                        rs.getString("Status"),
                        rs.getTimestamp("OrderTime").toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }

            if (!hasOrders) {
                System.out.println("No orders found for " + username);
            }
        }
    }

    static void viewLoyaltyPoints(String username) throws SQLException {
        String sql = "SELECT Points FROM loyalty_program WHERE Username = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int points = rs.getInt("Points");
                System.out.println("\n--- YOUR LOYALTY POINTS ---");
                System.out.println("Customer: " + username);
                System.out.println("Points: " + points);

                // Display points earning structure
                System.out.println("\nPoints Earning Structure:");
                System.out.println("- Rs. 0-50: 1 point");
                System.out.println("- Rs. 51-100: 2 points");
                System.out.println("- Rs. 101-150: 3 points");
                System.out.println("- Rs. 151-200: 4 points");
                System.out.println("- Rs. 200+: 5 points");

                // Check and redeem rewards in sequence
                boolean continueChecking = true;
                while (continueChecking) {
                    if (points >= 50 && points < 100) {
                        System.out.println("\nHurray! You've won a FREE COFFEE!");
                        System.out.print("Would you like to redeem your 50 points for a free coffee? (yes/no): ");
                        String choice = scanner.nextLine().trim().toLowerCase();

                        if (choice.equals("yes")) {
                            points -= 50;
                            updatePointsInDatabase(username, points);
                            System.out.println("Congratulations! Your free coffee has been redeemed!");
                            System.out.println("Remaining points: " + points);
                        }
                        System.out.println("\nNext reward: 20% Discount at 100 points");
                        continueChecking = false;
                    }
                    else if (points >= 100 && points < 200) {
                        System.out.println("\nHurray! You've earned a 20% DISCOUNT!");
                        System.out.print("Would you like to redeem your 100 points for a 20% discount? (yes/no): ");
                        String choice = scanner.nextLine().trim().toLowerCase();

                        if (choice.equals("yes")) {
                            points -= 100;
                            updatePointsInDatabase(username, points);
                            System.out.println("Congratulations! Your 20% discount has been applied to your next order!");
                            System.out.println("Remaining points: " + points);
                        }
                        System.out.println("\nNext reward: Free Meal at 200 points");
                        continueChecking = false;
                    }
                    else if (points >= 200) {
                        System.out.println("\nHurray! You've earned a FREE MEAL!");
                        System.out.print("Would you like to redeem your 200 points for a free meal? (yes/no): ");
                        String choice = scanner.nextLine().trim().toLowerCase();

                        if (choice.equals("yes")) {
                            points -= 200;
                            updatePointsInDatabase(username, points);
                            System.out.println("Congratulations! Your free meal has been redeemed!");
                            System.out.println("Remaining points: " + points);
                        }
                        System.out.println("\nYou've reached the maximum reward level!");
                        continueChecking = false;
                    }
                    else {
                        System.out.println("\nKeep ordering to earn more points!");
                        continueChecking = false;
                    }
                }
            } else {
                System.out.println("No loyalty points found. Start ordering to earn points!");
            }
        }
    }
    // manager mode methods
    static void loadMenuItems() throws SQLException {
        menuMap.clear();
        ResultSet rs = st.executeQuery("SELECT * FROM menu_items");
        while (rs.next()) {
            int id = rs.getInt("ID");
            String name = rs.getString("Name");
            String category = rs.getString("Category");
            double price = rs.getDouble("Price");
            boolean availability = rs.getBoolean("availability");
            int popularity = rs.getInt("Popularity");

            MenuItem item = new MenuItem(id, name, category, price, availability);
            item.popularity = popularity;
            menuMap.put(id, item);
        }
    }
    static void managerMode() throws SQLException {
        while (true) {
            System.out.println("\n--- MANAGER MODE ---");
            System.out.println("1. Menu Management");
            System.out.println("2. Order Management");
            System.out.println("3. Generate sales report");
            System.out.println("4. Manage Special Offers");
            System.out.println("5. View Loyalty Program Report");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choice: ");

            int choice;
            while (true) {
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    if (choice >= 1 && choice <= 6) {
                        break;
                    } else {
                        System.out.print("Please enter a number between 1 and 6: ");
                    }
                } catch (InputMismatchException e) {
                    System.out.print("Invalid input. Please enter a number between 1 and 6: ");
                    scanner.nextLine();
                }
            }

            switch (choice) {
                case 1:
                    menuManagement();
                    break;
                case 2:
                    orderManagement();
                    break;
                case 3:
                    generateSalesReport();

                    break;
                case 4:
                    specialOffers();
                    break;
                case 5:
                    loyaltyProgramReport();
                    break;
                case 6:
                    return;

            }
        }
    }

    static void menuManagement() throws SQLException {
        while (true) {
            System.out.println("\n--- MENU MANAGEMENT ---");
            System.out.println("1. Add Menu Item");
            System.out.println("2. Update Menu Item");
            System.out.println("3. Delete Menu Item");
            System.out.println("4. View All Menu Items");
            System.out.println("5. Bulk Update Availability");
            System.out.println("6. Back to Manager Menu");
            System.out.print("Choice: ");

            int choice;
            while (true) {
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    if (choice >= 1 && choice <= 6) {
                        break;
                    } else {
                        System.out.print("Please enter a number between 1 and 6: ");
                    }
                } catch (InputMismatchException e) {
                    System.out.print("Invalid input. Please enter a number between 1 and 6: ");
                    scanner.nextLine();
                }
            }

            switch (choice) {
                case 1:
                    addMenuItem();
                    break;
                case 2:
                    updateMenuItem();
                    break;
                case 3:
                    deleteMenuItem();
                    break;
                case 4:
                    displayMenu();
                    break;
                case 5:
                    bulkUpdateAvailability();
                    break;
                case 6:
                    return;
            }
        }
    }

    static void addMenuItem() throws SQLException {
        System.out.print("Enter Item Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Category: ");
        String category = scanner.nextLine();

        System.out.print("Enter Price: ");
        double price;
        while (true) {
            try {
                price = scanner.nextDouble();
                scanner.nextLine(); // consume newline
                if (price >= 0) {
                    break;
                } else {
                    System.out.print("Price cannot be negative. Please enter again: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a number: ");
                scanner.nextLine(); // clear invalid input
            }
        }

        System.out.print("Is available (true/false): ");
        boolean availability;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                availability = Boolean.parseBoolean(input);
                break;
            }
            System.out.print("Please enter 'true' or 'false': ");
        }

        String sql = "INSERT INTO menu_items (ID, Name, Category, Price, availability) " +
                "VALUES ((SELECT IFNULL(MAX(ID), 0) + 1 FROM menu_items m), ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setBoolean(4, availability);
            ps.executeUpdate();
        }

        loadMenuItems();
        System.out.println("Menu item added successfully!");
    }

    static void deleteMenuItem() throws SQLException {
        System.out.println("\n--- DELETE MENU ITEM ---");
        displayMenu();

        System.out.print("Enter Item ID to delete: ");
        int itemId;
        while (true) {
            try {
                itemId = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (menuMap.containsKey(itemId)) {
                    break;
                } else {
                    System.out.print("Item ID not found! Please enter a valid ID: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a numeric ID: ");
                scanner.nextLine(); // clear invalid input
            }
        }
        String checkOrdersSql = "SELECT COUNT(*) FROM orders WHERE ItemID = ?";
        try (PreparedStatement ps = con.prepareStatement(checkOrdersSql)) {
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Cannot delete this item because it has existing orders!");
                System.out.println("You can set it as unavailable instead.");
                return;
            }
        }

        System.out.print("Are you sure you want to delete item ID " + itemId + "? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (!confirmation.equals("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String sql = "DELETE FROM menu_items WHERE ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                menuMap.remove(itemId);
                System.out.println("Menu item deleted successfully!");
            } else {
                System.out.println("Failed to delete menu item.");
            }
        }
    }

    static void updateMenuItem() throws SQLException {
        System.out.println("\n--- UPDATE MENU ITEM ---");
        displayMenu();

        System.out.print("Enter Item ID to update: ");
        int itemId;
        while (true) {
            try {
                itemId = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (menuMap.containsKey(itemId)) {
                    break;
                } else {
                    System.out.print("Item ID not found! Please enter a valid ID: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a numeric ID: ");
                scanner.nextLine(); // clear invalid input
            }
        }
        MenuItem item = menuMap.get(itemId);
        System.out.println("\nCurrent Details:");
        System.out.println(item);

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Category");
        System.out.println("3. Price");
        System.out.println("4. Availability");
        System.out.println("5. All Fields");
        System.out.println("6. Cancel Update");
        System.out.print("Choice: ");

        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (choice >= 1 && choice <= 6) {
                    break;
                } else {
                    System.out.print("Please enter a number between 1 and 6: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number between 1 and 6: ");
                scanner.nextLine(); // clear invalid input
            }
        }

        String name = item.name;
        String category = item.category;
        double price = item.price;
        boolean availability = item.availability;

        switch (choice) {
            case 1:
                System.out.print("Enter new Name: ");
                name = scanner.nextLine();
                break;
            case 2:
                System.out.print("Enter new Category: ");
                category = scanner.nextLine();
                break;
            case 3:
                System.out.print("Enter new Price: ");
                while (true) {
                    try {
                        price = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        if (price >= 0) {
                            break;
                        } else {
                            System.out.print("Price cannot be negative. Please enter again: ");
                        }
                    } catch (InputMismatchException e) {
                        System.out.print("Invalid input! Please enter a number: ");
                        scanner.nextLine(); // clear invalid input
                    }
                }
                break;
            case 4:
                System.out.print("Set Availability (true/false): ");
                while (true) {
                    String input = scanner.nextLine().trim().toLowerCase();
                    if (input.equals("true") || input.equals("false")) {
                        availability = Boolean.parseBoolean(input);
                        break;
                    }
                    System.out.print("Please enter 'true' or 'false': ");
                }
                break;
            case 5:
                System.out.print("Enter new Name: ");
                name = scanner.nextLine();
                System.out.print("Enter new Category: ");
                category = scanner.nextLine();
                System.out.print("Enter new Price: ");
                while (true) {
                    try {
                        price = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        if (price >= 0) {
                            break;
                        } else {
                            System.out.print("Price cannot be negative. Please enter again: ");
                        }
                    } catch (InputMismatchException e) {
                        System.out.print("Invalid input! Please enter a number: ");
                        scanner.nextLine(); // clear invalid input
                    }
                }
                System.out.print("Set Availability (true/false): ");
                while (true) {
                    String input = scanner.nextLine().trim().toLowerCase();
                    if (input.equals("true") || input.equals("false")) {
                        availability = Boolean.parseBoolean(input);
                        break;
                    }
                    System.out.print("Please enter 'true' or 'false': ");
                }
                break;
            case 6:
                System.out.println("Update cancelled.");
                return;
        }

        String sql = "UPDATE menu_items SET Name = ?, Category = ?, Price = ?, availability = ? WHERE ID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setBoolean(4, availability);
            ps.setInt(5, itemId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                item.name = name;
                item.category = category;
                item.price = price;
                item.availability = availability;
                System.out.println("Menu item updated successfully!");
            } else {
                System.out.println("Failed to update menu item.");
            }
        }
    }
    //bulk update
    static void bulkUpdateAvailability() throws SQLException {
        System.out.print("Enter category to update (or 'all' for all items): ");
        String category = scanner.nextLine().trim();

        System.out.print("Set availability to (true/false): ");
        boolean availability;
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                availability = Boolean.parseBoolean(input);
                break;
            } else {
                System.out.print("Invalid input. Please enter 'true' or 'false': ");
            }
        }

        String sql;
        if ("all".equalsIgnoreCase(category)) {
            sql = "UPDATE menu_items SET availability = ?";
        } else {
            sql = "UPDATE menu_items SET availability = ? WHERE Category = ?";
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, availability);
            if (!"all".equalsIgnoreCase(category)) {
                ps.setString(2, category);
            }
            int count = ps.executeUpdate();
            System.out.println("Updated " + count + " items.");
            // Commit the changes for the update to take effect in the database
            con.commit();
            // Reload the menu items to reflect updated availability in memory
            loadMenuItems();
        } catch (SQLException e) {
            con.rollback();
            System.out.println("Error during bulk update: " + e.getMessage());
            throw e;
        }
    }


    static void orderManagement() throws SQLException {
        while (true) {
            System.out.println("\n--- ORDER MANAGEMENT ---");
            System.out.println("1. View All Orders");
            System.out.println("2. View Pending Orders");
            System.out.println("3. Mark Order as Completed");
            System.out.println("4. Cancel Order");
            System.out.println("5. Back to Manager Menu");
            System.out.print("Choice: ");

            int choice;
            while (true) {
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    if (choice >= 1 && choice <= 5) {
                        break;
                    } else {
                        System.out.print("Please enter a number between 1 and 5: ");
                    }
                } catch (InputMismatchException e) {
                    System.out.print("Invalid input. Please enter a number between 1 and 5: ");
                    scanner.nextLine(); // clear invalid input
                }
            }

            switch (choice) {
                case 1:
                    displayMenu();
                    break;
                case 2:
                    viewPendingOrders();
                    break;
                case 3:
                    markOrderCompleted();
                    break;
                case 4:
                    cancelOrder();
                    break;
                case 5:
                    return;
            }
        }
    }
    static void viewPendingOrders() throws SQLException {
        System.out.println("\n--- PENDING ORDERS ---");
        String sql = "SELECT o.*, m.Name FROM orders o " +
                "JOIN menu_items m ON o.ItemID = m.ID " +
                "WHERE Status = 'PENDING' " +
                "ORDER BY OrderTime ASC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-8s %-20s %-15s %-6s %-10s %-20s\n",
                    "OrderID", "Item", "Category", "Qty", "Customer", "Order Time");

            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                MenuItem item = menuMap.get(rs.getInt("ItemID"));
                System.out.printf("%-8d %-20s %-15s %-6d %-10s %-20s\n",
                        rs.getInt("OrderID"),
                        rs.getString("Name"),
                        item.category,
                        rs.getInt("Quantity"),
                        rs.getString("Username"),
                        rs.getTimestamp("OrderTime").toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("MM-dd HH:mm")));
            }

            if (!hasOrders) {
                System.out.println("No pending orders found.");
            }
        }
    }


    static void generateSalesReport() throws SQLException {
        // Start Date Validation
        LocalDate startDate = null;
        while (startDate == null) {
            System.out.print("Enter start date (YYYY-MM-DD): ");
            String startInput = scanner.nextLine();
            try {
                startDate = LocalDate.parse(startInput);
                if (startDate.isAfter(LocalDate.now())) {
                    System.out.println("Start date cannot be in the future!");
                    startDate = null;
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        // End Date Validation
        LocalDate endDate = null;
        while (endDate == null) {
            System.out.print("Enter end date (YYYY-MM-DD): ");
            String endInput = scanner.nextLine();
            try {
                endDate = LocalDate.parse(endInput);

                if (endDate.isBefore(startDate)) {
                    System.out.println("End date cannot be before start date!");
                    endDate = null;
                    continue;
                }

                if (endDate.isAfter(LocalDate.now())) {
                    System.out.println("End date cannot be in the future!");
                    endDate = null;
                }

                LocalDate oneYearLater = startDate.plusYears(1);
                if (endDate.isAfter(oneYearLater)) {
                    System.out.print("Date range exceeds 1 year! Do you want to continue? (yes/no): ");
                    String confirm = scanner.nextLine();
                    if (!confirm.equalsIgnoreCase("yes")) {
                        endDate = null;
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
        int daysBetween = 0;
        LocalDate tempDate = startDate;
        while (!tempDate.isAfter(endDate)) {
            daysBetween++;
            tempDate = tempDate.plusDays(1);
        }
        // Corrected SQL query
        String sql = "SELECT m.Name, m.Category, SUM(o.Quantity) AS TotalQty, SUM(o.Quantity * m.Price) AS TotalSales " +
                "FROM orders o JOIN menu_items m ON o.ItemID = m.ID " +
                "WHERE DATE(o.OrderTime) BETWEEN ? AND ? AND o.Status = 'COMPLETED' " +
                "GROUP BY m.Name, m.Category ORDER BY TotalSales DESC";

        try (PreparedStatement ps = con.prepareStatement(sql,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- SALES REPORT (" + startDate + " to " + endDate + ") ---");
                System.out.printf("%-20s %-15s %-10s %-10s\n", "Item", "Category", "Quantity", "Total Sales");

                double grandTotal = 0;
                while (rs.next()) {
                    String name = rs.getString("Name");
                    String category = rs.getString("Category");
                    int qty = rs.getInt("TotalQty");
                    double sales = rs.getDouble("TotalSales");
                    grandTotal += sales;
                    System.out.printf("%-20s %-15s %-10d %-9.2f\n", name, category, qty, sales);
                }

                System.out.println("-----------------------------------------");
                System.out.printf("Total Days: %d\n", daysBetween);
                System.out.printf("GRAND TOTAL: %.2f\n", grandTotal);

                // Create a new ResultSet for saving to file
                try (PreparedStatement ps2 = con.prepareStatement(sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)) {
                    ps2.setDate(1, Date.valueOf(startDate));
                    ps2.setDate(2, Date.valueOf(endDate));
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        saveReportToFile(startDate, endDate, rs2, daysBetween, grandTotal);
                    }
                }
            }
        }
    }

    private static void saveReportToFile(LocalDate startDate, LocalDate endDate,
                                         ResultSet rs, int daysBetween, double grandTotal)
            throws SQLException {
        // Create a valid filename without special characters
        String filename = "sales_report_" + startDate + "_to_" + endDate + ".txt";

        // Save to current working directory or a specific valid path
        String filePath ="C:\\reports\\"+ filename; // saves to current directory

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("SALES REPORT (" + startDate + " to " + endDate + ")");
            writer.printf("%-20s %-15s %-10s %-10s%n", "Item", "Category", "Quantity", "Total Sales");

            // Reset the ResultSet cursor to beginning
            rs.beforeFirst();
            while (rs.next()) {
                writer.printf("%-20s %-15s %-10d %-9.2f%n",
                        rs.getString("Name"),
                        rs.getString("Category"),
                        rs.getInt("TotalQty"),
                        rs.getDouble("TotalSales"));
            }

            writer.println("-----------------------------------------");
            writer.printf("Total Days: %d%n", daysBetween);
            writer.printf("GRAND TOTAL: %.2f%n", grandTotal);

            System.out.println("Report saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
    static void viewPopularItems() {
        System.out.println("\n--- MOST POPULAR ITEMS ---");
        menuMap.values().stream()
                .sorted(Comparator.comparingInt((MenuItem item) -> item.popularity).reversed())
                .limit(5)
                .forEach(System.out::println);
    }



    static void specialOffers() {
        System.out.println("\n--- SPECIAL OFFERS MANAGEMENT ---");
        System.out.println("1. Add Happy Hour Discount");
        System.out.println("2. Add Category Discount");
        System.out.println("3. View Current Offers");
        System.out.println("4. Back");
        System.out.print("Choice: ");
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (choice >= 1 && choice <=4) {
                    break;
                } else {
                    System.out.print("Please enter a number between 1 and 4: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number between 1 and 4: ");
                scanner.nextLine();
            }
        }

        switch (choice) {
            case 1: // Happy Hour
                HappyHourOffer hhOffer = new HappyHourOffer();
                hhOffer.type = "HAPPY_HOUR";

                // Discount percentage validation
                int discountPercent;
                while (true) {
                    System.out.print("Enter discount percentage (0-100): ");
                    String input = scanner.nextLine();
                    try {
                        discountPercent = Integer.parseInt(input);
                        if (discountPercent >= 0 && discountPercent <= 100) {
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter a value between 0 and 100.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a numeric value.");
                    }
                }
                hhOffer.discountPercent = discountPercent;

                // Start time validation
                LocalTime startTime;
                while (true) {
                    System.out.print("Enter start time (HH:mm): ");
                    String timeInput = scanner.nextLine();
                    try {
                        startTime = LocalTime.parse(timeInput);
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid time format. Please use HH:mm (e.g., 14:30).");
                    }
                }
                hhOffer.startTime = startTime;

                // End time validation
                LocalTime endTime;
                while (true) {
                    System.out.print("Enter end time (HH:mm): ");
                    String timeInput = scanner.nextLine();
                    try {
                        endTime = LocalTime.parse(timeInput);
                        if (endTime.isAfter(startTime)) {
                            break;
                        } else {
                            System.out.println("End time must be after start time.");
                        }
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid time format. Please use HH:mm (e.g., 15:30).");
                    }
                }
                hhOffer.endTime = endTime;

                // Days validation
                System.out.print("Enter days (comma separated, e.g., MONDAY,TUESDAY): ");
                String[] dayInput = scanner.nextLine().toUpperCase().split(",");
                hhOffer.days = new DataStructures.CustomHashSet<>();
                boolean daysValid = true;
                for (String day : dayInput) {
                    try {
                        hhOffer.days.add(DayOfWeek.valueOf(day.trim()));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid day: '" + day.trim() + "'. Please enter valid day names.");
                        daysValid = false;
                        break;
                    }
                }
                if (!daysValid) {
                    System.out.println("Happy hour offer not added due to invalid day input.");
                    break;
                }
                happyHourOffers.add(hhOffer);
                System.out.println("Happy hour offer added!");
                break;

            case 2: // Category Discount
                CategoryOffer catOffer = new CategoryOffer();
                catOffer.type = "CATEGORY";

                String cat;
                while (true) {
                    System.out.print("Enter category: ");
                    cat = scanner.nextLine().trim();
                    if (!cat.isEmpty()) break;
                    System.out.println("Category cannot be empty.");
                }
                catOffer.category = cat;

                // Discount percentage validation
                while (true) {
                    System.out.print("Enter discount percentage (0-100): ");
                    String input = scanner.nextLine();
                    try {
                        discountPercent = Integer.parseInt(input);
                        if (discountPercent >= 0 && discountPercent <= 100) {
                            break;
                        } else {
                            System.out.println("Invalid input. Please enter a value between 0 and 100.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a numeric value.");
                    }
                }
                catOffer.discountPercent = discountPercent;

                categoryOffers.add(catOffer);
                System.out.println("Category offer added!");
                break;


            case 3:
                viewCurrentOffers();
                break;
        }
    }
    static void viewCurrentOffers() {
        System.out.println("\n--- CURRENT ACTIVE OFFERS ---");

        // Happy Hour Offers
        if (!happyHourOffers.isEmpty()) {
            System.out.println("\nHappy Hour Discounts:");
            for (HappyHourOffer offer : happyHourOffers) {
                if (offer.isActive) {
                    String days = offer.days.stream()
                            .map(d -> d.toString().substring(0, 3))
                            .collect(Collectors.joining(", "));

                    System.out.printf("- %d%% off (%s to %s on %s)\n",
                            offer.discountPercent,
                            offer.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            offer.endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            days);
                }
            }
        }

        // Category Offers
        if (!categoryOffers.isEmpty()) {
            System.out.println("\nCategory Discounts:");
            for (CategoryOffer offer : categoryOffers) {
                if (offer.isActive) {
                    System.out.printf("- %d%% off on %s\n",
                            offer.discountPercent,
                            offer.category);
                }
            }
        }


        if (happyHourOffers.isEmpty() && categoryOffers.isEmpty()) {
            System.out.println("No active offers currently.");
        }
    }

    static void loyaltyProgramReport() throws SQLException {
        System.out.println("\n--- LOYALTY PROGRAM REPORT ---");
        String sql = "SELECT Username, Points, LastVisit FROM loyalty_program ORDER BY Points DESC LIMIT 10";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-20s %-10s %-15s\n", "Customer", "Points", "Last Visit");
            while (rs.next()) {
                System.out.printf("%-20s %-10d %-15s\n",
                        rs.getString("Username"),
                        rs.getInt("Points"),
                        rs.getDate("LastVisit"));
            }
        }
    }


    static void updatePointsInDatabase(String username, int newPoints) throws SQLException {
        String updateSql = "UPDATE loyalty_program SET Points = ? WHERE Username = ?";
        try (PreparedStatement ps = con.prepareStatement(updateSql)) {
            ps.setInt(1, newPoints);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }
    static void generateBill(int itemId, int quantity, String username, List<String> freeItems) throws SQLException {
        if (itemId == 0 && freeItems.isEmpty()) {
            System.out.println("No items in order!");
            return;
        }

        double basePrice = 0;
        MenuItem item = null;

        if (itemId != 0) {
            item = menuMap.get(itemId);
            if (item == null) {
                System.out.println("Item not found!");
                return;
            }
            basePrice = item.price * quantity;
        }

        double happyHourDiscount = calculateHappyHourDiscount(basePrice);
        double categoryDiscount = calculateCategoryDiscount(item != null ? item.category : "", basePrice);

        double loyaltyDiscount = calculateLoyaltyDiscount(username, basePrice);
        double totalDiscount = happyHourDiscount + categoryDiscount  + loyaltyDiscount;
        double finalPrice = basePrice - totalDiscount;

        System.out.println("\n--- ORDER BILL ---");

        if (item != null) {
            System.out.printf("Item: %s (Qty: %d)\n", item.name, quantity);
            System.out.printf("Base Price: %.2f\n", basePrice);

            if (happyHourDiscount > 0)
                System.out.printf("Happy Hour Discount (-%.2f)\n", happyHourDiscount);
            if (categoryDiscount > 0)
                System.out.printf("Category Discount (-%.2f)\n", categoryDiscount);
            if (loyaltyDiscount > 0)
                System.out.printf("Loyalty Discount (-%.2f)\n", loyaltyDiscount);
        }

        if (!freeItems.isEmpty()) {
            System.out.println("\nFree Items Included:");
            for (String freeItem : freeItems) {
                System.out.println("- " + freeItem);
            }
        }

        System.out.println("-------------------");
        System.out.printf("TOTAL: %.2f\n", finalPrice);
    }

    // Discount calculation methods
    static double calculateHappyHourDiscount(double basePrice) {
        LocalTime now = LocalTime.now();
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        for (HappyHourOffer offer : happyHourOffers) {
            if (offer.isActive &&
                    offer.days.contains(today) &&
                    !now.isBefore(offer.startTime) &&
                    !now.isAfter(offer.endTime)) {
                return basePrice * (offer.discountPercent / 100.0);
            }
        }
        return 0;
    }

    static double calculateCategoryDiscount(String category, double basePrice) {
        for (CategoryOffer offer : categoryOffers) {
            if (offer.isActive && offer.category.equalsIgnoreCase(category)) {
                return basePrice * (offer.discountPercent / 100.0);
            }
        }
        return 0;
    }
    static void markOrderCompleted() throws SQLException {
        viewPendingOrders();

        System.out.print("\nEnter OrderID to mark as completed: ");
        int orderId;
        while (true) {
            try {
                orderId = scanner.nextInt();
                scanner.nextLine(); // consume newline
                break;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a numeric ID: ");
                scanner.nextLine(); // clear invalid input
            }
        }
        System.out.println("Entered OrderID: " + orderId);

        String checkSql = "SELECT * FROM orders WHERE OrderID = ? AND Status = 'PENDING'";
        try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setInt(1, orderId);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                System.out.println("Order not found or already completed/cancelled!");
                return;
            }

            System.out.println("Order found, processing...");

            String username = rs.getString("Username");
            int quantity = rs.getInt("Quantity");
            int itemId = rs.getInt("ItemID");

            MenuItem item = menuMap.get(itemId);
            double orderValue = item != null ? item.price * quantity : 0;
            int pointsEarned = calculateLoyaltyPoints(orderValue);

            String updateSql = "UPDATE orders SET Status = 'COMPLETED' WHERE OrderID = ?";
            try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                updatePs.setInt(1, orderId);
                int rows = updatePs.executeUpdate();

                if (rows > 0) {
                    System.out.println("Order #" + orderId + " marked as completed!");

                    if (item != null) {
                        item.popularity += quantity;
                        String updatePop = "UPDATE menu_items SET Popularity = ? WHERE ID = ?";
                        try (PreparedStatement popPs = con.prepareStatement(updatePop)) {
                            popPs.setInt(1, item.popularity);
                            popPs.setInt(2, itemId);
                            popPs.executeUpdate();
                        }
                    }

                    updateLoyaltyPoints(username, pointsEarned);
                    System.out.println("Added " + pointsEarned + " loyalty points to " + username + " (order value: Rs." + orderValue + ")");
                } else {
                    System.out.println("Failed to update order status.");
                }
            }
        }
    }
    static void cancelOrder() throws SQLException {
        viewPendingOrders();

        System.out.print("\nEnter OrderID to cancel: ");
        int orderId;
        while (true) {
            try {
                orderId = scanner.nextInt();
                scanner.nextLine(); // consume newline
                break;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input! Please enter a numeric ID: ");
                scanner.nextLine(); // clear invalid input
            }
        }

        try {
            String checkSql = "SELECT * FROM orders WHERE OrderID = ? AND Status = 'PENDING'";
            try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                checkPs.setInt(1, orderId);
                ResultSet rs = checkPs.executeQuery();

                if (!rs.next()) {
                    System.out.println("Error: Order not found or not in PENDING status!");
                    System.out.println("Only pending orders can be cancelled.");
                    return;
                }

                String username = rs.getString("Username");
                int itemId = rs.getInt("ItemID");
                int quantity = rs.getInt("Quantity");

                // Calculate order value for loyalty points
                MenuItem item = menuMap.get(itemId);
                double orderValue = item != null ? item.price * quantity : 0;
                int pointsToRemove = calculateLoyaltyPoints(orderValue);

                System.out.println("\nOrder Details:");
                System.out.println("Customer: " + username);
                System.out.println("Item ID: " + itemId);
                System.out.println("Quantity: " + quantity);
                System.out.println("Order Value: Rs." + orderValue);
                System.out.println("Points to remove: " + pointsToRemove);

                System.out.print("Are you sure you want to cancel this order? (yes/no): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();
                if (!confirmation.equals("yes")) {
                    System.out.println("Order cancellation aborted.");
                    return;
                }

                String updateSql = "UPDATE orders SET Status = 'CANCELLED' WHERE OrderID = ?";
                try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                    updatePs.setInt(1, orderId);
                    int rows = updatePs.executeUpdate();

                    if (rows > 0) {
                        con.commit();
                        System.out.println("Order #" + orderId + " successfully cancelled!");

                        if (item != null) {
                            item.popularity -= quantity;
                            String updatePop = "UPDATE menu_items SET Popularity = ? WHERE ID = ?";
                            try (PreparedStatement popPs = con.prepareStatement(updatePop)) {
                                popPs.setInt(1, item.popularity);
                                popPs.setInt(2, itemId);
                                popPs.executeUpdate();
                            }
                        }

                        updateLoyaltyPoints(username, -pointsToRemove);
                        System.out.println("Removed " + pointsToRemove + " loyalty points from " + username);
                    } else {
                        System.out.println("Failed to cancel order!");
                    }
                }
            }
        } catch (SQLException e) {
            con.rollback();
            System.err.println("Error cancelling order: " + e.getMessage());
            throw e;
        }
    }
    static void logOrderToFile(int itemId, int quantity, String username, List<String> freeItems) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders.txt", true))) {
            writer.write(String.format("[%s] User: %s, ItemID: %d, Quantity: %d",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    username, itemId, quantity));

            if (!freeItems.isEmpty()) {
                writer.write(", Free Items: " + String.join(", ", freeItems));
            }
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}