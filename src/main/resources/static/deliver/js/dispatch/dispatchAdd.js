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
                    case '1': position = '#order_bill'; break;
                    default: position = '#order_dispatch';
                }
                getList(position, status)
        })        

        var dispatchWidth = $("#dispatchTable").width();

        var tableIns;

        var dispatchIns = table.render({
                    elem: '#dispatchTable',
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
                        }
                    }
                });
        var layTableId = "layTable";
        var vehicleV = 1; //车辆体积
        var vehicleW  = 1;//车辆重量
     
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
        function getList(position, status = '') {
                var tableData=new Array();  
                //数据表格实例化           
                var tbWidth = $("#tableRes").width();
                tableIns = table.render({
                    url:'/deliver/dispactAddress/list',
                    where: {
                        status: status
                    },
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
            //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
          
        }
    });     
   table.on('rowDouble(order_dispatch)', function(obj){
        var data = obj.data;
        //对应增加预载单据得行
        var newRow =data;
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
        active.addRow(newRow,'dispatchTable');
         obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
         //合计数改下
         $("#dispatchVolume").val(mathV());
         $("#lodaRate").val(Percentage( $("#dispatchVolume").val(),vehicleV));
         $("#dispatchWeight").val(mathW());
         $("#weightRate").val(Percentage( $("#dispatchWeight").val(),vehicleW));
    }); 

    table.on('rowDouble(dispatchTable)', function(obj){
        var data = obj.data;
        //对应增加预载单据得行
        var newRow =data;
        active.addRow(newRow,'order_dispatch');
        obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
            //合计数改下
         $("#dispatchVolume").val(mathV());
         $("#lodaRate").val(Percentage( $("#dispatchVolume").val(),vehicleV));
         $("#dispatchWeight").val(mathW());
         $("#weightRate").val(Percentage( $("#dispatchWeight").val(),vehicleW));
    });       



//计算表中合计体积
function mathV(){
     var data = table.cache["dispatchTable"];
     var sum = 0;
     for (var i = 0; i < data.length; i++) {
          console.log(JSON.stringify(data[i]))
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
        form.on('submit(adddispatch)',function(data){
        activeByType('save');    //更新行记录对象
      
        data.field.detailSet = table.cache['dispatchTable'];  
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/deliver/dispatch/add",
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

