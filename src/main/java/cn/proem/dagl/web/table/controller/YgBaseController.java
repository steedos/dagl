package cn.proem.dagl.web.table.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.proem.core.annotation.LogService;
import cn.proem.core.entity.User;
import cn.proem.core.entity.UserDepartment;
import cn.proem.core.model.DataGrid;
import cn.proem.core.model.Module;
import cn.proem.dagl.core.model.Pager;
import cn.proem.dagl.web.archives.entity.DField;
import cn.proem.dagl.web.archives.service.CustomArchiveService;
import cn.proem.dagl.web.common.entity.CheckBox;
import cn.proem.dagl.web.common.entity.Input;
import cn.proem.dagl.web.common.entity.Option;
import cn.proem.dagl.web.common.entity.SearchInput;
import cn.proem.dagl.web.common.entity.Select;
import cn.proem.dagl.web.common.entity.SimpleInput;
import cn.proem.dagl.web.common.entity.SimpleSelect;
import cn.proem.dagl.web.common.entity.Textarea;
import cn.proem.dagl.web.common.entity.Number;
import cn.proem.dagl.web.common.entity.DateE;
import cn.proem.dagl.web.dicManager.entity.DictionaryValue;
import cn.proem.dagl.web.dicManager.service.DicManagerService;
import cn.proem.dagl.web.fileNum.entity.FileNumRule;
import cn.proem.dagl.web.fileNum.service.FileNumService;
import cn.proem.dagl.web.preArchive.service.ZlsjService;
import cn.proem.dagl.web.table.dto.Header;
import cn.proem.dagl.web.table.entity.inf.BaseEntityInf;
import cn.proem.dagl.web.table.entity.inf.Tag;
import cn.proem.dagl.web.table.service.RoleService;
import cn.proem.dagl.web.tools.util.QueryUtils;
import cn.proem.suw.web.auth.service.PrivilegeManagementService;
import cn.proem.suw.web.common.exception.ServiceException;
import cn.proem.suw.web.common.exception.UserNotExistException;
import cn.proem.suw.web.common.model.BaseCtrlModel;
import cn.proem.suw.web.common.model.ResultModel;
import cn.proem.suw.web.common.service.CommonService;
import cn.proem.suw.web.common.util.StringUtil;

/**
 * 资料收集控制层
 * 
 * @author bao
 *
 */
@Controller
@RequestMapping(value = "/w/example/ygtable")
public class YgBaseController extends BaseCtrlModel {

	  /**
     * 公共服务
     */
    @Autowired
    private CommonService commonService;

    /**
     * 档案服务
     */
	@Autowired
	private CustomArchiveService customArchiveService;
	
	/**
	 * 字典服务
	 */
	@Autowired
	private DicManagerService dicService;
	@Autowired
    private RoleService roleService;
    /**
	 * 权限服务
	 */
	@Autowired
    private PrivilegeManagementService privilegeManagementService;
	/**
	 * 档号生成服务
	 */
	@Autowired
	private FileNumService fileNumService;
	@Autowired
	private ZlsjService zlsjService;

	// ===========================================================================================================================================
	// 一栏页面
	// ===========================================================================================================================================

	/**
	 * @Description 进入表格展示页面
	 * @MethodName init
	 * @author tangcc
	 * @date 2017/4/24
	 * @param request
	 * @return ModelAndView
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/init/{tablename}")
	public ModelAndView init(HttpServletRequest request,
			@PathVariable("tablename") String tablename)
			throws ServiceException {
		
		ModelAndView modelAndView = this.createNormalView("/web/ygtable/list.vm");
		User user = this.getCurrentUser(request);
		List<Module> buttonList = this.buttons(user);
		for(Module button:buttonList){
			modelAndView.addObject(button.getCode(), true && roleService.hasDalx(user.getId(), tablename));
		}
		modelAndView.addObject("tablename", tablename);
		
		

		// 获得档案定义字段
        List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
        
        List<String> htmls = new ArrayList<String>();
        // 查询字段获得
        for(BaseEntityInf field: fields){
            String sfcxx = field.get("sfcxx");
            if("1".equals(sfcxx)){
                Tag search =  makeSearch(field);;
                // 查询项目，添加查询输入框
                htmls.add(search.html());
            }
        }
        
        modelAndView.addObject("conditions", htmls);
	
        return modelAndView;
	}

	/**
	 * @Description 表格赋值
	 * @MethodName getHeader
	 * @author tangcc
	 * @date 2017/4/24
	 * @param request
	 * @return ModelAndView
	 */
	@ResponseBody
	@RequestMapping(value = "/getHeader/{tablename}")
	@LogService(description = "获取表格头部信息")
	public ResultModel<Header> getHeader(HttpServletRequest request,
			@PathVariable("tablename") String tablename)
			throws ServiceException {
		ResultModel<Header> resultModel = new ResultModel<Header>();
		
	    // 获得档案定义字段
	    List<BaseEntityInf> fields = customArchiveService.getFields(tablename);

		List<Header> headers = this.getHeaders(fields);;

		resultModel.setDatas(headers);
		
		return resultModel;
	
	}

	
	
	/**
	 * @Description 表格赋值
	 * @MethodName details
	 * @author tangcc
	 * @date 2017/4/24
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/getList/{tablename}", method = RequestMethod.POST)
	@ResponseBody
	@LogService(description = "表格赋值")
	public String getList(String dtGridPager, HttpServletRequest request,@PathVariable("tablename") String tablename)throws ServiceException {
	    User user = this.getCurrentUser(request);
	    Pager dataGridQuery = parseToPager(dtGridPager == null ? "" : dtGridPager);
	    String orders = QueryUtils.getAdvanceQuerySortSql(dataGridQuery.getAdvanceQuerySorts());
		Map<String,Object> parameters =  dataGridQuery.getParameters();
		// 没有逻辑删除
	    parameters.put(DField.DELFLAG, "0");
	    // 状态为档案
	    //parameters.put(DField.ISARCHIVE, "3");
		 // 获得档案定义字段
        List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
		DataGrid<HashMap<String, Object>> dataGrid = new DataGrid<HashMap<String, Object>>(dataGridQuery.getNowPage(), dataGridQuery.getPageSize());
		// 存在高级查询条件
		if(dataGridQuery.getAdvanceQueryConditions().size() > 0){
		    String sql = QueryUtils.getAdvanceQueryConditionSql(dataGridQuery.getAdvanceQueryConditions());
		    sql = DField.DELFLAG + "='0' and " + DField.ISARCHIVE + "<'3' " // 档案状态
		        + authDepart(user) // 部门控制
		        + sql + " " //用户设置查询条件
		        + orders; //排序
		    System.out.println(sql);
		    dataGrid.setRecordCount(customArchiveService.getEntitiesByConditions(tablename, sql).size());
	        List<BaseEntityInf> records = customArchiveService.getEntitiesByPaging(tablename, sql, dataGrid.getStartRecord(), dataGrid.getPageSize());
	        // 字段值
	        List<HashMap<String, Object>> details = null;
	        if (records.size() != 0) {
	            details = this.getDetails(fields, records);
	        }
	        dataGrid.setExhibitDatas(details);
	        return JSON.toJSONStringWithDateFormat(dataGrid,
	                "yyyy-MM-dd HH:mm:ss",
	                SerializerFeature.WriteDateUseDateFormat,
	                SerializerFeature.DisableCircularReferenceDetect);
		}else{
		    
		    String otherSql = " AND " + DField.ISARCHIVE + "<'3' "+ authDepart(user);
	        if("".equals(orders.trim())){
	            otherSql = otherSql  + " ORDER BY UUID";
	        }else{
	            otherSql = otherSql + orders;
	        }
	        
	        // 查看是否字典值
            for(String param : parameters.keySet()){
                if(isDic(param, fields)){
                    otherSql = " AND " + param + "= '" + (String) parameters.get(param) + "'" + otherSql;
                }
            }
	        
	        // 获得未被删除的所有数据
	        dataGrid.setRecordCount(customArchiveService.getLikeEntitiesByPaging(tablename, parameters, otherSql,0, -1).size());
	        List<BaseEntityInf> records = customArchiveService.getLikeEntitiesByPaging(tablename, parameters, otherSql, dataGrid.getStartRecord(), dataGrid.getPageSize());
	        // 字段值
	        List<HashMap<String, Object>> details = null;
	        if (records.size() != 0) {
	            details = this.getDetails(fields, records);
	        }
	        dataGrid.setExhibitDatas(details);
	        return JSON.toJSONStringWithDateFormat(dataGrid,
	                "yyyy-MM-dd HH:mm:ss",
	                SerializerFeature.WriteDateUseDateFormat,
	                SerializerFeature.DisableCircularReferenceDetect);
		}
		
		
	}
	
	// 判断是否是字典项目
    private boolean isDic(String fieldname, List<BaseEntityInf> fields){
        for(BaseEntityInf field : fields){
            if(field.get("did") != null && fieldname.equals(field.get("zdywm"))){
                return true;
            }
        }
        return false;
    }
	
	
	
	// ===========================================================================================================================================
	// 表格一览页面增删改查
	// ===========================================================================================================================================

	/**
	 * @Description 表格一览页面-删除
	 * @MethodName del
	 * @author tangcc
	 * @date 2017/4/24
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/delete/{tablename}/{id}")
	@ResponseBody
	@LogService(description = "表格一览页面删除操作")
	public ResultModel<String> delete(HttpServletRequest request,
			@PathVariable("tablename") String tablename,
			@PathVariable("id") String id) {
		ResultModel<String> resultModel = new ResultModel<String>();
		// 获取表定义

		try {
			if(zlsjService.deleteYwgz(id)){
				BaseEntityInf entity = customArchiveService.getEntity(tablename, id);
				entity.set("delflag", "1");
				entity.set(DField.UPDATETIME, this.nowTime());
				customArchiveService.update(entity);
				resultModel.setMsg("删除成功");
			}
		
		} catch (ServiceException e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("删除失败");
			e.printStackTrace();
		}

		return resultModel;
	}

	/**
	 * @Description 点击详情页面弹出模态框
	 * @MethodName detail
	 * @author
	 * @date 2017年4月27日
	 * @param request
	 * @param tablename
	 * @param id
	 * @return
	 * @throws ServiceException
	 *             ModelAndView
	 */
	@RequestMapping(value = "/detail/{tablename}/{id}")
	public ModelAndView detail(HttpServletRequest request,
			@PathVariable("tablename") String tablename,
			@PathVariable("id") String id) throws ServiceException {

		ModelAndView modelAndView = this
				.createNormalView("/web/ygtable/detail.vm");
		// 获得表的定义
		String tabledefined = "pdagl_tablename";
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("bm", tablename);
		BaseEntityInf table = customArchiveService.getEntityByConditions(
				tabledefined, conditions);
		// 获得表的所有字段
		conditions = new HashMap<String, Object>();
		conditions.put("tableNameId", table.get("id"));
		List<BaseEntityInf> fields = customArchiveService
				.getEntitiesByConditions("pdagl_tablefield", conditions);
		// 获得所有数据
		BaseEntityInf record = customArchiveService.getEntity(tablename, id);
		List<String> htmls = new ArrayList<String>();
		// 获得所有字段的html
		for (BaseEntityInf field : fields) {
			//排除集团号
			if("fieldid".equalsIgnoreCase((String)field.get("zdywm"))){
				continue;
			}
			
			Tag element = null;
			if ("0".equals(field.get("zdlx"))) {
				// 字符类型
				element = new Input();
//				element.setVal((String) record.get((String) field.get("zdywm")));
				String val = (String) record.get((String) field.get("zdywm"));
				if(StringUtil.isEmpty(val)) {
					element.setVal("");
				}else {
					element.setVal(val);
				}
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("1".equals(field.get("zdlx"))) {
				// 日期
				element = new DateE();
//				element.setVal((String) record.get((String) field.get("zdywm")));
				String val = (String) record.get((String) field.get("zdywm"));
				if(StringUtil.isEmpty(val)) {
					element.setVal("");
				}else {
					element.setVal(val);
				}
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("2".equals(field.get("zdlx"))) {
				// 下拉框
				element = new Select();
				// 取得字典项目
				List<DictionaryValue> dictionaryValues = dicService
						.getDicValueList((String) field.get("did"));
				// 循环字典项目
				StringBuilder options = new StringBuilder();
				for (DictionaryValue dictionaryValue : dictionaryValues) {
					options.append(String.format(
							"<option value='%s' %s>%s</option>",
							dictionaryValue.getDvno(),
							(dictionaryValue.getDvno()).equals(record.get((String) field.get("zdywm")))?"selected": "", 
							dictionaryValue.getDvalue()));
					
				}
				// 结果setVal
				element.setVal(options.toString());
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("3".equals(field.get("zdlx"))) {
				// 文本框
				element = new Textarea();
//				element.setVal((String) record.get((String) field.get("zdywm")));
				String val = (String) record.get((String) field.get("zdywm"));
				if(StringUtil.isEmpty(val)) {
					element.setVal("");
				}else {
					element.setVal(val);
				}
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("4".equals(field.get("zdlx"))) {
				StringBuilder checkBoxs = new StringBuilder();
				// 复选框
				List<DictionaryValue> dictionaryValues = dicService
						.getDicValueList((String) field.get("did"));
				for (DictionaryValue dictionaryValue : dictionaryValues) {
				    checkBoxs
                    .append(String
                            .format("<input type='checkbox' name='%s' value='%s' %s title='%s'>",
                                    (String) field.get("zdywm"),
                                    dictionaryValue.getDvno(),
                                    dictionaryValue.getDvno().equals((String) record
                                            .get((String) field.get("zdywm"))) ? "checked"
                                            : "", dictionaryValue
                                            .getDvalue()));
				}
				element = new CheckBox();
				element.setVal(checkBoxs.toString());
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("5".equals(field.get("zdlx"))) {
				// 数字型
				element = new Number();
//				element.setVal((String) record.get((String) field.get("zdywm")));
				String val = (String) record.get((String) field.get("zdywm"));
				if(StringUtil.isEmpty(val)) {
					element.setVal("");
				}else {
					element.setVal(val);
				}
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("6".equals(field.get("zdlx"))) {
				// 可输入下拉框
				element = new Select();
//				element.setVal((String) record.get((String) field.get("zdywm")));
				String val = (String) record.get((String) field.get("zdywm"));
				if(StringUtil.isEmpty(val)) {
					element.setVal("");
				}else {
					element.setVal(val);
				}
				//判断是否必填项
	            if("1".equals((String) field.get("sfbtx"))) {
	                element.setRequired(true);
	            }
			}
			
			element.setTitle((String) field.get("zdzwm"));
			element.setName((String) field.get("zdywm"));
			element.set("isEditable", "readOnly");
			htmls.add(element.html());
		}
		User user = this.getCurrentUser(request);
		if(privilegeManagementService.hasPrivilege(user.getUsername(), "auth_save")){
		    modelAndView.addObject("auth_save", true);
		}
		if(privilegeManagementService.hasPrivilege(user.getUsername(), "auth_file")){
		    modelAndView.addObject("auth_file", true);
		}
		// 注入详情数据
		modelAndView.addObject("tablename", tablename);
		//uuid
		modelAndView.addObject("detailid", id);
		//资料id
		modelAndView.addObject("htmls", htmls);
		return modelAndView;
	}

	/**
	 * @Description 跳转增加页面
	 * @MethodName adddetail
	 * @author bao
	 * @date 2017年5月16日
	 * @param request
	 * @param tablename
	 * @return
	 * @throws ServiceException
	 *             ModelAndView
	 */
	@RequestMapping(value = "/adddetail/{tablename}")
	public ModelAndView adddetail(HttpServletRequest request,
			@PathVariable("tablename") String tablename)
			throws ServiceException {

		ModelAndView modelAndView = this
				.createNormalView("/web/ygtable/detail.vm");
		// 获得表的定义
		String tabledefined = "pdagl_tablename";
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("bm", tablename);
		BaseEntityInf table = customArchiveService.getEntityByConditions(
				tabledefined, conditions);
		// 获得表的所有字段
		conditions = new HashMap<String, Object>();
		conditions.put("tableNameId", table.get("id"));
		List<BaseEntityInf> fields = customArchiveService
				.getEntitiesByConditions("pdagl_tablefield", conditions);
		// 获得所有数据
		List<String> htmls = new ArrayList<String>();
		// 获得所有字段的html
		for (BaseEntityInf field : fields) {
			Tag element = null;
			if ("0".equals(field.get("zdlx"))) {
				// 字符类型
				element = new Input();
				element.setVal("");
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("1".equals(field.get("zdlx"))) {
				// 日期
				element = new DateE();
				element.setVal("");
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("2".equals(field.get("zdlx"))) {
				// 下拉框
				element = new Select();
				// 取得字典项目
				List<DictionaryValue> dictionaryValues = dicService
						.getDicValueList((String) field.get("did"));
				// 循环字典项目
				StringBuilder options = new StringBuilder();
				for (DictionaryValue dictionaryValue : dictionaryValues) {
					options.append(String.format(
							"<option value='%s'>%s</option>",
							dictionaryValue.getDvno(),
							dictionaryValue.getDvalue()));
				}
				// 结果setVal
				element.setVal(options.toString());
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("3".equals(field.get("zdlx"))) {
				// 文本框
				element = new Textarea();
				element.setVal("");
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("4".equals(field.get("zdlx"))) {
				StringBuilder checkBoxs = new StringBuilder();
				// 复选框
				List<DictionaryValue> dictionaryValues = dicService
						.getDicValueList((String) field.get("did"));
				for (DictionaryValue dictionaryValue : dictionaryValues) {
				    checkBoxs.append(String.format(
                            "<input type='checkbox' name='%s' value='%s'  title='%s'>",
                            (String) field.get("zdywm"),
                            dictionaryValue.getDvno(),
                            dictionaryValue.getDvalue()));
				}
				element = new CheckBox();
				element.setVal(checkBoxs.toString());
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("5".equals(field.get("zdlx"))) {
				// 数字型
				element = new Number();
				element.setVal("");
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			} else if ("6".equals(field.get("zdlx"))) {
				// 可输入下拉框
				element = new Select();
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
			}
			element.setTitle((String) field.get("zdzwm"));
			element.setName((String) field.get("zdywm"));
			element.set("isEditable", "readOnly");
			htmls.add(element.html());
		}
		User user = this.getCurrentUser(request);
		if(privilegeManagementService.hasPrivilege(user.getUsername(), "addGrid")){
		    modelAndView.addObject("auth_save", true);
		}
		if(privilegeManagementService.hasPrivilege(user.getUsername(), "auth_file")){
		    modelAndView.addObject("auth_file", true);
		}
		// 注入详情数据
		modelAndView.addObject("tablename", tablename);
		modelAndView.addObject("htmls", htmls);
		return modelAndView;

	}


	/**
	 * @Description 增加数据
	 * @MethodName update
	 * @author bao
	 * @date 2017年4月28日
	 * @param request
	 * @param param
	 * @return
	 * @throws ServiceException
	 *             ResultModel<String>
	 */
	@RequestMapping(value = "/saveOrUpdate/{tablename}/{fileUUID}", method = RequestMethod.POST)
	@ResponseBody
	@LogService(description = "保存或修改档案数据")
	public ResultModel<String> saveOrUpdate(HttpServletRequest request,
			@PathVariable("tablename") String tablename,
			@PathVariable("fileUUID") String fileUUID) throws ServiceException {
	    User user = this.getCurrentUser(request);
	    String time = nowTime();
		ResultModel<String> resultModel = new ResultModel<String>();
		// 获得档案定义字段
        List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
        // 记录对象
		BaseEntityInf entity = null;
		if(StringUtil.isEmpty(request.getParameter("detailId"))){
		    // 新建表记录
		    entity = customArchiveService.getEntity(tablename);
		    
		    if(StringUtil.isEmpty(fileUUID)||"undefined".equals(fileUUID)){
	            // 无原文生成UUID
	            entity.set(DField.UUID, UUID.randomUUID().toString());
	            //设置集团号
	            entity.set(DField.NOWCAMP, getDepartment(user));
	            //设置新建时间
	            entity.set(DField.CREATETIME, time);
	            //设置创建人员
	            entity.set(DField.CREATEUSER, user.getId());
	            //设置更新时间
	            entity.set(DField.UPDATETIME, time);
	            //设置更新人员
	            entity.set(DField.UPDATEUSER, user.getId());
	        }else{

	            //档案添加原文，使用原文UUID
	            entity.set(DField.UUID, fileUUID);
	            //设置集团号
                entity.set(DField.NOWCAMP, getDepartment(user));
	            //设置更新时间
                entity.set(DField.CREATETIME, time);
                //设置更新人员
                entity.set(DField.CREATEUSER, user.getId());
                //设置更新时间
	            entity.set(DField.UPDATETIME, time);
	            //设置更新人员
	            entity.set(DField.UPDATEUSER, user.getId());
	        }
        }else{
            // 更新表记录
            // 获得表记录对象
            entity = customArchiveService.getEntity(tablename,request.getParameter("detailId"));
            //设置更新时间
            entity.set(DField.UPDATETIME, time);
            //设置更新人员
            entity.set(DField.UPDATEUSER, user.getId());
            
        }
		// 设置页面获得值
		this.setEntity(request, fields, entity);
		//设置默认删除标识
        entity.set(DField.DELFLAG, "0");
        //设置默认的归档状态
        entity.set(DField.ISARCHIVE,"0");
        //设置档案鉴定字段
        entity.set(DField.SFJD,"sfjd_2");
		try {
			customArchiveService.saveOrUpdate(entity);
		} catch (ServiceException e) {
			resultModel.setSuccess(false);
			resultModel.setMsg(e.getMessage());
		}catch (Exception e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("操作失败");
		}
		return resultModel;
	}

	/**
	 * @Description 导入的上传文件模态框界面
	 * @MethodName importView
	 * @author bao
	 * @date 2017年5月2日
	 * @param request
	 * @param tablename
	 * @return
	 * @throws ServiceException
	 *             ModelAndView
	 */
	@RequestMapping(value = "/importView/{tablename}")
	public ModelAndView importView(HttpServletRequest request,
			@PathVariable("tablename") String tablename)
			throws ServiceException {
		ModelAndView modelAndView = this
				.createNormalView("/web/ygtable/import.vm");
		modelAndView.addObject("tablename", tablename);
		return modelAndView;
	}
	
	/**
	 * @Description 批量删除
	 * @MethodName batchdelete
	 * @author bao
	 * @date 2017年5月18日
	 * @param request
	 * @param tablename
	 * @param fileids
	 * @return ResultModel<String>
	 */
	@RequestMapping(value = "/batchdelete")
	@ResponseBody
	@LogService(description = "预归档批量删除操作")
	public ResultModel<String> batchdelete(HttpServletRequest request,
			@RequestParam("tablename") String tablename,
			@RequestParam(value = "fileids[]")  String[]  fileids) {
		ResultModel<String> resultModel = new ResultModel<String>();
		// 获取表定义

		try {
			for(String fileid : fileids){
				if(zlsjService.deleteYwgz(fileid)){
					BaseEntityInf entity = customArchiveService.getEntity(tablename, fileid);
					entity.set("delflag", "1");
					entity.set(DField.UPDATETIME, this.nowTime());
					customArchiveService.update(entity);
				}
				
			}
				
			resultModel.setMsg("删除成功");
		} catch (ServiceException e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("删除失败");
			e.printStackTrace();
		}

		return resultModel;
	}
	
	/**
	 * 根据页面数据设置记录数据
	 * @param request
	 * @param fields
	 * @param entity
	 */
	private void setEntity(HttpServletRequest request, List<BaseEntityInf> fields, BaseEntityInf entity){
	    for (BaseEntityInf field : fields) {
            String fidleStr = (String) field.get("zdywm");
            if("4".equals(field.get("zdlx"))){
                // 多选项将选择项目合并记入
                String[] vals = request.getParameterValues(fidleStr);
                if(vals != null){
                    String ret = vals[0];
                    for(int i=1 ; i<vals.length ; i++){
                        ret += "," + vals[i]; 
                    }
                    entity.set(fidleStr, ret);
                }else{
                    entity.set(fidleStr, null);
                }
            }else{
                // 非多选项直接记入
                entity.set(fidleStr, request.getParameter(fidleStr));
            }
            
        }
	}
	
	
	
	/**
	 * 获得记录
	 * @param fields 
	 * @param records
	 * @return
	 */
	private List<HashMap<String, Object>> getDetails(List<BaseEntityInf> fields, List<BaseEntityInf> records ){
	    List<HashMap<String, Object>> details = new ArrayList<HashMap<String, Object>>();
	    for (int i = 0; i < records.size(); i++) {
            BaseEntityInf record = records.get(i);
            HashMap<String, Object> detail = new HashMap<String, Object>();
            for (BaseEntityInf field : fields) {
                // 列标题编辑
                String id = field.get("zdywm");
                // 判断列是否是字典值
                String did = field.get("did");
                if(did == null || "".equals(did)){
                    // 列值编辑
                    if(record.get(id)!=null){
                        detail.put(id, record.get(id));
                    }else{
                        detail.put(id, "");
                    }
                }else{
                    if("4".equals(field.get("zdlx"))){
                        String vals = record.get(id);
                        if(vals!=null){
                            String rets[] = vals.split(",");
                            vals = "";
                            vals = this.getDicTitle(did, rets[0]);
                            for(int j=1;j<rets.length;j++){
                                vals += "," + this.getDicTitle(did, rets[j]);
                            }
                            detail.put(id, vals);
                        }else{
                            detail.put(id,"");
                        }
                    }else{
                        detail.put(id, this.getDicTitle(did, (String) record.get(id)));
                    }
                }
                
            }
            // 获得uuid
            String uuid = record.get("uuid");
            detail.put("uuid", uuid);
            detail.put("id", record.get("id"));
            // 获得原文
            String dataid = record.get("dataid");
            detail.put("dataid", dataid);
            // 获得状态
            String  isarchive = record.get("isarchive");
            detail.put("isarchive", isarchive);
            details.add(detail);
        }
	    return details;
	}
	
	
	/**
	 * 获得字典标题值
	 * @param did
	 * @param val
	 * @return
	 */
	private String getDicTitle(String did, String val){
	    List<DictionaryValue> dictionaryValues = dicService
                .getDicValueList(did);
	    for(DictionaryValue dic : dictionaryValues){
	        // 获得字典内容
	        if(dic.getDvno().equals(val)){
	            return dic.getDvalue();
	        }
	    }
	    return null;
	}
	
	
	/**
	 * 获得记录标题
	 * @param fields
	 * @return
	 */
	private List<Header> getHeaders(List<BaseEntityInf> fields){
	    List<Header> headers = new ArrayList<Header>();
	    //uuid
        Header header = new Header();
        header.setHide(true);
        header.setId("uuid");
        header.setTitle("uuid");
        // 隐藏uuid
        header.setAdvanceQuery(false);
        headers.add(header);
        
	    // 列标题编辑
        for (BaseEntityInf field : fields) {
            header = new Header();
            header.setTitle((String) field.get("zdzwm"));
            header.setId((String) field.get("zdywm"));
            header.setType("string");
            header.setColumnClass("text-center");
            // 是否信息显示列判断
            if("0".equals((String)field.get("sfgyxxx"))){
                header.setHide(true);
            }else{
                header.setHide(false);
            }
            headers.add(header);
        }
      
        //原文
        /*header = new Header();
        header.setHide(false);
        header.setId("uuid");
        header.setTitle("挂接原文");
        header.setType("string");
        header.setColumnClass("text-center");
        headers.add(header);*/
        
        //状态值
        header = new Header();
        header.setHide(false);
        header.setId("isarchive");
        header.setTitle("状态");
        header.setType("string");
        header.setColumnClass("text-center");
        headers.add(header);
        return headers;
	}
	private Tag makeSearch(BaseEntityInf field){
	    if ("0".equals(field.get("zdlx"))) {
	        // 字符类型
	        Tag search = new SearchInput();
            Tag input = new SimpleInput();
            input.setName((String)field.get("zdywm"));
            input.set("class", "form-control input-sm");
            search.set("Input", input);
            search.setTitle((String)field.get("zdzwm"));
            return search;
        } else if ("1".equals(field.get("zdlx"))) {
            // 日期
            Tag search = new SearchInput();
            Tag input = new SimpleInput();
            input.setName((String)field.get("zdywm"));
            input.set("class", "form-control input-sm");
            search.set("Input", input);
            search.setTitle((String)field.get("zdzwm"));
            return search;
        } else if ("2".equals(field.get("zdlx"))) {
            // 下拉框
            Tag search = new SearchInput();
            Tag input = new SimpleSelect();
            input.setName((String)field.get("zdywm"));
            input.set("class", "form-control input-sm");
            List<Option> opts = new ArrayList<Option>();
            // 取得字典项目
            List<DictionaryValue> dics = dicService
                    .getDicValueList((String) field.get("did"));
            // 循环字典项目
            opts.add(new Option(" ", " "));
            for (DictionaryValue dictionaryValue : dics) {
                opts.add(new Option(dictionaryValue.getDvalue(), dictionaryValue.getDvno()));
            }
            input.set("options", opts);
            search.set("Input", input);
            search.setTitle((String)field.get("zdzwm"));
            return search;
        } else if ("3".equals(field.get("zdlx"))) {
         // 文本框
            Tag search = new SearchInput();
            Tag input = new SimpleInput();
            input.setName((String)field.get("zdywm"));
            input.set("class", "form-control input-sm");
            search.set("Input", input);
            search.setTitle((String)field.get("zdzwm"));
            return search;
        } else if ("4".equals(field.get("zdlx"))) {
         // 复选框
            Tag search = new SearchInput();
            Tag input = new SimpleInput();
            input.setName((String)field.get("zdywm"));
            input.set("class", "form-control input-sm");
            search.set("Input", input);
            search.setTitle((String)field.get("zdzwm"));
            return search;
        } else if ("5".equals(field.get("zdlx"))) {
         // 数字型
            Tag search = new SearchInput();
            Tag input = new SimpleInput();
            input.setName((String)field.get("zdywm"));
            input.set("class", "form-control input-sm");
            search.set("Input", input);
            search.setTitle((String)field.get("zdzwm"));
            return search;
        } else if ("6".equals(field.get("zdlx"))) {
         // 可输入下拉框
            Tag search = new SearchInput();
            Tag input = new SimpleInput();
            input.setName((String)field.get("zdywm"));
            input.set("class", "form-control input-sm");
            search.set("Input", input);
            search.setTitle((String)field.get("zdzwm"));
            return search;
        }
	    return null;
	}
	
	
	/**
     * 将字符串转换为查询对象
     * 
     * @param dtGridPager
     *            查询字符串
     * @return DataGridQuery
     */
    private Pager parseToPager(String dtGridPager)
    {
        return JSON.toJavaObject(JSON.parseObject(dtGridPager), Pager.class);
    }
    
    /**
     * 返回部门权限查询条件
     * @Method: authDepart 
     * @Description: 返回权限查询条件
     * @param user 登录用户
     * @return 权限where条件
     * @throws UserNotExistException 
     * @throws ServiceException 
     */
    private String authDepart(User user) throws ServiceException{
        // 用户部门
        StringBuilder authdepart = new StringBuilder();
        UserDepartment userdepartment = commonService.findUserDepartmentByUserId(user.getId());
        if(userdepartment != null){
            // 部门
            String department = userdepartment.getDepartment() != null ? userdepartment.getDepartment().getId() : "";
            if(!department.isEmpty()){
                authdepart.append(" and ");
                authdepart.append(" (");
                authdepart.append(DField.NOWCAMP);
                authdepart.append("='");
                authdepart.append(department);
                authdepart.append("' ");
                authdepart.append(" or ");
                authdepart.append(DField.OLDCAMP);
                authdepart.append("='");
                authdepart.append(department);
                authdepart.append("' ");
                authdepart.append(") ");
            }
        }
        return authdepart.toString();
    }
    
    private String getDepartment(User user) throws ServiceException{
        // 用户部门
        UserDepartment userdepartment = commonService.findUserDepartmentByUserId(user.getId());
        if(userdepartment != null){
            // 部门
            String department = userdepartment.getDepartment() != null ? userdepartment.getDepartment().getId() : "";
            return department;
        }
        return "";
    }
    
    /**
     * 
     * 
     * @Method: nowTime 
     * @Description: 获得当前时间
     * @return
     */
    private String nowTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }
    
    /**
	 * 获取按钮权限
	 * @param user
	 * @return
	 */
	private List<Module> buttons(User user){
		List<Module> buttonList = privilegeManagementService.getPrivilegeButtons(user);
		return buttonList;
		
	}
	
	/**
	 * @Description 跳转批量修改页面
	 * @MethodName batchModifyView
	 * @author bao
	 * @date 2017年6月13日
	 * @param request
	 * @param tablename
	 * @return
	 * @throws ServiceException ModelAndView
	 */
	@RequestMapping(value = "/batchModifyView/{tablename}")
	public ModelAndView batchModifyView(HttpServletRequest request,@PathVariable("tablename") String tablename)
			throws ServiceException {


		ModelAndView modelAndView = this
				.createNormalView("/web/ygtable/batchModify.vm");
		// 获得表的定义
		String tabledefined = "pdagl_tablename";
		Map<String, Object> conditions = new HashMap<String, Object>();
		conditions.put("bm", tablename);
		BaseEntityInf table = customArchiveService.getEntityByConditions(
				tabledefined, conditions);
		// 获得表的所有字段
		conditions = new HashMap<String, Object>();
		conditions.put("tableNameId", table.get("id"));
		List<BaseEntityInf> fields = customArchiveService
				.getEntitiesByConditions("pdagl_tablefield", conditions);
		// 获得所有数据
		List<String> htmls = new ArrayList<String>();
		// 获得所有字段的html
		for (BaseEntityInf field : fields) {
			Tag element = null;
			if ("0".equals(field.get("zdlx"))) {
				// 字符类型
				element = new Input();
				element.setVal("");
			} else if ("1".equals(field.get("zdlx"))) {
				// 日期
				element = new DateE();
				element.setVal("");
			} else if ("2".equals(field.get("zdlx"))) {
				// 下拉框
				element = new Select();
				// 取得字典项目
				List<DictionaryValue> dictionaryValues = dicService
						.getDicValueList((String) field.get("did"));
				// 循环字典项目
				StringBuilder options = new StringBuilder();
				for (DictionaryValue dictionaryValue : dictionaryValues) {
					options.append(String.format(
							"<option value='%s'>%s</option>",
							dictionaryValue.getDvno(),
							dictionaryValue.getDvalue()));
				}
				// 结果setVal
				element.setVal(options.toString());
			} else if ("3".equals(field.get("zdlx"))) {
				// 文本框
				element = new Textarea();
				element.setVal("");
			} else if ("4".equals(field.get("zdlx"))) {
				StringBuilder checkBoxs = new StringBuilder();
				// 复选框
				List<DictionaryValue> dictionaryValues = dicService
						.getDicValueList((String) field.get("did"));
				for (DictionaryValue dictionaryValue : dictionaryValues) {
				    checkBoxs.append(String.format(
                            "<input type='checkbox' name='%s' value='%s'  title='%s'>",
                            (String) field.get("zdywm"),
                            dictionaryValue.getDvno(),
                            dictionaryValue.getDvalue()));
				}
				element = new CheckBox();
				element.setVal(checkBoxs.toString());
			} else if ("5".equals(field.get("zdlx"))) {
				// 数字型
				element = new Number();
				element.setVal("");
			} else if ("6".equals(field.get("zdlx"))) {
				// 可输入下拉框
				element = new Select();
			}
			element.setRequired(false);
			element.setTitle((String) field.get("zdzwm"));
			element.setName((String) field.get("zdywm"));
			element.set("isEditable", "readOnly");
			htmls.add(element.html());
		}
		User user = this.getCurrentUser(request);
		if(privilegeManagementService.hasPrivilege(user.getUsername(), "addGrid")){
		    modelAndView.addObject("auth_save", true);
		}
		// 注入详情数据
		modelAndView.addObject("tablename", tablename);
		modelAndView.addObject("htmls", htmls);
		
		String dhDicId = getDhDicIdByTablename(tablename);
		modelAndView.addObject("dhDicId", dhDicId);
		return modelAndView;

	}
	
	/**
	 * 
	 * @Description:批量修改操作
	 * @author:bao
	 * @time:2017年7月14日 下午3:41:02
	 */
	@RequestMapping(value = "/batchModify/{tablename}", method = RequestMethod.POST)
	@ResponseBody
	@LogService(description = "批量修改预归档档案")
	public ResultModel<String> batchModify(HttpServletRequest request,
			@PathVariable("tablename") String tablename,
			@RequestParam(value = "autofields[]",required = false) String[] autofields,
			@RequestParam(value = "uuids[]",required = false) String[] uuids){
		ResultModel<String> resultModel = new ResultModel<String>();
		User user = this.getCurrentUser(request);
		try{	
				//判断档号字段不能只修改
				if(autofields == null || !Arrays.asList(autofields).contains("dh")){
					if(StringUtil.isNotEmpty(request.getParameter("dh"))){
						throw new  ServiceException("档号字段不能只勾选修改框");
						
					}
				}
				
				/*String dh = request.getParameter("dh");
				if(StringUtil.isNotEmpty(dh)){
					List<BaseEntityInf> entities =  customArchiveService.getEntitiesByConditions(tablename, " DELFLAG = '0' AND DH = '"+dh+"' AND ISARCHIVE < '3'");
					if(entities!=null && entities.size()>0){
						throw new  ServiceException("当前输入的档号值有对应数据");
					}
				}*/
			
			
				Map<String,Integer> autoMap = new HashMap<String, Integer>();
				if(autofields!=null){
					for(String autoField : autofields){
						String startIndex = request.getParameter(autoField);
						if("dh".equalsIgnoreCase(autoField)){
							startIndex =  startIndex.substring(startIndex.lastIndexOf("-")+1);
							
						}
						if(startIndex!=null && !"".equals(startIndex.trim()) && startIndex.matches("[0-9]+")){
							autoMap.put(autoField, Integer.parseInt(startIndex));
						}else{
							throw new  ServiceException("选中自增选项要输入正确的格式");
						}
						
					}
				}
				
				
				//String dhFormat = getDhByTablename(tablename);
				
				//表字段定义
				List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
				for(String uuid : uuids){
					BaseEntityInf entity = customArchiveService.getEntity(tablename,uuid);
					
					
					for (BaseEntityInf field : fields) {
						//字段英文名
						 String fidleStr = (String) field.get("zdywm");
						//判断当前属性是否包含在自增数组中
						if(autoMap.keySet().contains(fidleStr)){
							if("dh".equalsIgnoreCase(fidleStr)){
								String dhValue = request.getParameter("dh");
								
								String dhValueFront = dhValue.substring(0,dhValue.lastIndexOf("-")+1);
								List<BaseEntityInf> entities =  customArchiveService.getEntitiesByConditions(tablename, " DELFLAG = '0' AND DH = '"+dhValueFront+autoMap.get(fidleStr)+"' AND ISARCHIVE < '3'");
								if(entities!=null && entities.size()>0){
									throw new  ServiceException("当前输入的档号值有对应数据");
								}
								entity.set(fidleStr,dhValueFront+String.format("%0"+dhValue.substring(dhValue.lastIndexOf("-")+1).length()+"d", autoMap.get(fidleStr)));
								
								autoMap.put(fidleStr, autoMap.get(fidleStr)+1);
							}else{
								
								entity.set(fidleStr,String.format("%0"+request.getParameter(fidleStr).length()+"d", autoMap.get(fidleStr)));
								autoMap.put(fidleStr, autoMap.get(fidleStr)+1);
							}
							continue;
						}
						
						
						    
			            if("4".equals(field.get("zdlx"))){
			                // 多选项将选择项目合并记入
			                String[] vals = request.getParameterValues(fidleStr);
			                if(vals != null){
			                    String ret = vals[0];
			                    for(int i=1 ; i<vals.length ; i++){
			                        ret += "," + vals[i]; 
			                    }
			                    entity.set(fidleStr, ret);
			                }
			            }else{
			                // 非多选项直接记入
			            	String fidleValue =  request.getParameter(fidleStr);
			            	if(StringUtil.isNotEmpty(fidleValue)){
			            		entity.set(fidleStr, fidleValue);
			            	}
			            }
			           
			            
					}
					 //设置更新人员
		            entity.set(DField.UPDATETIME, nowTime());
		            //设置更新时间
		            entity.set(DField.UPDATEUSER, user.getId());
		            
		            
		           /* String dh = new String(dhFormat);
		            for(BaseEntityInf field : fields){
		            	
		            	String zdywm = field.get("zdywm");
		            	dh = dh.replaceAll("%"+zdywm+"%", String.valueOf(entity.get(zdywm)));
		            	
		            }
		            entity.set("dh", dh);*/
		            
		            //档号设置修改和自增未选择
		            Boolean flag = false;
		            if(autofields==null || !Arrays.asList(autofields).contains("dh")){
		            	List<FileNumRule> fileNumRules = this.getFileNumRuleByTablename(tablename);
		            	for(FileNumRule fileNumRule : fileNumRules){
		            		String title = fileNumRule.getTitle();
		            		if("text".equals(title) || "year".equals(title) || "month".equals(title) || "date".equals(title) ||"number".equals(title)){
		            			continue;
		            		}else{
		            			if(StringUtil.isEmpty(request.getParameter(title))){
		            				throw new  ServiceException(title+"是档号配置字段,不能为空");
		            			}
		            			if(autofields!=null && Arrays.asList(autofields).contains(title)){
		            				flag = true;
		            			}
		            			
		            		}
		            	}
		            	if(!flag){
		            		throw new  ServiceException("档号配置字段没有勾选自增致使档号出现重复！");
		            	}
		            	
		            	
		            	
	            		String dh = new String(getDhByTablename(tablename));
	            		for (BaseEntityInf field : fields) {
	            			String zdywm = field.get("zdywm");
	            			dh = dh.replaceAll("%"+zdywm+"%", String.valueOf(entity.get(zdywm)));
	            		}
	            		entity.set("dh", dh);
		            }
		            
		            
					customArchiveService.update(entity);
				}
				
		} catch (ServiceException e) {
			resultModel.setSuccess(false);
			resultModel.setMsg(e.getMessage());
		}catch (Exception e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("操作失败");
		}
		return resultModel;
	}
	
	@RequestMapping(value = "/checkRequired", method = RequestMethod.POST)
	@ResponseBody
	public ResultModel<String> checkRequired(HttpServletRequest request,@RequestBody Map<String, String> obj){
		ResultModel<String> resultModel = new ResultModel<String>();
		List<String> infos = new ArrayList<String>();
		String format = "错误信息为第%d条,请检查字段:%s是否填写";
		String tablename = obj.get("tablename");
		String[] uuids = obj.get("businessStr").split(",");
		for(int i = 0;i<uuids.length;i++){
			Boolean flag = false;
			StringBuilder sb = new StringBuilder("");
			String uuid = uuids[i];
			
			BaseEntityInf entity =  customArchiveService.getEntity(tablename, uuid);
			List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
			List<String> errorfilelds = new ArrayList<String>();
			for(BaseEntityInf field : fields){
				if("1".equals((String) field.get("sfbtx"))){
					if(StringUtil.isEmpty(entity.get((String) field.get("zdywm")))){
						flag = true;
						errorfilelds.add((String) field.get("zdzwm"));
						
					}
				}
			}
			if(flag){
				sb.append(StringUtils.join(errorfilelds.toArray(), ","));
				
				infos.add(String.format(format, i+1,new String(sb)));
			}
		}
		resultModel.setDatas(infos);
		return resultModel;
	}
	//档号规则
	private String getDhByTablename(String tablename) throws ServiceException{
		String dh = "";
		DictionaryValue dicDh = null;
		List<DictionaryValue> dictionaryValues = dicService.getDicValueList("DHSC");
		for(DictionaryValue dic : dictionaryValues){
		    // 获得字典内容
		    if(dic.getDvno().equalsIgnoreCase(tablename)){
		    	dicDh = dic;
		       break;
		    }
		}
		if(dicDh!=null){
			dh = fileNumService.getDahByType(dicDh.getId());
			
		}
		return dh;
	}
	
	//获得档号规则字典id
	private String getDhDicIdByTablename(String tablename) throws ServiceException{
		String dh = "";
		DictionaryValue dicDh = null;
		List<DictionaryValue> dictionaryValues = dicService.getDicValueList("DHSC");
		for(DictionaryValue dic : dictionaryValues){
		    // 获得字典内容
		    if(dic.getDvno().equalsIgnoreCase(tablename)){
		    	dicDh = dic;
		       break;
		    }
		}
		if(dicDh!=null){
			dh = dicDh.getId();
			
		}
		return dh;
	}
	
	private List<FileNumRule> getFileNumRuleByTablename(String tablename) throws ServiceException{
		List<FileNumRule> fileNumRules = new ArrayList<FileNumRule>();
		DictionaryValue dicDh = null;
		List<DictionaryValue> dictionaryValues = dicService.getDicValueList("DHSC");
		for(DictionaryValue dic : dictionaryValues){
		    // 获得字典内容
		    if(dic.getDvno().equalsIgnoreCase(tablename)){
		    	dicDh = dic;
		       break;
		    }
		}
		if(dicDh!=null){
			fileNumRules = fileNumService.getRule(dicDh.getId());
			
		}
		return fileNumRules;
	}
}