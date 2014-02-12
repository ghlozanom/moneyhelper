package com.gabloz.moneytracker.data.datastore;

import java.util.Arrays;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class ConceptoDatastore {

	public void createConceptos() {
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		Entity renta = new Entity("Concepto", "Renta" );
		renta.setProperty("nombre", "Renta" );

		Entity alimentacion = new Entity("Concepto", "Alimentacion" );
		alimentacion.setProperty("nombre", "Alimentacion" );		
		
		Entity diversion = new Entity("Concepto", "Diversion" );
		diversion.setProperty("nombre", "Diversion" );
		
		Entity transporte = new Entity("Concepto", "Transporte" );
		transporte.setProperty("nombre", "Transporte" );	
		
		Entity ropa = new Entity("Concepto", "Ropa" );
		ropa.setProperty("nombre", "Ropa" );
		
		Entity mercado = new Entity("Concepto", "Mercado" );
		mercado.setProperty("nombre", "Mercado" );		
		
		Entity otros = new Entity("Concepto", "Otros" );
		otros.setProperty("nombre", "Otros" );		
		
		Entity internet = new Entity("Concepto", "Internet" );
		internet.setProperty("nombre", "Internet" );	
		
		Entity handy = new Entity("Concepto", "Handy" );
		handy.setProperty("nombre", "Handy" );		
		
		Entity gabrielPlus = new Entity("Concepto", "GabrielPlus" );
		gabrielPlus.setProperty("nombre", "Gabriel +" );	
		
		Entity profesionalCursos = new Entity("Concepto", "Profesional" );
		profesionalCursos.setProperty("nombre", "Cursos" );		
		
		ds.put(Arrays.asList(renta, alimentacion, mercado, diversion, transporte, ropa, internet, handy, gabrielPlus, profesionalCursos, otros) );
		
	}

}
