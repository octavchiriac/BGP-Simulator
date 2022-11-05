package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserList {
    //FROM LIST TO STRING
    // convert the list of maps to a string
    public static String parseList(List<Map<Integer,String>> list) {
        String result = "";
        System.out.println("ParserList.parseList");
        System.out.println("list: " + list);
        for (Map<Integer,String> o : list) {
            result += ParserList.MapToString(o) + ";";
            System.out.println("result: " + result);
        }
        result.substring(0, result.length() - 1);
        return result;
    }

    //convert the map to a string
    public static String MapToString(Map<Integer,String> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (Integer key: map.keySet()) {
            mapAsString.append(key + "=" + map.get(key));
        }
        return mapAsString.append("}").toString();
    }

    //INVERSE OPERATION
    //convert the string to a list of maps
    public static ArrayList<Map<Integer,String>> parseString(String mapString){
        List<Map<Integer,String>> tmpList = new ArrayList<Map<Integer,String>>();
        String[] mapStringArray = mapString.split(";");
        for (String o : mapStringArray) {
            tmpList.add(ParserList.StringToMap(o));
        }
        return (ArrayList<Map<Integer,String>>) tmpList;
    }

    //for each entry in the string (which corresponds to a map entry), split the string and convert it into a map
    public static Map<Integer,String> StringToMap(String mapString){
        Map<Integer,String> map = new HashMap<Integer,String>();
        String[] mapStringArray = mapString.split(";");
        for (String o : mapStringArray) {
            map.put(Integer.parseInt(o.split("=")[0]), o.split("=")[1]);
        }
        return map;

    }

}
