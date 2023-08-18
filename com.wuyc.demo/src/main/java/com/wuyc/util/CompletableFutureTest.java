package com.wuyc.util;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wuyc.vo.StudentVO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author sp0313
 * @date 2023年04月19日 10:03:00
 */
@Slf4j
public class CompletableFutureTest {

    private final static Integer PAGE_SIZE = 3;
    private static volatile ExecutorService EXECUTOR = null;
    public static Integer COUNT = 0;

    private CompletableFutureTest() {
    }

    public static ExecutorService getInstance() {
        if (EXECUTOR == null) {
            synchronized (CompletableFutureTest.class) {
                if (EXECUTOR == null) {
                    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("test-pool-%d").build();
                    EXECUTOR = new ThreadPoolExecutor(10,
                            100,
                            5000L,
                            TimeUnit.MILLISECONDS,
                            new ArrayBlockingQueue<>(300),
                            threadFactory,
                            new ThreadPoolExecutor.CallerRunsPolicy()
                    );
                }
            }
        }
        return EXECUTOR;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<StudentVO> studentList = initSmallStudentList();
        int total = studentList.size();
        int totalPage = total % PAGE_SIZE == 0 ? total / PAGE_SIZE : total / PAGE_SIZE + 1;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < totalPage; i++) {
            final int startIndex = i * PAGE_SIZE;
            final int endIndex = Math.min(startIndex + PAGE_SIZE, total);

//            CompletableFuture.runAsync(() -> handleInfo(startIndex, endIndex, studentList)).get();
//            CompletableFuture<List<StudentVO>> completableFuture = CompletableFuture.supplyAsync(CompletableFutureTest::initSmallStudentList);
            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> handleInfo(startIndex, endIndex, studentList));
            futureList.add(completableFuture);
        }

        for (CompletableFuture<Void> feature : futureList) {
            feature.get();
        }
        System.out.println(COUNT);
    }

    private static void handleInfo(int startIndex, int endIndex, List<StudentVO> studentList) {
        studentList.subList(startIndex, endIndex).forEach(data -> {
            System.out.println(data.getName());
            COUNT++;
        });
    }

    public static List<StudentVO> initSmallStudentList() {
        return Stream.of(
                new StudentVO(20, 60, "张三", 1, Lists.newArrayList("张三01", "张三01", "张三02")),
                new StudentVO(60, 170, "李四", 1, Lists.newArrayList("李四01", "李四01", "李四02")),
                new StudentVO(45, 140, "王五", 1, Lists.newArrayList("王五01", "王五01", "王五2")),
                new StudentVO(55, 165, "梦莹1", 2, Lists.newArrayList("梦莹09", "梦莹08", "梦莹08")),
                new StudentVO(55, 165, "梦莹2", 2, Lists.newArrayList("梦莹01", "梦莹02", "梦莹03")),
                new StudentVO(55, 165, "梦莹3", 2, Lists.newArrayList("梦莹09", "梦莹08", "梦莹08")),
                new StudentVO(55, 165, "梦莹4", 2, Lists.newArrayList("梦莹01", "梦莹02", "梦莹03")),
                new StudentVO(55, 165, "梦莹5", 2, Lists.newArrayList("梦莹09", "梦莹08", "梦莹08")),
                new StudentVO(55, 165, "梦莹6", 2, Lists.newArrayList("梦莹01", "梦莹02", "梦莹03")),
                new StudentVO(55, 165, "梦莹7", 2, Lists.newArrayList("梦莹09", "梦莹08", "梦莹08")),
                new StudentVO(40, 130, "杨迪", 2, Lists.newArrayList("杨迪01", "杨迪01", "杨迪02")),
                new StudentVO(40, 130, "杨迪2", 2, Lists.newArrayList("杨迪01", "杨迪01", "杨迪02")),
                new StudentVO(55, 120, "杨迪", 2, Lists.newArrayList("杨迪03", "杨迪04", "杨迪05")))
                .collect(Collectors.toList());
    }

}
