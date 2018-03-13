package quill.gmail.com.licenta.helper;

import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.Random;

import quill.gmail.com.licenta.R;

/**
 * Created by Paul on 3/13/2018.
 */

public class Generator {

    private boolean highChar;
    private boolean lowChar;
    private boolean specialChar;
    private int numbers;
    private int length;
    private Random randomizer;

    private String password;
    private String options = "";
    private char[] choices;
    private static final String NR = "0123456789";
    private static final String LOW = "qwertyuiopasdfghjklzxcvbnm";
    private static final String HIGH = "QWERTYUIOPASDFGHJKLZXCVBNM";
    private static final String SPECIAL = "!@#$%^&*?";
    //private static final char[] lowcharChoices = ("qwertyuiopasdfghjklzxcvbnm").toCharArray();
    //private static final char[] highCharChoices = ("QWERTYUIOPASDFGHJKLZXCVBNM").toCharArray();

    public Generator(boolean high, boolean low, boolean special, int numbers, int length){
        highChar = high;
        lowChar = low;
        specialChar = special;
        this.numbers = numbers;
        this.length = length;
        randomizer = new Random();
    }

    private void createChoices(){
        if(highChar)
            options +=HIGH;
        if(lowChar)
            options +=LOW;
        if(numbers > 0)
            options +=NR;
        if(specialChar)
            options +=SPECIAL;
        if(options !=null)
            choices = options.toCharArray();
        else
            password = "FAILED/NULL password";
    }

    public void generatePassword(){
        StringBuilder stringBuilder = new StringBuilder(length);
        createChoices();
        for(int i= 0;i<length;i++){
            stringBuilder.append(choices[randomizer.nextInt(choices.length)]);
        }
        password = stringBuilder.toString();
    }

    public String getPassword() {
        return password;
    }
}
