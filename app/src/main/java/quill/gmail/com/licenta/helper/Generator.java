package quill.gmail.com.licenta.helper;

import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private String createNumbers(){
        if(numbers == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder(numbers);
        char[] temp = NR.toCharArray();
        for(int i=0;i<numbers;i++){
            stringBuilder.append(temp[randomizer.nextInt(temp.length)]);
        }
        return stringBuilder.toString();
    }

    private boolean createChoices(){
        if(highChar)
            options +=HIGH;
        if(lowChar)
            options +=LOW;
        if(specialChar)
            options +=SPECIAL;
        if(!options.isEmpty()) {
            choices = options.toCharArray();
            return true;
        }
        else
            password = "FAILED/NULL password";
        return false;
    }

    private String generatePassword(){
        if(length-numbers>0) {
            StringBuilder stringBuilder = new StringBuilder(length - numbers);
            if (createChoices()) {
                for (int i = 0; i < (length - numbers); i++) {
                    stringBuilder.append(choices[randomizer.nextInt(choices.length)]);
                }
            }
            return stringBuilder.toString();
        }
        return "";
    }

    private String shuffleString(String word) {
        List<Character> characters = new ArrayList<Character>();
        for(char c : word.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder sb = new StringBuilder();
        for(char c : characters) {
            sb.append(c);
        }
        return sb.toString();
    }

    private String genPass(){
        String shuffledPass = generatePassword() + createNumbers();
        if(shuffledPass.isEmpty())
            return "Please input some choices";
        return shuffleString(shuffledPass);
    }

    public String getPassword() {
        password = genPass();
        return password;
    }
}
