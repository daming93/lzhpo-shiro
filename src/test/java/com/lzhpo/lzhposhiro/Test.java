package com.lzhpo.lzhposhiro;

import java.util.Date;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

public class Test {

	public static void main(String[] args) {
		Date date = DateUtil.date();
    	String dateStr = "2020-04-01";
    	Date dateOld = DateUtil.parse(dateStr, "yyyy-MM-dd");
    	long V = DateUtil.between(date, dateOld, DateUnit.DAY);
    	System.out.println(V);

	}

}
