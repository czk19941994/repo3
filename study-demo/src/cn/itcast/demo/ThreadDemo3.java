package cn.itcast.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadDemo3 {
    public static void main(String[] args)throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Future submit = executorService.submit(new SubCallable());
        //拿到返回值
        submit.get();
    }
}
