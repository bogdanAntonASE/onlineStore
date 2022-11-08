package util;

import exceptions.ExportException;
import exceptions.InvalidAnswerException;
import exceptions.RegistrationException;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;
import models.MenuPayload;
import models.Product;
import models.PurchasePayload;
import models.User;
import service.BankService;
import service.ShoppingCartService;
import service.DatabaseService;
import service.ExportService;
import service.RegexValidator;
import service.RegisterService;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import static service.LoginService.checkIfPasswordCorrect;
import static service.LoginService.checkIfUserPresent;

public final class Utils {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public static User displayLoginScreen(Scanner scanner, List<User> knownUsers)
            throws RegistrationException,
            UserNotFoundException,
            WrongPasswordException,
            InvalidAnswerException {
        boolean newUser = isNewUser(scanner);

        User user;
        if (newUser) {
            user = displayAlreadyExistingUserScreen(scanner, knownUsers);
        } else {
            user = displayNewUserScreen(scanner, knownUsers);
        }
        return user;
    }

    public static MenuPayload displayMenu(Scanner scanner,
                                          User user,
                                          Product[] products,
                                          List<User> knownUsers,
                                          List<Product> shoppingCart,
                                          PurchasePayload[] sessionPurchases) throws InvalidAnswerException, ExportException, RegistrationException {
        MenuPayload menuPayload = new MenuPayload();
        menuPayload.setLoggedIn(true);
        menuPayload.setProducts(products);
        menuPayload.setSessionPurchases(sessionPurchases);

        System.out.println("Please select one of the following options (must be a number):");
        System.out.print("1. Display available products.            ");
        System.out.print("2. Add a product to shopping cart.               ");
        System.out.println("3. Display shopping cart.");
        System.out.print("4. Remove a product from shopping cart.   ");
        System.out.print("5. Go to checkout.                               ");
        System.out.println("6. Add funds.");
        System.out.print("7. Display user information.              ");
        System.out.print("8. Logout.                                       ");
        System.out.println("9. Change Password.");

        if (user.isAdmin()) {
            System.out.print("10. Display all users.                    ");
            System.out.print("11. Export purchases between dates.              ");
            System.out.println("12. Make a user ADMIN.");
            System.out.print("13. Add products to database.             ");
            System.out.print("14. Restock an existing product.                 ");
            System.out.println("15. Remove product from store.");
        }

        int option = scanner.nextInt();
        switch (option) {
            case 1 -> displayProducts(products);
            case 2 -> {
                displayProducts(products);
                ShoppingCartService.addProductToShoppingCart(scanner, products, shoppingCart);
            }
            case 3 -> displayShoppingCart(shoppingCart);
            case 4 -> ShoppingCartService.removeProductFromShoppingCart(scanner, shoppingCart);
            case 5 -> {
                displayShoppingCart(shoppingCart);
                if (!shoppingCart.isEmpty()) {
                    BankService.checkout(scanner,
                            user, shoppingCart, products, sessionPurchases, menuPayload);
                }
            }
            case 6 -> BankService.addFunds(scanner, user);
            case 7 -> System.out.println("User information:\n" + user);
            case 8 -> menuPayload.setLoggedIn(false);
            case 9 -> {
                boolean isPasswordChanged = RegisterService.changePassword(scanner, user);
                if (!isPasswordChanged) {
                    System.out.println("Unfortunately, the password inserted is wrong... You are going to be logged out.");
                }
                menuPayload.setLoggedIn(isPasswordChanged);
            }
            case 10 -> displayUsers(knownUsers);
            case 11 -> {
                LocalDate from = handleReadDate(scanner, "from");
                LocalDate to = handleReadDate(scanner, "to");
                ExportService.exportPayloadsForPeriod(from, to);
            }
            case 12 -> DatabaseService.makeAUserAdmin(scanner, knownUsers);
            case 13 -> menuPayload.setProducts(DatabaseService.addNewProduct(scanner, products));
            case 14 -> DatabaseService.addNewStockForProduct(scanner, products);
            case 15 -> menuPayload.setProducts(DatabaseService.removeProductFromStore(scanner, products));
            default -> throw new InvalidAnswerException("Invalid answer!");
        }
        return menuPayload;
    }

    public static boolean displayShoppingCart(List<Product> shoppingCart) {
        if (!shoppingCart.isEmpty()) {
            System.out.println("Your shopping cart:");
            shoppingCart.forEach(System.out::println);

            System.out.println();
            return false;
        } else {
            System.out.println("Your shopping cart is empty!");

            System.out.println();
            return true;
        }
    }

    public static boolean displayProducts(Product[] products) {
        if (products.length > 0) {
            System.out.println("The available products:");
            for (Product product: products) {
                System.out.println(product);
            }
            System.out.println();
            return false;
        } else {
            System.out.println("No products on stock.");
            System.out.println();
            return true;
        }
    }

    private static LocalDate handleReadDate(Scanner scanner, String fromTo) throws InvalidAnswerException {
        System.out.println("Do you have preferences for '" + fromTo + "' date? (Y/n)");
        boolean isFromToPresent = RegexValidator.handleYesNo(scanner.next());
        LocalDate fromToLocalDate = fromTo.equals("from") ? LocalDate.of(1900, 1, 1) : LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (isFromToPresent) {
            System.out.println("Please type in '" + fromTo + "' date (yyyy-MM-dd):");
            String fromToDate = scanner.next();
            fromToLocalDate = LocalDate.parse(fromToDate, dateTimeFormatter);
        }

        return fromToLocalDate;
    }

    private static void displayUsers(List<User> users) {
        users.forEach(System.out::println);
        System.out.println();
    }

    private static boolean isNewUser(Scanner scanner) throws InvalidAnswerException {
        System.out.println("Do you have an account? (Y/n)");

        String next = scanner.next();
        return RegexValidator.handleYesNo(next);
    }

    private static User displayNewUserScreen(Scanner scanner, List<User> knownUsers) throws RegistrationException {
        User user = RegisterService.pickUserName(scanner, knownUsers);
        if (user == null) {
            throw new RegistrationException("Chosen usernames already persent in database " +
                    "or the naming rules were not respected.");
        }

        RegisterService.pickPassword(scanner, user);
        knownUsers.add(user);
        return user;
    }

    private static User displayAlreadyExistingUserScreen(Scanner scanner, List<User> knownUsers) throws WrongPasswordException, UserNotFoundException {
        System.out.println("Please insert your username:");

        String userName = scanner.next();
        User user = checkIfUserPresent(knownUsers, userName);

        System.out.println("Please insert your password:");
        String password = scanner.next();

        String encryptedPassword = EncryptionService.encrypt(password.toCharArray());
        checkIfPasswordCorrect(user, encryptedPassword);

        System.out.println("Login successful!");
        return user;
    }

    private Utils() {}
}
