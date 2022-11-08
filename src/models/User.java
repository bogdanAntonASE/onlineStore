package models;


import java.io.Serial;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Objects;

import static util.Utils.DECIMAL_FORMAT;

public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567L;
    private static long counter = 0;

    private long id;
    private String userName = "";
    private String password = "";
    private boolean isAdmin;
    private Float credit = 0.0f;

    public User() {
        this.id = counter++;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", isAdmin=" + isAdmin +
                ", credit=" + DECIMAL_FORMAT.format(credit) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (isAdmin != user.isAdmin) return false;
        if (!Objects.equals(userName, user.userName)) return false;
        if (!Objects.equals(password, user.password)) return false;
        return Objects.equals(credit, user.credit);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (isAdmin ? 1 : 0);
        result = 31 * result + (credit != null ? credit.hashCode() : 0);
        return result;
    }
}
