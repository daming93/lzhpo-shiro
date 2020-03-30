package com.lzhpo.common.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lzhpo.sys.entity.Dictionary;
import com.lzhpo.sys.service.IDictionaryService;
@Component
public class CacheUtils {
	public static  Map<String, List<Dictionary>> allDicts = new HashMap<String, List<Dictionary>>();
	public static Map<String, Dictionary> keyDict = new HashMap<String, Dictionary>();
	
	
	@Autowired
	private IDictionaryService service;
	
	@PostConstruct
	public void  nextCode(){
		System.out.println("------加载字典表--------");
		List<Dictionary> dicList = service.selectAll();
		List<String> categoryList = new ArrayList<String>();
		if (MapUtils.isNotEmpty(keyDict)) {
			keyDict.clear();
			allDicts.clear();
		}
		for (Dictionary dict : dicList) {
			keyDict.put(dict.getDkey(), dict);
			if (!categoryList.contains(dict.getCategoryCode())) {
				categoryList.add(dict.getCategoryCode());
			}
		}
		for (String category : categoryList) {
			List<Dictionary> sDictList = new ArrayList<Dictionary>();
			for (Dictionary dict : dicList) {
				if (dict.getCategoryCode().equals(category)) {
					sDictList.add(dict);
				}
			}
			allDicts.put(category, sDictList);
		}
		System.out.println("------加载字典表结束--------");
	}
}
