package notepad;

import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/*
 * Michael Neet 4/25/2018
 * Final Project CPS261
 */

abstract class NotepadMenu extends JMenu implements ActionListener {
	private JMenuItem[] itemArr;
	protected Notepad notepad;
	protected Font font;

	public NotepadMenu(String title, Notepad np, String[] itemNameArr, int[] accKeyArr, int[] accModArr) {
		super(title);
		notepad = np;
		font = new Font("Dialog", Font.PLAIN, 17);
		setFont(font);
		
		KeyStroke[] acceleratorArr = null;
		if (accKeyArr != null && accModArr != null) {
			acceleratorArr = new KeyStroke[accKeyArr.length];
			for (int i=0; i < acceleratorArr.length; i++) {
				if (accKeyArr[i] != 0)
					acceleratorArr[i] = KeyStroke.getKeyStroke(accKeyArr[i], accModArr[i]);
				else
					acceleratorArr[i] = null;
			}
		}
		
		itemArr = new JMenuItem[itemNameArr.length];
		for (int i=0; i < itemArr.length; i++) {
			// Null menu item name adds a separator
			if (itemNameArr[i] == null) 
				addSeparator();
			else {
				itemArr[i] = new JMenuItem(itemNameArr[i]);
				itemArr[i].setFont(font);
				if (acceleratorArr != null && acceleratorArr[i] != null)
					itemArr[i].setAccelerator(acceleratorArr[i]);
				add(itemArr[i]);
				itemArr[i].addActionListener(this);
			}
		}
	}
	
	// abstract public void actionPerformed(ActionEvent e) ;
}

class FileMenu extends NotepadMenu{
	//	Null string adds a separator
	private static String[] itemNameArr = {"New", "Open...", "Save", "Save As...", null, "Page Setup...", "Print...", null, "Exit"};
	private static int[] accKeyArr = {KeyEvent.VK_N, KeyEvent.VK_O, KeyEvent.VK_S, 0, 0, 0, KeyEvent.VK_P, 0, 0};
	private static int[] accModArr = {Event.CTRL_MASK, Event.CTRL_MASK, Event.CTRL_MASK, 0, 0, 0, Event.CTRL_MASK, 0, 0};	

	public FileMenu(Notepad notepad) {
		super("File", notepad, itemNameArr, accKeyArr, accModArr);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String itemPressed = e.getActionCommand();
		switch (itemPressed) {
			case "New":
				notepad.newFile();
				break;
			case "Open...":
				notepad.open();
				break;
			case "Save":
				notepad.save();
				break;
			case "Save As...":
				notepad.saveAs();
				break;
			case "Page Setup...":
				notepad.print();
				break;
			case "Print...":
				notepad.print();
				break;
			case "Exit":
				notepad.exit();
				break;
		}
	}
}

class EditMenu extends NotepadMenu {
	private static String[] itemNameArr = {"Undo", null, "Cut", "Copy", "Paste", "Delete", null,
			"Find...", "Find Next", "Replace...", "Go To...", null, "Select All", "Time/Date"};
	private static int[] accKeyArr = {KeyEvent.VK_Z, 0, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V, KeyEvent.VK_DELETE, 0, 
			KeyEvent.VK_F, KeyEvent.VK_F3, KeyEvent.VK_R, KeyEvent.VK_G, 0, KeyEvent.VK_A, KeyEvent.VK_F5};
	private static int[] accModArr = {Event.CTRL_MASK, 0, Event.CTRL_MASK, Event.CTRL_MASK, Event.CTRL_MASK, 0, 0, 
			Event.CTRL_MASK, 0, Event.CTRL_MASK, Event.CTRL_MASK, 0, Event.CTRL_MASK, 0};
	
	public EditMenu(Notepad notepad) {
		super("Edit", notepad, itemNameArr, accKeyArr, accModArr);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String itemPressed = e.getActionCommand();
		switch (itemPressed) {
			case "Undo":
				notepad.undo();
				break;
			case "Cut":
				notepad.cut();
				break;
			case "Copy":
				notepad.copy();
				break;
			case "Paste":
				notepad.paste();
				break;
			case "Delete":
				notepad.delete();
				break;
			case "Find...":
				notepad.findWindow();
				break;
			case "Find Next":
				notepad.findNext(notepad);
				break;
			case "Replace...":
				notepad.replaceWindow();
				break;
			case "Go To...":
				notepad.goToWindow();
				break;
			case "Select All":
				notepad.selectAll();
				break;
			case "Time/Date":
				notepad.timeAndDate();
				break;
		}	
	}
}

class FormatMenu extends NotepadMenu {
	private static String[] itemNameArr = {"Font..."};
	
	public FormatMenu(Notepad notepad) {
		super("Format", notepad, itemNameArr, null, null);
		JCheckBoxMenuItem wrap = new JCheckBoxMenuItem("Word Wrap");
		wrap.setFont(font);		
		wrap.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					notepad.setWordWrap(true);
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					notepad.setWordWrap(false);
			}		
		});
		add(wrap, 0);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String itemPressed = e.getActionCommand();
		switch (itemPressed) {
			case "Font...":
				notepad.fontWindow();
				break;
		}	
	}
}

class ViewMenu extends NotepadMenu {
	private static String[] itemNameArr = {"Status Bar"};
	
	public ViewMenu(Notepad notepad) {
		super("View", notepad, itemNameArr, null, null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String itemPressed = e.getActionCommand();
		switch (itemPressed) {
			case "Status Bar":
				break;
		}	
	}
}

class HelpMenu extends NotepadMenu {
	private static String[] itemNameArr = {"View Help", "About Notepad"};
			
	public HelpMenu(Notepad notepad) {
		super("Help", notepad, itemNameArr, null, null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String itemPressed = e.getActionCommand();
		switch (itemPressed) {
			case "View Help":
				break;
			case "About Notepad":
				break;
		}
	}
}