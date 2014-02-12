package com.gabloz.moneytracker.business.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gabloz.moneytracker.util.Constants;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class Movimiento {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String descripcion;

	@Persistent
	private double valor;

	@Persistent
	private Date fecha;

	@Persistent(defaultFetchGroup = "true", dependent = "true")
	private Operacion operacion1; // con la anotación dependent se logra el borrado en cascada

	@Persistent(defaultFetchGroup = "true", dependent = "true")
	private Operacion operacion2; 
	
	@Persistent
	private String nicknameUsr;	
	
	@Persistent
	private String concepto;		

	//INGRESO(+Patrimonio) o GASTO(-Patrimonio)
	@Persistent
	private String tipo;	
	
	public Movimiento(String nicknameUsr){
		this.nicknameUsr = nicknameUsr;
	}
	
	
	/**
	 * Construye un JSON a partir de los valores de las propiedades del objeto.
	 * 
	 * @return la representación JSON
	 */
	public String toJSON() {
		StringBuilder sB = new StringBuilder();

		sB.append("{\"id\":");
		if( key != null ){
			sB.append("\"" + key.getId() + "\"");	
		}else{
			sB.append("\"" + null + "\"");
		}		
		sB.append(", \"descripcion\":");
		sB.append("\"" + getDescripcion() + "\"");
		sB.append(", \"valor\": ");
		sB.append("\"" + Util.numberToString(getValor(), Constants.FORMATO_NUM_ESTANDAR) + "\"");
		sB.append(", \"fecha\": ");
		sB.append("\"" + Util.dateToString(getFecha(), Constants.FORMATO_FECHA_ESTANDAR)  + "\"");
		sB.append(", \"operacion1\": ");
		sB.append("\"" + getOperacion1() + "\"");
		sB.append(", \"operacion2\": ");
		sB.append("\"" + getOperacion1() + "\"");
		sB.append(", \"nicknameUsr\": ");
		sB.append("\"" + getNicknameUsr() + "\"");			
		sB.append(" }");

		return sB.toString();
	}	
	
	public String getDescripcionMovimiento(){
		
		String descripcion = "" + Util.numberToString(this.getValor(), Constants.FORMATO_NUM_ESTANDAR) + ",";
		if( "ENTRADA".equals(this.getOperacion1().getTipo()) ){
			descripcion += "+";
		}else {
			descripcion += "-";
		}
		descripcion += this.getOperacion1().getCuentaUsuario().getTipoCuenta().getNombre() + ",";
		if( "ENTRADA".equals(this.getOperacion2().getTipo()) ){
			descripcion += "+";
		}else {
			descripcion += "-";
		}	
		SimpleDateFormat simpleDateFormat =
			(SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);
		descripcion += this.getOperacion2().getCuentaUsuario().getTipoCuenta().getNombre() + ",";
		descripcion += simpleDateFormat.format(getFecha());
		
		return descripcion;
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return toJSON();
	}		
	
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Key getKey() {
		return key;
	}

	public Operacion getOperacion1() {
		return operacion1;
	}

	public void setOperacion1(Operacion operacion1) {
		this.operacion1 = operacion1;
	}

	public Operacion getOperacion2() {
		return operacion2;
	}

	public void setOperacion2(Operacion operacion2) {
		this.operacion2 = operacion2;
	}

	public String getNicknameUsr() {
		return nicknameUsr;
	}

	public void setNicknameUsr(String nicknameUsr) {
		this.nicknameUsr = nicknameUsr;
	}


	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}


	public String getConcepto() {
		return concepto;
	}


	public void setKey(Key key) {
		this.key = key;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public String getTipo() {
		return tipo;
	}

}
