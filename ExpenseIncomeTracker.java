package Tracker;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class ExpenseIncomeTracker {
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "haha@1234");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ModernExpensesIncomesTracker().setLocationRelativeTo(null);
        });
    }
}

class ExpenseIncomeEntry {
    private String date;
    private String description;
    private double amount;
    private String type;
    private String category;

    public ExpenseIncomeEntry(String date, String description, double amount, String type, String category) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }


    public String getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getCategory() { return category; }
}

class ModernTableModel extends AbstractTableModel {
    private final List<ExpenseIncomeEntry> entries;
    private final String[] columnNames = {"Date", "Description", "Amount", "Type", "Category"};

    public ModernTableModel() {
        entries = new ArrayList<>();
    }

    public void addEntry(ExpenseIncomeEntry entry) {
        entries.add(entry);
        fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
    }

    @Override
    public int getRowCount() { return entries.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ExpenseIncomeEntry entry = entries.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> entry.getDate();
            case 1 -> entry.getDescription();
            case 2 -> String.format("%.2f", entry.getAmount());
            case 3 -> entry.getType();
            case 4 -> entry.getCategory();
            default -> null;
        };
    }

    public List<ExpenseIncomeEntry> getEntries() {
        return entries;
    }
}

class ModernExpensesIncomesTracker extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(236, 240, 241);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    private final ModernTableModel tableModel;
    private JTable table;
    private JDateChooser dateChooser;
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<String> typeCombobox;
    private JComboBox<String> categoryCombobox;
    private JLabel balanceLabel;
    private double balance;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/finance_tracker";
    private static final String USER = "root";
    private static final String PASS = "haha@1234";

    public ModernExpensesIncomesTracker() {
        this.table = table;
        this.dateChooser = dateChooser;
        this.descriptionField = descriptionField;
        this.amountField = amountField;
        this.typeCombobox = typeCombobox;
        this.categoryCombobox = categoryCombobox;
        this.balanceLabel = balanceLabel;
        balance = 0.0;
        tableModel = new ModernTableModel();

        setTitle("Personal Finance Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));

        setupComponents();

        setupLayout();

        loadDataFromDatabase();

        pack();
        setVisible(true);
    }

    private void setupComponents() {
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    double amount = Double.parseDouble(getModel().getValueAt(row, 2).toString());
                    comp.setBackground(amount < 0 ? new Color(255, 235, 238) : new Color(232, 245, 233));
                }
                return comp;
            }
        };

        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(30);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, 30));
        dateChooser.setDateFormatString("yyyy-MM-dd");

        descriptionField = createStyledTextField(200);
        amountField = createStyledTextField(100);

        typeCombobox = new JComboBox<>(new String[]{"Expense", "Income"});
        categoryCombobox = new JComboBox<>(new String[]{
                "Food & Dining", "Transportation", "Shopping", "Bills & Utilities",
                "Entertainment", "Health & Fitness", "Travel", "Education", "Other"
        });

        styleComboBox(typeCombobox);
        styleComboBox(categoryCombobox);


        balanceLabel = new JLabel("Current Balance: $0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceLabel.setForeground(PRIMARY_COLOR);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR),
                        "New Transaction",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        PRIMARY_COLOR
                )
        ));


        inputPanel.add(createLabeledComponent("Date:", dateChooser));
        inputPanel.add(createLabeledComponent("Description:", descriptionField));
        inputPanel.add(createLabeledComponent("Amount:", amountField));
        inputPanel.add(createLabeledComponent("Type:", typeCombobox));
        inputPanel.add(createLabeledComponent("Category:", categoryCombobox));

        JButton addButton = createStyledButton("Add Transaction");
        addButton.addActionListener(e -> addEntry());
        inputPanel.add(addButton);


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(balanceLabel);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JTextField createStyledTextField(int width) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(width, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setPreferredSize(new Dimension(150, 30));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);
        ((JComponent) comboBox.getRenderer()).setPreferredSize(new Dimension(140, 25));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    private JPanel createLabeledComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_COLOR);
        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void addEntry() {
        // Input validation
        if (dateChooser.getDate() == null) {
            showError("Please select a date");
            return;
        }

        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            showError("Please enter a description");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Please enter a valid amount");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(dateChooser.getDate());
        String type = (String) typeCombobox.getSelectedItem();
        String category = (String) categoryCombobox.getSelectedItem();

        if ("Expense".equals(type)) {
            amount *= -1;
        }

        ExpenseIncomeEntry entry = new ExpenseIncomeEntry(date, description, amount, type, category);
        tableModel.addEntry(entry);

        balance += amount;
        updateBalanceLabel();

        saveEntryToDatabase(entry);

        clearInputFields();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Current Balance: $%.2f", balance));
        balanceLabel.setForeground(balance >= 0 ? new Color(46, 204, 113) : new Color(231, 76, 60));
    }

    private void saveEntryToDatabase(ExpenseIncomeEntry entry) {
        String sql = "INSERT INTO entries (date, description, amount, type, category) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, entry.getDate());
            pstmt.setString(2, entry.getDescription());
            pstmt.setDouble(3, entry.getAmount());
            pstmt.setString(4, entry.getType());
            pstmt.setString(5, entry.getCategory());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            showError("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDataFromDatabase() {
        String sql = "SELECT * FROM entries ORDER BY date DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ExpenseIncomeEntry entry = new ExpenseIncomeEntry(
                        rs.getString("date"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        rs.getString("category")
                );
                tableModel.addEntry(entry);
                balance += entry.getAmount();
            }
            updateBalanceLabel();
        } catch (SQLException e) {
            showError("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearInputFields() {
        dateChooser.setDate(null);
        descriptionField.setText("");
        amountField.setText("");
        typeCombobox.setSelectedIndex(0);
        categoryCombobox.setSelectedIndex(0);
    }
}
