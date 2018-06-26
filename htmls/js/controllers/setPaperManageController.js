//定义模块时引入依赖  
var app=angular.module('app',['ui.bootstrap','toastr']);
app.controller('setPaperManageCtrl', function($rootScope,$scope,$modal,toastr,$window) {
	//返回设置页面
	$scope.returnPage=function(){
		 window.location.href="../../page/setmodule/setmodule.html"; 
	}
	//跳转到添加试卷页面
	$scope.addTestPaper=function(){	
		var modalInstance = $modal.open({
			templateUrl: 'selsubjectModal.html',
			controller: 'selsubjectCtrl',
			size: 'md',
			backdrop:false,
			resolve: {
				/*content: function() {
					return content;
				}*/
			}
		});
		modalInstance.result.then(function(info) {
			if(info){
			$scope.subject=angular.copy(info);//选择的科目
			$scope.testId=execute_testPaper("create_test_id");
			$scope.param = "testId=" + $scope.testId+"&subject="+$scope.subject;
			console.log(JSON.stringify($scope.param))		
			$scope.objectUrl = '../../page/setmodule/addtestPage.html' + '?' + $scope.param;				
			$window.location.href = $scope.objectUrl;	
			}
		}, function() {
			//$log.info('Modal dismissed at: ' + new Date());
		});
		
		
	}
	
	//查询所有试卷
	var _selectPaper = function(){
		var param = {};
		var result = JSON.parse(execute_testPaper("select_paper",JSON.stringify(param)));
		console.log(JSON.stringify(result));
		if(result.ret == 'success'){
			$scope.paperInfoList = result.item;
		}else{
			toastr.error(result.message);
		}
		
	};
	_selectPaper();
	//导入试卷
	$scope.importPaper=function(){
		var modalInstance = $modal.open({
		templateUrl: 'importFile.html',
		controller: 'uploadfileModalCtrl',
		size: 'md',
		backdrop:false
	});

	modalInstance.result.then(function(info) {
		_selectPaper();
	}, function() {
	});
	}
	//编辑试卷
	$scope.editPaper=function(item){
		$scope.param = "atype=" + item.atype + "&describe=" + item.describe + "&id=" + item.id+"&subject=" +item.subject+ "&testId="+item.testId +"&testName=" +item.testName;			
		$scope.objectUrl = '../../page/setmodule/edittestPage.html' + '?' + $scope.param;
		console.log(JSON.stringify(item))
		$window.location.href = $scope.objectUrl;			
		
		//window.location.href="../../page/setmodule/edittestPage.html";
	}
	
	//删除题目 
	$scope.delPaper=function(item){
		var content="删除题目";
		var modalInstance = $modal.open({
			templateUrl: 'sureModal.html',
			controller: 'sureModalCtrl',
			size: 'sm',
			backdrop:false,
			resolve: {
				content: function() {
					return content;
				}
			}
		});
		modalInstance.result.then(function(info) {
			var param={
				id:item.id
			}
			console.log(JSON.stringify(param))
			param=JSON.stringify(param)			
			$scope.result=JSON.parse(execute_testPaper("delete_paper",param));
			if($scope.result.ret=='success'){
				toastr.success($scope.result.message);
				_selectPaper();
			}else{
				toastr.error($scope.result.message);
			}
		}, function() {
			//$log.info('Modal dismissed at: ' + new Date());
		});
	}
})
//选择科目控制器
app.controller('selsubjectCtrl',function($rootScope,$scope,$modal,$modalInstance,toastr){
	$scope.sujectlists=[];//科目数组
	$scope.sujectlists= JSON.parse(execute_testPaper("get_subject"));
	if($scope.sujectlists.length>0){
		$scope.selsubject=$scope.sujectlists[0];
		$scope.selsubject1=angular.copy($scope.selsubject);
	}	
	$scope.ok = function() {
		var param=$scope.selsubject;
		$modalInstance.close(param);
	}
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	}
})

app.config(['$locationProvider', function($locationProvider) {  
	    //$locationProvider.html5Mode(true);  
 	$locationProvider.html5Mode({
	enabled: true,
	requireBase: false
	});
}]);
//添加试卷控制器
app.controller('addPaperManageCtrl',function($rootScope,$scope,$modal,toastr,$location,$window){
	if($location.search()){
		$scope.testId=$location.search().testId;	
		$scope.subject=$location.search().subject;
	}
	$scope.paperInfo={
		testName:'',//试卷名称
		describe:''//试卷描述
	}
	$scope.subjectList=[];//题目数组
	//返回返回试卷管理页面
	$scope.returnPage=function(){
		 window.location.href="../../page/setmodule/testPaperManage.html"; 
	}
	//添加题目
	$scope.addSubject=function(){
		var modalInstance = $modal.open({
			templateUrl: 'addSubjectModal.html',
			controller: 'addSubjectModalCtrl',
			size: 'md',
			backdrop:false,
			resolve: {
				infos: function() {
					return $scope.testId;
				}
			}
		});
		modalInstance.result.then(function(info) {
			var param={
				testId:$scope.testId,
			}
			console.log(JSON.stringify(param))
			$scope.result= JSON.parse(execute_testPaper("select_question",JSON.stringify(param)));
			if($scope.result.ret=='success'){
			$scope.subjectList=$scope.result.item;
			
			}else{
				toastr.error($scope.result.message);
			}
			
		}, function() {
			//$log.info('Modal dismissed at: ' + new Date());
		});
	}
	//保存试卷
	$scope.savePaper=function(){
		var param={
			testId:$scope.testId,
			subject:$scope.subject,
			testName:$scope.paperInfo.testName,//试卷名称
			describe:$scope.paperInfo.describe,//试卷描述			
		}
		$scope.result= JSON.parse(execute_testPaper("insert_paper",JSON.stringify(param)));
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			/*$scope.lastUrl="../../page/setmodule/testPaperManage.html";
			 $window.location.href=$scope.lastUrl;*/
		}else{
			toastr.error($scope.result.message);
			console.log(JSON.stringify($scope.result.detail))
		}
	}
	
	
})

//编辑试卷控制器
app.controller('editPaperManageCtrl',function($rootScope,$scope,$modal,$location,$window,toastr){
	//console.log(JSON.stringify($location.search()))
	if($location.search()){
		$scope.paperInfo=$location.search();		
	}
	$scope.subjectList=[];//题目数组
	$scope.checkedId=[];
	$scope.onechecked = [];
	//查询该试卷的题目
	var _selectQuestion=function(){		
		var param = {
			testId:$scope.paperInfo.testId
		}
		console.log(JSON.stringify(param));
		$scope.result = JSON.parse(execute_testPaper("select_question",JSON.stringify(param)));
		console.log(JSON.stringify($scope.result));
		if($scope.result.ret == 'success'){
			$scope.subjectList = $scope.result.item;
		}else{
			toastr.error($scope.result.message);
		}
	}
	
	
	//全选
	$scope.selectAll = function(data) {
		if($scope.selected) {
			$scope.onechecked = [];
			angular.forEach($scope.subjectList, function(i) {
				i.checked = true;
				var item = i;
				$scope.checkedId.push(i.id.toString());
				$scope.onechecked.push(item);

			})
		} else {
			angular.forEach($scope.subjectList, function(i) {
				i.checked = false;
				$scope.onechecked = [];
				$scope.checkedId = [];
			})
		}

	};
	//单选
	$scope.selectOne = function(param) {
		$scope.onechecked = [];
		$scope.checkedId = [];
		angular.forEach($scope.subjectList, function(i) {
			var index = $scope.checkedId.indexOf(i.id);
			if(i.checked && index === -1) {
				var item = i;
				$scope.onechecked.push(item);
				$scope.checkedId.push(i.id.toString());

			} else if(!i.checked && index !== -1) {
				$scope.selected = false;
				$scope.onechecked.splice(index, 1);
				$scope.checkedId.splice(index, 1);
			};
		})

		if($scope.subjectList.length === $scope.onechecked.length) {
			$scope.selected = true;
		} else {
			$scope.selected = false;
		}
	}
	var _init=function(){
		_selectQuestion();
	}();
	//添加题目
	$scope.addSubject=function(){
		var modalInstance = $modal.open({
			templateUrl: 'addSubjectModal.html',
			controller: 'addSubjectModalCtrl',
			size: 'md',
			backdrop:false,
			resolve: {
				infos: function() {
					return $scope.paperInfo.testId;
				}
			}
		});
		modalInstance.result.then(function(info) {
			_selectQuestion();
			
		}, function() {
			//$log.info('Modal dismissed at: ' + new Date());
		});
	}
	//编辑题目
	$scope.editSuject=function(item){
		console.log(JSON.stringify(item))
		var modalInstance = $modal.open({
			templateUrl: 'addSubjectModal.html',
			controller: 'editSubjectModalCtrl',
			size: 'md',
			backdrop:false,
			resolve: {
				infos: function() {
					return item;
				}
			}
		});
		modalInstance.result.then(function(info) {
			_selectQuestion();
		}, function() {
			//$log.info('Modal dismissed at: ' + new Date());
		});
	}
	//删除题目 
	$scope.delSuject=function(item){
		var content="删除题目";
		var modalInstance = $modal.open({
			templateUrl: 'sureModal.html',
			controller: 'sureModalCtrl',
			size: 'sm',
			backdrop:false,
			resolve: {
				content: function() {
					return content;
				}
			}
		});
		modalInstance.result.then(function(info) {
			var param=$scope.checkedId;	
			$scope.result=JSON.parse(execute_testPaper("delete_question",JSON.stringify(param)));
			if($scope.result.ret=='success'){
				toastr.success($scope.result.message);
				_selectQuestion();
				$scope.onechecked = [];
				$scope.checkedId = [];
				$scope.selected = false;
			}else{
				toastr.error($scope.result.message);
			}
		}, function() {
			//$log.info('Modal dismissed at: ' + new Date());
		});
	}
	//修改试卷
	$scope.savePaper=function(){
		var param={
			id:$scope.paperInfo.id,
			testId:$scope.paperInfo.testId,
			subject:$scope.paperInfo.subject,
			testName:$scope.paperInfo.testName,//试卷名称
			describe:$scope.paperInfo.describe,//试卷描述			
		}
		console.log(JSON.stringify(param))
		$scope.result= JSON.parse(execute_testPaper("update_paper",JSON.stringify(param)));
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			/*$scope.lastUrl="../../page/setmodule/testPaperManage.html";
			 $window.location.href=$scope.lastUrl;*/
		}else{
			toastr.error($scope.result.message);
			console.log(JSON.stringify($scope.result.detail))
		}
	}
})
//新增题目控制器
app.controller('addSubjectModalCtrl',function($rootScope,$modalInstance,$scope,$modal,toastr,infos){
	$scope.title="新增题目";
	$scope.testInfo={
		questionType:'3',
		selType:'1',
		/*range:'A-D',*/
		trueAnswer:'T'
	}	
	//$scope.idnum=1;
	if(infos){
		$scope.testId=angular.copy(infos);
	}
	$scope.testInfo.questionType1=angular.copy($scope.testInfo.questionType);
	
	//切换答案类型
	$scope.changequesType=function(quesType){
		$scope.testInfo.questionType=quesType;
		if($scope.testInfo.questionType=='0'){			
			$scope.testInfo.selType="1";
			$scope.testInfo.range="A-D";
			$scope.testInfo.selType1=angular.copy($scope.testInfo.selType);
			$scope.testInfo.range1=angular.copy($scope.testInfo.range);
		}
	}
	
	$scope.isLetterRange = function(value, letterRange) {

		var isLetterRange = true;
		alert(value)
		/*if(time && value) {
			isLetterRange = moment(today + ' ' + value).isAfter(today + ' ' + time);
			return isAfter;
		} else {
			return true;
		}*/

	}
	$scope.changeselType=function(selType){
		$scope.testInfo.selType=selType;
		if($scope.testInfo.questionType=='0'){			
			switch($scope.testInfo.selType)
				{
				case '1':
				 if($scope.testInfo.range=='A-D'){
				 	$scope.pattern="/^(A|B|C|D)$/";
				 }else{
				 	$scope.pattern="/^[A-F]$/";
				 }
				  break;
				case '2':
				  if($scope.testInfo.range=='A-D'){
				 	$scope.pattern="/^[A-D]?+$/";
				 }else{
				 	$scope.pattern="/^[A-F]?+$/";
				 }
				  break;
				
				}
		}
	}
	//检验对错
	$scope.changeJudge=function(trueAnswer){
		$scope.testInfo.trueAnswer=trueAnswer;
		if($scope.testInfo.questionType=='0'){
			switch($scope.testInfo.selType)
				{
				case '1':
				 if($scope.testInfo.range=='A-D'){
				 	$scope.pattern="/^[a-zA-Z](A|B|C|D)$/";
				 }else{
				 	$scope.pattern="/^[a-zA-Z][A-F]$/";
				 }
				  break;
				case '2':
				  if($scope.testInfo.range=='A-D'){
				 	$scope.pattern="/^[a-zA-Z][ABCD]+$/";
				 }else{
				 	$scope.pattern="/^[a-zA-Z][ABCDEF]+$/";
				 }
				  break;
				
				}
		}
	}
	$scope.ok = function() {
		var param={
			//id:$scope.idnum,
			testId:$scope.testId,
			questionId:$scope.testInfo.questionId,
			question:$scope.testInfo.question,
			questionType:$scope.testInfo.questionType,
			trueAnswer:$scope.testInfo.trueAnswer,
			range:$scope.testInfo.range
		}
		console.log("参数"+JSON.stringify(param))
		$scope.result= JSON.parse(execute_testPaper("insert_question",JSON.stringify(param)));		
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			$modalInstance.close('success');	
		}else{
			toastr.error($scope.result.message);
		}
		
	}
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	}
})
//编辑题目控制器
app.controller('editSubjectModalCtrl',function($rootScope,$modalInstance,$scope,$modal,toastr,infos){
	$scope.title="编辑试卷";
	if(infos){
		$scope.testInfo=angular.copy(infos);
		if(typeof $scope.testInfo.questionId=='string'){
			$scope.testInfo.questionId=parseInt($scope.testInfo.questionId);
		}
		$scope.testInfo.questionType1=angular.copy($scope.testInfo.questionType);
		$scope.testInfo.range1=angular.copy($scope.testInfo.range);
	}
	//切换答案类型
	$scope.changequesType=function(quesType){
		$scope.testInfo.questionType=quesType;
		if($scope.testInfo.questionType=='0'){			
			$scope.testInfo.selType="1";
			$scope.testInfo.range="A-D";
			$scope.testInfo.selType1=angular.copy($scope.testInfo.selType);
			$scope.testInfo.range1=angular.copy($scope.testInfo.range);
		}
	}
	
	$scope.isLetterRange = function(value, letterRange) {

		var isLetterRange = true;
		alert(value)
		/*if(time && value) {
			isLetterRange = moment(today + ' ' + value).isAfter(today + ' ' + time);
			return isAfter;
		} else {
			return true;
		}*/

	}
	$scope.changeselType=function(selType){
		$scope.testInfo.selType=selType;
		if($scope.testInfo.questionType=='0'){			
			switch($scope.testInfo.selType)
				{
				case '1':
				 if($scope.testInfo.range=='A-D'){
				 	$scope.pattern="/^(A|B|C|D)$/";
				 }else{
				 	$scope.pattern="/^[A-F]$/";
				 }
				  break;
				case '2':
				  if($scope.testInfo.range=='A-D'){
				 	$scope.pattern="/^[A-D]?+$/";
				 }else{
				 	$scope.pattern="/^[A-F]?+$/";
				 }
				  break;
				
				}
		}
	}
	//检验对错
	$scope.changeJudge=function(trueAnswer){
		$scope.testInfo.trueAnswer=trueAnswer;
		if($scope.testInfo.questionType=='0'){
			switch($scope.testInfo.selType)
				{
				case '1':
				 if($scope.testInfo.range=='A-D'){
				 	$scope.pattern="/^[a-zA-Z](A|B|C|D)$/";
				 }else{
				 	$scope.pattern="/^[a-zA-Z][A-F]$/";
				 }
				  break;
				case '2':
				  if($scope.testInfo.range=='A-D'){
				 	$scope.pattern="/^[a-zA-Z][ABCD]+$/";
				 }else{
				 	$scope.pattern="/^[a-zA-Z][ABCDEF]+$/";
				 }
				  break;
				
				}
		}
	}
	$scope.ok = function() {
		if(typeof $scope.testInfo.questionId=='number'){
			$scope.testInfo.questionIds=parseInt($scope.testInfo.questionId);
		}
		var param={
			id:$scope.testInfo.id,
			testId:$scope.testInfo.testId,
			questionId:$scope.testInfo.questionIds,
			question:$scope.testInfo.question,
			questionType:$scope.testInfo.questionType,
			trueAnswer:$scope.testInfo.trueAnswer,
			range:$scope.testInfo.range
		}
		console.log("参数"+JSON.stringify(param))
		$scope.result= JSON.parse(execute_testPaper("update_question",JSON.stringify(param)));		
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			$modalInstance.close('success');	
		}else{
			toastr.error($scope.result.message);
		}
	}
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	}
})
//确认弹出框
app.controller('sureModalCtrl',function($scope,$modalInstance,toastr,content){
	$scope.content='是否进行'+angular.copy(content)+'操作？';
	$scope.ok = function() {
		$modalInstance.close('success');
	}
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	}
})
//导入试卷控制器
app.controller('uploadfileModalCtrl', function($scope,$modalInstance,toastr) {
	$scope.fileType='0';//0:本地导入;1:服务获取
	$scope.fileType1=angular.copy($scope.fileType);
	//切换文件类型
	$scope.changefileType=function(fileType){
		$scope.fileType=fileType;
	}
	$scope.filepath='';	
	$scope.fileChanged=function(){
		if(document.querySelector('#uploadFile').value){
			$scope.filepath= document.querySelector('#uploadFile').value;			
		}
	}
	$scope.ok = function() {
		if($scope.fileType=='0'){
			if($scope.filepath){
				var extStart = $scope.filepath.lastIndexOf(".");
				var ext = $scope.filepath.substring(extStart, $scope.filepath.length).toUpperCase();
				if(ext != ".XLS" && ext != ".XLSX") {
					toastr.warning("只能导入.XLSX、.XLS类型文件");
					return ;
				}else{
					//console.log("参数"+JSON.stringify($scope.filepath))
					$scope.result=JSON.parse(execute_testPaper("import_paper",$scope.filepath));
					if($scope.result.ret=='success'){
						toastr.success($scope.result.message);
						 window.location.href="../../page/setmodule/testPaperManage.html"; 
						
						$modalInstance.close('success');
					}else{
						console.log($scope.result.detail);
						toastr.error($scope.result.message);
					}
					
					//$('#myModal').modal('show');
				}
			}else{
				toastr.warning("请选择文件");
			}
		}else{
			$scope.result=JSON.parse(execute_testPaper("import_server",$scope.filepath));
			if($scope.result.ret=='success'){
				toastr.success($scope.result.message);
				// window.location.href="../../page/setmodule/testPaperManage.html"; 
				$modalInstance.close('success');
			}else{
				toastr.error($scope.result.message);
			}
		}
		
		
	}
	$scope.cancel=function(){
		$modalInstance.dismiss('cancel');
	}

})
app.directive('select', function() {
	return {
		restrict: 'A',
		require: 'ngModel',
		scope:{
			defalutvalue:'=?'
		},
		link: function(scope, element, attrs, ngModelCtr) {
		scope.$watch('defalutvalue',function(){
			if(scope.defalutvalue){
				$(element).multiselect({
				multiple: false,
				selectedHtmlValue: '请选择',
				defalutvalue:scope.defalutvalue,
				change: function() {
					$(element).val($(this).val());
					scope.$apply();
					if(ngModelCtr) {
						ngModelCtr.$setViewValue($(element).val());
						if(!scope.$root.$$phase) {
							scope.$apply();
						}
					}
				}
			});
			}
		})
			
			
			
		}
	}
})

app.directive('select1', function() {
	return {
		restrict: 'A',
		require: 'ngModel',
		scope:{
			defalutvalue:'=?',
			list:'=?'
		},
		link: function(scope, element, attrs, ngModelCtr) {
		scope.$watch('defalutvalue+list',function(){
			if(scope.defalutvalue){
				if(scope.list){
					var str='';
					for(var i=0;i<scope.list.length;i++){
						str+='<option value="'+scope.list[i]+'">'+scope.list[i]+'</option>';
					}
					$(element).html(str);
				}
				
				$(element).multiselect({
				multiple: false,
				selectedHtmlValue: '请选择',
				defalutvalue:scope.defalutvalue,
				change: function() {
					$(element).val($(this).val());
					scope.$apply();
					if(ngModelCtr) {
						ngModelCtr.$setViewValue($(element).val());
						if(!scope.$root.$$phase) {
							scope.$apply();
						}
					}
				}
			});
			}
		})
			
			
			
		}
	}
})
app.filter('questionType', function() {
	return function(questionType) {
		var statename = '';
		switch(questionType) {
			case '3':
				{
					statename = '判断';
					break;
				}
			case '1':
				{
					statename = '单选';
					break;
				}
			case '2':
				{
					statename = '多选';
					break;
				}
			case '4':
				{
					statename = '数字';
					break;
				}
		}
		return statename;
	}
});