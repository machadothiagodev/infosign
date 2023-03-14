package br.com.signer;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import br.com.signer.model.PrescriptionModel;

public class PrescriptionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final String[] COLUMNS_NAME = { "Paciente", "Data do Pedido", "Receituário" };

	private transient List<PrescriptionModel> tableItems;

	@Override
	public String getColumnName(int column) {
		return COLUMNS_NAME[column];
	}

	@Override
	public int getColumnCount() {
		return COLUMNS_NAME.length;
	}

	@Override
	public int getRowCount() {
		return this.tableItems.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		PrescriptionModel prescription = this.tableItems.get(row);

		switch (col) {
			case 0:
				return prescription.getPatientName();
			case 1:
				return prescription.getRequestDate();
			case 2:
				return prescription.getFileURL(Boolean.TRUE);
			default:
				return null;
		}

	}

	public List<PrescriptionModel> getTableItems() {
		return tableItems;
	}

	public void setTableItems(List<PrescriptionModel> tableItems) {
		this.tableItems = tableItems;
	}

}
