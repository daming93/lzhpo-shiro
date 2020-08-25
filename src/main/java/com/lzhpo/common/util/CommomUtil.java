package com.lzhpo.common.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.sys.entity.Dictionary;

public class CommomUtil {
	
	public static  Date localDate2Date(LocalDate localDate) {
        if(null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }
	public static String valueToNameInDict(Integer value,String listName){
		List<Dictionary> dicList = CacheUtils.allDicts.get(listName);
		for (Dictionary dictionary : dicList) {
			if(StringUtils.checkValNotNull(value)&&value.equals(dictionary.getValue())){
				return dictionary.getName();
			}
		}
		return null;
	}
	
	public static Integer nameToValueInDict(String name,String listName){
		List<Dictionary> dicList = CacheUtils.allDicts.get(listName);
		for (Dictionary dictionary : dicList) {
			if(StringUtils.checkValNotNull(name)&&name.equals(dictionary.getName())){
				return dictionary.getValue();
			}
		}
		return null;
	}
	public static Integer AllToOne(String numZ,Integer rate){
		if(numZ!=null&&numZ.contains(".")){
			String[] str = numZ.split("\\.");
			return Integer.valueOf(str[0])*rate+Integer.valueOf(str[1]);
		}else{
			return Integer.valueOf(numZ)*rate;
		}
	};
}
