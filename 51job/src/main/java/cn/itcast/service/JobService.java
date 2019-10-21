package cn.itcast.service;

import cn.itcast.domain.JobInfo;

import java.util.List;

public interface JobService {
    List<JobInfo> find(JobInfo jobInfo);
    void  save(JobInfo jobInfo);
}
