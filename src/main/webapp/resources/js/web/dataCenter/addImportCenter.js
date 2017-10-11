//存储附件的集合
var savFiles = [];
//原文挂接附件的集合
var ywFiles = [];

$(function(){
	
	initMethod();
	
	initFileBtn();
	
	listenSubmit();
});


function initMethod(){
	//删除资料
	$(".file-div").on("click", ".file-remove", function () {
		var $this = $(this);
		layer.confirm('是否删除该资料？', {
			  skin: 'layui-layer-lan',
			  btn: ['删除','取消'] //按钮
			}, function(){
					// 去除该文件
					var name = $this.prev().prev().prev().text();
					savFiles = savFiles.filter(function(file) {
						return file.name !== name;
					});
				$this.parent().parent("div").remove();
				layer.msg('已删除',{time: 1000});
			}, function(){
				layer.msg('已取消',{time: 1000});
			});
	});
	
	//删除原文
	$(".yw-file-div").on("click", ".file-remove", function () {
		var $this = $(this);
		layer.confirm('是否删除该原文？', {
			  skin: 'layui-layer-lan',
			  btn: ['删除','取消'] //按钮
			}, function(){
					// 去除该文件
					var name = $this.prev().prev().prev().text();
					ywFiles = ywFiles.filter(function(file) {
						return file.name !== name;
					});
				$this.parent().parent("div").remove();
				layer.msg('已删除',{time: 1000});
			}, function(){
				layer.msg('已取消',{time: 1000});
			});
	});
	
	
	$("a.closeup").on("click",function(){
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
         parent.layer.close(index);
	});
	
	$("a.download").on("click",function(){
		var tablename = $('[name="fileSelect"] option:selected').val();
		if(!Util.checkNull(tablename)){
			location.href = Util.getRootPath() + "/w/example/tools/downloadTempletDef/"+tablename;
		}
	});
}

function listenSubmit(){
	var importCenterId;
	layui.use(['form','jquery'], function(){
	    var $ = layui.jquery,
	    form = layui.form();
	    var tablename;
	    var tablenamecn; 
	    form.on('submit(upload)', function(data){
	    	var fileData = new FormData($('mailForm')[0]);
			for (var i = 0, j = savFiles.length; i < j; ++i) {
				fileData.append('attachment[]', savFiles[i]);
			}
			fileData.append('tablename',tablename);
			fileData.append('tablenamecn',$('[name="fileSelect"] option:selected').text());
			$.ajax({
		        async : false,
		        cache: false,
		        type: 'POST',
		        dataType : "json",
		        data: fileData,
				processData : false,
				contentType : false,
		        url: Util.getRootPath() + "/w/dataCenter/uploadImportFile",
		        success: function(result){
		        	console.log(result);
		        	if(!result.success){
		        		layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
		        	}else{
		        		console.log(result.data);
		        		importCenterId = result.data;
		        	}
		        },
		        error: function(ex) {
		        	layer.alert("上传档案请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
		        }
			});
				
	    	
			var fileData = new FormData($('mailForm')[0]);;
			if(ywFiles.length == 0){
				var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
        		layer.msg("导入成功。",{time: 2000});
 	            parent.layer.close(index);
        		parent.mainIframe.refreshGrid();
				return false;
			}
			for (var i = 0, j = ywFiles.length; i < j; ++i) {
				fileData.append('attachment[]', ywFiles[i]);
			}
			fileData.append('dataCollectId',importCenterId);
			$.ajax({
		        async : false,
		        cache: false,
		        type: 'POST',
		        dataType : "json",
		        data: fileData,
				processData : false,
				contentType : false,
		        url: Util.getRootPath() + "/w/dataCenter/uploadFile",
		        success: function(result){
		        	if(!result.success){
		        		layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
		        	}else{
		        		var zlsc = parent.layer.getFrameIndex(window.name); //获取窗口索引
		        		layer.msg(result.msg,{time: 2000});
		 	            parent.layer.close(zlsc);
		        		parent.mainIframe.refreshGrid();
		        	}
		        },
		        error: function(ex) {
		        	layer.alert("上传原文请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
		        }
			});
	    	return false;
	    });
	    
	    
	    form.on('select(fileSelect)', function(data1){
			tablename = data1.value;
		});
	});
	
}




function initFileBtn(){
	//选择附件事件
	var attachment = document.getElementById("attachment");
	if (attachment != null) {
		attachment.onchange = function() {
			if(attachment.files.length>1 || $(".file-div").children('div').length>0){
				layer.alert("一次只能上传一次", {  skin: 'layui-layer-lan',closeBtn: 0});
				return;
			}
			var addFiles = attachment.files;
			if (addFiles && addFiles.length) {
				// 原始FileList对象不可更改，所以将其赋予curFiles提供接下来的修改
				Array.prototype.push.apply(savFiles, addFiles);
			}
			var $fileDiv = $(".file-div");
			$(".file-none").remove();
			var html = '';
			for (var i = 0; i < addFiles.length; i++) {
				var wjdx = Util.bytesToSize(addFiles[i].size);
				
				html += '<div>';
				html += '	<div>';
				html += '	<i class="fa fa-file"></i>';
				html += '	<a class="file">'+addFiles[i].name+'</a>';
				html += '	<span class="file-size">'+wjdx+'</span>';
				html += '	<span class="file-download" style="display:none;" title="下载"><i class="fa fa-download"></i></span>';
				html += '	<span class="file-remove"><i class="fa fa-close fa-lg"></i></span>';
				html += '	</div>';
				html += '</div>';
				
			}
			$fileDiv.append(html);
		}
	}
	
	//选择批量挂接事件
	var gjAttachment = document.getElementById("gjAttachment");
	if (gjAttachment != null) {
		gjAttachment.onchange = function() {
			var addFiles = gjAttachment.files;
			if (addFiles && addFiles.length) {
				// 原始FileList对象不可更改，所以将其赋予curFiles提供接下来的修改
				Array.prototype.push.apply(ywFiles, addFiles);
			}
			var $fileDiv = $(".yw-file-div");
			var html = '';
			for (var i = 0; i < addFiles.length; i++) {
				var wjdx = Util.bytesToSize(addFiles[i].size);
				
				html += '<div>';
				html += '	<div>';
				html += '	<i class="fa fa-file"></i>';
				html += '	<a class="file">'+addFiles[i].name+'</a>';
				html += '	<span class="file-size">'+wjdx+'</span>';
				html += '	<span class="file-download" style="display:none;" title="下载"><i class="fa fa-download"></i></span>';
				html += '	<span class="file-remove"><i class="fa fa-close fa-lg"></i></span>';
				html += '	</div>';
				html += '</div>';
				
			}
			$fileDiv.append(html);
		}
	}
	
	
}