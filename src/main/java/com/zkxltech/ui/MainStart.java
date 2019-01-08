package com.zkxltech.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.App;
import com.ejet.core.util.io.IOUtils;
import com.sun.awt.AWTUtilities;
import com.zkxltech.config.ConfigConstant;
import com.zkxltech.config.Global;
import com.zkxltech.service.impl.ClassHourServiceImpl;
import com.zkxltech.ui.util.Colors;
import com.zkxltech.ui.util.PageConstant;
import com.zkxltech.ui.util.StringConstant;
import com.zkxltech.ui.util.StringUtils;
import com.zkxltech.ui.util.SwtTools;
/*
 * 	页面回调指令
 * 	start_answer 开始作答
 */
public class MainStart {
	private static final Logger log = LoggerFactory.getLogger(MainStart.class);
	protected Object result;
	public static Shell shell;
	
	/*悬浮框对应参数--图标部分*/
	public static JFrame frame;
	private Panel panel;
	private String title = Global.VERSION; //窗口标题
	int Window_Width = Toolkit.getDefaultToolkit().getScreenSize().width;
	int Window_Height = Toolkit.getDefaultToolkit().getScreenSize().height;
	int Frame_Width = 59;
	int Frame_Height = 59;
	int shellX,shellY;/* 悬浮窗口坐标 */
	int x1, y1;// 鼠标释放位置
	int mouse_X = 0, mouse_Y = 0;
	/*悬浮框对应参数--图标上方部分*/
	private int shellMaxWidth;/* 窗口最大宽度 */
	private int shellMaxHeight;/* 窗口最大高度 */
	private int shellMainX;/* 窗口x坐标 */
	private int shellMainY;/* 窗口y坐标 */
	private ImageIcon imageIcon, icon; 
	
	private static MainStart mianStart;
	private boolean isTest;
	
	private static boolean isShow = false;
	private static boolean flag = false;
	
	
	public MainStart(Shell parent) {
		shell = parent;
	}
	
	public static void main(String[] args) {
		try {
			//启动答题器通信等接口
			new Thread(new Runnable() {
				@Override
				public void run() {
					App.startCommunication();
				}
			}).start();
			
			mianStart = new MainStart(new Shell());
			mianStart.initialize();
			mianStart.open();
		} catch (Exception e) {
			log.error(IOUtils.getError(e));
		}
		
	}
	
	//悬浮框
	public void initData(){
		shellX = Window_Width/10*9;
//		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		Rectangle maximumWindowBounds = graphicsEnvironment.getMaximumWindowBounds();
		shellY = Window_Height/10*7;;
		x1 =shellX;
		y1 =shellY;
	}
	/**
	 * 重置变量值
	 */
	public void resetValue(){
		isShow = false;
		flag = true;
	}
	
	public void initImage() {
		/*任务栏图片*/
		imageIcon = new ImageIcon(this.getClass().getResource(PageConstant.image01));
		/*默认背景图片*/
		icon = new ImageIcon(this.getClass().getResource(PageConstant.image01));
	}
	
	public void changeImage(){
		if (flag) {
			icon = new ImageIcon(this.getClass().getResource(PageConstant.image01));
		}else {
			icon = new ImageIcon(this.getClass().getResource(PageConstant.image02));
		}

		frame.repaint();
		flag = !flag;
	}
	
	public void closeShell() {
		shell.setVisible(false);
		changeImage();
	}

	public void showShell() {
		shell.setLocation(shellX, shellY-shellMaxHeight);
		shell.setVisible(true);
		changeImage();
	}
	
	
	//返回悬浮框状态
	public void floatingWindow(){
		resetValue();
		frame.setVisible(true);
		shell.setVisible(false);
		changeImage();
		frame.repaint();
	}
	
	/* 初始化配置 */
	private void init() {
//		shellMaxWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width / 2.4);
//		shellMaxHeight = shellMaxWidth * 560 / 800;
		shellMaxWidth = Frame_Width;
		shellMaxHeight = 155;
		shellMainX = 0;
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle maximumWindowBounds = graphicsEnvironment.getMaximumWindowBounds();
		shellMainY = maximumWindowBounds.height - shellMaxHeight - 6;
		
		isTest = Boolean.parseBoolean(ConfigConstant.projectConf.getApp_test());
	}
	
	public Shell open() {
		try {
			init();
			createContents();
			shell.layout();
			Display display = shell.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
		}
		return shell;
	}
	
	private void initialize() {
		initImage();//初始化
		initData();// 初始化窗口位置
		frame = new JFrame();
		frame.setUndecorated(true); // 不装饰
		AWTUtilities.setWindowOpaque(frame, false);
		frame.getContentPane().setLayout(null);// 设置布局
		frame.setBounds(shellX, shellY, Frame_Width, Frame_Height);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* 默认显示的圆形 */
		panel = new Panel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2d = (Graphics2D) g;
				icon.paintIcon(this, g2d, 0, 0);
				g.dispose();
			}
		};
		panel.setBounds(0, 0, Frame_Width, Frame_Height);
		/*鼠标拖拽事件*/
		panel.addMouseListener(new java.awt.event.MouseAdapter(){
         	@Override
         	public void mousePressed(java.awt.event.MouseEvent e) {
         		if (!isShow) {
         			mouse_X =e.getXOnScreen();
             		mouse_Y =e.getYOnScreen();
				}
         	}
        	@Override
        	public void mouseReleased(java.awt.event.MouseEvent e) {
        		if (!isShow) {
            		x1 =shellX;
            		y1 =shellY;
            		Display.getDefault().syncExec(new Runnable() {
					    public void run() {
							flag = false;
					    	showShell();
					    	}
					    });
        		}
        	}
			
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if(e.getX() >= 0 && e.getX() <= Frame_Width){
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
							flag = false;
					    	showShell();
					    	}
					    });
				}
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				if(e.getX() <= 0 || e.getX() >= Frame_Width || e.getY() >= Frame_Height){
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
							flag = true;
					    	closeShell();
					    	}
					    });
				}
			}
			
			
        });
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			
//			@Override
//			public void mouseMoved(java.awt.event.MouseEvent e) {
//				shellX=x1 + e.getXOnScreen()-mouse_X;
//				shellY=y1 +e.getYOnScreen()-mouse_Y;
//				frame.setBounds(shellX, shellY, Frame_Width, Frame_Height);
//			}

			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				Display.getDefault().syncExec(new Runnable() {
				    public void run() {
				    	if(shell.isVisible()){
					    	closeShell();
					    	flag = false;
				    	}
				    }
				});
				if (!isShow) {
					shellX=x1 + e.getXOnScreen()-mouse_X;
					shellY=y1 +e.getYOnScreen()-mouse_Y;
					frame.setBounds(shellX, shellY, Frame_Width, Frame_Height);
				}
			}
		});
		frame.getContentPane().add(panel);

		frame.setIconImage(imageIcon.getImage()); // 任务栏图片
		frame.setTitle(title);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);

		// //显示手状
		panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		
		frame.repaint();
	
	}
	
	
	
	private void createContents() {
		try {
			shell = new Shell(shell, SWT.NO_TRIM | SWT.ON_TOP);
			shell.setBackground(Colors.color_blue02);
			shell.setSize(shellMaxWidth, shellMaxHeight);
			shell.setLocation(shellMainX, shellMainY);
			shell.addTraverseListener(new TraverseListener() {
				public void keyTraversed(TraverseEvent e) {
					// 屏蔽按下Esc按键
					if (e.detail == SWT.TRAVERSE_ESCAPE) {
						e.doit = false;
					}
				}
			});
			
			 
			//关闭
			CLabel cLabel01 = new CLabel(shell, SWT.NONE);
			cLabel01.setToolTipText("Shut down");//关闭
			cLabel01.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_close));
			cLabel01.setBounds(0, 0, 60, 38);
			cLabel01.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if(e.button == 1){
						MessageBox messageBox = new MessageBox(new Shell(),SWT.ICON_QUESTION|SWT.YES|SWT.NO);
		        		messageBox.setMessage("Are you sure you want to quit?");//确定要退出？
		        		if (messageBox.open() == SWT.YES) {
		        			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		        			 ClassHourSql classHourSql = new ClassHourSql();
//		                     classHourSql.updateClassHourById(classHourSql.getId(ClassSelection. classHour), simpleDateFormat.format(new Date()));
		                     System.exit(0); 
						}; 
					}
				}
			});
			
			//答题
			CLabel cLabel02 = new CLabel(shell, SWT.NONE);
			cLabel02.setToolTipText("Answer the questions");//答题
			cLabel02.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_answer));
			cLabel02.setBounds(0, 38, 59, 38);
			cLabel02.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if(e.button == 1){
						if (!StringUtils.isEmpty(com.ejet.core.util.constant.Global.classId)) {
							ClassHourServiceImpl.refreshGload();
						}
						frame.setVisible(false);
						shell.setVisible(false);
						new MainPage(shell,mianStart,StringConstant.PAGE_ANSWER_TYPE).open();
					}
				}
			});
			//设置
			CLabel cLabel03 = new CLabel(shell, SWT.NONE);
			cLabel03.setToolTipText("Setting up");//设置
			cLabel03.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_set));
			cLabel03.setBounds(0, 76, 60, 40);
			cLabel03.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if(e.button == 1){
						frame.setVisible(false);
						shell.setVisible(false);
						new MainPage(shell,mianStart,StringConstant.PAGE_SET_TYPE).open();
					}
				}
			});
			//记录
			CLabel cLabel04 = new CLabel(shell, SWT.NONE);
			cLabel04.setToolTipText("Record");//记录
			cLabel04.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_record));
			cLabel04.setBounds(0, 114, 60, 40);
			cLabel04.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if(e.button == 1){
						frame.setVisible(false);
						shell.setVisible(false);
						new MainPage(shell,mianStart,StringConstant.PAGE_RECORD_TYPE).open();
					}
				}
			});
			cLabel01.addMouseTrackListener(new MouseTrackAdapter() {
			
				@Override
				public void mouseEnter(MouseEvent e) {
					cLabel01.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_close02));
					cLabel02.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_answer));
					cLabel03.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_set));
					cLabel04.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_record));
				}

				@Override
				public void mouseExit(MouseEvent e) {
					if(e.x <= 0 || e.x >= Frame_Width || e.y <= 0){
						Display.getDefault().syncExec(new Runnable() {
						    public void run() {
								flag = true;
						    	closeShell();
						    	}
						    });
						cLabel01.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_close));
					}
				}
			});
			cLabel02.addMouseTrackListener(new MouseTrackAdapter() {

				@Override
				public void mouseEnter(MouseEvent e) {
					cLabel01.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_close));
					cLabel02.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_answer02));
					cLabel03.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_set));
					cLabel04.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_record));
				}

				@Override
				public void mouseExit(MouseEvent e) {
					if(e.x <= 0 || e.x >= Frame_Width){
						Display.getDefault().syncExec(new Runnable() {
						    public void run() {
								flag = true;
						    	closeShell();
						    	}
						    });
						cLabel02.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_answer));
					}
				}
			});
			cLabel03.addMouseTrackListener(new MouseTrackAdapter() {
				@Override
				public void mouseEnter(MouseEvent e) {
					cLabel01.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_close));
					cLabel02.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_answer));
					cLabel03.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_set02));
					cLabel04.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_record));
				}
				@Override
				public void mouseExit(MouseEvent e) {
					if(e.x <= 0 || e.x >= Frame_Width){
						Display.getDefault().syncExec(new Runnable() {
						    public void run() {
								flag = true;
						    	closeShell();
						    	}
						    });
						cLabel03.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_set));
					}
				}
			});
			cLabel04.addMouseTrackListener(new MouseTrackAdapter() {
				@Override
				public void mouseEnter(MouseEvent e) {
					cLabel01.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_close));
					cLabel02.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_answer));
					cLabel03.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_set));
					cLabel04.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_record02));
				}
				@Override
				public void mouseExit(MouseEvent e) {
					if(e.x <= 0 || e.x >= Frame_Width){
						Display.getDefault().syncExec(new Runnable() {
						    public void run() {
								flag = true;
						    	closeShell();
						    	}
						    });
						cLabel04.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.select_record));
					}
				}
			});
			
			cLabel01.addMouseTrackListener(SwtTools.showHand(cLabel01));
			cLabel02.addMouseTrackListener(SwtTools.showHand(cLabel02));
			cLabel03.addMouseTrackListener(SwtTools.showHand(cLabel03));
			cLabel04.addMouseTrackListener(SwtTools.showHand(cLabel04));
		} catch (Exception e) {
			log.error(IOUtils.getError(e));
		}
		
	}
}
