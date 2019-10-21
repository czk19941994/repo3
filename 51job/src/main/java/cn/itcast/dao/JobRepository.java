package cn.itcast.dao;

import cn.itcast.domain.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<JobInfo,Long> {
}
