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
    table.on('toolbar(incomeList)', function(obj) {
        var checkStatus = table.checkStatus(obj.config.id),
            data = checkStatus.data; //获取选中的数据
            switch (obj.event) {
                case 'table_export':
                    exportFile('income-table')
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
                    $.ajax({
                            type:"POST",
                            url:"/finance/income/list?limit=999999",
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
                                }, '财务收入' + new Date().toLocaleString() + '.xlsx', 'xlsx');

                            }
                    });
    }



   //监听工具条
    table.on('tool(incomeList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "审计收入",
                type : 2,
                content : "/finance/income/edit?id="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回收入列表', '.layui-layer-setwin .layui-layer-close', {
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
        if(obj.event === 'tableCode'){
            var content ;
            if(data.tableFrom==1){//路单
                content = "/deliver/wayBill/edit?id="+data.tableId.toString();
            }else if(data.tableFrom==2){//入库
                content = "/stock/storage/edit?id="+data.tableId.toString();
            }else if(data.tableFrom==3){//出库
                content = "/stock/takeout/edit?id="+data.tableId.toString();
            }else if(data.tableFrom==4){//退库
                content = "/stock/saleReturn/edit?id="+data.tableId.toString();
            }
            var editIndex = layer.open({
                title : "查看原表",
                type : 2,
                content : content,
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
        if(obj.event === 'basis'){
            var content ;
            if(data.basis.indexOf("HT") != -1 ){//仓储合同
                content = "/client/contractMain/edit?id="+data.basicId.toString();
            }else if(data.basis.indexOf("PS") != -1 ){//配送合同
                content = "/client/deliverContractMain/edit?id="+data.basicId.toString();
            }
            var editIndex = layer.open({
                title : "查看原表",
                type : 2,
                content : content,
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
    })    
    t = {
        elem: '#income-table',
        even: true,
        url:'/finance/income/list',
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
            {field:'code',        title: '编码'   },
            { field:'basis' ,event:"basis",title:'依据',templet: function(d){   
                    if(d.basicId!=null){
                        return  '<div><a> '+ d.basis+' </a></div>'
                    }else{
                         return  '暂无'
                    }
                }
            },
            { field:'tableCode' ,event:"tableCode",title:'单号',templet: function(d){   
                    if(d.tableCode!=null){
                        return  '<div><a> '+ d.tableCode+' </a></div>'
                    }else{
                         return  '暂无'
                    }
                }
            },
            {field:'clientName',        title: '客户名称' ,    width:'10%'   },
            { field:'optionName',title:'收费项目',align:'center',width:100},
            { field:'moeny',title:'费用',align:'center',width:100},
            { field:'auditMoeny',title:'审计金额',align:'center',width:100},
            { field:'typeStr',title:'收费类型',align:'center',width:100},
            {field:'auditMan',    title: '审计人'   },
            {field:'remarks',    title: '备注'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#incomeBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
  
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
   
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('income-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});