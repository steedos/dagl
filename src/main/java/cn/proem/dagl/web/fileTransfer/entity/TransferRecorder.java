package cn.proem.dagl.web.fileTransfer.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.proem.core.entity.Department;
import cn.proem.suw.web.common.model.MappedEntityModel;

/**
 * 档案移交记录实体类
 * @author Administrator
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "PFM_FILE_TRANSFER")
public class TransferRecorder extends MappedEntityModel{
	
	@Column
	private String dh;
	@Column
	private String tm;
	@Column
	private String mj;
	@Column
	private String cwrq;
	@Column
	private String zrz;
	@Column
	private String bgqx;
	
	/**
	 * 开始时间
	 */
	@Column(name = "start_time")
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	@Column(name = "end_time")
	protected Date endTime;
	
	
	
	/**
	 * 移交公司
	 */
	@ManyToOne
	@JoinColumn(name="transfCompany_id")
	private Department transfCompany;
	/**
	 * 接受公司
	 */
	@ManyToOne
	@JoinColumn(name="receiCompany_id")
	private Department receiCompany;
	/**
	 * 审批人
	 */
	@Column
	private String approver;
	/**
	 * 审批状态
	 */
	@Column
	private String state;
	/**
	 * 移交档案id
	 */
	@Column
	private String daid;
	
	public String getDh() {
		return dh;
	}
	public void setDh(String dh) {
		this.dh = dh;
	}
	public String getTm() {
		return tm;
	}
	public void setTm(String tm) {
		this.tm = tm;
	}
	public String getMj() {
		return mj;
	}
	public void setMj(String mj) {
		this.mj = mj;
	}
	public String getCwrq() {
		return cwrq;
	}
	public void setCwrq(String cwrq) {
		this.cwrq = cwrq;
	}
	public String getZrz() {
		return zrz;
	}
	public void setZrz(String zrz) {
		this.zrz = zrz;
	}
	public String getBgqx() {
		return bgqx;
	}
	public void setBgqx(String bgqx) {
		this.bgqx = bgqx;
	}
	
	public Department getTransfCompany() {
		return transfCompany;
	}
	public void setTransfCompany(Department transfCompany) {
		this.transfCompany = transfCompany;
	}
	public Department getReceiCompany() {
		return receiCompany;
	}
	public void setReceiCompany(Department receiCompany) {
		this.receiCompany = receiCompany;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDaid() {
		return daid;
	}
	public void setDaid(String daid) {
		this.daid = daid;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	

}
