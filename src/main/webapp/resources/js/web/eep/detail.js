/* $(function(){
	initMethod();
});*/


/* function initMethod(){
	$("a.doEEP").on("click",function(){
		var records = parent.mainIframe.grid.getCheckedRecords(); 
		var dhs = [];
		records.forEach(function(value,index,array){
			dhs.push(value.id);
		});
		var tablename = $.trim($('[name="tablename"]').val());
		$.ajax({
			type: 'POST',
			url : Util.getRootPath() + "/w/example/eep/doeep",
			contentType:'application/json;charset=utf-8',
			async:false,
			data:JSON.stringify({
				tablename : tablename,
				dhs : dhs
			}),
			dataType:'json',
			success:function(result){
				if(result.success){
					var data =  result.data;
					window.location.href = Util.getRootPath() + "/w/common/downLoadAttach?filePath="+data.realpath+data.filename+"&fileName="+data.filename;
				}
			},
			error:function(){
				
			}
		});
	})
}*/