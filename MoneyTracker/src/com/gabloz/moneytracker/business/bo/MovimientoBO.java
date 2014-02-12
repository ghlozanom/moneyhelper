package com.gabloz.moneytracker.business.bo;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.gabloz.moneytracker.business.entity.ContabilidadUsuario;
import com.gabloz.moneytracker.business.entity.CuentaUsuario;
import com.gabloz.moneytracker.business.entity.Movimiento;
import com.gabloz.moneytracker.business.entity.Operacion;
import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.gabloz.moneytracker.data.datastore.CuentaUsuarioDatastore;
import com.gabloz.moneytracker.util.Constants;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;


public class MovimientoBO {

	private static final Logger logger = Logger.getLogger(MovimientoBO.class
			.getCanonicalName());

	public static final String GASTO = "GASTO";

	private TipoCuentaBO tipoCuentaBO = new TipoCuentaBO();
	private ContabilidadUsuarioBO contabilidadUsuarioBO = new ContabilidadUsuarioBO();
	private CuentaUsuarioBO cuentaUsuarioBO = new CuentaUsuarioBO();

	/**
	 * Crea ó actualiza un movimiento.
	 * 
	 * @param movimiento
	 */
	public void createOrUpdate(Movimiento movimiento) {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			logger.log(Level.INFO, "Guardando un movimiento..");

			pm.makePersistent(movimiento);

		} finally {

		}

		pm.close();
	}

	/**
	 * TODO Crea ó actualiza un movimiento.
	 * @param concepto 
	 * @param tipo 
	 * 
	 * @param movimiento
	 */
	public void createOrUpdate(String valor, String descripcion, Date fechaStr,
			String idCuenta1, String idCuenta2, String tipoOp1, String tipoOp2,
			User usuario, String concepto, String tipo) {

		// Identificación de los tipos de cuenta.
		TipoCuenta tipoCuenta1 = tipoCuentaBO.findByKey(Long
				.parseLong(idCuenta1));
		TipoCuenta tipoCuenta2 = tipoCuentaBO.findByKey(Long
				.parseLong(idCuenta2));

		// Identificación de la contabilidad del usuario.
		ContabilidadUsuario contabilidadUsuario = contabilidadUsuarioBO
				.creatIfNotExistByUser(new ContabilidadUsuario(Util
						.getUsuario().getNickname()));

		// Idenficación de las cuentas de usuario.
		CuentaUsuario cuenta1 = new CuentaUsuario();
		CuentaUsuario cuenta2 = new CuentaUsuario();

		cuenta1.setContabilidadUsuarioKey(contabilidadUsuario.getKey());
		cuenta1.setTipoCuentaKey(tipoCuenta1.getKey());
		
		CuentaUsuarioDatastore cuentaUsuarioDS = new CuentaUsuarioDatastore();
		cuenta1.setCuentaUsuarioKey(cuentaUsuarioDS.getCuentaUsuarioKey(cuenta1));
		
		cuenta1 = cuentaUsuarioBO.createIfNotExist(cuenta1);

		cuenta2.setContabilidadUsuarioKey(contabilidadUsuario.getKey());
		cuenta2.setTipoCuentaKey(tipoCuenta2.getKey());
		cuenta2.setCuentaUsuarioKey(cuentaUsuarioDS.getCuentaUsuarioKey(cuenta2));
		cuenta2 = cuentaUsuarioBO.createIfNotExist(cuenta2);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();

		try {

			tx.begin();

			Operacion operacion1 = new Operacion();
			operacion1.setCuentaUsuarioKey(cuenta1.getCuentaUsuarioKey());
			operacion1.setTipo(tipoOp1);

			Operacion operacion2 = new Operacion();
			operacion2.setCuentaUsuarioKey(cuenta2.getCuentaUsuarioKey());
			operacion2.setTipo(tipoOp2);

			double valorDb = Double.parseDouble(valor);

			Movimiento movimiento = new Movimiento(Util.getUsuario()
					.getNickname());
			movimiento.setValor(valorDb);
			movimiento.setDescripcion(descripcion);
			movimiento.setFecha(fechaStr);
			movimiento.setOperacion1(operacion1);
			movimiento.setOperacion2(operacion2);
			if(concepto != null){
				movimiento.setConcepto(concepto);
			}
			if(tipo != null){
				movimiento.setTipo(tipo);
			}

			logger.log(Level.INFO, "Guardando un movimiento..");
			// Crear el movimiento
			pm.makePersistent(movimiento);

			// Actualizar de los saldos de las cuentas
			if (Constants.ENTRADA.equals(tipoOp1)) {
				cuenta1.setSaldo(cuenta1.getSaldo() + valorDb);
			} else {
				cuenta1.setSaldo(cuenta1.getSaldo() - valorDb);
			}
			cuentaUsuarioBO.createOrUpdate(cuenta1);

			if (Constants.ENTRADA.equals(tipoOp2)) {
				cuenta2.setSaldo(cuenta2.getSaldo() + valorDb);
			} else {
				cuenta2.setSaldo(cuenta2.getSaldo() - valorDb);
			}
			cuentaUsuarioBO.createOrUpdate(cuenta2);

			// Actualizar de los saldos del patrimonio.
			// if (Constants.TIP_PATRIMONIO.equals(tipoCuenta1.getTipo())
			// || Constants.TIP_PATRIMONIO.equals(tipoCuenta2.getTipo())) {

			if (Constants.TIP_ACTIVO.equals(tipoCuenta1.getTipo())) {

				if (Constants.ENTRADA.equals(tipoOp1)) {
					contabilidadUsuario.setTotalActivos(contabilidadUsuario
							.getTotalActivos() + valorDb);
				} else {
					contabilidadUsuario.setTotalActivos(contabilidadUsuario
							.getTotalActivos() - valorDb);
				}

			} else if (Constants.TIP_PASIVO.equals(tipoCuenta1.getTipo())) {

				if (Constants.ENTRADA.equals(tipoOp1)) {
					contabilidadUsuario.setTotalPasivos(contabilidadUsuario
							.getTotalPasivos() + valorDb);
				} else {
					contabilidadUsuario.setTotalPasivos(contabilidadUsuario
							.getTotalPasivos() - valorDb);
				}

			}

			if (Constants.TIP_ACTIVO.equals(tipoCuenta2.getTipo())) {

				if (Constants.ENTRADA.equals(tipoOp2)) {
					contabilidadUsuario.setTotalActivos(contabilidadUsuario
							.getTotalActivos() + valorDb);
				} else {
					contabilidadUsuario.setTotalActivos(contabilidadUsuario
							.getTotalActivos() - valorDb);
				}

			} else if (Constants.TIP_PASIVO.equals(tipoCuenta2.getTipo())) {

				if (Constants.ENTRADA.equals(tipoOp2)) {
					contabilidadUsuario.setTotalPasivos(contabilidadUsuario
							.getTotalPasivos() + valorDb);
				} else {
					contabilidadUsuario.setTotalPasivos(contabilidadUsuario
							.getTotalPasivos() - valorDb);
				}

			}

			contabilidadUsuario.setTotalPatrimonio(contabilidadUsuario
					.getTotalActivos() - contabilidadUsuario.getTotalPasivos());

			contabilidadUsuarioBO.createOrUpdate(contabilidadUsuario);

			// }

			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		pm.close();

	}


	/**
	 * Retorna la contabilidad de un usuario. TODO Revisar
	 * 
	 * @param usuario
	 */
	public Collection<Movimiento> findByUsuario(String usuario) {

		Movimiento movimiento = new Movimiento(usuario);

		Collection<Movimiento> movimientoList = find(movimiento);

		return movimientoList;
	}

	/**
	 * TODO Documentme
	 * 
	 * @param movimiento
	 * @return
	 */
	private String buildWhere(Movimiento movimiento) {
		StringBuilder sb = new StringBuilder();

		boolean usarConector = false;

		if (movimiento == null) {
			return sb.toString();
		}

		if (movimiento.getNicknameUsr() != null) {
			sb.append("nicknameUsr == '" + movimiento.getNicknameUsr() + "' ");
			usarConector = true;
		}

		if (movimiento.getValor() != 0) {
			sb.append((usarConector ? "&&" : "") + " Valor == "
					+ movimiento.getValor() + " ");
		}

		return sb.toString();
	}

	/**
	 * Busca un movimiento
	 * 
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public Collection<Movimiento> find(Movimiento movimiento) {
		logger.log(Level.INFO, "find() = " + movimiento);

		PersistenceManager pm = PMF.get().getPersistenceManager();

		String whereStr = buildWhere(movimiento);
		logger.log(Level.INFO, "find():where = " + whereStr);

		Query movsQuery = pm.newQuery(Movimiento.class);
		movsQuery.setFilter(whereStr);
		movsQuery.setOrdering("fecha desc");
		
		List<Movimiento> movimientoList = (List<Movimiento>) movsQuery
				.execute();

		logger.log(Level.INFO, "Cantidad de objetos recuperados = "
				+ movimientoList.size());

		Collection<Movimiento> detachedList = pm.detachCopyAll(movimientoList);

		for (Movimiento mov : detachedList) {

			if (mov.getOperacion1() != null) {

				CuentaUsuario cuentaUsuario = cuentaUsuarioBO.findByKey(mov
						.getOperacion1().getCuentaUsuarioKey());
				mov.getOperacion1().setCuentaUsuario(cuentaUsuario);
			}

			if (mov.getOperacion2() != null) {

				CuentaUsuario cuentaUsuario = cuentaUsuarioBO.findByKey(mov
						.getOperacion2().getCuentaUsuarioKey());
				mov.getOperacion2().setCuentaUsuario(cuentaUsuario);
			}

		}

		return detachedList;
	}

	/**
	 * Borra un movimiento.
	 * 
	 * @param movParam
	 */
	public void delete(Long key) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();

		try {

			Movimiento movParam = findByKey(key);

			ObjectState objState = JDOHelper.getObjectState(movParam);
			logger.log(Level.INFO, "getObjectState(movimiento) = " + objState);

			if (movParam == null) {
				return;
			}

			// Obtener las cuentas implicadas en el movimiento
			Operacion op1 = movParam.getOperacion1();
			Operacion op2 = movParam.getOperacion2();

			CuentaUsuario cuenta1 = cuentaUsuarioBO.findByKey(op1
					.getCuentaUsuarioKey());
			TipoCuenta tipoCuenta1 = cuenta1.getTipoCuenta();
			String tipoOp1 = op1.getTipo();

			CuentaUsuario cuenta2 = cuentaUsuarioBO.findByKey(op2
					.getCuentaUsuarioKey());
			TipoCuenta tipoCuenta2 = cuenta2.getTipoCuenta();
			String tipoOp2 = op2.getTipo();

			// Obtener el usuario y el valor del movimiento
			String usuario = movParam.getNicknameUsr();
			Double valorDb = movParam.getValor();

			tx.begin();

			logger.log(Level.INFO,
					"delete(): JDOHelper.getPersistenceManager(movParam) = "
							+ JDOHelper.getPersistenceManager(movParam));

			// Borrar el movimiento
			// pm.deletePersistent(pm.getObjectById(Movimiento.class, key));
			pm.deletePersistent(movParam);

			// Actualizar de los saldos de las cuentas
			if (Constants.ENTRADA.equals(tipoOp1)) {
				cuenta1.setSaldo(cuenta1.getSaldo() - valorDb);
			} else {
				cuenta1.setSaldo(cuenta1.getSaldo() + valorDb);
			}
			cuentaUsuarioBO.createOrUpdate(cuenta1);

			if (Constants.ENTRADA.equals(tipoOp2)) {
				cuenta2.setSaldo(cuenta2.getSaldo() - valorDb);
			} else {
				cuenta2.setSaldo(cuenta2.getSaldo() + valorDb);
			}
			cuentaUsuarioBO.createOrUpdate(cuenta2);

			// Actualizar la contabilidad del usuario
			ContabilidadUsuario contabilidadUsuario = contabilidadUsuarioBO
					.findByUsuario(usuario);

			if (Constants.TIP_ACTIVO.equals(tipoCuenta1.getTipo())) {

				if (Constants.ENTRADA.equals(tipoOp1)) {
					contabilidadUsuario.setTotalActivos(contabilidadUsuario
							.getTotalActivos() - valorDb);
				} else {
					contabilidadUsuario.setTotalActivos(contabilidadUsuario
							.getTotalActivos() + valorDb);
				}

			} else if (Constants.TIP_PASIVO.equals(tipoCuenta1.getTipo())) {

				if (Constants.ENTRADA.equals(tipoOp1)) {
					contabilidadUsuario.setTotalPasivos(contabilidadUsuario
							.getTotalPasivos() - valorDb);
				} else {
					contabilidadUsuario.setTotalPasivos(contabilidadUsuario
							.getTotalPasivos() + valorDb);
				}

			}

			if (Constants.TIP_ACTIVO.equals(tipoCuenta2.getTipo())) {

				if (Constants.ENTRADA.equals(tipoOp2)) {
					contabilidadUsuario.setTotalActivos(contabilidadUsuario
							.getTotalActivos() - valorDb);
				} else {
					contabilidadUsuario.setTotalActivos(contabilidadUsuario
							.getTotalActivos() + valorDb);
				}

			} else if (Constants.TIP_PASIVO.equals(tipoCuenta2.getTipo())) {

				if (Constants.ENTRADA.equals(tipoOp2)) {
					contabilidadUsuario.setTotalPasivos(contabilidadUsuario
							.getTotalPasivos() - valorDb);
				} else {
					contabilidadUsuario.setTotalPasivos(contabilidadUsuario
							.getTotalPasivos() + valorDb);
				}

			}

			contabilidadUsuario.setTotalPatrimonio(contabilidadUsuario
					.getTotalActivos() - contabilidadUsuario.getTotalPasivos());

			contabilidadUsuarioBO.createOrUpdate(contabilidadUsuario);

			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}

			pm.close();
		}

	}

	/**
	 * Busca un movimiento por la clave.
	 * 
	 * @param key
	 */
	public Movimiento findByKey(Long key) {

		if (key == null) {
			return null;
		}

		Movimiento movimiento = null, movTemp = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();

		ObjectState objState = null;

		try {
			movimiento = pm.getObjectById(Movimiento.class, key);

			logger.log(Level.INFO,
					"1 - findByKey() : JDOHelper.getPersistenceManager(movimiento) = "
							+ JDOHelper.getPersistenceManager(movimiento));

			objState = JDOHelper.getObjectState(movimiento);
			logger.log(Level.INFO, "getObjectState(movimiento) = " + objState);

			movTemp = pm.detachCopy(movimiento);

			logger.log(Level.INFO,
					"2 - findByKey() : JDOHelper.getPersistenceManager(movParam) = "
							+ JDOHelper.getPersistenceManager(movTemp));

			objState = JDOHelper.getObjectState(movTemp);
			logger.log(Level.INFO, "getObjectState(movTemp) = " + objState);

			// Operacion op1 = movimiento.getOperacion1();
			// Operacion op2 = movimiento.getOperacion2();

			// if (op1 != null) {
			// op1.setCuentaUsuario(cuentaUsuarioBO.findByKey(op1
			// .getCuentaUsuarioKey()));
			// }
			//
			// if (op2 != null) {
			// op2.setCuentaUsuario(cuentaUsuarioBO.findByKey(op2
			// .getCuentaUsuarioKey()));
			// }

			logger.log(Level.INFO, "Objeto con id " + key + "recuperado : "
					+ movimiento);

		} catch (JDOObjectNotFoundException e) {
			logger.log(Level.INFO, "Objeto con id " + key
					+ " no pudo ser recuperado.");
		} finally {
			pm.close();
		}

		logger.log(Level.INFO,
				"3 - findByKey() : JDOHelper.getPersistenceManager(movimiento) = "
						+ JDOHelper.getPersistenceManager(movimiento));

		objState = JDOHelper.getObjectState(movimiento);
		logger.log(Level.INFO, "getObjectState(movimiento) = " + objState);

		logger.log(Level.INFO,
				"4 - findByKey() : JDOHelper.getPersistenceManager(movParam) = "
						+ JDOHelper.getPersistenceManager(movTemp));

		objState = JDOHelper.getObjectState(movTemp);
		logger.log(Level.INFO, "getObjectState(movTemp) = " + objState);

		return movTemp;

	}

	public void ingresarGasto(String valor, String descripcion, Date fecha,
			String idCuenta1, String idCuenta2, String tipoOp2, User usuario,
			String concepto)  {
		
		//Se calcula el tipo de la cuenta uno, si es pasivo entonces el tipo de operacion es de entrada,
		//por ejemplo, si se gasta desde una tarjeta de credito.
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Key k = KeyFactory.createKey("TipoCuenta", Long.parseLong(idCuenta1));
		try {
			Entity tipoCuenta = ds.get(k);
			String tipo = (String) tipoCuenta.getProperty("tipo");
			String tipoOp1 = "SALIDA";
			if("PASIVO".equals(tipo)){
				tipoOp1 = "ENTRADA";
			}
			
			createOrUpdate(valor, descripcion, fecha,
					idCuenta1, idCuenta2, tipoOp1, tipoOp2, usuario, concepto, GASTO);			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
