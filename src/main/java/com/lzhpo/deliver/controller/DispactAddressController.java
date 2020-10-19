package com.lzhpo.deliver.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.DispactAddress;
import com.lzhpo.deliver.entity.Dispatch;
import com.lzhpo.deliver.service.IDispactAddressService;
import com.lzhpo.deliver.service.IDispatchService;
import com.lzhpo.sys.service.ITerritoryService;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
@Controller
@RequestMapping("deliver/dispactAddress")
public class DispactAddressController {
 	@Autowired
    private IDispactAddressService dispactAddressService;

	@Autowired
    private IDispatchService dispatchService;
 	
    @Autowired
    UserService userService;

    @Autowired
    ITerritoryService territoryService;
    
    @GetMapping(value = "list")
    public String list(){
        return "deliver/dispactAddress/listDispactAddress";
    }
	
    /**
     * 查询分页数据（待选择表中的快速出单）
     */
    @PostMapping("list")
    @ResponseBody
    public PageData<DispactAddress> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        if(!map.isEmpty()){
            String waitList = (String) map.get("waitList");
            if(StringUtils.isNotBlank(waitList)) {
                String[] arr = waitList.split(",");
                List<String> list = new ArrayList<>();
                for (String string : arr) {
                	list.add(string);
				}
                if(!list.isEmpty()){
                	map.put("waitedList", list);
                }
            }
        }
        PageData<DispactAddress> dispactAddressPageData = new PageData<>();
        dispactAddressPageData.setData(setUserToDispactAddress(dispactAddressService.getDispactWaitForDeliverBill(map)));
        dispactAddressPageData.setCount(1000l);
        return dispactAddressPageData ;
    }
    /**
     * 查询分页数据（待选择表中的库存出单）
     */
    @PostMapping("listTakout")
    @ResponseBody
    public PageData<DispactAddress> listTakout(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        if(!map.isEmpty()){
            String waitList = (String) map.get("waitList");
            if(StringUtils.isNotBlank(waitList)) {
                String[] arr = waitList.split(",");
                List<String> list = new ArrayList<>();
                for (String string : arr) {
                	list.add(string);
				}
                if(!list.isEmpty()){
                	map.put("waitedList", list);
                }
            }
        }
        PageData<DispactAddress> dispactAddressPageData = new PageData<>();
        dispactAddressPageData.setData(setUserToDispactAddress(dispactAddressService.getDispactWaitForTakeoutBill(map)));
        dispactAddressPageData.setCount(1000l);
        return dispactAddressPageData ;
    }
    
    /**
     * 查询分页数据（配送计划得明细）
     */
    @PostMapping("listByDispacthId")
    @ResponseBody
    public PageData<DispactAddress> listByDispacthId(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "1000")Integer limit,String id,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<DispactAddress> dispactAddressPageData = new PageData<>();
        QueryWrapper<DispactAddress> addressWrapper = new QueryWrapper<>();
        addressWrapper.eq("dispacth_id", id);
        IPage<DispactAddress> addressPage = dispactAddressService.page(new Page<>(page, limit), addressWrapper);
        dispactAddressPageData.setData(setUserToDispactAddress(addressPage.getRecords()));
        dispactAddressPageData.setCount(addressPage.getTotal());
        return dispactAddressPageData ;
    }
    /**
     * 查询分页数据(根据路单得id查，使用在路单得详情页面）
     */
    @PostMapping("listByWayBillId")
    @ResponseBody
    public PageData<DispactAddress> listByWayBillId(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "1000")Integer limit,String id,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        List<String> dispatchIds = new ArrayList<>();
        List<Dispatch> mainList = dispatchService.selectByWayBillId(id);
        for (Dispatch dispatch : mainList) {
        	dispatchIds.add(dispatch.getId());
		}
        PageData<DispactAddress> dispactAddressPageData = new PageData<>();
        QueryWrapper<DispactAddress> addressWrapper = new QueryWrapper<>();
        addressWrapper.in("dispacth_id", dispatchIds);
        IPage<DispactAddress> addressPage = dispactAddressService.page(new Page<>(page, limit), addressWrapper);
        dispactAddressPageData.setData(setUserToDispactAddress(addressPage.getRecords()));
        dispactAddressPageData.setCount(addressPage.getTotal());
        return dispactAddressPageData ;
    }
    //创建者，和修改人
   private List<DispactAddress> setUserToDispactAddress(List<DispactAddress> dispactAddresss){
        dispactAddresss.forEach(r -> {
        	String proviceName = "";
        	String city = "";
        	String area = "";
        	if(r.getProvinceId()!=null){
        		proviceName = territoryService.getById(r.getProvinceId()).getName();
        	}
        	if(r.getCityId()!=null){
        		city = territoryService.getById(r.getCityId()).getName();
        	}
        	if(r.getCountiesId()!=null){
        		area = territoryService.getById(r.getCountiesId()).getName();
        	}
        	switch (r.getType()) {
			case 1:
				r.setTypeStr("快速发单");
				break;
			case 2:
				r.setTypeStr("库存发单");
				break;
			case 3:
				r.setTypeStr("快速发单拆单");
				break;
			case 4:
				r.setTypeStr("库存发单拆单");
				break;
			default:
				break;
			}
        	r.setCountiesName(proviceName+city+area);
        });

        return dispactAddresss;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<DispactAddress> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "deliver/dispactAddress/addDispactAddress";
    }

    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody DispactAddress dispactAddress){
        dispactAddressService.saveDispactAddress(dispactAddress);
        return ResponseEntity.success("操作成功");
    }
 
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        DispactAddress dispactAddress = dispactAddressService.getDispactAddressById(id);
        dispactAddressService.deleteDispactAddress(dispactAddress);
        return ResponseEntity.success("操作成功");
    }
 
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<DispactAddress> dispactAddresss){
        if(dispactAddresss == null || dispactAddresss.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (DispactAddress r : dispactAddresss){
            dispactAddressService.deleteDispactAddress(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       DispactAddress dispactAddress =  dispactAddressService.getDispactAddressById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("dispactAddress", dispactAddress);
      
        return "deliver/dispactAddress/editDispactAddress";
   }

    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody DispactAddress dispactAddress){
        if(StringUtils.isBlank(dispactAddress.getId())){
            return ResponseEntity.failure("id（不能为空)");
        }
         dispactAddressService.updateDispactAddress(dispactAddress);
        return ResponseEntity.success("操作成功");
    }
    
    @GetMapping("spilt")
    public String toSpilt(String id,ModelMap modelMap,Integer type ){
    	if(type!=null){
    		if(type==1){//库存发单
        		modelMap.put("dispactAddress", dispactAddressService.getDispactAddressByTakoutId(id));
        	}else{//快速发单
        		modelMap.put("dispactAddress", dispactAddressService.getDispactAddressByBillId(id));
        	}
    		modelMap.put("type",type);
    	}
        return "deliver/dispatch/editSpilt";
   }
    
    @PostMapping("spilt")
    @ResponseBody
    @SysLog("拆单")
    public ResponseEntity spilt(@RequestBody DispactAddress dispactAddress){
        if(StringUtils.isBlank(dispactAddress.getId())){
            return ResponseEntity.failure("id（不能为空)");
        }
        dispactAddressService.splitBill(dispactAddress);
        return ResponseEntity.success("操作成功");
    }
    
    @GetMapping("deleteDetail")
    @ResponseBody
    @SysLog("删除配送计划的明细")
    public ResponseEntity changeBindingStatus(String dispactAddressId){
        dispactAddressService.changeBindingStatus(dispactAddressId);
        return ResponseEntity.success("操作成功");
    }
    
    @PostMapping("bindingStatus")
    @ResponseBody
    @SysLog("运输计划编辑页面的绑定操作")
    public ResponseEntity bindingStatus(@RequestBody DispactAddress dispactAddress){
        String message = dispactAddressService.bindingStatus(dispactAddress);
        return ResponseEntity.success(message);
    }
    
}
