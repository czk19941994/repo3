package cn.itcast.task;

import cn.itcast.domain.JobInfo;
import cn.itcast.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
@Component
public class SpringdataPipline implements Pipeline {
    @Autowired
    private JobService jobService;
    @Override
    public void process(ResultItems resultItems, Task task) {
        //获得封装的
        JobInfo jobInfo = resultItems.get("jobInfo");
        if(jobInfo!=null){
            jobService.save(jobInfo);
        }
    }
}
