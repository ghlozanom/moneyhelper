package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

public class DetalleCuentaUsuarioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger
			.getLogger(DetalleCuentaUsuarioServlet.class.getCanonicalName());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doPost()");
		
		String id = request.getParameter("idCuentaUsuario");
		String nombre = request.getParameter("nombreCuentaUsuario");
		
		try {
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Entity cuentaUsuario = ds.get(KeyFactory.stringToKey(id));
			
			PrintWriter out = response.getWriter();
			out.println(getCuentaUsuarioSerialized(cuentaUsuario, id, nombre));
			
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getCuentaUsuarioSerialized(Entity cuentaUsuario, String id, String nombre) {
		StringBuffer cuentaUsuarioStringBuffer = new StringBuffer();
		cuentaUsuarioStringBuffer.append("{\"saldo\" : \" " + cuentaUsuario.getProperty("saldo") + "\"," );
		cuentaUsuarioStringBuffer.append("\"key\" : \" " + id + "\"," );
		cuentaUsuarioStringBuffer.append("\"nombre\" : \" " + nombre + "\"}" );
		
		return cuentaUsuarioStringBuffer.toString();
	}	

}
