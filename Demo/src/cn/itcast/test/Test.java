package cn.itcast.test;

public class Test {
    public static void main(String[] args) {
        Thread t0=new Thread(new ThreadTest());
        boolean interrupted = t0.isInterrupted();
        System.out.println(interrupted);
        t0.start();
        t0.interrupt();
        boolean interrupted1 = t0.isInterrupted();
        boolean interrupted2 = Thread.interrupted();
        System.out.println(interrupted2);
        System.out.println(interrupted1);
        t0.interrupt();

    }
}
