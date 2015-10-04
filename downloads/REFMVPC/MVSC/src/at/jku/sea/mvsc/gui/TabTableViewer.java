package at.jku.sea.mvsc.gui;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

import at.jku.sea.mvsc.constraints.*;


import java.util.List;

import org.eclipse.swt.graphics.*;

public class TabTableViewer {
	Display display;
	Shell shell;
	
	public TabTableViewer() {
		/*
		display = new Display();
		shell = new Shell(display);
		shell.setText("SPL Rule Viewer");
		shell.setBounds(100, 100, 175, 125);
		shell.setLayout(new FillLayout());
		final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
		for (int i=1 ; i < 4; i++) {
			TabItem tabItem = new TabItem(tabFolder,SWT.NULL);
			tabItem.setText("Tab " + i);
			Composite composite = new Composite (tabFolder, SWT.NULL);
			tabItem.setControl(composite);
			Button button = new Button(composite, SWT.PUSH);
			button.setBounds(25,25,100,25);
			button.setText("Click me now");
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					((Button)event.widget).setText("I was clicked");
				}
			});
		}
		*/
	} // of constructor
	

	
	public TabTableViewer(Table table) {
		display = new Display();
		shell = new Shell(display);
		shell.setText("SPL Rule Viewer");
		shell.setBounds(100, 100, 175, 125);
		shell.setLayout(new FillLayout());
		TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
		TabItem tabItem = new TabItem(tabFolder,SWT.NULL);
		tabItem.setText("Rule 4");
		Composite composite = new Composite (tabFolder, SWT.NULL);
		tabItem.setControl(table);
	} // of constructor with viewer
	
	public void open() {
		shell.open();
		while(!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
} // of TabTableViewer

