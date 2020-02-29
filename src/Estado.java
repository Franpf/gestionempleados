public enum Estado {
	
	OK("Ok"),
	FALLO_LOGIN("No se ha podido iniciar sesión:<br/>usuario no registrado o contraseña incorrecta"),
	ERROR_LOGIN("Un error interno impide el inicio de sesión:<br/>contacte con el administrador"),
	FALLO_REGISTRO("No se ha podido registrar el usuario:<br/>el nombre de usuario ya existe"),
	ERROR_REGISTRO("Un error interno impide el registro del usuario:<br/>contacte con el administrador"),
	FALLO_ALTAING("No se ha podido dar de alta al ingeniero:<br/>el número de la seguridad social ya existe"),
	ERROR_ALTAING("Un error interno impide el alta del ingeniero:<br/>contacte con el administrador"),
	ERROR_INTERNO("Error interno, contacte con el administrador");
		
	String msg;
	
	private Estado(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return msg;
	}

}
