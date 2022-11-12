package service;

import exceptions.InvalidAnswerException;
import dto.MenuPayload;
import models.Product;
import models.Purchase;
import models.User;
import util.RegexValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static util.Utils.DECIMAL_FORMAT;

public final class BankService {
    public static void addFunds(Scanner scanner, User user) {
        System.out.println("Please insert the amount of money you want to add:");
        float funds = scanner.nextFloat();

        user.setCredit(user.getCredit() + funds);
    }

    public static void checkout(Scanner scanner,
                                     User user,
                                     List<Product> shoppingCart,
                                     Product[] products,
                                     Purchase[] sessionPurchases,
                                     MenuPayload menuPayload) throws InvalidAnswerException {
        System.out.println("The total price for your products is: " + getTotalPrice(shoppingCart));
        System.out.println("Are your sure you want to proceed? (Y/n)");

        boolean proceed = RegexValidator.handleYesNo(scanner.next());
        if (proceed) {
            System.out.println("Buying...");
            float totalPrice = getTotalPrice(shoppingCart);
            boolean isAccepted = checkFunds(user, totalPrice);

            if (isAccepted) {
                System.out.println("The transaction has been completed successfully!");

                Purchase purchase = new Purchase();
                purchase.setUsername(user.getUserName());
                purchase.setProductList(List.copyOf(shoppingCart));
                purchase.setCreationDate(LocalDateTime.now());

                menuPayload.setSessionPurchases(DatabaseService.enlargeArrayAndAddPurchase(sessionPurchases, purchase));
                DatabaseService.updateStocks(products, shoppingCart);
                shoppingCart.clear();
                user.setCredit(user.getCredit() - totalPrice);

                System.out.println("The shopping cart is clear and your balance is: " + DECIMAL_FORMAT.format(user.getCredit()));
            } else {
                System.out.println("You have just " + DECIMAL_FORMAT.format(user.getCredit())
                        + " and the products total price is "
                        + DECIMAL_FORMAT.format(totalPrice) + ".");
            }
        }

        menuPayload.setProducts(products);
    }

    private static boolean checkFunds(User user, float totalPrice) {
        return user.getCredit() >= totalPrice;
    }

    private static float getTotalPrice(List<Product> shoppingCart) {
        return shoppingCart.stream()
                .map(Product::getPrice)
                .reduce(0.0f, Float::sum);
    }

    private BankService() {}
}
