package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gabloz.moneytracker.data.datastore.CuentaUsuarioDatastore;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Servlet implementation class ContabilidadUsuarioServlet
 */
public class SaldoCuentaPorUsuarioServlet extends HttpServlet {

	private static final long serialVersionUID = 8493779429098309591L;
	private static final Logger logger = Logger
			.getLogger(SaldoCuentaPorUsuarioServlet.class.getCanonicalName());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doGet()");

		UserService userService = UserServiceFactory.getUserService();
		User usuario = userService.getCurrentUser();		
		
		CuentaUsuarioDatastore cuentaUsuarioDS = new CuentaUsuarioDatastore();
		String result = cuentaUsuarioDS.getCuentasPorUsuario(usuario);
		
		PrintWriter out = response.getWriter();
		out.println(result);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doPost()");

		UserService userService = UserServiceFactory.getUserService();
		User usuario = userService.getCurrentUser();

		if (usuario == null) {
			response.sendRedirect(userService.createLoginURL(request
					.getRequestURI()));
		} else {
			// TODO:
		}

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
			// TODO:
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
