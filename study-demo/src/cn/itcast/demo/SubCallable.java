package cn.itcast.demo;

import java.util.concurrent.Callable;

public class SubCallable implements Callable {
    @Override
    public Object call() throws Exception {
        return "陈彰坤";
    }
}
