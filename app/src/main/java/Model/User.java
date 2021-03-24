package Model;

public class User {
    int avtResource;
    String userName;

    public User(int avtResource, String userName) {
        this.avtResource = avtResource;
        this.userName = userName;
    }

    public int getAvtResource() {
        return avtResource;
    }

    public void setAvtResource(int avtResource) {
        this.avtResource = avtResource;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
