package app.models;

import java.util.Scanner;

public class User {

    protected int userID;
    public String name;
    protected String phone;
    protected String email;
    public String role;

    protected Scanner input = new Scanner(System.in);
    private String location;

    public User(int id, String name, String phone, String email, String role) {
        this.userID = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public User() {}

    public int getUserID() { return userID; }

    // helper getters used by DAO / GUI
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }

    public String getLocation() { return location; }

    // safe getter to avoid NPEs in UI/DAOs
    public String getLocationSafe() { return location == null ? "" : location.trim(); }

    public void setLocation(String location) { this.location = location; }

    // placeholder (override in subclasses if needed)
    public void editProfile(String finalName, String finalSpec, int exp, double rating) { }

}
