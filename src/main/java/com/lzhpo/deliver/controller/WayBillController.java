package com.lzhpo.deliver.controller;

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
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.config.MySysUser;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.Dispatch;
import com.lzhpo.deliver.entity.WayBill;
import com.lzhpo.deliver.service.IWayBillService;
import com.lzhpo.finance.service.ITableService;
import com.lzhpo.finance.service.IUserTableService;
import com.lzhpo.stock.service.IHandleAbnormityService;
import com.lzhpo.sys.service.IUserSettingService;
/**
 * <p>
 * 路单(和计划表基本一样，是计划表得主表，和统计内容) 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-10-14
 */
@Controller
@RequestMapping("deliver/wayBill")
public class WayBillController {
 	@Autowired
    private IWayBillService wayBillService;

    @Autowired
    UserService userService;

	@Autowired
	private IUserSettingService userSettingService; 
 	
	@Autowired
    private IUserTableService userTableService;
	
	@Autowired
    private ITableService tableService;
	
	@Autowired
	private IHandleAbnormityService handleAbnormityService;
    @GetMapping(value = "list")
    public String list(ModelMap modelMap){
    	// 自定义附表
		Integer user_setting_table = CacheUtils.keyDict.get("user_setting_table").getValue();
		//快速发单模块
		Integer modular_way_bill = CacheUtils.keyDict.get("modular_way_bill").getValue();
		
		modelMap.put("tableList", tableService.selectListByModular(modular_way_bill));
		String userId = MySysUser.id();
		modelMap.put("modular", userSettingService.getUserSettingByUserId(userId, modular_way_bill, user_setting_table));
        return "deliver/wayBill/listWayBill";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("deliver:wayBill:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<WayBill> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<WayBill> wayBillPageData = new PageData<>();
        QueryWrapper<WayBill> wayBillWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        wayBillWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                wayBillWrapper.like("name", keys);
            }
        }
        IPage<WayBill> wayBillPage = wayBillService.page(new Page<>(page,limit),wayBillWrapper);
        wayBillPageData.setCount(wayBillPage.getTotal());
        wayBillPageData.setData(setUserToWayBill(wayBillPage.getRecords()));
        return wayBillPageData;
    }
    //创建者，和修改人
   private List<WayBill> setUserToWayBill(List<WayBill> wayBills){
        wayBills.forEach(r -> {
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
            r.setUserTable(userTableService.getUserTableByuserTableId(r.getId()));
        });

        return wayBills;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<WayBill> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "deliver/wayBill/addWayBill";
    }

  //  @RequiresPermissions("deliver:wayBill:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据(录单)")
	public ResponseEntity add(@RequestBody List<Dispatch> dispatchs) {
		if (dispatchs == null || dispatchs.size() == 0) {
			return ResponseEntity.failure("请选择需要录入得单据");
		}
		//这里作i一个基本得验证，一个是单据是否已经被排过 一个是是否所有得拆单都在一个路单中
		switch (wayBillService.verifyWayBill(dispatchs)) {
		case 1://有排过单得了
			return ResponseEntity.failure("列表中有已排单据");
		case 2://有拆单未放一起
			return ResponseEntity.failure("列表中有拆单未全部排入一个路单中");
		default:
			try {
				wayBillService.saveWayBill(dispatchs);
			} catch (RuntimeJsonMappingException e) {
				return ResponseEntity.failure(e.getMessage());
			}
			return ResponseEntity.success("操作成功");
		}
		
	}
    @RequiresPermissions("deliver:wayBill:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("解散路单")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        if(handleAbnormityService.getHandleAbnormityCountBywaybillId(id)>0){
            return ResponseEntity.failure("该单据存在异常,不能解散");
        }
        WayBill wayBill = wayBillService.getWayBillById(id);
        wayBillService.deleteWayBill(wayBill);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("deliver:wayBill:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选解散路单")
    public ResponseEntity deleteSome(@RequestBody List<WayBill> wayBills){
        if(wayBills == null || wayBills.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (WayBill r : wayBills){
        	if(handleAbnormityService.getHandleAbnormityCountBywaybillId(r.getId())>0){
                return ResponseEntity.failure("该单据("+r.getCode()+")存在异常,不能解散");
            }
        }
        for (WayBill r : wayBills){
            wayBillService.deleteWayBill(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       WayBill wayBill =  wayBillService.getWayBillById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("wayBill", wayBill);
      
        return "deliver/wayBill/editWayBill";
   }

    @RequiresPermissions("deliver:wayBill:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody WayBill wayBill){
        if(StringUtils.isBlank(wayBill.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        WayBill oldWayBill =  wayBillService.getWayBillById(wayBill.getId());
        
        wayBillService.updateWayBill(wayBill);
        return ResponseEntity.success("操作成功");
    }
}
