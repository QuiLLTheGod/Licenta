package quill.gmail.com.licenta.model;

/**
 * Created by Paul on 1/4/2018.
 */

public class User {

    private int id = 0;
    public static String NAME = null;
    private String email = null;
    private String name = null;
    private String password = null;
    private String hash = null;
    private byte[] salt;
    public int getId(){
        return id;
    }
    public void setID(int id){
        this.id = id;
    }


    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
