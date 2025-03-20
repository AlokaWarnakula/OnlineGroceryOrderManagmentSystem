package model;

public class User {
    private String username;
    private String password;
    private String userNumber;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;

    public User(String username, String password, String userNumber, String fullName, String email, String phoneNumber, String address) {
        this.username = username;
        this.password = password;
        this.userNumber = userNumber;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "username=" + username + ",password=" + password + ",userNumber=" + userNumber +
                ",fullName=" + fullName + ",email=" + email + ",phoneNumber=" + phoneNumber +
                ",address=" + address;
    }
}