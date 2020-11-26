package com.lzhpo.customer.controller;

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
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.customer.entity.CustomerServiceResponsibleDepartment;
import com.lzhpo.customer.service.ICustomerServiceResponsibleDepartmentService;
/**
 * <p>
 * 客服责任部门名 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
@Controller
@RequestMapping("customer/customerServiceResponsibleDepartment")
public class CustomerServiceResponsibleDepartmentController {
 	@Autowired
    private ICustomerServiceResponsibleDepartmentService customerServiceResponsibleDepartmentService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "customer/customerServiceResponsibleDepartment/listCustomerServiceResponsibleDepartment";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("customer:customerServiceResponsibleDepartment:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<CustomerServiceResponsibleDepartment> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<CustomerServiceResponsibleDepartment> customerServiceResponsibleDepartmentPageData = new PageData<>();
        QueryWrapper<CustomerServiceResponsibleDepartment> customerServiceResponsibleDepartmentWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        customerServiceResponsibleDepartmentWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                customerServiceResponsibleDepartmentWrapper.like("name", keys);
            }
        }
        IPage<CustomerServiceResponsibleDepartment> customerServiceResponsibleDepartmentPage = customerServiceResponsibleDepartmentService.page(new Page<>(page,limit),customerServiceResponsibleDepartmentWrapper);
        customerServiceResponsibleDepartmentPageData.setCount(customerServiceResponsibleDepartmentPage.getTotal());
        customerServiceResponsibleDepartmentPageData.setData(setUserToCustomerServiceResponsibleDepartment(customerServiceResponsibleDepartmentPage.getRecords()));
        return customerServiceResponsibleDepartmentPageData;
    }
    //创建者，和修改人
   private List<CustomerServiceResponsibleDepartment> setUserToCustomerServiceResponsibleDepartment(List<CustomerServiceResponsibleDepartment> customerServiceResponsibleDepartments){
        customerServiceResponsibleDepartments.forEach(r -> {
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

        return customerServiceResponsibleDepartments;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<CustomerServiceResponsibleDepartment> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "customer/customerServiceResponsibleDepartment/addCustomerServiceResponsibleDepartment";
    }

    @RequiresPermissions("customer:customerServiceResponsibleDepartment:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment){
        if(StringUtils.isBlank(customerServiceResponsibleDepartment.getName())){
            return ResponseEntity.failure("名称（不能为空)");
        }
        if(customerServiceResponsibleDepartmentService.getCustomerServiceResponsibleDepartmentCount(customerServiceResponsibleDepartment.getName())>0){
             return ResponseEntity.failure("名称（不能重复)");
        }
        customerServiceResponsibleDepartmentService.saveCustomerServiceResponsibleDepartment(customerServiceResponsibleDepartment);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("customer:customerServiceResponsibleDepartment:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment = customerServiceResponsibleDepartmentService.getCustomerServiceResponsibleDepartmentById(id);
        customerServiceResponsibleDepartmentService.deleteCustomerServiceResponsibleDepartment(customerServiceResponsibleDepartment);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("customer:customerServiceResponsibleDepartment:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<CustomerServiceResponsibleDepartment> customerServiceResponsibleDepartments){
        if(customerServiceResponsibleDepartments == null || customerServiceResponsibleDepartments.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (CustomerServiceResponsibleDepartment r : customerServiceResponsibleDepartments){
            customerServiceResponsibleDepartmentService.deleteCustomerServiceResponsibleDepartment(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment =  customerServiceResponsibleDepartmentService.getCustomerServiceResponsibleDepartmentById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("customerServiceResponsibleDepartment", customerServiceResponsibleDepartment);
      
        return "customer/customerServiceResponsibleDepartment/editCustomerServiceResponsibleDepartment";
   }

    @RequiresPermissions("customer:customerServiceResponsibleDepartment:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment){
        if(StringUtils.isBlank(customerServiceResponsibleDepartment.getId())){
            return ResponseEntity.failure("关键信息（不能为空)");
        }
        if(StringUtils.isBlank(customerServiceResponsibleDepartment.getName())){
            return ResponseEntity.failure("名称不能为空");
        }
        CustomerServiceResponsibleDepartment oldCustomerServiceResponsibleDepartment =  customerServiceResponsibleDepartmentService.getCustomerServiceResponsibleDepartmentById(customerServiceResponsibleDepartment.getId());
        if(!oldCustomerServiceResponsibleDepartment.getName().equals(customerServiceResponsibleDepartment.getName())){
             if( customerServiceResponsibleDepartmentService.getCustomerServiceResponsibleDepartmentCount(customerServiceResponsibleDepartment.getName())>0){
                return ResponseEntity.failure("名称（不能重复)");
            }
        }
         customerServiceResponsibleDepartmentService.updateCustomerServiceResponsibleDepartment(customerServiceResponsibleDepartment);
        return ResponseEntity.success("操作成功");
    }
}
