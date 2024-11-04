# Personal Finance Tracker

A modern Java Swing application for tracking personal expenses and income with a clean, user-friendly interface and MySQL database integration.

## Features

- **Modern UI**: Clean and intuitive interface with a professional color scheme
- **Transaction Management**: 
  - Add both expenses and income
  - Categorize transactions
  - Date selection using calendar widget
  - Detailed transaction descriptions
- **Visual Feedback**:
  - Color-coded transactions (red for expenses, green for income)
  - Real-time balance updates
  - Interactive buttons with hover effects
- **Data Persistence**: MySQL database integration for reliable data storage
- **Transaction Categories**:
  - Food & Dining
  - Transportation
  - Shopping
  - Bills & Utilities
  - Entertainment
  - Health & Fitness
  - Travel
  - Education
  - Other

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server
- Maven (for dependency management)

## Required Libraries

- `javax.swing` and `java.awt` for the GUI
- `java.sql` for database connectivity
- `com.toedter.calendar.JDateChooser` for date selection
- MySQL JDBC Driver

## Database Setup

1. Create a MySQL database named `finance_tracker`
2. Create a table named `entries` with the following schema:

```sql
CREATE TABLE entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(100) NOT NULL
);
```

3. Update the database connection details in the code:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/finance_tracker";
private static final String USER = "root";
private static final String PASS = "your_password";
```

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/personal-finance-tracker.git
```

2. Import the project into your preferred IDE

3. Install the required dependencies:
- MySQL JDBC Connector
- JCalendar library

4. Build the project

## Usage

1. Run the `ExpenseIncomeTracker` class to start the application

2. To add a new transaction:
   - Select a date using the calendar widget
   - Enter a description for the transaction
   - Input the amount
   - Select the transaction type (Expense/Income)
   - Choose a category
   - Click "Add Transaction"

3. The transaction will be displayed in the table and the balance will update automatically

## Customization

You can customize the appearance by modifying the following constants in the `ModernExpensesIncomesTracker` class:

```java
private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
private static final Color SECONDARY_COLOR = new Color(236, 240, 241);
private static final Color ACCENT_COLOR = new Color(52, 152, 219);
private static final Color TEXT_COLOR = new Color(44, 62, 80);
```

## Error Handling

The application includes validation for:
- Empty date fields
- Empty description fields
- Invalid amount formats
- Database connection issues

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Uses JCalendar library for date picking functionality
- Inspired by modern financial management applications
- Color scheme based on flat UI design principles
