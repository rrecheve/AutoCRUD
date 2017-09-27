/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 */
package org.homeria.webratioassistant.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.homeria.webratioassistant.registry.RegistryDialog;

/**
 * This Wizard Dialog adds a Registry button in the button bar.
 */
public class WizardDialogWithRegistryButton extends WizardDialog {

	public WizardDialogWithRegistryButton(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		// Change parent layout data to fill the whole bar
		parent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button registryButt = this.createButton(parent, IDialogConstants.NO_ID, "Registry", true);
		registryButt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				RegistryDialog dialog = new RegistryDialog(WizardDialogWithRegistryButton.this.getShell());

				dialog.setBlockOnOpen(true);
				dialog.open();
			}
		});

		// Create a spacer label
		Label spacer = new Label(parent, SWT.NONE);
		spacer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Update layout of the parent composite to count the spacer
		GridLayout layout = (GridLayout) parent.getLayout();
		layout.numColumns++;
		layout.makeColumnsEqualWidth = false;

		super.createButtonsForButtonBar(parent);
	}
}