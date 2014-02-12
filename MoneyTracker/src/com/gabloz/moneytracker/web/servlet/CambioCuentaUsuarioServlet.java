package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gabloz.moneytracker.business.bo.CuentaUsuarioBO;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
/**
 * Servlet implementation class GastoServlet
 */
public class CambioCuentaUsuarioServlet extends HttpServlet {

	private static final long serialVersionUID = 3840512889269991194L;
	private static final Logger logger = Logger
			.getLogger(CambioCuentaUsuarioServlet.class.getCanonicalName());

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doPost()");
		doPut(request, response);

	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doPut()");

		UserService userService = UserServiceFactory.getUserService();
		User usuario = userService.getCurrentUser();

		if (usuario == null) {
			response.sendRedirect(userService.createLoginURL(request
					.getRequestURI()));
		} else {
			try {
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				Entity cuentaUsuario = null;		
//				1. Encontrar el tipo de Cuenta Usuario
				String key = request.getParameter("cuentaUsuarioKey");
				cuentaUsuario = ds.get(KeyFactory.stringToKey(key));	
				cuentaUsuario.setProperty("saldo", request.getParameter("saldoCuentaUsuario"));
				ds.put(cuentaUsuario);
				Entity tipoCuenta = ds.get((Key) cuentaUsuario.getProperty("tipoCuentaKey"));
				
//				2. Actualizar la contabilidad usuario
				CuentaUsuarioBO cuentaUsuarioBO =new CuentaUsuarioBO();
				Iterable<Entity> cuentasUsuario = 
					cuentaUsuarioBO.findCuentaUsuarioForContabilidadUsuario((Key) cuentaUsuario.getProperty("contabilidadUsuarioKey"));
				
				double total = 0;
				for(Entity cuentaUsuarioEntity : cuentasUsuario ){
					if(cuentaUsuarioEntity.equals(cuentaUsuario)){
						cuentaUsuarioEntity = cuentaUsuario;
					}
					Entity tipoCuentaCuentaUsuario = ds.get((Key) cuentaUsuarioEntity.getProperty("tipoCuentaKey"));
					if( tipoCuentaCuentaUsuario.getProperty("tipo").equals(tipoCuenta.getProperty("tipo")) ){
						Object saldo = cuentaUsuarioEntity.getProperty("saldo");
						if(saldo instanceof Double){
							total += (Double)saldo;
						}else if (saldo instanceof String ){
							total += Double.parseDouble((String)saldo);
						}
					}
				}
				
				Entity contabilidadUsuario = ds.get((Key) cuentaUsuario.getProperty("contabilidadUsuarioKey"));
				contabilidadUsuario.setProperty("totalActivos", total);
				ds.put(contabilidadUsuario);
				
				
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doDelete()");

		UserService userService = UserServiceFactory.getUserService();
		User usuario = userService.getCurrentUser();

		if (usuario == null) {
			response.sendRedirect(userService.createLoginURL(request
					.getRequestURI()));
		} else {
			// TODO:
		}

	}

}
