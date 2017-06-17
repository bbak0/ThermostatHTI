package util;

/**
 * Created by Zordon on 16.06.2017.
 */

public class Utils {
     public static String getTempString(int x){

        int a =  x/10;
        int b = x%10;
        return a + "." + b+ "°C";
    }
}
