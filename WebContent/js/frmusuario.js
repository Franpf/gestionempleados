var error;

function load() {
	error = false;
	document.getElementById('id').focus();
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
	
	var id = document.getElementById('id').value.trim();
	var password = document.getElementById('password').value;
	
	var regexId = /^\w+$/;
	
	
	if (id == '' || !regexId.test(id)) {
		mostrarError('id');
		return false;
	}
	
	if (password == '') {
		mostrarError('password');
		return false;
	}
	
	document.getElementById('id').value = id;
	document.getElementById('enviar').disabled = true;
	document.getElementById('enviar').value = submittingTxt;
	return true;
}