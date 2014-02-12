package com.gabloz.moneytracker.business.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class TipoCuentaBO {

	private static final Logger logger = Logger.getLogger(TipoCuentaBO.class
			.getCanonicalName());

	/**
	 * Crea ó actualiza un tipo cuenta.
	 * 
	 * @param tipoCuenta
	 */
	public void createOrUpdate(TipoCuenta tipoCuenta) {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			logger.log(Level.INFO, "Guardando el tipo de cuenta..");

			pm.makePersistent(tipoCuenta);
		} finally {
			pm.close();
		}

	}

	/**
	 * Retorna un objeto tipoCuenta encontrado ó en caso de no hallarlo, el que
	 * crea de acuerdo a lo que viendo en los parámetros
	 * 
	 * @param TipoCuenta
	 *            los parametros de busqueda
	 * @return un objeto TipoCuenta
	 */
	@SuppressWarnings("unchecked")
	public TipoCuenta createIfNotExistByNombre(TipoCuenta tipoCuenta) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		TipoCuenta tipoCuentaTemp = null, detached = null;
		List<TipoCuenta> tipoCuentaList = null;

		Transaction tx = pm.currentTransaction();

		ObjectState objState = null;

		try {

			tx.begin();

			tipoCuentaList = (List<TipoCuenta>) pm.newQuery(TipoCuenta.class,
					" nombre == '" + tipoCuenta.getNombre() + "' ").execute();

			if (tipoCuentaList.isEmpty()) {
				logger.log(Level.INFO, "El objeto " + tipoCuenta
						+ " no existe, se creara.");
				createOrUpdate(tipoCuenta);
				tipoCuentaTemp = tipoCuenta;
			} else {
				logger.log(Level.INFO, "El objeto " + tipoCuenta
						+ " ya existe, no se creara.");
				tipoCuentaTemp = tipoCuentaList.iterator().next();
			}

			objState = JDOHelper.getObjectState(tipoCuentaTemp);
			logger.log(Level.INFO, "objState 1 = " + objState);

			detached = pm.detachCopy(tipoCuentaTemp);

			objState = JDOHelper.getObjectState(tipoCuentaTemp);
			logger.log(Level.INFO, "objState 2 = " + objState);

			tx.commit();

			objState = JDOHelper.getObjectState(tipoCuentaTemp);
			logger.log(Level.INFO, "objState 3 = " + objState);
		} finally {

			if (tx.isActive()) {
				tx.rollback();
			}

			pm.close();
			objState = JDOHelper.getObjectState(tipoCuentaTemp);
			logger.log(Level.INFO, "objState 4 = " + objState);
		}

		return detached;

	}

	/**
	 * Borra un tipo cuenta.
	 * 
	 * @param tipoCuenta
	 */
	public void delete(TipoCuenta tipoCuenta) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.deletePersistent(tipoCuenta);
		} finally {
			pm.close();
		}

	}

	/**
	 * Busca un tipoCuenta por la clave.
	 * 
	 * @param key
	 */
	public TipoCuenta findByKey(long id) {

		TipoCuenta tipoCuenta, detached = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			tipoCuenta = pm.getObjectById(TipoCuenta.class, id);

			detached = pm.detachCopy(tipoCuenta);

			logger.log(Level.INFO, "Objeto con id " + id + "recuperado : "
					+ tipoCuenta);

		} catch (JDOObjectNotFoundException e) {
			logger.log(Level.INFO, "Objeto con id " + id
					+ " no pudo ser recuperado.");
		} finally {
			pm.close();
		}

		return detached;

	}

	/**
	 * Busca un tipoCuenta por la clave.
	 * 
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public List<TipoCuenta> findAll() {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<TipoCuenta> tipoCuentaList = new ArrayList<TipoCuenta>();
		try {
			String query = "select from " + TipoCuenta.class.getName();
			tipoCuentaList = (List<TipoCuenta>) pm.newQuery(query).execute();

			logger.log(Level.INFO, "Cantidad de objetos recuperados = "
					+ tipoCuentaList.size());
		} finally {
			pm.close();
		}

		return tipoCuentaList;
	}
	
	public String getIdCuenta(String nombreCuenta){
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("TipoCuenta");
		q.setFilter(new Query.FilterPredicate("nombre",
				Query.FilterOperator.EQUAL,
				nombreCuenta));
		
		PreparedQuery pq = ds.prepare(q);
		Entity cuenta = pq.asSingleEntity();
		
		return "" + cuenta.getKey().getId();
	}

}
