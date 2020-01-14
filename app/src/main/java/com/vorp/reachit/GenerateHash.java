package com.vorp.reachit;

import java.util.Random;

/**
 * Created by asd on 2018-07-07.
 */

public abstract class GenerateHash {

    private static final String numbers = "0123456789";
    private static final String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lowerCase = upperCase.toLowerCase();
    private static final String charBucket = upperCase+lowerCase+numbers;
    private static Random charPicker = new Random();

    public static String getHash(int length)
    {
        StringBuffer output=new StringBuffer("");
        for(int i=0;i<length;i++) {
            output.append(charBucket.charAt(charPicker.nextInt(charBucket.length())));
        }
        return output.toString();
    }
}
