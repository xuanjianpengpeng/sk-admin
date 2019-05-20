package com.dxj.modules.system.rest;

import com.dxj.aop.log.Log;
import com.dxj.config.DataScope;
import com.dxj.enums.EntityEnums;
import com.dxj.exception.BadRequestException;
import com.dxj.modules.system.domain.Job;
import com.dxj.modules.system.dto.JobDTO;
import com.dxj.modules.system.service.JobService;
import com.dxj.modules.system.query.JobQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author dxj
 * @date 2019-03-29
 */
@RestController
@RequestMapping("api")
public class JobController {

    private final JobService jobService;

    private final JobQueryService jobQueryService;

    private final DataScope dataScope;

    @Autowired
    public JobController(JobService jobService, JobQueryService jobQueryService, DataScope dataScope) {
        this.jobService = jobService;
        this.jobQueryService = jobQueryService;
        this.dataScope = dataScope;
    }

    @Log("查询岗位")
    @GetMapping(value = "/job")
    @PreAuthorize("hasAnyRole('ADMIN', 'USERJOB_ALL', 'USERJOB_SELECT', 'USER_ALL', 'USER_SELECT')")
    public ResponseEntity<Object> getJobs(@RequestParam(required = false) String name,
                                          @RequestParam(required = false) Long deptId,
                                          @RequestParam(required = false) Boolean enabled,
                                          Pageable pageable) {
        // 数据权限
        Set<Long> deptIds = dataScope.getDeptIds();
        return new ResponseEntity<>(jobQueryService.queryAll(name, enabled, deptIds, deptId, pageable), HttpStatus.OK);
    }

    @Log("新增岗位")
    @PostMapping(value = "/job")
    @PreAuthorize("hasAnyRole('ADMIN','USERJOB_ALL','USERJOB_CREATE')")
    public ResponseEntity<JobDTO> create(@Validated @RequestBody Job resources) {
        if (resources.getId() != null) {
            throw new BadRequestException("A new " + EntityEnums.JOB_ENTITY + " cannot already have an ID");
        }
        return new ResponseEntity<>(jobService.create(resources), HttpStatus.CREATED);
    }

    @Log("修改岗位")
    @PutMapping(value = "/job")
    @PreAuthorize("hasAnyRole('ADMIN','USERJOB_ALL','USERJOB_EDIT')")
    public ResponseEntity<Void> update(@Validated(Job.Update.class) @RequestBody Job resources) {
        jobService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除岗位")
    @DeleteMapping(value = "/job/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USERJOB_ALL','USERJOB_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
