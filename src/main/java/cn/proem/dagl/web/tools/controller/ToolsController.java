package cn.proem.dagl.web.tools.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.proem.core.annotation.LogService;
import cn.proem.core.entity.User;
import cn.proem.core.entity.UserDepartment;
import cn.proem.core.model.DataGrid;
import cn.proem.dagl.core.model.Pager;
import cn.proem.dagl.web.archives.entity.DField;
import cn.proem.dagl.web.archives.service.CustomArchiveService;
import cn.proem.dagl.web.dataCenter.service.DataCenterService;
import cn.proem.dagl.web.dicManager.entity.DictionaryValue;
import cn.proem.dagl.web.dicManager.service.DicManagerService;
import cn.proem.dagl.web.flow.service.FlowService;
import cn.proem.dagl.web.preArchive.entity.Zlsj;
import cn.proem.dagl.web.preArchive.service.ZlsjService;
import cn.proem.dagl.web.table.entity.inf.BaseEntityInf;
import cn.proem.dagl.web.tools.service.ToolsService;
import cn.proem.dagl.web.tools.util.CommonFile;
import cn.proem.dagl.web.tools.util.QueryUtils;
import cn.proem.dagl.web.upload.service.UploadService;
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
 * 工具控制层
 * @author lenovo
 *
 */
@Controller
@RequestMapping(value = "/w/example/tools")
public class ToolsController  extends BaseCtrlModel{
	 @Autowired
	 private UploadService uploadService;
	 @Autowired
	 private DataCenterService  dataCenterService;
	 @Autowired
	 private FlowService flowService;
	 @Autowired
	 private ToolsService toolsService;
	 @Autowired
	 private DicManagerService dicManagerService;
	 @Autowired
	 private CustomArchiveService customArchiveService;
	 @Autowired
	 private ZlsjService zlsjService;
	 @Autowired
	 private CommonService commonService;
	 
	 private String getValue(Cell cell) {
	        if (cell == null)
	            return "";
	        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
	            return String.valueOf(cell.getBooleanCellValue()).trim();
	        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
	            return String.valueOf(cell.getNumericCellValue()).trim();
	        } else {
	            return String.valueOf(cell.getStringCellValue()).trim();
	        }
	    }
	 
	
	/**
	 * @Description 对象导入
	 * @MethodName importTemplet
	 * @author bao
	 * @date 2017年5月17日
	 * @param request
	 * @param tablename
	 * @param attachments
	 * @return ResultModel<String>
	 */
	@RequestMapping(value = "/importTemplet")
	@ResponseBody
	@LogService(description = "对象导入")
	public ResultModel<String> importTemplet(HttpServletRequest request,@RequestParam("tablename")String tablename,@RequestParam("attachment[]") MultipartFile[] attachments){
		ResultModel<String> resultModel = new ResultModel<String>();
		String path = Path.UPLOAD_IMPORT_FILE_PATH;
		try{
			if (attachments != null){
				for (MultipartFile atta : attachments) {
					String uuidname = uploadService.upload(request,atta, path);
					String allpath = this.getFilePath()+path+uuidname;
					Map<String,Object> extraParams = new HashedMap<String, Object>();
					extraParams.put("DEL_FLAG", Integer.valueOf(0));
					extraParams.put("drz", this.getCurrentUser(request).getId());
					extraParams.put("drsj", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
					extraParams.put("CREATE_TIME", " to_date ('"+new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"' , 'YYYY-MM-DD HH24:MI' )");
					toolsService.importFromExcel(allpath, tablename, extraParams);
					
				}
			}
			
		}catch(ServiceException e){
			resultModel.setSuccess(false);
			resultModel.setMsg(e.getMessage());
		}catch (Exception e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("导入失败");
		}
		return resultModel;
	}
	
	/**
	 * @Description 资料导入
	 * @MethodName importTemplet1
	 * @author bao
	 * @date 2017年5月26日
	 * @param request
	 * @param tablename
	 * @param attachments
	 * @return ResultModel<String>
	 */
	@RequestMapping(value = "/importTemplet1")
	@ResponseBody
	@LogService(description = "资料导入")
	public ResultModel<String> importTemplet1(HttpServletRequest request,@RequestParam("tablename")String tablename,@RequestParam("attachment[]") MultipartFile[] attachments){
		ResultModel<String> resultModel = new ResultModel<String>();
		String path = Path.UPLOAD_IMPORT_FILE_PATH;
		try{
			if (attachments != null){
				for (MultipartFile atta : attachments) {
					String uuidname = uploadService.upload(request,atta, path);
					String allpath = this.getFilePath()+path+uuidname;

					List<DictionaryValue> fields =  dicManagerService.getDicValueList(tablename);
					
					
					Workbook wookbook = WorkbookFactory.create(new FileInputStream(allpath));
					//用于放ImpExcelBean的list
					//将Excel的各行记录放入ImpExcelBean的list里面
					//WorkbookFactory是用来将Excel内容导入数据库的一个类
					Sheet sheet = wookbook.getSheetAt(0);//统计excel的行数
					
					
					int rowLen = sheet.getPhysicalNumberOfRows();//excel总行数，记录数=行数-1
					//导入各条记录
					for (int i = 1; i < rowLen; i++) {
						Zlsj zlsj = new Zlsj();
						zlsj.setCreateId(this.getCurrentUser(request).getId());
						zlsj.setDrz(this.getCurrentUser(request).getName());
						zlsj.setDrsj(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
						Row row = sheet.getRow(i);
							for(DictionaryValue dictionaryValue : fields){
								if("tm".equals(dictionaryValue.getDvno())){
									zlsj.setTm(getValue(row.getCell(Integer.valueOf(dictionaryValue.getXsxh()))));
								}
								if("zrz".equals(dictionaryValue.getDvno())){
									zlsj.setZrz(getValue(row.getCell(Integer.valueOf(dictionaryValue.getXsxh()))));
								}
								if("cwrq".equals(dictionaryValue.getDvno())){
									zlsj.setCwrq(getValue(row.getCell(Integer.valueOf(dictionaryValue.getXsxh()))));
								}
								if("wh".equals(dictionaryValue.getDvno())){
									zlsj.setWh(getValue(row.getCell(Integer.valueOf(dictionaryValue.getXsxh()))));
								}
							}
							zlsjService.saveAttachment(zlsj);
					}
					
				
					
				}
			}
			
		}catch(ServiceException e){
			resultModel.setSuccess(false);
			resultModel.setMsg(e.getMessage());
		}catch (Exception e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("导入失败");
		}
		return resultModel;
	}
	
	
	
	/**
	 * @Description 根据表名查询字典表获得上传模版模版
	 * @MethodName downloadTemplet
	 * @author bao
	 * @date 2017年5月12日
	 * @param request
	 * @param response
	 * @param tablename
	 * @return ResultModel<String>
	 */
	@RequestMapping(value = "/downloadTempletDef/{tablename}")
	@ResponseBody
	@LogService(description = "根据表名查询字典表获取上传模板")
	public ResultModel<DictionaryValue> downloadTempletDef(HttpServletRequest request,HttpServletResponse response,@PathVariable("tablename")String tablename) {
		ResultModel<DictionaryValue> resultModel = new ResultModel<DictionaryValue>();
		List<DictionaryValue> fields =  dicManagerService.getDicValueList(tablename);
		try{
			 String cnNmae;
			 if("pfm_zlzl_zlsj".equalsIgnoreCase(tablename)){
				 cnNmae = "资料收集.xls";
			 }else{
				 
				 Map<String, Object> conditions = new HashMap<String, Object>();
				 conditions.put("BM", tablename);
				 BaseEntityInf table = customArchiveService.getEntityByConditions("pdagl_tablename", conditions);
				 cnNmae = table.get("zwm")+".xls";
			 }
			
			
			 HSSFWorkbook workbook = new HSSFWorkbook();			//生成工作簿
			HSSFSheet sheet = workbook.createSheet();		//生成工作页
			sheet.setDefaultColumnWidth(15);				//sheet默认列宽
			HSSFCellStyle style = workbook.createCellStyle();	//生成样式
			
			HSSFCellStyle cellStyle=workbook.createCellStyle();       //设置自动换行的样式
			cellStyle.setWrapText(true);
			
			//导出excel的样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);  
	        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
			
	        // 生成一个字体  
	        HSSFFont font = workbook.createFont();  
	        font.setColor(HSSFColor.BLACK.index);  
	        font.setFontHeightInPoints((short) 12);  
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	        style.setFont(font);
	        
	        //生成表格标题行
	        HSSFRow row = sheet.createRow(0);
	        for (int i = 0; i < fields.size(); i++) {
				HSSFCell cell = row.createCell(i);
				HSSFRichTextString text = new HSSFRichTextString(fields.get(i).getDvalue());
				cell.setCellStyle(style);
				cell.setCellValue(text);
	       
	        }
	        tablename = tablename+".xls";	//表名    
		    response.setContentType("application/vnd.ms-excel; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment;filename="+new String(cnNmae.getBytes("utf-8"), "iso8859-1"));// 设置头信息
			response.setCharacterEncoding("utf-8");
		  	OutputStream outputStream = response.getOutputStream();
			
		  	
		  	workbook.write(outputStream);
			outputStream.close();
		}catch(Exception e){
			resultModel.setSuccess(false);
			resultModel.setMsg("导出模版失败");
		}
		resultModel.setDatas(fields);
		return resultModel;
	}
	
	
	
	/**
	 * @Description 档案上传模版并导入
	 * @MethodName importTemplet
	 * @author bao
	 * @date 2017年5月17日
	 * @param request
	 * @param tablename
	 * @param attachments
	 * @return ResultModel<String>
	 */
	@RequestMapping(value = "/importFileDef")
	@ResponseBody
	@LogService(description = "档案上传模板并导入")
	public ResultModel<String> importFileTemplet(HttpServletRequest request,
							@RequestParam("tablename")String tablename,
							@RequestParam("attachment[]") MultipartFile[] attachments,
							@RequestParam("isarchive") String isarchive){
		ResultModel<String> resultModel = new ResultModel<String>();
		User user = this.getCurrentUser(request);
		String path = Path.UPLOAD_IMPORT_FILE_PATH;
		try{
			if (attachments != null){
				for (MultipartFile atta : attachments) {
					String uuidname = uploadService.upload(request,atta, path);
					//String realpath = request.getServletContext().getRealPath("/")+File.separator+ ".." + pathname.replace("/", File.separator) + realname;
					String allpath = this.getFilePath()+path+uuidname;
					Map<String,Object> extraParams = new HashedMap<String, Object>();
					extraParams.put(DField.DELFLAG, "0");
					extraParams.put(DField.ISARCHIVE, isarchive);
					extraParams.put(DField.SFJD, "sfjd_2");
					extraParams.put(DField.NOWCAMP, getDepartment(user));
					extraParams.put(DField.CREATETIME, nowTime());
					extraParams.put(DField.CREATEUSER, user.getId());
					extraParams.put(DField.UPDATEUSER, user.getId());
					extraParams.put(DField.UPDATETIME, nowTime());
					
					List<String> infos = toolsService.importFileFromExcel(allpath, tablename, extraParams,new ArrayList<String>());
					
					resultModel.setDatas(infos);
				}
			}
			
		}catch(ServiceException e){
			resultModel.setSuccess(false);
			resultModel.setMsg(e.getMessage());
		}catch (Exception e) {
			resultModel.setSuccess(false);
			resultModel.setMsg("导入失败");
		}
		return resultModel;
	}
	
	
	
	
	/**
	 * @Description 档案自定义表单全部批量下载
	 * @MethodName exportFile
	 * @author bao
	 * @date 2017年5月11日
	 * @param request
	 * @param response
	 * @param tablename
	 * @return ResultModel<String>
	 */
	@RequestMapping(value = "/exportFileDef/{tablename}/{isarchive}")
	@ResponseBody
	@LogService(description = "档案自定义表单全部批量导出")
	public ResultModel<String> exportFileDef(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("tablename")String tablename,
			@PathVariable("isarchive")Integer isarchive){
		ResultModel<String> resultModel = new ResultModel<String>();
		try{
			String query; 
			//判断档案的归档状态
			if(isarchive == 0){
				query = String.format("delflag = '%s' AND isarchive < '%s'", "0", "3");
			}else{
				query = String.format("delflag = '%s' AND isarchive = '%s'", "0", "3");
			}
			
			//获得档案所有记录
			List<BaseEntityInf> records = customArchiveService.getEntitiesByConditions(tablename, query);
			
			//获得文件对象集合
			List<CommonFile> commonFiles = new ArrayList<CommonFile>();
			
			for(BaseEntityInf entity : records){
				//根据档案获得对应附件集合
				List<CommonFile> commonFiles1 =  flowService.getFilesByDataId((String)entity.get("uuid"));
				if(commonFiles1!=null && commonFiles1.size()>0){
					commonFiles.addAll(commonFiles1);
				}
				
			}
			
			//导出的excel文件
			CommonFile commonFile = dataCenterService.exportDefFromTable(records,tablename);
			commonFiles.add(commonFile);
			
			//打包操作
			toolsService.batchDownLoad(request, response, commonFiles, tablename);
			
			//删除已有的文件
			File excelFile = new File(this.getFilePath()+commonFile.getRealpath());
			excelFile.delete();
		}catch(Exception e){
			resultModel.setSuccess(false);
			resultModel.setMsg("批量导出失败");
		}
		return resultModel;
	}
	
	/**
	 * @Description 档案自定义表单部分批量下载
	 * @MethodName exportFileDef
	 * @author bao
	 * @date 2017年5月18日
	 * @param request
	 * @param response
	 * @param tablename
	 * @param flag
	 * @param ids
	 * @return ResultModel<String>
	 */
	@RequestMapping(value = "/exportFileDef/{tablename}/{isarchive}/{ids}")
	@ResponseBody
	@LogService(description = "档案自定义表单部分批量导出")
	public ResultModel<String> exportFileDef(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("tablename")String tablename,
			@PathVariable("isarchive")Integer isarchive,
			@PathVariable("ids")String ids){
		ResultModel<String> resultModel = new ResultModel<String>();
		try{
			//获得表的定义
			
			
			String[] uuids = ids.split(",");
			
			String conditions = "delflag = '0' and uuid in(";
			for(int i = 0 ; i<uuids.length;i++){
				if(i == 0){
					conditions += "'"+uuids[i]+"'";
					
				}else{
					conditions += ",'"+uuids[i]+"'";
				}
			}
			conditions += ") ";
			
			if(isarchive == 0){
				conditions += String.format(" AND isarchive < '%s'", "3");
			}else{
				conditions += String.format(" AND isarchive = '%s'", "3");
			}
			List<BaseEntityInf> records = customArchiveService.getEntitiesByConditions(tablename, conditions);
			
			//字段表
			Boolean isFile = false;
			String relatablename = "";
			List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
			for(BaseEntityInf field: fields){
	        	//判断是否是案卷级
	        	if("fieldid".equalsIgnoreCase(field.get("zdywm").toString())){
	        		isFile = true;
	        		relatablename = field.get("did");
	        		break;
	        	}
			 }
			
			
			//获得文件对象集合
			List<CommonFile> commonFiles = new ArrayList<CommonFile>();
			for(BaseEntityInf entity : records){
				List<CommonFile> commonFiles1 =  flowService.getFilesByDataId((String)entity.get("uuid"));
				if(commonFiles1!=null && commonFiles1.size()>0){
					commonFiles.addAll(commonFiles1);
				}
				
				
				String dh = entity.get("dh");
				
				//判断是否是案卷级
				if(isFile && StringUtil.isNotEmpty(dh)){
					String condition = "DELFLAG = '0' AND  DH like '"+dh+"%' ";
					List<BaseEntityInf> relaRecords = customArchiveService.getEntitiesByConditions(relatablename, condition);
					CommonFile commonFileRela = dataCenterService.exportDefFromTable(relaRecords,relatablename);
					commonFiles.add(commonFileRela);
					
					for(BaseEntityInf relaEntity : relaRecords){
						List<CommonFile> commonFiles2 =  flowService.getFilesByDataId((String)relaEntity.get("uuid"));
						if(commonFiles2!=null && commonFiles2.size()>0){
							commonFiles.addAll(commonFiles2);
						}
					}
					
				}
				
				
			}
			
			CommonFile commonFile = dataCenterService.exportDefFromTable(records,tablename);
			commonFiles.add(commonFile);
			toolsService.batchDownLoad(request, response, commonFiles, tablename);
			
			File excelFile = new File(this.getFilePath()+commonFile.getRealpath());
			excelFile.delete();
		}catch(Exception e){
			resultModel.setSuccess(false);
			resultModel.setMsg("批量导出失败");
		}
		return resultModel;
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
     * @Method: nowTime 
     * @Description: 获得当前时间
     * @return
     */
    private String nowTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }
    
    
	/**
	 * @throws ServiceException 
	 * @Description 档案自定义报表全部批量导出
	 *
	 */
	@RequestMapping(value = "/reportFileDef/{tablename}/{ywm}/{zwm}/{dtGridPager}/{flag}/{reportTitle}")
	@ResponseBody
	@LogService(description = "档案自定义报表全部批量导出")
	public ResultModel<String> reportFileDef(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("dtGridPager")String dtGridPager,
			@PathVariable("tablename")String tablename,
			@PathVariable("ywm")List<String> ywm,
			@PathVariable("zwm")List<String> zwm,
			@PathVariable("flag")String flag,
			@PathVariable("reportTitle")String reportTitle) throws ServiceException{
		User user = this.getCurrentUser(request);
		ResultModel<String> resultModel = new ResultModel<String>();
		try{
			List<Map<String,String>> fields=new ArrayList<Map<String,String>>(0);
			for (int i = 0; i < ywm.size(); i++) {
				Map<String, String> map=new HashMap<String, String>(0);
				map.put("zdywm", ywm.get(i));
				map.put("zdzwm", zwm.get(i));
				fields.add(map);
			}
			
			List<BaseEntityInf> records = this.getRecoreds(dtGridPager, tablename, user,flag);
			
			HSSFWorkbook workbook = new HSSFWorkbook();			//生成工作簿
			HSSFSheet sheet = workbook.createSheet();		//生成工作页
			sheet.setDefaultColumnWidth(15);				//sheet默认列宽
			HSSFCellStyle style = workbook.createCellStyle();	//生成样式
			
			HSSFCellStyle cellStyle=workbook.createCellStyle();       //设置自动换行的样式
			cellStyle.setWrapText(true);
			
			//导出excel的样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);  
	        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
			
	        // 生成一个字体  
	        HSSFFont font = workbook.createFont();  
	        font.setColor(HSSFColor.BLACK.index);  
	        font.setFontHeightInPoints((short) 12);  
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	        style.setFont(font);
	        
	        /* 
	         * 设定合并单元格区域范围 
	         *  firstRow  0-based 
	         *  lastRow   0-based 
	         *  firstCol  0-based 
	         *  lastCol   0-based 
	         */  
	        HSSFCellStyle titleStyle = workbook.createCellStyle();   //标题格式
	        titleStyle.setFillForegroundColor((short) 13);// 设置背景色    
	        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
	        titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
	        titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
	        titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框    
	        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中    
	        HSSFFont titleFont = workbook.createFont();  
	        titleFont.setColor(HSSFColor.BLACK.index);  
	        titleFont.setFontHeightInPoints((short) 16);  
	        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	        titleStyle.setFont(titleFont);
	        
	        CellRangeAddress cra=new CellRangeAddress(0, 0, 0, fields.size()==0?0:fields.size()-1);   
	        sheet.addMergedRegion(cra);  
	          
	        Row rowTitle = sheet.createRow(0);
	        Cell cell_1 = rowTitle.createCell(0);  
	        cell_1.setCellValue(reportTitle);
	        cell_1.setCellStyle(titleStyle);
	        
	        //生成表格标题行
	        HSSFRow row = sheet.createRow(1);
	        for (int i = 0; i < fields.size(); i++) {
				HSSFCell cell = row.createCell(i);
				HSSFRichTextString text = new HSSFRichTextString((String)(fields.get(i).get("zdzwm")));
				cell.setCellStyle(style);
				cell.setCellValue(text);
	       
	        }
	        
	        
			if(records != null && records.size()>0){
				for(int j = 0; j < records.size(); j ++) {
					row = sheet.createRow(j+2);
					for (int i = 0; i < fields.size(); i++) {
						HSSFCell hssfCell = row.createCell(i);	//生成列   
						String value;
						if(records.get(j).get((String)fields.get(i).get("zdywm")) == null){
							value = "";
						}else{
							value = String.valueOf(records.get(j).get((String)fields.get(i).get("zdywm")));
						}
						hssfCell.setCellValue(value);
					}
				}
			}
				
				Map<String, Object> conditions = new HashMap<String, Object>();
				conditions.put("bm", tablename);
				BaseEntityInf table = customArchiveService.getEntityByConditions("pdagl_tablename", conditions);
				String cnNmae = table.get("zwm");
			
				tablename = cnNmae+".xls";	//表名    
			    response.setContentType("application/vnd.ms-excel; charset=utf-8");
				response.setHeader("Content-Disposition", "attachment;filename="+new String(tablename.getBytes("utf-8"), "iso8859-1"));// 设置头信息
				response.setCharacterEncoding("utf-8");
			  	OutputStream outputStream = response.getOutputStream();
				
			  	
			  	workbook.write(outputStream);
				outputStream.close();
		}catch(Exception e){
			resultModel.setSuccess(false);
			resultModel.setMsg("批量导出失败");
		}
		return resultModel;
	}
	
	
	@RequestMapping(value = "/reportFileDef/{tablename}/{ywm}/{zwm}/{ids}/{reportTitle}")
	@ResponseBody
	@LogService(description = "档案自定义报表全部批量导出")
	public ResultModel<String> reportFileDef(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("tablename")String tablename,
			@PathVariable("ywm")List<String> ywm,
			@PathVariable("zwm")List<String> zwm,
			@PathVariable("ids")String ids,
			@PathVariable("reportTitle")String reportTitle) throws ServiceException{
		User user = this.getCurrentUser(request);
		ResultModel<String> resultModel = new ResultModel<String>();
		try{
			List<Map<String,String>> fields=new ArrayList<Map<String,String>>(0);
			for (int i = 0; i < ywm.size(); i++) {
				Map<String, String> map=new HashMap<String, String>(0);
				map.put("zdywm", ywm.get(i));
				map.put("zdzwm", zwm.get(i));
				fields.add(map);
			}
			
			List<String> idList = new ArrayList<String>();
			String[] idArr = ids.split(",");
			for(String id : idArr){
				idList.add("'"+id+"'");
			}
				
			ids = "UUID IN ( "+StringUtils.join(idList, ",")+"	)";
    		 
    		 
 	    	List<BaseEntityInf> records = customArchiveService.getEntitiesByConditions(tablename, ids);
			
			HSSFWorkbook workbook = new HSSFWorkbook();			//生成工作簿
			HSSFSheet sheet = workbook.createSheet();		//生成工作页
			sheet.setDefaultColumnWidth(15);				//sheet默认列宽
			HSSFCellStyle style = workbook.createCellStyle();	//生成样式
			
			HSSFCellStyle cellStyle=workbook.createCellStyle();       //设置自动换行的样式
			cellStyle.setWrapText(true);
			
			//导出excel的样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);  
	        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
			
	        // 生成一个字体  
	        HSSFFont font = workbook.createFont();  
	        font.setColor(HSSFColor.BLACK.index);  
	        font.setFontHeightInPoints((short) 12);  
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	        style.setFont(font);
	        
	        /* 
	         * 设定合并单元格区域范围 
	         *  firstRow  0-based 
	         *  lastRow   0-based 
	         *  firstCol  0-based 
	         *  lastCol   0-based 
	         */  
	        HSSFCellStyle titleStyle = workbook.createCellStyle();   //标题格式
	        titleStyle.setFillForegroundColor((short) 13);// 设置背景色    
	        titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
	        titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
	        titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
	        titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框    
	        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中    
	        HSSFFont titleFont = workbook.createFont();  
	        titleFont.setColor(HSSFColor.BLACK.index);  
	        titleFont.setFontHeightInPoints((short) 16);  
	        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	        titleStyle.setFont(titleFont);
	        
	        CellRangeAddress cra=new CellRangeAddress(0, 0, 0, fields.size()==0?0:fields.size()-1);   
	        sheet.addMergedRegion(cra);  
	          
	        Row rowTitle = sheet.createRow(0);
	        Cell cell_1 = rowTitle.createCell(0);  
	        cell_1.setCellValue(reportTitle);
	        cell_1.setCellStyle(titleStyle);
	        
	        //生成表格标题行
	        HSSFRow row = sheet.createRow(1);
	        for (int i = 0; i < fields.size(); i++) {
				HSSFCell cell = row.createCell(i);
				HSSFRichTextString text = new HSSFRichTextString((String)(fields.get(i).get("zdzwm")));
				cell.setCellStyle(style);
				cell.setCellValue(text);
	       
	        }
	        
	        
			if(records != null && records.size()>0){
				for(int j = 0; j < records.size(); j ++) {
					row = sheet.createRow(j+2);
					for (int i = 0; i < fields.size(); i++) {
						HSSFCell hssfCell = row.createCell(i);	//生成列   
						String value;
						if(records.get(j).get((String)fields.get(i).get("zdywm")) == null){
							value = "";
						}else{
							value = String.valueOf(records.get(j).get((String)fields.get(i).get("zdywm")));
						}
						hssfCell.setCellValue(value);
					}
				}
			}
				
				Map<String, Object> conditions = new HashMap<String, Object>();
				conditions.put("bm", tablename);
				BaseEntityInf table = customArchiveService.getEntityByConditions("pdagl_tablename", conditions);
				String cnNmae = table.get("zwm");
			
				tablename = cnNmae+".xls";	//表名    
			    response.setContentType("application/vnd.ms-excel; charset=utf-8");
				response.setHeader("Content-Disposition", "attachment;filename="+new String(tablename.getBytes("utf-8"), "iso8859-1"));// 设置头信息
				response.setCharacterEncoding("utf-8");
			  	OutputStream outputStream = response.getOutputStream();
				
			  	
			  	workbook.write(outputStream);
				outputStream.close();
		}catch(Exception e){
			resultModel.setSuccess(false);
			resultModel.setMsg("批量导出失败");
		}
		return resultModel;
	}
	
	
	
    
    //跳转
    @RequestMapping(value = "/jump/{tablename}/{flag}")
	public ModelAndView jumpModel(HttpServletRequest request,
								  @PathVariable("tablename")String tablename,
								  @PathVariable("flag")String flag,
								  String dtGridPager,
								  String ids)
			throws ServiceException {
		ModelAndView modelAndView = this.createNormalView("/web/table/field.vm");
		 // 获得档案定义字段
		java.util.List<BaseEntityInf> dazd= customArchiveService.getFields(tablename);
		Map<String,String> map=new HashMap<String,String>(0);
		//存key
		List<String> key=new ArrayList<String>(0);
		for (BaseEntityInf b : dazd) {
			key.add((String)b.get("zdywm"));
			map.put((String)b.get("zdywm"), (String)b.get("zdzwm"));
		}
		
		 Map<String, Object> conditions = new HashMap<String, Object>();
		 conditions.put("bm", tablename);
		 BaseEntityInf table = customArchiveService.getEntityByConditions("pdagl_tablename", conditions);
		 String cnNmae = table.get("zwm");
		
		 modelAndView.addObject("cnNmae", cnNmae+"报表导出标题");  
		modelAndView.addObject("tablename", tablename);
		modelAndView.addObject("key", key);
		modelAndView.addObject("dazd", map);
		modelAndView.addObject("flag", flag);
		modelAndView.addObject("dtGridPager",dtGridPager);
		modelAndView.addObject("ids",ids);
		return modelAndView;
	}
	
	/**
	 * @param dtGridPager
	 * @param request
	 * @param tablename
	 * @return
	 * @throws ServiceException 
	 */
	private List<BaseEntityInf> getRecoreds(String dtGridPager, String tablename, User user,String flag) throws ServiceException {
	    List<BaseEntityInf> records = null;
	    Pager dataGridQuery = JSON.toJavaObject(JSON.parseObject(dtGridPager == null ? "" : dtGridPager), Pager.class);
		Map<String,Object> parameters =  dataGridQuery.getParameters();
		// 没有逻辑删除
	    parameters.put(DField.DELFLAG, "0");
		 // 获得档案定义字段
        List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
		DataGrid<HashMap<String, Object>> dataGrid = new DataGrid<HashMap<String, Object>>(dataGridQuery.getNowPage(), dataGridQuery.getPageSize());
		if((dataGridQuery.getAdvanceQueryConditions().size() > 0) || (dataGridQuery.getAdvanceQuerySorts().size() > 0)){
		    String sql = QueryUtils.getAdvanceQueryConditionSql(dataGridQuery.getAdvanceQueryConditions());
		    String orders = QueryUtils.getAdvanceQuerySortSql(dataGridQuery.getAdvanceQuerySorts());
		    if("0".equals(flag)){
		    	sql = DField.DELFLAG + "='0' and " + DField.ISARCHIVE + "<'3' " // 档案状态
				        + authDepart(user) // 部门控制
				        + sql + " " //用户设置查询条件
				        + orders; //排序
		    }else{
		    	sql = DField.DELFLAG + "='0' and " + DField.ISARCHIVE + ">='3' " // 档案状态
		    			+ authDepart(user) // 部门控制
		    			+ sql + " " //用户设置查询条件
		    			+ orders; //排序
		    }
		    records = customArchiveService.getEntitiesByConditions(tablename, sql);
		}else{
			 if("0".equals(flag)){
				 String otherSql =  " AND "+DField.ISARCHIVE + "<'3' "+ authDepart(user) ;
				 records = customArchiveService.getLikeEntitiesByPaging(tablename, parameters, otherSql,0,-1);
			 }else{
				 String otherSql = " AND " + DField.ISARCHIVE + ">='3' "+ authDepart(user) ;
				 records = customArchiveService.getLikeEntitiesByPaging(tablename, parameters, otherSql, 0,-1);
			 }
			
		}
		
		return records;
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
    
    /**
     * @Description:档案自定义表单部分导出功能
     * @author:bao
     * @time:2017年7月5日 下午2:00:44
     */
    @RequestMapping(value = "/exportExcelDef/{tablename}/{isarchive}/{dtGridPager}")
	@ResponseBody
	@LogService(description = "")
	public ResultModel<String> exportExcelDef(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("tablename")String tablename,
			@PathVariable("isarchive")String isarchive,
			@PathVariable("dtGridPager")String dtGridPager) throws ServiceException{
    	ResultModel<String> resultModel = new ResultModel<String>();
    	
	    try{
	    	
	    	Integer isarchiveNum = Integer.valueOf(isarchive);
	    	List<BaseEntityInf> records = null;
	    	User user = this.getCurrentUser(request);
	    	
	    	records = this.getRecoreds(dtGridPager, tablename, user, isarchive);
	    	
	    	List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
	    	
	    	
	    /*	Pager dataGridQuery = JSON.toJavaObject(JSON.parseObject(dtGridPager == null ? "" : dtGridPager), Pager.class);
	    	Map<String,Object> parameters =  dataGridQuery.getParameters();
	    	List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
	    	
	    	
	    	//归档状态
	    	if(isarchiveNum>=3){
	    		
	    		// 没有逻辑删除
	    		parameters.put(DField.DELFLAG, "0");
	    		// 状态为档案
	    		parameters.put(DField.ISARCHIVE, "3");
	    		// 获得档案定义字段
	    		// 存在高级查询条件
	    		if((dataGridQuery.getAdvanceQueryConditions().size() > 0) || (dataGridQuery.getAdvanceQuerySorts().size() > 0)){
	    			String sql = QueryUtils.getAdvanceQueryConditionSql(dataGridQuery.getAdvanceQueryConditions());
	    			String orders = QueryUtils.getAdvanceQuerySortSql(dataGridQuery.getAdvanceQuerySorts());
	    			sql = DField.DELFLAG + "='0' and " + DField.ISARCHIVE + "='3' " // 档案状态
	    					+ authDepart(user) // 部门控制
	    					+ sql + " " //用户设置查询条件
	    					+ orders; //排序
	    			records = customArchiveService.getEntitiesByPaging(tablename, sql, 0, 1000000);
	    		}else{
	    			// 获得未被删除的所有数据
	    			records = customArchiveService.getLikeEntitiesByPaging(tablename, parameters, authDepart(user), 0, 1000000);
	    		}
	    	//预归档
	    	}else{
	    			if(dataGridQuery.getAdvanceQueryConditions().size() > 0){
	    			    String sql = QueryUtils.getAdvanceQueryConditionSql(dataGridQuery.getAdvanceQueryConditions());
	    			    String orders = QueryUtils.getAdvanceQuerySortSql(dataGridQuery.getAdvanceQuerySorts());
	    			    sql = DField.DELFLAG + "='0' and " + DField.ISARCHIVE + "<'3' " // 档案状态
	    			        + authDepart(user) // 部门控制
	    			        + sql + " " //用户设置查询条件
	    			        + orders; //排序
	    		        records = customArchiveService.getEntitiesByPaging(tablename, sql, 0, 10000);
	    		        // 字段值
	    			}else{
	    				String otherSql = DField.DELFLAG + "='0' and " + DField.ISARCHIVE + "<'3' "+ authDepart(user) + " ORDER BY UUID";
	    				// 获得未被删除的所有数据
	    				String conditions = DField.DELFLAG + "='0' and " + DField.ISARCHIVE + "<'3' ";
	    				records = customArchiveService.getEntitiesByPaging(tablename,otherSql, 0,10000);
	    				
	    			}
	    		   
	    			
	    			
		   
	    	}*/
	    	
	    	
	    	
			
	        // 获得所有数据
	        HSSFWorkbook workbook = new HSSFWorkbook();			//生成工作簿
			HSSFSheet sheet = workbook.createSheet();		//生成工作页
			sheet.setDefaultColumnWidth(15);				//sheet默认列宽
			HSSFCellStyle style = workbook.createCellStyle();	//生成样式
			
			HSSFCellStyle cellStyle=workbook.createCellStyle();       //设置自动换行的样式
			cellStyle.setWrapText(true);
			
			//导出excel的样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);  
	        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
			
	        // 生成一个字体  
	        HSSFFont font = workbook.createFont();  
	        font.setColor(HSSFColor.BLACK.index);  
	        font.setFontHeightInPoints((short) 12);  
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	        style.setFont(font);
	        
	        for(int i = 0;i<fields.size();i++){
	        	if("fieldid".equalsIgnoreCase((String)fields.get(i).get("zdywm"))){
	        		fields.remove(i); 
				}
	        }
	        
	        //Excel行
	        
			for(int i = 0;i<records.size()+1;i++){
				
				
				HSSFRow row = sheet.createRow(i);
				for(int j = 0;j<fields.size();j++){
					
					
					BaseEntityInf field = fields.get(j);
					
					
					HSSFCell cell = row.createCell(j);
					//绘制表头
					if(i == 0){
						HSSFRichTextString text = new HSSFRichTextString((String)(field.get("zdzwm")));
						cell.setCellStyle(style);
						cell.setCellValue(text);
						continue;
					}
					
					
					BaseEntityInf record = records.get(i-1);
					
					 // 判断列是否是字典值
	                String did = field.get("did");
	                // 字段英文名
	                String zdywm = field.get("zdywm");
	                String value;
	                
	                
	                
	                if ("0".equals(field.get("zdlx"))) {
	                	value = (String) record.get((String) field.get("zdywm"));
	                }else if("1".equals(field.get("zdlx"))){
	                	value = (String) record.get((String) field.get("zdywm"));
	                }else if("2".equals(field.get("zdlx"))){
	                	value = "";
	                	String vals = record.get(zdywm);
	                	List<DictionaryValue> dictionaryValues = dicManagerService.getDicValueList(did);
	                	for (DictionaryValue dictionaryValue : dictionaryValues) {
	                		if(dictionaryValue.getDvno().equals(vals)){
	                			value = dictionaryValue.getDvalue();
	                			continue;
	                		}
	                	}
	                	
	                }else if ("3".equals(field.get("zdlx"))) {
	                	value = (String) record.get((String) field.get("zdywm"));
	                }else  if("4".equals(field.get("zdlx"))){
                        String vals = record.get(zdywm);
                        if(vals!=null){
                            String rets[] = vals.split(",");
                            vals = "";
                            vals = this.getDicTitle(did, rets[0]);
                            for(int k=1;k<rets.length;k++){
                                vals += "," + this.getDicTitle(did, rets[k]);
                            }
                            value = vals;
                        }else{
                        	value = "";
                        }
                    }else {
                    	value = (String) record.get((String) field.get("zdywm"));
                    }
	                
	                
	              /*  //判断字典项
					if(did == null || "".equals(did)){
	                    // 列值编辑
	                    if(record.get(zdywm)!=null){
	                    	value = record.get(zdywm);
	                    }else{
	                    	value = "";
	                    }
	                }else{
	                    if("4".equals(field.get("zdlx"))){
	                        String vals = record.get(zdywm);
	                        if(vals!=null){
	                            String rets[] = vals.split(",");
	                            vals = "";
	                            vals = this.getDicTitle(did, rets[0]);
	                            for(int k=1;k<rets.length;k++){
	                                vals += "," + this.getDicTitle(did, rets[k]);
	                            }
	                            value = vals;
	                        }else{
	                        	value = "";
	                        }
	                    }else{
	                    	value = this.getDicTitle(did, (String) record.get(zdywm));
	                    }
	                }
					*/
					
					cell.setCellValue(value);
					
					
				}
			}
	    	
			 Map<String, Object> conditions = new HashMap<String, Object>();
			 conditions.put("bm", tablename);
			 BaseEntityInf table = customArchiveService.getEntityByConditions("pdagl_tablename", conditions);
			 String cnNmae = table.get("zwm")+".xls";
		    response.setContentType("application/vnd.ms-excel; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment;filename="+new String(cnNmae.getBytes("utf-8"), "iso8859-1"));// 设置头信息
			response.setCharacterEncoding("utf-8");
		  	OutputStream outputStream = response.getOutputStream();
			
		  	
		  	workbook.write(outputStream);
			outputStream.close();
	    }catch(Exception e){
			resultModel.setSuccess(false);
			resultModel.setMsg("excel失败");
		}
		return resultModel;
		
    }
    
    /**
	 * 获得字典标题值
	 * @param did
	 * @param val
	 * @return
	 */
	private String getDicTitle(String did, String val){
	    List<DictionaryValue> dictionaryValues = dicManagerService
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
	 * 
	 * @Description:档案部分导出excel
	 * @author:bao
	 * @time:2017年7月7日 上午9:25:54
	 */
	@RequestMapping(value = "/exportExcelDef/{tablename}/{ids}")
	@ResponseBody
	@LogService(description = "")
	public ResultModel<String> exportExcelDef(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("tablename")String tablename,
			@PathVariable("ids")String ids) throws ServiceException{
    	ResultModel<String> resultModel = new ResultModel<String>();
    	List<BaseEntityInf> fields = customArchiveService.getFields(tablename);
    	 try{
    		List<String> idList = new ArrayList<String>();
			String[] idArr = ids.split(",");
			for(String id : idArr){
				idList.add("'"+id+"'");
			}
				
			ids = "UUID IN ( "+StringUtils.join(idList, ",")+"	)";
    		 
    		 
 	    	List<BaseEntityInf> records = customArchiveService.getEntitiesByConditions(tablename, ids);
 	    	
 	    	 // 获得所有数据
	        HSSFWorkbook workbook = new HSSFWorkbook();			//生成工作簿
			HSSFSheet sheet = workbook.createSheet();		//生成工作页
			sheet.setDefaultColumnWidth(15);				//sheet默认列宽
			HSSFCellStyle style = workbook.createCellStyle();	//生成样式
			
			HSSFCellStyle cellStyle=workbook.createCellStyle();       //设置自动换行的样式
			cellStyle.setWrapText(true);
			
			//导出excel的样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);  
	        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
	        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
	        style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
	        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
			
	        // 生成一个字体  
	        HSSFFont font = workbook.createFont();  
	        font.setColor(HSSFColor.BLACK.index);  
	        font.setFontHeightInPoints((short) 12);  
	        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
	        style.setFont(font);
	        
	        for(int i = 0;i<fields.size();i++){
	        	if("fieldid".equalsIgnoreCase((String)fields.get(i).get("zdywm"))){
	        		fields.remove(i); 
				}
	        }
	        
	        //Excel行
	        
			for(int i = 0;i<records.size()+1;i++){
				
				
				HSSFRow row = sheet.createRow(i);
				for(int j = 0;j<fields.size();j++){
					
					
					BaseEntityInf field = fields.get(j);
					
					
					HSSFCell cell = row.createCell(j);
					//绘制表头
					if(i == 0){
						HSSFRichTextString text = new HSSFRichTextString((String)(field.get("zdzwm")));
						cell.setCellStyle(style);
						cell.setCellValue(text);
						continue;
					}
					
					
					BaseEntityInf record = records.get(i-1);
					
					 // 判断列是否是字典值
	                String did = field.get("did");
	                // 列标题编辑
	                String zdywm = field.get("zdywm");
	                String value;
	                
	                
	                if ("0".equals(field.get("zdlx"))) {
	                	value = (String) record.get((String) field.get("zdywm"));
	                }else if("1".equals(field.get("zdlx"))){
	                	value = (String) record.get((String) field.get("zdywm"));
	                }else if("2".equals(field.get("zdlx"))){
	                	value = "";
	                	String vals = record.get(zdywm);
	                	List<DictionaryValue> dictionaryValues = dicManagerService.getDicValueList(did);
	                	for (DictionaryValue dictionaryValue : dictionaryValues) {
	                		if(dictionaryValue.getDvno().equals(vals)){
	                			value = dictionaryValue.getDvalue();
	                			continue;
	                		}
	                	}
	                	
	                }else if ("3".equals(field.get("zdlx"))) {
	                	value = (String) record.get((String) field.get("zdywm"));
	                }else  if("4".equals(field.get("zdlx"))){
                        String vals = record.get(zdywm);
                        if(vals!=null){
                            String rets[] = vals.split(",");
                            vals = "";
                            vals = this.getDicTitle(did, rets[0]);
                            for(int k=1;k<rets.length;k++){
                                vals += "," + this.getDicTitle(did, rets[k]);
                            }
                            value = vals;
                        }else{
                        	value = "";
                        }
                    }else {
                    	value = (String) record.get((String) field.get("zdywm"));
                    }
	                
	                /*//判断字典项
					if(did == null || "".equals(did)){
	                    // 列值编辑
	                    if(record.get(zdywm)!=null){
	                    	value = record.get(zdywm);
	                    }else{
	                    	value = "";
	                    }
	                }else{
	                    if("4".equals(field.get("zdlx"))){
	                        String vals = record.get(zdywm);
	                        if(vals!=null){
	                            String rets[] = vals.split(",");
	                            vals = "";
	                            vals = this.getDicTitle(did, rets[0]);
	                            for(int k=1;k<rets.length;k++){
	                                vals += "," + this.getDicTitle(did, rets[k]);
	                            }
	                            value = vals;
	                        }else{
	                        	value = "";
	                        }
	                    }else{
	                    	value = this.getDicTitle(did, (String) record.get(zdywm));
	                    }
	                }*/
					
					
					cell.setCellValue(value);
					
					
				}
			}
	    	
			 Map<String, Object> conditions = new HashMap<String, Object>();
			 conditions.put("bm", tablename);
			 BaseEntityInf table = customArchiveService.getEntityByConditions("pdagl_tablename", conditions);
			 String cnNmae = table.get("zwm")+".xls";
		    response.setContentType("application/vnd.ms-excel; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment;filename="+new String(cnNmae.getBytes("utf-8"), "iso8859-1"));// 设置头信息
			response.setCharacterEncoding("utf-8");
		  	OutputStream outputStream = response.getOutputStream();
			
		  	
		  	workbook.write(outputStream);
			outputStream.close();
 	    	
 	    	
    	 }catch(Exception e){
    		resultModel.setSuccess(false);
 			resultModel.setMsg("excel导出失败");
    	 }
    	return resultModel;
	}
}
