package models;

public class MenuPayload {

    private Product[] products;
    private PurchasePayload[] sessionPurchases;
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

    public PurchasePayload[] getSessionPurchases() {
        return sessionPurchases;
    }

    public void setSessionPurchases(PurchasePayload[] sessionPurchases) {
        this.sessionPurchases = sessionPurchases;
    }
}
