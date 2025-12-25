package app.models;

public class Supplier extends User {

    private String shopLocation;
    public boolean delivery; // kept public for simplicity

    public Supplier(int id, String name, String phone, String email, String shopLocation, boolean delivery) {
        super(id, name, phone, email, "supplier");
        this.shopLocation = shopLocation;
        this.delivery = delivery;
    }

    public int getUserID() { return userID; }

    // supplier's "location" for display/use is shopLocation
    public String getLocation() { return shopLocation; }
    public String getShopLocation() { return shopLocation; }
    public boolean hasDelivery() { return delivery; }
}
