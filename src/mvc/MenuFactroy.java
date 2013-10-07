package mvc;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;

public class MenuFactroy {
	
	private final ArrayList<JMenu> menues;
	private final ArrayList<JMenuItem> menuItems;
	
	private JMenuBar menuBar;
	
	public MenuFactroy() {
		this.menues = new ArrayList<JMenu>();
		this.menuItems = new ArrayList<JMenuItem>();
		this.menuBar = new JMenuBar();
	}
	
	public void addMenu(String name) {
		JMenu menu = new JMenu(name);
		this.menues.add(menu);
		this.menuBar.add(menu);
	}
	
	public JMenu getMenu(String menuName) {
		for (JMenu menu : this.menues) {
			if (menu.getText().equals(menuName)) {
				return menu;
			}
		}
		return null;
	}
	
	private JMenu getMenuVerbose(String menuName) {
		JMenu menu = this.getMenu(menuName);
		if (menu == null) {
			System.err.println("Warning: The menu " + menuName + " was not found in the MenuFactory");
		}
		return menu;
	}
	
	public void addSubMenu(String menuName, String subMenuName) {
		JMenu menu = this.getMenuVerbose(menuName);
		if (menu != null) {
			JMenu subMenu = new JMenu(subMenuName);
			menu.add(subMenu);
			this.menues.add(subMenu);
		}
	}
	
	//public void setActionListener(String menuName, ActionListener actionListener) {
	//	for (JMenu menu : this.menues) {
	//		if (menu.getText().equals(menuName))
	//			menu.addActionListener(actionListener);
	//	}
	//}
	
	public void addSubMenu(JMenu menu, String subMenuName) {
		JMenu subMenu = new JMenu(subMenuName);
		menu.add(subMenu);
		this.menues.add(subMenu);
	}
	
	public void addMenuItem(String menuName, JMenuItem menuItem) {
		JMenu menu = this.getMenuVerbose(menuName);
		if (menu != null) {
			menu.add(menuItem);
			this.menuItems.add(menuItem);
		}
	}
	
	public void addMenuItem(String menuName, String menuItemName) {
		JMenu menu = this.getMenuVerbose(menuName);
		if (menu != null) {
			JMenuItem menuItem = new JMenuItem(menuItemName);
			menu.add(menuItem);
			this.menuItems.add(menuItem);
		}
	}
	
	public JMenuBar getMenuBar() {
		return this.menuBar;
	}
	
	public JMenuItem createMenuItem(String name, String actionCommand, ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(actionListener);
		return menuItem;
	}
	
	public JMenuItem createMenuItem(String name, ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setActionCommand(name);
		menuItem.addActionListener(actionListener);
		return menuItem;
	}
	
	public JMenuItem createMenuItem(String name) {
		JMenuItem menuItem = new JMenuItem(name);
		return menuItem;
	}
	
	public ButtonGroup createRadioButtonGroup(String[] buttonNames, String identifier, String selected, ActionListener actionListener) {
		ButtonGroup buttonGroup = new ButtonGroup();
		for (String s : buttonNames) {
			JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem(s);
			radioButton.setActionCommand(identifier + "/" + s);
			radioButton.addActionListener(actionListener);
			buttonGroup.add(radioButton);
			if (s == selected) {
				radioButton.setSelected(true);
			}
		}
		return buttonGroup;
	}
	
	public void addButtonGroup(String menuName, ButtonGroup buttonGroup) {
		JMenu menu = this.getMenuVerbose(menuName);
		if (menu != null) {
			Enumeration<AbstractButton> buttons = buttonGroup.getElements();
			while(buttons.hasMoreElements()) {
				menu.add(buttons.nextElement());
			}
		}
	}
	
	public void addSeparator(String menuName) {
		
	}
}
