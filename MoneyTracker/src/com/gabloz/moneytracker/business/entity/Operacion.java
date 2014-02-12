package com.gabloz.moneytracker.business.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Operacion {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private String tipo;
    
    @Persistent
    private String cuentaUsuarioKey;
    
    @NotPersistent
    private CuentaUsuario cuentaUsuario; 
    
	/**
	 * Construye un JSON a partir de los valores de las propiedades del objeto.
	 * 
	 * @return la representación JSON
	 */
	public String toJSON() {
		StringBuilder sB = new StringBuilder();

		sB.append("{\"id\":");
		sB.append("\"" + key + "\"");
		sB.append(", \"tipo\":");
		sB.append("\"" + getTipo() + "\"");
		sB.append(", \"cuentaUsuarioKey\": ");
		sB.append("\"" + getCuentaUsuarioKey() + "\"");	
		sB.append(", \"cuentaUsuario\": ");
		sB.append("\"" + getCuentaUsuario() + "\"");			
		sB.append(" }");

		return sB.toString();
	}	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return toJSON();
	}	    

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCuentaUsuarioKey() {
		return cuentaUsuarioKey;
	}

	public void setCuentaUsuarioKey(String cuentaUsuario) {
		this.cuentaUsuarioKey = cuentaUsuario;
	}

	public Key getKey() {
		return key;
	}

	public CuentaUsuario getCuentaUsuario() {
		return cuentaUsuario;
	}

	public void setCuentaUsuario(CuentaUsuario cuentaUsuario) {
		this.cuentaUsuario = cuentaUsuario;
	}
    
}
