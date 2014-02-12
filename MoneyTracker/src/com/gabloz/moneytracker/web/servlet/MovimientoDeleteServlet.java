package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gabloz.moneytracker.business.bo.MovimientoBO;
import com.gabloz.moneytracker.util.Constants;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
/**
 * Servlet implementation class MovimientoServlet
 */
public class MovimientoDeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 2276782047951024855L;
	private static final Logger logger = Logger
			.getLogger(MovimientoDeleteServlet.class.getCanonicalName());

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
			MovimientoBO movimientoBO = new MovimientoBO();

			PrintWriter out = response.getWriter();
			out.println(" { \"movimientos\" :"
					+ Arrays.toString(movimientoBO
							.findByUsuario(usuario.getNickname()).toArray()) + "}");
		}
	}

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
			String fechaStr = null;
			try {
				fechaStr = request.getParameter("fecha");
				Date fecha = Util.stringToDate(fechaStr,
						Constants.FORMATO_FECHA_ESTANDAR);

				String valor = request.getParameter("valor");
				String descripcion = request.getParameter("descripcion");

				String idCuenta1 = request.getParameter("idCuenta1");
				String idCuenta2 = request.getParameter("idCuenta2");

				String tipoOp1 = request.getParameter("tipoOp1");
				String tipoOp2 = request.getParameter("tipoOp2");

				MovimientoBO movimientoBO = new MovimientoBO();
				movimientoBO.createOrUpdate(valor, descripcion, fecha,
						idCuenta1, idCuenta2, tipoOp1, tipoOp2, usuario, null, null);
				
			} catch (ParseException pException) {
				PrintWriter out = response.getWriter();
				out.println("Error al procesar la fecha " + fechaStr
						+ " con el formato " + Constants.FORMATO_FECHA_ESTANDAR);
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
