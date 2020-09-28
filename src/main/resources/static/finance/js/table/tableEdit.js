Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};



 
window.viewObj = {
        tbData: [{
            tempId: new Date().valueOf(),
            type: 2,
            name: '测试项名称',
            state: 1,
            optionId:null,
        }],
            typeData:JSON.parse(document.getElementById('incomeType').value),
        optionData:JSON.parse(document.getElementById('options').value),
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

        var tableId =     document.getElementById("tableId").value;   

        var submitFlag = false;

        var tableData=new Array(); // 用于存放表格数据
        $.ajax({
          url: "/finance/table/selectDetail?tableId="+tableId
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
            data: tableData,
            width: tbWidth,
            page: false,
            loading: true,
            even: false, //不开启隔行背景
            limit: Number.MAX_VALUE, // 数据表格默认全部显示
            cols: [[
                {title: '序号', type: 'numbers'},
                {field: 'optionName', title: '选项', templet: function(d){
                    var options = viewObj.renderSelectOptions(viewObj.optionData, {valueField: "name", textField: "name", selectedValue: d.optionName});
                    return '<a lay-event="optionName"></a><select name="optionName" lay-filter="optionName"><option  value="">请选择</option>' + options + '</select>';
                }},
                {field: 'math', title: '收入类型', templet: function(d){
                    var options = viewObj.renderSelectOptions(viewObj.typeData, {valueField: "value", textField: "name", selectedValue: d.math});
                    return '<a lay-event="math"></a><select name="math" lay-filter="math"><option  value="">请选择结算方式</option>' + options + '</select>';
                }},
                {field: 'defaultMoney',title: '默认金额', edit: 'number' ,event:"checkTest"},
                               
                {field: 'tempId', title: '操作', templet: function(d){
                    return '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del" lay-id="'+ d.tempId +'"><i class="layui-icon layui-icon-delete"></i>移除</a>';
                }}
            ]],
            done: function(res, curr, count){
              //  viewObj.tbData = res.data;
            }
        });
        
        //定义事件集合
        var active = {
            addRow: function(){ //添加一行
                var oldData = table.cache[layTableId];
                var newRow = {tempId: new Date().valueOf(),optionName:null,defaultMoney:0, type: null}; 
                oldData.push(newRow);
                tableIns.reload({
                    data : oldData
                });
            },
            updateRow: function(obj){
                var oldData = table.cache[layTableId];              
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(row.tempId == obj.tempId){
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
                    if(!row || !row.tempId){
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
                    if(!row.math){
                        layer.msg("检查每一行，请选择分类！", { icon: 5 }); //提示
                        return false;
                    }
                    if(!row.optionName){
                        layer.msg("检查每一行，请选择选项！", { icon: 5 }); //提示
                        return false;
                    } 
                }
                submitFlag = true;
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
        form.on('select(math)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='math']").trigger("click");
        });
        //监听select下拉选中事件
        form.on('select(optionName)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='optionName']").trigger("click");
        });
         //监听工具条
        table.on('tool(dataTable)', function (obj) {
            var data = obj.data, event = obj.event, tr = obj.tr; //获得当前行 tr 的DOM对象;
            switch(event){
                case "math":
                    //console.log(data);
                    var select = tr.find("select[name='math']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个分类", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }
                        $.extend(obj.data, {'math': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;
                 case "optionName":
                    //console.log(data);
                    var select = tr.find("select[name='optionName']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个分类", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }
                        $.extend(obj.data, {'optionName': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;    
                case "defaultMoney":
                    var stateVal = tr.find("input[name='defaultMoney']").val();
                    $.extend(obj.data, {'defaultMoney': stateVal}) 
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
        form.on('submit(edittable)',function(data){
       //更新行记录对象
        data.field.detailSets = table.cache[layTableId];  
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/finance/table/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("表单添加成功！",{time:1000},function(){
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

