/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 * */
package org.homeria.webratioassistant.generation;

import javax.xml.transform.TransformerException;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * This window gives generation process information to the user and gives him the control to generate one or more units
 */
public class StepGenerationAppWindow extends ApplicationWindow {

	Generate generate;
	Button next;
	Button fastForward;
	ProgressBar progressBar;
	Label titleNextElemLabel;
	Label nextElemLabel;

	/**
	 * Constructor that creates the new instance
	 * 
	 * @param parentShell
	 *            the parent Shell
	 * @param generate
	 *            the Generate instance with all the elements to generate
	 */
	public StepGenerationAppWindow(Shell parentShell, Generate generate) {
		super(parentShell);
		this.generate = generate;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout containerLayout = new GridLayout(1, false);
		containerLayout.marginWidth = 20;
		containerLayout.marginHeight = 20;
		containerLayout.verticalSpacing = 15;
		container.setLayout(containerLayout);
		container.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		this.progressBar = new ProgressBar(container, SWT.SMOOTH);
		this.progressBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		this.titleNextElemLabel = new Label(container, SWT.NONE);
		this.titleNextElemLabel.setText("Next element: ");
		this.titleNextElemLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		this.nextElemLabel = new Label(container, SWT.NONE);
		this.nextElemLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		Composite buttonsCompo = new Composite(container, SWT.NONE);
		buttonsCompo.setLayout(new GridLayout(2, false));
		buttonsCompo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		this.next = new Button(buttonsCompo, 0);
		this.next.setText("Next Step");
		this.next.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				try {
					if (!StepGenerationAppWindow.this.generate.next())
						StepGenerationAppWindow.this.close();
				} catch (TransformerException e1) {
					e1.printStackTrace();
				}
			}
		});

		this.fastForward = new Button(buttonsCompo, 0);
		this.fastForward.setText("Fast Forward");
		this.fastForward.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				try {
					StepGenerationAppWindow.this.disableButtons();
					StepGenerationAppWindow.this.generate.end();
					StepGenerationAppWindow.this.close();
				} catch (TransformerException e1) {
					e1.printStackTrace();
				}
			}
		});

		this.generate.setUIelements(this.progressBar, this.nextElemLabel);

		return super.createContents(buttonsCompo);
	}

	private void disableButtons() {
		this.next.setEnabled(false);
		this.fastForward.setEnabled(false);

	}
}