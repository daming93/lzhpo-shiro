Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  
  //执行一个laydate实例
  laydate.render({
    elem: '#startTime' //指定元素
     
  });
  var laydate2 = layui.laydate;
  
  //执行一个laydate实例
  laydate2.render({
    elem: '#overTime' //指定元素
  });
});
window.viewObj = {
        tbData: [{
            id: new Date().valueOf(),
            provinceName:"",
            cityName:"",
            areaName:"",
            minNumber:"",
            maxNumber:"",
            money:"",
            provinceId:"",
            cityId:"",
            areaId:"" 
        }],
    };
    
    //layui 模块化引用
    layui.use(['jquery', 'table','upload', 'layer'], function(){

        var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer,upload = layui.upload;
              //回车操作
        form.on('select(clientId)', function(data){
            $("#clientName").val(data.elem[data.elem.selectedIndex].text);
        })
   
        form.on('select(provinceId)', function(data){
            $("#cityId").empty();
            $("#countiesId").empty();
            //data.value 得到被选中的值
             var url = '/sys/territory/selectCityByProvinceCode?id=' + data.value;
             $.get(url,function(data){
                $("#cityId").empty();
                $("#cityId").append(new Option("",""));
                $.each(data,function(index,item){
                    $("#cityId").append(new Option(item.name,item.id));
                });
                layui.form.render("select");
                $("#cityId").parent().find('input:first').click();
                $("#cityId").parent().find('input:first').focus();
             });
         
        });
        form.on('select(cityId)', function(data){
            $("#countiesId").empty();
             var url = '/sys/territory/selectAreaByCityCode?id=' + data.value;
             $.get(url,function(data){
                $("#countiesId").empty();
                $("#countiesId").append(new Option("",""));
                $.each(data,function(index,item){
                    $("#countiesId").append(new Option(item.name,item.id));
                });
                layui.form.render("select");
                $("#countiesId").parent().find('input:first').click();
                $("#countiesId").parent().find('input:first').focus();
             });
   
        });
         form.on('select(countiesId)', function(data){
             $("#moneyType").parent().find('input:first').click();
             $("#moneyType").parent().find('input:first').focus();
        }); 
         form.on('select(moneyType)', function(data){
            $("#minNumber").focus(); 
        });  
         
        $("#minNumber").keypress(function(e) {
            if (e.which == 13) {
              $("#maxNumber").focus();
            }
        });
        $("#maxNumber").keypress(function(e) {
            if (e.which == 13) {
              $("#number").focus();
            }
        });
        $("#number").keypress(function(e) {
            if (e.which == 13) {
                $("#detailType").parent().find('input:first').click();
                $("#detailType").parent().find('input:first').focus();
           
            }

        });
        form.on('select(detailType)', function(data){
               activeByType('addRow');
               //清空部分输入框
               $("#number").val("");
               $("#maxNumber").val("");              
               $("#minNumber").val("");                
               $("#provinceId").parent().find('input:first').val("");
               $("#countiesId").parent().find('input:first').val("");
               $("#cityId").parent().find('input:first').val("");
        }); 
        $("form").keypress(function(e) {
             if(e.keyCode==10&&e.ctrlKey) {
                $("#adddeliverContractMain").click();
            }
        });
        var deliverContractMainId =     document.getElementById("deliverContractMainId").value;   
        var tableData=new Array(); // 用于存放表格数据
        $.ajax({
          url: "/client/deliverContractMain/selectDetail?contractId="+deliverContractMainId
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
            data:tableData,
            width: tbWidth,
            page: false,
            loading: true,
            even: false, //不开启隔行背景
            limit: 2000, // 数据表格默认全部显示
            cols: [[
                 {title: '序号', type: 'numbers'},
                 { field:'provinceName',title:'省',align:'center',width:100},
                 { field:'cityName',title:'市',align:'center',width:100},
                 { field:'areaName',title:'区',align:'center',width:100},
                 { field:'moneyType',title:'收入方式值',align:'center',hide:true,width:100},
                 { field:'moneyTypeName',title:'收入方式',align:'center',width:100},
                 { field:'minNumber',title:'最小值',edit: 'select',align:'center',width:100},
                 { field:'maxNumber',title:'最大值',edit: 'select',align:'center',width:100},
                 { field:'money',title:'单价',edit: 'select',align:'center',width:100},
                 { field:'provinceId',title:'省id',align:'center',hide:true,width:100},
                 { field:'type',title:'类型',align:'center',hide:true,width:100},
                 { field:'typeName',title:'类型名称',align:'center',width:100},
                 { field:'cityId',title:'市id',align:'center',hide:true,width:100},
                 { field:'areaId',title:'区id',align:'center',hide:true,width:100},
                 {field: 'id', title: '操作', width:70,templet: function(d){
                    return '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del" lay-id="'+ d.id +'"><i class="layui-icon layui-icon-delete"></i>移除</a>';
                }}
            ]],
            done: function(res, curr, count){
             
            }
        });
        table.on('edit(dataTable)', function(obj){
            var value = obj.value //得到修改后的值
            ,data = obj.data //得到所在行所有键值
            ,field = obj.field; //得到字段
          
            //  获取单元格编辑之前td的选择器
            var selector = obj.tr.selector+' td[data-field="'+obj.field+'"] div';
            // 单元格编辑之前的值
            var oldtext = $(selector).text();

            if( field=="minNumber"){
                 if(!( /^\d+(\.\d{0,2})?$/.test(value))){
                    layer.msg("请输入正确最小值！");
                    $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                    return;
                }
                if(value>=obj.data.maxNumber){
                    layer.msg("最大值应该大于最小值");
                    $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                    return;
                }
                obj.update({
                      minNumber:value,
                });
            }
            if( field=="maxNumber"){
                 if(!( /^\d+(\.\d{0,2})?$/.test(value))){
                    layer.msg("请输入正确最大值！");
                    $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                    return;
                }
                if(obj.data.minNumber>=value){
                    layer.msg("最大值应该大于最小值");
                    $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                    return;
                }
                obj.update({
                      maxNumber:value,
                });
            }
            if( field=="money"){
                 if(!(/^\d+(\.\d{0,2})?$/.test(value))){
                    layer.msg("请输入金额(两位小数)！");
                    $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                    return;
                }
                obj.update({
                      money:value,
                });
            }
       });
        //定义事件集合
        var active = {
            addRow: function(){ //添加一行
                var oldData = table.cache[layTableId];
                var provinceId = $("#provinceId").val();
                var provinceName = $("#provinceId").find("option:selected").text();
                var cityId = $("#cityId").val();
                var cityName = $("#cityId").find("option:selected").text();
                var areaId = $("#countiesId").val();
                var areaName = $("#countiesId").find("option:selected").text();
                var minNumber = $("#minNumber").val().trim();
                var maxNumber = $("#maxNumber").val().trim();
                var money = $("#number").val().trim();
                var type = $("#detailType").val();
                var typeName = $("#detailType").find("option:selected").text();
                var moneyType = $("#moneyType").val();
                var moneyTypeName = $("#moneyType").find("option:selected").text();
                //验证
                if(!( /^\d+(\.\d{0,2})?$/.test(minNumber))){
                    layer.msg("请输入正确最小值！(两位小数)");
                    return;
                }
                 //验证
                if(!( /^\d+(\.\d{0,2})?$/.test(maxNumber))){
                    layer.msg("请输入正确最大值！(两位小数)");
                    return;
                }
                if(minNumber>maxNumber){
                    layer.msg("最大值应该大于最小值！");
                    return;
                }
                if(!provinceId){
                    layer.msg("请选择必填省！");
                    return;
                }
                if(!type){
                    layer.msg("请选择类型！");
                    return;
                }
                if(!moneyType){
                    layer.msg("请选择收入方式！");
                    return;
                }
                var newRow = {
                    provinceId:provinceId,
                    provinceName:provinceName,
                    cityId:cityId,
                    cityName:cityName,
                    areaId:areaId,
                    areaName:areaName,
                    minNumber:minNumber,
                    maxNumber:maxNumber,
                    type:type,
                    typeName:typeName,
                    moneyType:moneyType,
                    moneyTypeName:moneyTypeName,
                    money:money //合计
                };
                oldData.push(newRow);
                tableIns.reload({
                    data : oldData
                });
            },
            updateRow: function(obj){
                var oldData = table.cache[layTableId];              
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(row.id == obj.id){
                        $.extend(oldData[i], obj);
                        return;
                    }
                }
                tableIns.reload({
                    data : oldData
                });
            },
            removeEmptyTableCache: function(){
                var oldData = table.cache[layTableId];      
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(!row || !row.id){
                        oldData.splice(i, 1);    //删除一项
                    }
                    continue;
                }
                tableIns.reload({
                    data : oldData
                });
            },
            save: function(){
                var oldData = table.cache[layTableId];  
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(!row.type){
                        return;
                    }
                }
            }
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
        
        
         //监听工具条
        table.on('tool(dataTable)', function (obj) {
            var data = obj.data, event = obj.event, tr = obj.tr; //获得当前行 tr 的DOM对象;
            switch(event){
                                      
                case "del":
                    layer.confirm('真的删除行么？', function(index){
                      obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                      layer.close(index);
                      activeByType('removeEmptyTableCache');
                    });
                    break; 
            }
        });
//提交数据代码
        form.on('submit(editdeliverContractMain)',function(data){
        activeByType('save');    //更新行记录对象
      
        data.field.detailSet = table.cache[layTableId];  
        if(!data.field.detailSet){
            layer.msg("子表不能为空");
            return false;
        }
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/client/deliverContractMain/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("单据添加成功！",{time:1000},function(){
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

