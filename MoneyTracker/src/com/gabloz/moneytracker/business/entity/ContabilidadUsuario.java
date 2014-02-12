package com.gabloz.moneytracker.business.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gabloz.moneytracker.util.Constants;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class ContabilidadUsuario {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String nicknameUsr;

	@Persistent
	private double totalActivos;

	@Persistent
	private double totalPasivos;

	@Persistent
	private double totalPatrimonio;

	@Persistent
	private int anio;
	
	/**
	 * Construye un JSON a partir de los valores de las propiedades del objeto.
	 * 
	 * @return la representación JSON
	 */
	public String toJSON() {
		StringBuilder sB = new StringBuilder();

		sB.append("{\"totalActivos\":");
		sB.append("\"" + Util.numberToString(getTotalActivos(), Constants.FORMATO_NUM_ESTANDAR) + "\"");
		sB.append(", \"totalPasivos\":");
		sB.append("\"" + Util.numberToString(getTotalPasivos(), Constants.FORMATO_NUM_ESTANDAR) + "\"");
		sB.append(", \"totalPatrimonio\": ");
		sB.append("\"" + Util.numberToString(getTotalPatrimonio(), Constants.FORMATO_NUM_ESTANDAR) + "\"");
		sB.append(", \"nicknameUsr\": ");
		sB.append("\"" + getNicknameUsr() + "\"");
		sB.append(" }");

		return sB.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toJSON();
	}	

	public ContabilidadUsuario(String nicknameUsr) {
		this.nicknameUsr = nicknameUsr;
	}

	public ContabilidadUsuario(String nicknameUsr, int anio) {
		this.nicknameUsr = nicknameUsr;
		this.anio = anio;
	}

	public double getTotalActivos() {
		return totalActivos;
	}

	public void setTotalActivos(double totalActivos) {
		this.totalActivos = totalActivos;
	}

	public double getTotalPasivos() {
		return totalPasivos;
	}

	public void setTotalPasivos(double totalPasivos) {
		this.totalPasivos = totalPasivos;
	}

	public double getTotalPatrimonio() {
		return totalPatrimonio;
	}

	public void setTotalPatrimonio(double totalPatrimonio) {
		this.totalPatrimonio = totalPatrimonio;
	}

	public String getNicknameUsr() {
		return nicknameUsr;
	}

	public void setNicknameUsr(String nicknameUsr) {
		this.nicknameUsr = nicknameUsr;
	}

	public Key getKey() {
		return key;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}

}
