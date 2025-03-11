package com.xuecheng.content.api;


import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//课程分类相关接口
@RestController
public class CourseCategoryController {

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        return null;
    }

}
