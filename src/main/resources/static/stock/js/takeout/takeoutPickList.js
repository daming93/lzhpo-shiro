layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t; //表格变量
    //监听工具条
    table.on('tool(takeoutList)', function(obj){
        var data = obj.data;
        if(obj.event === 'print'){
            window.open("/stock/takeout/printPick?id="+data.id);
        }
     
         if(obj.event === "history"){
           $('#showpick').html("");//清空上一个
           $.post("/stock/takeout/history",{"takeoutId":data.id},function (res){
                        var html ='<ul class="layui-timeline">';
                        for(i in res){
                            html+= ' <li class="layui-timeline-item"> <i class="layui-icon layui-timeline-axis">&#xe63f;</i>                <div class="layui-timeline-content layui-text">'
                           +'<h3 class="layui-timeline-title">'+res[i].createDate+'</h3><p>'+
                                '<br>'+res[i].createUser.nickName+'对该张单据进行了'+ res[i].typeStr+'操作'+
                              '</p> </div>  </li>  ';
                        }
                        html += '</ul>';
                        $('#showpick').html(html);
                        layer.open({
                            type: 1,
                            area: '300px',
                            content: $('#showpick') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });       
                    });
         }
         if(obj.event === "startPick"){
            layer.confirm("你确定要开始拣货么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/stock/takeout/startPick",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("成功",{time: 1000},function(){
                                table.reload('takeout-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === "ensurePick"){
            layer.confirm("你确定要完成拣货么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/stock/takeout/ensurePick",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("成功",{time: 1000},function(){
                                table.reload('takeout-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
    });
    table.on('rowDouble(takeoutList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "拣货详情",
            type : 2,
            content : "/stock/takeout/edit?id="+data.id,
            success : function(layero, index){
                setTimeout(function(){
                    layer.tips('点击此处返回拣货列表', '.layui-layer-setwin .layui-layer-close', {
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
        elem: '#takeout-table',
        even: true,
        url:'/stock/takeout/pickList',
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
            {field:'takeoutTime',  title: '出库时间',    width:'10%'}, 
            {field:'clientName',        title: '客户名称'   },
            {field:'clientCode',        title: '客户单号' ,    width:'12%'  },
            {field:'pickingCode',        title: '拣货单号' ,    width:'13%'   },
            {field:'code',        title: '出库单号' ,    width:'13%'   },
            {field:'total',  title: '数量(整)'}, 
            {field:'number',        title: '数量(零)'   },
            {field:'volume',        title: '体积(m³) '   },
            {field:'weight',        title: '重量(kg)'   },
         //   {field:'trayNumber',        title: '件/托(kg)'   },
            {field:'pickStatusStr',        title: '拣货状态'   },
            {field:'remakes',        title: '备注'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#takeoutBar'}
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
        table.reload('takeout-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});