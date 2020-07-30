layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t; //表格变量


    //监听工具条
    table.on('tool(materialList)', function(obj){
        var data = obj.data;
        if(obj.event === "history"){
           $('#show').html("");//清空上一个
           $.post("/stock/material/history",{"materialId":data.id},function (res){
                        var html ='<ul class="layui-timeline">';
                        for(i in res){
                            html+= ' <li class="layui-timeline-item"> <i class="layui-icon layui-timeline-axis">&#xe63f;</i>                <div class="layui-timeline-content layui-text">'
                           +'<h3 class="layui-timeline-title">'+res[i].createDate+'</h3><p>'+
                                '<br>'+res[i].createUser.nickName+'在'+ res[i].typeStr+
                              '</p> </div>  </li>  ';
                        }
                        html += '</ul>';
                        $('#show').html(html);
                        layer.open({
                            type: 1,
                            area: '300px',
                            content: $('#show') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });       
                    });
         }
         if(obj.event === "distribution"){
           $('#show').html("");//清空上一个
           $.post("/stock/material/distribution",{"materialId":data.id},function (res){
                        var html ='<ul class="layui-timeline">';
                        for(i in res){
                            html+= ' <li class="layui-timeline-item"> <i class="layui-icon layui-timeline-axis">&#xe63f;</i>                <div class="layui-timeline-content layui-text">'
                           +'<h3 class="layui-timeline-title">'+res[i].createDate+'</h3><p>'+
                                '<br>'+ res[i].typeStr+
                              '</p> </div>  </li>  ';
                        }
                        html += '</ul>';
                        $('#show').html(html);
                        layer.open({
                            type: 1,
                            area: '300px',
                            content: $('#show') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });       
                    });
         }
        
    });
    t = {
        elem: '#material-table',
        even: true,
        url:'/stock/material/list',
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
            {field:'systemCode',        title: '品项编码'   },
            {field:'itemName',        title: '名称' ,    width:'15%'  },
            {field:'batchNumber',        title: '批次号' ,    width:'10%'   },
            { field:'wholeNum',title:'数量(整)',align:'center',width:100},
            { field:'scatteredNum',title:'数量(零)',align:'center',width:100},
            { field:'availableNum',title:'合计',align:'center',width:100},
            { field:'rate',title:'换算率',align:'center',width:100},
            // {field:'materialStatus',    align:'center'  ,  title: '物料冻结状态'   },
            {field:'typeStr',   align:'center'   ,  title: '状态'   },
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
        table.reload('material-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});