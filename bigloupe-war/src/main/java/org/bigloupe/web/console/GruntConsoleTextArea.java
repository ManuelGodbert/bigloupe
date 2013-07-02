package org.bigloupe.web.console;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JTextArea;

import jline.ConsoleReader;
import jline.ConsoleReaderInputStream;
import jline.History;

import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.apache.pig.impl.PigContext;
import org.apache.pig.impl.util.LogUtils;
import org.apache.pig.impl.util.PropertiesUtil;
import org.apache.pig.scripting.Pig;
import org.apache.pig.tools.grunt.Grunt;
import org.apache.pig.tools.grunt.GruntParser;
import org.apache.pig.tools.grunt.PigCompletor;
import org.apache.pig.tools.grunt.PigCompletorAliases;
import org.apache.pig.tools.pigstats.PigStatsUtil;

public class GruntConsoleTextArea extends JTextArea {

	private static final long serialVersionUID = 3779250917000389174L;

	ConsoleReader reader;
	protected final In in;
	protected final Writer out;

	protected CmdLine cline;

	protected InputStream input = null;
	protected Character mask;

	protected StringBuilder pwd;

	protected HistoryFinder finder;

	PigCompletorAliases aliasesCompletor;
	PigCompletor completor;

	PigServer pig;
	
	GruntParser parser;

	public GruntConsoleTextArea() throws IOException {
		in = new In();
		out = new Out();
		input = new ByteArrayInputStream(getText().getBytes("UTF-8"));
		ExecType execType = ExecType.LOCAL;
		Properties properties = new Properties();
		PropertiesUtil.loadDefaultProperties(properties);
		PigContext pigContext = new PigContext(execType, properties);
		reader = new ConsoleReader(in, out, null, new SwingTerminal(this));

		reader.setDefaultPrompt("grunt> ");
		final String HISTORYFILE = ".pig_history";
		String historyFile = System.getProperty("user.home") + File.separator
				+ HISTORYFILE;
		reader.setHistory(new History(new File(historyFile)));

		pig = new PigServer(pigContext);
		aliasesCompletor = new PigCompletorAliases(pig);
		completor = new PigCompletor();
		reader.addCompletor(aliasesCompletor);
		reader.addCompletor(completor);
		setText("grunt> ");
		parser = new GruntParser(in);
		parser.setInteractive(false);
        parser.setParams(pig);
        parser.setConsoleReader(reader);

		Thread thread = new Thread() {
			public void run() {
		        while(true) {
		            try {
		                PigStatsUtil.getEmptyPigStats();
		                parser.setInteractive(false);
		                parser.parseStopOnError();
		                break;                            
		            } catch(Throwable t) {
		            	append(t.getMessage());
		                parser.ReInit(in);
		            }
		        }
			};
		};
		thread.start();


	}

	@Override
	protected void processComponentKeyEvent(KeyEvent e) {
		if (e.isControlDown()) {
			return;
		}
		int id = e.getID();
		if (id == KeyEvent.KEY_PRESSED) {
			int code = e.getKeyCode();
			if (handleControlChars(e, code)) {
				e.consume();
				return;
			}
			// handle passwords
			if (mask != null) {
				char c = e.getKeyChar();
				if (c >= 32 && c < 127) {
					append(mask.toString());
					pwd.append(c);
				}
				e.consume();
			}
		} else if (mask != null) {
			e.consume(); // do not show password
		}
	}

	/**
	 * Return true if should consume the event.
	 * 
	 * @param code
	 * @return
	 */
	protected boolean handleControlChars(KeyEvent event, int code) {
		switch (code) {
		case KeyEvent.VK_LEFT:
			if (event.isMetaDown()) {
				setCaretPosition(getCmdLine().getCmdStart());
				return true;
			}
			if (!getCmdLine().canMoveCaret(-1)) {
				beep();
				return true;
			}
			return false;
		case KeyEvent.VK_RIGHT:
			if (event.isMetaDown()) {
				setCaretPosition(getCmdLine().getEnd());
				return true;
			}
			if (!getCmdLine().canMoveCaret(1)) {
				beep();
				return true;
			}
			return false;
		case KeyEvent.VK_UP:
			if (event.isMetaDown()) {
				reader.getHistory().moveToFirstEntry();
				getCmdLine().setText(reader.getHistory().current());
				return true;
			}
			moveHistory(false);
			return true;
		case KeyEvent.VK_DOWN:
			if (event.isMetaDown()) {
				reader.getHistory().moveToLastEntry();
				getCmdLine().setText(reader.getHistory().current());
				return true;
			}
			moveHistory(true);
			return true;
		case KeyEvent.VK_ENTER:
			execute();
			return true;
		case KeyEvent.VK_BACK_SPACE:
			if (!getCmdLine().canMoveCaret(-1)) {
				beep();
				return true;
			}
			return false;
		case KeyEvent.VK_TAB:
			complete();
			return true;
		case KeyEvent.VK_K:
			if (event.isMetaDown()) {
				killLineAfter();
				return true;
			}
		case KeyEvent.VK_U:
			if (event.isMetaDown()) {
				killLineBefore();
				return true;
			}
		case KeyEvent.VK_L:
			if (event.isMetaDown()) {
				killLine();
				return true;
			}
		case KeyEvent.VK_X:
			if (event.isMetaDown()) {
				reset();
				in.put("\n");
				return true;
			}
		case KeyEvent.VK_I:
			if (event.isMetaDown()) {
				Font font = new Font(Font.MONOSPACED, Font.PLAIN, getFont()
						.getSize() + 1);
				setFont(font);
				return true;
			}
		case KeyEvent.VK_O:
			if (event.isMetaDown()) {
				Font font = new Font(Font.MONOSPACED, Font.PLAIN, getFont()
						.getSize() - 1);
				setFont(font);
				return true;
			}
		case KeyEvent.VK_EQUALS:
			if (event.isMetaDown()) {
				Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
				setFont(font);
				return true;
			}
		case KeyEvent.VK_S:
			if (event.isMetaDown()) {
				if (finder != null) {
					finder.setVisible(true);
					finder.getParent().validate();
					finder.requestFocus();
					return true;
				}
			}
		}
		return false;
	}

	public CmdLine getCmdLine() {
		if (cline == null) {
			cline = new CmdLine(this);
		}
		return cline;
	}

	public History getHistory() {
		return reader.getHistory();
	}

	protected void moveHistory(boolean next) {
		if (next && !reader.getHistory().next()) {
			beep();
		} else if (!next && !reader.getHistory().previous()) {
			beep();
		}

		String text = reader.getHistory().current();
		getCmdLine().setText(text);

	}

	/**
	 * Completion
	 */
	public void complete() {
		try {
			List<String> candidates = new ArrayList<String>();
			completor.complete(getCmdLine().getText(), getCmdLine()
					.getLocalCaretPosition(), candidates);
			aliasesCompletor.complete(getCmdLine().getText(), getCmdLine()
					.getLocalCaretPosition(), candidates);
			if (candidates.size() > 0) {
				int i = 0;
				append("\n");
				if (candidates.size() == 1)
					getCmdLine().setCompletionWord(candidates.get(0));
				else {
					for (String candidate : candidates) {
						append(candidate);
						i++;
						if (i < candidates.size())
							append(" ");
					}
					append("\ngrunt> ");
					append(getCmdLine().getText().trim());
				}
			} else
				beep();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			cline = null;
		}
	}

	public void reset() {
		try {
			setText("grunt> ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void killLine() {
		getCmdLine().setText("grunt> ");
	}

	public void killLineBefore() {
		int p = getCmdLine().getLocalCaretPosition();
		getCmdLine().setText(getCmdLine().getText().substring(p));
	}

	public void killLineAfter() {
		int p = getCmdLine().getLocalCaretPosition();
		getCmdLine().setText(getCmdLine().getText().substring(0, p));
	}

	public void execute() {
		try {
			String cmd = getCmdLine().getText().trim();
			append("\n");
			setCaretPosition(getDocument().getLength());
			if (pwd != null) {
				cline = null;
				in.put(pwd.toString() + "\n");
				pwd = null;
				return;
			}
			if (cmd.length() > 0 && reader.getUseHistory()) {
				reader.getHistory().addToHistory(cmd);
				reader.getHistory().moveToEnd();
			}
			cline = null;
			in.put(cmd + "\n");
			out.append(cmd+ "\n");

			append("grunt> ");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void beep() {

		// visualBell();

		audibleBell();
	}

	public void audibleBell() {
		Toolkit.getDefaultToolkit().beep();
	}

	public void visualBell() {
		setBackground(Color.GREEN);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setBackground(Color.BLACK);
	}

	public void setMask(Character mask) {
		if (mask != null) {
			pwd = new StringBuilder();
		} else {
			pwd = null;
		}
		this.mask = mask;
	}

	class In extends InputStream {
		protected StringBuilder buf = new StringBuilder();

		public synchronized void put(int key) {
			buf.append((char) key);
			notify();
		}

		public synchronized void put(String text) {
			buf.append(text);
			notify();
		}

		@Override
		public synchronized int read() throws IOException {
			if (buf.length() > 0) {
				char c = buf.charAt(0);
				buf.deleteCharAt(0);
				return c;
			}
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (buf.length() == 0) {
				throw new IllegalStateException(
						"invalid state for console input stream");
			}
			char c = buf.charAt(0);
			buf.deleteCharAt(0);
			return c;
		}
	}

	class Out extends Writer {

		protected void _write(char[] cbuf, int off, int len) throws IOException {
			_write(new String(cbuf, off, len));
		}

		protected void _write(String str) throws IOException {
			GruntConsoleTextArea.this.append(str);
			setCaretPosition(getDocument().getLength());
		}

		protected boolean handleOutputChar(char c) {
			try {
				if (c == 7) { // beep
					beep();
				} else if (c < 32 && c != '\n' && c != '\t') {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			if (len == 1) {
				char c = cbuf[off];
				if (!handleOutputChar(c)) {
					_write(cbuf, off, len);
				}
			} else {
				StringBuilder buf = new StringBuilder();
				for (int i = off, end = off + len; i < end; i++) {
					char c = cbuf[i];
					if (!handleOutputChar(c)) {
						buf.append(c);
					}
				}
				if (buf.length() > 0) {
					_write(buf.toString());
				}
			}
		}

		@Override
		public void flush() throws IOException {
		}

		@Override
		public void close() throws IOException {
			flush();
		}
	}

}
