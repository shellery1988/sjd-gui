package com;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * 
 * @author liusongsong
 *
 */
public class JdkCompi implements ActionListener {
	JFrame frame = new JFrame("jdk decompiler edit by liusongsong");
	JTabbedPane tabPane = new JTabbedPane();
	Container con = new Container();
	JLabel label2 = new JLabel("选择文件");
	JTextField text2 = new JTextField();
	JButton button2 = new JButton("...");
	JFileChooser jfc = new JFileChooser();
	//JButton button3 = new JButton("编译");
	MyTextPane textField = new MyTextPane();
	JScrollPane jScrollPane = null;

	JdkCompi() {
		// 文件选择器的初始目录定为c盘
		jfc.setCurrentDirectory(new File("c://"));
		jfc.setFileFilter(new ClassFilter());
		frame.setResizable(false);
		// 设定窗口出现位置
		frame.setLocation(0, 0);
		// 设定窗口大小
		frame.setSize(1090, 710);
		// 设置布局
		frame.setContentPane(tabPane);
		label2.setBounds(10, 10, 70, 20);
		text2.setBounds(75, 10, 261, 20);
		button2.setBounds(356, 10, 50, 20);
		//button3.setBounds(424, 10, 60, 20);
		// 添加事件处理
		button2.addActionListener(this); 
		// 添加事件处理
		//button3.addActionListener(this); 
		con.add(label2);
		con.add(text2);
		con.add(button2);
		//con.add(button3);
		// 窗口可见
		frame.setVisible(true);
		// 使能关闭窗口，结束程序
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 添加布局1
		tabPane.add("反编译窗口", con);


		//con.add(textField);
		textField.setBounds(10, 10, 1064, 600);
		//textField.setColumns(10);
		//textField.setLineWrap(true);// 激活自动换行功能  
		//textField.setWrapStyleWord(true);// 激活断行不断字功能 
		textField.setBackground(Color.white);
		textField.setBounds(2, 33, 1033, 306);
		jScrollPane = new JScrollPane(textField);
		jScrollPane.setBounds(0, 40, 1080, 600);
		con.add(jScrollPane);


		//con.add(textField);
	}
	
	/**
	 * 事件监听的方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// 绑定到选择文件，先择文件事件
		String filename = "";
		BufferedReader br = null;
		if (e.getSource().equals(button2)) {
			// 设定只能选择到文件
			jfc.setFileSelectionMode(0);
			// 此句是打开文件选择器界面的触发语句
			// 此句是打开文件选择器界面的触发语句
			int state = jfc.showOpenDialog(null);
			// 撤销则返回
			if (state == 1) {
				return;
			} else {
				// f为选择到的文件
				File f = jfc.getSelectedFile();
				filename = f.getName().replace(".class", ".java");
				text2.setText(f.getAbsolutePath());
				text2.setName(filename);
				
				try {
					long d1 = new Date().getTime();
					Runtime rt = Runtime.getRuntime();
					System.out.println(getClassPath());
					String cmdStr = "cmd.exe /c java -jar "+getClassPath()+"\\procyon-decompiler-0.5.30.jar "+ text2.getText();
					//String cmdStr = "cmd.exe /c java -jar C:\\procyon-decompiler-0.5.30.jar "+ text2.getText();
					System.out.println(cmdStr);
					br = new BufferedReader(new InputStreamReader(rt.exec(cmdStr).getInputStream()));
					System.out.println("==============反编译耗时："+(new Date().getTime()-d1)+"ms");
					String line=null;
					
					long d3 = new Date().getTime();
					StringBuffer b=new StringBuffer();
					while ((line=br.readLine())!=null) {
						b.append(line+"\n");
						System.out.println(line);
					}
					String javaCode = b.toString();
					System.out.println("==============字符拼接耗时："+(new Date().getTime()-d3)+"ms");

					long d2 = new Date().getTime();
					textField.setTextByColor(StringUtils.unicodeToString(javaCode));
					System.out.println("==============语法高亮耗时："+(new Date().getTime()-d2)+"ms");

					/*FileWriter fw = new FileWriter(getClassPath()+"\\"+text2.getName()); 
					fw.write(b.toString());
					fw.close();*/
					//JOptionPane.showMessageDialog(null, b.toString(), "提示", 2);
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally{
					try {
						br.close();
					} catch (IOException e1) {

					}
				}
				
			}
		}
		/*if (e.getSource().equals(button3)) {
			// 弹出对话框可以改变里面的参数具体得靠大家自己去看，时间很短
			try {
				long d1 = new Date().getTime();
				Runtime rt = Runtime.getRuntime();
				//String cmdStr = "cmd.exe /c java -jar "+getClassPath()+"\\procyon-decompiler-0.5.30.jar "+ text2.getText();
				String cmdStr = "cmd.exe /c java -jar C:\\procyon-decompiler-0.5.30.jar "+ text2.getText();
				System.out.println(cmdStr);
				br = new BufferedReader(new InputStreamReader(rt.exec(cmdStr).getInputStream()));
				System.out.println("==============反编译耗时："+(new Date().getTime()-d1)+"ms");
				String line=null;
				
				long d3 = new Date().getTime();
				StringBuffer b=new StringBuffer();
				while ((line=br.readLine())!=null) {
					b.append(line+"\n");
				}
				String javaCode = b.toString();
				System.out.println("==============字符拼接耗时："+(new Date().getTime()-d3)+"ms");

				long d2 = new Date().getTime();
				textField.setTextByColor(StringUtils.unicodeToString(javaCode));
				System.out.println("==============语法高亮耗时："+(new Date().getTime()-d2)+"ms");

				FileWriter fw = new FileWriter(getClassPath()+"\\"+text2.getName()); 
				fw.write(b.toString());
				fw.close();
				//JOptionPane.showMessageDialog(null, b.toString(), "提示", 2);
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally{
				try {
					br.close();
				} catch (IOException e1) {

				}
			}
		}*/
	}

	@SuppressWarnings("unused")
	private String getClassPath(){
		return System.getProperty("user.dir") ;
	}

	public static void main(String[] args) {
		new JdkCompi();
	}
}
