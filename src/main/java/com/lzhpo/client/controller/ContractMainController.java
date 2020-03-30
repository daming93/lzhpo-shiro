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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.client.entity.ContractMain;
import com.lzhpo.client.entity.ContractOption;
import com.lzhpo.client.service.IContractMainService;
import com.lzhpo.client.service.IContractOptionService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.sys.entity.Dictionary;
import com.lzhpo.sys.service.IGenerateNoService;


/**
 * <p>
 *  
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */

@Controller
@RequestMapping("client/contractMain")
public class ContractMainController {
    @Autowired
    private IContractMainService contractMainService;
 
    @Autowired
    private UserService userService;
    
    @Autowired
    private IGenerateNoService generateNoService ;
    
    @Autowired
    private IContractOptionService contractOptionService ;
    @GetMapping(value = "list")
    public String list(){
        return "client/contractMain/listContractMain";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("client:contractMain:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<ContractMain> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<ContractMain> ContractMainPageData = new PageData<>();
        QueryWrapper<ContractMain> ContractMainWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        ContractMainWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("key");
            if(StringUtils.isNotBlank(keys)) {
                ContractMainWrapper.like("name", keys);
            }
        }
        ContractMainWrapper.orderByDesc("contract_code ");
        IPage<ContractMain> ContractMainPage = contractMainService.page(new Page<>(page,limit),ContractMainWrapper);
        ContractMainPageData.setCount(ContractMainPage.getTotal());
        ContractMainPageData.setData(setUserToContractMain(ContractMainPage.getRecords()));
        return ContractMainPageData;
    }
    //创建者，和修改人
   private List<ContractMain> setUserToContractMain(List<ContractMain> contractMains){
        contractMains.forEach(r -> {
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

        return contractMains;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<ContractMain> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap,String clientId,String clientName){
        /**
	*自定义传入add页面的数据
	*/
    	List<Dictionary> handingType = CacheUtils.allDicts.get("handing_type");
    	List<Dictionary> periodStatus = CacheUtils.allDicts.get("period_status");
    	List<ContractOption> options =  contractOptionService.selectAll();
    	modelMap.put("clientId", clientId);
    	modelMap.put("clientName", clientName);
    	modelMap.put("handingType", handingType);
    	modelMap.put("periodStatus",JSONObject.toJSON(periodStatus));
    	modelMap.put("options",JSONObject.toJSON(options));
        return "client/contractMain/addContractMain";
    }

    @RequiresPermissions("client:contractMain:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody ContractMain contractMain){
//        if(StringUtils.isBlank(contractMain.getClientId())){
//            return ResponseEntity.failure("客户（不能为空)");
//        }
        /*
         * 新增合同主体要生成code
         */
        contractMain.setContractCode(generateNoService.nextCode("HT"));
        contractMainService.saveContractMain(contractMain);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("client:contractMain:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        ContractMain contractMain = contractMainService.getContractMainById(id);
        contractMainService.deleteContractMain(contractMain);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("client:contractMain:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<ContractMain> contractMains){
        if(contractMains == null || contractMains.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (ContractMain r : contractMains){
            contractMainService.deleteContractMain(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       ContractMain contractMain =  contractMainService.getContractMainById(id);
       /**
       *自定义代码
       */
       // Map<String,Object> map = new HashMap();
        modelMap.put("contractMain", contractMain);
      
        return "client/contractMain/editContractMain";
   }

    @RequiresPermissions("client:contractMain:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody ContractMain contractMain){
        if(StringUtils.isBlank(contractMain.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
//        if(StringUtils.isBlank(contractMain.getName())){
//            return ResponseEntity.failure("角色名称不能为空");
//        }
        ContractMain oldContractMain =  contractMainService.getContractMainById(contractMain.getId());
//        if(!oldContractMain.getName().equals(contractMain.getName())){
//             if( contractMainService.getContractMainCount(contractMain.getName())>0){
//                return ResponseEntity.failure("修改提示信息（不能重复)");
//            }
//        }
         contractMainService.updateContractMain(contractMain);
        return ResponseEntity.success("操作成功");
    }
}
