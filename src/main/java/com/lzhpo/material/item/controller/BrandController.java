package com.lzhpo.material.item.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.springframework.web.bind.annotation.RestController;

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

import com.lzhpo.material.item.entity.Brand;
import com.lzhpo.material.item.mapper.BrandMapper;
import com.lzhpo.material.item.service.IBrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
/**
 * <p>
 * 物料品牌表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-04-15
 */
@Controller
@RequestMapping("/item/brand")
public class BrandController {
 	@Autowired
    private IBrandService brandService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "material/item/brand/listBrand";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("item:brand:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<Brand> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<Brand> brandPageData = new PageData<>();
        QueryWrapper<Brand> brandWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        brandWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                brandWrapper.like("name", keys);
            }
        }
        IPage<Brand> brandPage = brandService.page(new Page<>(page,limit),brandWrapper);
        brandPageData.setCount(brandPage.getTotal());
        brandPageData.setData(setUserToBrand(brandPage.getRecords()));
        return brandPageData;
    }
    //创建者，和修改人
   private List<Brand> setUserToBrand(List<Brand> brands){
        brands.forEach(r -> {
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

        return brands;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<Brand> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "material/item/brand/addBrand";
    }

    @RequiresPermissions("item:brand:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody Brand brand){
        if(StringUtils.isBlank(brand.getName())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(brandService.getBrandCount(brand.getName())>0){
             return ResponseEntity.failure("修改提示信息（不能重复)");
        }
        brandService.saveBrand(brand);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("item:brand:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        Brand brand = brandService.getBrandById(id);
        brandService.deleteBrand(brand);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("item:brand:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<Brand> brands){
        if(brands == null || brands.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (Brand r : brands){
            brandService.deleteBrand(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       Brand brand =  brandService.getBrandById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("brand", brand);
      
        return "material/item/brand/editBrand";
   }

    @RequiresPermissions("item:brand:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody Brand brand){
        if(StringUtils.isBlank(brand.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(StringUtils.isBlank(brand.getName())){
            return ResponseEntity.failure("角色名称不能为空");
        }
        Brand oldBrand =  brandService.getBrandById(brand.getId());
        if(!oldBrand.getName().equals(brand.getName())){
             if( brandService.getBrandCount(brand.getName())>0){
                return ResponseEntity.failure("修改提示信息（不能重复)");
            }
        }
         brandService.updateBrand(brand);
        return ResponseEntity.success("操作成功");
    }
}
