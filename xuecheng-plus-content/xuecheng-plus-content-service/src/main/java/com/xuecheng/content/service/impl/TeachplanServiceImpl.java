package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service

public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //通过课程计划的id判断是新增还是修改
        //因为id是自增的，如果是修改，就可以得到原来的id，如果是新增，就没有id
        Long id = teachplanDto.getId();
        if (id != null) {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            //新增
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count + 1);
            BeanUtils.copyProperties(teachplanDto, teachplanNew);

            teachplanMapper.insert(teachplanNew);


        }
    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        // 获取教学计划的ID
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();

        // 查询该教学计划信息
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);

        // 如果教学计划不存在，抛出异常
        if (teachplan == null) {
            XueChengPlusException.cast("教学计划不存在");
        }

        // 获取教学计划的层级
        Integer grade = teachplan.getGrade();

        // 判断层级是否为2（只允许第二级教学计划绑定媒资文件）
        if (grade != 2) {
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }

        // 获取该教学计划关联的课程ID
        Long courseId = teachplan.getCourseId();

        // 删除原来该教学计划绑定的媒资文件
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));

        // 创建新的TeachplanMedia对象，并设置其属性
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);  // 设置课程ID
        teachplanMedia.setTeachplanId(teachplanId);  // 设置教学计划ID
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());  // 设置媒资文件名
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());  // 设置媒资ID
        teachplanMedia.setCreateDate(LocalDateTime.now());  // 设置创建日期为当前时间

        // 将新的媒资绑定关系插入数据库
        teachplanMediaMapper.insert(teachplanMedia);

        // 返回新创建的TeachplanMedia对象
        return teachplanMedia;
    }


    /**
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return int 最新排序号
     * @description 获取最新的排序号
     */
    private int getTeachplanCount(long courseId, long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
