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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
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
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.MaterialDepot;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.lzhpo.warehouse.entity.Depot;
import com.lzhpo.warehouse.entity.MaterialManage;
import com.lzhpo.warehouse.entity.MaterialManageDetail;
import com.lzhpo.warehouse.entity.Tray;
import com.lzhpo.warehouse.service.IDepotService;
import com.lzhpo.warehouse.service.IMaterialManageDetailService;
import com.lzhpo.warehouse.service.IMaterialManageService;
import com.lzhpo.warehouse.service.ITrayService;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-06-05
 */
@Controller
@RequestMapping("warehouse/materialManage")
public class MaterialManageController {
 	@Autowired
    private IMaterialManageService materialManageService;

    @Autowired
    UserService userService;
    
    @Autowired
	private IBasicdataService basicdateService;

	@Autowired
	private ITrayService trayService;

	@Autowired
	private IDepotService depotService;
	
	@Autowired
	private IClientitemService clientitemService;
	
	@Autowired
	private IMaterialDepotService materialDepotService;
	
	@Autowired
	private IMaterialManageDetailService materialManageDetailService;
	
	
    @GetMapping(value = "list")
    public String list(){
        return "warehouse/materialManage/listMaterialManage";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("warehouse:materialManage:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<MaterialManage> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<MaterialManage> materialManagePageData = new PageData<>();
        QueryWrapper<MaterialManage> materialManageWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        materialManageWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                materialManageWrapper.like("name", keys);
            }
        }
        IPage<MaterialManage> materialManagePage = materialManageService.page(new Page<>(page,limit),materialManageWrapper);
        materialManagePageData.setCount(materialManagePage.getTotal());
        materialManagePageData.setData(setUserToMaterialManage(materialManagePage.getRecords()));
        return materialManagePageData;
    }
    //创建者，和修改人
   private List<MaterialManage> setUserToMaterialManage(List<MaterialManage> materialManages){
        materialManages.forEach(r -> {
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
              if (StringUtils.isNotBlank(r.getClientId())) {
  				r.setClientName(basicdateService.getById(r.getClientId()).getClientShortName());
  			}
  			if (r.getModifyStatus() != null) {
  				r.setModifyStatusStr(CommomUtil.valueToNameInDict(r.getModifyStatus(), "modify_status"));
  			}
  			if (r.getStatus() != null) {
  				r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "material_regulate"));
  			}
        });

        return materialManages;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<MaterialManage> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap,@RequestParam(value = "continuity", required = false) String continuity,@RequestParam(value = "mode", required = false) String mode){
    	List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		modelMap.put("continuity", continuity);
		modelMap.put("mode", mode);
        return "warehouse/materialManage/addMaterialManage";
    }

    @RequiresPermissions("warehouse:materialManage:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody MaterialManage materialManage){
    	try {
    		materialManageService.saveMaterialManage(materialManage);
		} catch (RuntimeJsonMappingException e) {
			return ResponseEntity.failure(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("系统异常,请联系管理员处理");
		}
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("warehouse:materialManage:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("ID不能为空");
        }
        MaterialManage materialManage = materialManageService.getMaterialManageById(id);
        materialManageService.deleteMaterialManage(materialManage);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("warehouse:materialManage:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<MaterialManage> materialManages){
        if(materialManages == null || materialManages.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (MaterialManage r : materialManages){
            materialManageService.deleteMaterialManage(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       MaterialManage materialManage =  materialManageService.getMaterialManageById(id);
        Map<String,Object> map = new HashMap();
        modelMap.put("materialManage", materialManage);
    	List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		List<Tray> trayList = trayService.selectByClientId(materialManage.getClientId());
		modelMap.put("trayList", JSONObject.toJSON(trayList));
		List<Depot> depots = depotService.selectByClientId(materialManage.getClientId());
		modelMap.put("depots", JSONObject.toJSON(depots));
		List<Clientitem> items = clientitemService.selectByClientId(materialManage.getClientId());
		modelMap.put("items", JSONObject.toJSON(items));
        return "warehouse/materialManage/editMaterialManage";
   }

    @RequiresPermissions("warehouse:materialManage:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody MaterialManage materialManage){
        if(StringUtils.isBlank(materialManage.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
         materialManageService.updateMaterialManage(materialManage);
        return ResponseEntity.success("操作成功");
    }
    
    /**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<MaterialManageDetail> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request,
			String manageId) {
		PageData<MaterialManageDetail> ContMaterialManageDetailData = new PageData<>();
		QueryWrapper<MaterialManageDetail> MaterialManageDetailWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		MaterialManageDetailWrapper.eq("manage_id", manageId);
		MaterialManageDetailWrapper.eq("del_flag", false);
		MaterialManageDetailWrapper.orderByDesc("create_date ");
		IPage<MaterialManageDetail> MaterialManageDetailPage = materialManageDetailService.page(new Page<>(page, limit),
				MaterialManageDetailWrapper);
		ContMaterialManageDetailData.setCount(MaterialManageDetailPage.getTotal());
		ContMaterialManageDetailData.setData(supplementToDetail(MaterialManageDetailPage.getRecords()));
		return ContMaterialManageDetailData;
	}

	private List<MaterialManageDetail> supplementToDetail(List<MaterialManageDetail> details) {
		details.forEach(r -> {
			if (StringUtils.isNotBlank(r.getItemId())) {// 品项
				Clientitem item = clientitemService.getById(r.getItemId());
				r.setRate(item.getUnitRate()+"");
				r.setItemName(item.getName());
				r.setItemCode(item.getCode());
				r.setNumZ(r.getNumber() / item.getUnitRate() + "." + r.getNumber() % item.getUnitRate());
			}
			if(StringUtils.isNotBlank(r.getMaterial())){
				//通过储位和物料共同找最大值
				MaterialDepot mDepot = materialDepotService.getMaterialDepotByMaterialIdAndDepotCode(r.getMaterial(), r.getDepot());
				if(mDepot!=null){
					r.setMaxNumber(mDepot.getNumber());
				}else{
					r.setMaxNumber(0);
				}
			}
		});
		return details;
	}
	@PostMapping("editDetail")
	@ResponseBody
	@SysLog("保存编辑明细出库数据")
	public ResponseEntity editDetail(@RequestBody MaterialManageDetail detail,Integer type) {
		if (StringUtils.isBlank(detail.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		String manageId = materialManageDetailService.getById(detail.getId()).getManageId();
		//只能改带确认状态得出库单
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		if(!modify_status_await.equals(materialManageService.getById(manageId).getModifyStatus())){
			return ResponseEntity.failure("该单据不在待确认状态无法修改");
		}
		try {
			materialManageDetailService.updateNumber(detail,materialManageService.getById(detail.getManageId()).getSystemCode(),type);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("操作失败");
		}
		return ResponseEntity.success("操作成功");
	}
	@PostMapping("deleteDetail")
	@ResponseBody
	@SysLog("删除明细数据")//
	public ResponseEntity deleteDetail(@RequestBody MaterialManageDetail detail,Integer type) {
		if (StringUtils.isBlank(detail.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		String manageId = materialManageDetailService.getById(detail.getId()).getManageId();
		//只能改带确认状态得出库单
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		if(!modify_status_await.equals(materialManageService.getById(manageId).getModifyStatus())){
			return ResponseEntity.failure("该单据不在待确认状态无法修改");
		}
		materialManageDetailService.deleteMaterialManageDetail(detail,materialManageService.getById(detail.getManageId()).getSystemCode(),type);
		return ResponseEntity.success("操作成功");
	}
}
