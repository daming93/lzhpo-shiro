package com.lzhpo.sys.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.config.MySysUser;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.sys.entity.UserSetting;
import com.lzhpo.sys.service.IUserSettingService;
/**
 * <p>
 * 用户的自定义设置(偏好设置表，目前用于偏好设计的财务附表) 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
@Controller
@RequestMapping("sys/userSetting")
public class UserSettingController {
 	@Autowired
    private IUserSettingService userSettingService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "sys/userSetting/listUserSetting";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("sys:userSetting:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<UserSetting> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<UserSetting> userSettingPageData = new PageData<>();
        QueryWrapper<UserSetting> userSettingWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        userSettingWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                userSettingWrapper.like("name", keys);
            }
        }
        IPage<UserSetting> userSettingPage = userSettingService.page(new Page<>(page,limit),userSettingWrapper);
        userSettingPageData.setCount(userSettingPage.getTotal());
        userSettingPageData.setData(setUserToUserSetting(userSettingPage.getRecords()));
        return userSettingPageData;
    }
    //创建者，和修改人
   private List<UserSetting> setUserToUserSetting(List<UserSetting> userSettings){
        userSettings.forEach(r -> {
            if(StringUtils.isNotBlank(r.getCreateId())){
                User u = userService.findUserById(r.getCreateId());
                if(StringUtils.isBlank(u.getNickName())){
                  u.setNickName(u.getLoginName());
                }
                r.setCreateUser(u);
            }
              if(StringUtils.isNotBlank(r.getUpdateId())){
                User u  = userService.findUserById(r.getUpdateId());
                if(StringUtils.isBlank(u.getNickName())){
                    u.setNickName(u.getLoginName());
                }
                r.setUpdateUser(u);
            }
        });

        return userSettings;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<UserSetting> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	


    @GetMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(Integer type,Integer modular,String tableId){
    	String userId = MySysUser.id();
    	UserSetting userSetting =  new UserSetting();
    	userSetting.setModular(modular);
    	userSetting.setType(type);
    	userSetting.setTableId(tableId);
    	userSetting.setUserId(userId);
    	userSetting.setModularName(CommomUtil.valueToNameInDict(modular, "modular"));
        userSettingService.saveUserSetting(userSetting);
        return ResponseEntity.success("操作成功");
    }
}
