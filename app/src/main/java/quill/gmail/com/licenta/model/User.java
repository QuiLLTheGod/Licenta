package quill.gmail.com.licenta.model;

/**
 * Created by Paul on 1/4/2018.
 */

public class User {

    private int id;
    public static String NAME;
    private String email;
    private String password;
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

}
