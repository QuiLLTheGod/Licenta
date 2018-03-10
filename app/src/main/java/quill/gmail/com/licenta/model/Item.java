package quill.gmail.com.licenta.model;

/**
 * Created by Paul on 3/4/2018.
 */

public class Item {

    private String data;
    private String password;
    private int id;

    public int getId(){return id;}
    public void setId(int id){ this.id = id; }

    public void setData(String data) {
        this.data = data;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getData() {
        return data;
    }

    public String getPassword() {
        return password;
    }

}
