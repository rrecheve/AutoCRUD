/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */

package org.homeria.webratioassistant.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.homeria.webratioassistant.plugin.ObjStViewArea;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

/**
 * @author Carlos Aguado Fuentes
 * @class WizardCRUDPage
 */
/**
 * WizardCRUDPage: Clase que genera los elementos visuales que se muestran en la
 * página que permite seleccionar las opciones para generar el CRUD
 */
public class WizardCRUDPage extends WizardPage {
	private Map<String, IAttribute> atributosRelacion;
	private List<TableItem> checkDataRead;
	private List<TableItem> checkIndexDelete;
	private List<TableItem> checkIndexRead;
	private List<TableItem> checkIndexUpdate;
	private ArrayList<CCombo> checkOpcionesDelete;
	private List<CCombo> checkOpcionesRead;
	private ArrayList<CCombo> checkOpcionesUpdate;
	private List<TableItem> checkShowUpdate;

	private Composite composite1;
	private Composite composite2;
	private Composite composite3;
	private Composite composite4;
	private Composite container;
	private IEntity entidad = null;
	// private ArrayList<IEntity> entidadesRelacionadas;
	private ArrayList<IRelationshipRole> entidadesRelacionadas;
	private Group group1;
	private Group group10;
	private Group group2;
	private Group group3;
	private Group group4;
	private Group group5;
	private Group group6;
	private Group group7;
	private Group group8;
	private Group group9;
	private List<IAttribute> listaAtributos;
	private List<IAttribute> listaAtributosSinDerivados;
	private List<CCombo> listaCombosCreate;
	private List<CCombo> listaCombosUpdate;
	private List<ISiteView> listaSiteViews;
	private WizardSelectEntityPage pageSelectEntity;
	private TabFolder tabFolder1;
	private TabItem tabItem1;
	private TabItem tabItem2;
	private TabItem tabItem3;
	private TabItem tabItem4;
	private Table tableDataRead;
	private Table tableDataAllInOne;
	private Table tableIndexCreate;
	private Table tableIndexDelete;
	private Table tableIndexRead;
	private Table tableIndexUpdate;
	private Table tableOpcionesDelete;
	private Table tableOpcionesRead;
	private Table tableOpcionesUpdate;
	private Table tableShowUpdate;

	private Tree arbolUpdate;
	private Tree arbolCreate;
	private Tree arbolDelete;
	private Tree arbolRead;
	private Tree arbolAllInOne;

	private TabItem tabItem0;
	private Composite composite5;
	private Group group11;
	private Group group12;
	private Table tableIndexAllInOne;
	private Group group13;
	private Table tableFormAllInOne;
	private List<TableItem> checkFormAllInOne;
	private List<CCombo> listaCombosAllInOne;
	private Group group14;
	private List<TableItem> checkDataAllInOne;

	public WizardCRUDPage(IEntity entity) {
		super("wizardCRUDPage");
		setTitle("Webratio CRUD");
		setDescription("Configure options.");

		declaracionEstructuras(entity);
	}

	private void declaracionEstructuras(IEntity entity) {
		this.entidad = entity;
		this.checkIndexRead = new ArrayList<TableItem>();
		this.checkIndexUpdate = new ArrayList<TableItem>();
		this.checkShowUpdate = new ArrayList<TableItem>();
		this.checkIndexDelete = new ArrayList<TableItem>();
		this.checkDataRead = new ArrayList<TableItem>();
		this.atributosRelacion = new HashMap<String, IAttribute>();
		this.listaCombosCreate = new ArrayList<CCombo>();
		this.listaCombosUpdate = new ArrayList<CCombo>();
		this.checkOpcionesRead = new ArrayList<CCombo>();
		this.checkOpcionesUpdate = new ArrayList<CCombo>();
		this.checkOpcionesDelete = new ArrayList<CCombo>();
		this.listaAtributos = new ArrayList<IAttribute>();
		this.listaAtributosSinDerivados = new ArrayList<IAttribute>();
		this.checkFormAllInOne = new ArrayList<TableItem>();
		this.checkDataAllInOne = new ArrayList<TableItem>();
		this.listaCombosAllInOne = new ArrayList<CCombo>();
	}

	/**
	 * 
	 * Nombre: addAtributesToCombo Funcion:
	 * 
	 * @param combo
	 * @param role
	 * @param editor
	 * @return
	 */
	private CCombo addAtributesToCombo(CCombo combo, IRelationshipRole role,
			TableEditor editor) {
		IEntity entidad;
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == this.entidad) {
			entidad = relation.getSourceEntity();
		} else
			entidad = relation.getTargetEntity();

		List<IAttribute> atributos = entidad.getAllAttributeList();
		Iterator<IAttribute> iteratorAtributos = atributos.iterator();
		IAttribute atributo;
		String texto;

		while (iteratorAtributos.hasNext()) {
			atributo = iteratorAtributos.next();
			texto = Utilities.getAttribute(atributo, "name") + " ("
					+ Utilities.getAttribute(role, "name") + ")";
			combo.add(Utilities.getAttribute(atributo, "name"));
			this.atributosRelacion.put(texto, atributo);
		}
		return combo;
	}

	/**
	 * 
	 */
	// (Detalle)
	private void addAttributes(Table tabla, List<TableItem> list,
			Boolean esDataUnit) {
		Iterator<IAttribute> iteratorAttribute;
		if (tabla == this.tableIndexUpdate)
			iteratorAttribute = this.listaAtributosSinDerivados.iterator();
		else
			iteratorAttribute = this.listaAtributos.iterator();
		IAttribute atributo;
		while (iteratorAttribute.hasNext()) {
			atributo = iteratorAttribute.next();
			// Se elimina oid de los Data Unit
			if (!(esDataUnit
					&& Utilities.getAttribute(atributo, "key").equals("true") && ("oid"
					.equals(Utilities.getAttribute(atributo, "name")) || "OID"
					.equals(Utilities.getAttribute(atributo, "name"))))) {
				list.add(new TableItem(tabla, SWT.NONE));
				list.get(list.size() - 1).setText(
						Utilities.getAttribute(atributo, "name") + " ("
								+ atributo.getFinalId() + ")");
			}

		}
	}

	/**
	 * 
	 * Nombre: addRelationships Funcion:
	 * 
	 * @param tabla
	 * @param list
	 */
	private void addRelationships(Table tabla, List<CCombo> list) {
		for (int i = 0; i < 2; i++) {
			TableColumn column = new TableColumn(tabla, SWT.NONE);
			column.setWidth(100);
		}
		for (int i = 0; i < entidadesRelacionadas.size(); i++) {
			new TableItem(tabla, SWT.NONE);
		}
		TableItem[] items = tabla.getItems();

		int numAtributos = this.entidad.getAttributeList().size();

		if (tabla == this.tableIndexUpdate)
			numAtributos = this.listaAtributosSinDerivados.size();

		if (tabla == this.tableIndexCreate || tabla == this.tableFormAllInOne)
			numAtributos = 0;
		for (int i = numAtributos; i < items.length; i++) {
			TableEditor editor = new TableEditor(tabla);
			Text text = new Text(tabla, SWT.NONE);
			text.setText(Utilities.getAttribute(
					this.entidadesRelacionadas.get(i - numAtributos), "name"));
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 0);
			editor = new TableEditor(tabla);
			CCombo combo = new CCombo(tabla, SWT.NONE);
			combo = this.addAtributesToCombo(combo,
					this.entidadesRelacionadas.get(i - numAtributos), editor);
			combo.select(0);
			Integer posicion = new Integer(i);//  se añade posicion que ocupa
												// el combo, sera igual a la del
												// editor asociado a dicho combo
			combo.setData(posicion);// se añade posicion que ocupa el combo,
									// sera igual a la del editor asociado a
									// dicho combo
			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					cCombo1WidgetSelected(evt);
				}
			});
			list.add(combo);
			editor.grabHorizontal = true;
			editor.setEditor(combo, items[i], 1);
		}
	}

	/**
	 * 
	 * Nombre: cCombo1WidgetSelected Funcion:
	 * 
	 * @param evt
	 */
	private void cCombo1WidgetSelected(SelectionEvent evt) {
		try {
			CCombo c = (CCombo) evt.getSource();
			Table t = (Table) c.getParent();

			t.getItems()[(Integer) c.getData()].setChecked(Boolean.TRUE);

			if (t == this.tableIndexCreate) {
				for (int i = 0; i < this.listaCombosCreate.size(); i++) {
					/*
					 * this.listaCombosUpdate.get(i).select(
					 * this.listaCombosCreate.get(i).getSelectionIndex());
					 * this.listaCombosAllInOne.get(i).select(
					 * this.listaCombosCreate.get(i).getSelectionIndex());
					 */
				}
			}

			if (t == this.tableIndexUpdate) {
				for (int i = 0; i < this.listaCombosCreate.size(); i++) {
					/*
					 * this.listaCombosCreate.get(i).select(
					 * this.listaCombosUpdate.get(i).getSelectionIndex());
					 * this.listaCombosAllInOne.get(i).select(
					 * this.listaCombosUpdate.get(i).getSelectionIndex());
					 */

				}
			}
			if (t == this.tableFormAllInOne) {

				for (int i = 0; i < this.listaCombosCreate.size(); i++) {
					/*
					 * this.listaCombosCreate.get(i) .select(
					 * this.listaCombosAllInOne.get(i) .getSelectionIndex());
					 * this.listaCombosUpdate.get(i) .select(
					 * this.listaCombosAllInOne.get(i) .getSelectionIndex());
					 */
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Nombre: crearActualizar Funcion:
	 */
	private void crearActualizar() {
		this.composite3 = new Composite(this.tabFolder1, SWT.NONE);
		FillLayout composite3Layout = new FillLayout(
				org.eclipse.swt.SWT.HORIZONTAL);

		composite3Layout.marginHeight = 5;
		composite3Layout.marginWidth = 5;
		composite3Layout.spacing = 10;

		this.composite3.setLayout(composite3Layout);
		this.tabItem3.setControl(this.composite3);

		this.group6 = new Group(this.composite3, SWT.NONE);
		FillLayout group6Layout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		this.group6.setLayout(group6Layout);
		this.group6.setText("SiteViews-Areas");
		// this.tableSiteViewUpdate = new Table(this.group6, SWT.CHECK
		// | SWT.V_SCROLL);

		this.arbolUpdate = new Tree(this.group6, SWT.MULTI | SWT.CHECK
				| SWT.BORDER);
		// SINGLE, MULTI, CHECK, FULL_SELECTION, FULL_SELECTION SWT.VIRTUAL |
		// SWT.BORDER)
		listaSiteAreaToArbol(this.arbolUpdate);

		arbolUpdate.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				//  Ver si el motivo de la seleccion ha sido el check
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					boolean checked = item.getChecked();
					if (!checked) {
						checkItems(item, checked);
					}
					checkPath(item.getParentItem(), checked, false);
				}
			}

		});



		this.group10 = new Group(this.composite3, SWT.NONE);
		GridLayout groupArbolLayout = new GridLayout();
		groupArbolLayout.numColumns = 1;
		this.group10.setLayout(groupArbolLayout);
		this.group10.setText("Power Index Unit");


		this.tableShowUpdate = new Table(this.group10, SWT.CHECK | SWT.V_SCROLL);


		GridData gridData = new GridData(160, 120);
		tableShowUpdate.setLayoutData(gridData);


		// boton select all y deselect all
		Button buttonSelectPower = new Button(this.group10, SWT.PUSH);
		buttonSelectPower.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectPower = new GridData(GridData.END,
				GridData.CENTER, false, false);
		gridDataButtonSelectPower.horizontalSpan = 3;
		buttonSelectPower.setLayoutData(gridDataButtonSelectPower);
		buttonSelectPower.setSelection(Boolean.FALSE);

		buttonSelectPower.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableIndexRead
						&& null != tableShowUpdate.getItems()
						&& tableShowUpdate.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableShowUpdate.getItems().length; i++) {
						if (tableShowUpdate.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableShowUpdate.getItems().length; i++) {
							tableShowUpdate.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableShowUpdate.getItems().length; i++) {
							tableShowUpdate.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.group10.setVisible(true);

		// fin  boton select all y deselect all

		this.group7 = new Group(this.composite3, SWT.NONE);
		FillLayout group7Layout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		this.group7.setLayout(group7Layout);
		this.group7.setText("Relations and forms");
		this.tableIndexUpdate = new Table(this.group7, SWT.CHECK | SWT.V_SCROLL);

		group7.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent evt) {
				group7PaintControl(evt);
			}
		});

		this.addAttributes(this.tableIndexUpdate, this.checkIndexUpdate,
				Boolean.FALSE);
		this.addAttributes(this.tableShowUpdate, this.checkShowUpdate,
				Boolean.FALSE);
		this.addRelationships(this.tableIndexUpdate, this.listaCombosUpdate);

		this.composite3.layout();
	}

	/**
	 * 
	 * Nombre: crearBorrado Funcion:
	 */
	private void crearBorrado() {
		this.composite4 = new Composite(this.tabFolder1, SWT.NONE);
		FillLayout composite4Layout = new FillLayout(
				org.eclipse.swt.SWT.HORIZONTAL);

		composite4Layout.spacing = 10;
		composite4Layout.marginWidth = 5;
		composite4Layout.marginHeight = 5;

		this.composite4.setLayout(composite4Layout);
		this.tabItem4.setControl(this.composite4);

		this.group8 = new Group(this.composite4, SWT.NONE);
		FillLayout group8Layout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		this.group8.setLayout(group8Layout);
		this.group8.setText("SiteViews-Areas");
		// this.tableSiteViewDelete = new Table(this.group8, SWT.CHECK
		// | SWT.V_SCROLL);

		this.arbolDelete = new Tree(this.group8, SWT.MULTI | SWT.CHECK
				| SWT.BORDER);
		// SINGLE, MULTI, CHECK, FULL_SELECTION, FULL_SELECTION SWT.VIRTUAL |
		// SWT.BORDER)
		listaSiteAreaToArbol(this.arbolDelete);

		arbolDelete.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// ver si el motivo de la seleccion ha sido el check
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					boolean checked = item.getChecked();
					if (!checked) {
						checkItems(item, checked);
					}
					checkPath(item.getParentItem(), checked, false);
				}
			}

		});


		this.group9 = new Group(this.composite4, SWT.NONE);
		GridLayout groupArbolLayout = new GridLayout();
		groupArbolLayout.numColumns = 1;
		this.group9.setLayout(groupArbolLayout);
		this.group9.setText("Power Index Unit");

		this.tableIndexDelete = new Table(this.group9, SWT.CHECK | SWT.V_SCROLL);


		GridData gridData = new GridData(180, 120);
		tableIndexDelete.setLayoutData(gridData);


		// boton select all y deselect all
		Button buttonSelectPowerIndexBorrado = new Button(this.group9, SWT.PUSH);
		buttonSelectPowerIndexBorrado.setText("(Select/Deselect) All Prueba");
		GridData gridDataButtonSelectPower = new GridData(GridData.END,
				GridData.CENTER, false, false);
		gridDataButtonSelectPower.horizontalSpan = 3;
		buttonSelectPowerIndexBorrado.setLayoutData(gridDataButtonSelectPower);
		buttonSelectPowerIndexBorrado.setSelection(Boolean.FALSE);

		buttonSelectPowerIndexBorrado
				.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						if (null != tableIndexDelete
								&& null != tableIndexDelete.getItems()
								&& tableIndexDelete.getItems().length > 0) {
							Boolean hayCheckeados = Boolean.FALSE;

							for (int i = 0; i < tableIndexDelete.getItems().length; i++) {
								if (tableIndexDelete.getItems()[i].getChecked()) {
									hayCheckeados = Boolean.TRUE;
								}
							}

							if (hayCheckeados) {
								// si hay elementos seleccionados: deselecciono
								// all
								// tableIndexRead.deselectAll();
								for (int i = 0; i < tableIndexDelete.getItems().length; i++) {
									tableIndexDelete.getItems()[i]
											.setChecked(false);

								}
							} else {
								// si no hay elementos seleccionados: selecciono
								// all
								// tableIndexRead.selectAll();
								for (int i = 0; i < tableIndexDelete.getItems().length; i++) {
									tableIndexDelete.getItems()[i]
											.setChecked(true);
								}
							}
						}
					}
				});
		this.group9.setVisible(true);

		// fin  boton select all y deselect all

		// this.addSiteViews(this.tableSiteViewDelete, this.checkSiteDelete);
		this.addAttributes(this.tableIndexDelete, this.checkIndexDelete,
				Boolean.FALSE);

		this.composite4.layout();
	}

	/**
	 * 
	 * Nombre: crearCrear Funcion:
	 */
	private void crearCrear() {
		try {
			this.composite1 = new Composite(this.tabFolder1, SWT.NONE);
			FillLayout composite1Layout = new FillLayout(
					org.eclipse.swt.SWT.HORIZONTAL);

			composite1Layout.marginHeight = 5;
			composite1Layout.marginWidth = 5;
			composite1Layout.spacing = 10;

			this.composite1.setLayout(composite1Layout);
			this.tabItem1.setControl(this.composite1);

			this.group1 = new Group(this.composite1, SWT.NONE);
			FillLayout group1Layout = new FillLayout(
					org.eclipse.swt.SWT.HORIZONTAL);
			this.group1.setLayout(group1Layout);
			this.group1.setText("SiteViews-Areas");
			// this.tableSiteViewCreate = new Table(this.group1, SWT.CHECK
			// | SWT.V_SCROLL);

			this.arbolCreate = new Tree(this.group1, SWT.MULTI | SWT.CHECK
					| SWT.BORDER);
			// SINGLE, MULTI, CHECK, FULL_SELECTION, FULL_SELECTION SWT.VIRTUAL
			// | SWT.BORDER)
			listaSiteAreaToArbol(this.arbolCreate);

			arbolCreate.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					// si el motivo de la seleccion ha sido el check
					if (event.detail == SWT.CHECK) {
						TreeItem item = (TreeItem) event.item;
						boolean checked = item.getChecked();
						if (!checked) {
							checkItems(item, checked);
						}
						checkPath(item.getParentItem(), checked, false);
					}
				}

			});

			// RELACION: se cambia check
			this.group2 = new Group(this.composite1, SWT.NONE);
			FillLayout group2Layout = new FillLayout(
					org.eclipse.swt.SWT.HORIZONTAL);
			this.group2.setLayout(group2Layout);
			this.group2.setText("Relations");
			this.tableIndexCreate = new Table(this.group2, SWT.CHECK
					| SWT.V_SCROLL);
			group2.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent evt) {
					group2PaintControl(evt);
				}
			});

			this.addRelationships(this.tableIndexCreate, this.listaCombosCreate);

			this.composite1.layout();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Nombre: crearObtener Funcion:
	 */
	private void crearObtener() {
		this.composite2 = new Composite(this.tabFolder1, SWT.NONE);
		FillLayout composite2Layout = new FillLayout(
				org.eclipse.swt.SWT.HORIZONTAL);

		composite2Layout.spacing = 10;
		composite2Layout.marginWidth = 5;
		composite2Layout.marginHeight = 5;

		this.composite2.setLayout(composite2Layout);
		tabItem2.setControl(this.composite2);

		this.group3 = new Group(this.composite2, SWT.NONE);
		FillLayout group3Layout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		this.group3.setLayout(group3Layout);
		this.group3.setText("SiteViews-Areas");

		this.arbolRead = new Tree(this.group3, SWT.MULTI | SWT.CHECK
				| SWT.BORDER);
		// SINGLE, MULTI, CHECK, FULL_SELECTION, FULL_SELECTION SWT.VIRTUAL |
		// SWT.BORDER)
		listaSiteAreaToArbol(this.arbolRead);

		arbolRead.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// si el motivo de la seleccion ha sido el check
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					boolean checked = item.getChecked();
					if (!checked) {
						checkItems(item, checked);
					}
					checkPath(item.getParentItem(), checked, false);
				}
			}

		});



		this.group4 = new Group(this.composite2, SWT.NONE);
		GridLayout groupArbolLayout = new GridLayout();
		groupArbolLayout.numColumns = 1;
		this.group4.setLayout(groupArbolLayout);
		this.group4.setText("Power Index Unit");

		this.tableIndexRead = new Table(this.group4, SWT.CHECK | SWT.V_SCROLL);


		GridData gridData = new GridData(155, 120);
		tableIndexRead.setLayoutData(gridData);



		Button buttonSelectPower = new Button(this.group4, SWT.PUSH);
		buttonSelectPower.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectPower = new GridData(GridData.END,
				GridData.CENTER, false, false);
		gridDataButtonSelectPower.horizontalSpan = 3;
		buttonSelectPower.setLayoutData(gridDataButtonSelectPower);
		buttonSelectPower.setSelection(Boolean.FALSE);

		buttonSelectPower.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableIndexRead && null != tableIndexRead.getItems()
						&& tableIndexRead.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableIndexRead.getItems().length; i++) {
						if (tableIndexRead.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableIndexRead.getItems().length; i++) {
							tableIndexRead.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableIndexRead.getItems().length; i++) {
							tableIndexRead.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.group4.setVisible(true);

		// fin boton select all y deselect all


		this.group5 = new Group(this.composite2, SWT.NONE);
		GridLayout groupArbolLayoutData = new GridLayout();
		groupArbolLayoutData.numColumns = 1;
		this.group5.setLayout(groupArbolLayoutData);
		this.group5.setText("Data Unit");

		this.tableDataRead = new Table(this.group5, SWT.CHECK | SWT.V_SCROLL);


		GridData gridDataRead = new GridData(155, 120);
		tableDataRead.setLayoutData(gridDataRead);


		// boton select all y deselect all
		Button buttonSelectData = new Button(this.group5, SWT.PUSH);
		buttonSelectData.setText("(Select/Deselect) All");
		GridData gridDatabuttonSelectData = new GridData(GridData.END,
				GridData.CENTER, false, false);
		gridDatabuttonSelectData.horizontalSpan = 3;
		buttonSelectData.setLayoutData(gridDatabuttonSelectData);
		buttonSelectData.setSelection(Boolean.FALSE);

		buttonSelectData.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableDataRead && null != tableDataRead.getItems()
						&& tableDataRead.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableDataRead.getItems().length; i++) {
						if (tableDataRead.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableDataRead.getItems().length; i++) {
							tableDataRead.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableDataRead.getItems().length; i++) {
							tableDataRead.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.group5.setVisible(true);

		// fin boton select all y deselect all

		this.addAttributes(this.tableDataRead, this.checkDataRead, Boolean.TRUE);
		this.addAttributes(this.tableIndexRead, this.checkIndexRead,
				Boolean.FALSE);

		this.composite2.layout();
	}

	/**
	 * 
	 * Nombre: crearTabs Funcion:
	 */
	private void crearTabs() {
		FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		this.container.setLayout(thisLayout);
		this.tabFolder1 = new TabFolder(this.container, SWT.NONE);
		this.tabItem0 = new TabItem(this.tabFolder1, SWT.NONE);
		this.tabItem0.setText("All In One");
		this.tabItem1 = new TabItem(this.tabFolder1, SWT.NONE);
		this.tabItem1.setText("Create");
		this.tabItem2 = new TabItem(this.tabFolder1, SWT.NONE);
		this.tabItem2.setText("Read");
		this.tabItem3 = new TabItem(this.tabFolder1, SWT.NONE);
		this.tabItem3.setText("Update");
		this.tabItem4 = new TabItem(this.tabFolder1, SWT.NONE);
		this.tabItem4.setText("Delete");
		this.tabFolder1.setSelection(0);
		this.container.layout();

	}

	/**
	 * 
	 */
	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NULL);
		this.container.setLayout(null);
		this.crearTabs();
		setControl(this.container);
		if (this.entidad != null)
			this.initialize();
	}

	/**
	 * 
	 * Nombre: getAttributesDataCreate Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesDataCreate() {
		return this.listaAtributosSinDerivados;
	}

	/**
	 * 
	 * Nombre: getAttributesDataRead Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesDataRead() {

		// crear hash de atributos
		HashMap<String, IAttribute> hashLista = new HashMap<String, IAttribute>();
		// cargar hash
		for (int i = 0; i < this.listaAtributos.size(); i++) {
			if (!hashLista.containsKey(Utilities.getAttribute(
					this.listaAtributos.get(i), "name"))) {
				hashLista.put(Utilities.getAttribute(
						this.listaAtributos.get(i), "name"),
						this.listaAtributos.get(i));
			}
		}

		List<IAttribute> lista = new ArrayList<IAttribute>();
		if (null != this.tableDataRead && null != this.tableDataRead.getItems()
				&& this.tableDataRead.getItems().length > 0) {
			for (int i = 0; i < this.tableDataRead.getItems().length; i++) {
				if (this.tableDataRead.getItem(i).getChecked()) {
					String name = (null == this.tableDataRead.getItem(i)
							.getText() ? null : this.tableDataRead.getItem(i)
							.getText().split(" \\(")[0]);
					if (null != name && hashLista.containsKey(name)) {
						lista.add(hashLista.get(name));
					}
				}
			}
		}

		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesDataAllInOne Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesDataAllInOne() {

		// crear hash de atributos
		HashMap<String, IAttribute> hashLista = new HashMap<String, IAttribute>();
		// cargar hash
		for (int i = 0; i < this.listaAtributos.size(); i++) {
			if (!hashLista.containsKey(Utilities.getAttribute(
					this.listaAtributos.get(i), "name"))) {
				hashLista.put(Utilities.getAttribute(
						this.listaAtributos.get(i), "name"),
						this.listaAtributos.get(i));
			}
		}

		List<IAttribute> lista = new ArrayList<IAttribute>();
		if (null != this.tableDataAllInOne
				&& null != this.tableDataAllInOne.getItems()
				&& this.tableDataAllInOne.getItems().length > 0) {
			for (int i = 0; i < this.tableDataAllInOne.getItems().length; i++) {
				if (this.tableDataAllInOne.getItem(i).getChecked()) {
					// el text esta compuesto por name + espacio + ( + id +):
					// obtener el name
					String name = (null == this.tableDataAllInOne.getItem(i)
							.getText() ? null : this.tableDataAllInOne
							.getItem(i).getText().split(" \\(")[0]);
					if (null != name && hashLista.containsKey(name)) {
						lista.add(hashLista.get(name));
					}
				}
			}
		}

		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesIndexDelete Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesIndexDelete() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributos.size(); i++) {
			if (this.tableIndexDelete.getItem(i).getChecked())
				lista.add(this.listaAtributos.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesIndexRead Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesIndexRead() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributos.size(); i++) {
			if (this.tableIndexRead.getItem(i).getChecked())
				lista.add(this.listaAtributos.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesShowUpdate Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesShowUpdate() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributos.size(); i++) {
			if (this.tableShowUpdate.getItem(i).getChecked())
				lista.add(this.listaAtributos.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: getAttributesUpdate Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesUpdate() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributosSinDerivados.size(); i++) {
			if (this.tableIndexUpdate.getItem(i).getChecked())
				lista.add(this.listaAtributosSinDerivados.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: getBuscadorDelete Funcion:
	 * 
	 * @return
	 */
	public IAttribute getBuscadorDelete() {
		if (this.tableOpcionesDelete.getItem(0).getChecked())
			return this.listaAtributos.get(this.checkOpcionesDelete.get(0)
					.getSelectionIndex());
		else
			return null;
	}

	/**
	 * 
	 * Nombre: getBuscadorRead Funcion:
	 * 
	 * @return
	 */
	public IAttribute getBuscadorRead() {
		if (this.tableOpcionesRead.getItem(0).getChecked())
			return this.listaAtributos.get(this.checkOpcionesRead.get(0)
					.getSelectionIndex());
		else
			return null;
	}

	/**
	 * 
	 * Nombre: getBuscadorUpdate Funcion:
	 * 
	 * @return
	 */
	public IAttribute getBuscadorUpdate() {
		if (this.tableOpcionesUpdate.getItem(0).getChecked())
			return this.listaAtributos.get(this.checkOpcionesUpdate.get(0)
					.getSelectionIndex());
		else
			return null;
	}

	/*
	 * @Override public IWizardPage getPreviousPage() { return null; }
	 */

	/**
	 * 
	 */
	public Map<IRelationshipRole, IAttribute> getRelationShipsCreate() {
		Map<IRelationshipRole, IAttribute> mapaRelaciones = new HashMap<IRelationshipRole, IAttribute>();
		try {
			String key;
			for (int i = 0; i < this.listaCombosCreate.size(); i++) {
				if (this.tableIndexCreate.getItems()[i].getChecked()) {
					// this.tableIndexCreate.getItems()[i].checked
					key = this.listaCombosCreate.get(i).getItem(
							this.listaCombosCreate.get(i).getSelectionIndex())
							+ " ("
							+ Utilities.getAttribute(
									this.entidadesRelacionadas.get(i), "name")
							+ ")";
					mapaRelaciones.put(this.entidadesRelacionadas.get(i),
							this.atributosRelacion.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapaRelaciones;
	}

	/**
	 * 
	 * Nombre: getRelationShipsAllInOne Funcion:
	 * 
	 * @return
	 */
	public Map<IRelationshipRole, IAttribute> getRelationShipsAllInOne() {
		Map<IRelationshipRole, IAttribute> mapaRelaciones = new HashMap<IRelationshipRole, IAttribute>();
		try {
			String key;
			for (int i = 0; i < this.listaCombosAllInOne.size(); i++) {
				// RELATION 1.15: solo meter la de aquellos que se
				// hayan seleccionado
				if (this.tableFormAllInOne.getItems()[i].getChecked()) {
					key = this.listaCombosAllInOne.get(i)
							.getItem(
									this.listaCombosAllInOne.get(i)
											.getSelectionIndex())
							+ " ("
							+ Utilities.getAttribute(
									this.entidadesRelacionadas.get(i), "name")
							+ ")";
					mapaRelaciones.put(this.entidadesRelacionadas.get(i),
							this.atributosRelacion.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapaRelaciones;
	}

	/**
	 * 
	 * Nombre: getRelationShipsUpdate Funcion:
	 * 
	 * @return
	 */
	public Map<IRelationshipRole, IAttribute> getRelationShipsUpdate() {

		Map<IRelationshipRole, IAttribute> mapaRelaciones = new HashMap<IRelationshipRole, IAttribute>();
		try {
			String key;
			for (int i = 0; i < this.listaCombosUpdate.size(); i++) {
				if (this.tableIndexUpdate.getItem(
						this.listaAtributosSinDerivados.size() + i)
						.getChecked()) {
					key = this.listaCombosUpdate.get(i).getItem(
							this.listaCombosUpdate.get(i).getSelectionIndex())
							+ " ("
							+ Utilities.getAttribute(
									this.entidadesRelacionadas.get(i), "name")
							+ ")";
					mapaRelaciones.put(this.entidadesRelacionadas.get(i),
							this.atributosRelacion.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapaRelaciones;
	}

	/**
	 * 
	 * Nombre: getSiteViews Funcion:
	 * 
	 * @param tipoOperacion
	 * @return
	 */
	public List<ISiteView> getSiteViews(String tipoOperacion) {

		// obtener solamente los checkeados
		TreeItem[] arrSiteViewSelected = null;

		if ("RETRIEVE".compareTo(tipoOperacion) == 0) {
			arrSiteViewSelected = this.arbolRead.getItems();
		}
		if ("UPDATE".compareTo(tipoOperacion) == 0) {
			arrSiteViewSelected = this.arbolUpdate.getItems();
		}
		if ("DELETE".compareTo(tipoOperacion) == 0) {
			arrSiteViewSelected = this.arbolDelete.getItems();
		}
		if ("CREATE".compareTo(tipoOperacion) == 0) {
			arrSiteViewSelected = this.arbolCreate.getItems();
		}
		if ("ALLINONE".compareTo(tipoOperacion) == 0) {
			arrSiteViewSelected = this.arbolAllInOne.getItems();
		}

		List<ISiteView> lista = new ArrayList<ISiteView>();
		if (null != arrSiteViewSelected && arrSiteViewSelected.length > 0) {
			for (int i = 0; i < arrSiteViewSelected.length; i++) {
				if (arrSiteViewSelected[i].getChecked()) {
					for (int j = 0; j < this.listaSiteViews.size(); j++) {
						ISiteView siteView = this.listaSiteViews.get(j);

						String valorCompleto = Utilities.getAttribute(siteView,
								"name") + " (" + siteView.getFinalId() + ")";
						String valorNameMasEspacio = Utilities.getAttribute(
								siteView, "name") + " ";
						if (arrSiteViewSelected[i].getText().compareTo(
								valorCompleto) == 0
								|| valorNameMasEspacio
										.compareTo(arrSiteViewSelected[i]
												.getText() + " ") == 0) {
							lista.add(this.listaSiteViews.get(j));
						}

					}

				}

			}
		}

		return lista;
	}

	/**
	 * 
	 * 
	 * Nombre: buscarElementoSiteView Funcion:
	 * 
	 * @param nombre
	 * @return
	 */
	public ISiteView buscarElementoSiteView(String nombre) {

		for (int j = 0; j < this.listaSiteViews.size(); j++) {
			ISiteView siteView = this.listaSiteViews.get(j);
			String valorCompleto = Utilities.getAttribute(siteView, "name")
					+ " (" + siteView.getFinalId() + ")";
			// 2 (sv11)
			if (nombre.compareTo(valorCompleto) == 0
					|| valorCompleto.startsWith(nombre + " ")) {
				return siteView;
			}
		}

		return null;
	}

	/**
	 * 
	 */
	public IArea buscarElementoAreaRecursivo(List<IArea> listArea, String nombre) {

		if (null != listArea && listArea.size() > 0) {
			for (Iterator iterator = listArea.iterator(); iterator.hasNext();) {
				IArea area = (IArea) iterator.next();

				String valorCompleto = Utilities.getAttribute(area, "name")
						+ " (" + area.getFinalId() + ")";
				// 2 (sv11)
				if (nombre.compareTo(valorCompleto) == 0
						|| valorCompleto.startsWith(nombre + " ")) {
					// if(nombre.contains(valor)){
					return area;
				} else {
					if (null != area.getAreaList()
							&& area.getAreaList().size() > 0) {
						IArea areabuscar = buscarElementoAreaRecursivo(
								area.getAreaList(), nombre);
						if (null != areabuscar) {
							return areabuscar;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * Nombre: buscarElementoArea Funcion:
	 * 
	 * @param nombre
	 * @return
	 */

	public IArea buscarElementoArea(String nombre) {

		IArea areaEnc = null;
		for (int j = 0; j < this.listaSiteViews.size(); j++) {
			ISiteView siteView = this.listaSiteViews.get(j);

			if (null != siteView.getAreaList()
					&& siteView.getAreaList().size() > 0) {

				areaEnc = buscarElementoAreaRecursivo(siteView.getAreaList(),
						nombre);
				if (null != areaEnc) {
					return areaEnc;
				}
			}

		}
		return null;
	}

	/**
	 * 
	 * Nombre: obtenerHijosCheckeados Funcion:
	 * 
	 * @param retColItemSelEhijos
	 * @param arrItemRecorrer
	 */
	private void obtenerHijosCheckeados(
			Collection<TreeItem> retColItemSelEhijos, TreeItem[] arrItemRecorrer) {

		if (null != arrItemRecorrer && arrItemRecorrer.length > 0) {

			for (int i = 0; i < arrItemRecorrer.length; i++) {
				// primero selecciona que el nodo este checkeado
				if (null != arrItemRecorrer[i]
						&& arrItemRecorrer[i].getChecked()) {
					if (null != arrItemRecorrer[i].getData()
							&& ((ObjStViewArea) arrItemRecorrer[i].getData())
									.getTipo().equals("STVIEW")
							&& null != arrItemRecorrer[i].getItems()) {
						obtenerHijosCheckeados(retColItemSelEhijos,
								arrItemRecorrer[i].getItems());
					} else {

						// segundo comprueba si es de tipo area y no tiene hijos
						if (null != arrItemRecorrer[i].getData()
								&& ((ObjStViewArea) arrItemRecorrer[i]
										.getData()).getTipo().equals("AREA")
								&& null == arrItemRecorrer[i].getItems()) {
	
							retColItemSelEhijos.add(arrItemRecorrer[i]);

						}

						// tercero comprueba si es de tipo area y ninguno de sus
						// hijos siguientes tiene checkeado
						if (null != arrItemRecorrer[i].getData()
								&& ((ObjStViewArea) arrItemRecorrer[i]
										.getData()).getTipo().equals("AREA")
								&& null != arrItemRecorrer[i].getItems()) {

							int contador = 0;
							for (int j = 0; j < arrItemRecorrer[i].getItems().length; j++) {
								if (null != arrItemRecorrer[i].getItems()[j]
										&& arrItemRecorrer[i].getItems()[j]
												.getChecked()) {
									contador++;
									obtenerHijosCheckeados(retColItemSelEhijos,
											arrItemRecorrer[i].getItems());
									break;
								}
							}

							if (contador == 0) {
								retColItemSelEhijos.add(arrItemRecorrer[i]);
							}

						}
					}

				}

			}

		}

	}

	/**
	 * 
	 * Nombre: getAreasRetrieve Funcion:
	 * 
	 * @return
	 */
	public List<IArea> getAreas(String tipoOperacion) {

		List<IArea> lista = new ArrayList<IArea>();
		Collection<TreeItem> retColItemSelEhijos = new ArrayList<TreeItem>();

		if ("RETRIEVE".compareTo(tipoOperacion) == 0) {
			obtenerHijosCheckeados(retColItemSelEhijos,
					this.arbolRead.getItems());
		}
		if ("UPDATE".compareTo(tipoOperacion) == 0) {
			obtenerHijosCheckeados(retColItemSelEhijos,
					this.arbolUpdate.getItems());
		}
		if ("DELETE".compareTo(tipoOperacion) == 0) {
			obtenerHijosCheckeados(retColItemSelEhijos,
					this.arbolDelete.getItems());
		}
		if ("CREATE".compareTo(tipoOperacion) == 0) {
			obtenerHijosCheckeados(retColItemSelEhijos,
					this.arbolCreate.getItems());
		}
		if ("ALLINONE".compareTo(tipoOperacion) == 0) {
			obtenerHijosCheckeados(retColItemSelEhijos,
					this.arbolAllInOne.getItems());
		}

		if (null != retColItemSelEhijos) {
			for (Iterator iterator = retColItemSelEhijos.iterator(); iterator
					.hasNext();) {
				TreeItem treeItem = (TreeItem) iterator.next();

				IArea area = buscarElementoArea(((ObjStViewArea) treeItem
						.getData()).getNombre());
				if (null != area) {
					lista.add(area);
				}
			}
		}
		return lista;

	}

	/**
	 * 
	 * Nombre: group2PaintControl Funcion:
	 * 
	 * @param evt
	 */
	private void group2PaintControl(PaintEvent evt) {
		int tamanio = (this.group2.getSize().x - 10) / 2;
		TableColumn[] columns = this.tableIndexCreate.getColumns();
		for (int i = 0; i < 2; i++) {
			columns[i].setWidth(tamanio);
		}
	}

	/**
	 * 
	 * Nombre: group7PaintControl Funcion:
	 * 
	 * @param evt
	 */
	private void group7PaintControl(PaintEvent evt) {
		int tamanio = (this.group7.getSize().x - 10) / 2;
		TableColumn[] columns = this.tableIndexUpdate.getColumns();
		for (int i = 0; i < 2; i++) {
			columns[i].setWidth(tamanio);
		}
	}

	/**
	 * 
	 * Nombre: group13PaintControl Funcion:
	 * 
	 * @param evt
	 */
	private void group13PaintControl(PaintEvent evt) {
		int tamanio = (this.group13.getSize().x - 10) / 2;
		TableColumn[] columns = this.tableFormAllInOne.getColumns();
		for (int i = 0; i < 2; i++) {
			columns[i].setWidth(tamanio);
		}
	}

	/**
	 * 
	 * Nombre: actualizarListaSiteViews Funcion:
	 * 
	 * @throws ExecutionException
	 */
	public void actualizarListaSiteViews() throws ExecutionException {
		ProjectParameters.init();
		ProjectParameters.initSiteViews();
		this.listaSiteViews = ProjectParameters.getWebModel().getSiteViewList();
	}

	/**
	 * 
	 * Nombre: initialize Funcion:
	 */
	public void initialize() {
		try {
			if (this.entidad == null) {
				this.pageSelectEntity = (WizardSelectEntityPage) this
						.getWizard().getStartingPage();

				this.entidad = (IEntity) this.pageSelectEntity
						.getSelectedElement();
			}

			declaracionEstructuras(entidad);

			this.initRelationShips();
			//  De aqui se obtienen los atributos de la entidad
			this.listaAtributos = this.entidad.getAllAttributeList();
			Iterator<IAttribute> iteratorAtributos = this.listaAtributos
					.iterator();
			IAttribute atributo;
			while (iteratorAtributos.hasNext()) {
				atributo = iteratorAtributos.next();
				if (Utilities.getAttribute(atributo, "derivationQuery").equals(
						"")
						&& !Utilities.getAttribute(atributo, "key").equals(
								"true")) {
					this.listaAtributosSinDerivados.add(atributo);
				}
			}
			this.listaSiteViews = ProjectParameters.getWebModel()
					.getSiteViewList();

			this.crearAllInOne();
			this.crearCrear();
			this.crearObtener();
			this.crearActualizar();
			this.crearBorrado();
			try {
				this.dispose();
				this.finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Nombre: initRelationShips Funcion:
	 */
	private void initRelationShips() {
		List<IRelationship> lista = this.entidad.getOutgoingRelationshipList();
		lista.addAll(this.entidad.getIncomingRelationshipList());
		Iterator<IRelationship> iteratorRelacion = lista.iterator();
		IRelationship relacion;
		IRelationshipRole role1, role2;
		String maxCard;
		this.entidadesRelacionadas = new ArrayList<IRelationshipRole>();
		while (iteratorRelacion.hasNext()) {
			relacion = iteratorRelacion.next();
			if (relacion.getSourceEntity() == this.entidad) {
				role1 = relacion.getRelationshipRole1();
				maxCard = Utilities.getAttribute(role1, "maxCard");
				if (maxCard.equals("1")) {
					this.entidadesRelacionadas.add(role1);
				} else {
					role2 = relacion.getRelationshipRole2();
					maxCard = Utilities.getAttribute(role2, "maxCard");
					if (maxCard.equals("N")) {
						this.entidadesRelacionadas.add(role1);
					}
				}
			} else {
				role1 = relacion.getRelationshipRole2();
				maxCard = Utilities.getAttribute(role1, "maxCard");
				if (maxCard.equals("1")) {
					this.entidadesRelacionadas.add(role1);
				} else {
					role2 = relacion.getRelationshipRole1();
					maxCard = Utilities.getAttribute(role2, "maxCard");
					if (maxCard.equals("N")) {
						this.entidadesRelacionadas.add(role1);
					}
				}
			}
		}
	}

	/**
	 * 
	 * Nombre: setEntity Funcion:
	 * 
	 * @param entidad
	 */
	public void setEntity(IEntity entidad) {
		this.entidad = entidad;
	}

	/**
	 * ALLINONE
	 */
	private void crearAllInOne() {
		this.composite5 = new Composite(this.tabFolder1, SWT.NONE);
		FillLayout composite5Layout = new FillLayout(
				org.eclipse.swt.SWT.HORIZONTAL);

		composite5Layout.marginHeight = 5;
		composite5Layout.marginWidth = 5;
		composite5Layout.spacing = 10;

		this.composite5.setLayout(composite5Layout);
		this.tabItem0.setControl(this.composite5);

		this.group11 = new Group(this.composite5, SWT.NONE);
		FillLayout group11Layout = new FillLayout(
				org.eclipse.swt.SWT.HORIZONTAL);
		this.group11.setLayout(group11Layout);
		this.group11.setText("SiteViews-Areas");

		this.arbolAllInOne = new Tree(this.group11, SWT.MULTI | SWT.CHECK
				| SWT.BORDER);
		// SINGLE, MULTI, CHECK, FULL_SELECTION, FULL_SELECTION SWT.VIRTUAL |
		// SWT.BORDER)

		listaSiteAreaToArbol(this.arbolAllInOne);

		arbolAllInOne.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// S el motivo de la seleccion ha sido el check
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					boolean checked = item.getChecked();
					if (!checked) {
						checkItems(item, checked);
					}
					checkPath(item.getParentItem(), checked, false);
				}
			}

		});


		this.group12 = new Group(this.composite5, SWT.NONE);
		GridLayout groupArbolLayoutIndex = new GridLayout();
		groupArbolLayoutIndex.numColumns = 1;
		this.group12.setLayout(groupArbolLayoutIndex);
		this.group12.setText("Power Index Unit");

		this.tableIndexAllInOne = new Table(this.group12, SWT.CHECK
				| SWT.V_SCROLL);


		GridData gridDataIndex = new GridData(120, 120);
		tableIndexAllInOne.setLayoutData(gridDataIndex);


		// boton select all y deselect all
		Button buttonSelectPower = new Button(this.group12, SWT.PUSH);
		buttonSelectPower.setText("(Select/Deselect) All");
		GridData gridDataButtonSelectPower = new GridData(GridData.END,
				GridData.CENTER, false, false);
		gridDataButtonSelectPower.horizontalSpan = 3;
		buttonSelectPower.setLayoutData(gridDataButtonSelectPower);
		buttonSelectPower.setSelection(Boolean.FALSE);

		buttonSelectPower.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableIndexAllInOne
						&& null != tableIndexAllInOne.getItems()
						&& tableIndexAllInOne.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableIndexAllInOne.getItems().length; i++) {
						if (tableIndexAllInOne.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableIndexAllInOne.getItems().length; i++) {
							tableIndexAllInOne.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableIndexAllInOne.getItems().length; i++) {
							tableIndexAllInOne.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.group12.setVisible(true);

		// fin boton select all y deselect all

		this.group13 = new Group(this.composite5, SWT.NONE);
		FillLayout group13Layout = new FillLayout(
				org.eclipse.swt.SWT.HORIZONTAL);
		this.group13.setLayout(group13Layout);
		this.group13.setText("Relations");
		// Se ha cambiado de NONE a CHECK
		this.tableFormAllInOne = new Table(this.group13, SWT.CHECK
				| SWT.V_SCROLL);

		group13.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent evt) {
				group13PaintControl(evt);
			}
		});

		// Añadir Data Unit: da opcion a elegir campos que aparecen en
		// el View

		this.group14 = new Group(this.composite5, SWT.NONE);
		GridLayout groupArbolLayoutData = new GridLayout();
		groupArbolLayoutData.numColumns = 1;
		this.group14.setLayout(groupArbolLayoutData);
		this.group14.setText("Data Unit");

		this.tableDataAllInOne = new Table(this.group14, SWT.CHECK
				| SWT.V_SCROLL);


		GridData gridData = new GridData(120, 120);
		tableDataAllInOne.setLayoutData(gridData);


		// boton select all y deselect all
		Button buttonSelectData = new Button(this.group14, SWT.PUSH);
		buttonSelectData.setText("(Select/Deselect) All");
		GridData gridDatabuttonSelectData = new GridData(GridData.END,
				GridData.CENTER, false, false);
		gridDatabuttonSelectData.horizontalSpan = 3;
		buttonSelectData.setLayoutData(gridDatabuttonSelectData);
		buttonSelectData.setSelection(Boolean.FALSE);

		buttonSelectData.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (null != tableDataAllInOne
						&& null != tableDataAllInOne.getItems()
						&& tableDataAllInOne.getItems().length > 0) {
					Boolean hayCheckeados = Boolean.FALSE;

					for (int i = 0; i < tableDataAllInOne.getItems().length; i++) {
						if (tableDataAllInOne.getItems()[i].getChecked()) {
							hayCheckeados = Boolean.TRUE;
						}
					}

					if (hayCheckeados) {
						// si hay elementos seleccionados: deselecciono all
						// tableIndexRead.deselectAll();
						for (int i = 0; i < tableDataAllInOne.getItems().length; i++) {
							tableDataAllInOne.getItems()[i].setChecked(false);

						}
					} else {
						// si no hay elementos seleccionados: selecciono all
						// tableIndexRead.selectAll();
						for (int i = 0; i < tableDataAllInOne.getItems().length; i++) {
							tableDataAllInOne.getItems()[i].setChecked(true);
						}
					}
				}
			}
		});
		this.group14.setVisible(true);

		this.addAttributes(this.tableIndexAllInOne, this.checkFormAllInOne,
				Boolean.FALSE);
		this.addRelationships(this.tableFormAllInOne, this.listaCombosAllInOne);
		// Añadir Data Unit: da opcion a elegir campos que aparecen en el View
		this.addAttributes(this.tableDataAllInOne, this.checkDataAllInOne,
				Boolean.TRUE);

		this.composite5.layout();
	}

	private void aniadirElementoToArbol(Tree nodoArbolPadre,
			TreeItem nodoItemPadre, List<ObjStViewArea> listaElementosPagePrevia) {

		if (null != listaElementosPagePrevia
				&& listaElementosPagePrevia.size() > 0) {
			for (Iterator iterator = listaElementosPagePrevia.iterator(); iterator
					.hasNext();) {
				ObjStViewArea objStViewArea = (ObjStViewArea) iterator.next();
				TreeItem item0 = null;
				if (objStViewArea.getTipo().equals("AREA")) {
					item0 = new TreeItem(nodoItemPadre, 0);
				} else {
					item0 = new TreeItem(nodoArbolPadre, 0);
				}
				item0.setText(objStViewArea.getNombre());
				item0.setData(objStViewArea);

				aniadirElementoToArbol(nodoArbolPadre, item0,
						objStViewArea.getListHijos());
			}
		}

	}

	/**
	 * 
	 * Nombre: listaSiteAreaToArbol Funcion:
	 * 
	 * @param arbol
	 */
	private void listaSiteAreaToArbol(Tree arbol) {
		if (null != ProjectParameters.getlistaSiteViewArea()
				&& ProjectParameters.getlistaSiteViewArea().size() > 0) {

			aniadirElementoToArbol(arbol, null,
					ProjectParameters.getlistaSiteViewArea());
		}
	}

	/**
	 * 
	 * Nombre: getAttributesIndexAllInOne Funcion:
	 * 
	 * @return
	 */
	public List<IAttribute> getAttributesIndexAllInOne() {
		List<IAttribute> lista = new ArrayList<IAttribute>();
		for (int i = 0; i < this.listaAtributos.size(); i++) {
			if (this.tableIndexAllInOne.getItem(i).getChecked())
				lista.add(this.listaAtributos.get(i));
		}
		return lista;
	}

	/**
	 * 
	 * Nombre: checkPath Funcion:
	 * 
	 * @param item
	 * @param checked
	 * @param grayed
	 */
	static void checkPath(

	TreeItem item, boolean checked, boolean grayed) {
		if (item == null)
			return;
		if (grayed) {
			checked = true;
		} else {
			int index = 0;
			TreeItem[] items = item.getItems();
			while (index < items.length) {
				TreeItem child = items[index];
				if (child.getGrayed() || checked != child.getChecked()) {
					checked = grayed = true;
					break;
				}
				index++;
			}
		}
		item.setChecked(checked);
		item.setGrayed(grayed);
		checkPath(item.getParentItem(), checked, grayed);
	}

	/**
	 * 
	 * Nombre: checkItems Funcion:
	 * 
	 * @param item
	 * @param checked
	 */
	static void checkItems(TreeItem item, boolean checked) {
		item.setGrayed(false);
		item.setChecked(checked);
		TreeItem[] items = item.getItems();
		for (int i = 0; i < items.length; i++) {
			checkItems(items[i], checked);
		}
	}
}