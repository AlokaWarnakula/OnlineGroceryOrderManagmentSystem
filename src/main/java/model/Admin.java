package model;

public class Admin {
    private String adminNumber;
    private String email;
    private String password;
    private String role; // Added role field

    // Constructor
    public Admin(String adminNumber, String email, String password, String role) {
        this.adminNumber = adminNumber;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getAdminNumber() {
        return adminNumber;
    }

    public void setAdminNumber(String adminNumber) {
        this.adminNumber = adminNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Admin [adminNumber=" + adminNumber + ", email=" + email + ", role=" + role + "]";
    }
}