import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCartApp {

    private static class MainFrame extends JFrame {

        private JTabbedPane tabbedPane;
        private LoginPanel loginPanel;
        private ShoppingCartPanel shoppingCartPanel;
        private HomePanel homePanel;

        public MainFrame() {
            super("Shopping Cart App");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(null);

            tabbedPane = new JTabbedPane();

            // Create login panel
            loginPanel = new LoginPanel(this);

            // Add login panel to the tabbed pane
            tabbedPane.addTab("Login", loginPanel);

            add(tabbedPane);

            setVisible(true);
        }

        public void showShoppingCart(String username) {
            // Remove the login panel
            tabbedPane.remove(loginPanel);

            // Create and add the shopping cart panel
            shoppingCartPanel = new ShoppingCartPanel(username);
            tabbedPane.addTab("Shopping Cart", shoppingCartPanel);

            // Create and add the home panel
            homePanel = new HomePanel(shoppingCartPanel);
            tabbedPane.addTab("Home", homePanel);

            // Show the home tab
            tabbedPane.setSelectedComponent(homePanel);
        }
    }

    private static class LoginPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private MainFrame mainFrame;

        public LoginPanel(MainFrame mainFrame) {
            this.mainFrame = mainFrame;
            setLayout(new BorderLayout());

            JLabel titleLabel = new JLabel("Shopping Cart Application", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            add(titleLabel, BorderLayout.NORTH);

            JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

            JLabel usernameLabel = new JLabel("Username:");
            JLabel passwordLabel = new JLabel("Password:");

            usernameField = new JTextField();
            passwordField = new JPasswordField();

            JButton loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    char[] password = passwordField.getPassword();

                    if (authenticate(username, password)) {
                        mainFrame.showShoppingCart(username);
                    } else {
                        JOptionPane.showMessageDialog(LoginPanel.this, "Invalid credentials. Please try again.");
                    }
                }
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(new Font("Arial", Font.PLAIN, 16));
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            loginPanel.add(usernameLabel);
            loginPanel.add(usernameField);
            loginPanel.add(passwordLabel);
            loginPanel.add(passwordField);
            loginPanel.add(loginButton);
            loginPanel.add(cancelButton);

            add(loginPanel, BorderLayout.CENTER);
        }

        private boolean authenticate(String username, char[] password) {
            return username.equals("1") && new String(password).equals("1");
        }
    }

    private static class ShoppingCartPanel extends JPanel {
        private DefaultTableModel tableModel;
        private JTable cartTable;
        private JComboBox<ProductItem> productsDropdown;
        private Map<String, Double> productPrices;

        public ShoppingCartPanel(String username) {
            setLayout(new BorderLayout());

            JLabel titleLabel = new JLabel("Your Shopping Cart", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
            add(titleLabel, BorderLayout.NORTH);

            tableModel = new DefaultTableModel(new Object[] { "Product", "Quantity", "Price" }, 0);
            cartTable = new JTable(tableModel);
            cartTable.setFont(new Font("Arial", Font.PLAIN, 24));
            cartTable.setRowHeight(40);
            cartTable.setPreferredScrollableViewportSize(new Dimension(500, 300));

            JScrollPane scrollPane = new JScrollPane(cartTable);
            add(scrollPane, BorderLayout.CENTER);

            JButton removeProductButton = new JButton("Remove Product");
            removeProductButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = cartTable.getSelectedRow();
                    if (selectedRow != -1) {
                        tableModel.removeRow(selectedRow);
                    } else {
                        JOptionPane.showMessageDialog(ShoppingCartPanel.this, "Please select a product to remove.");
                    }
                }
            });

            JButton checkoutButton = new JButton("Checkout");
            checkoutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    double totalAmount = calculateTotalAmount();
                    showCheckoutDialog(username, totalAmount);
                }
            });

            JPanel controlPanel = new JPanel(new FlowLayout());
            controlPanel.add(removeProductButton);
            controlPanel.add(checkoutButton);

            add(controlPanel, BorderLayout.SOUTH);

            productPrices = new HashMap<>();
            productPrices.put("Orange", 1.99);
            productPrices.put("Apple", 2.49);
            productPrices.put("Pear", 1.79);
            productPrices.put("Grape", 3.99);
            productPrices.put("Lychee", 0.50);
            productPrices.put("Dragon Fruit", 4.99);
            productPrices.put("Longan", 2.49);
        }

        public void addProductToCart(ProductItem product) {
            int rowIndex = findProductRowIndex(product.getName());

            if (rowIndex == -1) {
                double price = product.getPrice();
                tableModel.addRow(new Object[] { product.getName(), 1, String.format("%.2f", price) });
            } else {
                int currentQuantity = (int) tableModel.getValueAt(rowIndex, 1);
                int newQuantity = currentQuantity + 1;

                double productPrice = productPrices.get(product.getName());
                double newTotal = newQuantity * productPrice;

                // Update the quantity and total amount with two decimal places
                tableModel.setValueAt(newQuantity, rowIndex, 1);
                tableModel.setValueAt(String.format("%.2f", newTotal), rowIndex, 2);
            }

            int lastRow = tableModel.getRowCount() - 1;
            Rectangle rect = cartTable.getCellRect(lastRow, 0, true);
            cartTable.scrollRectToVisible(rect);
            cartTable.setRowSelectionInterval(lastRow, lastRow);
        }

        private int findProductRowIndex(String productName) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(productName)) {
                    return i;
                }
            }
            return -1;
        }

        private double calculateTotalAmount() {
            double totalAmount = 0.0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String productName = (String) tableModel.getValueAt(i, 0);
                int quantity = (int) tableModel.getValueAt(i, 1);
                double productPrice = productPrices.get(productName);
                totalAmount += productPrice * quantity;
            }
            return totalAmount;
        }

        private void showCheckoutDialog(String username, double totalAmount) {
            JDialog checkoutDialog = new JDialog((Frame) null, "Checkout", true);
            checkoutDialog.setSize(300, 150);
            checkoutDialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridLayout(3, 1));

            JLabel messageLabel = new JLabel("Checkout completed for " + username, SwingConstants.CENTER);
            JLabel amountLabel = new JLabel("Total Amount: $" + String.format("%.2f", totalAmount),
                    SwingConstants.CENTER);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkoutDialog.dispose(); // close the dialog
                }
            });

            panel.add(messageLabel);
            panel.add(amountLabel);
            panel.add(okButton);

            checkoutDialog.add(panel);
            checkoutDialog.setVisible(true);
        }
    }

    private static class HomePanel extends JPanel {
        private ShoppingCartPanel shoppingCartPanel;

        public HomePanel(ShoppingCartPanel shoppingCartPanel) {
            this.shoppingCartPanel = shoppingCartPanel;
            setLayout(new BorderLayout());

            JLabel titleLabel = new JLabel("Shopping Application", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
            add(titleLabel, BorderLayout.NORTH);

            JPanel productPanel = new JPanel(new GridLayout(0, 3, 10, 10)); // 3 columns, adjust as needed

            ProductItem[] products = {
                    new ProductItem("Orange", 1.99, "orange.jpg"),
                    new ProductItem("Apple", 2.49, "apple.jpg"),
                    new ProductItem("Pear", 1.79, "pear.jpg"),
                    new ProductItem("Grape", 3.99, "grape.jpg"),
                    new ProductItem("Lychee", 0.50, "lychee.jpg"),
                    new ProductItem("Dragon Fruit", 3.99, "dragon.jpg"),
                    new ProductItem("Longan", 2.49, "longan.jpg"),

            };

            for (ProductItem product : products) {
                JButton addToCartButton = new JButton("Add to Cart");
                addToCartButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        shoppingCartPanel.addProductToCart(product);
                    }
                });

                try {
                    // Load the image using ImageIO
                    Image image = new ImageIcon(getClass().getResource(product.getImagePath())).getImage();
                    // Resize the image to a smaller size
                    Image resizedImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    ImageIcon resizedIcon = new ImageIcon(resizedImage);

                    JLabel imageLabel = new JLabel(resizedIcon, SwingConstants.CENTER);

                    // Set a larger font size for the product label
                    Font productFont = new Font("Arial", Font.PLAIN, 20);
                    JLabel productLabel = new JLabel("<html><center>" + product.getName() + "<br> $"
                            + String.format("%.2f", product.getPrice()) + "</center></html>", SwingConstants.CENTER);
                    productLabel.setFont(productFont);

                    JPanel productButtonPanel = new JPanel(new GridLayout(2, 1));
                    productButtonPanel.add(imageLabel);
                    productButtonPanel.add(productLabel);

                    JPanel buttonPanel = new JPanel(new BorderLayout());
                    buttonPanel.add(addToCartButton, BorderLayout.SOUTH);

                    JPanel combinedPanel = new JPanel(new BorderLayout());
                    combinedPanel.add(productButtonPanel, BorderLayout.CENTER);
                    combinedPanel.add(buttonPanel, BorderLayout.SOUTH);

                    productPanel.add(combinedPanel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JScrollPane productScrollPane = new JScrollPane(productPanel);
            add(productScrollPane, BorderLayout.CENTER);
        }
    }

    private static class ProductItem {
        private String name;
        private double price;
        private String imagePath;

        public ProductItem(String name, double price, String imagePath) {
            this.name = name;
            this.price = price;
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getImagePath() {
            return imagePath;
        }

        @Override
        public String toString() {
            return name + " - $" + String.format("%.2f", price);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}