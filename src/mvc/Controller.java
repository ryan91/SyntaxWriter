package mvc;

import io.FileHandlerCls;
import io.FileHandlerTexmaker;
import io.FileHandlerTexstudio;

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
	
	public enum ExportStyle {TEXMAKER, TEXSTUDIO, LATEX};
	public enum ImportStyle {TEXMAKER, TEXSTUDIO, LATEX};
	
	private View view;
	private ExportStyle exportStyle;
	private ImportStyle importStyle;
	private final FileHandlerTexmaker texmaker;
	private final FileHandlerTexstudio texstudio;
	private final FileHandlerCls latex;
	
	public Controller() {
		this.view = new View(this);
		this.exportStyle = Controller.ExportStyle.TEXMAKER;
		this.importStyle = Controller.ImportStyle.TEXMAKER;
		this.texmaker = new FileHandlerTexmaker();
		this.texstudio = new FileHandlerTexstudio();
		this.latex = new FileHandlerCls();
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
		}else if (actionCommand.contains("importstyle/")) {
			String whichButton = actionCommand.substring(
					actionCommand.indexOf("/") + 1, actionCommand.length());
			if (whichButton.equals("Texmaker ini file")) {
				this.importStyle = Controller.ImportStyle.TEXMAKER;
			} else if (whichButton.equals("Texstudio cwl file")) {
				this.importStyle = Controller.ImportStyle.TEXSTUDIO;
			} else if (whichButton.equals("LaTeX class cls file")) {
				this.importStyle = Controller.ImportStyle.LATEX;
			}
		} else if(actionCommand.contains("exportstyle/")) {
			String whichButton = actionCommand.substring(
					actionCommand.indexOf("/") + 1, actionCommand.length());
			if (whichButton.equals("Texmaker ini file")) {
				this.exportStyle = Controller.ExportStyle.TEXMAKER;
			} else if (whichButton.equals("Texstudio cwl file")) {
				this.exportStyle = Controller.ExportStyle.TEXSTUDIO;
			} else if (whichButton.equals("LaTeX class cls file")) {
				this.exportStyle = Controller.ExportStyle.LATEX;
			}
		} else if (actionCommand == View.OPEN_NAME) {
			File file = this.view.createOpenFileDialog();
			if (file != null) {
				if (file.exists()) {
					if (file.canRead()) {
						List<String> existingCommands = new ArrayList<String>();
						try {
							switch (this.importStyle) {
							case TEXMAKER:
								existingCommands = this.texmaker.readFile(file);
								break;
							case TEXSTUDIO:
								existingCommands = this.texstudio.readFile(file);
								break;
							case LATEX:
								existingCommands = this.latex.readFile(file);
								break;
							default:
								System.err.println("Unsupported Option!");
								break;
							}
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
			// TODO delete this passage after LaTeX writer implementation
			if (this.exportStyle == ExportStyle.LATEX) {
				JOptionPane.showMessageDialog(this.view,
						"This feature is unfortunately not provided yet",
						"Unsupported Feature", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			File file = this.view.createSaveFileDialog();
			if (file != null) {
				if (file.exists()) {
					if (file.canWrite()) {
						System.out.println(file.getAbsolutePath());
						try {
							switch (this.exportStyle) {
							case TEXMAKER:
								this.texmaker.writeFile(file, this.view.getCommands());
								break;
							case TEXSTUDIO:
								this.texstudio.writeFile(file, this.view.getCommands());
								break;
							case LATEX:
								// TODO implement class writer
							default:
								System.err.println("Unsupported Option");
								break;
							}
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
