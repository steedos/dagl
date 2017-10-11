var grid;

$(function(){
	initGrid();
	refreshGrid();
	
	initMethod();
});



function initGrid(){
	grid = $.fn.DtGrid.init({
		loadURL : Util.getRootPath() + "/w/dataCenter/getImportCenterList",
	    ajaxLoad : true,
	    gridContainer : 'tableImportDiv',
	    toolbarContainer : 'pagingImportDiv',
	    lang : 'zh-cn',
	    tools : '',
	    pageSize : 20,
	    check : true,
	    pageSizeLimit : [20,50,100,300],
		columns : [
			{
				id:'tablenamecn',
				title : '<b>表名</b>',
				type:"string",
				hideType:'xs',
				columnClass:'text-center'
			},
			{
				id:'filename',
				title : '<b>文件名</b>',
				type:"string",
				hideType:'xs',
				columnClass:'text-center'
			},
			{
				id:'status',
				title : '<b>状态</b>',
				type:"string",
				hideType:'xs',
				columnClass:'text-center',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					if(record.status == '0'){
						return '未导入';
					}else{
						return '已导入';
					}
				}
			},
			{
				id:'id',
				title : '<b>操作</b>',
				type:"string",
				hideType:'xs',
				columnClass:'text-center',
				isContentEditable: false,
				resolution:function(value, record, column, grid, dataNo, columnNo){
					var btn = "";
						btn += '<a title="删除" class="btn btn-primary btn-xs delete" data-id="'+record.id+'">删除</a>';
					return btn;
				}
			},
		]
	});
}

function refreshGrid() {
	grid.parameters = getParameters();
	grid.load();
}
function getParameters() {
}

function initMethod(){
	$("button.addImportCenter").on("click",function(){
		var options = {};
		options.url = Util.getRootPath() + "/w/dataCenter/addImportCenterView";
		options.title = "增加导入数据";
    	window.parent.showModal(options);
	});
	
	
	
		$("#tableImportDiv").on("click","a.delete",function(){
			var id = $(this).attr("data-id");
			$.ajax({
		        async : false,
		        type: 'POST',
		        dataType : "json",
		        data: {},
				contentType : false,
		        url: Util.getRootPath() + "/w/dataCenter/deleteImportCenter?id="+id,
		        success: function(result){
		        	if(!result.success){
		        		layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
		        	}else{
		        		layer.msg(result.msg,{time: 2000});
		        		
		        		refreshGrid();
		        	}
		        }, 
		        error: function(ex) {
		        	layer.alert("删除请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
		        }
			});
		});
		
		$(".import").on("click",function(){
			var ids = [];
			var records = grid.getCheckedRecords();
			if(records.length==0){
				layer.alert("请勾选数据！", {  skin: 'layui-layer-lan',closeBtn: 0});
				return;
			}
			for(var i=0;i<records.length;i++){
				ids.push(records[i].id);
			} 
			$.ajax({  
			    type : 'POST',  
			    url: Util.getRootPath() +  '/w/dataCenter/batchImport',  
			    contentType : "application/json" ,
			    data : JSON.stringify(ids), 
			    success : function(result) {  
			    	if(!result.success){
		        		layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
		        		
			    	}else{
			    		
			    		var datas = result.datas;
		        		var html = "";
		        		html += '<div style="padding: 20px;position: relative;">';
		        		datas.forEach(function(value,index,array){
		        			html += "<div>"+value+"</div>";
		        		})
		        		html += "</div>";
		        		parent.layer.open({
				              type: 1,
							  title:'导入详情',
				              area: ['700px', '530px'],
				              fixed: false, //不固定
				              maxmin: true,
				              content:html
				        });
		        		
		        		//关闭弹出框
						var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
						parent.layer.close(index);
		        		//刷新表格
		        		refreshGrid();
		        	}
			    }, 
		        error: function(ex) {
		        	layer.alert("批量导入请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
		        } 
			}); 
			
		});
}