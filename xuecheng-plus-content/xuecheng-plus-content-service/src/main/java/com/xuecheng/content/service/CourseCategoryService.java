package com.xuecheng.content.service;


import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import lombok.Data;

import java.util.List;


public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
