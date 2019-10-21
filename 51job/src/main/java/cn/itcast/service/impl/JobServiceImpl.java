package cn.itcast.service.impl;

import cn.itcast.dao.JobRepository;
import cn.itcast.domain.JobInfo;
import cn.itcast.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.util.List;
@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobRepository jobRepository;
    @Override
    public List<JobInfo> find(JobInfo jobInfo) {
        Example example=Example.of(jobInfo);
        //
        List list = jobRepository.findAll(example);
       return list;
    }

    @Override
    @Transactional
    public void save(JobInfo jobInfo) {
        //查询是否为空
        JobInfo jobInfo1=new JobInfo();
        jobInfo1.setUrl(jobInfo.getUrl());
        jobInfo1.setTime(jobInfo.getTime());
        List<JobInfo> list = this.find(jobInfo1);
        if(list.size()==0){
            jobRepository.save(jobInfo);
        }

    }
}
