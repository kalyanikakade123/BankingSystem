import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;
    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void creditMoney(long accountNumber) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security pin: ");
        String security_pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if (accountNumber != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?");
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(creditQuery);
                    preparedStatement1.setDouble(1,amount );
                    preparedStatement1.setLong(2, accountNumber);
                    int rowsAffected = preparedStatement1.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Rs " + amount + " credited successfully!");
                        connection.commit();;
                        connection.setAutoCommit(true);
                    }
                    else {
                        System.out.println("Money is not credited!");
                        connection.rollback();;
                        connection.setAutoCommit(true);
                    }
                }
                else {
                    System.out.println("Invalid security pin!");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }


    public void debitMoney(long accountNumber) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security pin: ");
        String security_pin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if (accountNumber != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?");
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");
                    if (amount <= currentBalance) {
                        String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debitQuery);
                        preparedStatement1.setDouble(1,amount );
                        preparedStatement1.setLong(2, accountNumber);
                        int rowsAffected = preparedStatement1.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Rs " + amount + " debited successfully!");
                            connection.commit();;
                            connection.setAutoCommit(true);
                        }
                        else {
                            System.out.println("Money is not debited!");
                            connection.rollback();;
                            connection.setAutoCommit(true);
                        }
                    }
                    else {
                        System.out.println("Insufficient balance!");
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void getBalance(long accountNumber) {
        scanner.nextLine();
        System.out.print("Enter security pin: ");
        String securityPin = scanner.nextLine();
        try {
            String query = "SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance: " + balance);
            }
            else {
                System.out.println("Invalid Pin!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferMoney(long senderAccountNumber) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter receiver account number: ");
        long receiverAccountNumber = scanner.nextLong();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter security pin: ");
        String securityPin = scanner.nextLine();
        try {
            connection.setAutoCommit(false);
            if (senderAccountNumber != 0 && receiverAccountNumber != 0) {
                String query = "SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, senderAccountNumber);
                preparedStatement.setString(2, securityPin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");
                    if (amount <= currentBalance) {
                        String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debitQuery);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, senderAccountNumber);
                        int rowsAffected1 = preparedStatement1.executeUpdate();
                        PreparedStatement preparedStatement2 = connection.prepareStatement(creditQuery);
                        preparedStatement2.setDouble(1, amount);
                        preparedStatement2.setLong(2, receiverAccountNumber);
                        int rowsAffected2 = preparedStatement2.executeUpdate();
                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction successful!");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }
                        else {
                            System.out.println("Transaction failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }
                    else {
                        System.out.println("Insufficient balance!");
                    }
                }
                else {
                    System.out.println("Invalid security pin!");
                }
            }
            else {
                System.out.println("Invalid account number!");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }
}
