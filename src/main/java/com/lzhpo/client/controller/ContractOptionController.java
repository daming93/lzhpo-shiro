package com.lzhpo.client.controller;


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
import com.lzhpo.client.entity.ContractOption;
import com.lzhpo.client.service.IContractOptionService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;


/**
 * <p>
 *  
 * </p>
 *
 * @author xdm
 * @since 2020-03-25
 */

@Controller
@RequestMapping("/client/contractOption")
public class ContractOptionController {
    @Autowired
    private IContractOptionService contractOptionService;
    
    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "client/contractOption/listContractOption";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("client:contractOption:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<ContractOption> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<ContractOption> ContractOptionPageData = new PageData<>();
        QueryWrapper<ContractOption> ContractOptionWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        ContractOptionWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                ContractOptionWrapper.like("name", keys);
            }
        }
        IPage<ContractOption> ContractOptionPage = contractOptionService.page(new Page<>(page,limit),ContractOptionWrapper);
        ContractOptionPageData.setCount(ContractOptionPage.getTotal());
        ContractOptionPageData.setData(setUserToContractOption(ContractOptionPage.getRecords()));
        return ContractOptionPageData;
    }
    //创建者，和修改人
   private List<ContractOption> setUserToContractOption(List<ContractOption> contractOptions){
        contractOptions.forEach(r -> {
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

        return contractOptions;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<ContractOption> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "client/contractOption/addContractOption";
    }

    @RequiresPermissions("client:contractOption:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody ContractOption contractOption){
        if(StringUtils.isBlank(contractOption.getName())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(contractOptionService.getContractOptionCount(contractOption.getName())>0){
             return ResponseEntity.failure("修改提示信息（不能重复)");
        }
        contractOptionService.saveContractOption(contractOption);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("client:contractOption:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        ContractOption contractOption = contractOptionService.getContractOptionById(id);
        contractOptionService.deleteContractOption(contractOption);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("client:contractOption:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<ContractOption> contractOptions){
        if(contractOptions == null || contractOptions.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (ContractOption r : contractOptions){
            contractOptionService.deleteContractOption(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       ContractOption contractOption =  contractOptionService.getContractOptionById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("contractOption", contractOption);
      
        return "client/contractOption/editContractOption";
   }

    @RequiresPermissions("client:contractOption:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody ContractOption contractOption){
        if(StringUtils.isBlank(contractOption.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(StringUtils.isBlank(contractOption.getName())){
            return ResponseEntity.failure("角色名称不能为空");
        }
        ContractOption oldContractOption =  contractOptionService.getContractOptionById(contractOption.getId());
        if(!oldContractOption.getName().equals(contractOption.getName())){
             if( contractOptionService.getContractOptionCount(contractOption.getName())>0){
                return ResponseEntity.failure("修改提示信息（不能重复)");
            }
        }
         contractOptionService.updateContractOption(contractOption);
        return ResponseEntity.success("操作成功");
    }
}
