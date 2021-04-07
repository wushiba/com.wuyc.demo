package com.yfshop.common.util;

public class StringUtil {

    public static String xor(String str1, String str2) {
        byte b1[] = str1.getBytes();
        byte b2[] = str2.getBytes();
        byte longbytes[],shortbytes[];
        if(b1.length>=b2.length){
            longbytes = b1;
            shortbytes = b2;
        }else{
            longbytes = b2;
            shortbytes = b1;
        }
        byte xorstr[] = new byte[longbytes.length];
        int i = 0;
        for (; i < shortbytes.length; i++) {
            xorstr[i] = (byte)(shortbytes[i]^longbytes[i]);
        }
        for (;i<longbytes.length;i++){
            xorstr[i] = longbytes[i];
        }
        return new String(xorstr);
    }
}
