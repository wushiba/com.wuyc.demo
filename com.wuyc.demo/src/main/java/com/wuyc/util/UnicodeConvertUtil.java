package com.wuyc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * unicode 与字符串转换工具类
 *
 * @author sp0313
 * @date 2023年04月07日 16:53:00
 */
public class UnicodeConvertUtil {

    public static void main(String[] args) throws Exception {
        initMessageEnum();
    }

    public static void initMessageEnum() throws Exception {
        Path path = Paths.get("D:/enums/bbb.txt");
        List<String> stringList = Files.readAllLines(path);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringList.size(); i++) {
            String[] split = stringList.get(i).split("=");
            String message = unicodeToCnNew(split[1]);
            System.out.println("第" + (i + 1) + "行的数据是:  " + message);

            String errorCode = split[0];
            stringBuilder.append(errorCode.replace(".", "_").toUpperCase())
                    .append("(\"" + split[0]).append("\", ")
                    .append("\"" + message + "\"),\r\n");
        }

        initOutputStream(stringBuilder.toString());
    }

    public static void initOutputStream(String message) throws Exception{
        File file = new File("D:/enums/enum.txt") ;
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs() ;
        }
        OutputStream outputStream = new FileOutputStream(file, true) ;
        outputStream.write(message.getBytes());
    }

    public static String unicodeToCnNew(String unicode) {
        StringBuilder resultStr = new StringBuilder();
        String[] strArr = unicode.split("\\\\u");
        if (!strArr[0].isEmpty()) {
            resultStr.append(strArr[0]);
        }
        for (int i = 1; i < strArr.length; i++) {
            String unicodeStr = strArr[i];
            if (unicodeStr.length() < 4) {
                continue;
            }
            resultStr.append((char) Integer.valueOf(unicodeStr.substring(0, 4), 16).intValue());
            if (unicodeStr.length() > 4) {
                resultStr.append(unicodeStr.substring(4));
            }
        }
        return resultStr.toString();
    }

}
