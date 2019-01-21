package com;

import javax.swing.JTextPane;
import javax.swing.text.*;
import javax.swing.text.rtf.*;
import java.awt.event.*;
import java.util.StringTokenizer;
import java.awt.*;

/**
 * 
 * @author liusongsong
 *
 */
public class MyTextPane extends JTextPane {
	/**
	 *
	 */
	private static final long serialVersionUID = -66377652770879651L;
	protected StyleContext meContext;
	protected DefaultStyledDocument meDoc;
	private MutableAttributeSet keyAttr, normalAttr;
	private MutableAttributeSet bracketAttr;
	private MutableAttributeSet inputAttributes = new RTFEditorKit()
			.getInputAttributes();
	
	/**
	 * 所有关键字
	 */
	private final static String[] KEYS = new String[] { "abstract","assert","boolean","break",
			"byte","case","catch","char","class","const","continue","default","do","double","else",
			"enum","extends","final","finally","float","for","goto","if","implements","import",
			"instanceof","int","interface","long","native","new","package","private","protected",
			"public","return","strictfp","short","static","super","switch","synchronized","this",
			"throw","throws","transient","try","void","volatile","while" };
	
	/**
	 * 所与排除字符集
	 */
	private final static char[] CHARACTERS = new char[] { '(', ')', ',', ';',
			':', '\t', '\n', '+', '-', '*', '/' };
	/**
	 * 初始化，包括关键字颜色，和非关键字颜色
	 */
	public MyTextPane() {
		super();
		meContext = new StyleContext();
		meDoc = new DefaultStyledDocument(meContext);
		this.setDocument(meDoc);
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				dealSingleRow();
			}
		});
		// 义关键字显示属性
		keyAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(keyAttr, new Color(127,0,116));
		StyleConstants.setBold(keyAttr, true);
		// 义一般文本显示属性
		normalAttr = new SimpleAttributeSet();
		// StyleConstants.setFontFamily(normalAttr, "serif");
		StyleConstants.setBold(normalAttr, false);
		StyleConstants.setForeground(normalAttr, Color.black);
		bracketAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(bracketAttr, Color.RED);
		StyleConstants.setFontFamily(bracketAttr, "Courier New");
		StyleConstants.setBold(bracketAttr, true);
		syntaxParse();
	}
	
	public void setTextByColor(String text){
		this.setText(text);
		syntaxParse();
	}
	
	/**
	 * 判断字符是不是在排除字符行列
	 * @param ch
	 * @return
	 */
	private boolean isCharacter(char ch) {
		for (int i = 0; i < CHARACTERS.length; i++) {
			if (ch == CHARACTERS[i]) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 设置关键字颜色
	 * @param key
	 * @param start
	 * @param length
	 * @return
	 */
	private int setKeyColor(String key, int start, int length) {
		for (int i = 0; i < KEYS.length; i++) {
			int liIndex = key.indexOf(KEYS[i]);
			if (liIndex < 0) {
				continue;
			}
			int liLength = liIndex + KEYS[i].length();
			if (liLength == key.length()) {
				//处理单独一个关键字的情况，例如：if else 等
				if (liIndex == 0) {
					meDoc.setCharacterAttributes(start, KEYS[i].length(),
							keyAttr, false);
				} else {//处理关键字前面还有字符的情况，例如：)if ;else 等
					char chTemp = key.charAt(liIndex - 1);
					if (isCharacter(chTemp)) {
						meDoc.setCharacterAttributes(start + liIndex,
								KEYS[i].length(), keyAttr, false);
					}
				}
			} else {
				//处理关键字后面还有字符的情况，例如：if(  end;等
				if (liIndex == 0) {
					char chTemp = key.charAt(KEYS[i].length());
					if (isCharacter(chTemp)) {
						meDoc.setCharacterAttributes(start, KEYS[i].length(),
								keyAttr, false);
					}
				} else {//处理关键字前面和后面都有字符的情况，例如：)if( 等
					char chTemp = key.charAt(liIndex - 1);
					char chTemp2 = key.charAt(liLength);
					if (isCharacter(chTemp) && isCharacter(chTemp2)) {
						meDoc.setCharacterAttributes(start + liIndex,
								KEYS[i].length(), keyAttr, false);
					}
				}
			}
		}
		return length + 1;
	}
	/**
	 * 处理一行的数据
	 * @param start
	 * @param _end
	 */
	private void dealText(int start, int end) {
		String text = "";
		try {
			text = meDoc.getText(start, end - start).toUpperCase();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (text == null || text.equals("")) {
			return;
		}
		int xStart = 0;
		// 析关键字---
		meDoc.setCharacterAttributes(start, text.length(), normalAttr, false);
		MyStringTokenizer st = new MyStringTokenizer(text);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s == null){
				return;
			}
			xStart = st.getCurrPosition();
			setKeyColor(s.toLowerCase(), start + xStart, s.length());
		}
		inputAttributes.addAttributes(normalAttr);
	}
	/**
	 * 在进行文本修改的时候
	 * 获得光标所在行，只对该行进行处理
	 */
	private void dealSingleRow() {
		Element root = meDoc.getDefaultRootElement();
		// 光标当前行
		int cursorPos = this.getCaretPosition();
		// 当前行
		int line = root.getElementIndex(cursorPos);
		Element para = root.getElement(line);
		int start = para.getStartOffset();
		// 除\r字符
		int end = para.getEndOffset() - 1;
		dealText(start, end);
	}
	/**
	 * 在初始化面板的时候调用该方法，
	 * 查找整个篇幅的关键字
	 */
	public void syntaxParse() {
		Element root = meDoc.getDefaultRootElement();
		int liCount = root.getElementCount();
		for (int i = 0; i < liCount; i++) {
			Element para = root.getElement(i);
			int start = para.getStartOffset();
			// 除\r字符
			int end = para.getEndOffset() - 1;
			dealText(start, end);
		}
	}
	
	class MyStringTokenizer extends StringTokenizer {
		String sval = " ";
		String oldStr, str;
		int meCurrPosition = 0, meBeginPosition = 0;
		MyStringTokenizer(String str) {
			super(str, " ");
			this.oldStr = str;
			this.str = str;
		}
		@Override
		public String nextToken() {
			try {
				String s = super.nextToken();
				int pos = -1;
				if (oldStr.equals(s)) {
					return s;
				}
				pos = str.indexOf(s + sval);
				if (pos == -1) {
					pos = str.indexOf(sval + s);
					if (pos == -1){
						return null;
					} else {
						pos += 1;
					}
				}
				int xBegin = pos + s.length();
				str = str.substring(xBegin);
				meCurrPosition = meBeginPosition + pos;
				meBeginPosition = meBeginPosition + xBegin;
				return s;
			} catch (java.util.NoSuchElementException ex) {
				ex.printStackTrace();
				return null;
			}
		}
		/**
		 * 返回token在字符串中的位置
		 * @return
		 */
		public int getCurrPosition() {
			return meCurrPosition;
		}
	}
}

