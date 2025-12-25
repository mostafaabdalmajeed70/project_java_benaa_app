package app.models;

public class Customer extends User {

    public Customer(int userID, String name, String phone, String email, String preferredLocation) {
        super(userID, name, phone, email, "customer");
        setLocation(preferredLocation);
    }

    public String getPreferredLocation() {
        return getLocation();
    }

    public String getPhoneSafe() {
        return phone;
    }


}