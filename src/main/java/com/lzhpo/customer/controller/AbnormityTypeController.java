package com.lzhpo.customer.controller;

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
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.customer.entity.AbnormityType;
import com.lzhpo.customer.service.IAbnormityTypeService;
/**
 * <p>
 * 异常类型 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
@Controller
@RequestMapping("customer/abnormityType")
public class AbnormityTypeController {
 	@Autowired
    private IAbnormityTypeService abnormityTypeService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "customer/abnormityType/listAbnormityType";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("customer:abnormityType:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<AbnormityType> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<AbnormityType> abnormityTypePageData = new PageData<>();
        QueryWrapper<AbnormityType> abnormityTypeWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        abnormityTypeWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                abnormityTypeWrapper.like("name", keys);
            }
        }
        IPage<AbnormityType> abnormityTypePage = abnormityTypeService.page(new Page<>(page,limit),abnormityTypeWrapper);
        abnormityTypePageData.setCount(abnormityTypePage.getTotal());
        abnormityTypePageData.setData(setUserToAbnormityType(abnormityTypePage.getRecords()));
        return abnormityTypePageData;
    }
    //创建者，和修改人
   private List<AbnormityType> setUserToAbnormityType(List<AbnormityType> abnormityTypes){
        abnormityTypes.forEach(r -> {
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

        return abnormityTypes;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<AbnormityType> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "customer/abnormityType/addAbnormityType";
    }

    @RequiresPermissions("customer:abnormityType:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody AbnormityType abnormityType){
        if(StringUtils.isBlank(abnormityType.getName())){
            return ResponseEntity.failure("名称（不能为空)");
        }
        if(abnormityTypeService.getAbnormityTypeCount(abnormityType.getName())>0){
             return ResponseEntity.failure("名称（不能重复)");
        }
        abnormityTypeService.saveAbnormityType(abnormityType);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("customer:abnormityType:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        AbnormityType abnormityType = abnormityTypeService.getAbnormityTypeById(id);
        abnormityTypeService.deleteAbnormityType(abnormityType);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("customer:abnormityType:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<AbnormityType> abnormityTypes){
        if(abnormityTypes == null || abnormityTypes.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (AbnormityType r : abnormityTypes){
            abnormityTypeService.deleteAbnormityType(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       AbnormityType abnormityType =  abnormityTypeService.getAbnormityTypeById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("abnormityType", abnormityType);
      
        return "customer/abnormityType/editAbnormityType";
   }

    @RequiresPermissions("customer:abnormityType:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody AbnormityType abnormityType){
        if(StringUtils.isBlank(abnormityType.getId())){
            return ResponseEntity.failure("名称（不能为空)");
        }
        if(StringUtils.isBlank(abnormityType.getName())){
            return ResponseEntity.failure("角色名称不能为空");
        }
        AbnormityType oldAbnormityType =  abnormityTypeService.getAbnormityTypeById(abnormityType.getId());
        if(!oldAbnormityType.getName().equals(abnormityType.getName())){
             if( abnormityTypeService.getAbnormityTypeCount(abnormityType.getName())>0){
                return ResponseEntity.failure("名称（不能重复)");
            }
        }
         abnormityTypeService.updateAbnormityType(abnormityType);
        return ResponseEntity.success("操作成功");
    }
}
