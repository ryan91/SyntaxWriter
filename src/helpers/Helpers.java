package helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class Helpers {
	private Helpers() {
		/* do nothing */
	}
	
	public static void sortCommands(final JList<String> list) {
		final DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
		if (!model.isEmpty()) {
			final List<String> sortList = new ArrayList<String>();
			for (int i = 0; i < model.size(); i++) {
				sortList.add(model.get(i));
			}
			Collections.sort(sortList);
			model.clear();
			for (String s : sortList)
				model.addElement(s);
		}
	}
}
