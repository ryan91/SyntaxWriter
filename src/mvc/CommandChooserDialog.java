package mvc;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListModel;

public class CommandChooserDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final ListModel<String> listModel;
	private final JList<String> list;
	
	public CommandChooserDialog(final JFrame frame, final List<String> commands) {
		super(frame, "Choose Commands", true);
		this.listModel = new DefaultListModel<String>();
		this.list = new JList<String>();
	}
	
	
}
