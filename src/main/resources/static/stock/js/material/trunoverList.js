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


    t = {
        elem: '#trunover-table',
        even: true,
        url:'/stock/material/listTrunover',
        method:'post',
        toolbar: true ,
        totalRow: true,
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
            {type:'checkbox', totalRowText: '合计'},
           /* {field:'id',        title: 'ID'   },*/
            {field:'systemCode',        title: '品项编码'   },
            {field:'itemName',        title: '名称' ,    width:'15%'  },
            {field:'batch',        title: '批次号' ,    width:'10%'   },
            {field:'numZ',title:'数量(整)',align:'center',width:100},
            {field:'number',title:'数量(零)',align:'center',width:100, totalRow: true},
            {field:'rate',title:'换算率',align:'center',width:100},
            {field:'fromTypeStr',    align:'center'  ,  title: '来源'   },
            {field:'fromCode',    align:'center'  ,  title: '来源编码'   },
            {field:'createDate',   align:'center'   ,  title: '创建日期'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#materialBar'}
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
        table.reload('trunover-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});