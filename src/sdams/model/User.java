package sdams.model;

public abstract class User {
    private String userId;
    private String fullName;
    private String role;
    private String email;
    private String phone;
    private String passwordHash;
    private String status;

    protected User(String userId, String fullName, String role, String email,
                   String phone, String passwordHash, String status) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    public abstract void openMenu();

    public String toFileString() {
        return String.join("|", userId, fullName, role, email, phone, passwordHash, status);
    }

    public String getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}