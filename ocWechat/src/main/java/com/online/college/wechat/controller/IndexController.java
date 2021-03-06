package com.online.college.wechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.online.college.common.page.TailPage;
import com.online.college.core.consts.CourseEnum;
import com.online.college.core.course.domain.Course;
import com.online.college.core.course.service.ICourseService;

/**
 * M站
 */
@Controller
@RequestMapping()
public class IndexController {

	@Autowired
	private ICourseService courseService;
	// 在项目里面引用mamcache
	
	//memcache客户端
	/*@Autowired
	MemcachedClient memcachedClient;*/
	
	/**
	 *  1 首页
	 */
	@RequestMapping("/index")
	public ModelAndView index(TailPage<Course> page){
		ModelAndView mv = new ModelAndView("index");
		
		/**只展示第一页的课程*/
		Course queryEntity = new Course();
		queryEntity.setOnsale(CourseEnum.ONSALE.value());
		
		page.descSortField("weight");
		/**queryEntity是搜索的入参数，page包含排序和limit start....*/
		page = this.courseService.queryPage(queryEntity, page);
		mv.addObject("page", page);
		//todo  分页在index.html include进来
		return mv;
	}
	
}
