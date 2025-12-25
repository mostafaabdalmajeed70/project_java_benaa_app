package app.models;

public class Worker extends User {

    public String specialization;
    public int experience;
    public double rating;
    private String location;

    public Worker(int id, String name, String phone, String email,
                  String specialization, double rating, int experience, String location) {
        super(id, name, phone, email, "worker");
        this.specialization = specialization;
        this.experience = experience;
        this.rating = rating;
        this.location = location;
        // also set parent's location for convenience
        setLocation(location);
    }

    @Override
    public String getLocation() {
        return location;
    }

    public String getSpecialization() { return specialization; }
    public int getExperience() { return experience; }
    public double getRating() { return rating; }
}
