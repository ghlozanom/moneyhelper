package com.gabloz.moneytracker.business.entity;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gabloz.moneytracker.util.Constants;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class CuentaUsuario {

	@PrimaryKey
	private String cuentaUsuarioKey;

	@Persistent
	private double saldo;

	@Persistent
	private Key contabilidadUsuarioKey;

	@Persistent
	private Key tipoCuentaKey;
	
	@NotPersistent
	private TipoCuenta tipoCuenta;
	
	@NotPersistent
	private ContabilidadUsuario contabilidadUsuario;

	/**
	 * Construye un JSON a partir de los valores de las propiedades del objeto.
	 * 
	 * @return la representación JSON
	 */
	public String toJSON() {
		StringBuilder sB = new StringBuilder();

		sB.append("{\"id\":");
		sB.append("\"" + getCuentaUsuarioKey() + "\"");
		sB.append(", \"saldo\":");
		sB.append("\"" + Util.numberToString(getSaldo(), Constants.FORMATO_NUM_ESTANDAR) + "\"");
		sB.append(", \"contabilidadUsuarioKey\": ");
		sB.append("\"" + getContabilidadUsuarioKey() + "\"");
		sB.append(", \"contabilidadUsuario\": ");
		sB.append("\"" + getContabilidadUsuario() + "\"");		
		sB.append(", \"tipoCuenta\": ");
		sB.append("\"" + getTipoCuenta() + "\"");
		sB.append(" }");

		return sB.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return toJSON();
	}	

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public Key getContabilidadUsuarioKey() {
		return contabilidadUsuarioKey;
	}

	public void setContabilidadUsuarioKey(Key contabilidadUsuarioKey) {
		this.contabilidadUsuarioKey = contabilidadUsuarioKey;
	}

	public Key getTipoCuentaKey() {
		return tipoCuentaKey;
	}

	public void setTipoCuentaKey(Key tipoCuentaKey) {
		this.tipoCuentaKey = tipoCuentaKey;
	}

	public String getCuentaUsuarioKey() {
		return cuentaUsuarioKey;
	}

	public void setCuentaUsuarioKey(String userIdTipoCuenta) {
		this.cuentaUsuarioKey = userIdTipoCuenta;
	}

	public TipoCuenta getTipoCuenta() {
		return tipoCuenta;
	}

	public void setTipoCuenta(TipoCuenta tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}

	public ContabilidadUsuario getContabilidadUsuario() {
		return contabilidadUsuario;
	}

	public void setContabilidadUsuario(ContabilidadUsuario contabilidadUsuario) {
		this.contabilidadUsuario = contabilidadUsuario;
	}

}
