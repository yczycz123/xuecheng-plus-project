package com.xuecheng.content.service.impl;


import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service

public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) { //这里传入的id是根节点id

        //调用mapper查询出分类信息

        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos
                .stream()
                .filter(item -> !id.equals(item.getId()))//将根节点的数据过滤掉
                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
        //将结果封装成List<CourseCategoryTreeDto>返回


        //定义一个list作为最终返回的list
        List<CourseCategoryTreeDto> courseCategoryList = new ArrayList<>();

        //从头遍历courseCategoryTreeDtos，一边遍历一边把子节点放在父节点的childrenTreeNodes
        courseCategoryTreeDtos
                .stream()
                .filter(item -> !id.equals(item.getId()))
                .forEach(item -> {
                    //找到父节点
                    if (id.equals(item.getParentid())) {
                        courseCategoryList.add(item);
                    }
                    //找到每个节点的子节点，把他放在父节点的childrenTreeNodes
                    CourseCategoryTreeDto courseCategoryParent = mapTemp.get(item.getParentid());
                    if (courseCategoryParent != null) {
                        if (courseCategoryParent.getChildrenTreeNodes() == null) {
                            //如果该父节点的childrenTreeNodes为空，则new一个集合，因为要往集合中方子节点
                            courseCategoryParent.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                        }
                        //将子节点添加进来
                        courseCategoryParent.getChildrenTreeNodes().add(item);
                    }
                });

        return courseCategoryList;
    }
}
