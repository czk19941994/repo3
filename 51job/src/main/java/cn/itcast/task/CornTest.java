package cn.itcast.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CornTest {
    @Scheduled(cron = "0/5 * * * * *")
    public void corn(){
        System.out.println("dandan");
    }
}
