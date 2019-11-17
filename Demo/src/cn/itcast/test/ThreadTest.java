package cn.itcast.test;

public class ThreadTest implements Runnable {

    @Override
    public void run() {
        System.out.println(111);
    }
}
