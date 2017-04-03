/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.wizards;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.homeria.webratioassistant.plugin.MyIEntityComparator;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

/**
 * WizardSelectEntityPage: Clase que genera los elementos visuales que se
 * muestran en la página que permite seleccionar la entidad sobre la que se
 * generará el CRUD
 */
public class WizardSelectEntityPage extends WizardPage {
	private Composite container = null;
	private Label labelEntity = null;
	private List<IEntity> listEntity; // esta ordenada por entidad
	private Combo selectEntity = null;

	public WizardSelectEntityPage() {
		super("wizardSelectEntityPage");
		setTitle("WebRatio Assistant");
		setDescription("This page lets you select the entity to perform the CRUD.");
	}

	@Override
	public boolean canFlipToNextPage() {
		if (this.selectEntity.getSelectionIndex() >= 0)
			return true;
		else
			return false;
	}

	private void combo1WidgetSelected(SelectionEvent evt) {
		this.getWizard().getContainer().updateButtons();
	}

	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NULL);
		this.container.setLayout(null);

		this.labelEntity = new Label(this.container, SWT.NONE);
		this.labelEntity.setText("Select an entity:");
		this.labelEntity.setBounds(new Rectangle(280 - 150, 45, 150, 25));
		createSelectEntity();

		initialize();
		setControl(this.container);
		// this.dispose();
		try {
			this.dispose();
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void createSelectEntity() {
		this.selectEntity = new Combo(this.container, SWT.NONE);
		this.selectEntity.setBounds(new Rectangle(280, 45, 166, 21));
		this.selectEntity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				combo1WidgetSelected(evt);
			}
		});

	}

	@Override
	public IWizardPage getNextPage() {
		
		WizardCRUDPreviaPage crud = (WizardCRUDPreviaPage) this.getWizard()
				.getPage("wizardCRUDPreviaPage");

		crud.changeEntity((IEntity) this.getSelectedElement());
		// crud.initialize();
		return crud;
	}

	public IMFElement getSelectedElement() {

		return this.listEntity.get(this.selectEntity.getSelectionIndex());

	}

	private void initialize() {
		/*
		 * if (ProjectParameters.getWebProjectEditor() != null) { try {
		 * ProjectParameters.init(); } catch (ExecutionException e) {
		 * e.printStackTrace(); } this.listEntity =
		 * ProjectParameters.getDataModel().getEntityList(); IMFElement imfe;
		 * Iterator<IEntity> iter = this.listEntity.iterator(); while
		 * (iter.hasNext()) { imfe = iter.next();
		 * this.selectEntity.add(Utilities.getAttribute(imfe, "name")); } }
		 */
		if (ProjectParameters.getWebProjectEditor() != null) {
			try {
				ProjectParameters.init();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			this.listEntity = ProjectParameters.getDataModel().getEntityList();
			this.listEntity = ProjectParameters.getDataModel()
					.getAllEntityList();
			// Ordenar entidades: si se quiere ascendetemente o
			// descente... usar comparator ordenar lista por name
			Collections.sort(this.listEntity, new MyIEntityComparator());

			IMFElement imfe;

			Iterator<IEntity> iter = this.listEntity.iterator();
			while (iter.hasNext()) {
				imfe = iter.next();
				this.selectEntity.add(Utilities.getAttribute(imfe, "name"));
			}

		}

	}

	// public void finalize(){
	// this.dispose();
	// }

}