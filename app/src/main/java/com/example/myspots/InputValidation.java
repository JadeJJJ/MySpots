package com.example.myspots;

import android.content.Context;
import android.widget.Toast;

public class InputValidation {
    //Constructor method
    public InputValidation()
    {

    }

    //Method to validate a string is not null or empty
    public boolean NotNullorEmpty(String sInput)
    {
        return !(sInput.isEmpty() || sInput.equals(null));
    }

    //Method to check if a string contains a number
    public boolean ContainsNumber(String sInput)
    {
        boolean bFlag = false;
        char[] arrInput = sInput.toCharArray();

        for(char c: arrInput)
        {
            if(Character.isDigit(c))
            {
                bFlag = true;
                break;
            }
        }
        return bFlag;
    }

    //Method to check if a string contains an Uppercase character
    public boolean ContainsUpcase(String sInput)
    {
        boolean bFlag = false;
        char[] arrInput = sInput.toCharArray();

        for(char c: arrInput)
        {
            if(Character.isUpperCase(c))
            {
                bFlag = true;
                break;
            }
        }
        return bFlag;
    }



    // this method shows an error message with the string it is given
    public void msg(String sMsg, Context context) {
        Toast.makeText(context, sMsg, Toast.LENGTH_SHORT).show();
    }

    // validates the password
    public boolean ValidatePassword(String sPass, Context context) {
        if (sPass.length() <= 5)
        {
            msg("Password Must be 6 Characters!!", context);
            return false;
        } else if (!ContainsNumber(sPass))
        {
            msg("Password Must Contain a Number!!", context);
            return false;
        } else if (!ContainsUpcase(sPass))
        {
            msg("Password Must Contain a Capital Letter!!", context);
            return false;
        }

        return true;
    }


}
