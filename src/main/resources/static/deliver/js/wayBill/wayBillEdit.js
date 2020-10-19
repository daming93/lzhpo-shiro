Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
layui.use('laydate', function(){
  var laydate = layui.laydate;
  
  //执行一个laydate实例
  laydate.render({
    elem: '#licencseTime' //指定元素
      ,value: new Date()
  });
});
layui.use(['form','jquery','table','layer'],function(){
    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer,
        table = layui.table;
 var wayBillId = $("#wayBillId").val();
 var dispatchIns = table.render({
                    elem: '#dispatchTable',
                    url:'/deliver/dispatch/list',
                    where: {
                        s_wayBillId: wayBillId
                    },
                    method:'post',
                    id: 'dispatchTable',
                    page: false,
                    cellMinWidth:100,
                    loading: true,
                //    height:512,
                    even: false, //不开启隔行背景
                    limit: 1000, // 数据表格默认全部显示
                     cols: [[
                        {type:'checkbox'},
                       /* {field:'id',        title: 'ID'   },*/
                        {field:'code',  title: '系统单号',    width:'10%'}, 
                        {field:'driverName',        title: '司机'   },
                        {field:'vehicleCode',        title: '车牌号' ,    width:'12%'  },
                        {field:'distributionTime',        title: '配送时间' ,    width:'13%'   },
                        {field:'lodaRate',  title: '装载率'}, 
                        {field:'dispatchAreaName',  title: '配送区域'}, 
                    //    {field:'trainNumber',        title: '车次'   },
                        {field:'dispatchVolume',        title: '体积(m³) '   },
                        {field:'dispatchWeight',        title: '重量(kg)'   },
                     //   {field:'trayNumber',        title: '件/托(kg)'   },
                        {field:'statusStr',        title: '状态'   },
                        {field:'dispactStatusStr',        title: '排单状态'   },
                        {field:'remarks',        title: '备注'   },
                        {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#dispatchBar'}
                    ]],
                    done: function(res, curr, count){
                        
                    }
                });
     table.on('rowDouble(dispatchTable)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "运输计划详情",
            type : 2,
            content : "/deliver/dispatch/edit?id="+data.id,
            success : function(layero, index){
                setTimeout(function(){
                    layer.tips('点击此处返回运输计划列表', '.layui-layer-setwin .layui-layer-close', {
                        tips: 3
                    });
                },500);
            }
        });
        //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
        $(window).resize(function(){
            layer.full(editIndex);
        });
        layer.full(editIndex);
    });
    var billIns = table.render({
                    elem: '#billTable',
                    url:'/deliver/dispactAddress/listByWayBillId',
                    where: {
                        id: wayBillId
                    },
                    method:'post',
                    id: 'billTable',
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
                        
                    }
                });



                 
    form.on("submit(editwaybill)",function(data){
        if(data.field.id == null){
            layer.msg("ID不存在");
            return false;
        }
        var menus = [];
        var c = $('form').find('input[type="checkbox"]');
        c.each(function(index, item){
            var m = {};
            if(item.checked){
                m.id = item.value;
                menus.push(m);
            }
        });
        data.field.menuSet = menus;
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/deliver/wayBill/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("路单编辑成功！",{time:1000},function(){
                        //刷新父页面
                        parent.location.reload();
                    });
                }else{
                    layer.msg(res.message,{time:1000},function(){
                        //刷新本页面
                        location.reload();
                    });

                }
            }
        });
        return false;
    });

});