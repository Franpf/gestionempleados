var error;

function load() {
	error = false;
	document.getElementById('nss').focus();
}

function addEspecialidad() {
	var especialidad = document.getElementById('esp');
	var esptxt = especialidad.value.trim();
	if (esptxt != '') {
		var select = document.getElementById('esplst');
		var option = document.createElement('option');
		option.text = esptxt;
		select.add(option);
	}
//	else
//		mostrarError('esp');
	especialidad.value = '';
	especialidad.focus();
}

function delEspecialidad() {
	var select = document.getElementById('esplst');
	for (var i=select.options.length-1; i>=0; i--)
		if (select.options[i].selected)
			select.remove(i)
}


function mostrarError(id) {
	document.getElementById(id + 'lbl').style.color = 'red';
	document.getElementById(id).style.color = 'red';
	document.getElementById(id).style.borderColor = 'red';
	document.getElementById(id).focus();
	error = true;
}

function limpiar(id) {
	document.getElementById(id + 'lbl').style.color = 'black';
	document.getElementById(id).style.color = 'black';
	document.getElementById(id).style.borderColor = 'blue';
	error = false;
}

function validar(submittingTxt) {
	
	var nss = document.getElementById('nss').value.trim();
	var nombre = document.getElementById('nombre').value.trim();
	var salario = document.getElementById('salario').value.trim();
	
	var regexInt = /^\d+$/;
	var regexDec = /^\d+([,\.]\d{2})?$/;
	
	if (!regexInt.test(nss)) {
		mostrarError('nss');
		return false;
	}
	if (nombre == '') {
		mostrarError('nombre');
		return false;
	}
	if (!regexDec.test(salario)) {
		mostrarError('salario');
		return false;
	}
	
	document.getElementById('nss').value = nss;
	document.getElementById('nombre').value = nombre;
	document.getElementById('salario').value = salario.replace(',', '.');
	document.getElementById('enviar').dissabled = true;
	document.getElementById('enviar').value = submittingTxt;
	
	var select = document.getElementById('esplst');
	for (var i=0; i<select.options.length; i++)
		select.options[i].selected = true;
	return true;
}