package com.elliott.notepad.view;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.undo.UndoManager;

import com.elliott.notepad.JFontChooser;
import com.elliott.notepad.controller.Controller;


public class MainFrame extends JFrame{
 
	private static final long serialVersionUID = 1L;
	//set the default file name as no title
	public String fileName = "无标题";
	private Controller con;
	private JScrollPane sp = new JScrollPane();
	public JTextArea body = new JTextArea();
	//use for paste function
	public JMenuItem pasteItem; //粘贴功能
	//use for mouse right click menu
	private JPopupMenu popMenu = new JPopupMenu();     //右击鼠标弹出的菜单
	//use for auto wrap
	public JMenuItem lineItem = new JMenuItem(); //自动换行选项
	//use for choose font
	public JMenuItem fontItem = new JMenuItem(); //字体选项
	//use for undo function
	public UndoManager undoMgr = new UndoManager(); //撤销管理器
	//use clipboard to temporary storage of information
	public Clipboard clipboard = null; //剪贴板
	
	public void setController(Controller con){
		this.con = con;
	}
	
	//初始化
	//initialization
	public void init(){
		//设置ICON
		java.net.URL imgURL = MainFrame.class.getResource("icon.png");
		ImageIcon imgIcon = new ImageIcon(imgURL);
		Image img = imgIcon.getImage();
		this.setIconImage(img);

		//设置默认字体
		body.setFont(new Font("微软雅黑", Font.PLAIN, 18));

		//set application title as file name plus simple notepad
		this.setTitle(fileName+" - 记事本");
		this.setSize(1500,800);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				//overwrite close function
				con.exit(); //重写默认关闭按钮		
			}

		});
		
		//菜单栏
		//menu bar
		JMenuBar mb = new JMenuBar();		
		mb.setBackground(Color.white);
		mb.setFont(new Font("微软雅黑", Font.PLAIN, 15));

		//定义“文件”菜单，包含新建，打开，保存，另存为，退出功能
		//define some functions in file menu
		JMenu fileMenu = new JMenu();
		fileMenu.setText("文件(F)");
		
		//新建
		//new file function
		JMenuItem newItem = new JMenuItem();
		newItem.setText("\t新建\t(N)");
		newItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.createFile();
			}
			
		});
		
		//打开
		//open file function
		JMenuItem openItem = new JMenuItem();
		openItem.setText("\t打开\t(O)...");
		openItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.openFile();
			}
			
		});
		
		//保存
		//save file function
		JMenuItem saveItem = new JMenuItem();
		saveItem.setText("\t保存\t(S)");
		//saveItem.setEnabled(false);
		saveItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.saveFile();
			}
			
		});
		
		//另存为
		//save as file function
		JMenuItem saveForItem = new JMenuItem();
		saveForItem.setText("\t另存为\t(A)...");
		saveForItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.saveForFile();
			}
			
		});
		
		//退出
		//exit function
		JMenuItem exitItem = new JMenuItem();
		exitItem.setText("\t退出\t(X)");
		exitItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.exit();
			}
			
		});
		
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveForItem);
		fileMenu.add(exitItem);		
		mb.add(fileMenu);
		
		//定义“编辑”菜单，包含剪切，复制，粘贴，全选，撤销功能
		//define some functions into edit menu
		JMenu editMenu = new JMenu();
		editMenu.setText("\t编辑(E)");
		
		//剪切
		//cut function
		final JMenuItem cutItem = new JMenuItem();
		cutItem.setText("\t剪切");
		cutItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.cut();				
			}
			
		});
				
		//复制
		//copy function
		final JMenuItem copyItem = new JMenuItem();
		copyItem.setText("\t复制");
		copyItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.copy();
			}
			
		});
		
		//粘贴
		//paste function
		pasteItem = new JMenuItem();
		pasteItem.setText("\t粘贴");
		pasteItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.paste();
			}
			
		});
		
		//全选
		//select all function
		JMenuItem selectAllItem = new JMenuItem();
		selectAllItem.setText("\t全选");
		selectAllItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.selectAll();
			}
			
		});
		
		//撤销
		//undo function
		JMenuItem rollbackItem = new JMenuItem();
		rollbackItem.setText("\t撤销");
		rollbackItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.rollback();
			}
			
		});
		
		editMenu.add(rollbackItem);
		editMenu.add(cutItem);
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.add(selectAllItem);		
		editMenu.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(body.getText()==""){
					cutItem.setEnabled(false);
					copyItem.setEnabled(false);
				}else{
					cutItem.setEnabled(true);
					copyItem.setEnabled(true);
				}
				
				if(clipboard.getContents(this)==null){
					pasteItem.setEnabled(false);
				}else{
					pasteItem.setEnabled(true);
				}
			}
			
		});
		mb.add(editMenu);
		
		//格式菜单，具有自动换行可选和字体设置功能
		//define some functions into data format menu
		JMenu formatMenu = new JMenu();
		formatMenu.setText("格式(O)");
		
		//自动换行
		//auto wrap
		lineItem = new JMenuItem();
		lineItem.setText("\t√自动换行");
		lineItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.lineWrap();
			}
			
		});
		fontItem = new JMenuItem("\t字体");

		fontItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFontChooser one = new JFontChooser(body.getFont(), body.getForeground());
				one.showDialog(null,500,200);
				//获取选择的字体
				Font font=one.getSelectedfont();

				if(font!=null){
					body.setFont(font);
				}
			}

		});

		formatMenu.add(lineItem);
		formatMenu.add(fontItem);
		mb.add(formatMenu);


		//查看 菜单
		JMenu viewMenu = new JMenu("查看(V)");
		JMenuItem statusItem=new JMenuItem("\t状态栏(S)");
		statusItem.setEnabled(false);

		viewMenu.add(statusItem);
		mb.add(viewMenu);

		//关于菜单
		//define a menu to show mainPgae information
		JMenu helpMenu = new JMenu();
		helpMenu.setText("帮助(H)");
		JMenuItem mainPageItem = new JMenuItem("\t查看项目主页");
		JMenuItem aboutItem = new JMenuItem("\t关于记事本");
		mainPageItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.mainPgae();
			}
			
		});

		aboutItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				con.about();
			}

		});

		helpMenu.add(mainPageItem);
		helpMenu.add(aboutItem);
		mb.add(helpMenu);
		
		//添加菜单栏
		this.setJMenuBar(mb);
		
		//剪切
		final JMenuItem cutItem2 = new JMenuItem();
		cutItem2.setText("剪切");
		cutItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.cut();				
			}
			
		});
				
		//复制
		//copy function for mouse right click menu
		final JMenuItem copyItem2 = new JMenuItem();
		copyItem2.setText("复制");
		copyItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.copy();
			}
			
		});
		
		//粘贴
		//paste function for mouse right click menu
		final JMenuItem pasteItem2 = new JMenuItem();
		pasteItem2.setText("粘贴");
		pasteItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.paste();
			}
			
		});
		
		//全选
		//select all function for mouse right click menu
		JMenuItem selectAllItem2 = new JMenuItem();
		selectAllItem2.setText("全选");
		selectAllItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.selectAll();
			}
			
		});
		
		//撤销
		//undo function for mouse right click menu
		final JMenuItem rollbackItem2 = new JMenuItem();
		rollbackItem2.setText("撤销");
		rollbackItem2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				popMenu.setVisible(false);
				con.rollback();
			}
			
		});
		
		popMenu.add(rollbackItem2);
		popMenu.add(cutItem2);
		popMenu.add(copyItem2);
		popMenu.add(pasteItem2);
		popMenu.add(selectAllItem2);
				
		body.setLineWrap(true);
		body.addKeyListener(new KeyAdapter(){
			//只要按下键盘，文件就是被修改过
			//if the keyboard has been pressed,sign it as the file has been edited
			@Override
			public void keyTyped(KeyEvent e) {
				con.isEdited = true;				
			}
	
		});
		body.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				int getCode =e.getButton();
				//鼠标右击事件
				//the code for mouse right click action
			if(getCode==3){				
					//如果文本区域没有内容，不能剪切和复制
					//if content has nothing,we can't cut or copy
					if(body.getText()==""){
						cutItem2.setEnabled(false);
						copyItem2.setEnabled(false);
					}else{
						cutItem2.setEnabled(true);
						copyItem2.setEnabled(true);
					}
					//如果剪贴板为空，不能粘贴
					//if the clipboard is empty,we can't paste
					if(clipboard.getContents(this)==null){
						pasteItem.setEnabled(false);
						pasteItem2.setEnabled(false);
					}else{
						
						pasteItem.setEnabled(true);
					}
					//显示位置在鼠标所在位置
					//show the popup menu near the mouse focus
					popMenu.setLocation(e.getXOnScreen(),e.getYOnScreen());
					popMenu.setVisible(true);
				}
				else{
					//隐藏
					//hide the popup menu
					popMenu.setVisible(false);
				}
			}
		});
		//添加撤销管理器
		//add undo manager to the frame
		body.getDocument().addUndoableEditListener(undoMgr);

		//设置滚动条一直存在，像windows 的notepad 一样
		sp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportView(body);

		this.add(sp);

		//get the system's clipboard
		clipboard = getToolkit().getSystemClipboard();//获取系统剪贴板
		setGlobalShortCuts();
	}
	/***
	 * 增加全局快捷键.
	 *
	 */
	protected void setGlobalShortCuts() {
		// Add global shortcuts
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		// 注册应用程序全局键盘事件, 所有的键盘事件都会被此事件监听器处理.
		toolkit.addAWTEventListener(new java.awt.event.AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				//这个是保存
				if (event.getClass() == KeyEvent.class) {
					KeyEvent kE = ((KeyEvent) event);
					// 处理按键事件 Ctrl+S
					if (kE.getKeyCode() == KeyEvent.VK_S
							&& kE.isControlDown()&&!kE.isAltDown()
							&& kE.getID() == KeyEvent.KEY_PRESSED) {
							con.saveFile();
					}
				}

				if (event.getClass() == KeyEvent.class) {
					KeyEvent kE = ((KeyEvent) event);
					// 处理按键事件 Ctrl+Z 撤销
					if (kE.getKeyCode() == KeyEvent.VK_Z
							&& kE.isControlDown()&&!kE.isAltDown()
							&& kE.getID() == KeyEvent.KEY_PRESSED) {
						con.rollback();
					}
				}

				if (event.getClass() == KeyEvent.class) {
					KeyEvent kE = ((KeyEvent) event);
					// 处理按键事件 Ctrl+N 新建文件
					if (kE.getKeyCode() == KeyEvent.VK_N
							&& kE.isControlDown()&&!kE.isAltDown()
							&& kE.getID() == KeyEvent.KEY_PRESSED) {
						con.createFile();
					}
				}

				if (event.getClass() == KeyEvent.class) {
					KeyEvent kE = ((KeyEvent) event);
					// 处理按键事件 Ctrl+O 打开文件
					if (kE.getKeyCode() == KeyEvent.VK_O
							&& kE.isControlDown()&&!kE.isAltDown()
							&& kE.getID() == KeyEvent.KEY_PRESSED) {
						con.openFile();
					}
				}

			}
		}, java.awt.AWTEvent.KEY_EVENT_MASK);

	}

	public MainFrame(){
		init();
	}

    public static void main(String[] args) {

        MainFrame mainFrame = new MainFrame();
        Controller controller = new Controller();

        mainFrame.setController(controller);
        controller.setMainFrame(mainFrame);

        mainFrame.setVisible(true);
    }
}