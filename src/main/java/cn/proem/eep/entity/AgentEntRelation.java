//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2017.06.12 时间 07:27:17 AM GMT+08:00 
//


package cn.proem.eep.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.saac.gov.cn/standards/ERM/encapsulation}AgentEntId"/>
 *         &lt;element ref="{http://www.saac.gov.cn/standards/ERM/encapsulation}RelationAgentId"/>
 *         &lt;element ref="{http://www.saac.gov.cn/standards/ERM/encapsulation}RelationType" minOccurs="0"/>
 *         &lt;element ref="{http://www.saac.gov.cn/standards/ERM/encapsulation}Relation" minOccurs="0"/>
 *         &lt;element ref="{http://www.saac.gov.cn/standards/ERM/encapsulation}RelationDesc" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "agentEntId",
    "relationAgentId",
    "relationType",
    "relation",
    "relationDesc"
})
@XmlRootElement(name = "AgentEntRelation")
public class AgentEntRelation {

    @XmlElement(name = "AgentEntId", required = true)
    protected String agentEntId;
    @XmlElement(name = "RelationAgentId", required = true)
    protected String relationAgentId;
    @XmlElement(name = "RelationType")
    protected String relationType;
    @XmlElement(name = "Relation")
    protected String relation;
    @XmlElement(name = "RelationDesc")
    protected String relationDesc;

    /**
     * 获取agentEntId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentEntId() {
        return agentEntId;
    }

    /**
     * 设置agentEntId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentEntId(String value) {
        this.agentEntId = value;
    }

    /**
     * 获取relationAgentId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationAgentId() {
        return relationAgentId;
    }

    /**
     * 设置relationAgentId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationAgentId(String value) {
        this.relationAgentId = value;
    }

    /**
     * 获取relationType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationType() {
        return relationType;
    }

    /**
     * 设置relationType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationType(String value) {
        this.relationType = value;
    }

    /**
     * 获取relation属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelation() {
        return relation;
    }

    /**
     * 设置relation属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelation(String value) {
        this.relation = value;
    }

    /**
     * 获取relationDesc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelationDesc() {
        return relationDesc;
    }

    /**
     * 设置relationDesc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelationDesc(String value) {
        this.relationDesc = value;
    }

}
