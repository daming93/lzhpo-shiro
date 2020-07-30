package com.lzhpo.warehouse.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.warehouse.entity.Tray;
import com.lzhpo.warehouse.service.ITrayService;
/**
 * <p>
 * 储位表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-04-24
 */
@Controller
@RequestMapping("/warehouse/tray")
public class TrayController {
 	@Autowired
    private ITrayService trayService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "warehouse/tray/listTray";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("warehouse:tray:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<Tray> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<Tray> trayPageData = new PageData<>();
        QueryWrapper<Tray> trayWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        trayWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("code");
            if(StringUtils.isNotBlank(keys)) {
                trayWrapper.like("code", keys);
            }
        }
        IPage<Tray> trayPage = trayService.page(new Page<>(page,limit),trayWrapper);
        trayPageData.setCount(trayPage.getTotal());
        trayPageData.setData(setUserToTray(trayPage.getRecords()));
        return trayPageData;
    }
    //创建者，和修改人
   private List<Tray> setUserToTray(List<Tray> trays){
        trays.forEach(r -> {
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

        return trays;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<Tray> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "warehouse/tray/addTray";
    }

    @RequiresPermissions("warehouse:tray:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody Tray tray){
        if(trayService.getTrayCount(tray)>0){
             return ResponseEntity.failure("不能重复");
        }
        trayService.saveTray(tray);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("warehouse:tray:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("托盘ID不能为空");
        }
        Tray tray = trayService.getTrayById(id);
        trayService.deleteTray(tray);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("warehouse:tray:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<Tray> trays){
        if(trays == null || trays.size()==0){
            return ResponseEntity.failure("请选择需要删除的托盘");
        }
        for (Tray r : trays){
            trayService.deleteTray(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       Tray tray =  trayService.getTrayById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("tray", tray);
      
        return "warehouse/tray/editTray";
   }

    @RequiresPermissions("warehouse:tray:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody Tray tray){
        if( trayService.getTrayCount(tray)>0){
           return ResponseEntity.failure("不能重复");
        }
        trayService.updateTray(tray);
        return ResponseEntity.success("操作成功");
    }
    
	
	@SysLog("上传文件")
	@PostMapping("upload")
	@ResponseBody
	public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {
		if (file == null) {
			return ResponseEntity.failure("上传文件为空 ");
		}
		Map map = new HashMap();
		String message = trayService.upload(file);
		return ResponseEntity.success(message).setAny("data", map);
	}
}
