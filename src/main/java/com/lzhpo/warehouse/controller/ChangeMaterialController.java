package com.lzhpo.warehouse.controller;

import java.util.ArrayList;
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
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.warehouse.entity.ChangeMaterial;
import com.lzhpo.warehouse.entity.Depot;
import com.lzhpo.warehouse.service.IChangeMaterialService;
import com.lzhpo.warehouse.service.IDepotService;
/**
 * <p>
 * 调仓物料表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-06-03
 */
@Controller
@RequestMapping("/warehouse/changeMaterial")
public class ChangeMaterialController {
 	@Autowired
    private IChangeMaterialService changeMaterialService;
 	
 	@Autowired
    private IDepotService depotservice;

 	@Autowired	
	private IMaterialService materialService;
 	
 	@Autowired
 	private IClientitemService clientitemService;
 	
 	@Autowired
 	private IBasicdataService basicdataService;
    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "warehouse/changeMaterial/listChangeMaterial";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("warehouse:changeMaterial:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<ChangeMaterial> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<ChangeMaterial> changeMaterialPageData = new PageData<>();
        QueryWrapper<ChangeMaterial> changeMaterialWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        changeMaterialWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("code");
            if(StringUtils.isNotBlank(keys)) {
            	List<String> strs = new ArrayList<String>();
            	List<Clientitem> lists = clientitemService.selectByItemCode(keys);
            	for (Clientitem clientitem : lists) {
            		strs.add(clientitem.getId());
				}
            	if(strs.isEmpty()){
            		changeMaterialWrapper.eq("item_id", 1);
            	}else{
            		changeMaterialWrapper.in("item_id", strs);
            	}
                
            }
        }
        IPage<ChangeMaterial> changeMaterialPage = changeMaterialService.page(new Page<>(page,limit),changeMaterialWrapper);
        changeMaterialPageData.setCount(changeMaterialPage.getTotal());
        changeMaterialPageData.setData(setUserToChangeMaterial(changeMaterialPage.getRecords()));
        return changeMaterialPageData;
    }
    //创建者，和修改人
   private List<ChangeMaterial> setUserToChangeMaterial(List<ChangeMaterial> changeMaterials){
        changeMaterials.forEach(r -> {
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
              if(StringUtils.isNotBlank(r.getItemId())){
            	  Clientitem item = clientitemService.getById(r.getItemId());
            	  r.setItemCode(item.getCode());
            	  r.setItemName(item.getName());
            	  
              }
        });

        return changeMaterials;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<ChangeMaterial> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

   @GetMapping("add")
    public String add(ModelMap modelMap, String materialDdepotId,String code,String itemName){
	    Material material = ((List<Material>)materialService.selectMaterialByDepot(null, null, null, null, null, materialDdepotId).get("list")).get(0);
	    material.setClientName(basicdataService.getById(material.getClientId()).getClientShortName());
    	modelMap.addAttribute("material", material);
    	modelMap.addAttribute("itemName", itemName);
    	modelMap.addAttribute("code", code);
    	List<Depot> depotList = depotservice.selectByClientId(material.getClientId());
		/**
		 * 自定义传入add页面的数据
		 */
		modelMap.put("depotList", com.alibaba.fastjson.JSONObject.toJSON(depotList));
        return "warehouse/changeMaterial/addChangeMaterial";
    }

    @RequiresPermissions("warehouse:changeMaterial:add")
    @PostMapping("addChangeMaterial")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody ChangeMaterial changeMaterial){
    	if(changeMaterial == null || changeMaterial.getNewWholeNum()>changeMaterial.getOldWholeNum()
    			||  changeMaterial.getNewScatteredNum()>changeMaterial.getOldScatteredNum()){
            return ResponseEntity.failure("新数量不能大于老数量");
        }
    	if(changeMaterial.getNewScatteredNum()<0|| changeMaterial.getNewWholeNum() < 0){
    		 return ResponseEntity.failure("调整数量不能为负数");
    	}
    	Integer sku =clientitemService.getById( materialService.getById(changeMaterial.getMaterialId()).getItemId()).getUnitRate();
    	changeMaterial.setNownum(changeMaterial.getNewWholeNum()*sku+changeMaterial.getNewScatteredNum());
        changeMaterialService.saveChangeMaterial(changeMaterial);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("warehouse:changeMaterial:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        ChangeMaterial changeMaterial = changeMaterialService.getChangeMaterialById(id);
        changeMaterialService.deleteChangeMaterial(changeMaterial);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("warehouse:changeMaterial:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<ChangeMaterial> changeMaterials){
        if(changeMaterials == null || changeMaterials.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (ChangeMaterial r : changeMaterials){
            changeMaterialService.deleteChangeMaterial(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       ChangeMaterial changeMaterial =  changeMaterialService.getChangeMaterialById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("changeMaterial", changeMaterial);
      
        return "warehouse/changeMaterial/editChangeMaterial";
   }

    @RequiresPermissions("warehouse:changeMaterial:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody ChangeMaterial changeMaterial){
        ChangeMaterial oldChangeMaterial =  changeMaterialService.getChangeMaterialById(changeMaterial.getId());
         changeMaterialService.updateChangeMaterial(changeMaterial);
        return ResponseEntity.success("操作成功");
    }
}
