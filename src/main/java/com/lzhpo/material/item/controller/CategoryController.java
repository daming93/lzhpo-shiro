package com.lzhpo.material.item.controller;

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
import com.lzhpo.material.item.entity.Category;
import com.lzhpo.material.item.service.ICategoryService;
/**
 * <p>
 * 物料分类表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-04-15
 */
@Controller
@RequestMapping("/item/category")
public class CategoryController {
 	@Autowired
    private ICategoryService categoryService;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "material/item/category/listCategory";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("item:category:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<Category> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<Category> categoryPageData = new PageData<>();
        QueryWrapper<Category> categoryWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        categoryWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                categoryWrapper.like("name", keys);
            }
        }
        IPage<Category> categoryPage = categoryService.page(new Page<>(page,limit),categoryWrapper);
        categoryPageData.setCount(categoryPage.getTotal());
        categoryPageData.setData(setUserToCategory(categoryPage.getRecords()));
        return categoryPageData;
    }
    //创建者，和修改人
   private List<Category> setUserToCategory(List<Category> categorys){
        categorys.forEach(r -> {
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

        return categorys;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<Category> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "material/item/category/addCategory";
    }

    @RequiresPermissions("item:category:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody Category category){
        if(StringUtils.isBlank(category.getName())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(categoryService.getCategoryCount(category.getName())>0){
             return ResponseEntity.failure("修改提示信息（不能重复)");
        }
        categoryService.saveCategory(category);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("item:category:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("类别ID不能为空");
        }
        Category category = categoryService.getCategoryById(id);
        categoryService.deleteCategory(category);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("item:category:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<Category> categorys){
        if(categorys == null || categorys.size()==0){
            return ResponseEntity.failure("请选择需要删除的类别");
        }
        for (Category r : categorys){
            categoryService.deleteCategory(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       Category category =  categoryService.getCategoryById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("category", category);
      
        return "material/item/category/editCategory";
   }

    @RequiresPermissions("item:category:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody Category category){
        if(StringUtils.isBlank(category.getName())){
            return ResponseEntity.failure("类别名称不能为空");
        }
        Category oldCategory =  categoryService.getCategoryById(category.getId());
        if(!oldCategory.getName().equals(category.getName())){
             if( categoryService.getCategoryCount(category.getName())>0){
                return ResponseEntity.failure("类别名（不能重复)");
            }
        }
         categoryService.updateCategory(category);
        return ResponseEntity.success("操作成功");
    }
}
