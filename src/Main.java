import exceptions.BaseException;
import exceptions.RegistrationException;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;
import models.MenuPayload;
import models.Product;
import models.PurchasePayload;
import models.User;
import service.DatabaseService;
import service.RegexValidator;
import util.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final List<User> users;
    private static Product[] products = new Product[]{};
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Product> basket = new ArrayList<>();
    private static PurchasePayload[] sessionPurchases = new PurchasePayload[]{};
    private static boolean isWillingToContinue = true;

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
            } catch (BaseException exception) {
                System.out.println("Exception occurred, proceeding to persist data: \n" + exception.getMessage());
                break;
            }
            boolean isLoggedIn = true;

            try {
                while (isLoggedIn) {
                    MenuPayload menuPayload = Utils.displayMenu(scanner, user, products, users, basket, sessionPurchases);
                    isLoggedIn = menuPayload.isLoggedIn();
                    products = menuPayload.getProducts();
                    sessionPurchases = menuPayload.getSessionPurchases();
                }
            } catch (BaseException exception) {
                System.out.println("Exception occurred, proceeding to persist data: \n" + exception.getMessage());
                break;
            }

            System.out.println("Logout successful, would you like to sign in again? (Y/n)");
            isWillingToContinue = RegexValidator.handleYesNo(scanner.next());
        }

        System.out.println("Closing the app...");
        DatabaseService.persistData(users);
        DatabaseService.persistProducts(products);
        DatabaseService.persistPurchases(sessionPurchases);
    }
}