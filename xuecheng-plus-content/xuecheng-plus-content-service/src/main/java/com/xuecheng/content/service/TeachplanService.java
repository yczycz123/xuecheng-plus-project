package com.xuecheng.content.service;


import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;


//课程计划管理相关接口
public interface TeachplanService {


    /**
     * 根据课程id查询课程计划
     *
     * @param courseId 课程id
     * @return 课程计划
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);


    /**
     * 新增/保存/修改课程计划
     *
     * @param teachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto teachplanDto);


    /**
     * 教学计划绑定媒资
     *
     * @param bindTeachplanMediaDto
     * @return com.xuecheng.content.model.po.TeachplanMedia
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

}
