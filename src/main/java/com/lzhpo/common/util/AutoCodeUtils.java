package com.lzhpo.common.util;

import com.lzhpo.sys.entity.GenerateNo;
import com.lzhpo.sys.service.IGenerateNoService;

public class AutoCodeUtils {
	IGenerateNoService generateNoService;
	public AutoCodeUtils() {}
	
	
	
	
	public synchronized  String nextCode(String prefix){
		String code = "";
		
		
		generateNoService.getGenerateNoCount(prefix, "");
		return code;
	}
}
