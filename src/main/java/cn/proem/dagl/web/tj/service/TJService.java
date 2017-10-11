package cn.proem.dagl.web.tj.service;

import java.util.List;
import java.util.Map;

import cn.proem.dagl.web.tj.dto.CheckDto;
import cn.proem.dagl.web.tj.dto.SeriesDto;
import cn.proem.dagl.web.tj.entity.GclOrder;

public interface TJService {
    List<Map<String, Object>> tjNdhArea(String company);
    List<Map<String, Object>> tjWjGcl(String company);
    List<Map<String, Object>> tjAjGcl(String company);
    
    List<Map<String, Object>> tjWjGclA(String company);
    List<Map<String, Object>> tjAjGclA(String company);
    List<Map<String, Object>> tjNdhAreaA(String company);
    
    void tjGclSaveOrUpdate(List<String> ord, String type, String company);
    List<GclOrder> tjGclFind(String type, String company);
    
    
    List<CheckDto> designMl(String company);
    List<String> designNd(String company);
    List<CheckDto> designMj(String company);
    List<CheckDto> designBgqx(String company);
    
    List<Map<String, Object>> designDalxReport(String from, String to, String company, List<String> mls, List<String> bgqxs, List<String> mjs);
    List<SeriesDto> designDalxPic(String from, String to, String company, List<String> mls, List<String> bgqxs, List<String> mjs);
    
    // 划控
    List<CheckDto> designHKMl(String company);
    List<CheckDto> designHKNd(String company);
    List<Map<String, Object>> tjHK(String from, String to, List<String> ywm, String hk, String company);
    
    // 鉴定
    List<CheckDto> designJDMl(String company);
    List<CheckDto> designJDNd(String company);
    List<CheckDto> designJDNr(String company);
    List<Map<String, Object>> tjJD(String from, String to, List<String> ywm, String hk, String company);
    
    // 档案(保管期限，密级)
    List<CheckDto> designDAMl(String company);
    List<CheckDto> designDAMj(String company);
    List<CheckDto> designDANd(String company);
    List<CheckDto> designDABgqx(String company);
    List<Map<String, Object>> tjDa(String from, String to, List<String> mls, List<String> mjs, List<String> bgqxs, String company);
    List<Map<String, Object>> tjDaMl(String from, String to, List<String> mls, String company);
    List<Map<String, Object>> tjDaNd(String from, String to, List<String> mls, String company);
    
    // 实体借阅统计
    List<CheckDto> designJYMl(String company);
    List<CheckDto> designJYjyxg(String company);
    List<CheckDto> designJYjymd(String company);
    List<CheckDto> designJYjysj(String company);
    List<Map<String, Object>> tjJymd(String from, String to, List<String> jymd, String company);
    List<Map<String, Object>> tjJyxg(String from, String to, List<String> jyxg, String company);
    
    // 电子借阅统计
    List<CheckDto> designDZJYMl(String company);
    List<CheckDto> designDZJYjyxg(String company);
    List<CheckDto> designDZJYjymd(String company);
    List<CheckDto> designDZJYjysj(String company);
    List<Map<String, Object>> tjDZJymd(String from, String to, List<String> ml, List<String> jymd, String company);
    List<Map<String, Object>> tjDZJyxg(String from, String to, List<String> ml, List<String> jyxg, String company);
    
    // 电子附件统计
    List<CheckDto> designDZFJMl(String company);
    List<CheckDto> designDZFJNd(String company);
    List<Map<String, Object>> tjDzfj(String from, String to, List<String> mls, String company);
    
    //挂接附件统计
    List<CheckDto> designGJFJMl(String company);
    List<CheckDto> designGJFJNd(String company);
    List<Map<String, Object>> tjGJFJ(String from, String to, List<String> mls, String company);
    
    //电子文件 
    List<CheckDto> designDZWJMl(String company);
    List<CheckDto> designDZWJNd(String company);
    List<Map<String, Object>> tjDZWJ(String from, String to, List<String> mls, String company);
    
    //EEP
    List<CheckDto> designEEPMl(String company);
    List<Map<String, Object>> tjEEP(List<String> mls, String company, String state);
}
