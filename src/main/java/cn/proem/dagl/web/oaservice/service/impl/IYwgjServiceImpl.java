package cn.proem.dagl.web.oaservice.service.impl;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.proem.core.annotation.LogService;
import cn.proem.core.dao.GeneralDao;
import cn.proem.core.model.ConditionType;
import cn.proem.core.model.FieldType;
import cn.proem.core.model.Order;
import cn.proem.core.model.OrderType;
import cn.proem.core.model.QueryBuilder;
import cn.proem.core.model.QueryCondition;
import cn.proem.dagl.web.oaservice.entity.InfoYwgj;
import cn.proem.dagl.web.oaservice.entity.TYwgjObj;
import cn.proem.dagl.web.oaservice.entity.TYwgjQueryObj;
import cn.proem.dagl.web.oaservice.service.IYwgjService;
import cn.proem.suw.web.common.util.StringUtil;



@Service
public class IYwgjServiceImpl implements IYwgjService {
	@Autowired
	private GeneralDao generalDao;
	@Override
	public Integer addYwgjObj(TYwgjObj ywgjObj) {
		System.out.println(ywgjObj);
		TYwgjQueryObj ywgjQueryObj = new TYwgjQueryObj();
		ywgjQueryObj.setId(ywgjObj.getId());
		
		Integer size  = this.getYwgjObjListSize(ywgjQueryObj);
		if(ywgjObj.getId()==null || ywgjObj.getId().equals("") || size==0){
			ywgjObj.setXsxh(1);
		}
		else{
			Integer sxsh = this.getXsxhSize(ywgjObj);
			//后台自动生成显示序号xsxh
			ywgjObj.setXsxh(sxsh+1);
		}
		if (ywgjObj.getId()==null || ywgjObj.getId().equals("")){
			ywgjObj.setId(UUID.randomUUID().toString().substring(0,32));		
		}
		ywgjObj.setScrq(new Date());
		
		String id = generalDao.save(ywgjObj);
		InfoYwgj info = new InfoYwgj();
		info.setId(id);
		info.setDalx(ywgjObj.getDalx());
		info.setScrq(ywgjObj.getScrq());
		info.setScz(ywgjObj.getScz());
		info.setWjdx(ywgjObj.getWjdx());
		info.setWjdz(ywgjObj.getWjdz());
		info.setWjlx(ywgjObj.getWjlx());
		info.setWjm(ywgjObj.getWjm());
		info.setXsxh(ywgjObj.getXsxh());
		info.setZlsj(ywgjObj.getFilelink());
		generalDao.save(info);
		//插入数据返回数字？？
		return 0;
	}

//	@Override
//	public Integer getDhSize(TDhcxObj dhcxObj) {
//		QueryBuilder queryBuilder = new QueryBuilder();
//		queryBuilder.addCondition(new QueryCondition("dh",dhcxObj.getDh(),ConditionType.EQ,FieldType.INTEGER,null));
//		queryBuilder.addCondition(new QueryCondition("bm",dhcxObj.getBm(),ConditionType.EQ,FieldType.INTEGER,null));
//		return generalDao.countByCriteria(TDhcxObj.class, queryBuilder);
//	}

	@Override
	public Integer getYwgjObjListSize(TYwgjQueryObj Obj) {
//		QueryBuilder queryBuilder = new QueryBuilder();
		
		String sql = "SELECT COUNT(*) FROM T_YWGJ t WHERE 1=1 ";
		if(StringUtil.isNotEmpty(Obj.getId())) {
			sql += " AND t.id = '" + Obj.getId() + "'";
		}
		if(StringUtil.isNotEmpty(Obj.getXsxh())) {
			//mysql
//			sql += " AND t.xsxh LIKE concat('%'," + Obj.getXsxh() + ",'%')";
			sql += " AND t.xsxh LIKE '%'||" + Obj.getXsxh() + "||'%'";
		}
		if(StringUtil.isNotEmpty(Obj.getWjdz())) {
//			sql += " AND t.wjdz = LIKE concat('%'," + Obj.getWjdz() + ",'%')";
			sql += " AND t.wjdz = LIKE '%'||" + Obj.getWjdz() + "||'%'";
		}
		if(StringUtil.isNotEmpty(Obj.getScrq1())) {
			sql += " AND t.scrq >= " + Obj.getScrq1();
		}
		if(StringUtil.isNotEmpty(Obj.getScrq2())) {
			sql += " AND t.scrq <= " +Obj.getScrq2();
		}
		if(StringUtil.isNotEmpty(Obj.getScz())) {
//			sql += " AND t.scz = LIKE concat('%'," + Obj.getScz() + ",'%')";
			sql += " AND t.scz = LIKE '%'||" + Obj.getScz() + "||'%'";
		}
		if(StringUtil.isNotEmpty(Obj.getDalx())) {
//			sql += " AND t.dalx = LIKE concat('%'," + Obj.getDalx() + ",'%')";
			sql += " AND t.dalx = LIKE '%'||" + Obj.getDalx() + "||'%'";
		}
		if(StringUtil.isNotEmpty(Obj.getWjlx())) {
//			sql += " AND t.wjlx = LIKE concat('%'," + Obj.getWjlx() + ",'%')";
			sql += " AND t.wjlx = LIKE '%'||" + Obj.getWjlx() + "||'%'";
		}
		if(StringUtil.isNotEmpty(Obj.getWjdx())) {
//			sql += " AND t.wjdx = LIKE concat('%'," + Obj.getWjdx() + ",'%')";
			sql += " AND t.wjdx = LIKE '%'||" + Obj.getWjdx() + "||'%'";
		}
		Map<String,Object> param = new HashMap<String,Object>();
		return generalDao.getObject(sql, param).size();
		
	}
	@Override
	public Integer getXsxhSize(TYwgjObj ywgjObj) {
		QueryBuilder queryBuilder = new QueryBuilder();
		queryBuilder.addCondition(new QueryCondition("id",ywgjObj.getId(),ConditionType.EQ,FieldType.STRING,null));
		List<TYwgjObj> list = generalDao.queryByCriteria(TYwgjObj.class, queryBuilder, new Order[]{new Order("xsxh",OrderType.DESC)}, 0, -1);
		if(list != null && list.size()>0) {
			return list.get(0).getXsxh();
		}
		return 0;
	}
	

}
