package cn.itcast.demo;

public class ThreadDemo {
    public static void main(String[] args) {
        SubThread subThread=new SubThread();
        subThread.start();
        for (int i=0;i<10;i++){
        System.out.println(i);
    }
    }
}
