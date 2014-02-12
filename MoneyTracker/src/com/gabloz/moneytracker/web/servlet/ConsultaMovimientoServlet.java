package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gabloz.moneytracker.business.bo.MovimientoBO;
import com.gabloz.moneytracker.business.entity.Movimiento;
import com.gabloz.moneytracker.data.datastore.MovimientoDatastore;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ConsultaMovimientoServlet extends HttpServlet {

	private static final long serialVersionUID = -2383834091165184249L;
	private static final Logger logger = Logger
			.getLogger(ConsultaMovimientoServlet.class.getCanonicalName());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doGet()");
		
		String cursor = request.getParameter("cursor");
		String tipo   = request.getParameter("tipo");
		logger.log(Level.INFO, cursor);

		UserService userService = UserServiceFactory.getUserService();
		User usuario = userService.getCurrentUser();

		if (usuario == null) {
			response.sendRedirect(userService.createLoginURL(request
					.getRequestURI()));
		} else {
			Movimiento movimiento = new Movimiento(usuario.getNickname());
			Object[] movimientosPorUsuario = null;
			if(MovimientoBO.GASTO.equals(tipo)){
				movimientosPorUsuario = new MovimientoDatastore().getGastos(movimiento, cursor);
			}else{
				movimientosPorUsuario = new MovimientoDatastore().getMovimientos(movimiento, cursor);
			}
			
			String movimientosJson = getConsultaJson(movimientosPorUsuario);
			PrintWriter out = response.getWriter();
			out.println(movimientosJson);
		}

	}

	private String getConsultaJson(Object[] movimientosPorUsuario) {
		
		String consultaJson = "{\"movAnt\": { \"items\" : [ ";
		List<Movimiento> movimientos = (List<Movimiento>) movimientosPorUsuario[0];
		for( Movimiento movimiento : movimientos ){
			consultaJson += "{\"descripcionMovimiento\" :\"" + movimiento.getDescripcionMovimiento() + "\",\"descripcionMovimiento2\":\"" 
			+ movimiento.getDescripcion() + "\"},";
			
		}
		if(consultaJson.endsWith(",")){
			consultaJson = consultaJson.substring(0, consultaJson.length() - 1);
		}
		consultaJson += " ], "; //cierro array items;
		consultaJson += "\"cursor\":\"" + movimientosPorUsuario[1] + "\" ";
		consultaJson += " }"; //cierro movAnt;
		consultaJson += " }"; //cierro papa;
		
		return consultaJson;
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
