package com.lzhpo.deliver.controller;

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
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.Vehicle;
import com.lzhpo.deliver.service.IDriverService;
import com.lzhpo.deliver.service.IVehicleService;
import com.lzhpo.deliver.service.IVehicleTypeService;
/**
 * <p>
 * 车辆信息表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-08-21
 */
@Controller
@RequestMapping("deliver/vehicle")
public class VehicleController {
 	@Autowired
    private IVehicleService vehicleService;

    @Autowired
    UserService userService;
    
    @Autowired
    private IVehicleTypeService vehicleTypeService;
    
    @Autowired
    private IDriverService driverService ;

    @GetMapping(value = "list")
    public String list(){
        return "deliver/vehicle/listVehicle";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("deliver:vehicle:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<Vehicle> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<Vehicle> vehiclePageData = new PageData<>();
        QueryWrapper<Vehicle> vehicleWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        vehicleWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("licencePlate");
            if(StringUtils.isNotBlank(keys)) {
                vehicleWrapper.like("licence_plate", keys);
            }
        }
        IPage<Vehicle> vehiclePage = vehicleService.page(new Page<>(page,limit),vehicleWrapper);
        vehiclePageData.setCount(vehiclePage.getTotal());
        vehiclePageData.setData(setUserToVehicle(vehiclePage.getRecords()));
        return vehiclePageData;
    }
    //创建者，和修改人
   private List<Vehicle> setUserToVehicle(List<Vehicle> vehicles){
        vehicles.forEach(r -> {
        	if(r.getVehicleTypeId()!=null){
        		r.setTypeName(vehicleTypeService.getById(r.getVehicleTypeId()).getName());
        	}
        	if(r.getDriverId()!=null){
        		r.setDriverName(driverService.getById(r.getDriverId()).getDriverName());
        	}
        	if(r.getVehicleStatus()!=null){
        		r.setVehicleStatusStr(CommomUtil.valueToNameInDict(r.getVehicleStatus(), "vehicle_status"));
        	}
        });

        return vehicles;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<Vehicle> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
    	modelMap.put("typeList", vehicleTypeService.selectAll());
    	modelMap.put("driverList", driverService.selectAll());
    	modelMap.put("statusList", CacheUtils.allDicts.get("vehicle_status"));
        return "deliver/vehicle/addVehicle";
    }

    @RequiresPermissions("deliver:vehicle:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody Vehicle vehicle){
        if(StringUtils.isBlank(vehicle.getLicencePlate())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(vehicleService.getVehicleCount(vehicle.getLicencePlate())>0){
             return ResponseEntity.failure("修改提示信息（不能重复)");
        }
        
        vehicleService.saveVehicle(vehicle);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("deliver:vehicle:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        Vehicle vehicle = vehicleService.getVehicleById(id);
        vehicleService.deleteVehicle(vehicle);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("deliver:vehicle:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<Vehicle> vehicles){
        if(vehicles == null || vehicles.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (Vehicle r : vehicles){
            vehicleService.deleteVehicle(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       Vehicle vehicle =  vehicleService.getVehicleById(id);
        modelMap.put("typeList", vehicleTypeService.selectAll());
        modelMap.put("vehicle", vehicle);
        modelMap.put("statusList", CacheUtils.allDicts.get("vehicle_status"));
        modelMap.put("driverList", driverService.selectAll());
        return "deliver/vehicle/editVehicle";
   }

    @RequiresPermissions("deliver:vehicle:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody Vehicle vehicle){
        if(StringUtils.isBlank(vehicle.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(StringUtils.isBlank(vehicle.getLicencePlate())){
            return ResponseEntity.failure("角色名称不能为空");
        }
        Vehicle oldVehicle =  vehicleService.getVehicleById(vehicle.getId());
        if(!oldVehicle.getLicencePlate().equals(vehicle.getLicencePlate())){
             if( vehicleService.getVehicleCount(vehicle.getLicencePlate())>0){
                return ResponseEntity.failure("修改提示信息（不能重复)");
            }
        }
         vehicleService.updateVehicle(vehicle);
        return ResponseEntity.success("操作成功");
    }
}
