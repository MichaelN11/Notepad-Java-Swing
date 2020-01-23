package notepad;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;

/*
 * Michael Neet 4/25/2018
 * Final Project CPS261
 */

public class Notepad extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final String lineSeparator = System.lineSeparator();
	private JTextArea textArea;
	private File sourceFile = null;
	private JFileChooser fileChooser;
	private String lastSavedText = "";
	private UndoManager undo = new UndoManager();
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private GoToDialog goToDialog;
	private String findText;
	private boolean findMatchCase = true;
	private boolean findDownDirection = true;
	private Font font;
	
	public Notepad() {
		setNotepadTitle();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setSize(800, 600);
		setupFrame();
		setupMenuBar();
		// Used for opening and saving files
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Text Documents (*.txt)", "txt"));
		// Checks for unsaved files when closing
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
			
		});
		setVisible(true);
	}
	
	private void setupFrame() {
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		font = new Font("Arial", Font.PLAIN, 19);
		textArea.setFont(font);
		textArea.setMargin(new Insets(5, 5, 5, 5));
		textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undo.addEdit(e.getEdit());
			}		
		});
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);	
	}
	
	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(new FileMenu(this));
		menuBar.add(new EditMenu(this));
		menuBar.add(new FormatMenu(this));
		menuBar.add(new ViewMenu(this));
		menuBar.add(new HelpMenu(this));
		
		setJMenuBar(menuBar);
	}
	
	private void setNotepadTitle() {
		String title;
		if (sourceFile == null)
			title = "Untitled";
		else
			title = sourceFile.getName();
		setTitle(title + " - Java Notepad");
	}
	
	public boolean newFile() {
		if (!saveCheck())
			return false;
		textArea.setText("");
		lastSavedText = "";
		sourceFile = null;
		setNotepadTitle();
		return true;
	}
	
	public boolean open() {
		File openFile;
		boolean success = true;
		Scanner fileScanner = null;
		if (!saveCheck())
			return false;
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			openFile = fileChooser.getSelectedFile();
		else
			return false;
		StringBuilder fileContents = new StringBuilder((int)openFile.length());
		try {
			fileScanner = new Scanner(openFile);
			if (fileScanner.hasNextLine())
				fileContents.append(fileScanner.nextLine());
			while (fileScanner.hasNextLine())
				fileContents.append(lineSeparator + fileScanner.nextLine());
			String newText = fileContents.toString();
			textArea.setText(newText);
			textArea.setCaretPosition(0);
			lastSavedText = newText;
			sourceFile = openFile;
			setNotepadTitle();
		}
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, openFile.getName() + lineSeparator + "File not found." + lineSeparator + 
					"Check the file name and try again.", "Open", JOptionPane.WARNING_MESSAGE);
			success = false;
		}
		finally {
			if (fileScanner != null)
				fileScanner.close();
		}
		return success;
	}
	
	public boolean save(File saveFile) {
		boolean success = true;
		PrintStream printStream = null;
		try {
			printStream = new PrintStream(saveFile);
			String currentText = textArea.getText();
			printStream.print(currentText);
			lastSavedText = currentText;
			sourceFile = saveFile;
			setNotepadTitle();
		}
		catch (FileNotFoundException e) {
			System.out.println(e);
			success = false;
		}
		finally {
			if (printStream != null)
				printStream.close();
		}	
		return success;
	}
	
	public boolean save() {
		if (sourceFile != null)
			return save(sourceFile);
		else
			return saveAs();
	}
	
	public boolean saveAs() {
		File saveFile;
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) 
			saveFile = fixFileExtension(fileChooser.getSelectedFile());
		else
			return false;
		if (saveFile.exists()) {
			int overwrite = JOptionPane.showConfirmDialog(this, saveFile.getName() + " already exists." + lineSeparator
					+ "Do you want to replace it?", "Confirm Save As", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (overwrite == JOptionPane.NO_OPTION)
				return false;
		}
		return save(saveFile);			
	}
	
	public boolean exit() {
		boolean success = true;
		if (saveCheck()) {
			dispose();
			System.exit(0);
		}
		else
			success = false;
		return success;
	}
	
	public void copy() {
		textArea.copy();
	}
	
	public void cut() {
		textArea.cut();
	}
	
	public void paste() {
		textArea.paste();
	}
	
	public void delete() {
		textArea.replaceSelection("");
	}
	
	public void selectAll() {
		textArea.selectAll();
	}
	
	public void timeAndDate() {
		Date currDate = new Date();
		textArea.replaceSelection(currDate.toString());
	}
	
	public void setWordWrap(Boolean wordWrap) {
		textArea.setLineWrap(wordWrap);
	}
	
	public void undo() {
		if (undo.canUndo()) 
			undo.undo();
	}
	
	public boolean find(Window parent, String s, Boolean matchCase, Boolean direction) {
		findText = s;
		findMatchCase = matchCase;
		findDownDirection = direction;
		return findNext(parent);
	}
	
	public boolean findNext(Window parent) {
		if (findText == null) {
			findWindow();
			return false;
		}
		String document = textArea.getText();
		String text = findText;
		if (!findMatchCase) {
			document = document.toLowerCase();
			text = text.toLowerCase();
		}
		int index;
		if (findDownDirection)
			index = document.indexOf(text, textArea.getCaretPosition());
		else
			index = document.lastIndexOf(text, textArea.getCaretPosition() - (text.length() + 1));
		if (index >= 0) {
			// Selects found string
			textArea.setCaretPosition(index);
			textArea.moveCaretPosition(index + text.length());
			return true;
		}
		else {
			JOptionPane.showMessageDialog(parent, "Cannot find \"" + findText + "\"", "Java Notepad", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}
	
	public boolean replace(String find, String replace, Boolean matchCase) {
		boolean success = false;
		String selectedText = textArea.getSelectedText();
		if (selectedText != null) {
			String f = find;
			if (!matchCase) {
				selectedText = selectedText.toLowerCase();
				f = f.toLowerCase();
			}
			if (selectedText.equals(f)) {
				textArea.replaceSelection(replace);
				success = true;
			}
		}
		find(replaceDialog, find, matchCase, true);
		return success;
	}
	
	public boolean replaceAll(String find, String replace, Boolean matchCase) {
		boolean success = false;
		String document = textArea.getText();
		if (find.length() == 0)
			return false;
		if (!matchCase) {
			find = find.toLowerCase();
			document = document.toLowerCase();
		}
		int index = document.indexOf(find);
		int indexTextArea = index;
		int indexOffset = 0;		// Offset between untouched 'document' string index and the textArea text that's being changed, used to replace without matching case
		while (index >= 0) {
			textArea.replaceRange(replace, indexTextArea, indexTextArea + find.length());
			index = document.indexOf(find, index + find.length());
			indexOffset += replace.length() - find.length();	// Every time a word is replaced the offset grows by the difference in length between the words
			indexTextArea = index + indexOffset;
			if (!success)
				success = true;
		}

		return success;
	}
	
	public boolean goTo(String lineStr) {
		int line = 0;
		try {
			line = Integer.parseInt(lineStr);
		} 
		catch (NumberFormatException e) {
			line = -1;
		}	
		if (line >= 0) {
			int index = 0;
			int lastIndex = 0;
			String docText = textArea.getText();
			while (index >= 0 && line > 0) {
				lastIndex = index;
				index = docText.indexOf(lineSeparator, index + 1);
				line -= 1;
			}
			textArea.setCaretPosition(lastIndex + 2);
			return true;
		}		
		else {
			JOptionPane.showMessageDialog(goToDialog, "Line number must be an integer greater than 0.", "Go To Line", JOptionPane.WARNING_MESSAGE);
			return false;
		}

	}
	
	public boolean print() {
		try {
			return textArea.print();
		} catch (PrinterException e) {
			JOptionPane.showMessageDialog(this, "The printer encountered an error.", "Print Error", JOptionPane.ERROR_MESSAGE);
			System.out.println(e);
			return false;
		}
	}
	
	public void findWindow() {
		int x = getX() + (this.getWidth() / 2);
		int y = getY() + (this.getHeight() / 2);
		if (findDialog == null) 
			findDialog = new FindDialog(this, x, y, findText);
		else
			findDialog.updateDialog(findText, x, y);
	}
	
	public void replaceWindow() {
		int x = getX() + (this.getWidth() / 2);
		int y = getY() + (this.getHeight() / 2);
		if (replaceDialog == null)
			replaceDialog = new ReplaceDialog(this, x, y, findText);
		else
			replaceDialog.updateDialog(findText, x, y);
	}
	
	public void goToWindow() {
		int x = getX() + (this.getWidth() / 2);
		int y = getY() + (this.getHeight() / 2);
		if (goToDialog == null) 
			goToDialog = new GoToDialog(this, x, y);
		else {
			goToDialog.setCenteredLocation(x, y);
			goToDialog.setVisible(true);
		}
	}
	
	public void fontWindow() {
		int x = getX() + (this.getWidth() / 2);
		int y = getY() + (this.getHeight() / 2);
		new FontDialog(this, x, y);
	}
	
	public Font getFont() {
		return font;
	}
	
	public void changeFont(Font f) {
		font = f;
		textArea.setFont(f);
	}
	
	private boolean saveCheck() {
		String title;
		boolean proceed = true;		
		if (textArea.getText().equals(lastSavedText))
			return true;
		if (sourceFile != null)
			title = sourceFile.getAbsolutePath();
		else
			title = "Untitled";
		Object[] dialogOptions = {"Save", "Don't Save", "Cancel"};
		int choice = JOptionPane.showOptionDialog(this, "Do you want to save changes to" + lineSeparator 
				+ title + "?", "Java Notepad", JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.WARNING_MESSAGE, null, dialogOptions, dialogOptions[0]);
		if (choice == JOptionPane.YES_OPTION) { 
			if (!save())
				// Chose to save but didn't save file
				proceed = false;
		}
		else if (choice == JOptionPane.CANCEL_OPTION)
				proceed = false;
		return proceed;
	}
	
	private File fixFileExtension(File oldFile) {
		File newFile = oldFile;
		String path = oldFile.getAbsolutePath();
		if (!path.toUpperCase().endsWith(".TXT"))
			newFile = new File(path + ".txt");		
		return newFile;
	}

}



