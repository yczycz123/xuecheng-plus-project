package com.xuecheng.content.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xuecheng.content.service.CoursePublishPreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 课程发布 前端控制器
 * </p>
 *
 * @author ycz
 */
@Slf4j
@RestController
@RequestMapping("coursePublishPre")
public class CoursePublishPreController {

    @Autowired
    private CoursePublishPreService  coursePublishPreService;
}
