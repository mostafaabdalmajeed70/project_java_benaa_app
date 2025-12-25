package app.models;

import app.data.UserDAO;

public class Material {

    private int materialID;
    private String name;
    private double price;
    private int quantity;
    private int supplierID;

    public Material(int materialID, String name, double price, int quantity, int supplierID) {
        this.materialID = materialID;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.supplierID = supplierID;
    }

    public int getMaterialID() { return materialID; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getSupplierID() { return supplierID; }

    public void setQuantity(int q) { this.quantity = q; }

    @Override
    public String toString() {
        String supplierName = "Unknown";
        Supplier s = UserDAO.findSupplierById(supplierID);
        if (s != null) supplierName = s.name;
        return materialID + " | " + name + " | " + price + " EGP | qty: " + quantity + " | supplier: " + supplierName;
    }

    public int getId() {
        return materialID;
    }

    public void setId(int id) {
        this.materialID = id;
    }
}
