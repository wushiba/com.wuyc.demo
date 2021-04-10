package com.yfshop.shop.utils;

import org.lionsoul.ip2region.*;

import java.io.File;
import java.io.IOException;

public class Ip2regionUtil {
    static DbSearcher searcher = null;

    static {
        String fileDb = System.getProperty("user.dir") + File.separator+"db"+File.separator+"ip2region.db";
        System.out.println(fileDb);
        File file = new File(fileDb);
        if (!file.exists()) {
            System.out.println("Error: Invalid ip2region.db file");
        } else {
            String algoName = "B-tree";
            try {
                System.out.println("initializing " + algoName + " ... ");
                DbConfig config = new DbConfig();
                searcher = new DbSearcher(config, fileDb);
                System.out.println("+----------------------------------+");
                System.out.println("| ip2region test shell             |");
                System.out.println("| Author: chenxin619315@gmail.com  |");
                System.out.println("| Type 'quit' to exit program      |");
                System.out.println("+----------------------------------+");
            } catch (IOException var14) {
                var14.printStackTrace();
            } catch (DbMakerConfigException var15) {
                var15.printStackTrace();
            } catch (SecurityException var17) {
                var17.printStackTrace();
            }

        }

    }

    public static DataBlock searchIp(String ip) {
        if (!Util.isIpAddress(ip)) {
            System.out.println("Error: Invalid ip address");
        } else {
            try {
                return searcher.btreeSearch(ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Ip2regionUtil.searchIp("115.192.37.107"));
    }

}
