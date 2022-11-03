package utils;

import java.util.List;
import java.util.Map;

public class ParserList {
    //FROM LIST TO STRING
    // convert the list of maps to a string
    public static String parseList(List<Map<Integer,String>> list) {
        String result = "";
        for (Map<Integer,String> o : list) {
            result += ParserList.MapToString(o) + ";";
        }
        result.substring(0, result.length() - 1);
        return result;
    }

    //convert the map to a string
    public static String MapToString(Map<Integer,String> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (Integer key: map.keySet()) {
            mapAsString.append(key + "=" + map.get(key) + ",");
        }
        return mapAsString.append("}").toString();
    }

    //INVERSE OPERATION
    public static String parseString(String mapString){
        String result = "";
        String[] mapStringArray = mapString.split(";");
        for (String o : mapStringArray) {
            result += ParserList.StringToMap(o) + ";";
        }
        result.substring(0, result.length() - 1);
        return result;
    }

    public static String StringToMap(String mapString){
        StringBuilder mapAsString = new StringBuilder("{");
        String[] mapStringArray = mapString.split(",");
        for (String o : mapStringArray) {
            String[] mapStringArray2 = o.split("=");
            mapAsString.append(mapStringArray2[0] + "=" + mapStringArray2[1] + ",");
        }
        return mapAsString.append("}").toString();
    }

}
