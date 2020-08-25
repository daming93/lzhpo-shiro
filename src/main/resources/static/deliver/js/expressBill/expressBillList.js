layui.use(['layer','form','table' ,'upload'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        upload = layui.upload,
        t; //表格变量

     
    //监听工具条
    table.on('tool(expressBillList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑单据",
                type : 2,
                content : "/deliver/expressBill/edit?id="+data.id.toString(),
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
         if(obj.event === "back"){
            layer.confirm("你确定要撤销该单据么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/deliver/expressBill/back",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("撤销成功",{time: 1000},function(){
                                table.reload('expressBill-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === "del"){
            layer.confirm("你确定要删除该单据么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/deliver/expressBill/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('expressBill-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
    });
       table.on('rowDouble(expressBillList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "单据详情",
            type : 2,
            content : "/deliver/expressBill/edit?id="+data.id,
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
    });
    t = {
        elem: '#expressBill-table',
        even: true,
        url:'/deliver/expressBill/list',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
          title: '提示'
          ,layEvent: 'LAYTABLE_TIPS'
          ,icon: 'layui-icon-tips'
        }],
        title:'客户单据表',
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
            { field:'code' ,title:'单据号'},
            { field:'sendDetail' ,title:'寄件区域'},
            { field:'sendDetailArea' ,title:'寄件详细地址'},
            { field:'sendName' ,title:'寄件人'},
            { field:'sendPhone' ,title:'寄件人号码'},
            { field:'receiveDetail' ,title:'收件区域'},
            { field:'receiveDetailArea' ,title:'收件详细地址'},
            { field:'receiveName' ,title:'收件人'},
            { field:'receivePhone' ,title:'收件人号码'},
            { field:'moeny' ,title:'收费'},
            { field:'statusStr' ,title:'状态'},
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#expressBillBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
        addUser : function(){
            addIndex = layer.open({
                title : "添加单据",
                type : 2,
                content : "/deliver/expressBill/add",
                success : function(layero, addIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回单据列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
            //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
            $(window).resize(function(){
                layer.full(addIndex);
            });
            layer.full(addIndex);
        },
        //批量删除
        deleteSome : function(){
            var checkStatus = table.checkStatus('expressBill-table'),
                data = checkStatus.data;
            if(data.length > 0){
                layer.confirm("你确定要删除这些单据么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/deliver/expressBill/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('expressBill-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的单据",{time:1000});
            }
        }
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('expressBill-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});