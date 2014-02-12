package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gabloz.moneytracker.business.bo.ContabilidadUsuarioBO;
import com.gabloz.moneytracker.business.bo.CuentaUsuarioBO;
import com.gabloz.moneytracker.business.bo.TipoCuentaBO;
import com.gabloz.moneytracker.business.entity.ContabilidadUsuario;
import com.gabloz.moneytracker.business.entity.CuentaUsuario;
import com.gabloz.moneytracker.business.entity.TipoCuenta;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class CuentaUsuarioServlet extends HttpServlet {

	private static final long serialVersionUID = -5177924860651006895L;
	private static final Logger logger = Logger
			.getLogger(CuentaUsuarioServlet.class.getCanonicalName());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doGet()");

		UserService userService = UserServiceFactory.getUserService();
		User usuario = userService.getCurrentUser();

		if (usuario == null) {
			response.sendRedirect(userService.createLoginURL(request
					.getRequestURI()));
		} else {
			String idCuenta = request.getParameter("idCuenta");

			CuentaUsuarioBO cuentaUsuarioBO = new CuentaUsuarioBO();
			CuentaUsuario cuentaUsuario = cuentaUsuarioBO.findByKey(usuario
					.getNickname() + idCuenta);

			PrintWriter out = response.getWriter();
			out.println(cuentaUsuario.toJSON());
		}

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

			// ********************************
			// Inicio código de prueba (Borrar)
			// ********************************
			TipoCuentaBO tipoCuentaBO = new TipoCuentaBO();
			TipoCuenta tipoCuenta = tipoCuentaBO.findByKey(141);

			ContabilidadUsuarioBO contabilidadUsuarioBO = new ContabilidadUsuarioBO();
			ContabilidadUsuario contabilidadUsuario = new ContabilidadUsuario(
					Util.getUsuario().getNickname());
			ContabilidadUsuario contabilidadUsuarioDetached = contabilidadUsuarioBO
					.creatIfNotExist(contabilidadUsuario);

			CuentaUsuario cuentaUsuario = new CuentaUsuario();
			cuentaUsuario.setTipoCuentaKey(tipoCuenta.getKey());
			cuentaUsuario.setContabilidadUsuarioKey(contabilidadUsuarioDetached
					.getKey());

			CuentaUsuarioBO cuentaUsuarioBO = new CuentaUsuarioBO();
			cuentaUsuarioBO.createIfNotExist(cuentaUsuario);
			// ********************************
			// Fin código de prueba (Borrar)
			// ********************************
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
