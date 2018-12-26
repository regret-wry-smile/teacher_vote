//定义模块时引入依赖  
var app = angular.module('app', ['ui.bootstrap', 'toastr']);
app.controller('LoginCtrl', function($scope, toastr) {
	$scope.login = {
		name: "Admin",
		password: ""
	}
	$scope.Login = function() {
		var param = {
			loginId: $scope.login.name,
			password: $scope.login.password
		}
		$scope.result = JSON.parse(execute_set("login", JSON.stringify(param)));
		console.log(JSON.stringify($scope.result))
		if($scope.result.ret == 'success') {
			window.location.href = "../../page/setmodule/setmodule.html"
		} else {
			toastr.error($scope.result.message);
		}
	}
	$scope.enterEvent = function(e) {
		var keycode = window.event ? e.keyCode : e.which;
		if(keycode == 13) {
			$scope.Login();
		}
	}
})
app.controller('setmoduleCtrl', function($scope, toastr) {
		//$scope.infoAllNameList = ["第一组", "第二组", "第三组", "第四组", "第五组", "第六组", "第七组", "第八组", "第九组", "第十组"]; //信道设置数组
		$scope.attendstatus = '1';
		$scope.sendpower = '1';
		$scope.sendpower1 = angular.copy($scope.sendpower);
		//$scope.chain = $scope.infoAllNameList[0];
		$scope.chain="1";
		$scope.chain1 = angular.copy($scope.chain);
		//同步数据库    该功能移动学生管理页面去了 setStuManageController.js
		//	var _equipmentsynchron=function(){
		//		$scope.result=JSON.parse(execute_set("equipment_database_synchronization"));
		//		if($scope.result.ret=='success'){
		//			
		//		}else{
		//			toastr.error($scope.result.message);
		//		}
		//	}
		//	_equipmentsynchron();
		/*获取所有组名*/
		var _getAllName = function() {
			/**
			 * 调用java方法
			 * 参数1,执行指令
			 * 参数2,执行参数,没有参数就不传
			 */
			//$scope.result = JSON.parse(execute_set("get_all_name"));
			/*
			 * result
			 * 		ret 执行结果
			 * 		message 业务提示信息
			 * 		detail错误详细信息（调试用）
			 * 		item 返回信息
			 * */
			/*if($scope.result.ret == 'success') {
				$scope.infoAllNameList = $scope.result.item;
				console.log(JSON.stringify($scope.infoAllNameList))
				if($scope.infoAllNameList.length > 0) {
					$scope.chain = $scope.infoAllNameList[0];
					$scope.chain1=angular.copy($scope.chain);
				}
			} else {
				toastr.error($scope.result.message);
			}*/

		};
		$scope.changeChain = function(chain) {
			$scope.chain = chain;
			$scope.chain1 = chain;
			console.log(JSON.stringify($scope.chain))
		};
		$scope.changeSendPower = function(sendpower) {
			$scope.sendpower = sendpower;
			$scope.sendpower1 = sendpower;
			console.log(JSON.stringify($scope.sendpower))
		};
		/*读取设置*/
		$scope.readSet = function() {
			$scope.result = JSON.parse(execute_set("read_setting"));
			if($scope.result.ret == 'success') {
				console.log(JSON.stringify($scope.result))
				$scope.chain = $scope.result.item.name;
				$scope.sendpower = $scope.result.item.power;
				/*		console.log("1"+JSON.stringify($scope.chain))
						console.log("2"+JSON.stringify($scope.sendpower))*/

				$scope.chain1 = angular.copy($scope.chain);
				$scope.sendpower1 = angular.copy($scope.sendpower);
				/*			console.log("3"+JSON.stringify($scope.chain1))
							console.log("4"+JSON.stringify($scope.sendpower1))*/
				toastr.success($scope.result.message);
			} else {
				toastr.error($scope.result.message);
			}
		};

		/*设置*/
		$scope.set = function() {
			var param = {
				name: $scope.chain,
				power: $scope.sendpower
			}
			console.log("参数" + JSON.stringify(param))
			$scope.result = JSON.parse(execute_set("set", JSON.stringify(param)));
			if($scope.result.ret == 'success') {
				toastr.success($scope.result.message);
			} else {
				toastr.error($scope.result.message);
			}
		};

		/*设置默认值*/
		$scope.setDefault = function() {
			$scope.result = JSON.parse(execute_set("set_default"));
			if($scope.result.ret == 'success') {
				toastr.success($scope.result.message);
			} else {
				toastr.error($scope.result.message);
			}
		};
		var _init = function() {
			_getAllName();
		}();
		//跳转到学生管理页面
		$scope.stuManage = function() {
				window.location.href = "../../page/setmodule/setStuManage.html?backurl=" + window.location.href;
			}
			//跳转到试卷管理页面
		$scope.paperManage = function() {
			window.location.href = "../../page/setmodule/testPaperManage.html?backurl=" + window.location.href;
		}

	})
	/*app.directive('select', function() {
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
	})*/
app.directive('select', function() {
	return {
		restrict: 'A',
		require: 'ngModel',
		scope: {
			defalutvalue: '=?'
		},
		link: function(scope, element, attrs, ngModelCtr) {
			scope.$watch('defalutvalue', function() {
				/*	if(scope.defalutvalue) {*/
				$(element).find('option').attr('selected', '');
				var str = '';
				for(var i = 0; i < $(element).find('option').length; i++) {
					str += '<option value=' + $(element).find('option').eq(i).val() + '>' + $(element).find('option').eq(i).html() + '</option>'
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
				/*}*/
			})

		}
	}
})
app.directive('select1', function() {
	return {
		restrict: 'A',
		require: 'ngModel',
		scope: {
			defalutvalue: '=?',
			list: '=?'
		},
		link: function(scope, element, attrs, ngModelCtr) {
			scope.$watch('defalutvalue+list', function(newvalue, oldvalue) {
				console.log("默认值" + JSON.stringify(scope.defalutvalue))
				if(scope.defalutvalue) {
					if(scope.list) {
						var str = '';
						for(var i = 0; i < scope.list.length; i++) {
							str += '<option value="' + scope.list[i] + '">' + scope.list[i] + '</option>';
						}
						$(element).html(str);
					}
				}
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
			}, true)

		}
	}
})