/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */

package org.homeria.webratioassistant.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.homeria.webratioassistant.crud.AllInOne;
import org.homeria.webratioassistant.crud.Create;
import org.homeria.webratioassistant.crud.Delete;
import org.homeria.webratioassistant.crud.Retrieve;
import org.homeria.webratioassistant.crud.Update;
import org.homeria.webratioassistant.plugin.Debug;
import org.homeria.webratioassistant.plugin.EventoNuevaArea;
import org.homeria.webratioassistant.plugin.EventoNuevaSiteView;
import org.homeria.webratioassistant.plugin.ObjStViewArea;
import org.homeria.webratioassistant.plugin.ProjectParameters;

import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ISiteView;

/**
 * @author Carlos Aguado Fuentes
 * @class WizardCRUD
 */
/**
 * WizardCRUD: Clase principal encargada del asistente gráfico
 */
public class WizardCRUD extends Wizard implements INewWizard {
	public static Create c = null;
	private Create create;
	private WizardCRUDPreviaPage crudPreviaPage;
	private WizardCRUDPage crudPage;

	private Delete delete;
	private IEntity entidadSeleccionada;
	private WizardSelectEntityPage pageSelectEntity;
	private Retrieve retrieve;

	private Update update;
	private AllInOne allinone;

	/**
	 * 
	 */
	public WizardCRUD() {
		super();
		setNeedsProgressMonitor(true);
		this.entidadSeleccionada = null;

	}

	/**
	 * 
	 * @param selection
	 */
	public WizardCRUD(IEntity selection) {
		super();
		setNeedsProgressMonitor(true);
		this.entidadSeleccionada = selection;
	}

	/**
	 * 
	 */
	public void addPages() {
		if (this.entidadSeleccionada == null) {
			this.pageSelectEntity = new WizardSelectEntityPage();
			addPage(this.pageSelectEntity);
		}
		// Se añade pagina intermedia
		this.crudPreviaPage = new WizardCRUDPreviaPage(this.entidadSeleccionada);
		addPage(this.crudPreviaPage);

		this.crudPage = new WizardCRUDPage(this.entidadSeleccionada);
		addPage(this.crudPage);

	}

	/**
	 * 
	 */
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == this.crudPage)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * Nombre: doInicial Funcion:
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void doInicial(IProgressMonitor monitor) throws CoreException {
		try {
			// Viene informado con los SiteView y Areas
			if (null != ProjectParameters.getlistaSiteViewArea()
					&& ProjectParameters.getlistaSiteViewArea().size() > 0) {

				List listaSiteViewAreaAlta = ProjectParameters
						.getlistaSiteViewArea();

				for (Iterator iterator = listaSiteViewAreaAlta.iterator(); iterator
						.hasNext();) {
					ObjStViewArea objSiteView = (ObjStViewArea) iterator.next();
					if (null != objSiteView && null != objSiteView.getEsNuevo()
							&& objSiteView.getEsNuevo()) {

						EventoNuevaSiteView crearSite = new EventoNuevaSiteView(
								objSiteView.getNombre());
						crearSite.ejecutar(new SubProgressMonitor(monitor, 1),
								objSiteView.getNombre());
						this.crudPage.actualizarListaSiteViews();
					}

					if (null != objSiteView.getListHijos()
							&& objSiteView.getListHijos().size() > 0) {
						this.crudPage.actualizarListaSiteViews();
						crearAreas(objSiteView.getListHijos(),
								objSiteView.getNombre(),
								objSiteView.getNombre());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			monitor.done();
		}
	}

	/**
	 * 
	 * Nombre: crearAreas Funcion:
	 * 
	 * @param listaObjSiteView
	 * @param nombreSiteView
	 * @param nombrePadre
	 * @throws ExecutionException
	 */
	private void crearAreas(List<ObjStViewArea> listaObjSiteView,
			String nombreSiteView, String nombrePadre)
			throws ExecutionException {

		for (Iterator iterator2 = listaObjSiteView.iterator(); iterator2
				.hasNext();) {

			ObjStViewArea objecthijo = (ObjStViewArea) iterator2.next();
			// Es un area
			if (null != objecthijo && null != objecthijo.getEsNuevo()
					&& objecthijo.getEsNuevo()) {

				// buscar el nodo padre del objeto que vamos a crear, para que
				// lo situe en el
				ISiteView siteView = crudPage
						.buscarElementoSiteView(nombreSiteView);
				if (nombreSiteView.compareTo(nombrePadre) != 0
						&& null != siteView.getAreaList()
						&& siteView.getAreaList().size() > 0) {
					IArea areaEnc = crudPage.buscarElementoAreaRecursivo(
							siteView.getAreaList(), nombrePadre);
					EventoNuevaArea nuevaArea = new EventoNuevaArea(areaEnc,
							150, 150, objecthijo.getNombre());
					nuevaArea.ejecutar();
				} else {
					EventoNuevaArea nuevaArea = new EventoNuevaArea(siteView,
							150, 150, objecthijo.getNombre());
					nuevaArea.ejecutar();
				}

				this.crudPage.actualizarListaSiteViews();
			}

			if (null != objecthijo.getListHijos()
					&& objecthijo.getListHijos().size() > 0) {
				List hijos2 = objecthijo.getListHijos();
				crearAreas(hijos2, nombreSiteView, objecthijo.getNombre());
			}
		}
	}

	/***
	 * 
	 * Nombre: doFinish Funcion:
	 * 
	 * @param a
	 * @param c
	 * @param r
	 * @param u
	 * @param d
	 * @param monitor
	 * @throws CoreException
	 */
	private void doFinish(AllInOne a, Create c, Retrieve r, Update u, Delete d,
			IProgressMonitor monitor) throws CoreException {
		try {

			int numTareas = 0;
			if (this.crudPage.getSiteViews("RETRIEVE").size() > 0)
				numTareas++;
			if (this.crudPage.getSiteViews("CREATE").size() > 0)
				numTareas++;
			if (this.crudPage.getSiteViews("ALLINONE").size() > 0)
				numTareas++;
			if (this.crudPage.getSiteViews("UPDATE").size() > 0)
				numTareas++;
			if (this.crudPage.getSiteViews("DELETE").size() > 0)
				numTareas++;

			monitor.beginTask("Running RETIEVE", numTareas);
			if (this.crudPage.getSiteViews("RETRIEVE").size() > 0)
				r.ejecutar(new SubProgressMonitor(monitor, 1));
			monitor.setTaskName("Running CREATE");
			if (this.crudPage.getSiteViews("CREATE").size() > 0)
				c.ejecutar(new SubProgressMonitor(monitor, 1));
			monitor.setTaskName("Running ALLINONE");
			if (this.crudPage.getSiteViews("ALLINONE").size() > 0)
				a.ejecutar(new SubProgressMonitor(monitor, 1));
			monitor.setTaskName("Running UPDATE");
			if (this.crudPage.getSiteViews("UPDATE").size() > 0)
				u.ejecutar(new SubProgressMonitor(monitor, 1));
			monitor.setTaskName("Running DELETE");
			if (this.crudPage.getSiteViews("DELETE").size() > 0)
				d.ejecutar(new SubProgressMonitor(monitor, 1));

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			monitor.done();
		}
	}

	/**
	 * 
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Debug.setOn();
		try {
			ProjectParameters.init();
			ProjectParameters.initSiteViews();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Debug.println(this.getClass().toString(), e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean performFinish() {
		try {
			final IEntity entidad;
			if (this.entidadSeleccionada == null) {
				entidad = (IEntity) this.pageSelectEntity.getSelectedElement();
			} else {
				entidad = this.entidadSeleccionada;
			}

			// Primera parte parte para la creacion de siteView y que para la
			// segunda parte ya esten todos los SiteView activos
			IRunnableWithProgress op1 = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor2)
						throws InvocationTargetException {
					try {
						doInicial(monitor2);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor2.done();
					}
				}
			};
			try {
				getContainer().run(false, false, op1);
				ProjectParameters.getActiveEditor().doSave(null);
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) {
				Throwable realException = e.getTargetException();
				MessageDialog.openError(getShell(), "Error",
						realException.getMessage());
				e.printStackTrace();
				return false;
			}

			// Resetar los project parameters para que siteView este correcto
			// segun siteView creada en metodo doInicial
			this.crudPage.actualizarListaSiteViews();
			// fin de la primera parte

			// Hay que pasarle SiteViews checkeadas y existentes, hay que pasale
			// Areas checkeadas y ya existentes
			this.create = new Create(entidad,
					this.crudPage.getSiteViews("CREATE"),
					this.crudPage.getAreas("CREATE"),
					this.crudPage.getRelationShipsCreate(),
					this.crudPage.getAttributesDataCreate());
			this.retrieve = new Retrieve(entidad,
					this.crudPage.getSiteViews("RETRIEVE"),
					this.crudPage.getAreas("RETRIEVE"),
					this.crudPage.getAttributesIndexRead(),
					this.crudPage.getAttributesDataRead());
			this.update = new Update(entidad,
					this.crudPage.getSiteViews("UPDATE"),
					this.crudPage.getAreas("UPDATE"),
					this.crudPage.getRelationShipsUpdate(),
					this.crudPage.getAttributesUpdate(),
					this.crudPage.getAttributesShowUpdate());
			this.delete = new Delete(entidad,
					this.crudPage.getSiteViews("DELETE"),
					this.crudPage.getAreas("DELETE"),
					this.crudPage.getAttributesIndexDelete());
			this.allinone = new AllInOne(entidad,
					this.crudPage.getSiteViews("ALLINONE"),
					this.crudPage.getAreas("ALLINONE"),
					this.crudPage.getRelationShipsAllInOne(),
					this.crudPage.getAttributesIndexAllInOne(),
					this.crudPage.getAttributesDataAllInOne());

			final Create c = this.create;
			final Retrieve r = this.retrieve;
			final Update u = this.update;
			final Delete d = this.delete;
			final AllInOne a = this.allinone;
			IRunnableWithProgress op2 = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						doFinish(a, c, r, u, d, monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			};
			try {
				getContainer().run(false, false, op2);

				ProjectParameters.getActiveEditor().doSave(null);
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) {
				Throwable realException = e.getTargetException();
				MessageDialog.openError(getShell(), "Error",
						realException.getMessage());
				e.printStackTrace();
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// finally{

		// }
		return true;
	}

	/**
	 * 
	 */
	public void finalize() {
		try {
			this.dispose();
			// this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
}
