package com.gabloz.moneytracker.business.bo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.gabloz.moneytracker.business.entity.ContabilidadUsuario;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class ContabilidadUsuarioBO {

	private static final Logger logger = Logger
			.getLogger(ContabilidadUsuarioBO.class.getCanonicalName());

	/**
	 * Crea ó actualiza una ContabilidadUsuario.
	 * 
	 * @param contabilidadUsuario
	 */
	public void createOrUpdate(ContabilidadUsuario contabilidadUsuario) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {

			logger.log(Level.INFO, "Guardando la contabilidad usuario..");

			pm.makePersistent(contabilidadUsuario);
		} finally {
			pm.close();
		}
	}

	/**
	 * Crea una ContabilidadUsuario si esta no existe.
	 * 
	 * @param contabilidadUsuario
	 */
	public ContabilidadUsuario creatIfNotExistByUser(ContabilidadUsuario contabilidadUsuario) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		ContabilidadUsuario contabilidadUsuarioTemp, detached = null;
		
		try {

			contabilidadUsuarioTemp = findByUsuario(contabilidadUsuario.getNicknameUsr());
			
			if( contabilidadUsuarioTemp == null || contabilidadUsuarioTemp.getKey() == null){
				
				logger.log(Level.INFO, "Usuario "+ contabilidadUsuario.getNicknameUsr() +" no encontrado. Se creara. ");
				
				createOrUpdate(contabilidadUsuario);
				contabilidadUsuarioTemp = findByUsuario(contabilidadUsuario.getNicknameUsr());
			}
			
			detached = pm.detachCopy(contabilidadUsuarioTemp);
			
		} finally {
			pm.close();
		}
		
		return detached;
	}	
	
	/**
	 * Crea una ContabilidadUsuario si esta no existe.
	 * 
	 * @param contabilidadUsuario
	 */
	public ContabilidadUsuario creatIfNotExist(ContabilidadUsuario contabilidadUsuario) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		ContabilidadUsuario contabilidadUsuarioTemp, detached = null;
		
		try {

			contabilidadUsuarioTemp = findByKey(contabilidadUsuario.getKey());
			
			if( contabilidadUsuarioTemp == null ){
				
				createOrUpdate(contabilidadUsuario);
				contabilidadUsuarioTemp = findByKey(contabilidadUsuario.getKey());
			}
			
			detached = pm.detachCopy(contabilidadUsuarioTemp);
				
			
			
			logger.log(Level.INFO, "Guardando la contabilidad usuario..");

			pm.makePersistent(contabilidadUsuario);
		} finally {
			pm.close();
		}
		
		return detached;
	}	
	
	/**
	 * Borra un contabilidadUsuario.
	 * 
	 * @param contabilidadUsuario
	 */
	public void delete(ContabilidadUsuario contabilidadUsuario) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			pm.deletePersistent(contabilidadUsuario);
		} finally {
			pm.close();
		}

	}

	/**
	 * Busca un contabilidadUsuario por la clave.
	 * 
	 * @param key
	 */
	public Entity find( ContabilidadUsuario contabilidadUsuario){
		
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Key userKey = KeyFactory.createKey("UserGroup", contabilidadUsuario.getNicknameUsr());
		Query q = new Query("ContabilidadUsuario").setAncestor(userKey);
		q.setFilter(
				new Query.FilterPredicate("nicknameUsr",
						Query.FilterOperator.EQUAL, 
						contabilidadUsuario.getNicknameUsr()
		));
		PreparedQuery pq = ds.prepare(q);
		return pq.asSingleEntity();
	}
	
	/**
	 * Retorna la contabilidad de un usuario. 
	 * TODO Revisar que asume que el usuario solo tiene una contabilidad.
	 * 
	 * @param usuario
	 */
	public ContabilidadUsuario findByUsuario(String usuario) {

		ContabilidadUsuario contabilidadUsuario = new ContabilidadUsuario(usuario); 

		Entity contabilidadUsuarioEntity = find(contabilidadUsuario);
		if(contabilidadUsuarioEntity == null ){
			return null;
		}
		
		ContabilidadUsuario contabilidadUsuarioReturned = new ContabilidadUsuario(usuario);
		contabilidadUsuarioReturned.setAnio(((Long) contabilidadUsuarioEntity.getProperty("anio")).intValue());
		contabilidadUsuarioReturned.setNicknameUsr((String) contabilidadUsuarioEntity.getProperty("nicknameUsr"));
		contabilidadUsuarioReturned.setTotalActivos((Double) contabilidadUsuarioEntity.getProperty("totalActivos"));
		contabilidadUsuarioReturned.setTotalPasivos((Double) contabilidadUsuarioEntity.getProperty("totalPasivos"));
		contabilidadUsuarioReturned.setTotalPatrimonio((Double) contabilidadUsuarioEntity.getProperty("totalPatrimonio"));
		
		return contabilidadUsuarioReturned;
	}	
	
	/**
	 * TODO
	 * 
	 * Busca un contabilidadUsuario por la clave.
	 * 
	 * @param key
	 */
	public ContabilidadUsuario findByKey(Key key) {
		
		if( key == null ){
			return null;
		}

		ContabilidadUsuario contabilidadUsuario, detached = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			contabilidadUsuario = pm.getObjectById(ContabilidadUsuario.class, key);

			detached = pm.detachCopy(contabilidadUsuario);

			logger.log(Level.INFO, "Objeto con id " + key + "recuperado : "
					+ contabilidadUsuario);

		} catch (JDOObjectNotFoundException e) {
			logger.log(Level.INFO, "Objeto con id " + key
					+ " no pudo ser recuperado.");
		} finally {
			pm.close();
		}

		return detached;
	}

	/**
	 * Retorna todos los movimientos
	 * 
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public List<ContabilidadUsuario> findAll() {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		String query = "select from " + ContabilidadUsuario.class.getName();
		List<ContabilidadUsuario> contabilidadUsuarioList = (List<ContabilidadUsuario>) pm
				.newQuery(query).execute();

		logger.log(Level.INFO, "Cantidad de objetos recuperados = "
				+ contabilidadUsuarioList.size());

		return contabilidadUsuarioList;
	}

}
