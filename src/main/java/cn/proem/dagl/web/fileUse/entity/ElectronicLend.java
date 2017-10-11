package cn.proem.dagl.web.fileUse.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.proem.core.entity.User;
import cn.proem.suw.web.common.model.MappedEntityModel;

/**
 * 电子借阅基本类
 * @author tangcc
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "pfm_electronic_lending")
public class ElectronicLend extends MappedEntityModel{
	/**
	 * 题名
	 */
	@Column
	private String tm;
	
	/**
	 * 文号
	 */
	@Column
	private String wh;
	
	/**
	 * 档号
	 */
	@Column
	private String dh;
	
	/**
	 * 档案id
	 */
	@Column
	private String daid;
	
	/**
	 * 表名
	 */
	@Column
	private String bm;
	
	/**
	 * 备注
	 */
	 @Column(name="BZ", columnDefinition="CLOB", nullable=true)
	private String bz;
	
	/**
	 * 借阅状态
	 */
	@Column
	private String jyzt;
	/**
	 * 借阅目的
	 */
	@Column
	private String jymd;
	
	/**
	 * 借阅时间
	 */
	@Column
	private String jysj;
	
	/**
	 * 借阅效果
	 */
	@Column
	private String jyxg;
	
	
	/**
	 * 借阅人
	 */
	@ManyToOne
	@JoinColumn(name = "jyrid")
	private User jyr;
	
	public String getTm() {
		return tm;
	}

	public void setTm(String tm) {
		this.tm = tm;
	}

	public String getWh() {
		return wh;
	}

	public void setWh(String wh) {
		this.wh = wh;
	}

	public String getDh() {
		return dh;
	}

	public void setDh(String dh) {
		this.dh = dh;
	}

	public String getDaid() {
		return daid;
	}

	public void setDaid(String daid) {
		this.daid = daid;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public String getJyzt() {
		return jyzt;
	}

	public void setJyzt(String jyzt) {
		this.jyzt = jyzt;
	}

	public String getJymd() {
		return jymd;
	}

	public void setJymd(String jymd) {
		this.jymd = jymd;
	}

	public String getJysj() {
		return jysj;
	}

	public void setJysj(String jysj) {
		this.jysj = jysj;
	}

	public String getJyxg() {
		return jyxg;
	}

	public void setJyxg(String jyxg) {
		this.jyxg = jyxg;
	}

	public String getBm() {
		return bm;
	}

	public void setBm(String bm) {
		this.bm = bm;
	}

	public User getJyr() {
		return jyr;
	}

	public void setJyr(User jyr) {
		this.jyr = jyr;
	}
	
	
}
