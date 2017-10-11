package cn.proem.suw.web.docu.service;

import cn.proem.suw.web.common.exception.ServiceException;
import cn.proem.suw.web.docu.entity.DocWDetail;
import cn.proem.suw.web.docu.entity.DocuAttachment;
import cn.proem.suw.web.docu.entity.DocuDetail;

public interface DocuService {

	/**
	 * @MethodName importDocuDetails
	 * @Description 导入文档详细信息
	 * @author Pan Jilong
	 * @date 2017年3月29日
	 * @param docuDetail
	 */
	void importDocu(DocuDetail docuDetail) throws ServiceException;

	/**
	 * @MethodName importAtta
	 * @Description 导入附件
	 * @author Pan Jilong
	 * @date 2017年3月29日
	 * @param docuAttachment
	 * @throws ServiceException 
	 */
	void importAtta(DocuAttachment docuAttachment) throws ServiceException;
	
	/**
     * @MethodName importWorkFlow
     * @Description 导入工作流
     * @author Pan Jilong
     * @date 2017年3月29日
     * @param docuAttachment
     * @throws ServiceException 
     */
    void importWorkFlow(DocWDetail docWDetail) throws ServiceException;
	
}
