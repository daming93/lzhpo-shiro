package ${package.Controller};

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;

</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
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
 * ${table.comment!} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
@RequestMapping("/needChange/${entity}")
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>
 	@Autowired
    private ${table.serviceName} ${entity?uncap_first}Service;

    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "needChange/${entity?uncap_first}/list${entity}";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("needChange:${entity?uncap_first}:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<${entity}> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<${entity}> ${entity?uncap_first}PageData = new PageData<>();
        QueryWrapper<${entity}> ${entity?uncap_first}Wrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        ${entity?uncap_first}Wrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                ${entity?uncap_first}Wrapper.like("name", keys);
            }
        }
        IPage<${entity}> ${entity?uncap_first}Page = ${entity?uncap_first}Service.page(new Page<>(page,limit),${entity?uncap_first}Wrapper);
        ${entity?uncap_first}PageData.setCount(${entity?uncap_first}Page.getTotal());
        ${entity?uncap_first}PageData.setData(setUserTo${entity}(${entity?uncap_first}Page.getRecords()));
        return ${entity?uncap_first}PageData;
    }
    //创建者，和修改人
   private List<${entity}> setUserTo${entity}(List<${entity}> ${entity?uncap_first}s){
        ${entity?uncap_first}s.forEach(r -> {
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

        return ${entity?uncap_first}s;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<${entity}> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	

    @GetMapping("add")
    public String add(ModelMap modelMap){
        /**
	*自定义传入add页面的数据
	*/
        return "needChange/${entity?uncap_first}/add${entity}";
    }

    @RequiresPermissions("needChange:${entity?uncap_first}:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody ${entity} ${entity?uncap_first}){
        if(StringUtils.isBlank(${entity?uncap_first}.getName())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(${entity?uncap_first}Service.get${entity}Count(${entity?uncap_first}.getName())>0){
             return ResponseEntity.failure("修改提示信息（不能重复)");
        }
        ${entity?uncap_first}Service.save${entity}(${entity?uncap_first});
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("needChange:${entity?uncap_first}:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        ${entity} ${entity?uncap_first} = ${entity?uncap_first}Service.get${entity}ById(id);
        ${entity?uncap_first}Service.delete${entity}(${entity?uncap_first});
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("needChange:${entity?uncap_first}:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<${entity}> ${entity?uncap_first}s){
        if(${entity?uncap_first}s == null || ${entity?uncap_first}s.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (${entity} r : ${entity?uncap_first}s){
            ${entity?uncap_first}Service.delete${entity}(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       ${entity} ${entity?uncap_first} =  ${entity?uncap_first}Service.get${entity}ById(id);
       /**
       *自定义代码
       */
        Map<String,Object> map = new HashMap();
        modelMap.put("${entity?uncap_first}", ${entity?uncap_first});
      
        return "needChange/${entity?uncap_first}/edit${entity}";
   }

    @RequiresPermissions("needChange:${entity?uncap_first}:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody ${entity} ${entity?uncap_first}){
        if(StringUtils.isBlank(${entity?uncap_first}.getId())){
            return ResponseEntity.failure("修改提示信息（不能为空)");
        }
        if(StringUtils.isBlank(${entity?uncap_first}.getName())){
            return ResponseEntity.failure("角色名称不能为空");
        }
        ${entity} old${entity} =  ${entity?uncap_first}Service.get${entity}ById(${entity?uncap_first}.getId());
        if(!old${entity}.getName().equals(${entity?uncap_first}.getName())){
             if( ${entity?uncap_first}Service.get${entity}Count(${entity?uncap_first}.getName())>0){
                return ResponseEntity.failure("修改提示信息（不能重复)");
            }
        }
         ${entity?uncap_first}Service.update${entity}(${entity?uncap_first});
        return ResponseEntity.success("操作成功");
    }
}
</#if>
