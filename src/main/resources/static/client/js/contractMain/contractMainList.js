layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t; //表格变量

    //监听工具条
    table.on('tool(contractMainList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑合同",
                type : 2,
                content : "/client/contractMain/edit?id="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回合同列表', '.layui-layer-setwin .layui-layer-close', {
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
             //檢查合同状态
            if(data.isUse){
                layer.msg("正在使用的合同无法删除");
                return;
            }
            layer.confirm("你确定要删除该合同么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/client/contractMain/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('contractMain-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === "audit"){
            layer.confirm("你确定要审核该合同么(请仔细阅读合同条款)？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/client/contractMain/audit",{"id":data.id,"status":1},function (res){
                        if(res.success){
                            layer.msg("审核成功",{time: 1000},function(){
                                table.reload('contractMain-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
    });
    table.on('rowDouble(contractMainList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "合同详情",
            type : 2,
            content : "/client/contractMain/edit?id="+data.id,
            success : function(layero, index){
                setTimeout(function(){
                    layer.tips('点击此处返回合同列表', '.layui-layer-setwin .layui-layer-close', {
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
        elem: '#contractMain-table',
        even: true,
        url:'/client/contractMain/list',
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
            {field:'clientId',        title: '客户名称'   },
            {field:'name',        title: '合同名称'   },
            {field:'contractCode',        title: '合同编号'   },
            {field:'startTime',  title: '开始时间',    width:'14%'}, 
            {field:'overTime',  title: '结束时间',    width:'14%'}, 
            {field:'createDate',  title: '创建时间',    width:'14%',templet:'<div>{{ layui.laytpl.toDateString(d.createDate) }}</div>',unresize: true}, //单元格内容水平居中
            {field:'updateDate',  title: '修改时间',    width:'14%',templet:'<div>{{ layui.laytpl.toDateString(d.updateDate) }}</div>',unresize: true}, //单元格内容水平居中
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#contractMainBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
      
        //批量删除
        deleteSome : function(){
            var checkStatus = table.checkStatus('contractMain-table'),
                data = checkStatus.data;
            if(data.length > 0){
                console.log(JSON.stringify(data));
                layer.confirm("你确定要删除这些合同么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/client/contractMain/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('contractMain-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的合同",{time:1000});
            }
        }
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('contractMain-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});