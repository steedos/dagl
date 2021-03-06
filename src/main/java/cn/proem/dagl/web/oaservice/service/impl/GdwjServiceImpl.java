package cn.proem.dagl.web.oaservice.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.proem.core.annotation.LogService;
import cn.proem.core.dao.GeneralDao;
import cn.proem.core.model.ConditionType;
import cn.proem.core.model.QueryBuilder;
import cn.proem.core.model.QueryCondition;
import cn.proem.dagl.web.archives.service.CustomArchiveService;
import cn.proem.dagl.web.oaservice.entity.InfoGdwj;
import cn.proem.dagl.web.oaservice.entity.TGdwjObj;
import cn.proem.dagl.web.oaservice.service.GdwjService;
import cn.proem.dagl.web.oaservice.service.IYwgjService;
import cn.proem.dagl.web.oaservice.util.ConstUtil;
import cn.proem.dagl.web.table.entity.inf.BaseEntityInf;
import cn.proem.suw.web.common.exception.ServiceException;
@Service
public class GdwjServiceImpl implements GdwjService {
	@Resource
	private IYwgjService ywgjService;
	@Resource
	private GeneralDao generalDao;
	@Resource
	private CustomArchiveService customArchiveService;
//	private GdwjMapper mapper;
	@Override
	public Integer addGdwjObj(TGdwjObj gdwjObj,String id) {
		//调用判断档号是否存在方法
//        TDhcxObj dhcxObj = new TDhcxObj();
//		dhcxObj.setBm(gdwjObj.getBm());
//		dhcxObj.setDh(gdwjObj.getDh());
//		int rows = ywgjService.getDhSize(dhcxObj);
		QueryBuilder queryBuilder = new QueryBuilder();
		queryBuilder.addCondition(new QueryCondition("id",id,ConditionType.EQ,null,null));
		queryBuilder.addCondition(new QueryCondition("isArchive","0",ConditionType.EQ,null,null));
		Integer rows = generalDao.countByCriteria(InfoGdwj.class, queryBuilder);
		if (rows >= 1) {
			//存在重复的
			return ConstUtil.DUPLICATE_DH;
		}else{
//			
//			
			String uid = generalDao.save(gdwjObj);
//			info.setId(id);
			
			BaseEntityInf entity = customArchiveService.getEntity("INFO_GDWJ");
//            entity.set(DField.UUID, UUID.randomUUID().toString());
            entity.set("bgqx",gdwjObj.getBgqx());
            entity.set("bm", gdwjObj.getBm());
            entity.set("bz",gdwjObj.getBz());
            entity.set("cwrq",gdwjObj.getCwrq());
            entity.set("dagdm",gdwjObj.getDagdm());
            entity.set("dh",gdwjObj.getDh());
            entity.set("dzwdh",gdwjObj.getDzwdh());
            entity.set("flh",gdwjObj.getFlh());
            entity.set("formId",gdwjObj.getFormId());
            entity.set("gb",gdwjObj.getGb());
            entity.set("gdnd",gdwjObj.getGdnd());
            entity.set("jh",gdwjObj.getJh());
            entity.set("kzf",gdwjObj.getKzf());
            entity.set("lcfqr",gdwjObj.getLcfqr());
            entity.set("lcfqrszbm",gdwjObj.getLcfqrszbm());
            entity.set("mj",gdwjObj.getMj());
            entity.set("mlh",gdwjObj.getMlh());
            entity.set("ndh",gdwjObj.getNdh());
            entity.set("qwbs",gdwjObj.getQwbs());
            entity.set("qzh",gdwjObj.getQzh());
//            entity.set("",gdwjObj.getSfjd());
            entity.set("sfjd",gdwjObj.getSfjd());
            entity.set("sfygd",gdwjObj.getSfygd());
            entity.set("swh",gdwjObj.getSwh());
            entity.set("szyh",gdwjObj.getSzyh());
            entity.set("tm",gdwjObj.getTm());
            entity.set("wh",gdwjObj.getWh());
            entity.set("wjnr",gdwjObj.getWjnr());
            entity.set("wz",gdwjObj.getWz());
            entity.set("xbjg",gdwjObj.getXbjg());
            entity.set("ywurl",gdwjObj.getYwurl());
            entity.set("zbjg",gdwjObj.getZbjg());
            entity.set("zrz",gdwjObj.getZrz());
            entity.set("ztc",gdwjObj.getZtc());
            entity.set("ztdw",gdwjObj.getZtdw());
            entity.set("ztgg",gdwjObj.getZtgg());
            entity.set("ztlx",gdwjObj.getZtlx());
            entity.set("ztsl",gdwjObj.getZtsl());
            entity.set("zzjgdm",gdwjObj.getZzjgdm());
            entity.set("zzwtdh",gdwjObj.getZzwtdh());
            entity.set("delflag",0);
            entity.set("isArchive",0);
            entity.set("companyNum","40284c815c34747a015c349902d30007");
            entity.set("uuid",uid);
            entity.set("id", id);
            try {
				customArchiveService.saveOrUpdate(entity);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			generalDao.save(info);
			return 0;
		}
	}

}
