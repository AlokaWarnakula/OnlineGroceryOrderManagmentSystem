package model;

public class Admin {
    private String username;
    private String password;
    private String userNumber;

    public Admin(String username, String password, String userNumber) {
        this.username = username;
        this.password = password;
        this.userNumber = userNumber;
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

    @Override
    public String toString() {
        return "Admin{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userNumber='" + userNumber + '\'' +
                '}';
    }
}