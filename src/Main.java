import exceptions.BaseException;
import exceptions.InvalidAnswerException;
import dto.MenuPayload;
import models.Product;
import models.Purchase;
import models.User;
import service.DatabaseService;
import util.RegexValidator;
import util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final List<User> users;
    private static Product[] products = new Product[]{};
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Product> shoppingCart = new ArrayList<>();
    private static Purchase[] sessionPurchases = new Purchase[]{};
    private static boolean isWillingToContinue = true;
    private static boolean isAdmin = true;

    static {
        users = new ArrayList<>();

        DatabaseService.initializeUsersList(users);
        products = DatabaseService.initializeProductsArray(products);
    }

    public static void main(String[] args) {

        System.out.println("Welcome to our Online Shop!");

        while (isWillingToContinue) {
            User user;
            try {
                user = Utils.displayLoginScreen(scanner, users);
                isAdmin = user.isAdmin();
            } catch (BaseException exception) {
                System.out.println("Exception occurred, leading you back to the main menu: " + exception.getMessage());
                continue;
            } catch (Exception exception) {
                System.out.println("Exception occurred, proceeding to persist data. Stacktrace: \n" + exception.getMessage());
                break;
            }

            boolean isLoggedIn = true;

            try {
                while (isLoggedIn) {
                    try {
                        MenuPayload menuPayload = Utils.displayMenu(scanner, user, products, users, shoppingCart, sessionPurchases);
                        isLoggedIn = menuPayload.isLoggedIn();
                        products = menuPayload.getProducts();
                        sessionPurchases = menuPayload.getSessionPurchases();
                    } catch (BaseException exception) {
                        System.out.println("Exception occurred... leading you back to main menu: " + exception.getMessage());
                    }
                }
            } catch (Exception exception) {
                System.out.println("Exception occurred, proceeding to persist data. Stacktrace: \n" + exception.getMessage());
                break;
            }

            System.out.println("Logout successful, would you like to sign in again? (Y/n)");
            try {
                isWillingToContinue = RegexValidator.handleYesNo(scanner.next());
                if (isWillingToContinue) {
                    sessionPurchases = new Purchase[]{};
                }
            } catch (InvalidAnswerException e) {
                System.out.println("Invalid answer! Proceeding to the closing app process...");
                isWillingToContinue = false;
            }
        }

        System.out.println("Closing the app...");
        DatabaseService.persistData(users, isAdmin);
        DatabaseService.persistProducts(products, isAdmin);
        DatabaseService.persistPurchases(sessionPurchases, isAdmin);
    }
}