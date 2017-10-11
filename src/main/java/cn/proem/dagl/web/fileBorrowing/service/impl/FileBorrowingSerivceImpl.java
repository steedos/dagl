package cn.proem.dagl.web.fileBorrowing.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.proem.core.annotation.LogService;
import cn.proem.core.dao.GeneralDao;
import cn.proem.core.model.DataGrid;
import cn.proem.core.model.QueryBuilder;
import cn.proem.dagl.web.fileBorrowing.entity.FileBorrowing;
import cn.proem.dagl.web.fileBorrowing.service.FileBorrowingService;
import cn.proem.suw.web.common.exception.ServiceException;

@Service
public class FileBorrowingSerivceImpl implements FileBorrowingService {
	
	@Autowired
	private GeneralDao generalDao;

	@Override
	public DataGrid<FileBorrowing> getFileBorrowingDataGrid(
			QueryBuilder queryBuilder, int nowPage, int pageSize) {
		
		DataGrid<FileBorrowing> dataGrid = new DataGrid<FileBorrowing>(nowPage,pageSize);
		dataGrid.setRecordCount(generalDao.countByCriteria(FileBorrowing.class, queryBuilder));
		List<FileBorrowing> list = generalDao.queryByCriteria(FileBorrowing.class, queryBuilder, null, dataGrid.getStartRecord(), dataGrid.getPageSize());
		dataGrid.setExhibitDatas(list);
		return dataGrid;
	}

	@Override
	@Transactional
	@LogService(description ="借阅归还")
	public void fileReturn(String id,String jyxg) throws ServiceException{
		FileBorrowing fileBorrowing = generalDao.findById(id, FileBorrowing.class);
		fileBorrowing.setJyzt("1");
		fileBorrowing.setJyxg(jyxg);
		fileBorrowing.setGhsj(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		generalDao.update(fileBorrowing);
		
	}
	
	@Override
	@Transactional
	@LogService(description ="删除借阅信息")
	public void deleteJyxx(String id) throws ServiceException{
		FileBorrowing fileBorrowing = generalDao.findById(id, FileBorrowing.class);
		fileBorrowing.setDelFlag(1);
		generalDao.update(fileBorrowing);
		
	}

	@Override
	@Transactional
	@LogService(description ="保存借阅记录")
	public void saveJyxx(FileBorrowing fb) throws ServiceException {
		generalDao.saveOrUpdate(fb);
		
	}
	
	
	
	

}
