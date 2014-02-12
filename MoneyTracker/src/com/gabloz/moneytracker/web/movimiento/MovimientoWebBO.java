package com.gabloz.moneytracker.web.movimiento;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gabloz.moneytracker.business.entity.Movimiento;
import com.gabloz.moneytracker.data.datastore.MovimientoDatastore;

public class MovimientoWebBO {
	
	private static final Logger logger = Logger.getLogger(MovimientoWebBO.class
			.getCanonicalName());	
	
	public Object[] findByUsuario(String usuario) {

		Object[] movimientosByUsuario = null;
		Movimiento movimiento = new Movimiento(usuario);

		movimientosByUsuario  = find(movimiento);

		return movimientosByUsuario;
	}
	
	public Object[] findGastosByUsuario(String usuario) {

		Object[] movimientosByUsuario = null;
		Movimiento movimiento = new Movimiento(usuario);

		movimientosByUsuario  = findGastos(movimiento);

		return movimientosByUsuario;
	}	
	
	/**
	 * Busca un movimiento
	 * 
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public Object[] find(Movimiento movimiento) {
		logger.log(Level.INFO, "find() = " + movimiento);
		
		Object[] movimientosPorUsuario = new MovimientoDatastore().getMovimientos(movimiento, null);
				
		logger.log(Level.INFO, "Cantidad de objetos recuperados = "
				+ ((Collection<Movimiento>) movimientosPorUsuario[0]).size());

		return movimientosPorUsuario;
	}	
	
	/**
	 * Busca los Gastos
	 * 
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public Object[] findGastos(Movimiento movimiento) {
		logger.log(Level.INFO, "find() = " + movimiento);
		
		Object[] movimientosPorUsuario = new MovimientoDatastore().getGastos(movimiento, null);
				
		logger.log(Level.INFO, "Cantidad de objetos recuperados = "
				+ ((Collection<Movimiento>) movimientosPorUsuario[0]).size());

		return movimientosPorUsuario;
	}	

}
