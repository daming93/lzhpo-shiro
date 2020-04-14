package com.lzhpo.common.config;

import org.apache.shiro.SecurityUtils;

import com.lzhpo.common.realm.AuthRealm;

/**
 * <p> Author：lzhpo </p>
 * <p> Title：</p>
 * <p> Description：</p>
 */
public class MySysUser {

    /**
     * 取出Shiro中的当前用户LoginName.
     */
    public static String icon() {
        return ShiroUser().getIcon();
    }

    public static String id() {
        return ShiroUser().getId();
    }

    public static String loginName() {
        return ShiroUser().getloginName();
    }

    public static String nickName() {
        return ShiroUser().getNickName();
    }

    public static AuthRealm.ShiroUser ShiroUser() {
    	AuthRealm.ShiroUser user = new AuthRealm.ShiroUser("18b8b543-9ad7-11e8-aebe-1368d4ec24eb", "admin", "管理员", "");
    	try {
    		user = (AuthRealm.ShiroUser) SecurityUtils.getSubject().getPrincipal();
		} catch (Exception e) {
			 return user;//如果发生错误就使用默认的
		}
        return user;
    }
}
