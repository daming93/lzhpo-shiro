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

layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t; //表格变量


    //监听工具条
    table.on('tool(inventoryList)', function(obj){
        var data = obj.data;
         if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑盘点",
                type : 2,
                content : "/warehouse/inventory/edit?id="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回盘点列表', '.layui-layer-setwin .layui-layer-close', {
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
            layer.confirm("你确定要删除该盘点么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/warehouse/inventory/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('inventory-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === "ensure"){
            layer.confirm("你确定要锁定该盘点么(锁定之后无法撤销)",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/warehouse/inventory/ensure",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("锁定成功",{time: 1000},function(){
                                table.reload('inventory-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        
    });
    t = {
        elem: '#inventory-table',
        even: true,
        url:'/warehouse/inventory/list',
        method:'post',
        toolbar: true ,
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
            {field:'clientName',        title: '客户'   },
            {field:'auditorTypeStr',        title: '盘点类型' ,    },
            {field:'inventoryTime',        title: '盘点时间' ,    },
            { field:'statusStr',title:'盘点结果',align:'center'},
            { field:'typeStr',title:'状态',align:'center'},
            { field:'inventoryStatusStr',title:'单据状态',align:'center'},
            {field:'createUser',  title: '盘点人',templet:'<div>{{  d.createUser.nickName }}</div>'},
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#inventoryBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };

    table.render(t);
    var active={
          addInventory : function(){
            var continuity =$('input[name="s_continuity"]:checked').val();
            var startTime = $("#startTime").val();
            var endTime = $("#overTime").val();
            var batchStatus = $('input[name="s_mode"]:checked').val();
            var clientId =  $("#clientId").val();
            if(!clientId){
                layer.msg('选择盘点时，客户是必填项'); 
                return;
            }

            if(continuity=="off"){//异动盘点
                if(startTime&&endTime){

                }else{
                     layer.msg('选择异动盘点时，开始时间和结束时间是必填项');
                     return;
                }
            }
            $.ajax({
                type:"POST",
                url:"/warehouse/inventory/add",
                dataType:"json",
                contentType:'application/x-www-form-urlencoded; charset=UTF-8',
                data:{
                    'continuity':continuity,
                    'startTime':startTime,
                    'endTime':endTime,
                    'batchStatus':batchStatus,
                    'clientId':clientId    

                },
                success:function(res){
                    if(res.success){
                        parent.layer.msg("单据添加成功！",{time:1000},function(){
                            location.reload(); 
                        });
                    }else{
                        layer.msg(res.message);
                    }
                }
            });
        },
        //批量删除
        deleteSome : function(){
            var checkStatus = table.checkStatus('inventory-table'),
                data = checkStatus.data;
            if(data.length > 0){
                layer.confirm("你确定要删除这些盘点么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/warehouse/inventory/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('inventory-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的盘点",{time:1000});
            }
        }
    };

    table.on('rowDouble(inventoryList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "盘点详情",
            type : 2,
            content : "/warehouse/inventory/edit?id="+data.id,
            success : function(layero, index){
                setTimeout(function(){
                    layer.tips('点击此处返回盘点列表', '.layui-layer-setwin .layui-layer-close', {
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
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
   
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('inventory-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});