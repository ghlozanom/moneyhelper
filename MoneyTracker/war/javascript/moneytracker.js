var botonAnteriorInit;

$(document).ready(function() {

	$(".movMuster").hide();
	$(".modeloTipoCuentaUsuario").hide();
	$(".modeloConcepto").hide();
	$(".modeloTotalGastos").hide();
	$(".modeloMovimiento").hide();
	$(".modeloGasto").hide();
	$(".modeloDetalleMovimiento").hide();

	updateActivos();
	updateCuentasUsuario();
	refresqueGastos();
});

$(document).delegate("#ingresarMovimientoPage", "pageinit", function() {
	updateActivos();
});

$(function() {
	$('#insertarMovimientoForm').bind('submit', function(e) {
		e.preventDefault();
		var formArray = $(this).serializeArray();
		var idCuenta1 = formArray['idCuenta1'];

		$.mobile.loading('show', {
			text : 'Ingresando Movimiento....',
			textVisible : true,
			theme : 'b',
			textOnly : false,
			html : ""
		});

		$.ajax( {
			type : "post",
			url : "/Movimiento",
			data : $(this).serialize(),
			success : function(data, text) {
				updateMovimientos(formArray, true);
				$('#valorInput').val("");
				$('#descripcion').val("");
			},
			error : function(request, status, error) {
				alert("error!!!");
			},
			complete : function(xhr, status) {
				updateCuentasUsuario();
				$.mobile.loading('hide');
			}
			
		});

	});
});

$(function() {
	$('#insertarGastoForm').bind('submit', function(e) {
		e.preventDefault();
		var formArray = $(this).serializeArray();

		$.mobile.loading('show', {
			text : 'Ingresando Gasto....',
			textVisible : true,
			theme : 'b',
			textOnly : false,
			html : ""
		});

		$.ajax( {
			type : "post",
			url : "/Gasto",
			data : $(this).serialize(),
			success : function(data, text) {
				$('#valorInputGasto').val("");
				$('#descripcionGasto').val("");
				refresqueGastos();
			},
			error : function(request, status, error) {
				alert("error!!!");
			},
			complete : function(xhr, status) {
				$.mobile.loading('hide');
				updateCuentasUsuario();
			}
			
		});

	});
});

$(function() {
	$('#cambiarCuentaUsuarioForm').bind('submit', function(e) {
		e.preventDefault();
		var formArray = $(this).serializeArray();

		$.mobile.loading('show', {
			text : 'Cambiando cuenta de usuario....',
			textVisible : true,
			theme : 'b',
			textOnly : false,
			html : ""
		});

		$.ajax( {
			type : "post",
			url : "/CambioCuentaUsuario",
			data : $(this).serialize(),
			success : function(data, text) {
			},
			error : function(request, status, error) {
				alert("error!!!");
			},
			complete : function(xhr, status) {
				
				updateActivos( function() {
					updateCuentasUsuario(function(){
						$.mobile.loading('hide');
						$.mobile.changePage("#homePage");
					});
				} );				
			}
			
		});

	});
});

$(function() {
	$('#verGastosForm').bind(
			'submit',
			function(e) {
				e.preventDefault();
				var formArray = $(this).serializeArray();

				$.mobile.loading('show', {
					text : 'Consultando Gastos....',
					textVisible : true,
					theme : 'b',
					textOnly : false,
					html : ""
				});

				$.ajax( {
					type : "get",
					url : "/verGastos",
					dataType : 'json',
					success : function(resp) {

						var gastos = resp.gastos.items;
						var htmlGastos = "";
						$("#listaGastos").html("");
						$("#listaTotalGastos").html("");
						for ( var i in gastos) {
							var modeloConcepto = $(".modeloConcepto");
							modeloConcepto.show();
							var elem = $(".modeloConcepto").clone();
							elem.find("a").attr("href", "javascript:muestreConcepto('" + gastos[i].nombre + "');").end();
							elem.find("span.nombreConcepto").html(
									gastos[i].nombre).end().find(
									"span.saldoConcepto").html(gastos[i].valor)
									.end().removeClass("modeloConcepto");
							$("#listaGastos").append(elem);
							modeloConcepto.hide();

							elem.removeAttr("style");
						}
						var totalGastos = resp.gastos.totalGastos;
						if (totalGastos != undefined) {
							var modeloTotalGastos = $(".modeloTotalGastos");
							modeloTotalGastos.show();
							var elem = $(".modeloTotalGastos").clone();
							elem.find("span.totalGastos").html(totalGastos)
									.end().removeClass("modeloTotalGastos");
							$("#listaTotalGastos").append(elem);
							modeloTotalGastos.hide();

							elem.removeAttr("style");

						}
						$("#listaGastos").listview("refresh");
						$("#listaTotalGastos").listview("refresh");

					},
					error : function(request, status, error) {
						alert("error!!!");
					},
					complete : function(xhr, status) {
						$.mobile.loading('hide');
					},
					data : $(this).serialize()
				});

			});	
	
	
	
});

function updateMovimientos(formArray, refreshView) {
	var valorEntrada = "";
	var tipoOp1 = "";
	var tipoOp2 = "";

	var labelOp1 = $('#tipoItem1 option:selected').text();
	var labelOp2 = $('#tipoItem2 option:selected').text();

	var fecha = "";

	$.each(formArray, function(i, item) {
		if (item.name == "valor")
			valorEntrada = item.value;
		if (item.name == "tipoOp1") {
			if (item.value == "ENTRADA") {
				tipoOp1 = "+";
			} else {
				tipoOp1 = "-";
			}
		}
		if (item.name == "tipoOp2") {
			if (item.value == "ENTRADA") {
				tipoOp2 = "+";
			} else {
				tipoOp2 = "-";
			}
		}
		if (item.name == "fecha") {
			fecha = item.value;
		}
	});
	valorEntrada += ", " + tipoOp1 + labelOp1 + "," + tipoOp2 + ":" + labelOp2
			+ "," + fecha;

	var modeloMovimiento = $(".movMuster");
	modeloMovimiento.show();

	var elem = $(".movMuster").clone();
	elem.find("span.valorEntrada").html(valorEntrada).end().removeClass(
			"movMuster").insertAfter($('#movimientoPrincipal'));
	modeloMovimiento.hide();

	elem.removeAttr("style");
	
	if(refreshView){
		$("#listMovimientos").listview("refresh");
	}
	updateActivos();
}

function updateActivos(callbackFunct) {

	$.ajax( {
				url : '/ContabilidadUsuario',
				type : "GET",
				dataType : 'json',
				data : null,
				success : function(resp) {

					// Get the page we are going to dump our content into.
				var $activos = $(".activos"), $pasivos = $(".pasivos"), $patrimonio = $(".patrimonio");

				$activos.each(function(i, elem) {
					$(elem).html(resp.totalActivos);
				});

				$pasivos.each(function(i, elem) {
					$(elem).html(resp.totalPasivos);
				});

				$patrimonio.each(function(i, elem) {
					$(elem).html(resp.totalPatrimonio);
				});
				
				if(callbackFunct){
					callbackFunct();
				}
				
			},
			error : function(e) {
				//calling the user defined error function
				if (errorFn)
					errorFn(e);
			}
			});
}

function actualiceMovimientos(cursor) {
	
	$.ajax( {
				url : '/ConsultaMovimientoSiguienteCursor',
				type : "GET",
				dataType : 'json',
				data : { cursor: cursor },
				success : function(resp) {
					var movAnteriores = resp.movAnt.items;
					var modeloMovimiento = $(".modeloMovimiento");
					modeloMovimiento.show();
					for ( var i in movAnteriores) {
						var elem = $(".modeloMovimiento").clone();
						elem.find("span.descripcionMovimiento").html(
								movAnteriores[i].descripcionMovimiento).end().find(
								"span.descripcionMovimiento2").html(movAnteriores[i].descripcionMovimiento2)
								.end().removeClass("modeloMovimiento");
						elem.addClass("movimientoItem");
						modeloMovimiento.before(elem);
					}
					modeloMovimiento.hide();
					
					var siguienteCursor = resp.movAnt.cursor;
					if(siguienteCursor == "-1" ){
						$("#botonActualizacionMovimientos").attr("href", "");
						return;
					}
					$("#botonActualizacionMovimientos").attr("href", "javascript: actualiceMovimientos('" + siguienteCursor + "');");
			},
			error : function(e) {
				//calling the user defined error function
				if (errorFn)
					errorFn(e);
			}
			});
}

function actualiceGastos(cursor) {
	
	$.ajax( {
				url : '/ConsultaMovimientoSiguienteCursor',
				type : "GET",
				dataType : 'json',
				data : { cursor: cursor,
						 tipo: 'GASTO'},
				success : function(resp) {
					var movAnteriores = resp.movAnt.items;
					var modeloMovimiento = $(".modeloGasto");
					modeloMovimiento.show();
					for ( var i in movAnteriores) {
						var elem = $(".modeloGasto").clone();
						elem.find("span.descripcionMovimiento").html(
								movAnteriores[i].descripcionMovimiento).end().find(
								"span.descripcionMovimiento2").html(movAnteriores[i].descripcionMovimiento2)
								.end().removeClass("modeloGasto");
						elem.addClass("gastoItem");
						modeloMovimiento.before(elem);
					}
					modeloMovimiento.hide();
					
					var siguienteCursor = resp.movAnt.cursor;
					if(siguienteCursor == "-1" ){
						$("#botonActualizacionGastos").attr("href", "");
						return;
					}
					$("#botonActualizacionGastos").attr("href", "javascript: actualiceGastos('" + siguienteCursor + "');");
			},
			error : function(e) {
				//calling the user defined error function
				if (errorFn)
					errorFn(e);
			}
			});
}

function refresqueMovimientos() {
	$('.movimientoItem').remove();
	actualiceMovimientos('');
}

function refresqueGastos() {
	$('.gastoItem').remove();
	actualiceGastos('');
}

function updateCuentasUsuario(callbackFunct) {

	$.ajax( {
		url : '/saldoCuentaPorUsuario',
		type : "GET",
		dataType : 'json',
		data : null,
		success : function(resp) {
			$("#listaCuentasUsuarioActivos").html("");
			var activos = resp.cuentasUsuario.activos;
			for ( var i in activos) {

				var modeloTipoCuentaUsuario = $(".modeloTipoCuentaUsuario");
				modeloTipoCuentaUsuario.show();
				var elem = $(".modeloTipoCuentaUsuario").clone();
				elem.find("span.nombreCuentaUsuario").html(
						activos[i].nombreCuenta).end().find(
						"span.saldoCuentaUsuario").html(activos[i].saldo).end()
						.removeClass("modeloTipoCuentaUsuario");
				elem.find("a").attr("href", "javascript:muestreCuentaUsuario('" + activos[i].id + "', '" +
						activos[i].nombreCuenta + "');" ).end();
				$("#listaCuentasUsuarioActivos").append(elem);
				modeloTipoCuentaUsuario.hide();

				elem.removeAttr("style");
				elem.find("a.abg").attr("id","abg" + i );
				$('#abg' + i).button();
				
			}

			$("#listaCuentasUsuarioActivos").listview("refresh");
			
			$("#listaCuentasUsuarioPasivos").html("");
			var pasivos = resp.cuentasUsuario.pasivos;
			for ( var i in pasivos) {

				var modeloTipoCuentaUsuario = $(".modeloTipoCuentaUsuario");
				modeloTipoCuentaUsuario.show();
				var elem = $(".modeloTipoCuentaUsuario").clone();
				elem.find("span.nombreCuentaUsuario").html(
						pasivos[i].nombreCuenta).end().find(
						"span.saldoCuentaUsuario").html(pasivos[i].saldo).end()
						.removeClass("modeloTipoCuentaUsuario");
				$("#listaCuentasUsuarioPasivos").append(elem);
				modeloTipoCuentaUsuario.hide();

				elem.removeAttr("style");
			}

			$("#listaCuentasUsuarioPasivos").listview("refresh");

			$("#listaCuentasUsuarioPatrimonio").html("");
			var patrimonio = resp.cuentasUsuario.patrimonio;
			for ( var i in patrimonio) {

				var modeloTipoCuentaUsuario = $(".modeloTipoCuentaUsuario");
				modeloTipoCuentaUsuario.show();
				var elem = $(".modeloTipoCuentaUsuario").clone();
				elem.find("span.nombreCuentaUsuario").html(
						patrimonio[i].nombreCuenta).end().find(
						"span.saldoCuentaUsuario").html(patrimonio[i].saldo)
						.end().removeClass("modeloTipoCuentaUsuario");
				$("#listaCuentasUsuarioPatrimonio").append(elem);
				modeloTipoCuentaUsuario.hide();

				elem.removeAttr("style");
			}

			$("#listaCuentasUsuarioPatrimonio").listview("refresh");
			
			if(callbackFunct){
				callbackFunct();
			}
		},
		error : function(e) {
			//calling the user defined error function
		if (errorFn)
			errorFn(e);
	}
	});
}

function muestreConcepto(concepto){
	
	$('#ingresoConcepto').attr("value", concepto);
	$('.movimientoDetalleItem').remove();
	var form = $('#verGastosForm');
	
	$.ajax( {
		type : "post",
		url : "/detalleMovimiento",
		data : form.serialize(),
		dataType : 'json',
		success : function(resp) {

			var movAnteriores = resp.movAnt.items;
			var modeloMovimiento = $(".modeloDetalleMovimiento");
			modeloMovimiento.show();
			for ( var i in movAnteriores) {
				var elem = $(".modeloDetalleMovimiento").clone();
				elem.find("span.descripcionMovimiento").html(
						movAnteriores[i].descripcionMovimiento).end().find(
						"span.descripcionMovimiento2").html(movAnteriores[i].descripcionMovimiento2)
						.end().removeClass("modeloDetalleMovimiento");
				elem.addClass("movimientoDetalleItem");
				modeloMovimiento.before(elem);
			}
			modeloMovimiento.hide();
			
			$.mobile.changePage("mHome.jsp#detalleMovimientosPage");
		},
		error : function(request, status, error) {
			alert("error!!!");
		},
		complete : function(xhr, status) {
		}
		
	});
	
}

function muestreCuentaUsuario(id, nombre){
	
	$('#idCuentaUsuario').attr("value", id);
	$('#nombreCuentaUsuario').attr("value", nombre);
	
	var form = $('#verCuentaUsuarioForm');
	
	$.ajax( {
		type : "post",
		url : "/detalleCuentaUsuario",
		data : form.serialize(),
		dataType : 'json',
		success : function(resp) {

			var saldo = resp.saldo;
			var saldoCuenta = $("#saldoCuentaUsuario");
			saldoCuenta.attr("value", saldo);
			
			var nombre = resp.nombre;
			var nombreCuenta = $("#nombreCuentaUsuarioHeader");
			nombreCuenta.html(nombre);
			
			var key = resp.key;
			var cuentaUsuarioKey = $("#cuentaUsuarioKey");
			cuentaUsuarioKey.attr("value", key);			
		
			$.mobile.changePage("#detalleCuentaUsuarioPage");
		
		},
		error : function(request, status, error) {
			alert("error!!!");
		},
		complete : function(xhr, status) {
		}
		
	});
	
}
