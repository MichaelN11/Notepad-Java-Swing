package notepad;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/*
 * Michael Neet 4/25/2018
 * Final Project CPS261
 */

class FindDialog extends JDialog {
	protected static String findText= "";
	private Notepad notepad;
	protected JTextField findField;
	protected JButton findButton;
	protected JButton cancelButton;
	protected Boolean matchCase = false;
	private Boolean findDownDirection = true;
	private JRadioButton downButton;
	private JRadioButton upButton;
	
	// Constructs actual "find" window
	public FindDialog(Notepad np, int x, int y, String findText) { 
		this(np, "Find", x, y, 400, 150, findText);
		ButtonGroup group = new ButtonGroup();
		downButton = new JRadioButton("Down");
		group.add(downButton);
		downButton.setSelected(true);
		downButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					findDownDirection = true;
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					findDownDirection = false;
			}		
		});
		upButton = new JRadioButton("Up");
		group.add(upButton);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		add(upButton, c);
		setVisible(true);
		
		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 1;
		add(downButton, c);
		setVisible(true);
	}
	
	public FindDialog(Notepad np, String title, int x, int y, int width, int height, String findText) {
		super(np, title);
		setSize(width, height);
		setCenteredLocation(x, y);
		setResizable(false);
		notepad = np;
		FindAction action = new FindAction();
		findField = new JTextField();
		findField.setPreferredSize(new Dimension(200, 22));
		findField.setText(findText);
		findField.addActionListener(action);
		findButton = new JButton("Find Next");
		findButton.addActionListener(action);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(action);
		JCheckBox matchCaseBox = new JCheckBox("Match Case");
		matchCaseBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					matchCase = true;
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					matchCase = false;
			}
			
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		add(new JLabel("Find what: "), c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		add(findField, c);
		
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 1;
		add(findButton, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		add(matchCaseBox, c);
		
		c.gridx = 3;
		c.gridy = 3;
		c.gridwidth = 1;
		add(cancelButton, c);
		
		findField.requestFocus();
	}
	
	public void updateDialog(String findText, int x, int y) {
		setCenteredLocation(x, y);
		findField.setText(findText);
		findField.requestFocus();
		setVisible(true);
	}
	
	public void setCenteredLocation(int x, int y) {
		int newX = x - (getWidth()/2);
		int newY = y - (getHeight()/2);
		setLocation(newX, newY);
	}
	
	private class FindAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == findButton || source == findField) {
				notepad.find(FindDialog.this, findField.getText(), matchCase, findDownDirection);
			}
			else if (source == cancelButton) {
				setVisible(false);
			}		
		}
		
	}

}

class ReplaceDialog extends FindDialog {
	private JTextField replaceField;
	private JButton replaceButton;
	private JButton replaceAllButton;
	private Notepad notepad;
	
	public ReplaceDialog(Notepad np, int x, int y, String findText) {
		super(np, "Replace", x, y, 400, 170, findText);
		notepad = np;
		ReplaceAction action = new ReplaceAction();
		replaceField = new JTextField();
		replaceField.setPreferredSize(new Dimension(200, 22));
		replaceField.addActionListener(action);
		replaceButton = new JButton("Replace");
		replaceButton.addActionListener(action);
		replaceAllButton = new JButton("Replace All");
		replaceAllButton.addActionListener(action);
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		add(new JLabel("Replace with: "), c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		add(replaceField, c);
		
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		add(replaceButton, c);
		
		c.gridx = 3;
		c.gridy = 2;
		c.gridwidth = 1;
		add(replaceAllButton, c);		
		
		setVisible(true);
	}
	
	private class ReplaceAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == replaceButton || source == replaceField) {
				notepad.replace(findField.getText(), replaceField.getText(), matchCase);
			}
			else if (source == replaceAllButton) {
				notepad.replaceAll(findField.getText(), replaceField.getText(), matchCase);
			}
			else if (source == cancelButton) {
				setVisible(false);
			}		
		}
		
	}
}

class GoToDialog extends JDialog {
	private Notepad notepad;
	private JTextField goToField;
	private JButton goToButton;
	private JButton cancelButton;

	public GoToDialog(Notepad np, int x, int y) {
		super(np, "Go To Line");
		setSize(250, 140);
		setResizable(false);
		setCenteredLocation(x, y);
		notepad = np;
		setLayout(new GridBagLayout());
		goToAction action = new goToAction();
		goToField = new JTextField();
		goToField.setPreferredSize(new Dimension(200, 22));
		goToField.addActionListener(action);
		goToButton = new JButton("Go To");
		goToButton.addActionListener(action);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(action);

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		add(new JLabel("Line number: "), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		add(goToField, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		add(goToButton, c);
		
		c.gridx = 2;
		c.gridy = 2;
		c.gridwidth = 1;
		add(cancelButton, c);
		
		goToField.requestFocus();
		
		setVisible(true);
	}
	
	private class goToAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == goToButton || source == goToField) {
				if (notepad.goTo(goToField.getText()))
					setVisible(false);
			}
			if (source == cancelButton) {
				setVisible(false);
			}		
		}
		
	}
	
	public void setCenteredLocation(int x, int y) {
		int newX = x - (getWidth()/2);
		int newY = y - (getHeight()/2);
		setLocation(newX, newY);
	}

}
