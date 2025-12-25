package app.models;

public class Order {

    private int orderID;
    private Customer customer;
    private Material material;
    private int quantity;
    private String status;

    public Order(int orderID, Customer customer, Material material, int quantity, String status) {
        this.orderID = orderID;
        this.customer = customer;
        this.material = material;
        this.quantity = quantity;
        this.status = status;
    }


    public double getTotal() { return material.getPrice() * quantity; }


    @Override
    public String toString() {
        String cust = (customer != null) ? customer.name : "CustomerID?";
        return "OrderID:" + orderID +
                " | Customer: " + cust +
                " | Material: " + (material!=null?material.getName():"?") +
                " | Qty: " + quantity +
                " | Total: " + getTotal() +
                " | Status: " + (status == null ? "?" : status);
    }
}
