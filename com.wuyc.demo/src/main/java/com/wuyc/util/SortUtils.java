package com.wuyc.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wuyc.vo.StudentVO;
import lombok.Data;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author sp0313
 * @date 2023年05月25日 16:52:00
 */
public class SortUtils {

    public static void main(String[] args) {

        List<User> userList = initUserList();
        Collator collator = Collator.getInstance(Locale.CHINA);
        userList.sort((v1, v2) -> collator.compare(v1.getName(), v2.getName()));
        System.out.println(JSON.toJSONString(userList, true));

//        List<User> dataList = initUserList().stream()
//                .sorted(Comparator.comparing(User::getName))
//                .collect(Collectors.toList());
//        System.out.println(JSON.toJSONString(dataList, true));
    }


    public static List<User> initUserList() {
        return Stream.of(
                new User("张3三"),
                new User("李四"),
                new User("王五"),
                new User("1111"),
                new User("8888"),
                new User("4444"),
                new User("3333"),
                new User("aaaa"),
                new User("gggg"),
                new User("cccc"),
                new User("张1三"),
                new User("ddddd"),
                new User("阿啊啊啊"))
                .collect(Collectors.toList());
    }



    @Data
    static class User {
        private String name;


        User(String name) {
           this.name = name;
        }
    }

}
