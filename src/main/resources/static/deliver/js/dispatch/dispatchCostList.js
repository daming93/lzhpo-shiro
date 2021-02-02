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
    table.on('toolbar(dispatchCostList)', function(obj) {
        var checkStatus = table.checkStatus(obj.config.id),
            data = checkStatus.data; //获取选中的数据
            switch (obj.event) {
                case 'table_export':
                    exportFile('dispatchCost-table')
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
                            url:"/deliver/dispatch/costList?limit=999999",
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
                                }, '运输计划列表' + new Date().toLocaleString() + '.xlsx', 'xlsx');

                            }
                    });
    }



    //监听工具条
    table.on('tool(dispatchCostList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑运输计划",
                type : 2,
                content : "/deliver/dispatch/costEdit?id="+data.id,
              
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
        }
        if(obj.event === 'dispatchId'){
            var editIndex = layer.open({
                title : "运输计划",
                type : 2,
                content : "/deliver/dispatch/edit?id="+data.dispatch.id.toString(),
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
    table.on('rowDouble(dispatchCostList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "运输计划详情",
            type : 2,
            content : "/deliver/dispatch/costEdit?id="+data.id,
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
    t = {
        elem: '#dispatchCost-table',
        even: true,
        url:'/deliver/dispatch/costList',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter',  'print'],
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
             { field:'dispatchId' ,event:"dispatchId",title:'运输计划单号',width:'20%',templet: function(d){   
                    if(d.dispatch.code!=null){
                        return  '<div><a> '+ d.dispatch.code+' </a></div>'
                    }else{
                         return  '暂无'
                    }
                }
            },
            { field:'dispatchId' ,title:'司机姓名',templet: function(d){   
                    if(d.dispatch.driverName!=null){
                        return  '<div>'+ d.dispatch.driverName+'</div>'
                    }else{
                         return  '暂无'
                    }
                }
            },
            { field:'dispatchId' ,title:'车牌号',templet: function(d){   
                    if(d.dispatch.vehicleCode!=null){
                        return  '<div>'+ d.dispatch.vehicleCode+'</div>'
                    }else{
                         return  '暂无'
                    }
                }
            },
            {field:'pointPrice',  title: '点费单价'}, 
            {field:'minPoint',        title: '最小计算点数'   },
            {field:'pointNum',        title: '点数'    },
            {field:'moeny',        title: '费用'    },
            {field:'auditMoeny',  title: '核算费用'}, 
            {field:'remarks',        title: '备注'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#dispatchCostBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
   
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('dispatchCost-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});