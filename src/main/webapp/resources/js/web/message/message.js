//存储附件的集合
var curFiles = [];

$(function(){
	initMethod();
	
	initTable();
	
	initFileBtn();
	
});

//初试化方法
function initMethod(){
	//增加信息
	$("button.add").on("click",function(){
		$('[name="messageId"]').val("");
		$("input").val("").removeAttr("readonly");
		$("button.dataUpdate").show();
		$("#messageUpdate").modal("show");
	});
	//点击查看事件
	$("#messageTable").on("click","a.looking",function(){
		
		var id = $(this).attr("data-id");
		$("button.dataUpdate").hide();
		//$('[name="messageId"]').val(id);
		$("#messageUpdate").modal("show");
		//根据资料收集id获得相应信息
		$.ajax({
			url : Util.getRootPath()+"/w/message/getMessageById",
			type:'POST', //GET
			async:false,
			data:{
				id:id
			},
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				//输入框赋值
				var dataInfo = data;
				$('[name="type"]').val(dataInfo.data.type).attr("readonly","readonly");
				$('[name="sendName"]').val(dataInfo.data.sendUser.name).attr("readonly","readonly");
				$('[name="sendTime"]').val(dataInfo.data.sendTime).attr("readonly","readonly");
				$('[name="content"]').val(dataInfo.data.content).attr("readonly","readonly");
				refreshGrid();
			},
			error:function(ex) {}
		});
	});
	
	//点击修改事件
	$("#messageTable").on("click","a.update",function(){
		
		var id = $(this).attr("data-id");
		$("input").removeAttr("readonly");
		$("button.dataUpdate").show();
		$('[name="messageId"]').val(id);
		$("#messageUpdate").modal("show");
		//根据资料收集id获得相应信息
		$.ajax({
			url : Util.getRootPath()+"/w/message/getMessageById",
			type:'POST', //GET
			async:false,
			data:{
				id:id
			},
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				//输入框赋值
				var dataInfo = data;
				$('[name="type"]').val(dataInfo.data.type);
				$('[name="sendName"]').val(dataInfo.data.sendUser.name);
				$('[name="sendTime"]').val(dataInfo.data.sendTime);
				$('[name="content"]').val(dataInfo.data.content);
				
			},
			error:function(ex) {}
		});
	});
	
	//删除信息
	$("#messageTable").on("click","a.delete",function(){
		var id = $(this).attr("data-id");
		
		$.ajax({
			url : Util.getRootPath()+"/w/message/deleteMessage",
			type:'POST', //GET
			async:true,
			data:{
				id:id
			},
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				if(data.success){
					layer.msg(data.msg,{time: 2000});
					refreshGrid();
				}else{
					layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
				}
			},
			error:function(ex) {
				layer.alert("删除失败！", {  skin: 'layui-layer-lan',closeBtn: 0});
			}
		});
	});
	
	//保存修改后的信息
	$("button.dataUpdate").on("click",function(){
		var id = $.trim($('[name="messageId"]').val());
		
		//前台校验
		var type = $.trim($('[name="type"]').val());
		if(Util.checkNull(type)){
			layer.alert('消息类型不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var sendName = $.trim($('[name="sendName"]').val());
		if(Util.checkNull(sendName)){
			layer.alert('发送者不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var sendTime = $.trim($('[name="sendTime"]').val());
		if(sendTime.length != 0){
			var reg =  /^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))$/;
			if(!reg.test(sendTime)){
				layer.alert('请输入正确的时间格式，如“xxxx-xx-xx”', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
				return;
			}
		}
		
		var content = $.trim($('[name="content"]').val());
		if(Util.checkNull(content)){
			layer.alert('消息内容不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var data={"id":id,"type":type,"sendName":sendName,"sendTime":sendTime,"content":content};
		$.ajax({
			url : Util.getRootPath()+"/w/message/updateMessage",
			type:'POST', 
			async:true,
			data:data,
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				if(data.success){
					layer.msg(data.msg,{time: 2000});
					$(".modal").modal("hide");
					refreshGrid();
				}else{
					layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
				}
			},
			error:function(ex) {
				layer.alert("操作失败！", {  skin: 'layui-layer-lan',closeBtn: 0});
			}
		});
	});
}


function initTable(){
	grid=$.fn.DtGrid.init({
		loadURL : Util.getRootPath() + "/w/message/getMessageList",
	    ajaxLoad : true,
	    gridContainer : 'messageTable',
	    toolbarContainer : 'pagingMessage',
	    lang : 'zh-cn',
	    tools : '',
	    pageSize : 20,
	    check : true,
	    pageSizeLimit : [20,50,100,300],
	    columns : [
		       		{
		       			id:'type',
		       			title : '<b>消息类型</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'sendUser',
		       			title : '<b>发送者姓名</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			resolution:function(value, record, column, grid, dataNo, columnNo){
		       				
		       				return record.sendUser.name;
		       			}
		       		},
		       		{
		       			id:'sendTime',
		       			title : '<b>消息发送时间</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			resolution:function(value, record, column, grid, dataNo, columnNo){
		       				var date = new Date(value);
		       			    return date.getFullYear()+"年"+(date.getMonth()+1)+"月"+date.getDate()+"日";
			       		}
		       		},
		       		{
		       			id:'isRead',
		       			title : '<b>是否已读</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			resolution:function(value, record, column, grid, dataNo, columnNo){
		       				if("isRead_0"==value){
		       					return "未读";
		       				}else{
		       					return "已读";
		       				}
			       		
		       			}
		       		},
		       		{
		       			id:'id',
		       			title : '<b>操作</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			resolution:function(value, record, column, grid, dataNo, columnNo){
		       			//存储record类，编辑时使用
		       				var html = ''; 	
		       				html += '<div class="btnDiv">';
		       				html += '	<a title="查看" class="btn btn-default btn-xs looking" data-id="'+value+'">查看</a>';
	       					//html += '	<a title="修改" class="btn btn-primary btn-xs update" data-id="'+value+'">修改</a>';
	       					html += '	<a title="删除" class="btn btn-primary btn-xs delete" data-id="'+value+'">删除</a>';
		       				html += '</div>';
			       			
		       				return html;
		       			}

		       		}
		       		]
	});
	refreshGrid();
}

function refreshGrid() {
	grid.parameters = getParameters();
	grid.load();
}
function getParameters() {
	
}

function initFileBtn(){
	//选择附件事件
	
	var attachment = document.getElementById("attachment");
	attachment.onchange = function () {
		var files = attachment.files;
		if (files && files.length) {
			// 原始FileList对象不可更改，所以将其赋予curFiles提供接下来的修改
			curFiles = [];
			Array.prototype.push.apply(curFiles, files);
			console.log(curFiles.length);
			initUpload();
		}
	}
	
	
}


function initUpload(){
	if (curFiles.length > 0) {
		var fileData = new FormData($('mailForm')[0]);
		for (var i = 0, j = curFiles.length; i < j; ++i) {
			fileData.append('attachment[]', curFiles[i]);
		}
		$.ajax({
	        async : false,
	        cache: false,
	        type: 'POST',
	        dataType : "json",
	        data: fileData,
			processData : false,
			contentType : false,
	        url: Util.getRootPath() + "/w/ywgj/uploadFile",
	        success: function(result){
	        	if(!result.success){
	        		layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
	        	}else{
	        		layer.msg(result.msg,{time: 2000});
	        		refreshGrid();
	        	}
	        },
	        error: function(ex) {
	        	layer.alert("上传附件请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
	        }
		});
	}
}