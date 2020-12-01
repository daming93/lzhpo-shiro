Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
//整转零
function zero(number,rate){
    var arr = number.split(".");
    var a = arr[0]*rate;
    var b = 0;
    if(arr.length==2){
        b = arr[1];
    }
    return parseInt(a)+parseInt(b);
}

//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  //执行一个laydate实例
  laydate.render({
    elem: '#saleReturnTime' //指定元素
  });
});
 
window.viewObj = {
        tbData: [{
            id: new Date().valueOf(),
            systemCode:"",
            itemName:"",
            depot:"",
            tray:"",
            batch:"",
            number:"",
            numL:"",
            rate:"",
            adjustment:""  }],
        trayListData:JSON.parse(document.getElementById('trayList').value),
        depotsData:JSON.parse(document.getElementById('depots').value),
        itemsData:JSON.parse(document.getElementById('items').value),
        renderSelectOptions: function(data, settings){
            settings =  settings || {};
            var valueField = settings.valueField || 'value',
                textField = settings.textField || 'text',
                selectedValue = settings.selectedValue || "";
            var html = [];
            for(var i=0, item; i < data.length; i++){
                item = data[i];
               
                html.push('<option style="" value="');
                html.push(item[valueField]);
                html.push('"');
                if(selectedValue && item[valueField] == selectedValue ){                        
                    html.push(' selected="selected"');
                }           
                html.push('>');     
                html.push(item[textField]);
                html.push('</option>');
            }
            return html.join('');
        }
    };
    
    //layui 模块化引用
    layui.use(['jquery', 'table','upload', 'layer'], function(){

       
        var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer,upload = layui.upload;

        var saleReturnId =     document.getElementById("saleReturnId").value;   

        var tableData=new Array(); // 用于存放表格数据
        $.ajax({
          url: "/stock/saleReturn/selectDetail?returnId="+saleReturnId+"&limit=2000"
          ,type:"get"
          ,async:false
          ,dataType:"json"
          , success: function(result){
              tableData=result.data;
            }
        });
        //数据表格实例化           
        var tbWidth = $("#tableRes").width();
        var layTableId = "layTable";
        var tableIns = table.render({
            elem: '#dataTable',
            id: layTableId,
            data:tableData,
            width: tbWidth,
            page: false,
            loading: true,
            even: false, //不开启隔行背景
            limit: 2000, // 数据表格默认全部显示
            cols: [[
                {title: '序号', type: 'numbers'},

                { field:'itemId',title:'系统物料编号',align:'center',width:160,templet: function(d){
                    var options = viewObj.renderSelectOptions(viewObj.itemsData, {valueField: "id", textField: "code", selectedValue: d.itemId});
                    return '<a lay-event="itemId"></a><select name="itemId"  lay-filter="itemId"><option  value="">请选择</option>' + options + '</select>';
                }},
                { field:'itemName',title:'品项名称',align:'center',width:200},
                { field:'depot',title:'储位',align:'center',width:130,templet: function(d){
                    var options = viewObj.renderSelectOptions(viewObj.depotsData, {valueField: "code", textField: "code", selectedValue: d.depot});
                    return '<a lay-event="depot"></a><select name="depot" lay-filter="depot"><option  value="">请选择</option>' + options + '</select>';
                }},
                { field:'batch',title:'批次',align:'center',width:100,edit: 'select',event:'date',data_field: "dBeginDate"},
                { field:'wholeNum',title:'数量(整)',edit: 'select',event:'wholeNum',align:'center',width:80},
                { field:'scatteredNum',title:'数量(零)',edit: 'select',event:'wholeNum',align:'center',width:80},
                { field:'number',title:'合计',align:'center',width:80},
                { field:'rate',title:'换算率',align:'center',width:100,templet: function(d){
                                return '1*'+d.rate;
                            }},
                {field: 'id', title: '操作', width:70,templet: function(d){
                    return '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del" lay-id="'+ d.id +'"><i class="layui-icon layui-icon-delete"></i>移除</a>';
                }}
            ]],
            done: function(res, curr, count){
                $(".layui-input-date").each(function (i) {
                       layui.laydate.render({
                           elem: this,
                           format: "yyyy-MM-dd",
                           done: function (value, date) {
                               if (res && res.data[i]) {
                                   $.extend(res.data[i], {'batch': value})
                            }
                        }
                    });

                })
            }
        });
        table.on('edit(dataTable)', function(obj){
            var value = obj.value //得到修改后的值
            ,data = obj.data //得到所在行所有键值
            ,field = obj.field; //得到字段
            console.log(data);
            if(obj.field=="scatteredNum"||obj.field=="wholeNum"){
                 obj.update({
                      number:parseInt(data.wholeNum)*parseInt(data.rate)+parseInt(data.scatteredNum)
                 });
            }
         
       });
        //定义事件集合
        var active = {
            addRow: function(){ //添加一行
                var oldData = table.cache[layTableId];
                var newRow = {
                    id: new Date().valueOf(),
                    systemCode:"",
                    itemName:"",
                    depot:"",
                    tray:"",
                    batch:"",
                    number:"",
                    wholeNum:"",
                    scatteredNum:"",
                    rate:"",
                    adjustment:""  };
                oldData.push(newRow);
                tableIns.reload({
                    data : oldData
                });
            },
            updateRow: function(obj){
                var oldData = table.cache[layTableId];              
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(row.id == obj.id){
                        $.extend(oldData[i], obj);
                        return;
                    }
                }
                tableIns.reload({
                    data : oldData
                });
            },
            removeEmptyTableCache: function(){
                var oldData = table.cache[layTableId];      
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(!row || !row.id){
                        oldData.splice(i, 1);    //删除一项
                    }
                    continue;
                }
                tableIns.reload({
                    data : oldData
                });
            },
            save: function(){
                var oldData = table.cache[layTableId];  
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(!row.type){
                        return;
                    }
                }
            }
        }
        function clearNoNum(value) {
            value = value.replace(/[^\d.]/g, "");  //清除“数字”和“.”以外的字符   
            value = value.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的   
            value = value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
            value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3');//只能输入两个小数   
            if (value.indexOf(".") < 0 && value != "") {//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额  
               value = parseFloat(value);
            }
            return value;
        }
        function getListNameById(list,id){
            for(i in list){
                if(list[i].id==id){
                    return list[i].name;
                }
            }
        }
         function getListBrandById(list,id){
            for(i in list){
                if(list[i].id==id){
                    if(list[i].brand){
                        return list[i].brand;
                    }else{
                        return "";
                    }
                }
            }
        }
         function getListRateById(list,id){
            for(i in list){
                if(list[i].id==id){
                    if(list[i].unitRate){
                        return list[i].unitRate;
                    }else{
                        return "";
                    }
                }
            }
        }
                //整转零
        function zero(number,rate){
            rate = (rate+"").substring(2);
            var arr = number.split(".");
            var a = arr[0]*parseInt(rate);
            var b = 0;
            if(arr.length==2){
                b = arr[1];
            }
            return parseInt(a)+parseInt(b);
        }
        //激活事件
        var activeByType = function (type, arg) {
            if(arguments.length === 2){                 
                active[type] ? active[type].call(this, arg) : '';
            }else{
                active[type] ? active[type].call(this) : '';
            }
        }
        
        //注册按钮事件
        $('.layui-btn[data-type]').on('click', function () {
            var type = $(this).data('type');
            activeByType(type);
        });
        
        //监听select下拉选中事件
        form.on('select(depot)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='depot']").trigger("click");
        });
        //监听select下拉选中事件
        form.on('select(itemId)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='itemId']").trigger("click");
           
        });
        form.on('select(tray)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='tray']").trigger("click");
        });
         //监听工具条
        table.on('tool(dataTable)', function (obj) {
            var data = obj.data, event = obj.event, tr = obj.tr; //获得当前行 tr 的DOM对象;
            switch(event){
                case "depot":
                    var select = tr.find("select[name='depot']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个分类", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }

                        $.extend(obj.data, {'depot': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;
                 case "itemId":
                    //console.log(data);
                    var select = tr.find("select[name='itemId']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个品项", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }
                        var itemName = tr.find("td[data-field='itemName'] .layui-table-cell");
                        itemName.html(getListNameById(viewObj.itemsData,selectedVal));
                        var rate = tr.find("td[data-field='rate'] .layui-table-cell");
                        rate.html(getListRateById(viewObj.itemsData,selectedVal));
                        obj.data.rate = rate.html();
                        tr.find("td[data-field='numZ'] .layui-table-cell").html("0");
                        tr.find("td[data-field='number'] .layui-table-cell").html("0");
                        $.extend(obj.data, {'itemId': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;    
                case "tray":
                    //console.log(data);
                    var select = tr.find("select[name='tray']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个分类", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }
                        $.extend(obj.data, {'tray': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;     
                case "date":
                    var stateVal = tr.find("input[name='batch']").val();
                    $.extend(obj.data, {'batch': stateVal}) 
                    activeByType('updateRow', obj.data);    //更新行记录对象
                    break;  
                case "wholeNum":
                   
                    var stateVal = tr.find("td[data-field='wholeNum'] .layui-table-cell").html();
                    var scatteredNum = tr.find("td[data-field='scatteredNum'] .layui-table-cell").html();
                    var number = tr.find("td[data-field='number'] .layui-table-cell");
                    var rate = tr.find("td[data-field='rate'] .layui-table-cell").html().substring(2);
                    number.html(parseInt(stateVal)*parseInt(rate)+parseInt(scatteredNum));
                    $.extend(obj.data, {'wholeNum': stateVal}) ;
                    $.extend(obj.data, {'scatteredNum': scatteredNum}) ;
                    $.extend(obj.data, {'number': parseInt(stateVal)*parseInt(rate)+parseInt(scatteredNum)}) 
                    activeByType('updateRow', obj.data);    //更新行记录对象
                    break;                           
                case "del":
                    layer.confirm('真的删除行么？', function(index){
                      obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                      layer.close(index);
                      activeByType('removeEmptyTableCache');
                    });
                    break; 
                 case "checkTest":
                    $(obj.tr).find(".layui-table-edit").keyup(function () {
                        var $input = $(this), val = $input.val();
                        $input.val(clearNoNum(val));
                    });
                    break;   


            }
        });
//提交数据代码
        form.on('submit(editsaleReturn)',function(data){
        activeByType('save');    //更新行记录对象
      
        data.field.detailSet = table.cache[layTableId];  
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/stock/saleReturn/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("单据添加成功！",{time:1000},function(){
                        //刷新父页面
                        parent.location.reload();
                    });
                }else{
                    layer.msg(res.message);
                }
            }
        });
        return false;
    });
    });

