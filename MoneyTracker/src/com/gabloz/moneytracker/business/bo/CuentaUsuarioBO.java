package com.gabloz.moneytracker.business.bo;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.gabloz.moneytracker.business.entity.ContabilidadUsuario;
import com.gabloz.moneytracker.business.entity.CuentaUsuario;
import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class CuentaUsuarioBO {

	private static final Logger logger = Logger.getLogger(CuentaUsuarioBO.class
			.getCanonicalName());

	private TipoCuentaBO tipoCuentaBO = new TipoCuentaBO();
	private ContabilidadUsuarioBO contabilidadUsuarioBO = new ContabilidadUsuarioBO();

	/**
	 * Crea ó actualiza una cuenta de usuario.
	 * 
	 * @param cuentaUsuario
	 */
	public void createOrUpdate(CuentaUsuario cuentaUsuario) {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			logger.log(Level.INFO, "Guardando una cuentaUsuario..");
			pm.makePersistent(cuentaUsuario);
		} finally {
			pm.close();
		}

	}

	/**
	 * Crea ó actualiza una cuenta de usuario.
	 * 
	 * @param cuentaUsuario
	 */
	public CuentaUsuario createIfNotExist(CuentaUsuario cuentaUsuario) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		CuentaUsuario cuentaUsuarioTemp, detached = null;

		Transaction tx = pm.currentTransaction();
		try {

			tx.begin();

			cuentaUsuarioTemp = findByKey(cuentaUsuario.getCuentaUsuarioKey());

			if (cuentaUsuarioTemp == null) {

				TipoCuenta tipoCuenta = tipoCuentaBO.findByKey(cuentaUsuario
						.getTipoCuentaKey().getId());

				cuentaUsuario.setCuentaUsuarioKey(Util.getUsuario().getUserId()
						+ tipoCuenta.getKey().getId());
				
				createOrUpdate(cuentaUsuario);

				cuentaUsuarioTemp = cuentaUsuario;

			}

			detached = pm.detachCopy(cuentaUsuarioTemp);

			tx.commit();
		} finally {

			if (tx.isActive()) {
				tx.rollback();
			}

			pm.close();
		}

		return detached;

	}

	/**
	 * Borra un tipo cuenta.
	 * 
	 * @param tipoCuenta
	 */
	public void delete(CuentaUsuario cuentaUsuario) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.deletePersistent(cuentaUsuario);
		} finally {
			pm.close();
		}

	}

	/**
	 * TODO
	 * 
	 * Busca un CuentaUsuario por la clave.
	 * 
	 * @param key
	 */
	public CuentaUsuario findByKey(Key key) {

		if (key == null) {
			return null;
		}

		CuentaUsuario cuentaUsuario, detached = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			cuentaUsuario = pm.getObjectById(CuentaUsuario.class, key);

			detached = pm.detachCopy(cuentaUsuario);
			
			findReferences(detached);

			logger.log(Level.INFO, "Objeto con id " + key + "recuperado : "
					+ cuentaUsuario);

		} catch (JDOObjectNotFoundException e) {
			logger.log(Level.INFO, "Objeto con id " + key
					+ " no pudo ser recuperado.");
		} finally {
			pm.close();
		}

		return detached;
	}

	/**
	 * Busca todos los objetos con relaciones de no pertencia 1-1 
	 * 
	 * @param cuentaUsuario
	 */
	private void findReferences(CuentaUsuario cuentaUsuario) {
		
		if (cuentaUsuario != null) {
			ContabilidadUsuario contabilidadUsuario = contabilidadUsuarioBO
					.findByKey(cuentaUsuario.getContabilidadUsuarioKey());
			cuentaUsuario.setContabilidadUsuario(contabilidadUsuario);

			if (cuentaUsuario.getTipoCuentaKey() != null) {
				TipoCuenta tipoCuenta = tipoCuentaBO.findByKey(cuentaUsuario
						.getTipoCuentaKey().getId());
				cuentaUsuario.setTipoCuenta(tipoCuenta);
			}

		}
	}

	/**
	 * 
	 * Busca un cuentaUsuario por la clave.
	 * 
	 * @param key
	 */
	public CuentaUsuario findByKey(String keyString) {

		if (keyString == null) {
			return null;
		}

		CuentaUsuario cuentaUsuario, detached = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			cuentaUsuario = pm.getObjectById(CuentaUsuario.class, new String(
					keyString));

			detached = pm.detachCopy(cuentaUsuario);

			findReferences(detached);
			
			logger.log(Level.INFO, "Objeto con id " + keyString
					+ "recuperado : " + cuentaUsuario);

		} catch (JDOObjectNotFoundException e) {
			logger.log(Level.INFO, "Objeto con id " + keyString
					+ " no pudo ser recuperado.");
		} finally {
			pm.close();
		}

		return detached;
	}
	
	public Iterable<Entity> findCuentaUsuarioForContabilidadUsuario
		(Key contabilidadUsuarioKey){
		
		Query q = new Query("CuentaUsuario");
		q.setFilter(
				new Query.FilterPredicate("contabilidadUsuarioKey", 
						Query.FilterOperator.EQUAL, 
						contabilidadUsuarioKey));
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(q);
		
		Iterable<Entity> results = pq.asIterable();
		return results;
	}

}
