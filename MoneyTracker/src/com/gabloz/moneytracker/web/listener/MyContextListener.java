package com.gabloz.moneytracker.web.listener;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.gabloz.moneytracker.business.bo.TipoCuentaBO;
import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.gabloz.moneytracker.util.Util;

public class MyContextListener implements ServletContextListener {

	private ServletContext context = null;

	/*
	 * This method is invoked when the Web Application has been removed and is
	 * no longer able to accept requests
	 */

	public void contextDestroyed(ServletContextEvent event) {
		// Outputs a simple message to the server's console
		System.out.println("IAcoountant Application ha sido removida.");
		this.context = null;

	}

	// This method is invoked when the Web Application
	// is ready to service requests

	public void contextInitialized(ServletContextEvent event) {
		this.context = event.getServletContext();

		// Output a simple message to the server's console
		System.out.println("Arrancando IAcoountant Application..");

		List<TipoCuenta> tipoCuentaList = Util.getTipoCuentaListFromCSV(context
				.getInitParameter("resourcesPath") + "tipoCuentaData.csv");

		// Loads account's data
		TipoCuentaBO tipoCuentaBO = new TipoCuentaBO();
		for (TipoCuenta tipoCuenta : tipoCuentaList) {
			tipoCuentaBO.createIfNotExistByNombre(tipoCuenta);
		}
		
		//Concepts creation
		Util.crearConceptos();

	}
}