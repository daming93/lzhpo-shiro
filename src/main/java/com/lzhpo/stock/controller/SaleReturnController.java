package com.lzhpo.stock.controller;

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
import com.lzhpo.stock.entity.SaleReturn;
import com.lzhpo.stock.service.ISaleReturnService;
/**
 * <p>
 * 退货表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Controller
@RequestMapping("/sale-return")
public class SaleReturnController {
 	@Autowired
    private ISaleReturnService saleReturnService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "needChange/saleReturn/listSaleReturn";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("needChange:saleReturn:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<SaleReturn> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<SaleReturn> saleReturnPageData = new PageData<>();
        QueryWrapper<SaleReturn> saleReturnWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        saleReturnWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                saleReturnWrapper.like("name", keys);
            }
        }
        IPage<SaleReturn> saleReturnPage = saleReturnService.page(new Page<>(page,limit),saleReturnWrapper);
        saleReturnPageData.setCount(saleReturnPage.getTotal());
        saleReturnPageData.setData(setUserToSaleReturn(saleReturnPage.getRecords()));
        return saleReturnPageData;
    }
    //创建者，和修改人
   private List<SaleReturn> setUserToSaleReturn(List<SaleReturn> saleReturns){
        saleReturns.forEach(r -> {
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

        return saleReturns;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<SaleReturn> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "needChange/saleReturn/addSaleReturn";
    }

    @RequiresPermissions("needChange:saleReturn:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody SaleReturn saleReturn){
        saleReturnService.saveSaleReturn(saleReturn);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("needChange:saleReturn:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        SaleReturn saleReturn = saleReturnService.getSaleReturnById(id);
        saleReturnService.deleteSaleReturn(saleReturn);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("needChange:saleReturn:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<SaleReturn> saleReturns){
        if(saleReturns == null || saleReturns.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (SaleReturn r : saleReturns){
            saleReturnService.deleteSaleReturn(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       SaleReturn saleReturn =  saleReturnService.getSaleReturnById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("saleReturn", saleReturn);
      
        return "needChange/saleReturn/editSaleReturn";
   }

    @RequiresPermissions("needChange:saleReturn:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody SaleReturn saleReturn){
        saleReturnService.updateSaleReturn(saleReturn);
        return ResponseEntity.success("操作成功");
    }
}
