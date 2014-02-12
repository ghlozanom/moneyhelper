package com.gabloz.moneytracker.data.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gabloz.moneytracker.business.bo.MovimientoBO;
import com.gabloz.moneytracker.business.entity.CuentaUsuario;
import com.gabloz.moneytracker.business.entity.Movimiento;
import com.gabloz.moneytracker.business.entity.Operacion;
import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;

public class MovimientoDatastore {

	private static final int LIMITE_REGISTROS_MOVIMIENTOS = 5;
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	public Object[] getMovimientos(Movimiento movimiento, String cursorWebSafe) {
		
		Object[] result = new Object[2];
		
		if(movimiento == null || movimiento.getNicknameUsr() ==null )return null;
		
		Query q = new Query("Movimiento").addSort("fecha", Query.SortDirection.DESCENDING);
		q.setFilter( new Query.FilterPredicate("nicknameUsr",
				Query.FilterOperator.EQUAL,
				movimiento.getNicknameUsr()));
		PreparedQuery pq = ds.prepare(q);
		FetchOptions fo = FetchOptions.Builder.withLimit(LIMITE_REGISTROS_MOVIMIENTOS);
		if(cursorWebSafe != null && !"".equals(cursorWebSafe) ){
			fo.startCursor(Cursor.fromWebSafeString(cursorWebSafe));
		}
		QueryResultList<Entity> results = pq.asQueryResultList(fo);
		
		List<Movimiento> movimientos = new ArrayList<Movimiento>();
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
	
		//aca se valida si el proximo cursor tiene resultados
		Cursor cursor = results.getCursor();
		fo.startCursor(cursor);
		boolean tieneMasMovimientos = pq.countEntities(fo) > 0;	
		result[0] = movimientos;
		if(tieneMasMovimientos){
			result[1] = cursor.toWebSafeString();
		}else{
			result[1] = "-1";
		}
		return result;
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

	public Object[] getGastos(Movimiento movimiento, String cursorWebSafe) {
		
		Object[] result = new Object[2];
		
		if(movimiento == null || movimiento.getNicknameUsr() ==null )return null;
		
		Query q = new Query("Movimiento").addSort("fecha", Query.SortDirection.DESCENDING);
		
		q.setFilter( 
				Query.CompositeFilterOperator.and(
						Query.FilterOperator.EQUAL.of("nicknameUsr", movimiento.getNicknameUsr() ),
						Query.FilterOperator.EQUAL.of("tipo", MovimientoBO.GASTO )));
		
		PreparedQuery pq = ds.prepare(q);
		FetchOptions fo = FetchOptions.Builder.withLimit(LIMITE_REGISTROS_MOVIMIENTOS);
		if(cursorWebSafe != null && !"".equals(cursorWebSafe) ){
			fo.startCursor(Cursor.fromWebSafeString(cursorWebSafe));
		}
		QueryResultList<Entity> results = pq.asQueryResultList(fo);
		
		List<Movimiento> movimientos = new ArrayList<Movimiento>();
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
	
		//To validate if the cursor has more results
		Cursor cursor = results.getCursor();
		fo.startCursor(cursor);
		boolean tieneMasMovimientos = pq.countEntities(fo) > 0;	
		result[0] = movimientos;
		if(tieneMasMovimientos){
			result[1] = cursor.toWebSafeString();
		}else{
			result[1] = "-1";
		}
		return result;
	}

}
