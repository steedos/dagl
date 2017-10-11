package cn.proem.dagl.web.fileIdentify.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.proem.core.annotation.LogService;
import cn.proem.core.dao.GeneralDao;
import cn.proem.core.entity.User;
import cn.proem.core.model.DataGrid;
import cn.proem.core.model.QueryBuilder;
import cn.proem.dagl.web.fileIdentify.entity.FileIdentify;
import cn.proem.dagl.web.fileIdentify.entity.IdentifyContent;
import cn.proem.dagl.web.fileIdentify.service.DestroyHistroyService;
import cn.proem.dagl.web.preArchive.entity.Zlsj;
import cn.proem.suw.web.common.exception.ServiceException;

/**
 * 销毁历史服务层
 * @author lenovo
 *
 */
@Service
public class DestroyHistroyServiceImpl implements DestroyHistroyService {
	@Autowired
	private GeneralDao generalDao;
	
	
	
	public DataGrid<FileIdentify> getDestroyTables(QueryBuilder queryBuilder,int nowPage, int pageSize) {
		DataGrid<FileIdentify> dataGrid = new DataGrid<FileIdentify>(nowPage, pageSize);
		dataGrid.setRecordCount(generalDao.countByCriteria(FileIdentify.class, queryBuilder));
		
		List<FileIdentify> list = new ArrayList<FileIdentify>();
		list = generalDao.queryByCriteria(FileIdentify.class, queryBuilder, null,dataGrid.getStartRecord(), dataGrid.getPageSize());
		
		dataGrid.setExhibitDatas(list);
		return dataGrid;
	}



	@Override
	@Transactional
	@LogService(description = "删除对应档案鉴定表")
	public void deleteDestroyHistroy(String id) throws ServiceException{
		FileIdentify fileIdentify = generalDao.findById(id,FileIdentify.class);
		fileIdentify.setDelFlag(1);
		generalDao.update(fileIdentify);
	}
	


	@Override
	public String addIdentifyContent(User user,String identifyId) throws ServiceException {
		FileIdentify fileIdentify = generalDao.findById(identifyId, FileIdentify.class);
		
		IdentifyContent identifyContent = new IdentifyContent();
		
		identifyContent.setDaid(fileIdentify.getDaid());
		identifyContent.setJdid(fileIdentify.getId());
		identifyContent.setJdnr("撤销");
		identifyContent.setJdsj(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		identifyContent.setJdr(user.getId());
		
		return generalDao.save(identifyContent);
		 
	}



	@Override
	public List<FileIdentify> getDestroyTables(QueryBuilder queryBuilder) {
		
		List<FileIdentify> list = generalDao.queryByCriteria(FileIdentify.class, queryBuilder, null,0, -1);
		
		return list;
	}
	
}
