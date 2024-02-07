import java.sql.*;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner scanner;
    public Accounts(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public long openAccount(String email) {
        if (!accountExist(email)) {
            String openAccountQuery = "INSERT INTO accounts(account_number, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?);";
            scanner.nextLine();
            System.out.print("Enter your full name: ");
            String full_name = scanner.nextLine();
            System.out.print("Enter initial amount to deposit: ");
            double balance = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter security pin: ");
            String security_pin = scanner.nextLine();
            try {
                long account_number = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(openAccountQuery);
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, full_name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, security_pin);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return account_number;
                }
                else {
                    throw new RuntimeException("Account creation failed!");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Account already exist!");
    }


    public long getAccountNumber(String email) {
        try {
            String query = "SELECT account_number FROM accounts WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("account_number");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Account Number doesn't exist!");
    }

    public long generateAccountNumber() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1");
            if (resultSet.next()) {
                long lastAccountNumber = resultSet.getLong("account_number");
                return lastAccountNumber+1;
            }
            else {
                return 10000100;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 10000100;
    }

    public boolean accountExist(String email) {
        String query = "SELECT account_number FROM accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
