<%@page import="com.gabloz.moneytracker.business.bo.TipoCuentaBO"%>
<%@page import="java.util.List"%>
<%@page import="com.gabloz.moneytracker.business.entity.TipoCuenta"%>
<%@page import="com.gabloz.moneytracker.business.bo.ConceptoBO"%>
<%@page import="com.gabloz.moneytracker.business.entity.Concepto"%>
<%@page import="com.gabloz.moneytracker.business.bo.MovimientoBO"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page import="com.google.appengine.api.users.User"%>
<%@page import="java.util.Collection"%>
<%@page import="com.gabloz.moneytracker.business.entity.Movimiento"%>
<%@page import="com.gabloz.moneytracker.web.movimiento.MovimientoWebBO"%>

<!DOCTYPE html>
<html>
	<head>  
		<link rel="stylesheet" href="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css" />      
		<link rel="stylesheet" href="css/iacc.css" />
		
		<style type="text/css">
    .ui-icon-edit {
        background-image: url(icons-18-white.png);
        background-repeat: no-repeat;
        background-position: -824px 50%;
    }
</style>
		
		<title>I, Accountant</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1">  
		
		<script src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
		<script src="http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js"></script>
		<link rel="stylesheet" href="css/iacc.css" />
	</head>
   
    <body > 

		<div data-role="page" id="loginPage" >
			<div data-role="header" data-theme="b" >
				<h1>I, Accountant</h1>
		   	</div>
		
			<div data-role="content" id="mainContent">
				<p>Eres un usuario no autenticado
					<a href="${loginUrl}" data-role="button" data-ajax="false">Autenticarse</a>
				</p>
			</div>
			
		</div> 	

        
    </body>
</html>