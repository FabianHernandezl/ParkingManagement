package model.entity;

public class Client {

    private String id;
    private String name;
    private String phone;
    private boolean preferential;

    public Client() {
    }

    public Client(String id, String name, String phone, boolean preferential) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.preferential = preferential;
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

    public boolean isPreferential() {
        return preferential;
    }

    public void setPreferential(boolean preferential) {
        this.preferential = preferential;
    }

    @Override
    public String toString() {
        return "Client: " +
                "id:'" + id + '\'' +
                ", name:'" + name + '\'' +
                ", phone:'" + phone + '\'' +
                ", preferential:" + preferential +
                '}';
    }
}

