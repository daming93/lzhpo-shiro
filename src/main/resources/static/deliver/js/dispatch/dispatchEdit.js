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
    elem: '#dispatchTime' //指定元素
      ,value: new Date()
  });
  var laydate2 = layui.laydate;
  

});
 
    //layui 模块化引用
    layui.use(['jquery', 'table','upload', 'layer','element'], function(){

       
        var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer,upload = layui.upload, element = layui.element;
        getList('#order_dispatch', '')
            // 监听tab切换 操作：文档 - 内置模块 - 常用元素操作 element - 监听tab切换
        element.on('tab(tab-all)', function (data) {
                var status = $(this).attr('data-status')
                var position = '#order_dispatch'
                switch (status) {
                    case '1': position = '#order_bill';    getExpressBillList(position, '') ; break;
                    case '2': position = '#order_line';    getLineList(position, '') ; break;
                    default: position = '#order_dispatch';getList(position, ''); break;
                }
              
        })        
       
        form.on('select(provinceId)', function(data){
            $("#cityId").empty();
            $("#countiesId").empty();
                //data.value 得到被选中的值
                 var url = '/sys/territory/selectCityByProvinceCode?id=' + data.value;
                 $.get(url,function(data){
                    $("#cityId").empty();
                    $("#cityId").append(new Option("请选择",""));
                    $.each(data,function(index,item){
                        $("#cityId").append(new Option(item.name,item.id));
                    });
                    layui.form.render("select");
                 });
        });
        form.on('select(cityId)', function(data){
                //data.value 得到被选中的值
            $("#countiesId").empty();
                 var url = '/sys/territory/selectAreaByCityCode?id=' + data.value;
                 $.get(url,function(data){
                    $("#countiesId").empty();
                    $("#countiesId").append(new Option("请选择",""));
                    $.each(data,function(index,item){
                        $("#countiesId").append(new Option(item.name,item.id));
                    });
                    layui.form.render("select");
                 });
        });
        var dispatchWidth = $("#dispatchTable").width();

        var tableIns;
        var dispatchId = $("#dispatchId").val();
        var dispatchIns = table.render({
                    elem: '#dispatchTable',
                    url:'/deliver/dispactAddress/listByDispacthId',
                    where: {
                        id: dispatchId
                    },
                    method:'post',
                    id: 'dispatchTable',
                    width: dispatchWidth,
                    page: false,
                    cellMinWidth:100,
                    loading: true,
                //    height:512,
                    even: false, //不开启隔行背景
                    limit: 1000, // 数据表格默认全部显示
                    cols: [[
                            {title: '序号', type: 'numbers'},
                            { field:'typeStr' ,title:'单据类型'},
                            { field:'clientName',title:'客户名称',align:'center'},
                            { field:'dispatchTime',title:'配送时间',align:'center'},
                            { field:'clientCode',title:'客户单号',align:'center'},
                            { field:'code',title:'系统单号',align:'center'},
                            { field:'volume',title:'体积',align:'center',width:100},
                            { field:'weight',title:'重量',align:'center',width:100},
                            { field:'countiesName',title:'区域',align:'center',width:100},
                            { field:'areaName',title:'送达地点',align:'center'},
                            { field:'provinceId',title:'省id',align:'center',hide:true,width:100},
                            { field:'cityId',title:'市id',align:'center',hide:true,width:100},
                            { field:'countiesId',title:'区id',align:'center',hide:true,width:100},
                            { field:'type',title:'类型id',align:'center',hide:true,width:100},
                            { field:'id',title:'id',align:'center',hide:true,width:100},
                           
                    ]],
                    done: function(res, curr, count){
                        if(count>0){
                            $("#dispatchVolume").val(mathV());
                            $("#lodaRate").val(Percentage( $("#dispatchVolume").val(),vehicleV));
                            $("#dispatchWeight").val(mathW());
                            $("#weightRate").val(Percentage( $("#dispatchWeight").val(),vehicleW));
                        }
                    }
                });
        var layTableId = "layTable";
        var vehicleV=$("#vehicleId").find("option:selected").attr("data-volume");
        var vehicleW=$("#vehicleId").find("option:selected").attr("data-bearing");
     
        //定义事件集合
        var active = {
            addRow: function(newRow,tableId){ //添加一行
                var oldData = table.cache[tableId];
                if(!oldData){
                    oldData = [];
                }
                oldData.push(newRow);
                if(tableId=="dispatchTable"){
                    dispatchIns.reload({
                        data : oldData
                    });

                }else{
                    tableIns.reload({
                        data : oldData
                    });
                }
                
            },
            updateRow: function(obj){
                var oldData = table.cache['dispatchTable'];              
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(row.tempId == obj.tempId){
                        $.extend(oldData[i], obj);
                        return;
                    }
                }
                dispatchIns.reload({
                    data : oldData
                });
            },
            removeEmptyTableCache: function(rowId,tableId){
                var oldData = table.cache[tableId];  
                for(var i=0, row; i < oldData.length; i++){
                      row = oldData[i];
                      if(row.id==rowId){
                          oldData.splice(i, 1);    //删除一项
                        
                          break;
                      }
                }    
              
               
            },
            save: function(){
                var oldData = table.cache['dispatchTable'];  
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

               //搜索
        form.on("submit(searchForm)",function(data){
            getExpressBillList('#order_bill', '',data.field) ;
            getList('#order_dispatch', '',data.field);
            return false;
        });

       function getList(position, status = '',field) {
                var tableData=new Array();  
                //数据表格实例化           
                var tbWidth = $("#tableRes").width();
                var oldData = table.cache['dispatchTable'];  
                var waitList = [];

                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    waitList.push(row.id);
                }
                if(field){
                    field.s_waitList = waitList.toLocaleString();
                }else{
                    field = {};
                    field.s_waitList = waitList.toLocaleString();
                }
                tableIns = table.render({
                    url:'/deliver/dispactAddress/listTakout',
                    where: field,
                    elem: position,
                    id: layTableId,
                    method: 'post',
                    height:312,
                   // width: tbWidth,
                    cellMinWidth:100,
                    page: false,
                    loading: true,
                    even: false, //不开启隔行背景
                    limit: 1000, // 数据表格默认全部显示
                    cols: [[
                            { title: '序号', type: 'numbers'},
                            { field:'clientName',title:'客户名称',align:'center'},
                            { field:'dispatchTime',title:'配送时间',align:'center'},
                            { field:'clientCode',title:'客户单号',align:'center'},
                            { field:'code',title:'系统单号',align:'center'},
                            { field:'volume',title:'体积',align:'center',width:100},
                            { field:'weight',title:'重量',align:'center',width:100},
                            { field:'countiesName',title:'区域',align:'center',width:100},
                            { field:'areaName',title:'送达地点',align:'center'},
                            { field:'provinceId',title:'省id',align:'center',hide:true,width:100},
                            { field:'cityId',title:'市id',align:'center',hide:true,width:100},
                            { field:'countiesId',title:'区id',align:'center',hide:true,width:100},
                            { field:'type',title:'类型id',align:'center',hide:true,width:100},
                            { field:'id',title:'id',align:'center',hide:true,width:100},
                             {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#dispatchBillBar'}
                    ]],
                    done: function(res, curr, count){
                            $("#dispatchVolume").val(mathV());
                            $("#lodaRate").val(Percentage( $("#dispatchVolume").val(),vehicleV));
                            $("#dispatchWeight").val(mathW());
                            $("#weightRate").val(Percentage( $("#dispatchWeight").val(),vehicleW));
                    }
                });

        }
                function getLineList(position, status = '',field) {
                var tableData=new Array();  
                //数据表格实例化           
                var tbWidth = $("#tableRes").width();
                var oldData = table.cache['dispatchTable'];  
                var waitList = [];

                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    waitList.push(row.id);
                }
                if(field){
                    field.s_waitList = waitList.toLocaleString();
                }else{
                    field = {};
                    field.s_waitList = waitList.toLocaleString();
                }
                tableIns = table.render({
                    url:'/deliver/dispactAddress/listLineTakout',
                    where: field,
                    elem: position,
                    id: layTableId,
                    method: 'post',
                    height:312,
                   // width: tbWidth,
                    cellMinWidth:100,
                    page: false,
                    loading: true,
                    even: false, //不开启隔行背景
                    limit: 1000, // 数据表格默认全部显示
                    cols: [[
                              {title: '序号', type: 'numbers'},
                            { field:'clientName',title:'客户名称',align:'center'},
                            { field:'dispatchTime',title:'配送时间',align:'center'},
                            { field:'clientCode',title:'客户单号',align:'center'},
                            { field:'code',title:'系统单号',align:'center'},
                            { field:'volume',title:'体积',align:'center',width:100},
                            { field:'weight',title:'重量',align:'center',width:100},
                            { field:'countiesName',title:'区域',align:'center',width:100},
                            { field:'areaName',title:'送达地点',align:'center'},
                            { field:'provinceId',title:'省id',align:'center',hide:true,width:100},
                            { field:'cityId',title:'市id',align:'center',hide:true,width:100},
                            { field:'countiesId',title:'区id',align:'center',hide:true,width:100},
                            { field:'tableId',title:'原表Id',align:'center',hide:true,width:100},
                            { field:'type',title:'类型id',align:'center',hide:true,width:100},
                            { field:'id',title:'id',align:'center',hide:true,width:100},
                             {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#dispatchBillBar'}
                    ]],
                    done: function(res, curr, count){
                  
                    }
                });

        }
        function getExpressBillList(position, status = '',field) {
                var tableData=new Array();  
                //数据表格实例化           
                var tbWidth = $("#tableRes").width();
                var oldData = table.cache['dispatchTable'];  
                var waitList = [];
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    waitList.push(row.id);
                }
                if(field){
                    field.s_waitList = waitList.toLocaleString();
                }else{
                    field = {};
                    field.s_waitList = waitList.toLocaleString();
                }
                tableIns = table.render({
                    url:'/deliver/dispactAddress/list',
                    where: field,
                    elem: position,
                    id: layTableId,
                    method: 'post',
                    height:312,
                   // width: tbWidth,
                    cellMinWidth:100,
                    page: false,
                    loading: true,
                    even: false, //不开启隔行背景
                    limit: 1000, // 数据表格默认全部显示
                    cols: [[
                              {title: '序号', type: 'numbers'},
                            { field:'clientName',title:'客户名称',align:'center'},
                            { field:'dispatchTime',title:'配送时间',align:'center'},
                            { field:'clientCode',title:'客户单号',align:'center'},
                            { field:'code',title:'系统单号',align:'center'},
                            { field:'volume',title:'体积',align:'center',width:100},
                            { field:'weight',title:'重量',align:'center',width:100},
                            { field:'countiesName',title:'区域',align:'center',width:100},
                            { field:'areaName',title:'送达地点',align:'center'},
                            { field:'provinceId',title:'省id',align:'center',hide:true,width:100},
                            { field:'cityId',title:'市id',align:'center',hide:true,width:100},
                            { field:'countiesId',title:'区id',align:'center',hide:true,width:100},
                            { field:'type',title:'类型id',align:'center',hide:true,width:100},
                            { field:'id',title:'id',align:'center',hide:true,width:100},
                             {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#dispatchBillBar'}
                    ]],
                    done: function(res, curr, count){
                        $("#dispatchVolume").val(mathV());
                        $("#lodaRate").val(Percentage( $("#dispatchVolume").val(),vehicleV));
                        $("#dispatchWeight").val(mathW());
                        $("#weightRate").val(Percentage( $("#dispatchWeight").val(),vehicleW));
                    }
                });

        }
      //监听工具条
    table.on('tool(order_dispatch)', function(obj){
        var data = obj.data;
        if(obj.event === 'spilt'){
            var editIndex = layer.open({
                title : "拆单",
                type : 2,
                offset: 'rt',
                area: ['410px','800px'],
                content : "/deliver/dispactAddress/spilt?id="+data.id,
                success : function(layero, index){
                  
                    setTimeout(function(){
                        layer.tips('点击此处返回排单列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
          
          
        }
    });
      //监听工具条
    table.on('tool(order_dispatch)', function(obj){
        var data = obj.data;
        if(obj.event === 'spilt'){
            var editIndex = layer.open({
                title : "拆单",
                type : 2,
                offset: 'rt',
                area: ['410px','800px'],
                content : "/deliver/dispactAddress/spilt?type=1&id="+data.id,
                success : function(layero, index){
                  
                    setTimeout(function(){
                        layer.tips('点击此处返回排单列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
        }
    });
        table.on('tool(order_line)', function(obj){
        var data = obj.data;
        if(obj.event === 'spilt'){
            var editIndex = layer.open({
                title : "拆单",
                type : 2,
                offset: 'rt',
                area: ['410px','800px'],
                content : "/deliver/dispactAddress/spilt?type=3&id="+data.id,
                success : function(layero, index){
                  
                    setTimeout(function(){
                        layer.tips('点击此处返回排单列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
        }
    });  
    table.on('tool(order_bill)', function(obj){
        var data = obj.data;
        if(obj.event === 'spilt'){
            var editIndex = layer.open({
                title : "拆单",
                type : 2,
                offset: 'rt',
                area: ['410px','800px'],
                content : "/deliver/dispactAddress/spilt?type=2&id="+data.id,
                success : function(layero, index){
                  
                    setTimeout(function(){
                        layer.tips('点击此处返回排单列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
        }
    });      
   table.on('rowDouble(order_dispatch)', function(obj){
        var data = obj.data;
        //对应增加预载单据得行
        var newRow =data;
      
        data.dispacthId =   $("#dispatchId").val();
        newRow.typeStr = "库存发单";
//        newRow.id = data.id;
        // newRow.type = 1;
  
        // newRow.code = data.code;
        // newRow.clientCode = data.code;
        // newRow.clientName = data.receiveName;
        // newRow.dispatchTime =data.deliverBillTime;
        // newRow.volume = data.volume;
        // newRow.weight = data.weight;
        // newRow.countiesName = data.receiveDetail;
        // newRow.areaName = data.receiveDetailArea;
        // newRow.provinceId = data.receiveProvinceId;
        // newRow.cityId = data.receiveCityId;
        // newRow.countiesId = data.receiveAreaId;
        $.ajax({
                type:"POST",
                url:"/deliver/dispactAddress/bindingStatus",
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(data),
                success:function(res){
                    if(res.success){
                         layer.msg(res.message,{time: 1000},function(){
                                 getList('#order_dispatch', '');
                                 //重新加载预载表格
                                 table.reload("dispatchTable");
                                  var formData = form.val("dispatchForm");
                                  $.ajax({
                                    type:"POST",
                                    url:"/deliver/dispatch/editJustRate",
                                    dataType:"json",
                                    contentType:"application/json",
                                    data:JSON.stringify(formData),
                                    success:function(res){
                                        layer.close(loadIndex);
                                        if(res.success){
                                            layer.msg(res.message);
                                        }else{
                                            layer.msg(res.message);
                                        }
                                    }
                                });
                         });
                     }else{
                         layer.msg(res.message);
                     }
                 }
            });   
    });
    table.on('rowDouble(order_line)', function(obj){
        var data = obj.data;
        //对应增加预载单据得行
        var newRow =data;
        //在这里检测是否能添加
        var vehicleId = $("#vehicleId").val();
        if(!vehicleId){
             layer.msg("请选择车辆");
             return false;
        }
        $.ajax({
            type:"POST",
            url:"/deliver/vehicle/searchAreaCanDeliver",
            data:{
                vehicleId:vehicleId,
                proviceId:data.provinceId,
                cityId:data.cityId,
                areaId:data.countiesId
            },
            success:function(res){
                if(res.success){
                   $.ajax({
                        type:"POST",
                        url:"/client/deliverContractMain/searchAreaCanDeliver",
                        data:{
                            tableId:data.tableId,//上面方法名是一样的类不同，其次在这里使用的是出库表的id
                            proviceId:data.provinceId,
                            cityId:data.cityId,
                            areaId:data.countiesId,
                            type:2
                        },
                        success:function(res){
                            if(res.success){
                               newRow.typeStr = "线路发单";
                               active.addRow(newRow,'dispatchTable');
                               obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                               //合计数改下
                               $("#dispatchVolume").val(mathV());
                               $("#lodaRate").val(Percentage( $("#dispatchVolume").val(),vehicleV));
                               $("#dispatchWeight").val(mathW());
                               $("#weightRate").val(Percentage( $("#dispatchWeight").val(),vehicleW));
                            }else{
                                layer.msg(res.message);
                                return false;
                            }
                        }
                    });
                }else{
                    layer.msg(res.message);
                    return false;
                }
            }
        });
      
//        newRow.id = data.id;
        // newRow.type = 1;
  
        // newRow.code = data.code;
        // newRow.clientCode = data.code;
        // newRow.clientName = data.receiveName;
        // newRow.dispatchTime =data.deliverBillTime;
        // newRow.volume = data.volume;
        // newRow.weight = data.weight;
        // newRow.countiesName = data.receiveDetail;
        // newRow.areaName = data.receiveDetailArea;
        // newRow.provinceId = data.receiveProvinceId;
        // newRow.cityId = data.receiveCityId;
        // newRow.countiesId = data.receiveAreaId;
       
    });          
   table.on('rowDouble(order_bill)', function(obj){
        var data = obj.data;
        //对应增加预载单据得行
        var newRow =data;
      
        data.dispacthId =   $("#dispatchId").val();
        newRow.typeStr = "快速发单";
//        newRow.id = data.id;
        // newRow.type = 1;
  
        // newRow.code = data.code;
        // newRow.clientCode = data.code;
        // newRow.clientName = data.receiveName;
        // newRow.dispatchTime =data.deliverBillTime;
        // newRow.volume = data.volume;
        // newRow.weight = data.weight;
        // newRow.countiesName = data.receiveDetail;
        // newRow.areaName = data.receiveDetailArea;
        // newRow.provinceId = data.receiveProvinceId;
        // newRow.cityId = data.receiveCityId;
        // newRow.countiesId = data.receiveAreaId;
        $.ajax({
                type:"POST",
                url:"/deliver/dispactAddress/bindingStatus",
                dataType:"json",
                contentType:"application/json",
                data:JSON.stringify(data),
                success:function(res){
                    if(res.success){
                         layer.msg(res.message,{time: 1000},function(){
                                 getExpressBillList('#order_bill', '');
                                 //重新加载预载表格
                                 table.reload("dispatchTable");
                                  var formData = form.val("dispatchForm");
                                  $.ajax({
                                    type:"POST",
                                    url:"/deliver/dispatch/editJustRate",
                                    dataType:"json",
                                    contentType:"application/json",
                                    data:JSON.stringify(formData),
                                    success:function(res){
                                        layer.close(loadIndex);
                                        if(res.success){
                                            layer.msg(res.message);
                                        }else{
                                            layer.msg(res.message);
                                        }
                                    }
                                });
                         });
                     }else{
                         layer.msg(res.message);
                     }
                 }
            });   
    }); 
    table.on('rowDouble(dispatchTable)', function(obj){
        var data = obj.data;
        //对应增加预载单据得行
        var newRow =data;
        //删除
        $.ajax({
                type:"GET",
                 url:"/deliver/dispactAddress/deleteDetail?dispactAddressId="+data.id,
                dataType:"json",
                contentType:"application/json",
                 success:function(res){
                    if(res.success){
                         layer.msg(res.message,{time: 1000},function(){
                                obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                                 getList('#order_dispatch', '');
                                 getExpressBillList('#order_bill', '');
                                  getLineList('#order_line', '');
                                  var formData = form.val("dispatchForm");
                                  $.ajax({
                                    type:"POST",
                                    url:"/deliver/dispatch/editJustRate",
                                    dataType:"json",
                                    contentType:"application/json",
                                    data:JSON.stringify(formData),
                                    success:function(res){
                                        layer.close(loadIndex);
                                        if(res.success){
                                            layer.msg(res.message);
                                        }else{
                                            layer.msg(res.message);
                                        }
                                    }
                                });
                         });
                     }else{
                         layer.msg(res.message);
                     }
                 }
            });        
    });       



//计算表中合计体积
function mathV(){
     var data = table.cache["dispatchTable"];
     var sum = 0;
     for (var i = 0; i < data.length; i++) {
         if(JSON.stringify(data[i]) != "[]"){
            sum += data[i].volume;
         }
     }
     return sum;
}
function mathW(){
     var data = table.cache["dispatchTable"];
     var sum = 0;
     for (var i = 0; i < data.length; i++) {
        if(JSON.stringify(data[i]) != "[]"){
            sum += data[i].weight;
        }
     }
     return sum;
}
function Percentage(num, total) { 
    if (num == 0 || total == 0){
        return 0;
    }
    return (Math.round(num / total * 10000) / 100.00);// 小数点后两位百分比
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
                 case "checkTest":
                    $(obj.tr).find(".layui-table-edit").keyup(function () {
                        var $input = $(this), val = $input.val();
                        $input.val(clearNoNum(val));
                    });
                    break;                         
            }
        });

      form.on('select(vehicleId)', function(data){
          var driverId=$(data.elem).find("option:selected").attr("data-driverId");
          vehicleV=$(data.elem).find("option:selected").attr("data-volume");
          vehicleW=$(data.elem).find("option:selected").attr("data-bearing");
          if(driverId){
            $("#driverId").val(driverId);
            form.render();
          }
         $("#dispatchVolume").val(mathV());
         $("#lodaRate").val(Percentage( $("#dispatchVolume").val(),vehicleV));
         $("#dispatchWeight").val(mathW());
         $("#weightRate").val(Percentage( $("#dispatchWeight").val(),vehicleW));
        
      }); 
        
//提交数据代码
        form.on('submit(editdispatch)',function(data){
        activeByType('save');    //更新行记录对象
      
        data.field.detailSet = table.cache['dispatchTable'];  
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/deliver/dispatch/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("单据添加成功！",{time:1000},function(){
                          // 刷新父页面
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

