package mvc;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class AddCommandDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final DefaultListModel<String> listModel;
	private final JList<String> commands;
	private final View view;
	
	public AddCommandDialog(final Frame owner, final Collection<String> commands) {
		super(owner, "Add Commands", true);
		
		if (this.getParent() instanceof View) {
			this.view = (View) this.getParent();
		} else {
			System.err.println("This class should only be used from instances of mvc.View");
			this.view = null;
		}
		
		this.listModel = new DefaultListModel<String>();
		for (String s : commands) {
			listModel.addElement(s);
		}
		this.commands = new JList<String>(this.listModel);
		this.commands.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.createLayout(commands);
	}

	private void createLayout(final Collection<String> commands) {
		if (this.view != null) {
			String ok = "OK";
			String abort = "abort";
			String selectAll = "select all";
			String deselectAll = "deselect all";
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.LINE_START;
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridLayout(2, 2));
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			ButtonController buttonController = new ButtonController(this.commands, this, this.view);
			final JButton okButton = new JButton(ok);
			okButton.setActionCommand(ok);
			okButton.addActionListener(buttonController);
			final JButton abortButton = new JButton(abort);
			abortButton.setActionCommand(abort);
			abortButton.addActionListener(buttonController);
			final JButton selectAllButton = new JButton(selectAll);
			selectAllButton.setActionCommand(selectAll);
			selectAllButton.addActionListener(buttonController);
			final JButton deselectAllButton = new JButton(deselectAll);
			deselectAllButton.setActionCommand(deselectAll);
			deselectAllButton.addActionListener(buttonController);
			buttonPanel.add(okButton);
			buttonPanel.add(abortButton);
			buttonPanel.add(selectAllButton);
			buttonPanel.add(deselectAllButton);
			
			JPanel scrollPanel = new JPanel();
			JScrollPane scrollPane = new JScrollPane(this.commands);
			scrollPane.setPreferredSize(view.getTextFieldSize());
			scrollPanel.add(scrollPane);
			
			this.add(scrollPanel, c);
			c.gridy++;
			this.add(buttonPanel, c);
			
			this.setLocation(view.getLocation());
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.pack();
			this.setVisible(true);
		} else {
			System.err.println("Could not create layout");
		}
	}
	
	class ButtonController implements ActionListener {
		
		private final JList<String> list;
		private final Window window;
		private final View view;
		
		public ButtonController(final JList<String> list, final Window window, View view) {
			this.list = list;
			this.window = window;
			this.view = view;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			if (actionCommand.equals("OK")) {
				final List<String> selectedCommands = this.list.getSelectedValuesList();
				this.view.addCommmands(selectedCommands);
				this.window.dispose();
			} else if (actionCommand.equals("abort")) {
				this.window.dispose();
			} else if (actionCommand.equals("select all")) {
				int start = 0;
				int end = this.list.getModel().getSize() - 1;
				if (start < end)
					this.list.setSelectionInterval(start, end);
			} else if (actionCommand.equals("deselect all")) {
				this.list.clearSelection();
			} else {
				System.err.println("Unknown button event detected");
			}
		}
		
	}
}
