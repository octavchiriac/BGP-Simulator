package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TypeHandlers {
    public static String getCurrentTimeFromMillis(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
        Date resultdate = new Date(millis);
        return sdf.format(resultdate);
    }
}