layui.config({
                base:"/static/layui/layui_exts/"
            }).use(['layer','form','table' ,'upload', 'excel'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        upload = layui.upload,
        excel = layui.excel,
        t; //表格变量
      
    //监听头工具栏事件
    table.on('toolbar(trayList)', function(obj) {
        var checkStatus = table.checkStatus(obj.config.id),
            data = checkStatus.data; //获取选中的数据
            switch (obj.event) {
                case 'table_export':
                    exportFile('tray-table')
                    break;
                };
            });
    /**
                 * by yutons
                 * Array.from() 非常方便的将一个类数组的集合 ==》 数组，直接使用数组身上的方法。例如：常用的map，foreach… 
                 * 但是，问题来了，IE不识别Array.from这个方法。所以写了它兼容IE的写法。
                 */
                if (!Array.from) {
                    Array.from = function(el) {
                        return Array.apply(this, el);
                    }
                }
                
//表格导出
                function exportFile(id) {
                    //根据传入tableID获取表头
                    var headers = $("div[lay-id=" + id + "] .layui-table-box table").get(0);
                    var htrs = Array.from(headers.querySelectorAll('tr'));
                    var titles = {};
                    var fiterArr = [];
                    for (var j = 0; j < htrs.length; j++) {
                        var hths = Array.from(htrs[j].querySelectorAll("th"));
                        for (var i = 0; i < hths.length; i++) {

                            var clazz = hths[i].getAttributeNode('class').value;
                            fiterArr[i] = hths[i].getAttributeNode('data-field').value;
                            if (clazz != ' layui-table-col-special' && clazz != 'layui-hide') {
                                //排除居左、具有、隐藏字段
                                //修改:默认字段data-field+i,兼容部分数据表格中不存在data-field值的问题
                                titles[hths[i].getAttributeNode('data-field').value] = hths[i].innerText;
                            }
                        }
                    }
                    var s_code = $("#s_code").val();
                    $.ajax({
                            type:"POST",
                            url:"/warehouse/tray/list?limit=999999",
                            dataType:"json",
                            contentType:'application/x-www-form-urlencoded; charset=UTF-8',
                            data:$('.layui-form').serialize(),
                            success:function(res){
                                  // 假如返回的 res.data 是需要导出的列表数据
                                // 1. 数组头部新增表头
                                res.data.unshift(titles);
                              //  2. 如果需要调整顺序，请执行梳理函数
                                var data = excel.filterExportData(res.data, fiterArr);
                                 //导出excel
                                excel.exportExcel({
                                    sheet1: data
                                }, '托盘表' + new Date().toLocaleString() + '.xlsx', 'xlsx');

                            }
                    });
    }
      

        upload.render({
            elem: '#upload',
            url: '/warehouse/tray/upload',
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
    table.on('tool(trayList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑托盘",
                type : 2,
                content : "/warehouse/tray/edit?id="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回托盘列表', '.layui-layer-setwin .layui-layer-close', {
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
            layer.confirm("你确定要删除该托盘么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/warehouse/tray/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('tray-table', t);
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
        elem: '#tray-table',
        even: true,
        url:'/warehouse/tray/list',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
          title: '提示'
          ,layEvent: 'LAYTABLE_TIPS'
          ,icon: 'layui-icon-tips'
        }],
        title:'客户托盘表',
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
            {field:'code',        title: '编号'   },
            {field:'subsectionA',        title: '分段A'   },
            {field:'subsectionB',        title: '分段B'   },
            {field:'subsectionC',        title: '分段C'   },
            {field:'subsectionD',        title: '分段D'   },
            {field:'subsectionE',        title: '分段E'   },
            {field:'subsectionF',        title: '分段F'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#trayBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
        addUser : function(){
            addIndex = layer.open({
                title : "添加托盘",
                type : 2,
                content : "/warehouse/tray/add",
                success : function(layero, addIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回托盘列表', '.layui-layer-setwin .layui-layer-close', {
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
            var checkStatus = table.checkStatus('tray-table'),
                data = checkStatus.data;
            if(data.length > 0){
                layer.confirm("你确定要删除这些托盘么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/warehouse/tray/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('tray-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的托盘",{time:1000});
            }
        }
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('tray-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});