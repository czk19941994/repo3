package cn.itcast.demo;

public class ThreadDemo2 {
    public static void main(String[] args) {
        SubRunnable subRunnable=new SubRunnable();
        Thread thread=new Thread(subRunnable);
        thread.start();
        System.out.println("陈");
    }
}
