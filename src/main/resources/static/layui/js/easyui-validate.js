/**************************************************************************************
    EasyUi自定义扩展
***************************************************************************************/

/**
 * 表格自适应数据条数
 */
(function(){
	$.extend($.fn.validatebox.defaults.rules, { 
		carNo : { 
	        validator : function(value) {  
	            return /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{0,1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$/.test(value); 
	        }, 
	        message : '车牌号码无效（例：青A12345）' 
	    }
		,
		checkidcd : {// 验证编号
			validator : function(value) {
				return /^\d{0,18}$/i.test(value);
			},
			message : '请输入规范的编号'
		},
		checkip : {// 验证ip
			validator : function(value) {
				return /^((25[0-5]|2[0-4]\d|[01]?\d\d?)($|(?!\.$)\.)){4}$/i.test(value);
			},
			message : '请输入正确的IP'
		},
		checkport : {// 验证编号
			validator : function(value) {
				return /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/i.test(value);
			},
			message : '请输入规范的端口号'
		},
		 checkjd :{
			validator:function(value){
				return /^[-]?(\d|([1-9]\d)|(1[0-7]\d)|(180))(\.\d*)?$/i.test(value); 
			},
			message:"请输入规范的精度"
		},			 
		checkwd :{
			validator:function(value){
				return /^[-]?(\d|([1-8]\d)|(90))(\.\d*)?$/i.test(value); 
			},
			message:"请输入规范的纬度"
		},
		equalsPwd:{
			validator:function(value,param){
				return $(param[0]).val() == value;
			},
			message:"两次输入不一致"
		},
		phoneNum: { //验证手机号    
            validator: function(value, param){  
             return /^1[3-8]+\d{9}$/.test(value);  
            },     
            message: '请输入正确的手机号码。'    
        },
        telNum:{ //既验证手机号，又验证座机号
            validator: function(value, param){  
                return /(^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$)|(^((\d3)|(\d{3}\-))?(1\d{10})$)/.test(value);  
               },     
               message: '请输入正确的电话号码。'
        },
	    oodNumber:{ //验证字母+数字
	        validator: function(value, param){  
               return /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{5,25}$/.test(value);  
              },     
              message: '请输入正确的单号。'  
        },
        number:{//验证数量
        	validator: function(value, param){
        		return /^[1-9]\d*$/.test(value);
        	},
        	message: '请输入正确的数量。'
        },
        oldnumber:{//验证数量 非0
        	validator: function(value, param){
        		if(value==0){
        			return false;
        		}
        		return /^[1-9]+$/.test(value) || /^[0-9]+(\.\d+)?$/.test(value);
        	},
        	message: '请输入正确的数量。'
        },
        
        isNumber:{//验证数值类型，包括整数和浮点数
        	validator:function(value, param){
        		return /^[-\+]?\d+$/.test(value) || /^[-\+]?\d+(\.\d+)?$/.test(value);
        	},
        	message:'请输入数字'
        },
        isTwoNumber:{//验证数值类型，包括整数和浮点数
        	validator:function(value, param){
        		return /^[-\+]?\d+$/.test(value) || /^\d+(?:\.\d{1,2})?$/.test(value);
        	},
        	message:'请输入不超过两位的数字'
        },
        isphone:{//开头为1,11位，只能是数字
        	validator:function(value, param){
        		return /^(1[358]\d{9})$/.test(value);
        	},
        	message:'请输入正确的电话'
        },
        isMoney:{//整数，三位小数
        	validator:function(value, param){
        		return /(^[0-9]+(.[0-9]{1,3})?$)/.test(value);
        	},
        	message:'请输入正确的金额'
        },
        threeMoneynumber:{//验证数量 非0
        	validator: function(value, param){
        		if(value==0){
        			return false;
        		}
        		return /^[1-9]+$/.test(value) || /^[0-9]+(\.\d{1,3})?$/.test(value);
        	},
        	message: '请输入正确的金额。'
        },
        isCard:{//身份证验证
        	validator:function(value, param){
        		return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(value);
        	},
        	message:'请输入正确的身份证号'
        },
        isNum:{//验证非0正浮点数
        	validator:function(value, param){
        		return /^(?!0+(\.0*)?$)\d+(.[0-9]{1,19})?$/.test(value);
        	},
        	message:'请输入正确的数量'
        }
	});
})();