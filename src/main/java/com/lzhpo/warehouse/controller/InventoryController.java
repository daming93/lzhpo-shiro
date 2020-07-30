package com.lzhpo.warehouse.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.warehouse.entity.Inventory;
import com.lzhpo.warehouse.entity.InventoryMaterial;
import com.lzhpo.warehouse.service.IInventoryMaterialService;
import com.lzhpo.warehouse.service.IInventoryService;

import cn.hutool.core.util.NumberUtil;
/**
 * <p>
 * 盘点表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-06-29
 */
@Controller
@RequestMapping("warehouse/inventory")
public class InventoryController {
 	@Autowired
    private IInventoryService inventoryService;

 	@Autowired
 	private IInventoryMaterialService inventoryMaterialService;
 	
 	
    @Autowired
    UserService userService;

    @Autowired
	private IBasicdataService basicdateService;
    
	@GetMapping(value = "list")
	public String list(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
        return "warehouse/inventory/listInventory";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("warehouse:inventory:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<Inventory> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<Inventory> inventoryPageData = new PageData<>();
        QueryWrapper<Inventory> inventoryWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        inventoryWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String clientId = (String) map.get("clientId");
        	String startTime =(String) map.get("startTime");
			String overTime =(String) map.get("overTime");
			String type  = (String) map.get("type");
			if (StringUtils.isNotBlank(startTime)) {
				inventoryWrapper.ge("inventory_time", startTime);
			}
			if (StringUtils.isNotBlank(overTime)) {
				inventoryWrapper.le("inventory_time", overTime);
			}
			if (StringUtils.isNotBlank(overTime)) {
				inventoryWrapper.eq("client_id", clientId);
			}
			if(type!=null){
				inventoryWrapper.eq("auditor_type", type);
			}
        }
        IPage<Inventory> inventoryPage = inventoryService.page(new Page<>(page,limit),inventoryWrapper);
        inventoryPageData.setCount(inventoryPage.getTotal());
        inventoryPageData.setData(setUserToInventory(inventoryPage.getRecords()));
        return inventoryPageData;
    }
    //创建者，和修改人
   private List<Inventory> setUserToInventory(List<Inventory> inventorys){
        inventorys.forEach(r -> {
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
              if(StringUtils.isNotBlank(r.getClientId())){
            	  r.setClientName(basicdateService.getBasicdataById(r.getClientId()).getClientShortName());
              }
              if(r.getAuditorType()!=null){
            	r.setAuditorTypeStr(CommomUtil.valueToNameInDict(r.getAuditorType(), "auditor_type"));
              }
              if(r.getStatus()!=null){
            	  r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "check_type"));
              }
              if(r.getBatchStatus()!=null){
            	  r.setTypeStr(CommomUtil.valueToNameInDict(r.getBatchStatus(), "material_search"));
              }
              if(r.getInventoryStatus()!=null){
            	  r.setInventoryStatusStr(CommomUtil.valueToNameInDict(r.getInventoryStatus(), "modify_status"));
              }
        });

        return inventorys;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<Inventory> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}

    @RequiresPermissions("warehouse:inventory:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(Integer continuity,String startTime,String endTime,Integer batchStatus,String clientId){
//        if(StringUtils.isBlank(inventory.getName())){
//            return ResponseEntity.failure("修改提示信息（不能为空)");
//        }
//        if(inventoryService.getInventoryCount(inventory.getName())>0){
//             return ResponseEntity.failure("修改提示信息（不能重复)");
//        }
    	Inventory inventory   = new Inventory(); 
    	if(!StringUtils.isBlank(startTime)){
    		inventory.setStartTime(LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    	}
    	if(!StringUtils.isBlank(endTime)){
    		inventory.setEndTime(LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    	}
    	inventory.setBatchStatus(batchStatus);
    	inventory.setClientId(clientId);
    	inventory.setAuditorType(continuity);
        inventoryService.saveInventory(inventory);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("warehouse:inventory:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        Inventory inventory = inventoryService.getInventoryById(id);
        inventoryService.deleteInventory(inventory);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("warehouse:inventory:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<Inventory> inventorys){
        if(inventorys == null || inventorys.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (Inventory r : inventorys){
            inventoryService.deleteInventory(r);
        }
        return ResponseEntity.success("操作成功");
    }
    
    @RequiresPermissions("warehouse:inventory:ensure")
    @PostMapping("ensure")
    @ResponseBody
    @SysLog("确定数据")
    public ResponseEntity ensure(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }

        Inventory inventory = inventoryService.getInventoryById(id);
        inventoryService.ensureInventory(inventory);
        return ResponseEntity.success("操作成功");
    }
    
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       Inventory inventory =  inventoryService.getInventoryById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("inventory", inventory);
    	List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
        return "warehouse/inventory/editInventory";
   }

    @RequiresPermissions("warehouse:inventory:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody Inventory inventory){
        if(StringUtils.isBlank(inventory.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
//        if(StringUtils.isBlank(inventory.getName())){
//            return ResponseEntity.failure("角色名称不能为空");
//        }
//        Inventory oldInventory =  inventoryService.getInventoryById(inventory.getId());
//        if(!oldInventory.getName().equals(inventory.getName())){
//             if( inventoryService.getInventoryCount(inventory.getName())>0){
//                return ResponseEntity.failure("修改提示信息（不能重复)");
//            }
//        }
         inventoryService.updateInventory(inventory);
        return ResponseEntity.success("操作成功");
    }
    
    /*---------------------- 以下对子表进行操作 -------------------- */
    /**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<InventoryMaterial> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request,
			String inventoryId) {
		PageData<InventoryMaterial> contInventoryMaterialData = new PageData<>();
		QueryWrapper<InventoryMaterial> inventoryMaterialWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		inventoryMaterialWrapper.eq("inventory_id", inventoryId);
		inventoryMaterialWrapper.eq("del_flag", false);
		inventoryMaterialWrapper.orderByAsc("code");
		IPage<InventoryMaterial> InventoryMaterialPage = inventoryMaterialService.page(new Page<>(page, limit),
				inventoryMaterialWrapper);
		contInventoryMaterialData.setCount(InventoryMaterialPage.getTotal());
		contInventoryMaterialData.setData(supplementToDetail(InventoryMaterialPage.getRecords()));
		return contInventoryMaterialData;
	}

	private List<InventoryMaterial> supplementToDetail(List<InventoryMaterial> details) {
		details.forEach(r -> {
			
			if(r.getCheckType()!=null){
				r.setCheckTypeStr(CommomUtil.valueToNameInDict(r.getCheckType(), "check_type"));
			}else{
				r.setCheckTypeStr("待定");
			}
			if(r.getType()!=null){
				r.setTypeStr(CommomUtil.valueToNameInDict(r.getType(), "material_type"));
			}
		});
		return details;
	}
	@PostMapping("editDetail")
	@ResponseBody
	@SysLog("保存编辑明细出库数据")
	public ResponseEntity editDetail(@RequestBody InventoryMaterial detail) {
		if (StringUtils.isBlank(detail.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		//只能改带确认状态得出库单
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		String inventoryId = inventoryMaterialService.getById(detail.getId()).getInventoryId();
		if(!modify_status_await.equals(inventoryService.getById(inventoryId).getInventoryStatus())){
			return ResponseEntity.failure("该单据不在待确认状态无法修改");
		}
		try {
			if(detail.getInventoryWholeNum()==null){
				detail.setInventoryWholeNum(0);
			}
			if(detail.getInventoryScatteredNum()==null){
				detail.setInventoryScatteredNum(0);
			}
			detail.setInventoryNum((long) (detail.getInventoryWholeNum()*detail.getUnitRate()+detail.getInventoryScatteredNum()));
			detail.setDifference(detail.getInventoryNum()-detail.getDepotNum());
			inventoryMaterialService.updateById(detail);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("操作失败");
		}
		return ResponseEntity.success("操作成功");
	}
	@PostMapping("deleteDetail")
	@ResponseBody
	@SysLog("删除明细数据")
	public ResponseEntity deleteDetail(@RequestBody InventoryMaterial detail) {
		if (StringUtils.isBlank(detail.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		//只能改带确认状态得出库单
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		String inventoryId = inventoryMaterialService.getById(detail.getId()).getInventoryId();
		if(!modify_status_await.equals(inventoryService.getById(inventoryId).getInventoryStatus())){
			return ResponseEntity.failure("该单据不在待确认状态无法修改");
		}
		try {
			inventoryMaterialService.deleteInventoryMaterial(detail);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("操作失败");
		}
		return ResponseEntity.success("操作成功");
	}
}
