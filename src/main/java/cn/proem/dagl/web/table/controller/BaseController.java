package cn.proem.dagl.web.table.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.proem.core.annotation.LogService;
import cn.proem.core.entity.User;
import cn.proem.core.entity.UserDepartment;
import cn.proem.core.model.DataGrid;
import cn.proem.core.model.Module;
import cn.proem.dagl.core.model.Pager;
import cn.proem.dagl.web.archives.entity.DField;
import cn.proem.dagl.web.archives.service.CustomArchiveService;
import cn.proem.dagl.web.common.entity.CheckBox;
import cn.proem.dagl.web.common.entity.DateE;
import cn.proem.dagl.web.common.entity.Input;
import cn.proem.dagl.web.common.entity.Number;
import cn.proem.dagl.web.common.entity.Option;
import cn.proem.dagl.web.common.entity.SearchInput;
import cn.proem.dagl.web.common.entity.Select;
import cn.proem.dagl.web.common.entity.SimpleInput;
import cn.proem.dagl.web.common.entity.SimpleSelect;
import cn.proem.dagl.web.common.entity.Textarea;
import cn.proem.dagl.web.dicManager.entity.DictionaryValue;
import cn.proem.dagl.web.dicManager.service.DicManagerService;
import cn.proem.dagl.web.preArchive.entity.Ywgj;
import cn.proem.dagl.web.preArchive.service.YwgjService;
import cn.proem.dagl.web.preArchive.service.ZlsjService;
import cn.proem.dagl.web.table.dto.Header;
import cn.proem.dagl.web.table.entity.inf.BaseEntityInf;
import cn.proem.dagl.web.table.entity.inf.Tag;
import cn.proem.dagl.web.table.service.RoleService;
import cn.proem.dagl.web.tools.util.QueryUtils;
import cn.proem.suw.web.auth.service.PrivilegeManagementService;
import cn.proem.suw.web.common.constant.Path;
import cn.proem.suw.web.common.exception.ServiceException;
import cn.proem.suw.web.common.exception.UserNotExistException;
import cn.proem.suw.web.common.model.BaseCtrlModel;
import cn.proem.suw.web.common.model.ResultModel;
import cn.proem.suw.web.common.service.CommonService;
import cn.proem.suw.web.common.util.StringUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 资料收集控制层
 * 
 * @author bao
 *
 */
@Controller
@RequestMapping(value = "/w/example/table")
public class BaseController extends BaseCtrlModel {
    
    @Autowired
    private CommonService commonService;
	@Autowired
	private CustomArchiveService customArchiveService;
	@Autowired
	private DicManagerService dicService;
    @Autowired
    private PrivilegeManagementService privilegeManagementService;
    @Autowired
	private YwgjService ywgjService;
    @Autowired
    private RoleService roleService;
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
	public ModelAndView init(HttpServletRequest request,@PathVariable("tablename") String tablename)throws ServiceException {
	    User user = this.getCurrentUser(request);
	    List<Module> buttons = buttons(user);
		ModelAndView modelAndView = this.createNormalView("/web/table/list.vm");
		modelAndView.addObject("tablename", tablename);
		//是否是案卷级
		Boolean flag = false;
		//对应文件级表名
		String relatablename = "";
		// 获得档案定义字段
        List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
        
        List<String> htmls = new ArrayList<String>();
        // 查询字段获得
        for(BaseEntityInf field: fields){
        	//判断是否是案卷级
        	if("fieldid".equalsIgnoreCase(field.get("zdywm").toString())){
        		flag = true;
        		relatablename = field.get("did");
        	}
            String sfcxx = field.get("sfcxx");
            if("1".equals(sfcxx)){
                Tag search =  makeSearch(field);;
                // 查询项目，添加查询输入框
                htmls.add(search.html());
            }
        }
        
        for(Module button: buttons){
            modelAndView.addObject(button.getCode(), true && roleService.hasDalx(user.getId(), tablename));
        }
        
        modelAndView.addObject("flag", flag);
        modelAndView.addObject("relatablename", relatablename);
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
	public ResultModel<Header> getHeader(HttpServletRequest request,@PathVariable("tablename") String tablename)throws ServiceException {
		ResultModel<Header> resultModel = new ResultModel<Header>();
	    // 获得档案定义字段
	    List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
		List<Header> headers = this.getHeaders(fields);;
		resultModel.setDatas(headers);
		return resultModel;
	
	}

	
	/**
	 * @Description 表格赋值
	 * @MethodName getList
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
		 // 获得档案定义字段
        List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
		DataGrid<HashMap<String, Object>> dataGrid = new DataGrid<HashMap<String, Object>>(dataGridQuery.getNowPage(), dataGridQuery.getPageSize());
		// 存在高级查询条件
		if(dataGridQuery.getAdvanceQueryConditions().size() > 0){
		    String sql = QueryUtils.getAdvanceQueryConditionSql(dataGridQuery.getAdvanceQueryConditions());
		    
		    sql = DField.DELFLAG + "='0' and " + DField.ISARCHIVE + ">='3' " // 档案状态
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
		    //查询档号是否存在
	        Boolean hasDh = false;
	        List<BaseEntityInf> fieldList = customArchiveService.getFields(tablename);
	        for(BaseEntityInf field : fieldList){
	            if("dh".equalsIgnoreCase((String)field.get("ZDYWM"))){
	                hasDh=true;
	                break;
	            }
	        }
	        //String otherSql = authDepart(user);
	        String otherSql =  " AND "+DField.ISARCHIVE + ">='3' "+ authDepart(user) ;
	        if(hasDh && "".equals(orders.trim())){
	            otherSql = otherSql + " ORDER BY DH,UUID";
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
	@LogService(description = "表格一览页面数据删除")
	public ResultModel<String> delete(HttpServletRequest request,
			@PathVariable("tablename") String tablename,
			@PathVariable("id") String id) {
		ResultModel<String> resultModel = new ResultModel<String>();
		User user = this.getCurrentUser(request);
		String time = this.nowTime();
		// 获取表定义
		try {
			if(zlsjService.deleteYwgz(id)){
				BaseEntityInf entity = customArchiveService.getEntity(tablename, id);
				entity.set("delflag", "1");
				entity.set(DField.UPDATEUSER, user.getId());
				entity.set(DField.UPDATETIME, time);
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
				.createNormalView("/web/table/detail.vm");
	    // 获得档案定义字段
        List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
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
//				element.setVal((String) record.get((String) field.get("zdywm")));
			} else if ("1".equals(field.get("zdlx"))) {
				// 日期
				element = new DateE();
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
//				element.setVal((String) record.get((String) field.get("zdywm")));
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
							dictionaryValue.getDvno().equals((String) record
									.get((String) field.get("zdywm"))) ? "selected"
									: "", dictionaryValue.getDvalue()));
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
//				element.setVal((String) record.get((String) field.get("zdywm")));
			} else if ("4".equals(field.get("zdlx"))) {
				StringBuilder checkBoxs = new StringBuilder();
				// 复选框
				List<DictionaryValue> dictionaryValues = dicService
						.getDicValueList((String) field.get("did"));
				    for (DictionaryValue dictionaryValue : dictionaryValues) {
				        String vals = record.get((String) field.get("zdywm"));
				        if(vals!=null){
	                        String rets[] = vals.split(",");
	                        for(int i=0; i< rets.length; i++){
	                            if(dictionaryValue.getDvno().equals(rets[i]) ){
	                                checkBoxs.append(String
	                                        .format("<input type='checkbox' name='%s' value='%s' %s title='%s'>",
	                                                (String) field.get("zdywm"),
	                                                dictionaryValue.getDvno(),
	                                                "checked",
	                                                dictionaryValue.getDvalue()));
	                                break;
	                            }else if(i==(rets.length-1)){
	                                checkBoxs.append(String.format("<input type='checkbox' name='%s' value='%s' %s title='%s'>",
	                                        (String) field.get("zdywm"),
	                                        dictionaryValue.getDvno(),
	                                        "",
	                                        dictionaryValue.getDvalue()));
	                            }
	                        }
				        }else{
				            checkBoxs.append(String.format("<input type='checkbox' name='%s' value='%s' %s title='%s'>",
                                    (String) field.get("zdywm"),
                                    dictionaryValue.getDvno(),
                                    "",
                                    dictionaryValue.getDvalue()));
				        }
	                }
				element = new CheckBox();
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
				element.setVal(checkBoxs.toString());
			} else if ("5".equals(field.get("zdlx"))) {
				// 数字型
				element = new Number();
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
//				element.setVal((String) record.get((String) field.get("zdywm")));
			} else if ("6".equals(field.get("zdlx"))) {
				// 可输入下拉框
				element = new Select();
				element.setVal((String) record.get((String) field.get("zdywm")));
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
		modelAndView.addObject("detailid", id);
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
				.createNormalView("/web/table/detail.vm");
		// 获得档案定义字段
        List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
		// 获得所有数据
		List<String> htmls = new ArrayList<String>();
		// 获得所有字段的html
		for (BaseEntityInf field : fields) {
			Tag element = null;
			if ("0".equals(field.get("zdlx"))) {
				// 字符类型
				element = new Input();
				element.setVal("");
//				(String) record.get((String) field.get("zdywm"));
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
//				element.setRequired();
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
				//判断是否必填项
				if("1".equals((String) field.get("sfbtx"))) {
					element.setRequired(true);
				}
				// 结果setVal
				element.setVal(options.toString());
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
	            //设置默认的归档状态
	            entity.set(DField.CREATEUSER, user.getId());
	            //设置更新人员
	            entity.set(DField.UPDATETIME, time);
	            //设置更新时间
	            entity.set(DField.UPDATEUSER, user.getId());
	            
	           
	        }else{

	            // 档案添加原文，使用原文UUID
	            entity.set(DField.UUID, fileUUID);
	            //设置集团号
                entity.set(DField.NOWCAMP, getDepartment(user));
	            //设置更新人员
                entity.set(DField.CREATETIME, time);
                //设置更新时间
                entity.set(DField.CREATEUSER, user.getId());
                //设置更新人员
                entity.set(DField.UPDATETIME, time);
                //设置更新时间
                entity.set(DField.UPDATEUSER, user.getId());
	        }
        }else{
            // 更新表记录
            // 获得表记录对象
            entity = customArchiveService.getEntity(tablename,request.getParameter("detailId"));
            //设置更新人员
            entity.set(DField.UPDATETIME, time);
            //设置更新时间
            entity.set(DField.UPDATEUSER, user.getId());
        }
		// 设置页面获得值
		this.setEntity(request, fields, entity);
		//设置默认删除标识
        entity.set(DField.DELFLAG, "0");
        //设置默认的归档状态
        entity.set(DField.ISARCHIVE,"3");
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
				.createNormalView("/web/table/import.vm");
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
	@LogService(description = "批量删除")
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
					entity.set(DField.UPDATEUSER, this.getCurrentUser(request).getId());
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
        
        //状态值
        header = new Header();
        header.setHide(true);
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
     * @Description 案卷、文件关联页面
     * @MethodName relative
     * @author bao
     * @date 2017年6月1日
     * @param request
     * @param tablename
     * @param id
     * @return
     * @throws ServiceException ModelAndView
     */
    @RequestMapping(value = "/relative/{tablename}/{relatablename}/{id}")
	public ModelAndView relative(HttpServletRequest request,
						@PathVariable("tablename") String tablename,
						@PathVariable("relatablename") String relatablename,
						@PathVariable("id") String id) throws ServiceException {
    	ModelAndView modelAndView = this.createNormalView("/web/table/relative.vm");
    	
    	User user = this.getCurrentUser(request);
    	Boolean isAdminOrFileAdmin = commonService.isAdminOrFileAdmin(user);
    	
		modelAndView.addObject("relatablename", relatablename);
		modelAndView.addObject("tablename", tablename);
		modelAndView.addObject("id", id);
		modelAndView.addObject("isAdminOrFileAdmin", isAdminOrFileAdmin);
		
		
		return modelAndView;
    }
    
    /**
     * @Description 获得相关的文件级表格数据
     * @MethodName getRelative
     * @author bao
     * @date 2017年6月1日
     * @param dtGridPager
     * @param request
     * @param tablename
     * @param id
     * @return
     * @throws ServiceException String
     */
    @RequestMapping(value = "/getRelative/{tablename}/{relatablename}/{id}", method = RequestMethod.POST)
	@ResponseBody
	@LogService(description = "获取相关文件级表格数据")
	public String getRelative(String dtGridPager, HttpServletRequest request,
							@PathVariable("tablename") String tablename,
							@PathVariable("relatablename") String relatablename,
							@PathVariable("id") String id)throws ServiceException {
	    Pager dataGridQuery = parseToPager(dtGridPager == null ? "" : dtGridPager);
	    DataGrid<HashMap<String, Object>> dataGrid = new DataGrid<HashMap<String, Object>>(dataGridQuery.getNowPage(), dataGridQuery.getPageSize());
	    List<HashMap<String, Object>> records = new ArrayList<HashMap<String,Object>>();
	    //案卷级对象
	    BaseEntityInf entity = customArchiveService.getEntity(tablename, id);
	    String dh = entity.get("DH");
	    if(StringUtil.isEmpty(dh)){
	    	 return JSON.toJSONStringWithDateFormat(dataGrid,
	                 "yyyy-MM-dd HH:mm:ss",
	                 SerializerFeature.WriteDateUseDateFormat,
	                 SerializerFeature.DisableCircularReferenceDetect);
	    }else{
	    	String conditions = "DELFLAG = '0' AND  DH like '"+dh+"%' ";
		    dataGrid.setRecordCount(customArchiveService.getEntitiesByConditions(relatablename, conditions).size());
		    List<BaseEntityInf> entities = customArchiveService.getEntitiesByPaging(relatablename, conditions, dataGridQuery.getStartRecord(), dataGridQuery.getPageSize());
		    for(BaseEntityInf baseEntity : entities){
		    	HashMap<String, Object> record = new HashMap<String, Object>();
		    	record.put("uuid", baseEntity.get("UUID"));
		    	record.put("dh", baseEntity.get("DH"));
		    	record.put("tm", baseEntity.get("TM"));
		    	records.add(record);
		    }
		    dataGrid.setExhibitDatas(records);
		    
		    return JSON.toJSONStringWithDateFormat(dataGrid,
	                "yyyy-MM-dd HH:mm:ss",
	                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect);
	    }
    }
    
    private List<Module> buttons(User user){
        return privilegeManagementService.getPrivilegeButtons(user);
    }
    
    
    /**
     * @Description 跳转批量挂接页面
     * @MethodName toBatchHookView
     * @author bao
     * @date 2017年6月12日
     * @param request
     * @param tablename
     * @return ModelAndView
     */
    @RequestMapping(value = "/toBatchHookView/{tablename}")
	public ModelAndView toBatchHookView(HttpServletRequest request,@PathVariable("tablename") String tablename){
    	ModelAndView modelAndView = this.createNormalView("/web/table/batchHook.vm");
    	
    	modelAndView.addObject("tablename", tablename);
    	return modelAndView;
    }
    
    /**
     * @Description 批量挂接操作
     * @MethodName batchHook
     * @author bao
     * @date 2017年6月12日
     * @param request
     * @param tablename
     * @return ResultModel<String>
     */
    @RequestMapping(value = "/batchHook/{tablename}")
    @ResponseBody
    public ResultModel<String> batchHook(HttpServletRequest request,
    		@PathVariable("tablename") String tablename,
    		@RequestParam("attachment[]") MultipartFile[] attachments){
    	
    	
    	ResultModel<String> resultModel = new ResultModel<String>();
    	List<String> infos = new ArrayList<String>();
    	String errorinfo = "导入失败的文件:%s";
    	String info = "本次导入共计:%d条,成功:%d条,失败: %d条";
    	// 记录总条数
		Integer totolCount = attachments.length;
		// 导入成功条数
		Integer successCount = 0;
		// 导入失败条数
		Integer errorCount = 0;

    	try {
			if (attachments != null) {
				for(int i=0;i<attachments.length;i++){
					MultipartFile atta = attachments[i];
					if (!atta.isEmpty() && (atta != null && !"".equals(atta.getOriginalFilename()))) {
						String filename = atta.getOriginalFilename();// 张三.doc
						String prefilename = atta.getOriginalFilename().substring(0,atta.getOriginalFilename().lastIndexOf("."));// 张三
						String suffilename = atta.getOriginalFilename().substring(atta.getOriginalFilename().lastIndexOf("."));// 获得后缀	.doc
						String filetype = atta.getOriginalFilename().substring(atta.getOriginalFilename().lastIndexOf(".")+1);// 获得后缀	doc
						String savename = UUID.randomUUID().toString() + suffilename; //uuid.doc
						String filepath = Path.UPLOAD_PLGJ_FILE_PATH + new SimpleDateFormat("yyyyMMdd").format(new Date()) +File.separator + savename;
						
						List<BaseEntityInf> entites = customArchiveService.getEntitiesByConditions(tablename, " DH LIKE '"+prefilename+"%' " +" and "+ DField.DELFLAG + "='0'");
						if(entites.size() == 1){
							BaseEntityInf entity = entites.get(0);
							successCount++;
							Ywgj ywgj = new Ywgj();
							
							String realpath = this.getFilePath() + filepath;
							File fileTo = new File(realpath);
							if (!fileTo.exists()) {
								fileTo.mkdirs();
							}
							atta.transferTo(fileTo);
							
							//ywgj.setXsxh(i+1);
							ywgj.setWjdz(filepath);
							ywgj.setScrq(new Date());
							ywgj.setScz(getCurrentUser(request).getId());
							ywgj.setWjlx(filetype);
							ywgj.setWjdx(atta.getSize());
							ywgj.setWjm(atta.getOriginalFilename());
							ywgj.setZlsj((String)entity.get("uuid"));
							try {
								ywgjService.saveAttachment(ywgj);
							} catch (ServiceException e) {
								resultModel.setSuccess(false);
								resultModel.setMsg(e.getMessage());
							} catch (Exception e) {
								resultModel.setSuccess(false);
								resultModel.setMsg(e.getMessage());
							}
						}else{
							infos.add(String.format(errorinfo, filename));
							errorCount++;
						}
						
					}
					
				}
			}
			
			
    	}catch (Exception e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("附件上传失败");
		}
    	infos.add(String.format(info, totolCount,successCount,errorCount));
    	resultModel.setDatas(infos);
    	return resultModel;
    }
    
    
    /**
	 * @Description 修改资料收集(上传和修改操作)
	 * @MethodName uploadFile
	 * @author bao
	 * @date 2017年4月20日
	 * @param request
	 * @param attachments
	 * @return ResultModel<String>
	 */
	@RequestMapping(value = "/uploadFile")
	@ResponseBody
	public ResultModel<String> uploadFile(HttpServletRequest request,
			@RequestParam("attachment[]") MultipartFile[] attachments,
			@RequestParam("dataCollectId") String dataCollectId){
		ResultModel<String> resultModel = new ResultModel<String>();
		
		//解析数据
		
		if(StringUtil.isEmpty(dataCollectId)){
			dataCollectId = UUID.randomUUID().toString();
		}
		List<String> msg = new ArrayList<String>();
		try {
			if (attachments != null) {
				for(int i=0;i<attachments.length;i++){
					MultipartFile atta = attachments[i];
					Ywgj ywgj = new Ywgj();
					if (!atta.isEmpty() && (atta != null && !"".equals(atta.getOriginalFilename()))) {
						String extName = atta.getOriginalFilename().substring(
								atta.getOriginalFilename().lastIndexOf("."));// 获得后缀
						String fileType = atta.getOriginalFilename().substring(
								atta.getOriginalFilename().lastIndexOf(".")+1);// 获得后缀
						String filePath = UUID.randomUUID().toString() + extName;
						String fileName = atta.getOriginalFilename();
						
						msg.add(fileName);
						
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
						
						String path = simpleDateFormat.format(new Date());
						
						String realpath = this.getFilePath() + Path.UPLOAD_DAGL_FILE_PATH + path +"/" + filePath;
						File fileTo = new File(realpath);
						if (!fileTo.exists()) {
							fileTo.mkdirs();
						}
						atta.transferTo(fileTo);
						
						ywgj.setXsxh(i+1);
						ywgj.setWjdz(Path.UPLOAD_DAGL_FILE_PATH+path+File.separator+ filePath);
						ywgj.setScrq(new Date());
						ywgj.setScz(getCurrentUser(request).getId());
						ywgj.setWjlx(fileType);
						ywgj.setWjdx(atta.getSize());
						ywgj.setWjm(fileName);
						ywgj.setZlsj(dataCollectId);
						try {
							ywgjService.saveAttachment(ywgj);
						} catch (ServiceException e) {
							resultModel.setSuccess(false);
							resultModel.setMsg(e.getMessage());
						} catch (Exception e) {
							resultModel.setSuccess(false);
							resultModel.setMsg(e.getMessage());
						}
					}
				}
			}
		} catch (Exception e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("附件上传失败");
		}
		resultModel.setMsg("资料"+StringUtils.join(msg.toArray(),",")+"上传成功");
		resultModel.setData(dataCollectId);
		return resultModel;
	}
    
    
}
