//存储附件的集合
var dicGrid;
var recordId = "";
$(function(){
	initDicMethod();
	
	initDicTable();
	
});

//初试化方法
function initDicMethod(){
	//增加信息
	$("button.addDicGrid").on("click",function(){
		$('[name="dictionaryId"]').val("");
		$("input").val("");
		//$("button.dataUpdate").show();
		$("h4.modal-title").text("字典项添加");
		$("#dictionaryUpdate").modal("show");
	});

	//点击修改事件
	$("#dictionaryTable").on("click","a.update",function(){
//		var index = layer.load(1, {shade: [0.1,'#FFFFFF']});
		var id = $(this).attr("data-id");
//		$("input").removeAttr("readonly");
//		$("button.dataUpdate").show();
		$("h4.modal-title").text("字典项修改");
		$('[name="dictionaryId"]').val(id);
		$("#dictionaryUpdate").modal("show");
		//根据资料收集id获得相应信息
		$.ajax({
			url : Util.getRootPath()+"/w/dicmanager/getDictionaryById",
			type:'POST', //GET
			async:false,
			data:{
				id:id
			},
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				//输入框赋值
				var dataInfo = data;
				$('[name="dno"]').val(dataInfo.data.dno);
				$('[name="dname"]').val(dataInfo.data.dname);
				$('[name="ddescr"]').val(dataInfo.data.ddescr);
//				layer.close(index);
				
			},
			error:function(ex) {}
		});
	});
	
	//删除信息
	$("#dictionaryTable").on("click","a.delete",function(){
		var id = $(this).attr("data-id");
		
		$.ajax({
			url : Util.getRootPath()+"/w/dicmanager/deleteDictionary",
			type:'POST', //GET
			async:true,
			data:{
				id:id
			},
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				if(data.success){
					layer.msg(data.msg,{time: 2000});
					refreshDicGrid();
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
		var id = $.trim($('[name="dictionaryId"]').val());
		
		//前台校验
		var dno = $.trim($('[name="dno"]').val());
		if(Util.checkNull(dno)){
			layer.alert('字典项编号不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var dname = $.trim($('[name="dname"]').val());
		if(Util.checkNull(dname)){
			layer.alert('字典项名称不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		var ddescr = $.trim($('[name="ddescr"]').val());
		if(Util.checkNull(ddescr)){
			layer.alert('字典项描述不能为空', {skin: 'layui-layer-lan',closeBtn: 0,anim: 5});
			return;
		}
		
		var index = layer.load(1, {shade: [0.1,'#FFFFFF']});
		var data={"id":id,"dno":dno,"dname":dname,"ddescr":ddescr};
		$.ajax({
			url : Util.getRootPath()+"/w/dicmanager/updateDictionary",
			type:'POST', 
			async:true,
			data:data,
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				if(data.success){
					layer.close(index);
					layer.msg(data.msg,{time: 2000});
					$(".modal").modal("hide");
					refreshDicGrid();
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


function initDicTable(){
	dicGrid=$.fn.DtGrid.init({
		loadURL : Util.getRootPath() + "/w/dicmanager/getDictionaryList",
	    ajaxLoad : true,
	    gridContainer : 'dictionaryTable',
	    toolbarContainer : 'pagingDictionary',
	    lang : 'zh-cn',
	    tools : '',
	    pageSize : 20,
	    check : false,
	    pageSizeLimit : [20,50,100,300],
	    onRowClick:function(value,record,column,grid,dataNo,columnNo,Row,row,extraRow,e){
	    	$('[name="dictionaryId"]').val(record.id);
	    	recordId = record.dno;
	    	refreshDicValueGrid();
	    },
	    columns : [
		       		{
		       			id:'dno',
		       			title : '<b>字典项编号</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'dname',
		       			title : '<b>字典项名称</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'ddescr',
		       			title : '<b>字典项描述</b>',
		       			type:"string",
		       			columnClass:'text-center'
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
		       				//html += '	<a title="查看" class="btn btn-default btn-xs add" data-id="'+value+'">增加</a>';
	       					html += '	<a title="修改" class="btn btn-info btn-xs update" data-id="'+value+'">修改</a>';
	       					html += '	<a title="删除" class="btn btn-warning btn-xs delete" data-id="'+value+'">删除</a>';
		       				html += '</div>';
			       			
		       				return html;
		       			}

		       		}
		       		]
	});
	refreshDicGrid();
}

function refreshDicGrid() {
	dicGrid.parameters = getDicParameters();
	dicGrid.load();
}
function getDicParameters() {
	
}

