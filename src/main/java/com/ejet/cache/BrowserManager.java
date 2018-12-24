package com.ejet.cache;

import java.lang.ref.WeakReference;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 【回调页面方法】
 *
 */
public class BrowserManager {
	private static final Logger log = LoggerFactory.getLogger(BrowserManager.class);
	private static WeakReference<Browser> browerManager = null; 
	private static Shell shell = null;
	
	public static void setShell(Shell shell) {
		BrowserManager.shell = shell;
	}
	public static void setBrower(Browser brower) {
		browerManager = new WeakReference<Browser>(brower);
	}
	
	/*******************/
	/****** 设置模块   ******/
	/*******************/
	/**
	 * 数据绑定调用页面刷新
	 * @param echoRequest
	 */
	public static void refreshBindCard() {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					log.info("刷新绑定页面：3");
					boolean  doRet = b.execute("document.getElementById('refreshBindCard').click();");
					log.info("刷新绑定页面："+doRet);
				}
			});
		}
	}
//	/**
//	 * 选中当前班级
//	 */
//	public static void selectClass(String classId) {
//		Browser b  = browerManager.get();
//		if (b!=null) {
//			shell.getDisplay().syncExec(new Runnable() {
//				@Override
//				public void run() {
//					boolean  doRet = b.execute("var classId = '"+classId+"';document.getElementById('selectClass').click();");
//				}
//			});
//		}
//	}
	
	/**
	 * 刷新班级
	 */
	public static void refreshClass() {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("document.getElementById('refreshClass').click();");
					log.info("刷新班级："+doRet);
				}
			});
		}
	}
	/**
	 * 刷新学生
	 */
	public static void refreshStudent(String classId) {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("var classId = '"+classId+"';document.getElementById('refreshStudent').click();");
					log.info("刷新学生："+doRet);
				}
			});
		}
	}
	/**
	 * 调用页面弹出相关提示(不需要回调)
	 * @param message 提示信息
	 */
	public static void showMessage(boolean ret,String message) {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("var ret = '"+ret+"';var message ='"+message+"';document.getElementById('getTip').click();");
					log.info("调用页面提示框："+doRet);
				}
			});
		}
	}
	
	@Deprecated
	/*弹出加载图片*/
	public static void showLoading(){
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("var message = '正在导入';document.getElementById('showLoading').click();");
					log.info("弹出正在加载页面："+doRet);
				}
			});
				
		}
	}
	
	/*移除正在加载*/
	public static void removeLoading(){
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("document.getElementById('removeLoading').click();");
					log.info("移除正在加载页面："+doRet);
				}
			});
				
		}
	}
	
	/**
	 * 刷新考勤数据
	 * @param echoRequest
	 */
	public static void refresAttendance() {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("document.getElementById('refresAttendance').click();");
					log.info("刷新考勤数据："+doRet);
				}
			});
		}
	}
	
	/**
	 * 刷新评分数据
	 * @param echoRequest
	 */
	public static void refresScore() {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("document.getElementById('refresScore').click();");
					log.info("刷新评分数据："+doRet);
				}
			});
		}
	}
	
	/**
	 * 刷新投票数据
	 * @param echoRequest
	 */
	public static void refresVote() {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("document.getElementById('refresVote').click();");
					log.info("刷新投票数据："+doRet);
				}
			});
		}
	}
	
	/**
	 * 刷新单题多选作答人数
	 * @param echoRequest
	 */
	public static void refresAnswerNum() {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("document.getElementById('refresAnswerNum').click();");
					log.info("刷新单题多选作答人数："+doRet);
				}
			});
		}
	}
	
	/**
	 * 刷新随堂检测作答进度
	 * @param echoRequest
	 */
	public static void refreClassTest() {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("document.getElementById('refreClassTest').click();");
					log.info("刷新随堂检测作答进度："+doRet);
				}
			});
		}
	}
	
	/**
	 * 刷新试卷下拉列表
	 * @param echoRequest
	 */
	public static void refreTestPaper(String testinfo) {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("var testinfo = '"+testinfo+"';document.getElementById('refreTestPaper').click();");
					log.info("刷新试卷下拉列表："+doRet);
				}
			});
		}
	}
	/**
	 * 刷新试卷列表
	 * @param echoRequest
	 */
	public static void refreTestPaper2() {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("document.getElementById('refreTestPaper2').click();");
					log.info("刷新试卷列表："+doRet);
				}
			});
		}
	}
	
	/**
	 * 刷新设备状态
	 * @param echoRequest
	 */
	public static void refreEquipmentState(boolean flag) {
		Browser b  = browerManager.get();
		if (b!=null) {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					boolean  doRet = b.execute("var isOnline = "+flag+";document.getElementById('refreEquipmentState').click();");
					log.info("刷新设备状态："+doRet);
				}
			});
		}
	}
	
	/**
     * 刷新查询作答记录
     * @param echoRequest
     */
    public static void refreSelectRecord(String result) {
        Browser b  = browerManager.get();
        if (b!=null) {
            shell.getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    boolean  doRet = b.execute("var result = '"+result+"';document.getElementById('refreSelectRecord').click();");
                    log.info("刷新查询作答记录："+doRet);
                }
            });
        }
    }
}
