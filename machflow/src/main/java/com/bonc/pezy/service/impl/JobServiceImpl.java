package com.bonc.pezy.service.impl;

import static com.google.common.collect.Iterables.toArray;

import com.bonc.pezy.dao.JobRepository;
import com.bonc.pezy.entity.Job;
import com.bonc.pezy.service.JobService;
import com.bonc.pezy.vo.JobQuery;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Created by 冯刚 on 2018/7/23.
 */
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public Job create(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Job save(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Job findByJobId(String jobId) {
        return jobRepository.findByJobId(jobId);
    }

    @Override
    public List<Job> findByModelId(String modelid) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        return jobRepository.findByModelId(modelid,sort);
    }

    @Override
    public Page<Job> findJobs(Integer pageNumber, Integer pageSize, JobQuery jobQuery) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.DESC, "createTime");
        Page<Job> jobs = jobRepository.findAll(new Specification<Job>() {
            @Override
            public Predicate toPredicate(Root<Job> root, CriteriaQuery<?> criteriaQuery,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList();
                if (null != jobQuery.getOwner()) {
                    predicates.add(criteriaBuilder
                            .equal(root.get("owner").as(Long.class), jobQuery.getOwner()));
                }
                if (null != jobQuery.getModelType()) {
                    predicates.add(criteriaBuilder
                            .equal(root.get("modelType").as(Short.class), jobQuery.getModelType()));
                }
                if (StringUtils.isNotEmpty(jobQuery.getCreateTimeBegin())) {
                    predicates.add(criteriaBuilder
                            .greaterThanOrEqualTo(root.get("createTime").as(String.class),
                                    jobQuery.getCreateTimeBegin()));
                }
                if (StringUtils.isNotEmpty(jobQuery.getCreateTimeEnd())) {
                    predicates.add(criteriaBuilder
                            .lessThanOrEqualTo(root.get("createTime").as(String.class),
                                    jobQuery.getCreateTimeEnd()));
                }
                if (StringUtils.isNotEmpty(jobQuery.getModelName())) {
                    predicates.add(criteriaBuilder
                            .like(root.get("modelName").as(String.class),
                                    "%"+jobQuery.getModelName()+"%"));
                }
                return criteriaBuilder.and(toArray(predicates, Predicate.class));
            }
        }, pageable);
        return jobs;
    }

    @Override
    public void updateByJobId(int status, String jobId) {
        jobRepository.updateByJobId(status,jobId);
    }
}
