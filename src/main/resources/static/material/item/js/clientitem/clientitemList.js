layui.use(['layer','form','table' ,'upload'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        upload = layui.upload,
        t; //表格变量

        upload.render({
            elem: '#upload',
            url: '/item/clientitem/upload',
            field: 'file',
            before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
                layer.load(); //上传loading
            },
            done: function (res) {
                //如果上传失败
                if (res.success === false) {
                     layer.closeAll('loading');
                    return layer.msg(res.message);
                }else{
                     layer.closeAll('loading');
                     layer.open({
                        title: '提示消息'
                        ,content: res.message
                     });     
                }
            }
        }); 
    //监听工具条
    table.on('tool(clientitemList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑品项",
                type : 2,
                content : "/item/clientitem/edit?id="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回品项列表', '.layui-layer-setwin .layui-layer-close', {
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
            layer.confirm("你确定要删除该品项么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/item/clientitem/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('clientitem-table', t);
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
        elem: '#clientitem-table',
        even: true,
        url:'/item/clientitem/list',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
          title: '提示'
          ,layEvent: 'LAYTABLE_TIPS'
          ,icon: 'layui-icon-tips'
        }],
        title:'客户品项表',
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
            {field:'clientId',        title: '客户'   },
            {field:'code',        title: '编号'   },
            {field:'name',        title: '名称'   },
            {field:'category',        title: '分类'   },
            {field:'brand',        title: '品牌'   },
            {field:'itemLength',        title: '长度(cm)'   },
            {field:'itemWidth',        title: '宽度(cm)'   },
            {field:'itemHeight',        title: '高度(cm)'   },
            {field:'itemVolume',        title: '体积'   },
            {field:'itemWeight',        title: '重量'   },
            {field:'unitWhole',        title: '单位（整）'   },
            {field:'unitScattered',        title: '单位（零）'   },
            {field:'tray',        title: '件/托'   },
            {field:'unitRate',        title: '换算率'   },
            {field:'day',        title: '保质天数'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#clientitemBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
        addUser : function(){
            addIndex = layer.open({
                title : "添加品项",
                type : 2,
                content : "/item/clientitem/add",
                success : function(layero, addIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回品项列表', '.layui-layer-setwin .layui-layer-close', {
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
            var checkStatus = table.checkStatus('clientitem-table'),
                data = checkStatus.data;
            if(data.length > 0){
                layer.confirm("你确定要删除这些品项么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/item/clientitem/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('clientitem-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的品项",{time:1000});
            }
        }
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('clientitem-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});