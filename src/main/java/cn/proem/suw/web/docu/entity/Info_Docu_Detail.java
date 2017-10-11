package cn.proem.suw.web.docu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import cn.proem.dagl.web.table.entity.inf.BaseEntityInf;

/**
 * @ClassName Docu
 * @Description 文档表
 * @author Pan Jilong
 * @date 2017年3月29日
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "INFO_DOCU_DETAIL")
public class Info_Docu_Detail implements BaseEntityInf{

	/**
	 * ID
	 */
	@Id
	@Column(name = "uuid", length = 40)
	private String uuid;

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Column(name = "delflag", length = 40)
	private String delFlag = "0";
	
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	/**
	 * 档号
	 */
	@Column
	private String dh;
	/**
	 * 全宗号
	 */
	@Column
	private String qzh;
	/**
	 * 目录号
	 */
	@Column
	private String mlh;
	/**
	 * 所在页号
	 */
	@Column
	private String szyh;
	/**
	 * 分类号
	 */
	@Column
	private String flh;
	/**
	 * 档案馆代码
	 */
	@Column
	private String dagdm;
	/**
	 * 件号
	 */
	@Column
	private String jh;
	/**
	 * 归档年度
	 */
	@Column(nullable=false)
	private String gdnd;
	/**
	 * 电子文档号
	 */
	@Column
	private String dzwdh;
	/**
	 * 缩微号
	 */
	@Column
	private String swh;
	/**
	 * 题名
	 */
	@Column(nullable=false)
	private String tm;
	/**
	 * 成文日期
	 */
	@Column(nullable=false)
	private String cwrq;
	/**
	 * 文号
	 */
	@Column
	private String wh;
	/**
	 * 责任者
	 */
	@Column(nullable=false)
	private String zrz;
	/**
	 * 稿本
	 */
	@Column
	private String gb;
	/**
	 * 文种
	 */
	@Column
	private String wz;
	/**
	 * 密级
	 */
	@Column
	private String mj;
	/**
	 * 保管期限
	 */
	@Column
	private String bgqx;
	/**
	 * 载体规格
	 */
	@Column
	private String ztgg;
	/**
	 * 载体类型
	 */
	@Column
	private String ztlx;
	/**
	 * 载体数量
	 */
	@Column
	private String ztsl;
	/**
	 * 载体单位
	 */
	@Column
	private String ztdw;
	/**
	 * 主题词
	 */
	@Column
	private String ztc;
	/**
	 * 全文标识
	 */
	@Column
	private String qwbs;
	/**
	 * 相关部门
	 */
	@Column(nullable=false)
	private String xgjg;
	/**
	 * 业务档案类型
	 */
	@Column(nullable=false)
	private String xbjg;
	/**
	 * 控制符
	 */
	@Column
	private String kzf;
	/**
	 * 备注
	 */
	@Column
	private String bz;
	/**
	 * 流程发起主办部门
	 */
	@Column
	private String lcfqrszbm;
	/**
	 * 流程发起人
	 */
	@Column
	private String lcfqr;
	/**
	 * 文件内容
	 */
	@Column
	private String wjnr;
	
	@Column(name = "isarchive")
	private String isArchive;

	public String getIsArchive() {
		return isArchive;
	}
	public void setIsArchive(String isArchive) {
		this.isArchive = isArchive;
	}

	/**
	 * 所属公司
	 */
	@Column(name = "companynum")
	private String companyNum;
	/**
	 * 以往所属公司
	 */
	@Column(name = "evercompanynum")
	private String everCompanyNum;
	public String getCompanyNum() {
		return companyNum;
	}
	public void setCompanyNum(String companyNum) {
		this.companyNum = companyNum;
	}
	public String getEverCompanyNum() {
		return everCompanyNum;
	}
	public void setEverCompanyNum(String everCompanyNum) {
		this.everCompanyNum = everCompanyNum;
	}
	
	public String getDh() {
		return dh;
	}
	public void setDh(String dh) {
		this.dh = dh;
	}
	public String getQzh() {
		return qzh;
	}
	public void setQzh(String qzh) {
		this.qzh = qzh;
	}
	public String getMlh() {
		return mlh;
	}
	public void setMlh(String mlh) {
		this.mlh = mlh;
	}
	public String getSzyh() {
		return szyh;
	}
	public void setSzyh(String szyh) {
		this.szyh = szyh;
	}
	public String getFlh() {
		return flh;
	}
	public void setFlh(String flh) {
		this.flh = flh;
	}
	public String getDagdm() {
		return dagdm;
	}
	public void setDagdm(String dagdm) {
		this.dagdm = dagdm;
	}
	public String getJh() {
		return jh;
	}
	public void setJh(String jh) {
		this.jh = jh;
	}
	public String getGdnd() {
		return gdnd;
	}
	public void setGdnd(String gdnd) {
		this.gdnd = gdnd;
	}
	public String getDzwdh() {
		return dzwdh;
	}
	public void setDzwdh(String dzwdh) {
		this.dzwdh = dzwdh;
	}
	public String getSwh() {
		return swh;
	}
	public void setSwh(String swh) {
		this.swh = swh;
	}
	public String getTm() {
		return tm;
	}
	public void setTm(String tm) {
		this.tm = tm;
	}
	public String getCwrq() {
		return cwrq;
	}
	public void setCwrq(String cwrq) {
		this.cwrq = cwrq;
	}
	public String getWh() {
		return wh;
	}
	public void setWh(String wh) {
		this.wh = wh;
	}
	public String getZrz() {
		return zrz;
	}
	public void setZrz(String zrz) {
		this.zrz = zrz;
	}
	public String getGb() {
		return gb;
	}
	public void setGb(String gb) {
		this.gb = gb;
	}
	public String getWz() {
		return wz;
	}
	public void setWz(String wz) {
		this.wz = wz;
	}
	public String getMj() {
		return mj;
	}
	public void setMj(String mj) {
		this.mj = mj;
	}
	public String getBgqx() {
		return bgqx;
	}
	public void setBgqx(String bgqx) {
		this.bgqx = bgqx;
	}
	public String getZtgg() {
		return ztgg;
	}
	public void setZtgg(String ztgg) {
		this.ztgg = ztgg;
	}
	public String getZtlx() {
		return ztlx;
	}
	public void setZtlx(String ztlx) {
		this.ztlx = ztlx;
	}
	public String getZtsl() {
		return ztsl;
	}
	public void setZtsl(String ztsl) {
		this.ztsl = ztsl;
	}
	public String getZtdw() {
		return ztdw;
	}
	public void setZtdw(String ztdw) {
		this.ztdw = ztdw;
	}
	public String getZtc() {
		return ztc;
	}
	public void setZtc(String ztc) {
		this.ztc = ztc;
	}
	public String getQwbs() {
		return qwbs;
	}
	public void setQwbs(String qwbs) {
		this.qwbs = qwbs;
	}
	public String getXgjg() {
		return xgjg;
	}
	public void setXgjg(String xgjg) {
		this.xgjg = xgjg;
	}
	public String getXbjg() {
		return xbjg;
	}
	public void setXbjg(String xbjg) {
		this.xbjg = xbjg;
	}
	public String getKzf() {
		return kzf;
	}
	public void setKzf(String kzf) {
		this.kzf = kzf;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	public String getLcfqrszbm() {
		return lcfqrszbm;
	}
	public void setLcfqrszbm(String lcfqrszbm) {
		this.lcfqrszbm = lcfqrszbm;
	}
	public String getLcfqr() {
		return lcfqr;
	}
	public void setLcfqr(String lcfqr) {
		this.lcfqr = lcfqr;
	}
	public String getWjnr() {
		return wjnr;
	}
	public void setWjnr(String wjnr) {
		this.wjnr = wjnr;
	}
	
	public void toInfo(DocuDetail d) {
		this.setBgqx(d.getBgqx());
		this.setBz(d.getBz());
		this.setCompanyNum("40284c815c34747a015c349902d30007");
		this.setIsArchive(Integer.toString(0));
		this.setCwrq(d.getCwrq());
		this.setDagdm(d.getDagdm());
		this.setDelFlag(Integer.toString(d.getDelFlag()));
		this.setDh(d.getDh());
		this.setDzwdh(d.getDzwdh());
		this.setFlh(d.getFlh());
		this.setGb(d.getGb());
		this.setGdnd(d.getGdnd());
		this.setJh(d.getJh());
		this.setKzf(d.getKzf());
		this.setLcfqr(d.getLcfqr());
		this.setLcfqrszbm(d.getLcfqrszbm());
		this.setMj(d.getMj());
		this.setMlh(d.getMlh());
		this.setQwbs(d.getQwbs());
		this.setQzh(d.getQzh());
		this.setSwh(d.getSwh());
		this.setSzyh(d.getSzyh());
		this.setTm(d.getTm());
		this.setUuid(d.getId());
		this.setWh(d.getWh());
		this.setWjnr(d.getWjnr());
		this.setWz(d.getWz());
		this.setXbjg(d.getXbjg());
		this.setXgjg(d.getXgjg());
		this.setZrz(d.getZrz());
		this.setZtc(d.getZtc());
		this.setZtdw(d.getZtdw());
		this.setZtgg(d.getZtgg());
		this.setZtlx(d.getZtlx());
		this.setZtsl(d.getZtsl());
    }
	@Override
	public void set(String colname, Object val) {
	}
	@Override
	public <T> T get(String colname) {
		return null;
	}
	
    
}
