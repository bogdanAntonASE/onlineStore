package service;

import exceptions.InvalidAnswerException;
import exceptions.PersistException;
import models.Product;
import models.PurchasePayload;
import models.User;
import util.Utils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


/**
 * Service to read/write from pseudo-database (binary and text files)
 * @author Anton Bogdan
 * @version 1.0
 */
public class DatabaseService {

    private static final String USERS_DB = "users.db";
    private static final String PRODUCTS_DB = "products.db";
    public static final String PURCHASES_DB = "purchases.txt";

    /**
     * Stores users to binary file
     * @param users -> the list of users at the end of the session
     * @param isAdmin -> tells us if the current is admin or not
     */
    public static void persistData(List<User> users, boolean isAdmin) {
        if (isAdmin) {
            System.out.println("---Persisting users to database " + USERS_DB  + "...");
        }

        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(
                             new BufferedOutputStream(
                                     new FileOutputStream(USERS_DB)))) {
            users.forEach(user -> {
                try {
                    outputStream.writeObject(user);
                } catch (IOException e) {
                    throw new PersistException(e.getMessage());
                }
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Initializes the session list of users from binary file
     * @param users -> the list of users to be populated
     */
    public static void initializeUsersList(List<User> users) throws PersistException {
        File database = new File(USERS_DB);
        if (database.isFile() && database.length() != 0) {
            try (ObjectInputStream inputStream =
                         new ObjectInputStream(new FileInputStream(database))) {
                User user;
                while ((user = (User) inputStream.readObject()) != null) {
                    users.add(user);
                }
            } catch (EOFException ignored) {
                //do nothing
            } catch (ClassNotFoundException | IOException exception) {
                throw new PersistException(exception.getMessage());
            }
        }
    }

    /**
     * Initializes the session array of products from binary file
     * @param products already existing products, if any
     * @return computed array of products
     * @throws PersistException
     */
    public static Product[] initializeProductsArray(Product[] products) throws PersistException {
        File database = new File(PRODUCTS_DB);
        if (database.isFile() && database.length() != 0) {
            try (ObjectInputStream inputStream =
                         new ObjectInputStream(new FileInputStream(database))) {
                Product product;
                while ((product = (Product) inputStream.readObject()) != null) {
                    Product productTemp = new Product();
                    productTemp.setName(product.getName());
                    productTemp.setPrice(product.getPrice());
                    productTemp.setIngredients(product.getIngredients());
                    productTemp.setDescription(product.getDescription());
                    productTemp.setAvailableQuantity(product.getAvailableQuantity());
                    productTemp.setId(product.getId());

                    products = enlargeArrayAndAddProduct(products, productTemp);
                }
            } catch (EOFException ignored) {
                //do nothing
            } catch (ClassNotFoundException | IOException exception) {
                throw new PersistException(exception.getMessage());
            }
        }
        return products;
    }

    /**
     * Adding new product to the array of products
     * @param scanner Scanner instance
     * @param products already existing products, if any
     * @return changed array of products
     * @throws InvalidAnswerException
     */
    public static Product[] addNewProduct(Scanner scanner, Product[] products) throws InvalidAnswerException {
        Product product = new Product();
        System.out.println("Please insert the name of the product (at least 3 letters, no numbers allowed):");
        String productName = scanner.next();

        RegexValidator.checkProductNameOrDescription(productName);
        product.setName(productName);

        System.out.println("Please insert the description of the product (at least 3 letters, no numbers allowed):");
        scanner.nextLine();
        String description = scanner.nextLine();

        RegexValidator.checkProductNameOrDescription(description);
        product.setDescription(description);

        System.out.println("Please insert the product's price:");
        float price = scanner.nextFloat();
        if (price < 0) {
            throw new InvalidAnswerException("Price cannot be negative...");
        }
        product.setPrice(price);

        System.out.println("Please insert the stock:");
        int stock = scanner.nextInt();
        if (stock < 0) {
            throw new InvalidAnswerException("Stock cannot be negative...");
        }
        product.setAvailableQuantity(stock);

        System.out.println("Please insert the list of ingredients separated by comma:");
        String ingredientsString = scanner.next();
        String[] ingredients = ingredientsString.split(",");
        product.setIngredients(ingredients);

        return enlargeArrayAndAddProduct(products, product);
    }

    /**
     * Updates stocks after a purchase is made
     * @param products already existing products, if any
     * @param shoppingCart user's shopping cart
     */
    public static void updateStocks(Product[] products, List<Product> shoppingCart) {
        for (Product product : products) {
            shoppingCart.forEach(productInCart -> {
                if (productInCart.getId() == product.getId()) {
                    product.setAvailableQuantity(product.getAvailableQuantity() - 1);
                }
            });
        }
    }

    /**
     * Make a user admin based on current user entries
     * @param scanner Scanner instance
     * @param users users list present on session
     */
    public static void makeAUserAdmin(Scanner scanner, List<User> users) {
        System.out.println("Enter the username for who you want to provide privileges:");
        String username = scanner.next();

        Optional<User> optionalUser = users.stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst();

        if (optionalUser.isEmpty()) {
            System.out.println("---User not found in database");
            return;
        }
        User user = optionalUser.get();
        user.setAdmin(true);
    }

    /**
     * Persists array of products in binary file
     * @param products already existing products, if any
     * @param isAdmin flag if user is admin
     */
    public static void persistProducts(Product[] products, boolean isAdmin) {
        if (isAdmin) {
            System.out.println("---Persisting products to database " + PRODUCTS_DB + "...");
        }
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(new FileOutputStream(PRODUCTS_DB))) {
            Arrays.stream(products).forEach(product -> {
                try {
                    outputStream.writeObject(product);
                } catch (IOException e) {
                    throw new PersistException(e.getMessage());
                }
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Add new stock for a product based on user input
     * @param scanner Scanner instance
     * @param products already existing products, if any
     */
    public static void addNewStockForProduct(Scanner scanner, Product[] products) {
        System.out.println("---Available stocks:");
        Utils.displayProducts(products);

        System.out.println("---Choose a product to restock... (type in the product's id)");
        int option = scanner.nextInt();
        boolean found = false;

        for (Product product: products) {
            if (product.getId() == option) {
                System.out.println("---Please provide the quantity:");
                int newQuantity = scanner.nextInt();
                product.setAvailableQuantity(product.getAvailableQuantity() + newQuantity);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("---Product with id " + option + " not found!");
        }
    }

    /**
     * Add a new purchase to the array
     * @param sessionPurchases already existing purchases, if any
     * @param purchase purchase to be added to the array
     * @return new array of purchases
     */
    public static PurchasePayload[] enlargeArrayAndAddPurchase(PurchasePayload[] sessionPurchases, PurchasePayload purchase) {
        PurchasePayload[] tempPurchases = new PurchasePayload[sessionPurchases.length];
        int k = 0;
        for (PurchasePayload purchasePayload: sessionPurchases) {
            tempPurchases[k++] = purchasePayload;
        }
        sessionPurchases = new PurchasePayload[tempPurchases.length + 1];
        k = 0;
        for (PurchasePayload purchasePayload: tempPurchases) {
            sessionPurchases[k++] = purchasePayload;
        }
        sessionPurchases[sessionPurchases.length - 1] = purchase;
        return sessionPurchases;
    }

    /**
     * Persist array of purchases into text file
     * @param sessionPurchases already existing purchases, if any
     * @param isAdmin flag if user is admin
     */
    public static void persistPurchases(PurchasePayload[] sessionPurchases, boolean isAdmin) {
        if (isAdmin) {
            System.out.println("---Persisting purchases to textfile " + PURCHASES_DB + "...");
        }
        try(FileWriter fileWriter = new FileWriter(PURCHASES_DB, true)) {
            for (PurchasePayload purchasePayload: sessionPurchases) {

                fileWriter.append(purchasePayload.getUsername()).append("; ");
                fileWriter.append(purchasePayload.getProductList().toString()).append("; ");
                fileWriter.append(purchasePayload.getCreationDate().toString()).append("\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static Product[] removeProductFromStore(Scanner scanner, Product[] products) {
        boolean isEmpty = Utils.displayProducts(products);
        if (isEmpty) return products;

        System.out.println("Please select what product you would like to remove from the store(write the id corresponding):");
        int option = scanner.nextInt();

        Optional<Product> optionalProduct = Arrays.stream(products)
                .filter(product -> product.getId() == option)
                .findFirst();

        if (optionalProduct.isEmpty()) {
            System.out.println("The product provided does not exist in our shop.");
            return products;
        }

        Product[] result = new Product[]{};
        for (Product product: products) {
            if (product.getId() != option) {
                result = enlargeArrayAndAddProduct(result, product);
            }
        }
        return result;
    }

    /**
     * Add a new product to the array
     * @param products already existing products, if any
     * @param product product to be added to the array
     * @return new array of products
     */
    private static Product[] enlargeArrayAndAddProduct(Product[] products, Product product) {
        Product[] tempProduct = new Product[products.length];
        int k = 0;
        for (Product product1: products) {
            tempProduct[k++] = product1;
        }
        products = new Product[tempProduct.length + 1];
        k = 0;
        for (Product product1: tempProduct) {
            products[k++] = product1;
        }
        products[products.length - 1] = product;
        return products;
    }

    private DatabaseService() {}
}
