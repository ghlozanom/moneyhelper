package com.gabloz.moneytracker.business.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gabloz.moneytracker.business.entity.Concepto;
import com.gabloz.moneytracker.business.entity.CuentaUsuario;
import com.gabloz.moneytracker.business.entity.Movimiento;
import com.gabloz.moneytracker.business.entity.Operacion;
import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;

public class ConceptoBO {
	
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	private static final Logger logger = Logger.getLogger(ConceptoBO.class
			.getCanonicalName());

	/**
	 * Devuelve todos los "Conceptos" que hay en la aplicacion
	 * 
	 * @param key
	 */
	public List<Concepto> findAll() {
		
		logger.log(Level.INFO, "Leyendo todos los conceptos..");

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Query q = new Query("Concepto");
		PreparedQuery pq = ds.prepare(q);
		List<Concepto> allConceptos = new ArrayList<Concepto>();
		for(Entity result : pq.asIterable()){
			String nombre = (String) result.getProperty("nombre");
			Concepto concepto = new Concepto();
			concepto.setNombre(nombre);
			allConceptos.add(concepto);
		}
		
		return allConceptos;
	}

	public String getGastosUsuarioPorFecha(User usuario, Date fecha) {
		
		String gastos = "{\"gastos\" : { ";
		gastos += "\"items\" : [ ";
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		List<Concepto> conceptos = findAll();
		boolean gastoLeido = false;
		Double valorTotalGastos = 0.0;
		for(Concepto concepto : conceptos ){
			Query q = new Query("Movimiento");
			q.setFilter(
					Query.CompositeFilterOperator.and(
							Query.FilterOperator.EQUAL.of("concepto", concepto.getNombre()),
							Query.CompositeFilterOperator.and(
									Query.FilterOperator.EQUAL.of("nicknameUsr", usuario.getNickname()),
									Query.FilterOperator.GREATER_THAN_OR_EQUAL.of("fecha", fecha)
							)
					)
					
			);
			PreparedQuery pq = ds.prepare(q);
			Double valor = 0.0;
			boolean conceptoLeido = false;
			for(Entity result : pq.asIterable()){
				gastoLeido = true;
				if(!conceptoLeido){
					gastos += "{\"nombre\": \"" + concepto.getNombre() + "\", \"valor\":\"";
					conceptoLeido = true;
				}
				Double valorConcepto = (Double)result.getProperty("valor");
				valor += valorConcepto;
			}
			if(conceptoLeido){
				gastos += valor + "\"},";
				valorTotalGastos += valor;
			}
			
		}
		if(gastos.endsWith(",")){
			gastos = gastos.substring(0, gastos.lastIndexOf(","));
		}
		gastos += " ]";
		if(gastoLeido){
			gastos += ", \"totalGastos\" : \"" + valorTotalGastos + "\"";
		}
		gastos += " } }"; 
				

		return gastos;
	}

	public List<Movimiento> getGastosUsuarioPorFechaYConcepto(User usuario, Date fecha,
			String concepto) {
		
		Query q = new Query("Movimiento");
		q.setFilter(
				Query.CompositeFilterOperator.and(
						Query.FilterOperator.EQUAL.of("concepto", concepto),
						Query.CompositeFilterOperator.and(
								Query.FilterOperator.EQUAL.of("nicknameUsr", usuario.getNickname()),
								Query.FilterOperator.GREATER_THAN_OR_EQUAL.of("fecha", fecha)
						)
				)
				
		);
		PreparedQuery pq = ds.prepare(q);
		
		List<Movimiento> movimientos = new ArrayList<Movimiento>();
		Iterable<Entity> results =  pq.asIterable();
		for( Entity movimientoEntity : results ){
			Movimiento movimientoResult = new Movimiento((String) movimientoEntity.getProperty("nicknameUsr"));
			try {
				setMovimientoFromEntity(movimientoResult, movimientoEntity);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			movimientos.add(movimientoResult);
		}		

		return movimientos;
	}
	
	private void setMovimientoFromEntity(Movimiento movimientoResult,
			Entity movimientoEntity) throws EntityNotFoundException {
		
		movimientoResult.setConcepto((String) movimientoEntity.getProperty("concepto"));
		movimientoResult.setDescripcion((String) movimientoEntity.getProperty("descripcion"));
		movimientoResult.setFecha((Date) movimientoEntity.getProperty("fecha"));
		movimientoResult.setValor( (Double) movimientoEntity.getProperty("valor"));
		
		Entity opEnt1 = ds.get((Key) movimientoEntity.getProperty("operacion1_key_OID"));
		Operacion op1 = new Operacion();
		op1.setTipo((String) opEnt1.getProperty("tipo"));
		Key cuentaUsuarioKey = KeyFactory.createKey("CuentaUsuario", (String) opEnt1.getProperty("cuentaUsuarioKey"));
		Entity cuentaUsuario1Ent = ds.get(cuentaUsuarioKey);
		Entity tipoCuentaEnt    = ds.get((Key) cuentaUsuario1Ent.getProperty("tipoCuentaKey"));
		CuentaUsuario cuentaUsuario1 = new CuentaUsuario();
		TipoCuenta tipoCuenta = new TipoCuenta(
				(String)tipoCuentaEnt.getProperty("nombre"), 
				(String)tipoCuentaEnt.getProperty("descripcion"), 
				(String)tipoCuentaEnt.getProperty("tipo"));
		cuentaUsuario1.setTipoCuenta(tipoCuenta);
		op1.setCuentaUsuario(cuentaUsuario1);
		movimientoResult.setOperacion1(op1);

		Entity opEnt2 = ds.get((Key) movimientoEntity.getProperty("operacion2_key_OID"));
		Operacion op2 = new Operacion();
		op2.setTipo((String) opEnt2.getProperty("tipo"));
		Key cuentaUsuarioKey2 = KeyFactory.createKey("CuentaUsuario", (String) opEnt2.getProperty("cuentaUsuarioKey"));
		Entity cuentaUsuario2Ent = ds.get(cuentaUsuarioKey2);
		Entity tipoCuenta2Ent    = ds.get((Key) cuentaUsuario2Ent.getProperty("tipoCuentaKey"));
		CuentaUsuario cuentaUsuario2 = new CuentaUsuario();
		TipoCuenta tipoCuenta2 = new TipoCuenta(
				(String)tipoCuenta2Ent.getProperty("nombre"), 
				(String)tipoCuenta2Ent.getProperty("descripcion"), 
				(String)tipoCuenta2Ent.getProperty("tipo"));
		cuentaUsuario2.setTipoCuenta(tipoCuenta2);
		op2.setCuentaUsuario(cuentaUsuario2);
		movimientoResult.setOperacion2(op2);
		
		
	}	

}
