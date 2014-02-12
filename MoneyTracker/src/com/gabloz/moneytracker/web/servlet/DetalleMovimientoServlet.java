package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gabloz.moneytracker.business.bo.ConceptoBO;
import com.gabloz.moneytracker.business.entity.Movimiento;
import com.gabloz.moneytracker.util.Constants;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class DetalleMovimientoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger
			.getLogger(DetalleMovimientoServlet.class.getCanonicalName());

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
		
		UserService userService = UserServiceFactory.getUserService();
		User usuario = userService.getCurrentUser();	
		
		String fechaStr = request.getParameter("fecha");
		String concepto = request.getParameter("concepto");
		try {
			Date fecha = Util.stringToDate(fechaStr,
					Constants.FORMATO_FECHA_ESTANDAR);
			ConceptoBO conceptoBO = new ConceptoBO();
			List<Movimiento> movimientos = conceptoBO.getGastosUsuarioPorFechaYConcepto(usuario, fecha, concepto);
			
			PrintWriter out = response.getWriter();
			out.println(getConsultaJson(movimientos));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	
	private String getConsultaJson(List<Movimiento> movimientos) {
		
		String consultaJson = "{\"movAnt\": { \"items\" : [ ";
		for( Movimiento movimiento : movimientos ){
			consultaJson += "{\"descripcionMovimiento\" :\"" + movimiento.getDescripcionMovimiento() + "\",\"descripcionMovimiento2\":\"" 
			+ movimiento.getDescripcion() + "\"},";
			
		}
		if(consultaJson.endsWith(",")){
			consultaJson = consultaJson.substring(0, consultaJson.length() - 1);
		}
		consultaJson += " ] "; //cierro array items;
		consultaJson += " }"; //cierro movAnt;
		consultaJson += " }"; //cierro papa;
		
		return consultaJson;
	}	

}
