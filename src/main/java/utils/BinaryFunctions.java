package utils;

public class BinaryFunctions {

	public static Object bitsArrayToObject(String bitsArray, int offset, int size, Class<?> type) {
		Object obj = null;
		String subst = "";
		
		if(size > -1) {
			subst = bitsArray.substring(offset, offset + size);
		} else {
			subst = bitsArray.substring(offset, bitsArray.length());
		}
		
		if(type.toString().contains("Integer")) {
			obj = Integer.parseInt(subst, 2);
		} else if(type.toString().contains("Long")) {
			obj = Long.parseLong(subst, 2);
		} else if(type.toString().contains("Boolean")) {
			obj = subst.equals("1") ? true : false;
		} else if(type.toString().contains("Address")){
			obj = convertBinaryToIp(subst);
		} else {
			obj = convertBinaryToString(subst);
		}
		return obj;
	}
	
	public static String toBitsArray (Object input, int size) {
		String bitsArray = "";
		
		if(input instanceof Boolean) {
			bitsArray += (boolean) input ? "1" : "0";
		} else if (input instanceof Integer) {
			bitsArray += String.format("%" + size + "s", Integer.toBinaryString((int) input))
					.replace(' ','0');
		} else if (input instanceof Long) {
		bitsArray += String.format("%" + size + "s", Long.toBinaryString((long) input))
				.replace(' ','0');
		} else {
			if(input.toString().contains(".")) {
				bitsArray += convertIpToBinary((String) input);
			} else {
				bitsArray += convertStringToBinary((String) input);
			}
		}
		
		return bitsArray;
	}
	
	private static String convertStringToBinary(String input) {

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))
                            .replaceAll(" ", "0")
            );
        }
        return result.toString();
    }
	
	private static String convertBinaryToString(String input) {
		StringBuilder stringBuilder = new StringBuilder();
	    int charCode;
	    
	    for (int i = 0; i < input.length(); i += 8) {
	        charCode = Integer.parseInt(input.substring(i, i + 8), 2);
	        String returnChar = Character.toString((char) charCode);
	        stringBuilder.append(returnChar);
	    }

	    return stringBuilder.toString();
	}
	
	private static String convertIpToBinary(String ip) {

        String binaryIp = "";
        String[] tokens = ip.split("\\.");

        for (String token : tokens) {
            String binaryToken = String.format("%8s", Integer.toBinaryString(Integer.parseInt(token))).replace(' ',
                    '0');
            binaryIp += binaryToken;
        }

        return binaryIp;
    }
	
	private static String convertBinaryToIp(String input) {
		StringBuilder stringBuilder = new StringBuilder();
	    int charCode;
	    
	    for (int i = 0; i < input.length(); i += 8) {
	        charCode = Integer.parseInt(input.substring(i, i + 8), 2);
	        stringBuilder.append(charCode);
	        stringBuilder.append(".");
	    }

	    return stringBuilder.toString().substring(0, stringBuilder.length() - 1);
	}
}
