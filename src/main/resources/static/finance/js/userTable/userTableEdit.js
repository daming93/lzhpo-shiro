Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
    //layui 模块化引用
    layui.use(['jquery', 'table', 'layer'], function(){

       
        var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer,upload = layui.upload;

        var tableId =     document.getElementById("tableId").value;   

        var submitFlag = false;

        var tableData=new Array(); // 用于存放表格数据 //注意的是这里的和add中使用的是不同的子表url
        $.ajax({
          url: "/finance/userTable/selectDetail?tableId="+tableId
          ,type:"get"
          ,async:false
          ,dataType:"json"
          , success: function(result){
              tableData=result.data;
            }
        });
  
         
        //数据表格实例化           
        var tbWidth = $("#tableRes").width();
        var layTableId = "layTable";
        var tableIns = table.render({
            elem: '#dataTable',
            id: layTableId,
            data: tableData,
            width: tbWidth,
            page: false,
            loading: true,
            even: false, //不开启隔行背景
            limit: Number.MAX_VALUE, // 数据表格默认全部显示
            cols: [[
                {title: '序号', type: 'numbers'},
                {field: 'optionName', title: '选项'},
                {field: 'mathStr', title: '收入类型'},
                {field: 'defaultMoney',title: '默认金额', edit: 'number' ,event:"checkTest"},
            ]],
            done: function(res, curr, count){
              //  viewObj.tbData = res.data;
            }
        });
         //监听单元格编辑
          table.on('edit(dataTable)', function(obj){
            var value = obj.value //得到修改后的值
            ,data = obj.data //得到所在行所有键值
            ,field = obj.field; //得到字段
          
            //  获取单元格编辑之前td的选择器
            var selector = obj.tr.selector+' td[data-field="'+obj.field+'"] div';
            // 单元格编辑之前的值
            var oldtext = $(selector).text();
            if(field=="defaultMoney"){
                if(value<0){
                  $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                     obj.update({
                      defaultMoney:oldtext,
                    });
                   layer.msg("不得小于0");
                  
                   return;
                }
                obj.update({
                      defaultMoney:value,
                    });
            }
            $.ajax({
                type:"POST",
                url:"/finance/userTable/editDetail",
                dataType:"json",
                contentType:"application/json",
                data: JSON.stringify(obj.data) ,
                success:function(res){
                    if(res.success){
                        
                        layer.msg('金额更改为：'+ value);
                    }else{
                        $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                        layer.msg(res.message);
                    }
                }
               });
          });
  
       
//提交数据代码
        form.on('submit(edittable)',function(data){
       //更新行记录对象
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/finance/userTable/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("表单添加成功！",{time:1000},function(){
                        //刷新父页面
                        parent.location.reload();
                    });
                }else{
                    layer.msg(res.message);
                }
            }
        });
        return false;
    });
    });

