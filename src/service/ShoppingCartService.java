package service;

import models.Product;
import util.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public final class ShoppingCartService {

    private ShoppingCartService() {}

    public static void addProductToShoppingCart(Scanner scanner, Product[] products, List<Product> shoppingCart) {
        System.out.println("Please select what products you would like to buy (write the id corresponding):");
        int option = scanner.nextInt();
        int noOfTries = 3;

        int k = 0;

        while (k++ < noOfTries) {
            int finalOption = option;
            Optional<Product> optionalProduct = Arrays.stream(products)
                    .filter(product -> product.getId() == finalOption)
                    .findFirst();

            if (optionalProduct.isEmpty()) {
                System.out.printf("Option invalid, please try again (try %d out of %d)%n", k, noOfTries);
                option = scanner.nextInt();
            } else {
                Product product = optionalProduct.get();
                if (product.getAvailableQuantity() > 0) {
                    shoppingCart.add(optionalProduct.get());
                } else {
                    System.out.println("Product is out of stock!");
                }
                break;
            }
        }
    }

    public static void removeProductFromShoppingCart(Scanner scanner, List<Product> shoppingCart) {
        boolean isEmpty = Utils.displayShoppingCart(shoppingCart);
        if (isEmpty) return;

        System.out.println("Please select what product you would like to remove from shopping cart (write the id corresponding):");
        int option = scanner.nextInt();

        Optional<Product> optionalProduct = shoppingCart.stream()
                .filter(product -> product.getId() == option)
                .findFirst();

        if (optionalProduct.isEmpty()) {
            System.out.println("The product provided does not exist in your shopping cart.");
            return;
        }
        shoppingCart.remove(optionalProduct.get());
    }
}
