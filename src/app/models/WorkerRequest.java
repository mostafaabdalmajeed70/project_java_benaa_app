package app.models;

public class WorkerRequest {
    public int id;
    public int customerId;
    public int workerId;
    public String message;
    public String status;
    public String createdAt;

    public WorkerRequest(int id, int customerId, int workerId, String message, String status, String createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.workerId = workerId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }
}
