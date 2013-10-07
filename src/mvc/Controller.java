package mvc;

import io.FileHandlerTexmaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller implements ActionListener, ListSelectionListener, ListDataListener, DocumentListener {
	
	public enum ExportStyle {TEXMAKER, TEXSTUDIO};
	
	private View view;
	private ExportStyle exportStyle;
	
	public Controller() {
		this.view = new View(this);
		this.exportStyle = Controller.ExportStyle.TEXMAKER;
	}
	
	public View getView() {
		return this.view;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand == View.ADD_NAME) {
			String command = this.view.getTextFieldString();
			if (!command.isEmpty()) {
				this.view.addCommand(this.view.getTextFieldString());
			} else {
				JOptionPane.showMessageDialog(this.view,
						"You can't add an empty command", "Invalid Command",
						JOptionPane.WARNING_MESSAGE);
			}
		} else if (actionCommand == View.REMOVE_NAME) {
			List<String> toRemove = this.view.getSelectedCommands();
			if (!toRemove.isEmpty()) {
				this.view.removeCommands(toRemove);
			}
		} else if (actionCommand.contains("uisize/")) {
			String whichButton = actionCommand.substring(
					actionCommand.indexOf('/') + 1, actionCommand.length());
			if (whichButton.equals("Small")) {
				this.view.resize(View.Size.SMALL);
			} else if (whichButton.equals("Medium")) {
				this.view.resize(View.Size.MEDIUM);
			} else if (whichButton.equals("Large")) {
				this.view.resize(View.Size.LARGE);
			}
		} else if(actionCommand.contains("exportstyle/")) {
			String whichButton = actionCommand.substring(
					actionCommand.indexOf("/") + 1, actionCommand.length());
			if (whichButton.equals("Texmaker ini file")) {
				this.exportStyle = Controller.ExportStyle.TEXMAKER;
			} else if (whichButton.equals("Texstudio cwl file")) {
				this.exportStyle = Controller.ExportStyle.TEXSTUDIO;
			}
		} else if (actionCommand == View.OPEN_NAME) {
			File file = this.view.createOpenFileDialog();
			if (file != null) {
				if (file.exists()) {
					if (file.canRead()) {
						try {
							FileHandlerTexmaker texmaker = new FileHandlerTexmaker();
							List<String> existingCommands = texmaker.readFile(file);
							Collections.sort(existingCommands);
							new AddCommandDialog(this.view, existingCommands);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
					} else {
						System.err.println("No read permissions for this file or directory");
					}
				} else {
					System.err.println("This file does not seem to exist");
				}
			} else {
				/* user aborted action - do nothing */
				new AddCommandDialog(this.view, new ArrayList<String>());
			}
		} else if (actionCommand == View.SAVE_NAME) {
			switch (this.exportStyle) {
			case TEXMAKER:
				File file = this.view.createSaveFileDialog();
				if (file != null) {
					if (file.exists()) {
						if (file.canWrite()) {
							System.out.println(file.getAbsolutePath());
							try {
								FileHandlerTexmaker texmaker = new FileHandlerTexmaker();
								texmaker.writeFile(file, this.view.getCommands());
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						} else {
							System.err.println("No write permissions for this file or directory");
						}
					} else {
						System.err.println("This file does not seem to exist");
					}
				} else {
					// user aborted action - do nothing
				}
				break;
			case TEXSTUDIO:
				System.out.println("Not implemented yet");
				break;
			default:
				System.err.println("Unsupported Option");
				break;
			}
		} else if (actionCommand == View.QUIT_NAME) {
			this.view.setVisible(false);
			System.exit(0);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		/* do nothing */
	}
	
	private static void launchApplication() {
		Controller controller = new Controller();
		View.setLookAndFeel();
		controller.getView().showGui();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				launchApplication();
			}
		});
	}
	
	// ---------------------------------------------
	// below ListDataListener
	@Override
	public void intervalAdded(ListDataEvent e) {
		/* do nothing */
		this.view.setSaveButtonEnabled(true);
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		final DefaultListModel<?> listModel = (DefaultListModel<?>) e.getSource();
		if (listModel.size() == 0) {
			this.view.setSaveButtonEnabled(false);
		} else {
			this.view.setSaveButtonEnabled(true);
		}
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		/* do nothing */
	}
	
	// ----------------------------------------------
	// below DocumentListener
	@Override
	public void insertUpdate(DocumentEvent e) {
		this.view.setAddButtonEnabled(true);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0) {
			this.view.setAddButtonEnabled(false);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		/* do nothing */
	}
}
