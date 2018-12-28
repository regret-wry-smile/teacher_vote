//定义模块时引入依赖  
var app = angular.module('app', ['ui.bootstrap', 'toastr']);
//作答记录
app.controller('answerRecordCtrl', function($scope, toastr, $modal) {

		$scope.setClass = {
			classes: '', //班级id
			subject: '', //科目名称
			sujectHour: '', //课程id
			answerStart: '', //试卷id
			sujectHour1: '',
			answerEnd: ''
		}
		$scope.classList = []; //班级数组
		$scope.subjectlists = []; //科目数组
		$scope.classhourList = [] //课程数组
		$scope.paperList = []; //试卷数组
		$scope.recordList = [] //作答记录数组
		$scope.onechecked = [];
		$scope.checkedId = [];
		$scope.checkedstudentIds = []; //学生id数组
		//答题类型数组
		$scope.answerTypeList = [{
			key: '0',
			value: 'all'
		},{
			key: '1',
			value: 'letter'
		}, {
			key: '2',
			value: 'digit'
		}, {
			key: '3',
			value: 'judge'
		}, {
			key: '4',
			value: 'multiple choice'
		}, {
			key: '5',
			value: 'vote'
		}, {
			key: '6',
			value: 'survey'
		}];
		$scope.setClass.sujectHour = "0";
		$scope.setClass.sujectHour1 = angular.copy($scope.setClass.sujectHour);
		$scope.setClass.subject='';
		$scope.setClass.subject1=angular.copy($scope.setClass.subject);
		$('#myModal').modal('hide');
		//隐藏loading
		var _hideModal = function() {
				$('#myModal').modal('hide');
			}
			//显示loading
		var _showModal = function() {
			$('#myModal').modal('show');
		}

		/*查询班级列表*/
		var _selectClass = function() {
			$scope.result = JSON.parse(execute_student("select_class"));
			if($scope.result.ret == 'success') {
				$scope.classList = [];
				if($scope.result.item.length > 0) {
					angular.forEach($scope.result.item, function(i) {
						var item = {
							key: i.classId,
							value: i.className
						}
						$scope.classList.push(item);
						
						$scope.setClass.classes = $scope.classList[0].key;
						$scope.classesobject = $scope.classList[0];
						$scope.setClass.classes1 = angular.copy($scope.setClass.classes);
						_getsubject($scope.setClass.classes);

					})
				}
			} else {
				toastr.error($scope.result.message);
			}
		};

		//查询科目
		var _getsubject = function(classeId) {			
			var params={
				classId:classeId
			}
			var result = JSON.parse(execute_record("get_subject",JSON.stringify(params)));
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
			_selectRecord();
			
		}
			
		}

		//查询记录
		var _selectRecord = function() {
				var params = {
					classId: $scope.setClass.classes,
					subject: $scope.setClass.subject,
					answerStart: $scope.setClass.answerStart,
					answerEnd: $scope.setClass.answerEnd,
					questionType: $scope.setClass.sujectHour
				}
				console.log("参数" + JSON.stringify(params))
				var result = JSON.parse(execute_record("select_record2", JSON.stringify(params)));
				console.log("记录" + JSON.stringify(result))
				if(result.ret == 'success') {
					$scope.recordList = result.item;
				} 

		}
			//切换班级

		$scope.changeClass = function(classes) {
				$scope.setClass.classes = classes;
				$scope.setClass.classes1=angular.copy(classes)
				_getsubject($scope.setClass.classes)
				
				

			}
			//切换科目
		$scope.changeSubject = function(subject) {
			$scope.setClass.subject = subject;
			$scope.setClass.subject1=angular.copy(subject);
			_selectRecord();
		}

		//切换课程
		$scope.changeClassHour = function(sujectHour) {
			
			$scope.setClass.sujectHour = sujectHour;
			$scope.setClass.sujectHour1=angular.copy(sujectHour);
			_selectRecord();

		};
		$scope.changeTime = function() {
			_selectRecord();
		}
		var _init = function() {
			_selectClass();
			
			_selectRecord();
		}();
		//全选
		$scope.selectAll = function(data) {
			if($scope.selected) {
				$scope.onechecked = [];
				angular.forEach($scope.recordList, function(i) {
					i.checked = true;
					var item = i;
					$scope.checkedId.push(i.id.toString());
					$scope.onechecked.push(item);

				})
			} else {
				angular.forEach($scope.recordList, function(i) {
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
			angular.forEach($scope.recordList, function(i) {
				//console.log("是什么"+JSON.stringify(i))
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

			if($scope.recordList.length === $scope.onechecked.length) {
				$scope.selected = true;
			} else {
				$scope.selected = false;
			}
		}

		//删除记录
		$scope.deleteRcord = function() {
			if($scope.onechecked.length > 0) {
				var content = "删除选中记录";
				var modalInstance = $modal.open({
					templateUrl: 'sureModal.html',
					controller: 'sureModalCtrl',
					size: 'sm',
					resolve: {
						content: function() {
							return content;
						}
					}
				});

				modalInstance.result.then(function(info) {
					for(var i = 0; i < $scope.onechecked.length; i++) {
						$scope.checkedstudentIds.push($scope.onechecked[i].studentId);
					}
					var param = {
						testId: $scope.setClass.paper,
						studentIds: $scope.checkedstudentIds
					}
					console.log(JSON.stringify(param))
					$scope.result = JSON.parse(execute_record("delete_record", JSON.stringify(param)));
					if($scope.result.ret == 'success') {
						toastr.success($scope.result.message);
						_selectRecord();
						$scope.onechecked = [];
						$scope.checkedId = [];
						$scope.selected = false;
					} else {
						toastr.error($scope.result.message);
					}

				}, function() {

					//$log.info('Modal dismissed at: ' + new Date());
				});
			} else {
				toastr.warning("请选择记录");
			}

		}

		//查看单个学生的记录
		$scope.viewOneInfo = function(item) {
				var modalInstance = $modal.open({
					templateUrl: 'oneAnswerDetailModal.html',
					controller: 'oneAnswerDetailModalCtrl',
					size: 'md',
					resolve: {
						infos: function() {
							return item;
						}
					}
				});
				modalInstance.result.then(function(info) {

				}, function() {

					//$log.info('Modal dismissed at: ' + new Date());
				});

			}
			//导出
		$scope.exportRecord = function() {
				if($scope.setClass.classes && $scope.setClass.paper && $scope.setClass.sujectHour && $scope.setClass.classes) {
					_showModal();
					var param = {
						classId: $scope.setClass.classes,
						subject: $scope.setClass.subject,
						classHourId: $scope.setClass.sujectHour,
						testId: $scope.setClass.paper
					}
					console.log("导出参数" + JSON.stringify(param))
					$scope.result = JSON.parse(execute_record('test_export', JSON.stringify(param)));
					if($scope.result.ret == 'success') {
						//toastr.success($scope.result.message);
					} else {
						/*toastr.error($scope.result.message);
						console.log(JSON.stringify($scope.result.message))*/
					}
				} else {
					toastr.warning("缺少必要条件，不能导出");
				}

			}
			//显示loading
		$scope.showLoading = function() {
				_showModal();
			}
			//显示loading
		$scope.removeLoading = function() {
				_hideModal();
			}
			//提示框
		$scope.getTip = function() {
			if(ret == 'true') {
				toastr.success(message);
			} else {
				toastr.error(message);
			}
		}
		$scope.refreSelectRecord = function() {
			var retDate = JSON.parse(result);
			if(result.ret == 'error') {
				toastr.error("查询失败！");
			} else {
				$scope.recordList = [];
				$scope.recordList = retDate.item;
			}
		}
	})
	//确认弹出框
app.controller('sureModalCtrl', function($scope, $modalInstance, toastr, content) {
	$scope.content = '是否进行' + angular.copy(content) + '操作？';
	$scope.ok = function() {
		$modalInstance.close('success');
	}
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	}
})

//个人详情控制器
app.controller("oneAnswerDetailModalCtrl", function($scope, $modalInstance, toastr, infos) {
		if(infos) {
			$scope.onrecordInfo = angular.copy(infos);
		}
		$scope.oneRecordList = []; //个人作答记录数组
		var param = {
			classId: $scope.onrecordInfo.classId,
			subject: $scope.onrecordInfo.subject,
			testId: $scope.onrecordInfo.testId,
			classHourId: $scope.onrecordInfo.classHourId,
			studentId: $scope.onrecordInfo.studentId
		}
		console.log(JSON.stringify(param))
		$scope.result = JSON.parse(execute_record("select_student_record_detail", JSON.stringify(param)));
		console.log(JSON.stringify($scope.result))
		if($scope.result.ret == 'success') {
			$scope.oneRecordList = $scope.result.item;
		} else {
			toastr.success($scope.result.message);
		}

		$scope.cancel = function() {
			$modalInstance.dismiss('cancel');
		}

	})
	/*app.directive('select', function() {
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
				if(scope.defalutvalue) {
					$(element).multiselect({
						width: "10rem",
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
				}
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
		scope: {
			defalutvalue: '=?',
			list: '=?'
		},
		link: function(scope, element, attrs, ngModelCtr) {
			scope.$watch('defalutvalue+list', function() {
				if(scope.defalutvalue) {
					if(scope.list) {
						var str = '';
						for(var i = 0; i < scope.list.length; i++) {
							str += '<option value="' + scope.list[i].key + '">' + scope.list[i].value + '</option>';
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
			});

		}
	}
})
app.filter('questionType', function() {
	return function(questionType) {
		var statename = '';
		switch(questionType) {
			case '2':
				{
					statename = '判断';
					break;
				}
			case '0':
				{
					statename = '单选';
					break;
				}
			case '1':
				{
					statename = '多选';
					break;
				}
			case '3':
				{
					statename = '数字';
					break;
				}
			case '4':
				{
					statename = '主观题';
					break;
				}
		}
		return statename;
	}
});
app.filter('AnswerType', function() {
	return function(AnswerType) {
		var statename = '';
		switch(AnswerType) {
			case 'T':
				{
					statename = '√';
					break;
				}
			case 'F':
				{
					statename = '×';
					break;
				}
			default:
				{
					statename = AnswerType;
					break;
				}
		}
		return statename;
	}
});

app.directive('datetimepicker', function($parse) {
	return {
		restrict: 'AE',
		scope: {
			startDate: '=?',
			endDate: '=?',
			upDate: '=?'
		},
		require: "?ngModel",
		link: function(scope, elem, attrs, ngModelCtr) {
			var $me = angular.element(elem);
			var linkOptions = {};
			if(attrs.uiOptions) {
				linkOptions = scope.$eval(attrs.uiOptions);
			}

			var mydata = '';
			var opt = {
				format: 'yyyy-mm-dd',
				autoclose: true,
				forceParse: false,
				minView: 2,
				startView: 2,
				language: 'zh-tw',
				todayBtn: 'linked'

			};
			var opt2 = {};
			if(attrs.timetype == 'time') {
				opt2 = {
					format: 'hh:ii',
					startView: 1,
					minView: 0,
					todayBtn: false
				}
			} else if(attrs.timetype == 'seconds') {
				opt2 = {
					format: 'hh:ii',
					startView: 1,
					minView: 0,
					showSecond: 1,
					minuteStep: 1,
					todayBtn: false
				}
			} else if(attrs.timetype == 'month') {
				opt2 = {
					format: 'yyyy-mm',
					startView: 3, //这里就设置了默认视图为年视图
					minView: 3, //设置最小视图为年视图
					todayBtn: 'linked',
					language: 'zh-cn',
				}
			} else if(attrs.timetype == 'year') {
				opt2 = {
					format: 'yyyy-mm-dd hh:ii',
					startView: 2,
					minView: 0,
					minuteStep: 5,

				}
			}
			if(scope.$eval(attrs.startDate)) {
				opt2.startDate = scope.$eval(attrs.startDate);
			}
			if(scope.$eval(attrs.endDate)) {
				opt2.endDate = scope.$eval(attrs.endDate)
			}
			var option = angular.extend({}, opt, opt2);
			$(elem).datetimepicker(option).on('changeDate', function() {
				scope.$apply(function() {
					if(attrs['ngModel']) {
						//elem[0].value = elem[0].value + ':00';
						ngModelCtr.$setViewValue(elem[0].value);
						if(!scope.$root.$$phase) {
							scope.$apply();
						}
					}
				})
			});
			if(attrs.ngModel) {
				$(elem).bind('change', function() {
					$(elem).trigger('input');

				});
			}
			/*监听最大时间和最小时间变化*/
			var watch = scope.$watch('endDate', function(newvalue, oldvalue) {
				if(scope.endDate) {
					uiLoad.load(JQ_CONFIG['datetimepicker']).then(function() {
						if($me.data('datetimepicker')) {
							$me.datetimepicker('setEndDate', scope.endDate)
						}
					})
				}
			});
			/*监听最大时间和最小时间变化*/
			var watch2 = scope.$watch('startDate', function(newvalue, oldvalue) {
				if(newvalue != oldvalue) {
					uiLoad.load(JQ_CONFIG['datetimepicker']).then(function() {
						if($me.data('datetimepicker')) {
							$me.datetimepicker('setStartDate', scope.startDate)
						}
					})
				}
			});
			/*监听默认值变化，更新*/
			var watch3 = scope.$watch('upDate', function(newvalue, oldvalue) {
				/*if(newvalue != oldvalue) {*/
				if($me.data('datetimepicker')) {
					$me.datetimepicker('update');
				}
				/*}*/
			}, true);
			scope.$on('$destroy', function() {
				watch();
				watch2();
				watch3();
				if($me.data('datetimepicker')) {
					$me.data('datetimepicker').remove();
				} else {
					if(mydata) {
						mydata.remove();
					}
				}

			});

		}
	}
})