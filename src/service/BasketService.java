package service;

import models.Product;
import util.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public final class BasketService {

    private BasketService() {}

    public static void addProductToBasket(Scanner scanner, Product[] products, List<Product> basket) {
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
                    basket.add(optionalProduct.get());
                } else {
                    System.out.println("Product is out of stock!");
                }
                break;
            }
        }
    }

    public static void removeProductFromBasket(Scanner scanner, List<Product> basket) {
        Utils.displayBasket(basket);
        System.out.println("Please select what product you would like to remove from basket (write the id corresponding):");
        int option = scanner.nextInt();

        Optional<Product> optionalProduct = basket.stream()
                .filter(product -> product.getId() == option)
                .findFirst();

        if (optionalProduct.isEmpty()) {
            System.out.println("The product provided does not exist in your basket.");
            return;
        }
        basket.remove(optionalProduct.get());
    }
}
