package cn.proem.dagl.web.tj.dto;

import java.util.List;
import java.util.Map;

public class GCLdto {
    
    
    /**
     * @return the ord
     */
    public List<String> getOrd() {
        return ord;
    }
    /**
     * @param ord the ord to set
     */
    public void setOrd(List<String> ord) {
        this.ord = ord;
    }
    private List<Map<String, Object>> gcl;
    private List<Map<String, Object>> ndhArea;
    private List<Map<String, Object>> gclwj;
    private List<Map<String, Object>> gclaj;
    private List<String> ord;
    /**
     * @return the gcl
     */
    public List<Map<String, Object>> getGcl() {
        return gcl;
    }
    /**
     * @param gcl the gcl to set
     */
    public void setGcl(List<Map<String, Object>> gcl) {
        this.gcl = gcl;
    }
    /**
     * @param maxNdh the maxNdh to set
     */
    public List<Map<String, Object>> getNdhArea() {
        return ndhArea;
    }
    public void setNdhArea(List<Map<String, Object>> ndhArea) {
        this.ndhArea = ndhArea;
    }
    /**
     * @return the gclwj
     */
    public List<Map<String, Object>> getGclwj() {
        return gclwj;
    }
    /**
     * @return the gclaj
     */
    public List<Map<String, Object>> getGclaj() {
        return gclaj;
    }
    /**
     * @param gclwj the gclwj to set
     */
    public void setGclwj(List<Map<String, Object>> gclwj) {
        this.gclwj = gclwj;
    }
    /**
     * @param gclaj the gclaj to set
     */
    public void setGclaj(List<Map<String, Object>> gclaj) {
        this.gclaj = gclaj;
    }
}