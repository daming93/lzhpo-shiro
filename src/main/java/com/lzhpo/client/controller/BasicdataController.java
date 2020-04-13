package com.lzhpo.client.controller;


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
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;


/**
 * <p>
 * 客户表 
 * </p>
 *
 * @author xdm
 * @since 2020-03-24
 */
@Controller
@RequestMapping("client/basicdata")
public class BasicdataController {
    @Autowired
    private IBasicdataService basicdataService;
 
    @Autowired
    private UserService userService ;
 
    @GetMapping(value = "list")
    public String list(){
        return "client/basicdata/list";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("client:basicdata:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<Basicdata> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<Basicdata> BasicdataPageData = new PageData<>();
        QueryWrapper<Basicdata> BasicdataWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        BasicdataWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
        	String clientName = (String) map.get("clientName");
            if(StringUtils.isNotBlank(clientName)) {
            	BasicdataWrapper.like("client_name", clientName);
            }
        }
        IPage<Basicdata> BasicdataPage = basicdataService.page(new Page<>(page,limit),BasicdataWrapper);
        BasicdataPageData.setCount(BasicdataPage.getTotal());
        BasicdataPageData.setData(setUserToBasicdata(BasicdataPage.getRecords()));
        return BasicdataPageData;
    }
    //创建者，和修改人
   private List<Basicdata> setUserToBasicdata(List<Basicdata> basicdatas){
        basicdatas.forEach(r -> {
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

        return basicdatas;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<Basicdata> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "client/basicdata/add";
    }

    @RequiresPermissions("client:basicdata:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody Basicdata basicdata){
        if(StringUtils.isBlank(basicdata.getClientName())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(basicdataService.getBasicdataCount(basicdata.getClientName())>0){
             return ResponseEntity.failure("修改提示信息（不能重复)");
        }
        basicdataService.saveBasicdata(basicdata);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("client:basicdata:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        Basicdata basicdata = basicdataService.getBasicdataById(id);
        basicdataService.deleteBasicdata(basicdata);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("client:basicdata:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<Basicdata> basicdatas){
        if(basicdatas == null || basicdatas.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (Basicdata r : basicdatas){
            basicdataService.deleteBasicdata(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
        Basicdata Basicdata =  basicdataService.getBasicdataById(id);
       /**
       *自定义代码
       */
        modelMap.put("parentId",null);
        modelMap.put("isShow",Boolean.FALSE);
        modelMap.put("basicdata", Basicdata);
        return "client/basicdata/edit";
   }

    @RequiresPermissions("client:basicdata:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody Basicdata basicdata){
        if(StringUtils.isBlank(basicdata.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(StringUtils.isBlank(basicdata.getClientName())){
            return ResponseEntity.failure("角色名称不能为空");
        }
        Basicdata oldBasicdata =  basicdataService.getBasicdataById(basicdata.getId());
        if(!oldBasicdata.getClientName().equals(basicdata.getClientName())){
             if( basicdataService.getBasicdataCount(basicdata.getClientName())>0){
                return ResponseEntity.failure("修改提示信息（不能重复)");
            }
        }
         basicdataService.updateBasicdata(basicdata);
        return ResponseEntity.success("操作成功");
    }
}
