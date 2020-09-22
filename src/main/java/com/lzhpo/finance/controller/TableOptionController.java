package com.lzhpo.finance.controller;

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
import com.lzhpo.finance.entity.TableOption;
import com.lzhpo.finance.service.ITableOptionService;
/**
 * <p>
 * 自定义收入表选项 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
@Controller
@RequestMapping("finance/tableOption")
public class TableOptionController {
 	@Autowired
    private ITableOptionService tableOptionService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "finance/tableOption/listTableOption";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("finance:tableOption:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<TableOption> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<TableOption> tableOptionPageData = new PageData<>();
        QueryWrapper<TableOption> tableOptionWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        tableOptionWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                tableOptionWrapper.like("name", keys);
            }
        }
        IPage<TableOption> tableOptionPage = tableOptionService.page(new Page<>(page,limit),tableOptionWrapper);
        tableOptionPageData.setCount(tableOptionPage.getTotal());
        tableOptionPageData.setData(setUserToTableOption(tableOptionPage.getRecords()));
        return tableOptionPageData;
    }
    //创建者，和修改人
   private List<TableOption> setUserToTableOption(List<TableOption> tableOptions){
        tableOptions.forEach(r -> {
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

        return tableOptions;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<TableOption> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "finance/tableOption/addTableOption";
    }

    @RequiresPermissions("finance:tableOption:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody TableOption tableOption){
        if(StringUtils.isBlank(tableOption.getName())){
            return ResponseEntity.failure("关键ID（不能为空)");
        }
        if(tableOptionService.getTableOptionCount(tableOption.getName())>0){
             return ResponseEntity.failure("名称（不能重复)");
        }
        tableOptionService.saveTableOption(tableOption);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("finance:tableOption:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        TableOption tableOption = tableOptionService.getTableOptionById(id);
        tableOptionService.deleteTableOption(tableOption);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("finance:tableOption:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<TableOption> tableOptions){
        if(tableOptions == null || tableOptions.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (TableOption r : tableOptions){
            tableOptionService.deleteTableOption(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       TableOption tableOption =  tableOptionService.getTableOptionById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("tableOption", tableOption);
      
        return "finance/tableOption/editTableOption";
   }

    @RequiresPermissions("finance:tableOption:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody TableOption tableOption){
        if(StringUtils.isBlank(tableOption.getId())){
            return ResponseEntity.failure("关键ID（不能为空)");
        }
        if(StringUtils.isBlank(tableOption.getName())){
            return ResponseEntity.failure("角色名称不能为空");
        }
        TableOption oldTableOption =  tableOptionService.getTableOptionById(tableOption.getId());
        if(!oldTableOption.getName().equals(tableOption.getName())){
             if( tableOptionService.getTableOptionCount(tableOption.getName())>0){
                return ResponseEntity.failure("名称（不能重复)");
            }
        }
         tableOptionService.updateTableOption(tableOption);
        return ResponseEntity.success("操作成功");
    }
}
