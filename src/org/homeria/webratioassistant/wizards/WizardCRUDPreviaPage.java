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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.homeria.webratioassistant.plugin.ObjStViewArea;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ISiteView;


/**
 * @author Carlos Aguado Fuentes
 * @class WizardCRUDPage
 */
/**
 * WizardCRUDPage: Clase que genera los elementos visuales
 * que se muestran en la página que permite seleccionar las 
 * opciones para generar el CRUD
 */


public class WizardCRUDPreviaPage extends WizardPage {


	private IEntity entidad = null;
	private Composite container = null;
	private Group groupArbol;
	private Group groupAlta;
	private Text nombre;
	private Label cadenaErrorValidacion;
	private Button buttonRadioSTV; 
	private Button buttonRadioArea; 
	private Tree arbol;
	private List<ObjStViewArea> listaSiteViewArea=null;
	private Label labElemSeleccionado;
	
	List<ISiteView> listaSiteViewsPreviaPage=null;
	
	TreeItem[] itemSeleccionado = null;
	
	/**
	 * 
	 * @param entity
	 */
	public WizardCRUDPreviaPage(IEntity entity) {
		super("wizardCRUDPreviaPage");
		setTitle("Webratio CRUD");
		setDescription("Configure options.");
		this.entidad = entity;
	}

	
	/**
	 * 
	 */
	public void changeEntity(IEntity entity){
		this.entidad=entity;
	}
	
	public void createControl(Composite parent) {
		
		this.container = new Composite(parent, SWT.NONE);//NULL
		//this.container.setLayout(null);
		FillLayout compositeLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		this.container.setLayout(compositeLayout);
		
		compositeLayout.spacing = 10;
		compositeLayout.marginWidth = 5;
		compositeLayout.marginHeight = 5;
		
		this.groupArbol = new Group(this.container, SWT.NONE);
		//FillLayout groupArbolLayout = new FillLayout(org.eclipse.swt.SWT.VERTICAL);
		GridLayout groupArbolLayout=new GridLayout();
		groupArbolLayout.numColumns = 1;
		
		this.groupArbol.setLayout(groupArbolLayout);
		this.groupArbol.setText("SiteView - Area");
		
		GridLayout gridLayout = new GridLayout();
		
		
		// Construccion del arbol
		arbol= new Tree(this.groupArbol,SWT.SIMPLE);
		//SINGLE, MULTI, CHECK, FULL_SELECTION, FULL_SELECTION SWT.VIRTUAL | SWT.BORDER)
		
		GridData gridData = new GridData(270, 150);
		arbol.setLayoutData(gridData);
		//Necesito dividir la pantalla en:  -SiteView o Area y dentro de cada uno de ellos: nombre y un boton de añadir
		arbol.addListener(SWT.Selection, new Listener() {
		
		//TreeItem[] oldSelection = null;
		public void handleEvent(Event event) {
			
			    Tree tree = (Tree)(event.widget);
		        TreeItem[] arrSelection = tree.getSelection();

		        itemSeleccionado=arrSelection;
		        
		        //añadir un hijo - Se ha puesto como SIMPLE el arbol asi que solo podrá seleccionarse de uno en uno
				
		        buttonRadioArea.setEnabled(Boolean.TRUE);
		        
		        if(null!= itemSeleccionado && null!=itemSeleccionado[0] && null!=itemSeleccionado[0].getText()){
		        	labElemSeleccionado.setText(itemSeleccionado[0].getText());
		        		
		        	//se selecciona el area
		        	buttonRadioSTV.setSelection(false);
		        	buttonRadioArea.setSelection(true);
		        }
	        }
	
	    });
	   
	   arbol.setSize(200, 200);
	   
	   //inicializa la lista de arboles y carga el arbol por primera vez
	    inicializarListaYarbol();
		
	    //GROUP ALTA________________________________________________________________
	    
	    //Parte para añadir elementos al arbol
	    this.groupAlta = new Group(this.container, SWT.NONE);

	    FillLayout groupAltaLayout = new FillLayout(org.eclipse.swt.SWT.VERTICAL);
		this.groupAlta.setLayout(groupAltaLayout);
		this.groupAlta.setText("Alta Elementos Site View o Areas");
		
		Group groupSeleccionarTipo = new Group(this.groupAlta, SWT.NONE);
		groupSeleccionarTipo.setText("Selecciona Tipo:");
		GridLayout groupSeleccionarTipoLayout=new GridLayout(2,false);
		
		buttonRadioSTV = new Button (groupSeleccionarTipo, SWT.RADIO);
		buttonRadioSTV.setText ("SiteView");
		buttonRadioSTV.setSelection (true);
		
		buttonRadioSTV.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		}); 
		
		buttonRadioArea = new Button (groupSeleccionarTipo, SWT.RADIO);
		buttonRadioArea.setText ("Area");
		buttonRadioArea.setSelection (false);
		buttonRadioArea.setEnabled(Boolean.FALSE);
		

		buttonRadioSTV.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(buttonRadioSTV.getSelection()){
					itemSeleccionado=null;
					labElemSeleccionado.setText("");
					arbol.deselectAll();
					groupAlta.setVisible(Boolean.TRUE);
				}
			}
		}); 


		groupSeleccionarTipo.setLayout(groupSeleccionarTipoLayout);
		
		Group groupNombreArea = new Group(this.groupAlta, SWT.NONE);
		groupNombreArea.setText("Nombre elem. seleccionado:");
		GridLayout groupNombreAreaLayout=new GridLayout(1,true);
		labElemSeleccionado=new Label(groupNombreArea, SWT.None);
		labElemSeleccionado.setText("NONE                      ");
		groupNombreArea.setLayout(groupNombreAreaLayout);
		
		
		Group groupIntroducirTexto = new Group(this.groupAlta, SWT.NONE);
		groupIntroducirTexto.setText("Inserta el nombre del Site View/Area:");
		GridLayout groupIntroducirTextoLayout=new GridLayout(2,true);
		groupSeleccionarTipoLayout.horizontalSpacing=150;
		groupSeleccionarTipoLayout.verticalSpacing=3;
		nombre = new Text(groupIntroducirTexto, SWT.NONE);
		nombre.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		groupIntroducirTexto.setLayout(groupIntroducirTextoLayout);
		//Se añade el input para meter el nombre y el boton de alta
		Button buttonAniadir = new Button (groupIntroducirTexto, SWT.PUSH);
		buttonAniadir.setText ("Añadir");
		buttonAniadir.setSelection (Boolean.FALSE);
		buttonAniadir.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		
		Group groupErrorValidacion= new Group(this.groupAlta, SWT.NONE);
		groupErrorValidacion.setText("Mensajes error validacion:");
		groupErrorValidacion.setLayout(new FillLayout());	
		cadenaErrorValidacion = new Label(groupErrorValidacion, SWT.NONE);
		cadenaErrorValidacion.setText("");
		
		this.groupAlta.setVisible(Boolean.TRUE);
		
		buttonAniadir.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
					
					// SiteView se crea en la raiz de la lista
					if(buttonRadioSTV.getSelection()){
						
						// Validaciones: el nombre del Area no puede ser igual a otro ya existente.
						if(validacionesInsertarSiteViewArbol()){
							TreeItem itemSiteView = new TreeItem(arbol, 0);
							itemSiteView.setText(nombre.getText());
							
							arbol.setSelection(itemSiteView); 
							groupAlta.setVisible(Boolean.TRUE); 
							
							// Quedar marcado como seleccionado el nuevo elemento añadido
							TreeItem[] arrSelection = arbol.getSelection();
						    itemSeleccionado=arrSelection;
						    //añadir un hijo - Se ha puesto como SIMPLE el arbol asi que solo podrá seleccionarse de uno en uno
						    buttonRadioArea.setEnabled(Boolean.TRUE);
						    buttonRadioArea.setSelection(Boolean.TRUE);
						    buttonRadioSTV.setSelection(Boolean.FALSE);
						    
						    // Decirle que el area
						    if(null!= itemSeleccionado && null!=itemSeleccionado[0] && null!=itemSeleccionado[0].getText()){
						        labElemSeleccionado.setText(itemSeleccionado[0].getText());
						    }
						    // Quedar marcado como seleccionado el nuevo elemento añadido
							
							if( null==listaSiteViewArea ){
								listaSiteViewArea=new ArrayList<ObjStViewArea>();
							}
							
							ObjStViewArea objStView= new ObjStViewArea();
							//objStView.setNombre( nombre.getText()+ " ("+ siteView.getFinalId() + ")");
							objStView.setNombre( nombre.getText());
							objStView.setTipo("STVIEW");
							objStView.setListHijos(null);
							objStView.setEsNuevo(Boolean.TRUE);
							listaSiteViewArea.add(objStView);
							
							//Limpiar
							nombre.setText("");
							cadenaErrorValidacion.setText("");
						}
					}
					//Area se crea dentro del elemento seleccionado
					else if(buttonRadioArea.getSelection()){
						
						 //Validaciones: el nombre del Area no puede ser igual a otro ya existente.
						 //Validaciones: se debe haber seleccionado previamente un padre "itemSeleccionado"
						 if(validacionesInsertarAreaArbol()){
							//lo inserta en el mapa
							TreeItem item2 = new TreeItem(itemSeleccionado[0], 0);
							item2.setText(nombre.getText());
							arbol.setSelection(item2);
							groupAlta.setVisible(Boolean.TRUE);
							
							//Insertar tb en la lista que se corresponde con el mapa
							ObjStViewArea nuevoObjetoStViewArea= new ObjStViewArea();
							nuevoObjetoStViewArea.setEsNuevo(Boolean.TRUE);
							nuevoObjetoStViewArea.setNombre(nombre.getText());
							nuevoObjetoStViewArea.setTipo("AREA");//sera depende del tipo seleccionado Area o StvView
							 
							aniadirToArbol(listaSiteViewArea,nuevoObjetoStViewArea,itemSeleccionado[0].getText()); 
							 
							
							// Quedar marcado como seleccionado el nuevo elemento añadido
							TreeItem[] arrSelection = arbol.getSelection();
							itemSeleccionado=arrSelection;
							//añadir un hijo - Se ha puesto como SIMPLE el arbol asi que solo podrá seleccionarse de uno en uno
							buttonRadioArea.setEnabled(Boolean.TRUE);
							//decirle que el area
							if(null!= itemSeleccionado && null!=itemSeleccionado[0] && null!=itemSeleccionado[0].getText()){
							    labElemSeleccionado.setText(itemSeleccionado[0].getText());
							}
							// Fin: Quedar marcado como seleccionado el nuevo elemento añadido
							
							//Limpiar
							nombre.setText("");
							//itemSeleccionado=null;
							//labElemSeleccionado.setText("");
							cadenaErrorValidacion.setText("");
						 }
				}
			}
		});

		//Se añade el input para meter el nombre y el boton de alta
		
		Button buttonDeshacer = new Button(this.groupArbol, SWT.PUSH);
		buttonDeshacer.setText("   Deshacer   ");
		GridData gridData2 = new GridData(GridData.END, GridData.CENTER, false, false);
		gridData2.horizontalSpan = 3;
		buttonDeshacer.setLayoutData(gridData2);
		buttonDeshacer.setSelection (Boolean.FALSE);
		this.groupAlta.setVisible(Boolean.TRUE);
		
		buttonDeshacer.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				 //inicializa la lista de arboles y carga el arbol por primera vez
			    inicializarListaYarbol();
			    
			    itemSeleccionado=null;
			    labElemSeleccionado.setText("");
			    buttonRadioArea.setEnabled(Boolean.FALSE);
		
				ProjectParameters.setArbolPaginaAlta(null);
				ProjectParameters.setlistaSiteViewArea(null);
			}
		});
		
		
		//initialize();
		setControl(this.container);
		//this.dispose();
		try{
			this.dispose();
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Nombre: buscarElementoCoincideTextoRecursivo
	 * Funcion: 
	 * @return
	 */
	public Boolean existeElementoCoincideTextoRecursivo(List<ObjStViewArea> lista){

		for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
			ObjStViewArea objStViewArea = (ObjStViewArea) iterator.next();
			String nombreLista=objStViewArea.getNombre();
			String[] primeraParte= nombreLista.split(" \\(");
			if(primeraParte[0].compareTo(nombre.getText())==0){
				return Boolean.TRUE;
			}else{
				if(null!=objStViewArea.getListHijos() && objStViewArea.getListHijos().size()>0){
					if(existeElementoCoincideTextoRecursivo(objStViewArea.getListHijos())){
						return Boolean.TRUE;
					}
				}
			}
		}
		
		return Boolean.FALSE;			
	}
	
	/**
	 * 
	 * Nombre: validacionesInsertarSiteViewArbol
	 * Funcion: 
	 * @return
	 */
	private Boolean validacionesInsertarSiteViewArbol(){
		
		if(null==nombre || (nombre!=null&& nombre.getText().isEmpty())){
			//sacar mensaje por pantalla avisando de que se debe insertar un nombre
			cadenaErrorValidacion.setText("*No se ha indicado nombre");
			return Boolean.FALSE;
		}
		if(existeElementoCoincideTextoRecursivo(listaSiteViewArea)){
			//sacar mensaje por pantalla avisando de que cambie el nombre
			cadenaErrorValidacion.setText("*El nuevo nombre: "+ nombre.getText()+" ya esta en uso.");
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	
	/**
	 * 
	 * Nombre: validacionesInsertarArbol
	 * Funcion: 
	 * @return
	 */
	private Boolean validacionesInsertarAreaArbol(){
		
		if(null==nombre || (nombre!=null&& nombre.getText().isEmpty())){
			//sacar mensaje por pantalla avisando de que se debe insertar un nombre
			cadenaErrorValidacion.setText("*No se ha indicado nombre");
			return Boolean.FALSE;
		}
		if(!(null!=itemSeleccionado&& itemSeleccionado.length>0)){
			//sacar mensaje por pantalla avisando de que seleccione un path
			cadenaErrorValidacion.setText("*No hay elemento padre seleccionado");
			return Boolean.FALSE;
		}
		if(existeElementoCoincideTextoRecursivo(listaSiteViewArea)){
			//sacar mensaje por pantalla avisando de que cambie el nombre
			cadenaErrorValidacion.setText("*El nuevo nombre: "+ nombre.getText()+" ya esta en uso.");
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	/**
	 * 
	 * Nombre: inicializarListaYarbol
	 * Funcion:
	 */
	private void inicializarListaYarbol() {
		this.listaSiteViewsPreviaPage=ProjectParameters.getWebModel().getSiteViewList();
	   
	    //Inicializa elementos del arbol para volver a version de siteView- areas creados
		listaSiteViewArea= new ArrayList();
		
	    arbol.removeAll();
	    arbol.clearAll(Boolean.TRUE);

	    
		if(null!= this.listaSiteViewsPreviaPage && this.listaSiteViewsPreviaPage.size()>0){
			for (Iterator iterator = listaSiteViewsPreviaPage.iterator(); iterator.hasNext();) {
				ISiteView siteView = (ISiteView) iterator.next();
				if(null!=siteView){
					ObjStViewArea objStView= new ObjStViewArea();
					objStView.setNombre(Utilities.getAttribute(siteView, "name") + " ("+ siteView.getFinalId() + ")");
					objStView.setTipo("STVIEW");
					listaSiteViewArea.add(objStView);
					
					TreeItem itemSiteView = new TreeItem(arbol, 0);
		
					itemSiteView.setText(Utilities.getAttribute(siteView, "name") + " ("+ siteView.getFinalId() + ")");
					
					if(null!=siteView.getAreaList() && siteView.getAreaList().size()>0){
						montarArbolAreas(itemSiteView, objStView, siteView.getAreaList());
					}
				}
			}
		}
		
		arbol.redraw();
	}
	
	/**
	 * 
	 * Nombre: añadirToArbol
	 * Funcion:
	 */
	private void aniadirToArbol(List<ObjStViewArea> listaRecorrer,ObjStViewArea nuevoObjetoStViewArea,String padreToBuscar){
		
		if(null != listaRecorrer && listaRecorrer.size()>0){
			for (Iterator iterator = listaRecorrer.iterator(); iterator.hasNext();) {
				ObjStViewArea stViewArea = (ObjStViewArea) iterator.next();
				
				if(stViewArea.getNombre().compareTo(padreToBuscar)==0){
					 if(null!=stViewArea.getListHijos()){
						 stViewArea.getListHijos().add(nuevoObjetoStViewArea);
					 }else{
						 List listaHijos=new ArrayList<ObjStViewArea>();
						 listaHijos.add(nuevoObjetoStViewArea);
						 stViewArea.setListHijos(listaHijos);
					 }
				}else{
					aniadirToArbol(stViewArea.getListHijos(),nuevoObjetoStViewArea,padreToBuscar);
				}
			}
		}
	}
	
	/**Para recorrer una lista de Areas y formar los nodos del padre
	 * 
	 * Nombre: montarArbol
	 * Funcion: 
	 * @param objPadre  --> con esto voy ir aumentando el arbol
	 * @param listaAreasPadre  ---> cone esto voy a ir recorriendo la lista de Areas de cada SiteView, y areas de areas..
	 */
	private void montarArbolAreas(TreeItem itemPadreArbol,ObjStViewArea objPadreArbol,List <IArea> listaAreasPadreRecorrer){
		
			List listaAreaHijo= null;
			if(null!=listaAreasPadreRecorrer && listaAreasPadreRecorrer.size()>0){
				listaAreaHijo=new ArrayList();
				objPadreArbol.setListHijos(listaAreaHijo);
				for (Iterator iterator = listaAreasPadreRecorrer.iterator(); iterator
						.hasNext();) {
					IArea area = (IArea) iterator.next();
					
					if(null!=area){
						ObjStViewArea objArea1= new ObjStViewArea();
						objArea1.setNombre(Utilities.getAttribute(area, "name") + " ("+ area.getFinalId() + ")");
						objArea1.setTipo("AREA");
						listaAreaHijo.add(objArea1);
						
						TreeItem itemHijo = new TreeItem(itemPadreArbol, 0);
						itemHijo.setText(Utilities.getAttribute(area, "name") + " ("+ area.getFinalId() + ")");
						
						arbol.select(itemHijo);
						
						montarArbolAreas(itemHijo,objArea1,area.getAreaList());
					}
				}
				
			}
	}
				
	
	@Override
	public IWizardPage getNextPage() {
		
		// Guardo arbol modificado en projectParameter
		ProjectParameters.setArbolPaginaAlta(arbol);
		ProjectParameters.setlistaSiteViewArea(listaSiteViewArea);
		
		//Cambiado para que vaya a pagina intermedia
		WizardCRUDPage crud = (WizardCRUDPage) this.getWizard().getPage(
				"wizardCRUDPage");
		
		crud.setEntity(this.entidad);
		
		crud.initialize();

		return crud;
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return Boolean.TRUE;
	}
	
	
}