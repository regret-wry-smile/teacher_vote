//定义模块时引入依赖  
var app = angular.module('app', ['ui.bootstrap', 'toastr']);

app.controller('mainAnswerCtrl', function($scope,$rootScope, toastr, $window) {
		$scope.tabpane = 's';
		$scope.selAnswerType = function(answerType) {
			$scope.tabpane = answerType;
		}
	})
	//单选
app.controller('answerCtrl', function($scope,$rootScope, toastr, $window) {
	$rootScope.userdetailshow = false; //默认答题详情页面隐藏
	$rootScope.isStopAswer = false;//默认在停止答题页面
		//切换答题类型
		$scope.selType = 'char'; //默认字母
		$scope.selAnswerType = function(selType) {
			$scope.selType = selType;
		}
		//跳转到答题中心页面
		$scope.startAnswer = function() {
			var param = {
				type: $scope.selType
			}
			//console.log("答题" + JSON.stringify(param))
			$scope.result = JSON.parse(execute_answer("single_answer", JSON.stringify(param)));
			//console.log("答题" + JSON.stringify($scope.result));
			if($scope.result.ret == 'success') {
				$scope.param = "answerType=" + $scope.selType;
				console.log(JSON.stringify($scope.param))
				$scope.objectUrl = '../../page/answermoudle/stopsingeAnswer.html' + '?' + $scope.param;
				$window.location.href = $scope.objectUrl;
			} else {
				toastr.error($scope.result.message);
				/*$scope.param = "answerType=" + $scope.selType;
				console.log(JSON.stringify($scope.param))
				$scope.objectUrl = '../../page/answermoudle/stopsingeAnswer.html' + '?' + $scope.param;
				$window.location.href = $scope.objectUrl;*/
			}
		}
	})
	//多选
app.controller('answerMutilCtrl', function($scope,$rootScope, toastr, $window) {
	$rootScope.userdetailshow = false; //默认答题详情页面隐藏
	$rootScope.isStopAswer = false;//默认在停止答题页面
	$scope.rangeList = ["C", "D", "E", "F"];
	$scope.range = "C";
	$scope.range1 = angular.copy($scope.range);
	$scope.changeRange = function(range) {
			$scope.range = range;
		}
		//跳转到答题中心页面
	$scope.startAnswer = function() {
		var param = {
			id: "1",
			type: "m", //多选
			range: "A-" + $scope.range
		}
		$scope.result = JSON.parse(execute_answer("start_multiple_answer", JSON.stringify(param)));
		if($scope.result.ret == 'success') {
			$window.location.href = "../../page/answermoudle/stopAnswerType.html";
		} else {
			toastr.error($scope.result.message);
		}
	}

})

app.config(['$locationProvider', function($locationProvider) {
	//$locationProvider.html5Mode(true);  
	$locationProvider.html5Mode({
		enabled: true,
		requireBase: false
	});
}]);

//停止多选答题
app.controller('stopAnswerTypeCtrl', function($scope,$rootScope, toastr, $location, $window) {
$('#myModal').modal('hide');
	//隐藏loading
	var _hideModal=function(){
		$('#myModal').modal('hide');
	}
	//显示loading
	var _showModal=function(){
		$('#myModal').modal('show');
	}
	$rootScope.isStopAswer = false;//默认在停止答题页面
	$rootScope.userdetailshow = false; //默认答题详情页面隐藏	
	$scope.studentNum = 0;
	var rangeList = []; //答题范围
	$scope.data = []; //柱状图数据
	$scope.resultmap = {};
	$scope.rangeList = [];
	var colors = []; //颜色数组
	var letterlist = []; //选择的字母数组
	$scope.answerResult = []; //选择的作答结果
	$scope.answerRate = 0; //正确率
	$scope.studentCount=0;//总人数
	var ajaxnum1=0;//防止页面闪烁
	//获取答题人数
	$scope.refresAnswerNum = function() {
			$scope.result = JSON.parse(execute_answer("get_multiple_answer_num"));
			if($scope.result) {
				$scope.studentCount = $scope.result.total;
				$scope.studentNum = $scope.result.answerNum;
				//console.log("获取答题" + JSON.stringify($scope.result))
			}

		}
		//停止答题
	var dom = document.getElementById("recordbar");
	var myChart = echarts.init(dom);
	$scope.stopAnswer = function() {
		ajaxnum1=1;
		_showModal();
		$scope.result = JSON.parse(execute_answer("stop_multiple_answer"));
		if(ajaxnum1==1){
			_hideModal();
		}
		if($scope.result.ret == 'success') {
			$rootScope.isStopAswer = true;
			rangeList = JSON.parse(execute_answer("get_multiple_range"));
			//console.log("答题范围" + JSON.stringify(rangeList))
			//$scope.rangeList=["A","B","C","D","E"];
			$scope.resultmap = JSON.parse(execute_answer("get_multiple_answer_detail"));
			//$scope.resultmap={"A":[{"classId":"BJ1001","className":"自动测试","iclickerId":"3429966709","id":1000662,"status":"1","studentId":"10000002","studentName":"学002"}],"E":[{"classId":"BJ1001","className":"自动测试","iclickerId":"3429469477","id":1000661,"status":"1","studentId":"10000001","studentName":"学001"}]};
			//console.log("data"+JSON.stringify($scope.resultmap))
			$scope.data = [];
			if(rangeList.length > 0) {
				for(var i = 0; i < rangeList.length; i++) {
					if($scope.resultmap != null) {
						//console.log("item" + JSON.stringify($scope.resultmap))
						if($scope.resultmap[rangeList[i]]) {
							var item = {
								value: $scope.resultmap[rangeList[i]].length,
								itemStyle: {
									normal: {
										color: '#fff'
									}
								}
							}
						} else {
							var item = {
								value: 0,
								itemStyle: {
									normal: {
										color: '#fff'
									}
								}
							}
						}
					} else {
						var item = {
							value: 0,
							itemStyle: {
								normal: {
									color: '#fff'
								}
							}
						}
					}
					$scope.data.push(item);
					var coloritem = "#fff";
					colors.push(coloritem);
				}
			} else {
				var item = {
					value: 0,
					itemStyle: {
						normal: {
							color: '#fff'
						}
					}
				}
				$scope.data.push(item);
			}
			//var colors = ['#5793f3', '#d14a61', '#675bba'];
			option = {
				color: ['#fff'],
				tooltip: {
					trigger: 'axis',
					axisPointer: { // 坐标轴指示器，坐标轴触发有效
						type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
					}
				},
				grid: {
					left: '3%',
					right: '4%',
					bottom: '3%',
					containLabel: true
				},
				xAxis: {
					type: 'category',
					//data : ['A', 'B', 'C','D','E','F'],
					data: rangeList,
					axisTick: {
						alignWithLabel: true
					},
					axisLine: {
						lineStyle: {
							color: '#dcdcdc', //这里是为了突出显示加上的，可以去掉
							show: false
						}
					},
					axisLabel: {
						show: true,
						textStyle: {
							color: '#fff',
							fontSize: '32'
						},
					},
				},
				yAxis: [{
					show: false,
					type: 'value',
					splitLine: false, //是否显示网格线
					axisLine: {
						lineStyle: {
							color: '#dcdcdc', //这里是为了突出显示加上的，可以去掉
							show: false
						}
					},
					axisLabel: {
						show: true,
						textStyle: {
							color: '#fff',
							fontSize: '16'
						}
					},
				}],
				series: [{
					name: '记录统计',
					type: 'bar',
					barWidth: '50%',
					//data:[7, 13, 5,14,2,0],
					data: $scope.data,
					label: {
						normal: {
							show: true,
							position: 'top',
							textStyle: {
								color: '#fff',
								fontSize: '32'
							}
						}
					},
					itemStyle: {
						normal: {　　　　　　　　　　　　
							color: function(params) {
								var colorList = colors;
								return colorList[params.dataIndex];
							}
						}
					},
				}]

			};
			var str;
			$scope.selresultmap = JSON.parse(execute_answer("get_answer_info_sum"));
			//console.log(JSON.stringify($scope.selresultmap));
			//查询正确答案人数
			var _getanswerinfo = function(str) {
					if($scope.selresultmap) {
						if($scope.selresultmap[str]) {
							$scope.answerResult = $scope.selresultmap[str];
						} else {
							$scope.answerResult = [];
						}
						//正确率
						$scope.answerRate = ($scope.answerResult.length / $scope.studentCount) * 100;

					}
				}
				/*柱状图点击事件*/
			myChart.on('click', function(params) {
				var objs = option.xAxis;
				for(var i = 0; i < objs.data.length; i++) {
					var datavalue = objs.data[i];
					if(datavalue == params.name) {
						if(option.series[0].data[params.dataIndex].itemStyle.normal.color == '#fff') {
							option.series[0].data[params.dataIndex].itemStyle.normal.color = '#5ed6be';
							var isflag = letterlist.some(function(item) {
								item == params.name
							});
							if(!isflag) {
								letterlist.push(datavalue);
							}
						} else {
							option.series[0].data[params.dataIndex].itemStyle.normal.color = '#fff';
							var index = letterlist.indexOf(params.name);
							if(index > -1) {
								letterlist.splice(index, 1);
							}
						}
						//按ABC排序
						letterlist = letterlist.sort(function(item1, item2) {
							return item1.localeCompare(item2)
						});
						//转换成字符串
						str = letterlist.join('');
						_getanswerinfo(str);
						$scope.$apply(); //刷新正确率
						myChart.setOption(option);

					}
				}

			});
			if(option && typeof option === "object") {
				myChart.setOption(option, true);
			}
		} else {
			toastr.error($scope.result.message);
		}

	}

	//查看详情
	$scope.viewDetail = function() {
		$rootScope.userdetailshow = true;
	}
	window.onresize = function() {
		myChart.resize();
	}
	//返回柱状图
	$scope.returnBar = function() {
		$rootScope.userdetailshow = !$rootScope.userdetailshow;
	}
});
//停止单选答题
app.controller('stopSingeAnswerCtrl', function($scope,$rootScope, $location, toastr, $window) {	
	$('#myModal').modal('hide');
	//隐藏loading
	var _hideModal=function(){
		$('#myModal').modal('hide');
	}
	//显示loading
	var _showModal=function(){
		$('#myModal').modal('show');
	}
		if($location.search()) {
			$scope.answerType = $location.search().answerType; //单选类型(数字,字母,判断)
		}
		$rootScope.userdetailshow = false; //默认答题详情页面隐藏
		$rootScope.isStopAswer = false;//默认在停止答题页面
		$scope.studentNum = 0;
		var rangeList = []; //答题范围
		$scope.data = []; //柱状图数据
		//var resultmap = [];
		var colors = []; //颜色数组
		$scope.answerResult = []; //选择的正确人数数组
		$scope.answerRate = 0;
		var rangeLists = [];
		$scope.totalStudent=0;//总人数
		$scope.studentNum=0;
		//获取答题人数
		var ajaxnum1=0;//防止页面闪烁
		$scope.refresAnswerNum = function() {
				$scope.result = JSON.parse(execute_answer("get_single_answer_num"));
				$scope.studentNum = $scope.result.current;
				$scope.totalStudent = $scope.result.totalStudent;
				console.log("获取答题" + JSON.stringify($scope.result));
			}
			//停止答题
		var dom = document.getElementById("recordbar");
		dom.style.width = (window.innerWidth + 'px') / 100 + 'rem';
		var myChart = echarts.init(dom);
		$scope.stopAnswer = function() {
				ajaxnum1=1;
				_showModal();
				$scope.result = JSON.parse(execute_answer("stop_single_answer"));
				if(ajaxnum1==1){
					_hideModal();
				}
				if($scope.result.ret == 'success') {
					$rootScope.isStopAswer = true;
					$scope.resultmap = JSON.parse(execute_answer("get_single_answer"));
					 var keys1 = [];
				      for (var i in $scope.resultmap) {
				         if ($scope.resultmap.hasOwnProperty(i)){
				         	 if(i=='true'){
				         	 	i='Yes';			         	 	
				         	 }else if(i=='false'){
				         	 	i='No';
				         	 }
				         	 keys1.push(i);
				         }
				         
				             
				     }
					console.log("data" + JSON.stringify($scope.resultmap));
					if($scope.answerType == 'char') {
						//rangeList = ["A", "B", "C", "D"];
						rangeList=keys1;
					} else if($scope.answerType == 'number') {
						rangeList=keys1;
						//rangeList = ["1", "2", "3", "4", "5", "6", "7", "8", "9"];
					} else if($scope.answerType == 'judge') {
						rangeList=keys1;
						//rangeList = ["Yes", "No"];
						$scope.resultmap = {
							"Yes": $scope.resultmap["true"],
							"No": $scope.resultmap["false"]
						}
					}
					//$scope.rangeList=["A","B","C","D","E"];
					//console.log("答题范围" + JSON.stringify(rangeList))
					//$scope.resultmap={"A":10,"B":3};				
					$scope.data = [];
					if(rangeList.length > 0) {
						for(var i = 0; i < rangeList.length; i++) {
							if($scope.resultmap != null) {
								//console.log("item" + JSON.stringify($scope.resultmap))
								if($scope.resultmap[rangeList[i]]) {
									var item = {
										value: $scope.resultmap[rangeList[i]],
										itemStyle: {
											normal: {
												color: '#fff'
											}
										}
									}
								} else {
									var item = {
										value: 0,
										itemStyle: {
											normal: {
												color: '#fff'
											}
										}
									}
								}
							} else {
								var item = {
									value: 0,
									itemStyle: {
										normal: {
											color: '#fff'
										}
									}
								}
							}
							$scope.data.push(item);
							var coloritem = "#fff";
							colors.push(coloritem);
						}
					} else {
						var item = {
							value: 0,
							itemStyle: {
								normal: {
									color: '#fff'
								}
							}
						}
						$scope.data.push(item);
					}
					option = {
						color: ['#fff'],
						tooltip: {
							trigger: 'axis',
							axisPointer: { // 坐标轴指示器，坐标轴触发有效
								type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
							}
						},
						grid: {
							left: '3%',
							right: '4%',
							bottom: '3%',
							containLabel: true
						},
						xAxis: {
							type: 'category',
							//data : ['A', 'B', 'C','D','E','F'],
							data: rangeList,
							axisTick: {
								alignWithLabel: true
							},
							axisLine: {
								lineStyle: {
									color: '#dcdcdc', //这里是为了突出显示加上的，可以去掉
									show: false
								}
							},
							axisLabel: {
								show: true,
								textStyle: {
									color: '#fff',
									fontSize: '32'
								}
							}
						},
						yAxis: [{
							show: false,
							type: 'value',
							splitLine: false, //是否显示网格线
							axisLine: {
								lineStyle: {
									color: '#dcdcdc', //这里是为了突出显示加上的，可以去掉
									show: false
								}
							},
							axisLabel: {
								show: true,
								textStyle: {
									color: '#fff',
									fontSize: '16'
								}
							},
						}],
						series: [{
							name: '记录统计',
							type: 'bar',
							barWidth: '50%',
							//data:[7, 13, 5,14,2,0],
							data: $scope.data,
							label: {
								normal: {
									show: true,
									position: 'top',
									textStyle: {
										color: '#fff',
										fontSize: '32'
									}
								}
							},
							itemStyle: {
								normal: {　　　　　　　　　　　　
									color: function(params) {
										var colorList = colors;
										return colorList[params.dataIndex];
									}
								}
							},
						}]

					};
					/*柱状图点击事件*/
					myChart.on('click', function(params) {
						var objs = option.xAxis;
						for(var i = 0; i < objs.data.length; i++) {
							var datavalue = objs.data[i];
							if(datavalue == params.name) {
								option.series[0].data[params.dataIndex].itemStyle.normal.color = '#5ed6be';
								if($scope.answerType == 'judge') {
									if(datavalue == "Yes") {
										datavalue = "true";
									} else {
										datavalue = "false";
									}
								}
								var dataparam = {
									answer: datavalue
								}
								//作答正确人员
								$scope.answerResult = JSON.parse(execute_answer("get_single_answer_studentName", JSON.stringify(dataparam)));
								console.log(JSON.stringify($scope.answerResult))
								//正确率
								$scope.answerRate = ($scope.answerResult.length / $scope.totalStudent) * 100;
								$scope.$apply(); //刷新正确率
								continue;
							}
							option.series[0].data[i].itemStyle.normal.color = '#fff';
						}
						myChart.setOption(option);
					});
					if(option && typeof option === "object") {
						myChart.setOption(option, true);
					}
				} else {
					toastr.error($scope.result.message);
				}
			}
			// 重绘echarts图
			window.onresize = function() {
				myChart.resize();
			}
			//查看详情
		$scope.viewDetail = function() {
			$rootScope.userdetailshow = true;
		}
		//返回柱状图
		$scope.returnBar = function() {
			$rootScope.userdetailshow = !$rootScope.userdetailshow;
		}
		
		//返回开始答题
		/*$scope.returnPage=function(){
			$rootScope.userdetailshow = false; //默认答题详情页面隐藏
			$rootScope.isStopAswer = false;
		}*/
})
	//随堂检测
app.controller('classCheckCtrl', function($scope,$rootScope,toastr,$window) {
	$rootScope.userdetailshow = false; //默认答题详情页面隐藏
	$rootScope.isStopAswer = false;//默认在停止答题页面
	$scope.classInfo = {}; //班级信息
	$scope.paperInfoList = []; //试卷数组
	$('#myModal').modal('hide');
	//获取当前班级信息
	var _isStartClass = function() {
			$scope.result = JSON.parse(execute_record("get_classInfo"));
			//console.log("巴巴爸爸" + JSON.stringify($scope.result));
			if($scope.result.ret == 'success' && $scope.result.item) {
				$scope.classInfo = $scope.result.item;
			} else {
				$scope.classInfo = {};
			}
		}
		//查询试卷
	var _selectPaper = function() {
		var param = {}
		$scope.result = JSON.parse(execute_testPaper("select_now_paper", JSON.stringify(param)));
		console.log(JSON.stringify($scope.result));
		if($scope.result.ret == 'success') {
			$scope.paperInfoList = $scope.result.item;
			if($scope.result.item.length > 0) {
				$scope.paperInfoList[0].checked = true;
				$scope.onePaperInfo = $scope.paperInfoList[0];
			}
		} else {
			toastr.error($scope.result.message);
		}

	};

	//单选试卷
	$scope.selectOne = function(paperInfo) {
		if(paperInfo.checked == true) {
			$scope.onePaperInfo = paperInfo;
			angular.forEach($scope.paperInfoList, function(i) {
				if($scope.onePaperInfo.testId != i.testId) {
					i.checked = false; ///单选 
				}
			})
		} else {
			$scope.onePaperInfo = '';
		}

	}
	var _init = function() {
		_isStartClass();
		_selectPaper();
	}();

	//开始客观答题
	$scope.startObjective = function() {
			if($scope.onePaperInfo) {
				$scope.result = JSON.parse(execute_answer("start_class_test_objective", $scope.onePaperInfo.testId));
				if($scope.result.ret == 'success') {
					$scope.param = "testId=" + $scope.onePaperInfo.testId + "&answerType=" + "1";
					$scope.objectUrl = '../../page/answermoudle/classCheck.html' + '?' + $scope.param;
					$window.location.href = $scope.objectUrl;
				} else {
					toastr.error($scope.result.message);
				}
			} else {
				toastr.warning("请选择试卷");
			}
		}
		//开始主观答题
	$scope.AnswerSrcoe = function() {
		if($scope.onePaperInfo) {
			$scope.result = JSON.parse(execute_answer("start_class_test_subjective", $scope.onePaperInfo.testId));
			if($scope.result.ret == 'success') {
				$scope.param = "testId=" + $scope.onePaperInfo.testId + "&answerType=" + "0";
				$scope.objectUrl = '../../page/answermoudle/classCheck.html' + '?' + $scope.param;
				$window.location.href = $scope.objectUrl;
			} else {
				toastr.error($scope.result.message);
			}
		} else {
			toastr.warning("请选择试卷");
		}

	}

	//上传服务
	$scope.uploadServer = function() {
		if($scope.onePaperInfo) {
			$('#myModal').modal('show');
			$scope.result = JSON.parse(execute_answer("upload_server", $scope.onePaperInfo.testId));
			//		setTimeout(function(){			
			//			console.log(JSON.stringify($scope.result))
			//			if($scope.result.ret=='success'){
			//				$('#myModal').modal('hide');
			//			}else{
			//				toastr.error($scope.result.message);
			//				$('#myModal').modal('hide');
			//			}
			//		},500)	
		}else {
			toastr.warning("请选择试卷");
		}
	}
		//移除loading
	$scope.removeLoading = function() {
		$('#myModal').modal('hide');
	}
		//显示loading
	$scope.showLoading = function() {
		$('#myModal').modal('show');
	}

	//提示框
	$scope.getTip = function() {
		if(ret == 'true') {
			toastr.success(message);
			$scope.isgatherPaper = false;
		} else {
			$scope.isgatherPaper = true;
			toastr.error(message);
		}
	}
})
//随堂检测人员进度条控制器
app.controller('classuserCheckCtrl', function($scope, toastr, $location, $window, $modal) {
		if($location.search()) {
			$scope.paperInfo = $location.search(); //试卷id	答题类型answerType("0":主观答题,"1":客观答题)
			console.log(JSON.stringify($scope.paperInfo))
		}
		$scope.AllanswerInfo = []; //作答信息数组	
		$scope.oneanswerList = []; //个人答题详情
		$('#myModal').modal('hide'); //默认隐藏loading
		$scope.isclick = true; //默认不能点击
		$scope.isgatherPaper = true; //是否是收取试卷按钮
		//隐藏loading
		var _hideModal = function() {
				$('#myModal').modal('hide');
			}
			//显示loading
		var _showModal = function() {
				$('#myModal').modal('show');
			}
			//查询每个人的个人信息
		var _getAllanswerInfo = function() {
				$scope.result = JSON.parse(execute_answer("get_everybody_answerInfo"));
				//console.log(JSON.stringify($scope.result))
				if($scope.result.ret == 'success') {
					$scope.AllanswerInfo = $scope.result.item;
					if($scope.result.item.length > 0) {
						angular.forEach($scope.AllanswerInfo, function(i) {
							i.style = {
								width: i.percent * 100 + '%',
								background: '#7ee074'
							}
							console.log(JSON.stringify($scope.AllanswerInfo))
						})

					}
				} else {
					toastr.error($scope.result.message);
				}
			}
			//收取试卷
		$scope.gatherPaper = function() {
				_showModal();
				if($scope.paperInfo.answerType == "1") {
					$scope.result = JSON.parse(execute_answer("stop_class_test_objective", $scope.paperInfo.testId));
				} else {
					$scope.result = JSON.parse(execute_answer("stop_class_test_subjective", $scope.paperInfo.testId));
				}
				console.log(JSON.stringify($scope.result))
			}
			//查询主观答题和客观答题的单个人的信息
		$scope.selectRecord = function(item) {
			if($scope.isclick == false) {
				var param = {
					testId: $scope.paperInfo.testId,
					studentId: item.studentId
				}
				if($scope.paperInfo.answerType == "1") {
					$scope.result = JSON.parse(execute_record("select_objective_record", JSON.stringify(param)));
				} else {
					$scope.result = JSON.parse(execute_record("select_subjective_record", JSON.stringify(param)));
				}
				//console.log(JSON.stringify($scope.result))
				if($scope.result.ret == 'success') {
					$scope.oneanswerList = [];
					$scope.oneanswerList = $scope.result.item;
					var modalInstance = $modal.open({
						templateUrl: 'oneAnswerDetailModal.html',
						controller: 'oneAnswerDetailCtrl',
						size: 'md',
						backdrop: false,
						resolve: {
							infos: function() {
								return $scope.oneanswerList;
							},
							type: function() {
								return $scope.paperInfo.answerType;
							}
						}
					});

					modalInstance.result.then(function(info) {}, function() {});

				} else {
					toastr.error($scope.result.message);
				}

			}
		}

		var _init = function() {
			_getAllanswerInfo();
		}();
		//刷新
		$scope.refreClassTest = function() {
			_getAllanswerInfo();
		}

		//移除loading
		$scope.removeLoading = function() {
				_hideModal();
			}
			//显示loading
		$scope.showLoading = function() {
			$('#myModal').modal('show');
		}

		//提示框
		$scope.getTip = function() {
			if(ret == 'true') {
				toastr.success(message);
				$scope.isgatherPaper = false;
				$scope.isclick = false;
			} else {
				$scope.isgatherPaper = true;
				toastr.error(message);
			}
		}
	})
	//个人答题详情
app.controller('oneAnswerDetailCtrl', function($scope, $modalInstance, toastr, infos, type) {
	$scope.oneanswerLists = []; //个人答题详情
	if(type) {
		$scope.answerType = type; //作答类型（主客）
	}
	if(infos) {
		$scope.oneanswerLists = angular.copy(infos);
	}

	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	}

})

//人员列表
/*app.controller('userAnswerListCtrl', function($scope, toastr, $location, $window, $modal) {
	$scope.userAnswerList = []; //人员作答数组
	if($location.search()) {
		$scope.userAnswerList = JSON.parse($location.search().userAnswerList);
	}
	$scope.returnPage = function() {
		$scope.param = "isStopAswer=" + true;
		$scope.objectUrl = '../../page/answermoudle/stopAnswerType.html' + '?' + $scope.param;
		$window.location.href = $scope.objectUrl;
	}
})
*/
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
				if(scope.defalutvalue) {
					if(scope.list) {
						var str = '';
						for(var i = 0; i < scope.list.length; i++) {
							str += '<option value="' + scope.list[i] + '">' + scope.list[i] + '</option>';
						}
						$(element).html(str);
					}

					/*$(element).multiselect({
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
					});*/
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
			})

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
			case 'true':
				{
					statename = '√';
					break;
				}
			case 'false':
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