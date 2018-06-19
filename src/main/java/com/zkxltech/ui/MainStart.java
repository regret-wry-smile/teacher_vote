package com.zkxltech.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseMotionListener;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.sun.awt.AWTUtilities;
import com.zkxltech.config.ConfigConstant;
import com.zkxltech.config.Global;
import com.zkxltech.ui.util.PageConstant;
import com.zkxltech.ui.util.SwtTools;
import org.eclipse.swt.events.MouseTrackAdapter;
/*
 * 	页面回调指令
 * 	start_answer 开始作答
 */
public class MainStart {

	protected Object result;
	public static Shell shell;
	
	/*悬浮框对应参数*/
	public static JFrame frame;
	private Panel panel;
	private String title = Global.VERSION; //窗口标题
	int Window_Width = Toolkit.getDefaultToolkit().getScreenSize().width;
	int Window_Height = Toolkit.getDefaultToolkit().getScreenSize().height;
	int Frame_Width = 50;
	int Frame_Height = 50;
	int shellX,shellY;/* 悬浮窗口坐标 */
	int x1, y1;// 鼠标释放位置
	int mouse_X = 0, mouse_Y = 0;
	
	private int shellMaxWidth;/* 窗口最大宽度 */
	private int shellMaxHeight;/* 窗口最大高度 */
	private int shellMainX;/* 窗口x坐标 */
	private int shellMainY;/* 窗口y坐标 */
	public static boolean isShow = false;
	private ImageIcon imageIcon, icon, backgroundIcon; 
	
	private static MainStart mianStart;
	private boolean isTest;
	
	private static boolean isOver = false;
	private Thread thread;

	public MainStart(Shell parent) {
		shell = parent;
	}
	
	public static void main(String[] args) {
		mianStart = new MainStart(new Shell());
		mianStart.initialize();
		mianStart.open();
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
	
	public void initImage() {
		/*任务栏图片*/
		imageIcon = new ImageIcon(this.getClass().getResource(PageConstant.image01));
		/*默认背景图片*/
		icon = new ImageIcon(this.getClass().getResource(PageConstant.image01));
		/*背景颜色图片*/
		backgroundIcon = new ImageIcon(this.getClass().getResource(PageConstant.image02));
	}
	
	public void changeImage(){
		if (isShow) {
			icon = new ImageIcon(this.getClass().getResource(PageConstant.image01));
		}else {
			icon = new ImageIcon(this.getClass().getResource(PageConstant.image01));
		}
	}
	
	public void closeShell() {
		shell.setVisible(false);
	}

	public void showShell() {
		shell.setLocation(shellX, shellY-shellMaxHeight);
		shell.setVisible(true);
		
	}
	
	
	//返回悬浮框状态
	public void floatingWindow(){
		isShow = false;
		frame.setVisible(true);
		shell.setVisible(false);
	}
	
	/* 初始化配置 */
	private void init() {
//		shellMaxWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width / 2.4);
//		shellMaxHeight = shellMaxWidth * 560 / 800;
		shellMaxWidth = Frame_Width;
		shellMaxHeight = 160;
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
				backgroundIcon.paintIcon(this, g2d, -15, -15);
				icon.paintIcon(this, g2d, 3, 3);
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
        		}
        	}
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
//				frame.setBounds(shellX, shellY, Frame_Width, Frame_Height);
//				x = shellX;
//				y = shellY;
//				x1 =shellX;
//				y1 =shellY;
				if (!isShow) {
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
					    	showShell();
					    	}
					    });
				}else {
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
					    	closeShell();
					    	}
					    }); 
				}
			}
			
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				Display.getDefault().syncExec(new Runnable() {
				    public void run() {
				    	showShell();
				    	}
				    });
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				if(!isOver){
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
					    	closeShell();
					    	}
					    }); 
				}
			}
			
			
        });
		panel.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				if (!isShow) {
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
					    	closeShell();
					    	}
					    });
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
	
	}
	
	
	
	private void createContents() {
		shell = new Shell(shell, SWT.NO_TRIM | SWT.ON_TOP);
		shell.setVisible(false);
		shell.setSize(shellMaxWidth, shellMaxHeight);
		shell.setLocation(shellMainX, shellMainY);
//		shell.setLayout(new FormLayout());
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
		cLabel01.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.close_white));
		cLabel01.setText("关闭");
		cLabel01.setBounds(0, 0, 50, 40);
		cLabel01.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(e.button == 1){
					MessageBox messageBox = new MessageBox(new Shell(),SWT.ICON_QUESTION|SWT.YES|SWT.NO);
	        		messageBox.setMessage("确定要退出？");
	        		if (messageBox.open() == SWT.YES) {
	        			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	        			 ClassHourSql classHourSql = new ClassHourSql();
//	                     classHourSql.updateClassHourById(classHourSql.getId(ClassSelection. classHour), simpleDateFormat.format(new Date()));
	                     System.exit(0); 
					}; 
				}
			}
		});
		
		//答题
		CLabel cLabel02 = new CLabel(shell, SWT.NONE);
		cLabel02.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.close_white));
		cLabel02.setBounds(0, 40, 50, 40);
		cLabel02.setText("答题");
		cLabel02.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(e.button == 1){
					frame.setVisible(false);
					shell.setVisible(false);
					new MainPage(shell,mianStart).open();
				}
			}
		});
		
		//设置
		CLabel cLabel03 = new CLabel(shell, SWT.NONE);
		cLabel03.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.close_white));
		cLabel03.setBounds(0, 80, 50, 40);
		cLabel03.setText("设置");
		cLabel03.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(e.button == 1){
					
				}
			}
		});
		
		//记录
		CLabel cLabel04 = new CLabel(shell, SWT.NONE);
		cLabel04.setBackground(SWTResourceManager.getImage(MainStart.class, PageConstant.close_white));
		cLabel04.setBounds(0, 120, 50, 40);
		cLabel04.setText("记录");
		cLabel04.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(e.button == 1){
					
				}
			}
		});
		cLabel01.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				isOver = true;
				if(thread != null){
					thread.stop();
				}
			}
			@Override
			public void mouseExit(MouseEvent e) {
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(200);
							isOver = false;
							Display.getDefault().syncExec(new Runnable() {
							    public void run() {
							    	closeShell();
							    	}
							    });
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				thread.start();
			}
		});
		cLabel02.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				isOver = true;
				if(thread != null){
					thread.stop();
				}
			}
			@Override
			public void mouseExit(MouseEvent e) {
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(200);
							isOver = false;
							Display.getDefault().syncExec(new Runnable() {
							    public void run() {
							    	closeShell();
							    	}
							    });
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				thread.start();
			}
		});
		cLabel03.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				isOver = true;
				if(thread != null){
					thread.stop();
				}
			}
			@Override
			public void mouseExit(MouseEvent e) {
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(200);
							isOver = false;
							Display.getDefault().syncExec(new Runnable() {
							    public void run() {
							    	closeShell();
							    	}
							    });
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				thread.start();
			}
		});
		cLabel04.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				isOver = true;
				if(thread != null){
					thread.stop();
				}
			}
			@Override
			public void mouseExit(MouseEvent e) {
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(200);
							isOver = false;
							Display.getDefault().syncExec(new Runnable() {
							    public void run() {
							    	System.out.println(123);
							    	closeShell();
							    	}
							    });
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				thread.start();
			}
		});
		
		cLabel01.addMouseTrackListener(SwtTools.showHand(cLabel01));
		cLabel02.addMouseTrackListener(SwtTools.showHand(cLabel02));
		cLabel03.addMouseTrackListener(SwtTools.showHand(cLabel03));
		cLabel04.addMouseTrackListener(SwtTools.showHand(cLabel04));
	}
}
