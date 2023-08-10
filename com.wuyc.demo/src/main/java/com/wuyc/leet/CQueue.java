package com.wuyc.leet;

import java.util.LinkedList;

/**
 * 两个栈实现一个队列 - 剑指offer 09
 * 先进后出，新增的在下面，先删上面的
 * 时间复杂度  O(1)
 * 空间复杂度  O(N)
 *
 * @author sp0313
 * @date 2023年07月24日 13:46:00
 */
public class CQueue {

    public static Integer DEFAULT_DELETE_RESULT = -1;
    public static LinkedList<Integer> valueList;

    public CQueue() {
        valueList = new LinkedList<>();
    }

    void appendTail(int value) {
        valueList.add(value);
    }

    int deleteHead() {
        return valueList == null || valueList.size() <= 0 ? DEFAULT_DELETE_RESULT : valueList.removeFirst();
    }

    public static void main(String[] args) {
//        String[] arr = new String[]{"CQueue", "appendTail", "deleteHead", "deleteHead", "deleteHead"};
        CQueue cQueue = new CQueue();
        cQueue.appendTail(3);
        cQueue.deleteHead();
    }

}
