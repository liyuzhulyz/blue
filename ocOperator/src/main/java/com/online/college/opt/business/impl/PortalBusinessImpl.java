package com.online.college.opt.business.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.online.college.core.consts.CourseEnum;
import com.online.college.core.consts.domain.ConstsClassify;
import com.online.college.core.consts.service.IConstsClassifyService;
import com.online.college.core.course.domain.Course;
import com.online.college.core.course.domain.CourseQueryDto;
import com.online.college.core.course.domain.CourseSection;
import com.online.college.core.course.service.ICourseSectionService;
import com.online.college.core.course.service.ICourseService;
import com.online.college.opt.business.IPortalBusiness;
import com.online.college.opt.vo.ConstsClassifyVO;
import com.online.college.opt.vo.CourseSectionVO;

/**
 * 首页业务层
 */
@Service
public class PortalBusinessImpl implements IPortalBusiness {

    @Autowired
    private IConstsClassifyService constsClassifyService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private ICourseSectionService courseSectionService;

    /**
     * 1 获取所有，包括一级分类&二级分类
     */
    public List<ConstsClassifyVO> queryAllClassify() {
        List<ConstsClassifyVO> resultList = new ArrayList<ConstsClassifyVO>();
        for (ConstsClassifyVO vo : this.queryAllClassifyMap().values()) {
            resultList.add(vo);
        }
        return resultList;
    }

    /**
     * 2 获取所有分类，二级分类填到一级分类
     */
    public Map<String, ConstsClassifyVO> queryAllClassifyMap() {
        Map<String, ConstsClassifyVO> resultMap = new LinkedHashMap<String, ConstsClassifyVO>();
        /**首先查询所有分类*/
        Iterator<ConstsClassify> it = constsClassifyService.queryAll().iterator();
        while (it.hasNext()) {
            ConstsClassify c = it.next();
            if ("0".equals(c.getParentCode())) {
                /**code为0说明就是一级分类*/
                ConstsClassifyVO vo = new ConstsClassifyVO();
                /**属性拷贝*/
                BeanUtils.copyProperties(c, vo);
                /**k,v的方法存储父级分类;
                 * be:后端；
                 * fe：前端；key就是be,fe......子分类的parentCode对应
                 * 父分类：*/
                resultMap.put(vo.getCode(), vo);
            } else {
                /**二级分类，子分类*/
                if (null != resultMap.get(c.getParentCode())) {
                    /**添加到子分类中;
                     * 把二级分类添加到一级分类的下面
                     * */
                    resultMap.get(c.getParentCode()).getSubClassifyList().add(c);
                }
            }
        }
        return resultMap;
    }

    /**
     * 3 为分类设置课程推荐
     */
    public void prepareRecomdCourses(List<ConstsClassifyVO> classifyVoList) {
        if (CollectionUtils.isNotEmpty(classifyVoList)) {
            for (ConstsClassifyVO item : classifyVoList) {
                CourseQueryDto queryEntity = new CourseQueryDto();
                queryEntity.setCount(5);
                queryEntity.descSortField("weight");
                queryEntity.setClassify(item.getCode());//分类code

                List<Course> tmpList = this.courseService.queryList(queryEntity);
                if (CollectionUtils.isNotEmpty(tmpList)) {
                    item.setRecomdCourseList(tmpList);
                }
            }
        }
    }

    /**
     * 获取课程章节
     */
    @Override
    public List<CourseSectionVO> queryCourseSection(Long courseId) {
        List<CourseSectionVO> resultList = new ArrayList<CourseSectionVO>();
        CourseSection queryEntity = new CourseSection();
        queryEntity.setCourseId(courseId);
        queryEntity.setOnsale(CourseEnum.ONSALE.value());//上架

        Map<Long, CourseSectionVO> tmpMap = new LinkedHashMap<Long, CourseSectionVO>();
        Iterator<CourseSection> it = courseSectionService.queryAll(queryEntity).iterator();
        while (it.hasNext()) {
            CourseSection item = it.next();
            if (Long.valueOf(0).equals(item.getParentId())) {//章
                CourseSectionVO vo = new CourseSectionVO();
                BeanUtils.copyProperties(item, vo);
                tmpMap.put(vo.getId(), vo);
            } else {
                tmpMap.get(item.getParentId()).getSections().add(item);//小节添加到大章中
            }
        }
        for (CourseSectionVO vo : tmpMap.values()) {
            resultList.add(vo);
        }
        return resultList;
    }

}
