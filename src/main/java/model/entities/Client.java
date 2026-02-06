package model.entities;

/**
 *
 * @author Jimena
 */
public class Client {

    private String id;
    private String name;
    private String phone;
    private boolean isPreferential;
    private String email;

    public Client() {
    }

    public Client(String id, String name, String phone, boolean isPreferential, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.isPreferential = isPreferential;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isIsPreferential() {
        return isPreferential;
    }

    public void setIsPreferential(boolean isPreferential) {
        this.isPreferential = isPreferential;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + " | Name: " + name
                + " | Phone: " + phone
                + " | Preferential: " + (isPreferential ? "Yes" : "No"
                        + "Email: " + email);
    }

}
