layui.use(['layer','form','table' ,'upload'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        upload = layui.upload,
        t; //表格变量
    //监听工具条
    table.on('tool(vehicleTypeList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑车型",
                type : 2,
                content : "/deliver/vehicleType/edit?id="+data.id.toString(),
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回列表', '.layui-layer-setwin .layui-layer-close', {
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
            layer.confirm("你确定要删除该记录么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/deliver/vehicleType/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('vehicleType-table', t);
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
        elem: '#vehicleType-table',
        even: true,
        url:'/deliver/vehicleType/list',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
          title: '提示'
          ,layEvent: 'LAYTABLE_TIPS'
          ,icon: 'layui-icon-tips'
        }],
        title:'车型表',
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
            { field:'name' ,title:'名称'},
            { field:'volume' ,title:'容积(m³)'},
            { field:'bearing' ,title:'承重(kg)'},
            { field:'contractName' ,title:'合同名称'},
            {field:'createUser',  title: '创建人',templet:'<div>{{  d.createUser.nickName }}</div>'},
            {field:'updateUser',  title: '修改人',templet:'<div>{{  d.updateUser.nickName }}</div>'},
            {field:'createDate',  title: '创建时间',    width:'14%',templet:'<div>{{ layui.laytpl.toDateString(d.createDate) }}</div>',unresize: true}, //单元格内容水平居中
            {field:'updateDate',  title: '修改时间',    width:'14%',templet:'<div>{{ layui.laytpl.toDateString(d.updateDate) }}</div>',unresize: true}, //单元格内容水平居中
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#vehicleTypeBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
        addUser : function(){
            addIndex = layer.open({
                title : "添加车型",
                type : 2,
                content : "/deliver/vehicleType/add",
                success : function(layero, addIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回车型列表', '.layui-layer-setwin .layui-layer-close', {
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
            var checkStatus = table.checkStatus('vehicleType-table'),
                data = checkStatus.data;
            if(data.length > 0){
                layer.confirm("你确定要删除这些车型么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/deliver/vehicleType/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('vehicleType-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的车型",{time:1000});
            }
        }
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('vehicleType-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});