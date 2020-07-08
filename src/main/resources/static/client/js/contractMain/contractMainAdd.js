Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};


//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  
  //执行一个laydate实例
  laydate.render({
    elem: '#startTime' //指定元素
     ,value: new Date()
  });
  var laydate2 = layui.laydate;
  
  //执行一个laydate实例
  laydate2.render({
    elem: '#overTime' //指定元素
    ,value: new Date()
  });
});
 
window.viewObj = {
        tbData: [{
            tempId: new Date().valueOf(),
            type: 2,
            name: '测试项名称',
            state: 1,
            optionId:null,
        }],
        typeData:JSON.parse(document.getElementById('periodStatus').value),
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

        //上传控件
        upload.render({
            elem: '#uploadPDF',
            url: '/admin/system/user/uploadPDF',
            field: 'file',
            done: function (res) {
                //如果上传失败
                if (res.success === false) {
                    return layer.msg('上传失败');
                }else{
                    layer.msg("上传成功",{time:1000},function () {
                        $("input[name='fileId']").val(res.data.fileId);
                        $("input[name='fileName']").val(res.data.name);
                        $("#uploadText").html(res.data.name+"上传成功");
                    })
                }
            }
        });            
        //数据表格实例化           
        var tbWidth = $("#tableRes").width();
        var layTableId = "layTable";
        var tableIns = table.render({
            elem: '#dataTable',
            id: layTableId,
            data: viewObj.tbData,
            width: tbWidth,
            page: false,
            loading: true,
            even: false, //不开启隔行背景
            limit: Number.MAX_VALUE, // 数据表格默认全部显示
            cols: [[
                {title: '序号', type: 'numbers'},
                {field: 'optionId', title: '选项', templet: function(d){
                    var options = viewObj.renderSelectOptions(viewObj.optionData, {valueField: "id", textField: "name", selectedValue: d.optionId});
                    return '<a lay-event="optionId"></a><select name="optionId" lay-filter="optionId"><option  value="">请选择</option>' + options + '</select>';
                }},
                {field: 'type', title: '结算方式', templet: function(d){
                    var options = viewObj.renderSelectOptions(viewObj.typeData, {valueField: "value", textField: "name", selectedValue: d.type});
                    return '<a lay-event="type"></a><select name="type" lay-filter="type"><option  value="">请选择结算方式</option>' + options + '</select>';
                }},
                {field: 'money',title: '金额', edit: 'number' ,event:"checkTest"},
                               
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
                console.log(oldData);
                var newRow = {tempId: new Date().valueOf(),optionId:null,money:0, type: null};
                oldData.push(newRow);
                tableIns.reload({
                    data : oldData
                });
            },
            updateRow: function(obj){
                var oldData = table.cache[layTableId];              
                console.log(oldData);
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
                console.log(oldData);   
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(!row.type){
                        layer.msg("检查每一行，请选择分类！", { icon: 5 }); //提示
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
        form.on('select(type)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='type']").trigger("click");
        });
        //监听select下拉选中事件
        form.on('select(optionId)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='optionId']").trigger("click");
        });
         //监听工具条
        table.on('tool(dataTable)', function (obj) {
            var data = obj.data, event = obj.event, tr = obj.tr; //获得当前行 tr 的DOM对象;
            switch(event){
                case "type":
                    //console.log(data);
                    var select = tr.find("select[name='type']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个分类", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }
                        $.extend(obj.data, {'type': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;
                 case "optionId":
                    //console.log(data);
                    var select = tr.find("select[name='optionId']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个分类", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }
                        $.extend(obj.data, {'optionId': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;    
                case "money":
                    var stateVal = tr.find("input[name='money']").val();
                    $.extend(obj.data, {'money': stateVal}) 
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
        form.on('submit(addcontractMain)',function(data){
        activeByType('save');    //更新行记录对象
      
        data.field.detailSet = table.cache[layTableId];  
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/client/contractMain/add",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("合同添加成功！",{time:1000},function(){
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

