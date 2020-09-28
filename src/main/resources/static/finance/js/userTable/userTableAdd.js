Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};




    //layui 模块化引用
    layui.use(['jquery', 'table', 'layer'], function(){

       
        var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer,upload = layui.upload;

        var tableId =     document.getElementById("userTableId").value;   

        var submitFlag = false;

        var tableData=new Array(); // 用于存放表格数据
        $.ajax({
          url: "/finance/table/selectDetail?tableId="+tableId
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
        
        //定义事件集合
        var active = {
            save: function(){
                var oldData = table.cache[layTableId];  
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(!row.math){
                        layer.msg("检查每一行，请选择分类！", { icon: 5 }); //提示
                        return false;
                    }
                    if(!row.optionName){
                        layer.msg("检查每一行，请选择选项！", { icon: 5 }); //提示
                        return false;
                    } 
                }
                submitFlag = true;
            }
        }
        function clearNoNum(value) {
            value = value.replace(/[^\d.]/g, "");  //清除“数字”和“.”以外的字符   
            value = value.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的   
            value = value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
            value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3');//只能输入两个小数   
            if (value.indexOf(".") < 0 && value != "") {//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额  
               value = parseFloat(value);
            }
            return value;
        }
        //激活事件
        var activeByType = function (type, arg) {
            if(arguments.length === 2){                 
                active[type] ? active[type].call(this, arg) : '';
            }else{
                active[type] ? active[type].call(this) : '';
            }
        }
        
        //注册按钮事件
        $('.layui-btn[data-type]').on('click', function () {
            var type = $(this).data('type');
            activeByType(type);
        });
       
//提交数据代码
        form.on('submit(addtable)',function(data){
       //更新行记录对象
        data.field.detailSet = table.cache[layTableId];  
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/finance/userTable/add",
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

