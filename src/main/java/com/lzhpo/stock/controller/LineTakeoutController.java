package com.lzhpo.stock.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.Address;
import com.lzhpo.deliver.service.IAddressService;
import com.lzhpo.stock.entity.LineTakeout;
import com.lzhpo.stock.service.ILineTakeoutService;
import com.lzhpo.sys.entity.Dictionary;
/**
 * <p>
 * 线路发单（无关库存的发单) 表单形式和库存发单接近，但是没有子表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2021-01-25
 */
@RestController
@RequestMapping("/stock/lineTakeout")
public class LineTakeoutController {
 	@Autowired
    private ILineTakeoutService lineTakeoutService;

    @Autowired
    UserService userService;
    
    @Autowired
	private IBasicdataService basicdateService;

	
	
	@Autowired
	private IAddressService addressService;
    @GetMapping(value = "list")
    public String list(){
        return "stock/lineTakeout/listLineTakeout";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("stock:lineTakeout:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<LineTakeout> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<LineTakeout> lineTakeoutPageData = new PageData<>();
        QueryWrapper<LineTakeout> lineTakeoutWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        lineTakeoutWrapper.eq("del_flag",false);
        if (!map.isEmpty()) {//检索项
			String clientCode = (String) map.get("clientCode");
			if (StringUtils.isNotBlank(clientCode)) {
				lineTakeoutWrapper.like("client_code", clientCode);
			}
			String code = (String) map.get("code");
			if (StringUtils.isNotBlank(code)) {
				lineTakeoutWrapper.like("code", code);
			}
			String clientId = (String) map.get("clientId");
			if (StringUtils.isNotBlank(clientId)) {
				lineTakeoutWrapper.eq("client_id", clientId);
			}
			String startTime =(String) map.get("startTime");
			String overTime =(String) map.get("overTime");
			if (StringUtils.isNotBlank(startTime)) {
				lineTakeoutWrapper.ge("takeout_time", startTime);
			}
			if (StringUtils.isNotBlank(overTime)) {
				lineTakeoutWrapper.le("takeout_time", overTime);
			}
		}
        lineTakeoutWrapper.orderByAsc("status");
        lineTakeoutWrapper.orderByDesc("takeout_time");
        IPage<LineTakeout> lineTakeoutPage = lineTakeoutService.page(new Page<>(page,limit),lineTakeoutWrapper);
        lineTakeoutPageData.setCount(lineTakeoutPage.getTotal());
        lineTakeoutPageData.setData(setUserToLineTakeout(lineTakeoutPage.getRecords()));
        return lineTakeoutPageData;
    }
    //创建者，和修改人
   private List<LineTakeout> setUserToLineTakeout(List<LineTakeout> lineTakeouts){
	   lineTakeouts.forEach(r -> {
//			if (StringUtils.isNotBlank(r.getCreateId())) {
//				User u = userService.findUserById(r.getCreateId());
//				if (StringUtils.isBlank(u.getNickName())) {
//					u.setNickName(u.getLoginName());
//				}
//				r.setCreateUser(u);
//			}
//			if (StringUtils.isNotBlank(r.getUpdateId())) {
//				User u = userService.findUserById(r.getUpdateId());
//				if (StringUtils.isBlank(u.getNickName())) {
//					u.setNickName(u.getLoginName());
//				}
//				r.setUpdateUser(u);
//			}
			if (StringUtils.isNotBlank(r.getClientId())) {
				r.setClientName(basicdateService.getById(r.getClientId()).getClientShortName());
			}
			if (r.getStatus() != null) {
				r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "modify_status"));
			}
			if (r.getPickingStatus()!= null) {
				r.setPickStatusStr(CommomUtil.valueToNameInDict(r.getPickingStatus(), "is_exsit_pick"));
			}
			if (r.getTransportationType() != null) {
				r.setTransportationTypeStr(CommomUtil.valueToNameInDict(r.getTransportationType(), "transportation_type"));
			}
			if (StringUtils.isNotBlank(r.getAddressId())) {
				r.setAddressName(addressService.getById(r.getAddressId()).getAddressName());
			}
		});

		return lineTakeouts;
	}	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<LineTakeout> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap,@RequestParam(value = "continuity", required = false) String continuity){
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		
		List<Address> addressList = addressService.selectAll();
		modelMap.put("addressList", addressList);
		
		//配送方式 CacheUtils. transportation_type
		List<Dictionary> transportationTypeList =   CacheUtils.allDicts.get("transportation_type");
		modelMap.put("transportationTypeList", transportationTypeList);
		modelMap.put("deliverTypeList",  CacheUtils.allDicts.get("deliver_type"));
		modelMap.put("continuity", continuity);
        return "stock/lineTakeout/addLineTakeout";
    }

    @RequiresPermissions("stock:lineTakeout:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody LineTakeout lineTakeout){
    	ResponseEntity entity = new ResponseEntity();
		try {
			String id =  lineTakeoutService.saveLineTakeout(lineTakeout).getId();
			entity.setAny("id", id);
			boolean flag = SecurityUtils.getSubject().isPermitted("stock:takeout:edit");//返回有没有编辑得权限
			entity.setAny("flag", flag);
			entity.setSuccess(true);
			entity.setMessage("操作成功");
		} catch (RuntimeJsonMappingException e) {
			return ResponseEntity.failure(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("系统异常,请联系管理员处理");
		}
        lineTakeoutService.saveLineTakeout(lineTakeout);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("stock:lineTakeout:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("ID不能为空");
        }
        LineTakeout lineTakeout = lineTakeoutService.getLineTakeoutById(id);
        lineTakeoutService.deleteLineTakeout(lineTakeout);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("stock:lineTakeout:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<LineTakeout> lineTakeouts){
        if(lineTakeouts == null || lineTakeouts.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (LineTakeout r : lineTakeouts){
            lineTakeoutService.deleteLineTakeout(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       LineTakeout lineTakeout =  lineTakeoutService.getLineTakeoutById(id);
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		//配送方式 CacheUtils. transportation_type
		List<Dictionary> transportationTypeList =   CacheUtils.allDicts.get("transportation_type");
		modelMap.put("transportationTypeList", transportationTypeList);
		List<Address> addressList = addressService.selectAll();
		modelMap.put("addressList", addressList);
		modelMap.put("deliverTypeList",  CacheUtils.allDicts.get("deliver_type"));
        modelMap.put("lineTakeout", lineTakeout);
      
        return "stock/lineTakeout/editLineTakeout";
   }

    @RequiresPermissions("stock:lineTakeout:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody LineTakeout lineTakeout){
        if(StringUtils.isBlank(lineTakeout.getId())){
            return ResponseEntity.failure("id（不能为空)");
        }
        lineTakeoutService.updateLineTakeout(lineTakeout);
        return ResponseEntity.success("操作成功");
    }
	@RequiresPermissions("stock:lineTakeout:back")
	@PostMapping("back")
	@ResponseBody
	@SysLog("撤销数据")
	public ResponseEntity back(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("单据ID不能为空");
		}
		lineTakeoutService.backTakeout(id);
		return ResponseEntity.success("操作成功");
	}
}
