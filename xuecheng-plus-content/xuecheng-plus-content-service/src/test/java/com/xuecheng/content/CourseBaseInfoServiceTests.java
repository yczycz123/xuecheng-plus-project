package com.xuecheng.content;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class CourseBaseInfoServiceTests {


    @Autowired
    CourseBaseInfoService courseBaseInfoService;


    @Test
    void testCourseBaseInfoService() {
        //分页参数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(2L);//页码
        pageParams.setPageSize(3L);//每页记录数


        //查询条件
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto(); //自定义的课程查询模型类
        queryCourseParamsDto.setCourseName("java"); //课程名称
        queryCourseParamsDto.setAuditStatus("202004"); //审核状态
        queryCourseParamsDto.setPublishStatus("203001");  //发布状态
        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);

        System.out.println(courseBasePageResult);

    }
}
