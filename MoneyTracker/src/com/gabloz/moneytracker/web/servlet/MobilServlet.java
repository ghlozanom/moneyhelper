package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gabloz.moneytracker.business.bo.ContabilidadUsuarioBO;
import com.gabloz.moneytracker.business.entity.ContabilidadUsuario;
import com.gabloz.moneytracker.data.datastore.CuentaUsuarioDatastore;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Servlet for the main path ("/") of the moneytracker app.
 * 
 * @author      Gabriel Lozano
 */
public class MobilServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3849405156266760386L;
	private static final Logger logger = Logger
			.getLogger(MobilServlet.class.getCanonicalName());
	private final String logingPage = "login.jsp";

	/**
	 * If the user is not logged in, is redirected.
	 * Creates a "ContabilidadUsuario" enity if it doesn't exist
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "MobilServlet doGet()");

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user == null) {
			request.setAttribute("loginUrl", userService.createLoginURL("/"));
			RequestDispatcher jsp = request.getRequestDispatcher(logingPage);
			jsp.forward(request, response);
		} else {
			ContabilidadUsuario contabilidadUsuario = new ContabilidadUsuarioBO().findByUsuario(user.getNickname());
			if(contabilidadUsuario == null){
				Key userGroupKey = KeyFactory.createKey("UserGroup", user.getNickname());
				Entity entity = new Entity("ContabilidadUsuario",userGroupKey );
				entity.setProperty("anio", 0);
				entity.setProperty("totalActivos", 0.0);
				entity.setProperty("totalPasivos", 0.0);
				entity.setProperty("totalPatrimonio", 0.0);
				entity.setProperty("nicknameUsr", user.getNickname());
				DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
				ds.put(entity);
				
				CuentaUsuarioDatastore cuentaUsuarioDatastore = new CuentaUsuarioDatastore();
				cuentaUsuarioDatastore.initCuentasUsuario(entity.getKey(), user);
			}
			request.setAttribute("logoutUrl", userService.createLogoutURL("/" + logingPage));
			response.setContentType("text/html");
			RequestDispatcher jsp = request.getRequestDispatcher("mHome.jsp");
			jsp.forward(request, response);
		}
	}


}
