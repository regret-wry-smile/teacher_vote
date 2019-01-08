//定义模块时引入依赖  
var app = angular.module('app', ['ui.bootstrap', 'toastr']);
app.controller('setClassCtrl', function($rootScope,$scope, toastr,$modal,$window) {
	$scope.setClass={
		classes:'',
		subject:'',
		sujectName:''
	}
	$scope.curclassName='';//当前班级
	$scope.classList=[];//班级数组
	$scope.subjectlists=[];//科目数组
	$scope.classhourList=[]//课程数组
	$rootScope.isSign=false;//是否是签到
	$rootScope.studentAttendList=[];//签到人员数组
	$rootScope.signList=0;//已签到默认0人
	/*查询班级列表*/
	var _selectClass = function() {
		$scope.result = JSON.parse(execute_student("select_class"));
		if($scope.result.ret=='success'){
			if($scope.result.item.length>0){
				angular.forEach($scope.result.item,function(i){
					var item={
						key:i.classId,
						value:i.className
					}
					$scope.classList.push(item);
					//console.log("班级"+JSON.stringify($scope.classList))
					$scope.setClass.classes=$scope.classList[0].key;
					$scope.classesobject=$scope.classList[0];
					$scope.setClass.classes1=angular.copy($scope.setClass.classes);								
	
				})			
			}
		}else{
			toastr.error($scope.result.message);
		}
	};
	//查询科目
	var _getsubject=function(){
		var params={
			classId:$scope.setClass.classes
		}
		console.log(JSON.stringify(params))
		var result= JSON.parse(execute_record("get_subject",JSON.stringify(params)));
		console.log(JSON.stringify(result))
		/*if($scope.subjectlists.length>0){
			$scope.setClass.subject=$scope.subjectlists[0];
			$scope.setClass.subject1=angular.copy($scope.setClass.subject);
		}*/
		if(result.ret=='success'){
				if(result.item&&result.item.length>0){
					$scope.subjectlists = [];
				for(var i=0;i<result.item.length;i++){				
					$scope.subjectlists.push(result.item[i].subjectName);
					$scope.setClass.subject = $scope.subjectlists[0];
					$scope.setClass.subject1 = angular.copy($scope.setClass.subject);
				}
			}	else{
				$scope.subjectlists=[];
				$scope.setClass.subject='';
				$scope.setClass.subject1='';
			}
	}
	}
	//查询课程
	/*var _selectClassHour=function(){
		$scope.result=JSON.parse(execute_record("select_class_hour",$scope.setClass.classes,$scope.setClass.subject));
		console.log("课程"+JSON.stringify($scope.result))
		if($scope.result.ret=='success'){			
			if($scope.result.item.length>0){
				angular.forEach($scope.result.item,function(i){
					var item={
						key:i.class_hour_id,
						value:i.class_hour_name
					}
					$scope.classhourList.push(item);
					$scope.setClass.sujectName=$scope.classhourList[0].key;
					$scope.sujectNameobject=$scope.classhourList[0];
					console.log(JSON.stringify($scope.sujectNameobject))
					$scope.setClass.sujectName1=angular.copy($scope.setClass.sujectName);
				})
			}
		}else{
			toastr.error($scope.result.message);
		}
	}*/
	//切换班级
	$scope.changeClass=function(classes){
		$scope.setClass.classes=classes;
		$scope.setClass.classes1=classes;
		_getsubject();
		angular.forEach($scope.classList,function(i){
			if($scope.setClass.classes==i.key){
				$scope.classesobject=i;
//				$scope.classhourList=[];
				//$scope.setClass.sujectName="";
				/*$scope.sujectNameobject="";
				_selectClassHour();*/
			}
		})
		
	}
	//切换科目
	$scope.changeSubject=function(subject){
		$scope.setClass.subject	=subject;
		$scope.classhourList=[];
		$scope.setClass.sujectName="";
		$scope.sujectNameobject="";
		//_selectClassHour();
	}
	
	//查询当前上课班级
	var _isStartClass=function(){
		$scope.result=JSON.parse(execute_record("get_classInfo"));
		//console.log("巴巴爸爸"+JSON.stringify($scope.result))
		if($scope.result.ret=='success'&&$scope.result.item){
			$scope.curclassName=$scope.result.item.className;
		}else{
			$scope.curclassName="";
		}
	}
	//切换课程
	$scope.changeClassHour=function(sujectName){
		$scope.setClass.sujectName=sujectName;
		angular.forEach($scope.classhourList,function(i){
			if($scope.setClass.sujectName==i.key){
				$scope.sujectNameobject=i;
			}
		})
		
	}
	//查询设备连接状态
	var _selectequip=function(){
		$scope.equipresult=JSON.parse(execute_answer("check_equipment_status_start"));//设备状态
			if($scope.equipresult.ret=='success'){
				$rootScope.equipstatus=JSON.stringify($scope.equipresult.item);
				if($rootScope.equipstatus==true){
					$rootScope.equipstatus='1';
				}else{
					$rootScope.equipstatus='0';
				}
			}
			console.log("设备状态"+JSON.stringify($rootScope.equipstatus));
	}
	//跳转到答题中心页面
	$scope.startClass=function(){
		console.log(JSON.stringify($scope.classesobject))
       var param={
       	classId:$scope.setClass.classes,
       	className:$scope.classesobject.value,
       	subjectName:$scope.setClass.subject,
       	/*classHourId:$scope.sujectNameobject.key,*/
//     	classHourName:$scope.sujectNameobject.value
		classHourName:$scope.setClass.sujectName
       }
       console.log(JSON.stringify(param))
      	$scope.result=JSON.parse(execute_record("start_class",JSON.stringify(param))); 
        	//console.log(JSON.stringify($scope.result))
	    if($scope.result.ret=='success'){	    	
			//_selectequip();
	      	$scope.objectUrl = '../../page/answermoudle/answerCenter.html';
	      	$window.location.href = $scope.objectUrl;	
	      	
	    }else{
	    	toastr.error($scope.result.message);
	    }
		
	}
	
	/*$scope.refreEquipmentState=function(){
		_selectequip();
	}*/
	//打开添加课时弹框
	$scope.addClassHour=function(){
		var item={
			classId:$scope.setClass.classes,
			className:$scope.classesobject.value,
			/*subjectName:$scope.setClass.subject*/
		}
		var modalInstance = $modal.open({
			templateUrl: 'addClassHourModal.html',
			controller: 'addClassHourCtrl',
			size: 'md',
			backdrop:false,
			resolve: {
				infos: function() {
					return item;
				}
			}
		});
		modalInstance.result.then(function(info) {
			/*$scope.classhourList=[];
			$scope.setClass.sujectName="";
			$scope.sujectNameobject="";*/
			$scope.subjectlists=[];
			$scope.setClass.subject='';
			$scope.setClass.subject1='';
			//_selectClassHour();
			_getsubject();
			/*$scope.setClass.subject = $scope.subjectlists[0];
			$scope.setClass.subject1 = angular.copy($scope.setClass.subject);
			$scope.setClass.sujectName=$scope.classhourList[$scope.classhourList.length-1].key;
			$scope.sujectNameobject=$scope.classhourList[$scope.classhourList.length-1];
			$scope.setClass.sujectName1=angular.copy($scope.setClass.sujectName);*/
			//$scope.startClass();
		}, function() {
			//$log.info('Modal dismissed at: ' + new Date());
		});
		
	}
	//删除课时
	$scope.delClassHour=function(){
		if($scope.setClass.subject){
			var content="delete scenario";
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
//				var param={
//					classHourId:$scope.setClass.subject,
//				}	
				var params={
					classId:$scope.setClass.classes,
					className:$scope.classesobject.value,
					subjectName:$scope.setClass.subject
					
				}
				console.log("删除场景参数"+JSON.stringify(params))
				var result=JSON.parse(execute_record("delete_class_hour",JSON.stringify(params)));
				if(result.ret=='success'){
					toastr.success(result.message);
					$scope.subjectlists=[];
					$scope.setClass.subject='';
					$scope.setClass.subject1='';
					_getsubject();
				}else{
					toastr.error(result.message);
				}
			}, function() {
				//$log.info('Modal dismissed at: ' + new Date());
			});
		}else{
			toastr.success("There are currently no deletable scenarios")
		}
	}
	var _init=function(){
		_selectClass();
		_getsubject();
		/*_selectClassHour();*/
		_isStartClass();
	}();
	
	//签到	
	$scope.signIn=function(){
		var param={
			classId:$scope.classesobject.key
		}
		$scope.result=JSON.parse(execute_attendance("sign_in_start",JSON.stringify(param)));
		
		if($scope.result.ret=='success'){
			$rootScope.isSign=true;
//			toastr.success($scope.result.message);
			$rootScope.studentAttendList=JSON.parse(execute_attendance("get_sign_in"));
			$rootScope.signList=JSON.parse(execute_attendance("get_submit_num"));				
			//console.log("人员列表"+JSON.stringify($rootScope.studentAttendList));
			/*$scope.param = "classId=" + $scope.setClass.classesobject.key + "&className=" + $scope.setClass.classesobject.value + "&classhourid=" + $scope.sujectNameobject.key+"&classhourname=" +$scope.sujectNameobject.value+ "&suject="+$scope.setClass.subject;			
			$scope.objectUrl = '../../page/answermoudle/userAttend.html' + '?' + $scope.param;
			$window.location.href =$scope.objectUrl;*/
		}else{
			$rootScope.isSign=false;
			toastr.error($scope.result.message);	
		}
	}
	//抢答
	$scope.quickAnswer=function(){
		var param={
			classId:$scope.classesobject.key
		}
		$scope.result=JSON.parse(execute_preemptive("quick_answer",JSON.stringify(param)));
		//console.log("抢答"+JSON.stringify($scope.result))
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			$scope.objectUrl = '../../page/answermoudle/stopAnswer.html';
			$window.location.href =$scope.objectUrl;
		}else{			
			toastr.error($scope.result.message);			
		}
	}
/*	//评分
	$scope.skipMark=function(){
		var param={
			classId:$scope.classesobject.key
		}
		$scope.result=JSON.parse(execute_preemptive("quick_answer",JSON.stringify(param)));
		console.log("抢答"+JSON.stringify($scope.result))
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			$scope.objectUrl = '../../page/answermoudle/stopAnswer.html';
			$window.location.href =$scope.objectUrl;
		}else{			
			toastr.error($scope.result.message);			
		}
	}*/
	/*跳转到投票*/
	$scope.skipVote=function(){		
		var result=JSON.parse(execute_vote("to_vote"));
		console.log(JSON.stringify(result))
		if(result.ret!='success'){
			toastr.warning(result.message);	
		}
		
	}
	/*跳转到答题页面*/
	$scope.Question=function(){
		$rootScope.signList=JSON.parse(execute_attendance("get_submit_num"));	
		if($rootScope.signList){			
			$scope.answerobjectUrl = '../../page/answermoudle/answermoudle.html';
			$window.location.href =$scope.answerobjectUrl;
		}else{
			toastr.warning("Please sign in first.");
		}
	}
	/*跳转到调查页面*/
	$scope.Survey=function(){
		$rootScope.signList=JSON.parse(execute_attendance("get_submit_num"));	
		if($rootScope.signList){			
			$scope.surveyobjectUrl = '../../page/answermoudle/setMarkmoudle.html';
			$window.location.href =$scope.surveyobjectUrl;
		}else{
			toastr.warning("Please sign in first.");
		}
	}
	//下课
	$scope.stopClass=function(){
		$scope.result=JSON.parse(execute_record("end_class"));
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			$scope.objectUrl = '../../page/answermoudle/startAnswer.html';
			$window.location.href =$scope.objectUrl;
		}else{			
			toastr.error($scope.result.message);	
		}
		
	}
})
app.controller('addClassHourCtrl', function($rootScope,$scope,$modal,$modalInstance,toastr,infos) {	
	$scope.title="AddScenario";
	$scope.classInfo={
		subjectName:''
	}
	if(infos){
		$scope.classInfo=angular.copy(infos);
		console.log(JSON.stringify(infos))
	}
	$scope.ok = function() {
		var param=$scope.classInfo;
		console.log("参数"+JSON.stringify(param))
		$scope.result=JSON.parse(execute_record("insert_class_hour",JSON.stringify(param)));
		console.log("返回结果"+JSON.stringify($scope.result))
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			$modalInstance.close(param);
		}else{
			toastr.error($scope.result.message);	
		}
		
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
//设置签到控制器
app.controller('userAttendCtrl', function($rootScope,$scope,$modal,toastr) {
	//$rootScope.studentAttendList=[];//签到学生数组
	$rootScope.signList=0;
	var _getsignStudent=function(){
		$rootScope.studentAttendList=JSON.parse(execute_attendance("get_sign_in"));
		//console.log("哈哈哈"+JSON.stringify($scope.studentAttendList))
	}
	/*var _init=function(){
		_getsignStudent();
	}();
	*/
	var _getSubmitNum=function(){
		$rootScope.signList=JSON.parse(execute_attendance("get_submit_num"));
		console.log("已签到"+JSON.stringify($scope.signList));
	}
	 $scope.returnPage=function(){
	 	$scope.result=JSON.parse(execute_attendance("sign_in_stop"));  
	 	console.log("停止答题"+JSON.stringify($scope.result));
	 	if($scope.result.ret=='success'){
			$rootScope.isSign=false;
	 	}else{
	 		$rootScope.isSign=true;
	 		toastr.error($scope.result.message);
	 	}
	 	
	 }
	$scope.refresAttendance=function(){
		_getsignStudent();
		_getSubmitNum();
	}
})
app.controller('stopAnswerCtrl', function($rootScope,$scope,$modal,toastr,$interval) {
	$scope.studentName='';//抢答数据
	var myTimer;
	$scope.time=3;
	var _stopAnswer=function(){
		if($scope.studentName&&$scope.studentName!="抢答中"){
			$interval.cancel(myTimer); 
		}

	}
	$scope.isStopAswer=true;//是否是停止按钮
	//定时器
	myTimer = $interval(function(){	
		$scope.result=JSON.parse(execute_preemptive("get_quick_answer_studentName"));
		$scope.time=$scope.time-1;
		console.log(JSON.stringify($scope.result))
		if($scope.result.studentName){
			$scope.studentName=$scope.result.studentName;
			_stopAnswer();
		}else{
			console.log($scope.time)		
			if($scope.time==0){
				$scope.studentName="抢答中...";		
				$scope.result=JSON.parse(execute_preemptive("set_flag_start_quick"));	
				if($scope.result.ret=='success'){					
				}else{
					toastr.error($scope.result.message);
				}
			}			
		}
	},1000);
	
	
	$scope.stopAnswer = function(){		
		 //关闭定时器
		$interval.cancel(myTimer); 
			$scope.result=JSON.parse(execute_preemptive("stop_quick_answer"));	
			if($scope.result.ret=='success'){
				$scope.isStopAswer=false;
				$scope.studentName='停止答题';
				$scope.time=0;
				toastr.success($scope.result.message);
			}else{
				$scope.isStopAswer=true;
				toastr.error($scope.result.message);
			}
	};
	
	$scope.$on('$destroy',function(){
        $interval.cancel(myTimer);
    })
	
})
//确认弹出框
app.controller('sureModalCtrl',function($scope,$modalInstance,toastr,content){
	$scope.content='Whether to ' +angular.copy(content)+ ' operations?';
	$scope.ok = function() {
		$modalInstance.close('success');
	}
	$scope.cancel = function() {
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
				width: "10rem",
				multiple: false,
				selectedHtmlValue: 'please selected',
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
/*app.directive('select1', function() {
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
			}
			$(element).multiselect({
				multiple: false,
				selectedHtmlValue: 'please selected',
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
		})	
		}
	}
})*/
app.directive('select1', function() {
	return {
		restrict: 'A',
		require: 'ngModel',
		scope: {
			defalutvalue: '=?',
			list: '=?'
		},
		link: function(scope, element, attrs, ngModelCtr) {
			scope.$watch('defalutvalue+list', function() {
                var str = '';
                //var str ='<option value="">please selected</option>'
		         if(scope.defalutvalue) { 
		         	if(scope.list) {
						for(var i = 0; i < scope.list.length; i++) {
							str += '<option value="' + scope.list[i] + '">' + scope.list[i] + '</option>';
						} 
					}
                  }
				$(element).html(str);
				$(element).multiselect({
						multiple: false,
						selectedHtmlValue: 'please selected',
						defalutvalue: scope.defalutvalue,
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
			})
		}
	}
})
app.directive('select2', function() {
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
						str+='<option value="'+scope.list[i].key+'">'+scope.list[i].value+'</option>';
					}
					$(element).html(str);
				}
			}
			$(element).multiselect({
				multiple: false,
				selectedHtmlValue: 'please selected',
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
		})
		}
	}
})