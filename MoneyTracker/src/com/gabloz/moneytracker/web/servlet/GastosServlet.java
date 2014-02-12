package com.gabloz.moneytracker.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gabloz.moneytracker.business.bo.ConceptoBO;
import com.gabloz.moneytracker.util.Constants;
import com.gabloz.moneytracker.util.Util;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Servlet implementation class ContabilidadUsuarioServlet
 */
public class GastosServlet extends HttpServlet {

	private static final long serialVersionUID = 5552265543061011412L;
	private static final Logger logger = Logger
			.getLogger(GastosServlet.class.getCanonicalName());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.INFO, "doGet()");

		UserService userService = UserServiceFactory.getUserService();
		User usuario = userService.getCurrentUser();	
		
		String fechaStr = request.getParameter("fecha");
		try {
			Date fecha = Util.stringToDate(fechaStr,
					Constants.FORMATO_FECHA_ESTANDAR);
			ConceptoBO conceptoBO = new ConceptoBO();
			String result = conceptoBO.getGastosUsuarioPorFecha(usuario, fecha);
			
			PrintWriter out = response.getWriter();
			out.println(result);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
