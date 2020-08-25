package com.lzhpo.warehouse.controller;

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
import com.lzhpo.warehouse.entity.TakeoutExcption;
import com.lzhpo.warehouse.service.ITakeoutExcptionService;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-08-06
 */
@Controller
@RequestMapping("warehouse/takeoutExcption")
public class TakeoutExcptionController {
 	@Autowired
    private ITakeoutExcptionService takeoutExcptionService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "warehouse/takeoutExcption/listTakeoutExcption";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("warehouse:takeoutExcption:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<TakeoutExcption> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<TakeoutExcption> takeoutExcptionPageData = new PageData<>();
        QueryWrapper<TakeoutExcption> takeoutExcptionWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        takeoutExcptionWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                takeoutExcptionWrapper.like("name", keys);
            }
        }
        IPage<TakeoutExcption> takeoutExcptionPage = takeoutExcptionService.page(new Page<>(page,limit),takeoutExcptionWrapper);
        takeoutExcptionPageData.setCount(takeoutExcptionPage.getTotal());
        takeoutExcptionPageData.setData(setUserToTakeoutExcption(takeoutExcptionPage.getRecords()));
        return takeoutExcptionPageData;
    }
    //创建者，和修改人
   private List<TakeoutExcption> setUserToTakeoutExcption(List<TakeoutExcption> takeoutExcptions){
        takeoutExcptions.forEach(r -> {
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

        return takeoutExcptions;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<TakeoutExcption> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap,String id){
        /**
	*自定义传入add页面的数据
	*/
    	modelMap.put("takeoutId", id);
        return "warehouse/takeoutExcption/addTakeoutExcption";
    }

    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody TakeoutExcption takeoutExcption){
        if(StringUtils.isBlank(takeoutExcption.getName())){
            return ResponseEntity.failure("简述（不能为空)");
        }
        takeoutExcptionService.saveTakeoutExcption(takeoutExcption);
        return ResponseEntity.success("操作成功");
    }
 
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        TakeoutExcption takeoutExcption = takeoutExcptionService.getTakeoutExcptionById(id);
        takeoutExcptionService.deleteTakeoutExcption(takeoutExcption);
        return ResponseEntity.success("操作成功");
    }
 
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<TakeoutExcption> takeoutExcptions){
        if(takeoutExcptions == null || takeoutExcptions.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (TakeoutExcption r : takeoutExcptions){
            takeoutExcptionService.deleteTakeoutExcption(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       TakeoutExcption takeoutExcption =  takeoutExcptionService.getTakeoutExcptionById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("takeoutExcption", takeoutExcption);
      
        return "warehouse/takeoutExcption/editTakeoutExcption";
   }

    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody TakeoutExcption takeoutExcption){
        if(StringUtils.isBlank(takeoutExcption.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(StringUtils.isBlank(takeoutExcption.getName())){
            return ResponseEntity.failure("简述不能为空");
        }
        takeoutExcptionService.updateTakeoutExcption(takeoutExcption);
        return ResponseEntity.success("操作成功");
    }
}
