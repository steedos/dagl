//存储附件的集合
var grid;

$(function(){
	initMethod();
	
	initTable("");
	
//	initFileBtn();
	
});

//初试化方法
function initMethod(){
	//添加借阅信息
	$("button.add").on("click",function(){
    	parent.layer.open({
            type: 2,
			title:"添加借阅信息",
            area: ['700px', '530px'],
            fixed: false, //不固定
            maxmin: true,
            content: Util.getRootPath() + "/w/fileBorrowing/jyxx/toAddJyxx",
          });
	})
	
	 //点击到处按钮
    $("button.export").on("click",function(){
    	var records = grid.getCheckedRecords();//记录
    	var pager = new Object();
		pager.parameters = grid.parameters;
		var dtGridPager = JSON.stringify(pager);//筛选、排序条件
    	if (records.length==0) {
    		//打包导出
			location.href = Util.getRootPath() + "/w/fileBorrowing/exportFileBorrow/null/"+dtGridPager;
		}else{
			var ids = [];
			for(var i = 0;i<records.length;i++){
				ids.push(records[i].id);
			}
			ids = ids.join(',');
			//打包导出
			location.href = Util.getRootPath() + "/w/fileBorrowing/exportFileBorrow/"+ids+"/"+dtGridPager;
		}
    	
    });
	
	
	//增加信息
	/*$("button.add").on("click",function(){
		//$('[name="messageId"]').val("");
		//$("input").val("").removeAttr("readonly");
		//$("button.dataUpdate").show();
		$("input").val("");
		$("#jyxxSave").modal("show");
	});*/
	//归还借阅
	$("button.return").on("click",function(){
		var business = grid.getCheckedRecords();
		if(business.length==0){
			layer.alert("请选择归还信息！", {skin: 'layui-layer-lan',closeBtn: 0});
			return;
		}
		var jyxg = "";
		layer.prompt({title: '请填写借阅效果，并确认！', formType:2}, function(text, index){
		    jyxg = text;
		    if($.trim(text).length == 0){
		    	layer.msg("输入内容不能为空！");
		    }else{
		    	var businessIds = [];
				for(var i=0;i<business.length;i++){
					businessIds.push(business[i].id);
				}
				var businessStr = businessIds.join();
				$.ajax({
					url:Util.getRootPath()+"/w/fileBorrowing/fileReturn",
					type:'POST',
					data:{
						businessStr:businessStr,
						jyxg:jyxg
					},
					dataType:'json',
					success:function(data){
						if(data.success){
							layer.msg(data.msg,{time: 2000});
							refreshGrid();
						}else{
							layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
						}
					},
					error:function(fx){}
				});
				layer.close(index);
		    }
		});
			    
		
	});
	
	//查询
	$("button.query").on("click",function(){
		var options = {}
		options.url = Util.getRootPath() + "/w/fileBorrowing/jyxxQueryView";
		options.title = "题名查询";
		
    	window.parent.showModal(options);
	});
	
	//点击查看事件
	$("#jyxxTable").on("click","a.looking",function(){
	    	var tablename = $('[name="tablename"]').val();
	    	var id = $.trim($(this).attr("data-id"));
	    	var options = {}
	    	var url = Util.getRootPath() + "/w/fileBorrowing/jyxx/toModifyJyxx?id="+id;
	    	options.url = url;
	    	options.title = "借阅信息详情";
	    	window.parent.showModal(options);
	 });
		
		
		
		/*var id = $(this).attr("data-id");
		//$("button.dataUpdate").hide();
		//$('[name="messageId"]').val(id);
		$("input").val("");
		$('[name="jyxxId"]').val(id);
		$("#jyxxSave").modal("show");
		//根据资料收集id获得相应信息
		$.ajax({
			url : Util.getRootPath()+"/w/fileBorrowing/getJyxxById",
			type:'POST', //GET
			async:false,
			data:{
				id:id
			},
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				//输入框赋值
				var dataInfo = data;
				$('[name="tm"]').val(dataInfo.data.tm);
				$('[name="dh"]').val(dataInfo.data.dh);
				$('[name="wh"]').val(dataInfo.data.wh);
				$('[name="fh"]').val(dataInfo.data.fh);
				$('[name="jymd"]').val(dataInfo.data.jymd);
				$('[name="jyr"]').val(dataInfo.data.jyr);
				$('[name="jbr"]').val(dataInfo.data.jbr);
				$('[name="pzr"]').val(dataInfo.data.pzr);
				$('[name="jyxg"]').val(dataInfo.data.jyxg);
				$('[name="jgmc"]').val(dataInfo.data.jgmc);
				$('[name="qwbs"]').val(dataInfo.data.qwbs);
				$('[name="jgmc"]').val(dataInfo.data.jgmc);
				$('[name="jysj"]').val(dataInfo.data.jysj);
				$('[name="nghsj"]').val(dataInfo.data.nghsj);
//				refreshGrid();
			},
			error:function(ex) {}
		});
	});*/

	//删除信息
	$("#jyxxTable").on("click","a.delete",function(){
		var id = $(this).attr("data-id");
		
		$.ajax({
			url : Util.getRootPath()+"/w/fileBorrowing/deleteJyxx",
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
	
	//保存添加信息
	$("button.dataSave").on("click",function(){
		var id = $.trim($('[name="jyxxId"]').val());
		
		var tm = $.trim($('[name="tm"]').val());
		var wh = $.trim($('[name="wh"]').val());
		var pzr = $.trim($('[name="pzr"]').val());
		var jyxg = $.trim($('[name="jyxg"]').val());
		var jgmc = $.trim($('[name="jgmc"]').val());
		var qwbs = $.trim($('[name="qwbs"]').val());
		
		//前台校验
		var dh = $.trim($('[name="dh"]').val());
		if(Util.checkNull(dh)){
			layer.alert('档号不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var jyrid = $.trim($('[name="jyrid"]').val());
//		if(Util.checkNull(jyrid)){
//			layer.alert('借阅人id不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
//			return;
//		}
		var fh = $.trim($('[name="fh"]').val());
		if(Util.checkNull(fh)){
			layer.alert('份号不能为空且为数字', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var jymd = $.trim($('[name="jymd"]').val());
		if(Util.checkNull(jymd)){
			layer.alert('借阅目的不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var jyr = $.trim($('[name="jyr"]').val());
		if(Util.checkNull(jyr)){
			layer.alert('借阅人不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var jbr = $.trim($('[name="jbr"]').val());
		if(Util.checkNull(jbr)){
			layer.alert('经办人不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var jysj = $.trim($('[name="jysj"]').val());
		if(Util.checkNull(jysj)){
			layer.alert('借阅时间不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var nghsj = $.trim($('[name="nghsj"]').val());
		if(Util.checkNull(nghsj)){
			layer.alert('拟归还时间不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		
//		var sendTime = $.trim($('[name="sendTime"]').val());
//		if(sendTime.length != 0){
//			var reg =  /^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))$/;
//			if(!reg.test(sendTime)){
//				layer.alert('请输入正确的时间格式，如“xxxx-xx-xx”', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
//				return;
//			}
//		}
		var data={"id":id,"tm":tm,"dh":dh,"jyrid":jyrid,"wh":wh,"fh":fh,"jymd":jymd,"jyr":jyr,"pzr":pzr,"jbr":jbr,
				"jyxg":jyxg,"jgmc":jgmc,"qwbs":qwbs,"jysj":jysj,"nghsj":nghsj};
		$.ajax({
			url : Util.getRootPath()+"/w/fileBorrowing/saveJyxx",
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
	
	//保存更新信息
	$("button.dataUpdate").on("click",function(){
		var id = $.trim($('[name="jyxxId"]').val());
		var jymd = $.trim($('[name="ujymd"]').val());
		var bz = $.trim($('[name="ubz"]').val());
		//前台校验
		var nghsj = $.trim($('[name="unghsj"]').val());
		if(Util.checkNull(nghsj)){
			layer.alert('拟归还时间不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}

		var data = {"id":id,"jymd":jymd,"bz":bz,"nghsj":nghsj};
		
		$.ajax({
			url : Util.getRootPath()+"/w/fileBorrowing/saveJyxx",
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
	


function initTable(tm){
	grid=$.fn.DtGrid.init({
		loadURL : Util.getRootPath() + "/w/fileBorrowing/getJyxxList",
	    ajaxLoad : true,
	    gridContainer : 'jyxxTable',
	    toolbarContainer : 'pagingJyxx',
	    lang : 'zh-cn',
	    tools : '',
	    pageSize : 20,
	    check : true,
	    pageSizeLimit : [20,50,100,300],
	    tableStyle:'table-layout: fixed;word-break: break-all; word-wrap: break-word;',
	    columns : [
		       		{
		       			id:'jyzt',
		       			title : '<b>借阅状态</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
						resolution:function(value, record, column, grid, dataNo, columnNo){
							if("0"==value){
		       					return '<span title="在借" style="cursor: pointer;">在借</span>';
		       				}else{
		       					return '<span title="已归还" style="cursor: pointer;">已归还</span>';
		       				}
						}
		       			
		       		},
		       		{
		       			id:'tm',
		       			title : '<b>题名</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
						resolution:function(value, record, column, grid, dataNo, columnNo){
							return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
						}
		       			
		       		},
		       		{
		       			id:'dh',
		       			title : '<b>档号</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
						resolution:function(value, record, column, grid, dataNo, columnNo){
							return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
						}
		       		},
		       		{
		       			id:'jymd',
		       			title : '<b>借阅目的</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
						resolution:function(value, record, column, grid, dataNo, columnNo){
							return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
						}
		       		},
		       		{
		       			id:'jyr',
		       			title : '<b>借阅人</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
						resolution:function(value, record, column, grid, dataNo, columnNo){
							return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
						}
		       		},
		       		{
		       			id:'jysj',
		       			title : '<b>借阅时间</b>',
		       			columnClass:'text-center',
		       			otype:'string', 
		       			columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
						resolution:function(value, record, column, grid, dataNo, columnNo){
							return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
						}

		       		},
		       		{
		       			id:'nghsj',
		       			title : '<b>拟归还时间</b>',
		       			columnClass:'text-center',
		       			columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
						resolution:function(value, record, column, grid, dataNo, columnNo){
							return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
						}
		       		},
		       		{
		       			id:'lrsj',
		       			title : '<b>录入时间</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
						resolution:function(value, record, column, grid, dataNo, columnNo){
							return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
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
	       					html += '	<a title="删除" class="btn btn-primary btn-xs delete" data-id="'+value+'">删除</a>';
		       				html += '</div>';
			       			
		       				return html;
		       			}

		       		}
		       		]
	});
	refreshGrid(tm);
}

function refreshGrid(tm) {
	grid.parameters = getParameters(tm);
	grid.load();
}
function getParameters(tm) {
	return {
		tm:tm
	};
}

//function initFileBtn(){
//	//选择附件事件
//	
//	var attachment = document.getElementById("attachment");
//	attachment.onchange = function () {
//		var files = attachment.files;
//		if (files && files.length) {
//			// 原始FileList对象不可更改，所以将其赋予curFiles提供接下来的修改
//			curFiles = [];
//			Array.prototype.push.apply(curFiles, files);
//			console.log(curFiles.length);
//			initUpload();
//		}
//	}
//	
//	
//}
//
//
//function initUpload(){
//	if (curFiles.length > 0) {
//		var fileData = new FormData($('mailForm')[0]);
//		for (var i = 0, j = curFiles.length; i < j; ++i) {
//			fileData.append('attachment[]', curFiles[i]);
//		}
//		$.ajax({
//	        async : false,
//	        cache: false,
//	        type: 'POST',
//	        dataType : "json",
//	        data: fileData,
//			processData : false,
//			contentType : false,
//	        url: Util.getRootPath() + "/w/ywgj/uploadFile",
//	        success: function(result){
//	        	if(!result.success){
//	        		layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
//	        	}else{
//	        		layer.msg(result.msg,{time: 2000});
//	        		refreshGrid();
//	        	}
//	        },
//	        error: function(ex) {
//	        	layer.alert("上传附件请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
//	        }
//		});
//	}
//}