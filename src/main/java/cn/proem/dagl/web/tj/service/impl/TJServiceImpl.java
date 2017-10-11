package cn.proem.dagl.web.tj.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.proem.core.dao.GeneralDao;
import cn.proem.core.model.ConditionType;
import cn.proem.core.model.FieldType;
import cn.proem.core.model.Order;
import cn.proem.core.model.OrderType;
import cn.proem.core.model.QueryBuilder;
import cn.proem.core.model.QueryCondition;
import cn.proem.dagl.web.tj.dto.CheckDto;
import cn.proem.dagl.web.tj.dto.SeriesDto;
import cn.proem.dagl.web.tj.entity.GclOrder;
import cn.proem.dagl.web.tj.service.TJService;

@Service
public class TJServiceImpl implements TJService {
	@Resource
	private GeneralDao generalDao;

	/**
	 * @Method: tjWjGcl
	 * @Description: 不包括接受档案-文件级别
	 * @param company
	 * @return
	 * @see cn.proem.dagl.web.tj.service.TJService#tjWjGcl(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> tjWjGcl(String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select zwm as \"zwm\", ywm as \"ywm\", ndh as \"ndh\", count(1) as \"cnt\" from (");
		sql.append("  select t1.zwm, t1.ywm, t1.qzh, t1.ndh, t1.company, t1.oldcompany from v_pdagl_gcl_wj t1");
		// 自有档案
		sql.append("  where company = '" + company + "' and");
		sql.append("        (oldcompany = '" + company + "' or");
		sql.append("         oldcompany is null) ");
		sql.append("  UNION ALL");
		sql.append("  select t2.zwm, t2.ywm, t2.qzh, t2.ndh, t2.company, t2.oldcompany from v_pdagl_gcl_wj t2");
		// 移交档案
		sql.append("  where company != '" + company + "' and");
		sql.append("        oldcompany = '" + company + "' )");
		sql.append(" group by zwm, ywm, ndh");
		sql.append(" order by zwm, ywm, ndh");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	/**
	 * @Method: tjAjGcl
	 * @Description: 不包括接受档案-案卷级别
	 * @param company
	 * @return
	 * @see cn.proem.dagl.web.tj.service.TJService#tjAjGcl(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> tjAjGcl(String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select zwm as \"zwm\", ywm as \"ywm\", ndh as \"ndh\", count(1) as \"cnt\" from (");
		sql.append("  select t1.zwm, t1.ywm, t1.qzh, t1.ndh, t1.company, t1.oldcompany from v_pdagl_gcl_aj t1");
		// 自由档案
		sql.append("  where company = '" + company + "' and");
		sql.append("        (oldcompany = '" + company + "' or");
		sql.append("         oldcompany is null) ");
		sql.append("  UNION ALL");
		// 移交档案
		sql.append("  select t2.zwm, t2.ywm, t2.qzh, t2.ndh, t2.company, t2.oldcompany from v_pdagl_gcl_aj t2");
		sql.append("  where company != '" + company + "' and");
		sql.append("        oldcompany = '" + company + "' )");
		sql.append(" group by zwm, ywm, ndh");
		sql.append(" order by zwm, ndh");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	/**
	 * @Method: tjNdhArea
	 * @Description: 年度列表
	 * @param company
	 * @return
	 * @see cn.proem.dagl.web.tj.service.TJService#tjNdhArea(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> tjNdhArea(String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct ndh as \"ndh\"");
		sql.append(" from (select t1.zwm, t1.qzh, t1.ndh, t1.company, t1.oldcompany");
		sql.append(" from v_pdagl_gcl t1 where company = '" + company
				+ "' and (oldcompany = '" + company
				+ "' or oldcompany is null)");
		sql.append(" UNION ALL");
		sql.append(" select t2.zwm, t2.qzh, t2.ndh, t2.company, t2.oldcompany");
		sql.append(" from v_pdagl_gcl t2");
		sql.append(" where company != '" + company + "'");
		sql.append(" and oldcompany = '" + company + "')");
		sql.append(" order by ndh");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	/**
	 * @Method: tjWjGclA
	 * @Description: 包括接受档案-文件级别
	 * @param company
	 * @return
	 * @see cn.proem.dagl.web.tj.service.TJService#tjWjGclA(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> tjWjGclA(String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select zwm as \"zwm\", ywm as \"ywm\", ndh as \"ndh\", count(1) as \"cnt\" from (");
		sql.append("  select t1.zwm, t1.ywm, t1.qzh, t1.ndh, t1.company, t1.oldcompany from v_pdagl_gcl_wj t1");
		// 包括接受档案
		sql.append("  where company = '" + company + "'");
		sql.append("  UNION ALL");
		// 移交档案
		sql.append("  select t2.zwm, t2.ywm, t2.qzh, t2.ndh, t2.company, t2.oldcompany from v_pdagl_gcl_wj t2");
		sql.append("  where company != '" + company + "' and");
		sql.append("        oldcompany = '" + company + "' )");
		sql.append(" group by zwm, ywm, ndh");
		sql.append(" order by zwm, ywm ,ndh");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	/**
	 * @Method: tjAjGclA
	 * @Description: 包括接受档案-案卷级
	 * @param company
	 * @return
	 * @see cn.proem.dagl.web.tj.service.TJService#tjAjGclA(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> tjAjGclA(String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select zwm as \"zwm\", ywm as \"ywm\", ndh as \"ndh\", count(1) as \"cnt\" from (");
		sql.append("  select t1.zwm, t1.ywm, t1.qzh, t1.ndh, t1.company, t1.oldcompany from v_pdagl_gcl_aj t1");
		// 包括接受档案
		sql.append("  where company = '" + company + "' ");
		sql.append("  UNION ALL");
		// 移交档案
		sql.append("  select t2.zwm, t2.ywm, t2.qzh, t2.ndh, t2.company, t2.oldcompany from v_pdagl_gcl_aj t2");
		sql.append("  where company != '" + company + "' and");
		sql.append("        oldcompany = '" + company + "' )");
		sql.append(" group by zwm, ywm, ndh");
		sql.append(" order by zwm, ywm, ndh");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	/**
	 * @Method: tjNdhAreaA
	 * @Description: 年度列表包括接受档案
	 * @param company
	 * @return
	 * @see cn.proem.dagl.web.tj.service.TJService#tjNdhAreaA(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> tjNdhAreaA(String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct ndh as \"ndh\"");
		sql.append(" from (select t1.zwm, t1.qzh, t1.ndh, t1.company, t1.oldcompany");
		sql.append(" from v_pdagl_gcl t1 where company = '" + company + "'");
		sql.append(" UNION ALL");
		sql.append(" select t2.zwm, t2.qzh, t2.ndh, t2.company, t2.oldcompany");
		sql.append(" from v_pdagl_gcl t2");
		sql.append(" where company != '" + company + "'");
		sql.append(" and oldcompany = '" + company + "')");
		sql.append(" order by ndh");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	@Transactional
	public void tjGclSaveOrUpdate(List<String> orders, String type,
			String company) {
		String sql = "delete from pdagl_gcl_order where company= '" + company
				+ "' and type='" + type + "'";
		generalDao.executeSQL(sql);
		for (int i = 0; i < orders.size(); i++) {
			GclOrder ord = new GclOrder();
			ord.setCompany(company);
			ord.setType(type);
			ord.setYwm(orders.get(i));
			ord.setOrder(i);
			generalDao.save(ord);
		}
	}

	@Override
	public List<GclOrder> tjGclFind(String type, String company) {
		QueryBuilder queryBuilder = new QueryBuilder();
		Order[] orders = new Order[1];
		orders[0] = new Order("ord", OrderType.ASC);
		queryBuilder.addCondition(new QueryCondition("company", company,
				ConditionType.EQ, FieldType.STRING, null));
		queryBuilder.addCondition(new QueryCondition("type", type,
				ConditionType.EQ, FieldType.STRING, null));
		return generalDao.queryByCriteria(GclOrder.class, queryBuilder, orders,
				0, -1);
	}

	@Override
	public List<CheckDto> designMl(String company) {
		// select distinct t.ndh from v_pdagl_record t order by ndh;
		// select distinct t.mj from v_pdagl_record t order by mj;
		// select distinct t.bgqx from v_pdagl_record t order by bgqx;
		// select distinct t.zwm from v_pdagl_record t order by zwm;
		String sql = "select distinct zwm as \"zwm\", ywm as \"ywm\" from v_pdagl_record where isarchive >= '3' and delflag = '0' and company = '"
				+ company + "' order by zwm";
		List<CheckDto> result = new ArrayList<CheckDto>();
		List<Map<String, Object>> list = generalDao.getObjectList(sql, 0, -1);
		for (Map<String, Object> ele : list) {
			CheckDto ml = new CheckDto();
			ml.setLx((String) ele.get("ywm"));
			ml.setLxm((String) ele.get("zwm"));
			result.add(ml);
		}
		return result;
	}

	@Override
	public List<CheckDto> designMj(String company) {
		String sql = "select distinct t1.mj as \"mj\", nvl(t2.DVALUE, t1.mj) as \"mjm\" from v_pdagl_record t1 right join v_pdagl_zd t2 on t2.DNO = 'mj' and t2.DVNO = t1.mj where t1.isarchive >= '3' and t1.delflag = '0' and t1.company = '"
				+ company + "' order by mj";
		List<Map<String, Object>> list = generalDao.getObjectList(sql, 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ele : list) {
			CheckDto mj = new CheckDto();
			mj.setLx((String) ele.get("mj"));
			mj.setLxm((String) ele.get("mjm"));
			result.add(mj);
		}
		return result;
	}

	@Override
	public List<CheckDto> designBgqx(String company) {
		String sql = "select dvno as \"bgqx\", dvalue as \"bgqxm\" from v_pdagl_zd where DNO = 'bgqx' order by dvalue";
		List<CheckDto> result = new ArrayList<CheckDto>();
		List<Map<String, Object>> list = generalDao.getObjectList(sql, 0, -1);
		for (Map<String, Object> ele : list) {
			CheckDto bgqx = new CheckDto();
			bgqx.setLx((String) ele.get("bgqx"));
			bgqx.setLxm((String) ele.get("bgqxm"));
			result.add(bgqx);
		}
		return result;
	}

	@Override
	public List<String> designNd(String company) {
		String sql = "select distinct ndh as \"ndh\" from v_pdagl_record where isarchive >= '3' and delflag = '0' and company = '"
				+ company + "' order by ndh";
		List<Map<String, Object>> list = generalDao.getObjectList(sql, 0, -1);
		List<String> result = new ArrayList<String>();
		for (Map<String, Object> ele : list) {
			result.add((String) ele.get("ndh"));
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> designDalxReport(String from, String to,
			String company, List<String> mls, List<String> bgqxs,
			List<String> mjs) {
		StringBuilder sql = new StringBuilder();
		sql.append("select a.zwm as \"lxm\", a.ywm  as \"lx\", a.bgqx as \"bgqx\", a.mj as \"mj\", a.cnt as \"cnt\", b.total as \"total\", round(a.cnt/b.total,4)*100 || '%' as \"pre\" from ");
		sql.append("(select zwm, ywm, bgqx, mj, count(1) as cnt from v_pdagl_record where ");
		// 公司
		sql.append(" company = '" + company
				+ "'  and isarchive >= '3' and delflag = '0' and ");
		// 年度
		sql.append("ndh >= '" + from + "' and ndh <= '" + to + "' ");
		// 门类
		if (!mls.get(0).equals("ml_all")) {
			sql.append(" and ywm in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}
		// 保管期限
		if (!bgqxs.get(0).equals("bgqx_all")) {
			sql.append(" and bgqx in (");
			sql.append("'" + bgqxs.get(0) + "'");
			for (int i = 1; i < bgqxs.size(); i++) {
				sql.append(", '" + bgqxs.get(i) + "'");
			}
			sql.append(") ");
		}
		// 密级
		if (!mjs.get(0).equals("mj_all")) {
			sql.append(" and mj in (");
			sql.append("'" + mjs.get(0) + "'");
			for (int i = 1; i < mjs.size(); i++) {
				sql.append(", '" + mjs.get(i) + "'");
			}
			sql.append(") ");
		}
		sql.append(" group by zwm, ywm, bgqx, mj");
		sql.append(" order by zwm, ywm, bgqx, mj) a, ");
		sql.append(" (select count(*) as total from v_pdagl_record where ");
		// 公司
		sql.append(" company = '" + company
				+ "'  and isarchive >= '3' and delflag = '0' and ");
		// 年度
		sql.append("ndh >= '" + from + "' and ndh <= '" + to + "' ");
		// 门类
		if (!mls.get(0).equals("ml_all")) {
			sql.append(" and ywm in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}
		// 保管期限
		if (!bgqxs.get(0).equals("bgqx_all")) {
			sql.append(" and bgqx in (");
			sql.append("'" + bgqxs.get(0) + "'");
			for (int i = 1; i < bgqxs.size(); i++) {
				sql.append(", '" + bgqxs.get(i) + "'");
			}
			sql.append(") ");
		}
		// 密级
		if (!mjs.get(0).equals("mj_all")) {
			sql.append(" and mj in (");
			sql.append("'" + mjs.get(0) + "'");
			for (int i = 1; i < mjs.size(); i++) {
				sql.append(", '" + mjs.get(i) + "'");
			}
			sql.append(") ");
		}
		sql.append(") b");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<SeriesDto> designDalxPic(String from, String to,
			String company, List<String> mls, List<String> bgqxs,
			List<String> mjs) {
		StringBuilder sql = new StringBuilder();
		sql.append("select mj as \"mj\", bgqx as \"bgqx\", zwm as \"lx\", nvl((select t1.cnt from ");
		sql.append("(select zwm, bgqx, mj, count(1) as cnt from v_pdagl_record where ");
		// 公司
		sql.append(" company = '" + company
				+ "'  and isarchive >= '3' and delflag = '0' and ");
		// 年度
		sql.append("ndh >= '" + from + "' and ndh <= '" + to + "' ");
		// 门类
		if (!mls.get(0).equals("ml_all")) {
			sql.append(" and ywm in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}
		// 保管期限
		if (!bgqxs.get(0).equals("bgqx_all")) {
			sql.append(" and bgqx in (");
			sql.append("'" + bgqxs.get(0) + "'");
			for (int i = 1; i < bgqxs.size(); i++) {
				sql.append(", '" + bgqxs.get(i) + "'");
			}
			sql.append(")");
		}
		// 密级
		if (!mjs.get(0).equals("mj_all")) {
			sql.append(" and mj in (");
			sql.append("'" + mjs.get(0) + "'");
			for (int i = 1; i < mjs.size(); i++) {
				sql.append(", '" + mjs.get(i) + "'");
			}
			sql.append(") ");
		}
		sql.append(" group by zwm, ywm, bgqx, mj");
		sql.append(" order by zwm, ywm, bgqx, mj) t1 where t1.zwm = t2.zwm and t1.mj = t2.mj and t1.bgqx = t2.bgqx), 0) as \"cnt\" ");
		sql.append(" from (select zwm, mj, bgqx ");
		sql.append(" from (select distinct zwm from v_pdagl_record where isarchive >= '3' and delflag = '0' and company = '"
				+ company + "'),");
		sql.append("      (select distinct mj from v_pdagl_record  where isarchive >= '3' and delflag = '0' and company = '"
				+ company + "'),");
		sql.append("      (select distinct bgqx from v_pdagl_record where isarchive >= '3' and delflag = '0' and company = '"
				+ company + "')) t2 order by mj, bgqx, zwm");
		List<Map<String, Object>> datas = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<String> lx = new ArrayList<String>();
		List<Object> das = new ArrayList<Object>();
		List<SeriesDto> seriesDtoList = new ArrayList<SeriesDto>();
		SeriesDto series = new SeriesDto();
		for (int i = 0; i < datas.size(); i++) {
			if (!lx.contains((String) datas.get(i).get("lx"))) {
				lx.add((String) datas.get(i).get("lx"));
				das.add(datas.get(i).get("cnt"));
			} else if (i == (datas.size() - 1)) {
				series.setType("bar");
				series.setName((String) datas.get(i).get("mj"));
				series.setStack((String) datas.get(i).get("bgqx"));
				das.add(datas.get(i).get("cnt"));
				series.setData(das);
				seriesDtoList.add(series);
			} else {
				series.setData(das);
				seriesDtoList.add(series);
				series.setType("bar");
				series.setName((String) datas.get(i - 1).get("mj"));
				series.setStack((String) datas.get(i - 1).get("bgqx"));
				lx = new ArrayList<String>();
				das = new ArrayList<Object>();
				series = new SeriesDto();
				i = i - 1;
			}
		}

		return seriesDtoList;
	}

	/**
	 * @Method: designHKMl
	 * @Description: 划空类型
	 * @param company
	 * @return
	 * @see cn.proem.dagl.web.tj.service.TJService#designHKMl(java.lang.String)
	 */
	@Override
	public List<CheckDto> designHKMl(String company) {
		String sql = "SELECT DISTINCT YWM as \"ywm\", ZWM as \"zwm\"  FROM V_PDAGL_HK WHERE company = '"
				+ company + "' order by zwm";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	/**
	 * @Method: designHKNd
	 * @Description: 划空年度
	 * @param company
	 * @return
	 * @see cn.proem.dagl.web.tj.service.TJService#designHKNd(java.lang.String)
	 */
	@Override
	public List<CheckDto> designHKNd(String company) {
		String sql = "SELECT DISTINCT NDH as \"ndh\" FROM V_PDAGL_HK WHERE company = '"
				+ company + "' order by ndh";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ndh"));
			check.setLxm((String) ml.get("ndh"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> tjHK(String from, String to,
			List<String> ywm, String hk, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ywm as \"ml\", zwm as \"mlm\", sum(cnt) as \"cnt\", hk as \"hk\" from v_pdagl_hk where company ='");
		sql.append(company);
		sql.append("'");
		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" ndh >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" ndh <= '");
			sql.append(to);
			sql.append("'");
		}

		// 密级
		if (!ywm.get(0).equals("ml_all")) {
			sql.append(" and ywm in (");
			sql.append("'" + ywm.get(0) + "'");
			for (int i = 1; i < ywm.size(); i++) {
				sql.append(", '" + ywm.get(i) + "'");
			}
			sql.append(") ");
		}

		if (hk != null) {
			sql.append(" and ");
			sql.append(" hk = '");
			sql.append(hk);
			sql.append("'");
		}
		sql.append(" group by ywm, zwm, hk");
		sql.append(" order by zwm");

		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<CheckDto> designJDMl(String company) {
		String sql = "SELECT DISTINCT bm as \"ywm\", ZWM as \"zwm\"  FROM v_pdagl_jd WHERE company = '"
				+ company + "' order by zwm";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designJDNd(String company) {
		String sql = "SELECT DISTINCT jdsj as \"ywm\", jdsj as \"zwm\"  FROM v_pdagl_jd WHERE company = '"
				+ company + "' order by jdsj";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> tjJD(String from, String to,
			List<String> ywm, String hk, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select bm as \"ml\", zwm as \"mlm\", jdnr as \"hk\", sum(cnt) as \"cnt\" from v_pdagl_jd where company ='");
		sql.append(company);
		sql.append("'");
		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" jdsj >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" jdsj <= '");
			sql.append(to);
			sql.append("'");
		}

		// 类型
		if (!ywm.get(0).equals("ml_all")) {
			sql.append(" and bm in (");
			sql.append("'" + ywm.get(0) + "'");
			for (int i = 1; i < ywm.size(); i++) {
				sql.append(", '" + ywm.get(i) + "'");
			}
			sql.append(") ");
		}

		// 鉴定内容
		if (hk != null) {
			sql.append(" and ");
			sql.append(" jdnr = '");
			sql.append(hk);
			sql.append("'");
		}
		sql.append(" group by zwm, bm, jdnr");
		sql.append(" order by zwm, jdnr");

		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<CheckDto> designJDNr(String company) {
		String sql = "SELECT DISTINCT jdnr as \"ywm\", jdnr as \"zwm\"  FROM v_pdagl_jd WHERE company = '"
				+ company + "' order by jdnr";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDAMl(String company) {
		String sql = "SELECT DISTINCT YWM as \"ywm\", ZWM as \"zwm\"  FROM V_PDAGL_DA WHERE company = '"
				+ company + "' order by zwm";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDAMj(String company) {
		String sql = "SELECT DISTINCT dvno as \"ywm\", dvalue as \"zwm\"  FROM v_pdagl_zd where DNO = 'mj' order by dvno";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDANd(String company) {
		String sql = "SELECT DISTINCT ndh as \"ywm\", ndh as \"zwm\"  FROM V_PDAGL_DA WHERE company = '"
				+ company + "' order by ndh";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDABgqx(String company) {
		String sql = "select dno as \"ywm\", dvalue as \"zwm\" from v_pdagl_zd where dno = 'bgqx' order by dvalue";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> tjDa(String from, String to,
			List<String> mls, List<String> mjs, List<String> bgqxs,
			String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select company as \"company\"");
		if (bgqxs != null) {
			sql.append(", bgqx as \"bgqx\"");
		}
		if (mjs != null) {
			sql.append(", mj as \"mj\"");
		}
		sql.append(", sum(cnt) as \"cnt\" from v_pdagl_da where company = '");
		sql.append(company);
		sql.append("'");
		// 门类
		if (mls != null && !mls.get(0).equals("ml_all")) {
			sql.append(" and ywm in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}

		// 保管期限
		if (bgqxs != null && !bgqxs.get(0).equals("bgqx_all")) {
			sql.append(" and bgqx in (");
			sql.append("'" + bgqxs.get(0) + "'");
			for (int i = 1; i < bgqxs.size(); i++) {
				sql.append(", '" + bgqxs.get(i) + "'");
			}
			sql.append(") ");
		}

		// 密级
		if (mjs != null && !mjs.get(0).equals("mj_all")) {
			sql.append(" and mj in (");
			sql.append("'" + mjs.get(0) + "'");
			for (int i = 1; i < mjs.size(); i++) {
				sql.append(", '" + mjs.get(i) + "'");
			}
			sql.append(") ");
		}

		sql.append(" group by company");

		if (bgqxs != null) {
			sql.append(", bgqx");
		}
		if (mjs != null) {
			sql.append(", mj");
		}
		sql.append(" order by company");

		if (bgqxs != null) {
			sql.append(", bgqx");
		}
		if (mjs != null) {
			sql.append(", mj");
		}
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<Map<String, Object>> tjDaMl(String from, String to,
			List<String> mls, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select zwm as \"ml\", sum(cnt) as \"cnt\" from v_pdagl_da");
		sql.append(" where company = '");
		sql.append(company);
		sql.append("'");
		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" ndh >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" ndh <= '");
			sql.append(to);
			sql.append("'");
		}
		// 门类
		if (mls != null && !mls.get(0).equals("ml_all")) {
			sql.append(" and ywm in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}

		sql.append(" group by zwm");
		sql.append(" order by zwm");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<Map<String, Object>> tjDaNd(String from, String to,
			List<String> mls, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ndh as \"nd\", sum(cnt) as \"cnt\" from v_pdagl_da");
		sql.append(" where company = '");
		sql.append(company);
		sql.append("'");
		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" ndh >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" ndh <= '");
			sql.append(to);
			sql.append("'");
		}
		// 门类
		if (mls != null && !mls.get(0).equals("ml_all")) {
			sql.append(" and ywm in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}

		sql.append(" group by ndh");
		sql.append(" order by ndh");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<CheckDto> designJYMl(String company) {
		return null;
	}

	@Override
	public List<CheckDto> designJYjyxg(String company) {
		String sql = "SELECT DISTINCT jyxg as \"ywm\", jyxg as \"zwm\"  FROM v_pdagl_stjy WHERE company = '"
				+ company + "' order by jyxg";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designJYjymd(String company) {
		String sql = "SELECT DISTINCT jymd as \"ywm\", jymd as \"zwm\"  FROM v_pdagl_stjy WHERE company = '"
				+ company + "' order by jymd";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> tjJymd(String from, String to,
			List<String> jymd, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select jymd as \"tj1\" , sum(cnt) as \"cnt\"");
		sql.append(" from V_PDAGL_STJY");
		sql.append(" where company='");
		sql.append(company);
		sql.append("'");
		// 鉴定时间
		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" jysj >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" jysj <= '");
			sql.append(to);
			sql.append("'");
		}
		// 借阅目的
		if (jymd != null && !jymd.get(0).equals("jymd_all")) {
			sql.append(" and jymd in (");
			sql.append("'" + jymd.get(0) + "'");
			for (int i = 1; i < jymd.size(); i++) {
				sql.append(", '" + jymd.get(i) + "'");
			}
			sql.append(") ");
		}
		sql.append(" group by jymd ");
		sql.append(" order by jymd ");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<Map<String, Object>> tjJyxg(String from, String to,
			List<String> jyxg, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select jyxg as \"tj1\" , sum(cnt) as \"cnt\"");
		sql.append(" from V_PDAGL_STJY");
		sql.append(" where company='");
		sql.append(company);
		sql.append("'");
		// 鉴定时间
		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" jysj >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" jysj <= '");
			sql.append(to);
			sql.append("'");
		}
		// 借阅目的
		if (jyxg != null && !jyxg.get(0).equals("jyxg_all")) {
			sql.append(" and jyxg in (");
			sql.append("'" + jyxg.get(0) + "'");
			for (int i = 1; i < jyxg.size(); i++) {
				sql.append(", '" + jyxg.get(i) + "'");
			}
			sql.append(") ");
		}
		sql.append(" group by jyxg ");
		sql.append(" order by jyxg ");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<CheckDto> designJYjysj(String company) {
		String sql = "SELECT DISTINCT jysj as \"ywm\", jysj as \"zwm\"  FROM v_pdagl_stjy WHERE company = '"
				+ company + "' order by jysj";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDZJYMl(String company) {
		String sql = "SELECT DISTINCT ml as \"ywm\", ml as \"zwm\"  FROM V_PDAGL_DZJY WHERE company = '"
				+ company + "' order by ml";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDZJYjyxg(String company) {
		String sql = "SELECT DISTINCT jyxg as \"ywm\", jyxg as \"zwm\"  FROM V_PDAGL_DZJY WHERE company = '"
				+ company + "' order by jyxg";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDZJYjymd(String company) {
		String sql = "SELECT DISTINCT jymd as \"ywm\", jymd as \"zwm\"  FROM V_PDAGL_DZJY WHERE company = '"
				+ company + "' order by jymd";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDZJYjysj(String company) {
		String sql = "SELECT DISTINCT jysj as \"ywm\", jysj as \"zwm\"  FROM V_PDAGL_DZJY WHERE company = '"
				+ company + "' order by jysj";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> tjDZJymd(String from, String to,
			List<String> ml, List<String> jymd, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select jymd as \"tj1\" , count(1) as \"cnt\"");
		sql.append(" from V_PDAGL_DZJY");
		sql.append(" where company='");
		sql.append(company);
		sql.append("'");
		// 鉴定时间
		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" jysj >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" jysj <= '");
			sql.append(to);
			sql.append("'");
		}
		// 门类
		if (ml != null && !ml.get(0).equals("ml_all")) {
			sql.append(" and ml in (");
			sql.append("'" + ml.get(0) + "'");
			for (int i = 1; i < ml.size(); i++) {
				sql.append(", '" + ml.get(i) + "'");
			}
			sql.append(") ");
		}
		// 借阅目的
		if (jymd != null && !jymd.get(0).equals("jymd_all")) {
			sql.append(" and jymd in (");
			sql.append("'" + jymd.get(0) + "'");
			for (int i = 1; i < jymd.size(); i++) {
				sql.append(", '" + jymd.get(i) + "'");
			}
			sql.append(") ");
		}
		sql.append(" group by jymd ");
		sql.append(" order by jymd ");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<Map<String, Object>> tjDZJyxg(String from, String to,
			List<String> ml, List<String> jyxg, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select jyxg as \"tj1\" , count(1) as \"cnt\"");
		sql.append(" from V_PDAGL_DZJY");
		sql.append(" where company='");
		sql.append(company);
		sql.append("'");
		// 鉴定时间
		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" jysj >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" jysj <= '");
			sql.append(to);
			sql.append("'");
		}
		// 门类
		if (ml != null && !ml.get(0).equals("ml_all")) {
			sql.append(" and ml in (");
			sql.append("'" + ml.get(0) + "'");
			for (int i = 1; i < ml.size(); i++) {
				sql.append(", '" + ml.get(i) + "'");
			}
			sql.append(") ");
		}
		// 借阅目的
		if (jyxg != null && !jyxg.get(0).equals("jyxg_all")) {
			sql.append(" and jyxg in (");
			sql.append("'" + jyxg.get(0) + "'");
			for (int i = 1; i < jyxg.size(); i++) {
				sql.append(", '" + jyxg.get(i) + "'");
			}
			sql.append(") ");
		}
		sql.append(" group by jyxg ");
		sql.append(" order by jyxg ");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<CheckDto> designDZFJMl(String company) {
		String sql = "SELECT DISTINCT ml as \"ywm\", ml as \"zwm\"  FROM V_PDAGL_DZFJ WHERE company = '"
				+ company + "' order by ml";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDZFJNd(String company) {

		String sql = "SELECT DISTINCT NDH as \"ndh\" FROM V_PDAGL_DZFJ WHERE company = '"
				+ company + "' order by NDH";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ndh"));
			check.setLxm((String) ml.get("ndh"));
			result.add(check);
		}
		return result;

	}

	@Override
	public List<Map<String, Object>> tjDzfj(String from, String to,
			List<String> mls, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ml as \"ml\", ml as \"mlm\", count(1) as \"cnt\" from V_PDAGL_DZFJ where company ='");
		sql.append(company);
		sql.append("'");

		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" ndh >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" ndh <= '");
			sql.append(to);
			sql.append("'");
		}
		// 门类
		if (!mls.get(0).equals("ml_all")) {
			sql.append(" and ml in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}

		sql.append(" group by ml ");
		sql.append(" order by ml");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<CheckDto> designGJFJMl(String company) {
		String sql = "SELECT DISTINCT ml as \"ywm\", ml as \"zwm\"  FROM V_PDAGL_GJFJ WHERE company = '"
				+ company + "' order by ml";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designGJFJNd(String company) {

		String sql = "SELECT DISTINCT NDH as \"ndh\" FROM V_PDAGL_GJFJ WHERE company = '"
				+ company + "' order by NDH";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ndh"));
			check.setLxm((String) ml.get("ndh"));
			result.add(check);
		}
		return result;

	}

	@Override
	public List<Map<String, Object>> tjGJFJ(String from, String to,
			List<String> mls, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ml as \"ml\", ml as \"mlm\", count(1) as \"cnt\" from V_PDAGL_GJFJ where company ='");
		sql.append(company);
		sql.append("'");

		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" ndh >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" ndh <= '");
			sql.append(to);
			sql.append("'");
		}
		// 门类
		if (!mls.get(0).equals("ml_all")) {
			sql.append(" and ml in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}

		sql.append(" group by ml ");
		sql.append(" order by ml");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<CheckDto> designDZWJMl(String company) {
		// String sql =
		// "SELECT DISTINCT ml as \"ywm\", ml as \"zwm\"  FROM V_PDAGL_GJFJ WHERE company = '"
		// + company +"' order by ml";
		String sql = "SELECT DISTINCT ml as \"ywm\", ml as \"zwm\"  FROM V_PDAGL_DZWJ WHERE company = '"
				+ company + "' order by ml";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ywm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<CheckDto> designDZWJNd(String company) {

		// String sql =
		// "SELECT DISTINCT NDH as \"ndh\" FROM V_PDAGL_GJFJ WHERE company = '"
		// + company +"' order by NDH";
		String sql = "SELECT DISTINCT NDH as \"ndh\" FROM V_PDAGL_DZWJ WHERE company = '"
				+ company + "' order by NDH";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("ndh"));
			check.setLxm((String) ml.get("ndh"));
			result.add(check);
		}
		return result;

	}

	@Override
	public List<Map<String, Object>> tjDZWJ(String from, String to,
			List<String> mls, String company) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ml as \"ml\", ml as \"mlm\", count(1) as \"cnt\" from V_PDAGL_DZWJ where company ='");
		sql.append(company);
		sql.append("'");

		if (from != null && to != null) {
			sql.append(" and ");
			sql.append(" ndh >= '");
			sql.append(from);
			sql.append("' and ");
			sql.append(" ndh <= '");
			sql.append(to);
			sql.append("'");
		}
		// 门类
		if (!mls.get(0).equals("ml_all")) {
			sql.append(" and ml in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}

		sql.append(" group by ml ");
		sql.append(" order by ml");
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}

	@Override
	public List<CheckDto> designEEPMl(String company) {
		String sql = "SELECT T.DVNO as \"ywm\", T.DVALUE as \"zwm\" FROM v_pdagl_zd T WHERE T.DNO = 'DALX'";
		List<Map<String, Object>> mls = generalDao.getObjectList(
				sql.toString(), 0, -1);
		List<CheckDto> result = new ArrayList<CheckDto>();
		for (Map<String, Object> ml : mls) {
			CheckDto check = new CheckDto();
			check.setLx((String) ml.get("zwm"));
			check.setLxm((String) ml.get("zwm"));
			result.add(check);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> tjEEP(List<String> mls, String company,
			String state) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ZWM as \"mlm\", STATE as \"fb\", CNT as \"cnt\" from V_PDAGL_EEP WHERE company = '"
				+ company + "' ");
		// 门类
		if (!mls.get(0).equals("ml_all")) {
			sql.append(" and zwm in (");
			sql.append("'" + mls.get(0) + "'");
			for (int i = 1; i < mls.size(); i++) {
				sql.append(", '" + mls.get(i) + "'");
			}
			sql.append(") ");
		}
		// 是否封包
		if (state != null && !state.equals("fb_all")) {
			sql.append(" and state = '" + state + "'");
		}
		return generalDao.getObjectList(sql.toString(), 0, -1);
	}
}
