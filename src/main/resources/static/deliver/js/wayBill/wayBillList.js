layui.use(['layer','form','table' ,'upload'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        upload = layui.upload,
        t; //表格变量
    //监听工具条
     table.on('rowDouble(wayBillList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
                title : "路单详情",
                type : 2,
                content : "/deliver/wayBill/edit?id="+data.id.toString(),
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回路单列表', '.layui-layer-setwin .layui-layer-close', {
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
    table.on('tool(wayBillList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑路单",
                type : 2,
                content : "/deliver/wayBill/edit?id="+data.id.toString(),
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回路单列表', '.layui-layer-setwin .layui-layer-close', {
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
        }
        if(obj.event === "del"){
            layer.confirm("你确定要解散该路单么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/deliver/wayBill/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("解散成功",{time: 1000},function(){
                                table.reload('wayBill-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === 'userTable'){
            var editIndex = layer.open({
                title : "编辑附表",
                type : 2,
                content : "/finance/userTable/edit?id="+data.userTable.id.toString(),
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回单据列表', '.layui-layer-setwin .layui-layer-close', {
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
        }
         if(obj.event === 'schedule'){
            var userTableId =  $("#tableListId").val();

            if(!userTableId){
                 layer.msg("请选择附表类型")
                return 
            }
            //偏好设置
            $.ajax({
                type:"GET",
                url:"/sys/userSetting/add?type=1&modular=3&tableId="+userTableId,
                dataType:"json",
                contentType:"application/json",
                success:function(res){
                   
                }
            });
            var editIndex = layer.open({
                title : "增加附表",
                type : 2,
                content : "/finance/userTable/add?tableId="+data.id.toString()+"&userTableId="+userTableId+"&modular=3"+"&code="+data.code,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回单据列表', '.layui-layer-setwin .layui-layer-close', {
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
        }
    });
    t = {
        elem: '#wayBill-table',
        even: true,
        url:'/deliver/wayBill/list',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
          title: '提示'
          ,layEvent: 'LAYTABLE_TIPS'
          ,icon: 'layui-icon-tips'
        }],
        title:'客户路单表',
        page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip'], //自定义分页布局
            //,curr: 5 //设定初始在第 5 页
            groups: 6, //只显示 1 个连续页码
            first: "首页", //显示首页
            last: "尾页", //显示尾页
            limits:[3,10, 20, 30]
        },
        width: $(parent.window).width()-223,
        cols: [[
            {type:'checkbox'},
           /* {field:'id',        title: 'ID'   },*/
            {field:'code',  title: '系统单号',    width:'10%'}, 
            {field:'vehicleNum',        title: '车辆数量' ,    width:'12%'  },
            {field:'lodaRate',  title: '平均装载率(%)'}, 
            {field:'dispatchAreaName',  title: '配送区域'}, 
        //    {field:'trainNumber',        title: '车次'   },
            {field:'dispatchVolume',        title: '体积(m³) '   },
            {field:'dispatchWeight',        title: '重量(kg)'   },
         //   {field:'trayNumber',        title: '件/托(kg)'   },
            {field:'statusStr',        title: '状态'   },
            { field:'userTableCode' ,event:"userTable",title:'附表',templet: function(d){   
                    if(d.userTable.code!=null){
                        return  '<div><a> '+ d.userTable.code+' </a></div>'
                    }else{
                         return  '暂无'
                    }
                }
            },
            {field:'remarks',        title: '备注'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#wayBillBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
        
        //批量解散
        deleteSome : function(){
            var checkStatus = table.checkStatus('wayBill-table'),
                data = checkStatus.data;
            if(data.length > 0){
                layer.confirm("你确定要解散这些路单么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('解散中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/deliver/wayBill/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("解散成功",{time: 1000},function(){
                                        table.reload('wayBill-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要解散的路单",{time:1000});
            }
        }
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('wayBill-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});