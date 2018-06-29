//定义模块时引入依赖  
//var app = angular.module('app', ['ngCookies','ui.bootstrap', 'toastr']);
var app = angular.module('app', ['ui.bootstrap', 'toastr']);
//app.controller('setClassCtrl', function($scope, toastr,$cookies, $cookieStore,$modal,$window) {
app.controller('setClassCtrl', function($scope, toastr,$modal,$window) {
	$scope.setClass={
		classes:'',
		subject:'',
		sujectName:''
	}
	$scope.curclassName='';//当前班级
	$scope.classList=[];//班级数组
	$scope.subjectlists=[];//科目数组
	$scope.classhourList=[]//课程数组
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
		$scope.subjectlists= JSON.parse(execute_testPaper("get_subject"));
		if($scope.subjectlists.length>0){
			$scope.setClass.subject=$scope.subjectlists[0];
			$scope.setClass.subject1=angular.copy($scope.setClass.subject);
		}
	}
	//查询课程
	var _selectClassHour=function(){
		$scope.result=JSON.parse(execute_record("select_class_hour",$scope.setClass.classes,$scope.setClass.subject));
		//console.log(JSON.stringify($scope.result))
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
	}
	//切换班级
	$scope.changeClass=function(classes){
		$scope.setClass.classes=classes;
		angular.forEach($scope.classList,function(i){
			if($scope.setClass.classes==i.key){
				$scope.classesobject=i;
				$scope.classhourList=[];
				_selectClassHour();
			}
		})
		
	}
	//切换科目
	$scope.changeSubject=function(subject){
		$scope.setClass.subject	=subject;
	}
	
	//查询当前上课班级
	var _isStartClass=function(){
		$scope.result=JSON.parse(execute_record("get_classInfo"));
		console.log("巴巴爸爸"+JSON.stringify($scope.result))
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
	
	//跳转到答题中心页面
	$scope.startClass=function(){
		/*var classInfo={
			classitem:$scope.setClass.classesobject,
			houritem:$scope.setClass.sujectNameobject,
			sujectitem:$scope.setClass.subject
		}*/
		// $cookieStore.put('classInfo',classInfo);
        // Get cookie
        //var favoriteCookie = $cookieStore.get('myFavorite');
        // Removing a cookie
       // $cookieStore.remove('myFavorite');
       var param={
       	classId:$scope.setClass.classes,
       	className:$scope.classesobject.value,
       	subjectName:$scope.setClass.subject,
       	classHourId:$scope.sujectNameobject.key,
       	classHourName:$scope.sujectNameobject.value
       }
       console.log(JSON.stringify(param))
      	$scope.result=JSON.parse(execute_record("start_class",JSON.stringify(param))); 
      	//console.log(JSON.stringify($scope.result))
	    if($scope.result.ret=='success'){
	      	$scope.objectUrl = '../../page/answermoudle/answerCenter.html';
	      	$window.location.href = $scope.objectUrl;	
	    }
		//$scope.param = "classId=" + $scope.classesobject.key + "&className=" + $scope.classesobject.value + "&classhourid=" + $scope.sujectNameobject.key+"&classhourname=" +$scope.sujectNameobject.value+ "&suject="+$scope.setClass.subject;			
		//console.log(JSON.stringify($scope.param))
		//$scope.objectUrl = '../../page/answermoudle/answerCenter.html' + '?' + $scope.param;
		
		
		//window.location.href = "../../page/answermoudle/answerCenter.html?backurl=" + window.location.href;
		
	}
	//打开添加课时弹框
	$scope.addClassHour=function(){
		var item={
			classId:$scope.setClass.classes,
			className:$scope.classesobject.value,
			subjectName:$scope.setClass.subject
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
			$scope.classhourList=[];
			_selectClassHour();
			$scope.setClass.sujectName=$scope.classhourList[$scope.classhourList.length-1].key;
			$scope.sujectNameobject=$scope.classhourList[$scope.classhourList.length-1];
			$scope.setClass.sujectName1=angular.copy($scope.setClass.sujectName);
			$scope.startClass();
		}, function() {
			//$log.info('Modal dismissed at: ' + new Date());
		});
		
	}
	var _init=function(){
		_selectClass();
		_getsubject();
		_selectClassHour();
		_isStartClass();
	}();
	
	//签到	
	$scope.signIn=function(){
		var param={
			classId:$scope.classesobject.key
		}
		$scope.result=JSON.parse(execute_attendance("sign_in_start",JSON.stringify(param)));
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			/*$scope.param = "classId=" + $scope.setClass.classesobject.key + "&className=" + $scope.setClass.classesobject.value + "&classhourid=" + $scope.sujectNameobject.key+"&classhourname=" +$scope.sujectNameobject.value+ "&suject="+$scope.setClass.subject;			
			$scope.objectUrl = '../../page/answermoudle/userAttend.html' + '?' + $scope.param;
			$window.location.href =$scope.objectUrl;*/
		}else{
			toastr.error($scope.result.message);	
		}
	}
	//抢答
	$scope.quickAnswer=function(){
		var param={
			classId:$scope.classesobject.key
		}
		$scope.result=JSON.parse(execute_preemptive("quick_answer",JSON.stringify(param)));
		console.log(JSON.stringify($scope.result))
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			$scope.objectUrl = '../../page/answermoudle/stopAnswer.html';
			$window.location.href =$scope.objectUrl;
		}else{			
			toastr.error($scope.result.message);	
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
	$scope.title="添加课程";
	$scope.classInfo={
		classHourName:''
	}
	if(infos){
		$scope.classInfo=angular.copy(infos);
		console.log(JSON.stringify(infos))
	}
	$scope.ok = function() {
		var param=$scope.classInfo;
		$scope.result=JSON.parse(execute_record("insert_class_hour",JSON.stringify(param)));
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
/*app.controller('setSignCtrl', function($rootScope,$scope,$modal,toastr,$location,$window) {
	if($location.search()){
		$scope.classInfo=$location.search();
		console.log(JSON.stringify($location.search()))
	}
	//签到
	$scope.signIn=function(){
		var param={
			classId:$scope.classInfo.classId
		}
		$scope.result=JSON.parse(execute_attendance("sign_in_start",JSON.stringify(param)));
		if($scope.result.ret=='success'){
			toastr.success($scope.result.message);
			console.log(JSON.stringify($scope.result))
			$scope.param = "classId=" + $scope.setClass.classesobject.key + "&className=" + $scope.setClass.classesobject.value + "&classhourid=" + $scope.sujectNameobject.key+"&classhourname=" +$scope.sujectNameobject.value+ "&suject="+$scope.setClass.subject;			
		console.log(JSON.stringify($scope.param))
		$scope.objectUrl = '../../page/answermoudle/userAttend.html' + '?' + $scope.param;
			$window.location.href = "../../page/answermoudle/userAttend.html";
		}else{
			toastr.error($scope.result.message);	
		}
	}
})*/
app.controller('userAttendCtrl', function($rootScope,$scope,$modal,toastr) {
	$scope.studentAttendList=[];//签到学生数组
	var _getsignStudent=function(){
		$scope.studentAttendList=JSON.parse(execute_attendance("get_sign_in"));
		console.log("哈哈哈"+JSON.stringify($scope.studentAttendList))
	}
	var _init=function(){
		_getsignStudent();
	}();
	
	 $scope.returnPage=function(){
	 	$scope.result=JSON.parse(execute_attendance("sign_in_stop"));  
	 	console.log("停止答题"+JSON.stringify($scope.result));
	 	if($scope.result.ret=='success'){
	 		history.go(-1);	 		
	 	}else{
	 		toastr.error($scope.result.message);
	 	}
	 	
	 }
	$scope.refresAttendance=function(){
		_getsignStudent();
	}
})
app.controller('stopAnswerCtrl', function($rootScope,$scope,$modal,toastr,$interval) {
	$scope.studentName='';//抢答数据
	var myTimer;
	$scope.time=3;
	var _stopAnswer=function(){
		if($scope.time<=0||($scope.studentName&&$scope.studentName!="抢答中")){
		$interval.cancel(myTimer); 
		}
	}
	//定时器
	myTimer = $interval(function(){	
		$scope.result=JSON.parse(execute_preemptive("get_quick_answer_studentName"));
		$scope.time=$scope.time-1;
		console.log(JSON.stringify($scope.result))
		if(JSON.stringify($scope.result)!="{}"){
			$scope.studentName=$scope.result.studentName;
			_stopAnswer();
		}else{
			console.log($scope.time)		
			if($scope.time<1){
				$scope.studentName="抢答中...";
			}	
		}
	},1000);
	
	
	$scope.stopAnswer = function(){		
		 //关闭定时器
		$interval.cancel(myTimer); 
	};
	
	$scope.$on('$destroy',function(){
        $interval.cancel(myTimer);
    })
	
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