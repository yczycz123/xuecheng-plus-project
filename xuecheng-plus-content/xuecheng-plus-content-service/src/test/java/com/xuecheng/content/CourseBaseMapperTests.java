package com.xuecheng.content;


import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.po.CourseBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseBaseMapperTests {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Test
    public void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(18L);
        Assertions.assertNotNull(courseBase);  //junit中的方法，断言一下对象是否为空，如果为空，那么测试就会抛异常
    }
}
