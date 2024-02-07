import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;

public class BankingApplication {

    public static final String url = "jdbc:mysql://localhost:3306/banking_system";
    public static final String username = "root";
    public static final String password = "Arya@123";
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);
            User user = new User(connection, scanner);
            Accounts accounts = new Accounts(connection, scanner);
            AccountManager accountManager = new AccountManager(connection, scanner);

            String email;
            long accountNumber;

            while (true) {
                System.out.println("-------------------------------------------------WELCOME TO BANKING SYSTEM-------------------------------------------------");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email = user.login();
                        if(email != null) {
                            System.out.println("You Logged In!");
                            if (!accounts.accountExist(email)) {
                                System.out.println();
                                System.out.println("1. Open a new Bank account");
                                System.out.println("2. Exit");
                                if (scanner.nextInt() == 1) {
                                    accountNumber = accounts.openAccount(email);
                                    System.out.println("Account created successfully!");
                                    System.out.println("Your account number is " + accountNumber);
                                }
                                else {
                                    break;
                                }
                            }
                            accountNumber = accounts.getAccountNumber(email);
                            int choice2 = 0;
                            while (choice2 != 5) {
                                System.out.println();
                                System.out.println("1. Debit Money");
                                System.out.println("2. Credit Money");
                                System.out.println("3. Transfer Money");
                                System.out.println("4. Check Money");
                                System.out.println("5. Log out");
                                System.out.print("Enter your choice: ");
                                choice2 = scanner.nextInt();
                                switch (choice2) {
                                    case 1:
                                        accountManager.debitMoney(accountNumber);
                                        break;
                                    case 2:
                                        accountManager.creditMoney(accountNumber);
                                        break;
                                    case 3:
                                        accountManager.transferMoney(accountNumber);
                                        break;
                                    case 4:
                                        accountManager.getBalance(accountNumber);
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.println("Enter valid choice!");
                                        break;
                                }
                            }
                        }
                        else {
                            System.out.println("Incorrect email or password!");
                        }
                    case 3:
                        System.out.println("----------------------------------------THANK YOU FOR USING BANKING SYSTEM----------------------------------------");
                        System.out.println("Exiting System...");
                        return;
                    default:
                        System.out.println("Please enter a valid choice!");
                        break;
                }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}