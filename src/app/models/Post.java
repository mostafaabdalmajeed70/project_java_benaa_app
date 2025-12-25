package app.models;

public class Post {
    public int id;
    public int customerId;
    public String location;
    public String content;
    public String createdAt;

    public Post(int id, int customerId, String location, String content, String createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.location = location;
        this.content = content;
        this.createdAt = createdAt;
    }

}
