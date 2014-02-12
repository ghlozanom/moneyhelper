package com.gabloz.moneytracker.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.gabloz.moneytracker.data.datastore.ConceptoDatastore;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Util {

	private static UserService userService = UserServiceFactory
			.getUserService();

	/**
	 * Retorna el usuario actual
	 * 
	 * @return
	 */
	public static User getUsuario() {
		return userService.getCurrentUser();
	}

	/**
	 * Lee un archivo csv extrayendo de cada una de sus líneas objetos
	 * TipoCuenta que retorna en un arrayList;
	 * 
	 * @param fileName
	 *            archivo csv que contiene la información
	 * @return Una colección de objetos TipoCuenta
	 */
	public static List<TipoCuenta> getTipoCuentaListFromCSV(String fileName) {

		ArrayList<TipoCuenta> tipoCuentaList = new ArrayList<TipoCuenta>();

		try {
			File file = new File(fileName);

			BufferedReader bufRdr = new BufferedReader(new FileReader(file));
			String line = null;

			// read each line from text file
			while ((line = bufRdr.readLine()) != null) {

				if (!line.startsWith("nombre") && !line.trim().startsWith(";")
						&& !line.trim().equals("")) {

					String[] attributes = line.split(";");

					if (attributes.length > 0) {

						String nombre = attributes[0];
						String descripcion = attributes[1];
						String tipo = attributes[2];

						tipoCuentaList.add(new TipoCuenta(nombre, descripcion,
								tipo));
					}

				}
			}

			// close the file
			bufRdr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return tipoCuentaList;
	}

	/**
	 * Retorna un Date dada su representación String basandose en el formato
	 * pasado por parámetro.
	 * 
	 * @param strFecha
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static Date stringToDate(String strFecha, String format)
			throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date fecha = formatter.parse(strFecha);

		return fecha;
	}

	/**
	 * Retorna la representación String de un Date dado el formato que le es
	 * pasado por parámetro.
	 * 
	 * @param fecha
	 * @param format
	 * @return
	 */
	public static String dateToString(Date fecha, String format) {

		String strFecha = null;
		
		if( fecha != null ){
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			strFecha = formatter.format(fecha);
		}		

		return strFecha;
	}
	
	/**
	 * Da formato la presentación a un número a de acuerdo a su formato. 
	 * 
	 * @param number
	 * @param format
	 * @return
	 */
	public static String numberToString(Double number, String format){
		
		String output = null;
		if( number != null ){
			DecimalFormat myFormatter = new DecimalFormat(format);
		    output = myFormatter.format(number);	
		}			
		
		return output;
	}

	/**
	 * Creates concepts using a low-level API, to be replaced
	 */
	public static void crearConceptos() {
		
		ConceptoDatastore conceptoDatastore = new ConceptoDatastore();
		conceptoDatastore.createConceptos();
		
	}

}
