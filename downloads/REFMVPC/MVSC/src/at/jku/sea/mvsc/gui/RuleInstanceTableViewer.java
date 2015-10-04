/**
 * Generic Table Viewer for the rule instance information
 */
package at.jku.sea.mvsc.gui;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import at.jku.sea.mvsc.constraints.*;

import java.util.List;

import org.eclipse.swt.graphics.*;

public class RuleInstanceTableViewer <T>{
	
	Display display;
	Shell shell;
	TableViewer tableViewer;
	Table table;
	
	/**
	 * Creates and does the set up of the table viewer
	 * @param tableTitle
	 * @param columnNames
	 * @param columnWidths
	 * @param columnAlignments
	 * @param ruleViewProvider
	 */
	public RuleInstanceTableViewer(String tableTitle, String[] columnNames, int[] columnWidths, int[] columnAlignments,
								   ITableLabelProvider ruleViewProvider, List<IRuleInstance<T>> ruleInstancesList) {
//								   Composite composite, Shell shell, Display display) {
		
		
		display = new Display();
		shell = new Shell(display);
		shell.setText(tableTitle);
		shell.setBounds(100, 100, 900, 200);
		shell.setLayout(new FillLayout());
		/*
		this.display = display;
		this.shell = shell;
		tableViewer = new TableViewer (composite, SWT.SINGLE | SWT.FULL_SELECTION);
		*/
		tableViewer = new TableViewer (shell, SWT.SINGLE | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// initialization of column data
		for (int i=0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(table, columnAlignments[i]);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
		}
		
		// sets the label provider
		tableViewer.setLabelProvider(ruleViewProvider);
	
		// By default it gets the information from an array
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		// sets the input for the table
		tableViewer.setInput(ruleInstancesList);
	
	} // of constructor
	
	
	// @test, alternative method that passes the parent shell for the table to be
	
	
	
	
	// get methods
	public Table getTable() { return table; }
	
	/**
	 * Opens the table for display
	 */
	public void open() {
		shell.open();
		
		// leave it open
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
		
		
	} // open
	

} // of Table Viewer
