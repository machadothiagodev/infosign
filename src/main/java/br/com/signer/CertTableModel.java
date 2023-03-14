package br.com.signer;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class CertTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnsName;
	private List<String> items;

	public CertTableModel(String[] columnsName, List<String> items) {
		this.columnsName = columnsName;
		this.items = items;
	}

	@Override
	public String getColumnName(int column) {
		return this.columnsName[column];
	}

	@Override
	public int getColumnCount() {
		return this.columnsName.length;
	}

	@Override
	public int getRowCount() {
		return this.getItems().size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return this.getItems().get(row);
	}

	public List<String> getItems() {
		if (this.items == null) {
			this.items = new LinkedList<>();
		}
		return this.items;
	}

	public void addItem(String item) {
		this.items.add(item);
		this.fireTableDataChanged();
	}
	
	public void removeItem(int index) {
		this.items.remove(index);
		this.fireTableDataChanged();
	}

}
