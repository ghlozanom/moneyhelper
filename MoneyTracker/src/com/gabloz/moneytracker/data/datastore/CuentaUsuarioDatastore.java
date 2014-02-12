package com.gabloz.moneytracker.data.datastore;

import java.util.ArrayList;
import java.util.Collection;

import com.gabloz.moneytracker.business.bo.ContabilidadUsuarioBO;
import com.gabloz.moneytracker.business.bo.TipoCuentaBO;
import com.gabloz.moneytracker.business.entity.ContabilidadUsuario;
import com.gabloz.moneytracker.business.entity.CuentaUsuario;
import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;

public class CuentaUsuarioDatastore {
	
	private static final String User_GROUP = "UserGroup";
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	public String getCuentasPorUsuario(User usuario){
		
		ContabilidadUsuarioBO cubo = new ContabilidadUsuarioBO();
		ContabilidadUsuario contabilidadUsuario = new ContabilidadUsuario(usuario.getNickname());
		Entity contabilidadUsuarioEntity = cubo.find(contabilidadUsuario);
		
		Key contabilidadUsuarioKey = contabilidadUsuarioEntity.getKey();
		
		
		String result = "{\"cuentasUsuario\": {\"activos\" : [ ";
		
		result += getCuentaUsuarioPorTipo("ACTIVO", ds, contabilidadUsuarioKey);
		result += ",\"pasivos\":[";
		result += getCuentaUsuarioPorTipo("PASIVO", ds, contabilidadUsuarioKey);
		result += ",\"patrimonio\":[";
		result += getCuentaUsuarioPorTipo("PATRIMONIO", ds, contabilidadUsuarioKey);				
		result += "}}";
		
		return result;
	}

	private String getCuentaUsuarioPorTipo(String tipoCuentaQuery, DatastoreService ds, Key contabilidadUsuarioKey) {
		
		Query q = new Query("TipoCuenta");
		q.setFilter(
				new Query.FilterPredicate("tipo",
						Query.FilterOperator.EQUAL,
						tipoCuentaQuery));
		q.addSort("nombre");
		
		String result = "";
		PreparedQuery pq = ds.prepare(q);
		boolean init = false;
		for(Entity tipoCuenta: pq.asIterable()){
			
			//Traigo la cuenta usuario por tipo cuenta y contabilidad usuario
			Query cuentaUsuarioQuery = new Query("CuentaUsuario");
			cuentaUsuarioQuery.setFilter(
					Query.CompositeFilterOperator.and(
							Query.FilterOperator.EQUAL.of("tipoCuentaKey", tipoCuenta.getKey()),
							Query.FilterOperator.EQUAL.of("contabilidadUsuarioKey", contabilidadUsuarioKey)));
			PreparedQuery cuentaUsuarioPQ = ds.prepare(cuentaUsuarioQuery);
			Entity cuentaUsuario = cuentaUsuarioPQ.asSingleEntity();
			//Si no tiene saldo en esta cuenta el usuario
			if(cuentaUsuario == null ){
				continue;
			}
			if(init){
				result += ",";
			}
			init = true;
			result += "{";
			result += "\"nombreCuenta\" : \"" + (String) tipoCuenta.getProperty("nombre") + "\""; 			
			result += ",\"saldo\":\"" + cuentaUsuario.getProperty("saldo") + "\"";
			result += ",\"id\":\"" + KeyFactory.keyToString(cuentaUsuario.getKey()) + "\"";
			result += "}";
		}
		result += "]"; //cierra activos
		return result;		
	}

	/**
	 * Encuentra el key de una cuentaUsuario del cual se tiene el tipoCuentaKey y contabilidadUsuarioKey
	 * @param cuentaUsuario
	 * @return
	 */
	public String getCuentaUsuarioKey(CuentaUsuario cuentaUsuario) {
		
		Entity cuentaUsuarioEntity = getCuentaUsuarioPorCuentaUsuarioYTipoCuenta(cuentaUsuario.getContabilidadUsuarioKey(),
				cuentaUsuario.getTipoCuentaKey());
		
		if(cuentaUsuarioEntity == null ){
			return null;
		}
		
		return cuentaUsuarioEntity.getKey().getName();

	}
	
/**
 * 	
 */
	public Entity getCuentaUsuarioPorCuentaUsuarioYTipoCuenta( Key contabilidadUsuarioKey, Key tipoCuentaKey ){
		//Traigo la cuenta usuario por tipo cuenta y contabilidad usuario
		Query cuentaUsuarioQuery = new Query("CuentaUsuario");
		cuentaUsuarioQuery.setFilter(
				Query.CompositeFilterOperator.and(
						Query.FilterOperator.EQUAL.of("tipoCuentaKey", tipoCuentaKey),
						Query.FilterOperator.EQUAL.of("contabilidadUsuarioKey", contabilidadUsuarioKey )));
		PreparedQuery cuentaUsuarioPQ = ds.prepare(cuentaUsuarioQuery);
		return cuentaUsuarioPQ.asSingleEntity();
	}
	
	public Entity getContabilidadUsuarioPorUsuario(User usuario){
		
		Query contabilidadUsuarioQuery = new Query("ContabilidadUsuario");
		contabilidadUsuarioQuery.setFilter(
				new Query.FilterPredicate("nicknameUsr", 
						Query.FilterOperator.EQUAL,
						usuario.getNickname()));
		
		PreparedQuery pqContabilidadUsuario = ds.prepare(contabilidadUsuarioQuery); 
		return pqContabilidadUsuario.asSingleEntity();
	}

	public String getSaldoCuentaPorUsuario(User usuario, String idCuenta) {
		TipoCuentaBO tipoCuentaBO = new TipoCuentaBO();
		TipoCuenta tipoCuenta = tipoCuentaBO.findByKey(Long
				.parseLong(idCuenta));
		
		Entity contabilidadUsuarioEntity = getContabilidadUsuarioPorUsuario(usuario);
		Entity cuentaUsuarioEntity = getCuentaUsuarioPorCuentaUsuarioYTipoCuenta(contabilidadUsuarioEntity.getKey(),
				tipoCuenta.getKey());
		Double saldo = (Double) cuentaUsuarioEntity.getProperty("saldo");
		return saldo.toString();
	}
	
	public void initCuentasUsuario( Key contabilidadUsuarioKey, User usuario) {
		
		Query q = new Query("TipoCuenta");
		q.addSort("nombre");
		PreparedQuery pq = ds.prepare(q);
		Collection<Entity> entities = new ArrayList<Entity>(); 
		
		for(Entity tipoCuenta: pq.asIterable()){ 
			Key userKey = KeyFactory.createKey(User_GROUP, usuario.getNickname());
			Entity cuentaUsuarioEntity = new Entity("CuentaUsuario", userKey );
			cuentaUsuarioEntity.setProperty("contabilidadUsuarioKey", contabilidadUsuarioKey);
			cuentaUsuarioEntity.setProperty("saldo", 0.0);
			cuentaUsuarioEntity.setProperty("tipoCuentaKey", tipoCuenta.getKey());
			entities.add(cuentaUsuarioEntity);
		}
		
		if(entities.size() > 0){
			ds.put(entities);
		}
	}	
	
	
}
