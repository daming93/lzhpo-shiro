layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t; //表格变量

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
    })    
    t = {
        elem: '#income-table',
        even: true,
        url:'/finance/income/list',
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
            {field:'code',        title: '编码'   },
            {field:'basis',        title: '依据' ,    width:'15%'  },
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