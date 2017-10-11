package cn.proem.dagl.web.table.dto;

public class Header {
    private String id;//键值,字段名
    private String title;//页面显示
    private String type;//数据类型
    private String resolution;//自定义解析函数
    private String columnClass;
    private Boolean hide;//
    private Boolean advanceQuery;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getColumnClass() {
		return columnClass;
	}
	public void setColumnClass(String columnClass) {
		this.columnClass = columnClass;
	}
	public Boolean getHide() {
		return hide;
	}
	public void setHide(Boolean hide) {
		this.hide = hide;
	}
    /**
     * @return the advanceQuery
     */
    public Boolean getAdvanceQuery() {
        return advanceQuery;
    }
    /**
     * @param advanceQuery the advanceQuery to set
     */
    public void setAdvanceQuery(Boolean advanceQuery) {
        this.advanceQuery = advanceQuery;
    }
	
}
