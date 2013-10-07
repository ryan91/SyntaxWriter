package mvc;

import io.TexmakerIo;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.lang.Character.Subset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller implements ActionListener, ListSelectionListener,WindowListener, WindowStateListener, ListDataListener {
	
	private View view;
	
	public Controller() {
		this.view = new View(this);
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
				System.out.println("small...");
			} else if (whichButton.equals("Medium")) {
				System.out.println("medium...");
			} else if (whichButton.equals("Large")) {
				System.out.println("large...");
			}
		} else if (actionCommand == View.OPEN_NAME) {
			File file = this.view.createOpenFileDialog();
			if (file != null) {
				if (file.exists()) {
					if (file.canRead()) {
						try {
							List<String> existingCommands = TexmakerIo.getExistingCommands(file);
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
			File file = this.view.createSaveFileDialog();
			if (file != null) {
				if (file.exists()) {
					if (file.canWrite()) {
						System.out.println(file.getAbsolutePath());
						try {
							TexmakerIo.writeToFile(file, this.view.getCommands());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
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
		controller.getView().createLayout();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				launchApplication();
			}
		});
	}

	@Override
	public void windowStateChanged(WindowEvent e) {
		if (e.getNewState() == Frame.MAXIMIZED_BOTH) {
			System.out.println("HELLLO");
			JFrame frame = (JFrame) e.getWindow();
			System.out.println(frame.getWidth());
			System.out.println(frame.getHeight());
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	
	// ---------------------------------------------
	// below ListDataListener
	@Override
	public void intervalAdded(ListDataEvent e) {
		/* do nothing */
		this.view.enableSaveButton(true);
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		final DefaultListModel<?> listModel = (DefaultListModel<?>) e.getSource();
		if (listModel.size() == 0) {
			this.view.enableSaveButton(false);
		} else {
			this.view.enableSaveButton(true);
		}
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		/* do nothing */
	}
}
