package dto;

import models.Product;
import models.Purchase;

public class MenuPayload {

    private Product[] products;
    private Purchase[] sessionPurchases;
    private boolean isLoggedIn;

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public Purchase[] getSessionPurchases() {
        return sessionPurchases;
    }

    public void setSessionPurchases(Purchase[] sessionPurchases) {
        this.sessionPurchases = sessionPurchases;
    }
}
