package mvc;

import helpers.Helpers;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class View extends JFrame {
	
	public enum Size {SMALL, MEDIUM, LARGE};

	private static final long serialVersionUID = 1L;
	
	public static final String REMOVE_NAME = "remove";
	public static final String ADD_NAME = "add";
	public static final String OPEN_NAME = "open";
	public static final String SAVE_NAME = "save";
	public static final String QUIT_NAME = "quit";
	
	private int textFieldWidth;
	private int textAreaWidth;
	private int textAreaHeight;
	
	private final JMenuItem saveButton;
	
	private final JList<String> list;
	private final JScrollPane scrollPane;
	private final DefaultListModel<String> commandModel;
	
	private final JButton remove;
	private final JButton add;
	
	private final JTextField nameField;
	
	private final Controller controller;
	
	private final JFileChooser fileChooser;
	
	private final MenuFactroy menuFactroy;
	
	public View(Controller controller) {
		this.controller = controller;
		this.setResizable(false);
		this.remove = new JButton(View.REMOVE_NAME);
		this.remove.setActionCommand(View.REMOVE_NAME);
		this.remove.addActionListener(this.controller);
		this.add = new JButton(View.ADD_NAME);
		this.add.setActionCommand(View.ADD_NAME);
		this.add.addActionListener(this.controller);
		this.commandModel = new DefaultListModel<String>();
		this.list = new JList<String>(this.commandModel);
		this.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.list.addListSelectionListener(this.controller);
		this.scrollPane = new JScrollPane(this.list);
		this.commandModel.addListDataListener(this.controller);
		this.nameField = new JTextField();
		
		this.menuFactroy = new MenuFactroy();
		
		menuFactroy.addMenu("File");
		JMenuItem open = menuFactroy.createMenuItem(View.OPEN_NAME, this.controller);
		this.saveButton = menuFactroy.createMenuItem(View.SAVE_NAME, this.controller);
		this.saveButton.setEnabled(false);
		JMenuItem quit = menuFactroy.createMenuItem(View.QUIT_NAME, this.controller);
		menuFactroy.addMenuItem("File", open);
		menuFactroy.addMenuItem("File", saveButton);
		menuFactroy.addSeparator("File");
		menuFactroy.addMenuItem("File", quit);
		
		menuFactroy.addMenu("Options");
		menuFactroy.addSubMenu("Options", "UI size");
		
		String[] uiSizeNames = new String[] {"Small", "Medium", "Large"};
		ButtonGroup uiSizes= menuFactroy.createRadioButtonGroup(uiSizeNames, "uisize", uiSizeNames[0], this.controller);
		menuFactroy.addButtonGroup("UI size", uiSizes);
		
		// TODO set initial directory
		this.fileChooser = new JFileChooser();
		FileNameExtensionFilter iniFilter = new FileNameExtensionFilter("texmaker .ini files", "INI", "ini");
		FileNameExtensionFilter cwsFilter = new FileNameExtensionFilter("texstudio .cws files", "CWS", "cws");
		this.fileChooser.removeChoosableFileFilter(this.fileChooser.getAcceptAllFileFilter());
		this.fileChooser.addChoosableFileFilter(cwsFilter);
		this.fileChooser.addChoosableFileFilter(iniFilter);
		this.fileChooser.addChoosableFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "all files";
			}			
			@Override
			public boolean accept(File f) {
				return true;
			}
		});
		this.createLayout();
	}
	
	public void resize(Size size) {
		this.calcNewSize(size);
		this.scrollPane.setPreferredSize(new Dimension(this.textAreaWidth,
				this.textAreaHeight));
		this.nameField.setColumns(this.textFieldWidth);
		this.pack();
	}
	
	private void calcNewSize(Size size) {
		switch (size) {
		case SMALL:
			this.textAreaWidth = 350;
			this.textFieldWidth = 25;
			break;
		case MEDIUM:
			this.textAreaWidth = 500;
			this.textFieldWidth = 35;
			break;
		case LARGE:
			this.textAreaWidth = 800;
			this.textFieldWidth = 60;
			break;
		default:
			System.err.println("Unsupported option!");
			return;
		}
		this.textAreaHeight = (int) (((double) this.textAreaWidth) * (3d / 7d));
	}
	
	//public String getSelectedUISize() {
	//	return this.uiSizes.getSelection().getActionCommand();
	//}
	
	public void addCommand(String command) {
		this.commandModel.addElement(command);
	}
	
	public void removeCommands(Collection<String> commands) {
		for (String s : commands) {
			this.commandModel.removeElement(s);
		}
	}
	
	public String getTextFieldString() {
		return this.nameField.getText();
	}
	
	public File createOpenFileDialog() {
		int userSelection = this.fileChooser.showOpenDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = this.fileChooser.getSelectedFile();
			return file;
		} else {
			return null;
		}
	}
	
	public Dimension getTextFieldSize() {
		return new Dimension(this.textAreaWidth, this.textAreaHeight);
	}
	
	public File createSaveFileDialog() {
		int userSelection = this.fileChooser.showSaveDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = this.fileChooser.getSelectedFile();
			return file;
		} else {
			return null;
		}
	}
	
	private void createLayout() {
		this.setJMenuBar(this.menuFactroy.getMenuBar());
		
		JPanel vertical = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		vertical.setLayout(new GridBagLayout());
		
		JPanel addPanel = new JPanel();
		addPanel.setLayout(new FlowLayout());
		addPanel.add(this.add);
		addPanel.add(this.nameField);
		
		JPanel removePanel = new JPanel();
		removePanel.add(this.remove);
		
		//this.scrollPane.setPreferredSize(new Dimension(this.textAreaWidth, this.textAreaHeight));
		vertical.add(scrollPane, c);
		c.gridy++;
		vertical.add(addPanel, c);
		c.gridy++;
		vertical.add(removePanel, c);
		
		this.add(vertical);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(this.controller);
		this.addWindowStateListener(this.controller);
		this.resize(View.Size.SMALL);
	}
	
	public void showGui() {
		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(this.fileChooser);
		this.pack();
		this.setVisible(true);
	}
	
	public void addCommmands(final List<String> newCommands) {
		for (String s : newCommands)
			this.commandModel.addElement(s);
		Helpers.sortCommands(list);
	}
	
	public List<String> getCommands() {
		List<String> commands = new ArrayList<String>();
		Enumeration<String> modelContent = this.commandModel.elements();
		while (modelContent.hasMoreElements()) {
			commands.add(modelContent.nextElement());
		}
		return commands;
	}
	
	public List<String> getSelectedCommands() {
		return this.list.getSelectedValuesList();
	}
	
	public static void setLookAndFeel() {
		try {
			LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
			LookAndFeelInfo GTK_info = null;
			LookAndFeelInfo numbus_info = null;
			for (LookAndFeelInfo i : installedLookAndFeels) {
				if (i.getName().equals("GTK+")) {
					GTK_info = i;
				} else if (i.getName().equals("Nimbus")) {
					numbus_info = i;
				}
			}
			if (GTK_info != null) {
				UIManager.setLookAndFeel(GTK_info.getClassName());
			} else if (numbus_info != null) {
				UIManager.setLookAndFeel(numbus_info.getClassName());
			} else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			System.err.println("Could not load GKT framework. Going to exit");
		}
	}

	public void enableSaveButton(boolean enabled) {
		if (enabled && !this.saveButton.isEnabled()) {
			this.saveButton.setEnabled(enabled);
		} else if (!enabled && this.saveButton.isEnabled()) {
			this.saveButton.setEnabled(enabled);
		}
	}
}
