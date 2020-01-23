package notepad;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/*
 * Michael Neet 4/25/2018
 * Final Project CPS261
 */

// A bit unfinished, I wanted to make a list of all of the fonts but ran out of time. Instead you have to enter the font names into the text field.

public class FontDialog extends JDialog {
	public final String[] FONT_STYLES = {"bold", "bold italic", "italic", "plain"};
	public final int[] FONT_SIZES = {};	
	private Notepad notepad;
	private JTextField nameField;
	private JTextField styleField;
	private JTextField sizeField;
	private JButton okButton;
	private JButton cancelButton;
	private JLabel sampleLabel;
	private Font font;
	private String fontName;
	private int fontStyle;
	private int fontSize;
	
	public FontDialog(Notepad np, int x, int y) {
		super(np, "Font");
		setSize(430, 470);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setCenteredLocation(x, y);
		notepad = np;
		
		font = notepad.getFont();
		fontName = font.getFontName();
		fontStyle = font.getStyle();
		fontSize = font.getSize();
		
		setLayout(new GridBagLayout());
		FontAction action = new FontAction();
		FontTextUpdate textUpdate = new FontTextUpdate();
		nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(150, 22));
		nameField.getDocument().addDocumentListener(textUpdate);
		styleField= new JTextField();
		styleField.setPreferredSize(new Dimension(150, 22));
		styleField.getDocument().addDocumentListener(textUpdate);
		sizeField = new JTextField();
		sizeField.setPreferredSize(new Dimension(80, 22));
		sizeField.getDocument().addDocumentListener(textUpdate);
		okButton = new JButton("OK");
		okButton.addActionListener(action);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(action);
		sampleLabel = new JLabel("AaBbYyZz");
		sampleLabel.setFont(font);
		JPanel samplePanel = new JPanel();
		samplePanel.add(sampleLabel);
		samplePanel.setLayout(new GridLayout());
		samplePanel.setPreferredSize(new Dimension(1, 1));
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		add(new JLabel("Font: "), c);

		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		add(new JLabel("Font style: "), c);
		
		c.gridx = 4;
		c.gridy = 0;
		c.gridwidth = 1;
		add(new JLabel("Size: "), c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		add(nameField, c);
		
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 2;
		add(styleField, c);
		
		c.gridx = 4;
		c.gridy = 1;
		c.gridwidth = 1;
		add(sizeField, c);
		
		c.gridx = 2;
		c.gridy = 5;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		add(samplePanel, c);
		
		c.gridx = 3;
		c.gridy = 6;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		add(okButton, c);
		
		c.gridx = 4;
		c.gridy = 6;
		c.gridwidth = 1;
		add(cancelButton, c);
		
		setVisible(true);
		
	}
	
	public String[] getSystemFonts() {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return e.getAvailableFontFamilyNames();
	}
	
	public void setCenteredLocation(int x, int y) {
		int newX = x - (getWidth()/2);
		int newY = y - (getHeight()/2);
		setLocation(newX, newY);
	}
	
	private void checkText(Document doc) {
		if (nameField.getDocument() == doc) {
			String[] options = getSystemFonts();
			int index = indexOfNoCase(options, nameField.getText());
			if (index > 0) {
				String name = options[index];
				if (!name.equals(fontName)) {
					fontName = name;
					updateFont();
				}
			}
		}
		else if (styleField.getDocument() == doc) {
			int style;
			switch(styleField.getText().toLowerCase()) {
			case "plain":
				style = Font.PLAIN;
				break;
			case "bold":
				style = Font.BOLD;
				break;
			case "italic":
				style = Font.ITALIC;
				break;
			case "bold italic":
			case "italic bold":
				style = Font.BOLD + Font.ITALIC;
				break;
			default:
				return;
			}
			if (style != fontStyle) {
				fontStyle = style;
				updateFont();
			}		
		}
		else if (sizeField.getDocument() == doc) {
			int size = 0;
			try {
				size = Integer.parseInt(sizeField.getText());
			}
			catch (NumberFormatException e) {
			}
			if (size >= 8 && size <= 72 && size != fontSize) {
				fontSize = size;
				updateFont();
			}
		}
	}
	
	private void updateFont() {
		font = new Font(fontName, fontStyle, fontSize);
		sampleLabel.setFont(font);
	}
	
	private int indexOfNoCase(String[] arr, String s) {
		s = s.toLowerCase();
		for (int i = 0; i < arr.length; i++) 
			if (s.equals(arr[i].toLowerCase()))
				return i;
		return -1;
	}
	
	private class FontAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == okButton) 
				notepad.changeFont(font);
			dispose();
		}
		
	}
	
	private class FontTextUpdate implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {
			checkText(e.getDocument());			
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			checkText(e.getDocument());		
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			checkText(e.getDocument());	
		}
		
	}

}
