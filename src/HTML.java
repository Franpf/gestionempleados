import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HTML {

	static void enviarPagina(HttpSession session, HttpServletResponse response, String contenido, String titulo, String css, String js)
			throws IOException {
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<meta charset=\"UTF-8\">");
			out.printf("<title>%s</title\n>", titulo);
			out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/base.css\" media=\"screen\" />");
			if (css != null)
				out.printf("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/%s.css\" media=\"screen\" />\n", css);
			if (js != null)
				out.printf("<script type=\"text/javascript\" src=\"js/%s.js\"></script>\n", js);
			out.println("</head>");
			out.println("<body onload=\"load()\">");
			out.println("<header>");
			out.println("	<div class=\"head\">");
			out.println("		<img src=\"img/ingenieros.png\" />");
			out.println("		<h1>Práctica Servlets</h1>");
			out.printf("		<h2>%s</h2>\n", titulo);
			out.println("	</div>");
			out.println("	<nav><div class=\"izda\">");
			if (!titulo.contentEquals("Inicio"))
				out.println("		<span><a class=\"menuitem\" href=\"/ge/\">Inicio</a></span>");
			if (session != null && !titulo.equals("Gestión de Empleados"))
				out.println("		<span><a class=\"menuitem\" href=\"gestion\">Gestión</a></span>");
			out.println("	</div><div class=\"dcha\">");
			if (session == null) {
				if (!titulo.equals("Inicio de Sesión"))
					out.println("		<span><a class=\"menuitem\" href=\"login\">Iniciar sesión</a></span>");
			}
			else
				out.printf("		%s (<a href=\"logout\">cerrar sesión</a>)", session.getAttribute("usuario"));
			out.println("	</div></nav>");
			out.println("</header>");			
			out.println("<section class=\"principal\">");
			out.println(contenido);
			out.println("</section>");
			out.println("<footer>");
			out.println("<p>CIFP Avilés - Desarrollo de Aplicaciones Web - Despligue de aplicaciones Web");
			out.println("</footer>");
			out.println("</body>");
			out.println("</html>");
		} finally {
			if (out != null)
				out.close();
		}
	}
	
	static void enviarInicio(HttpSession session, HttpServletResponse response) throws IOException {
		HTML.enviarPagina(session, response, aviso("pagina en construcción"), "Inicio", null, null);
	}
	
	static void enviarGestion(HttpSession session, HttpServletResponse response) throws IOException {
		String usuario = (String) session.getAttribute("usuario");
		StringBuilder html = new StringBuilder();
		html.append("<p><a href=\"altaingeniero\">Altas de Ingenieros</a></p>");
		if (usuario.equals("admin"))
			html.append("<p><a href=\"registro\">Registro de Usuarios</a></p>");
		HTML.enviarPagina(session, response, html.toString(), "Gestión de Empleados", null, null);
	}
	
	static String aviso(String mensaje) throws IOException {
		StringBuilder html = new StringBuilder();
		html.append("<div class=\"sinpermiso\">");
		html.append(divError(mensaje));
		html.append("</div>");
		return html.toString();
	}
	
	static void enviarSinPermiso(HttpServletResponse response) throws IOException {
		enviarPagina(null, response, aviso("no tiene permiso para acceder a este recurso"), "Recurso no disponible", null, null);
	}
	
	static String divError(String mensaje) {
		return String.format("<div class=\"error\"><p>%s</p></div>", mensaje);
	}
	
	static void enviarFormAltaIngenieros(HttpSession session, HttpServletResponse response, Estado estado, List<String> departamentos) throws IOException {
		String titulo = "Altas de Ingenieros";
		StringBuilder html = new StringBuilder();
		if (estado == Estado.ERROR_INTERNO)
			html.append(divError(estado.toString()));
		else {
			if (estado != Estado.OK)
				html.append(divError(estado.toString()));
			html.append("	<form action=\"altaingehtml.append(\"	</div>\");niero\" method=\"post\" onsubmit=\"return validar('Enviando...')\">");
			html.append("	<div class=\"out\">\n");
			html.append("		<div class=\"in\">\n");
			html.append("			<p><label for=\"nss\" id=\"nsslbl\">Número de la Seguridad Social</label></p>\n");
			html.append("			<p><input type=\"text\" id=\"nss\" name=\"nss\" oninput=\"if (error) limpiar('nss')\"/></p>\n");
			html.append("			<p><label for=\"nombre\" id=\"nombrelbl\">Nombre</label></p>\n");
			html.append("			<p><input type=\"text\" id=\"nombre\" name=\"nombre\" oninput=\"if (error) limpiar('nombre')\"/></p>\n");
			html.append("			<p><label for=\"salario\" id=\"salariolbl\">Salario</label></p>\n");
			html.append("			<p><input type=\"text\" id=\"salario\" name=\"salario\" oninput=\"if (error) limpiar('salario')\"/></p>\n");
			html.append("		</div>\n");
			html.append("	</div>\n");
			html.append("	<div class=\"out\">\n");
			html.append("		<div class=\"in\">\n");
			html.append("			<p><label for=\"espTxt\" id=\"esplbl\">Especialidades</label></p>\n");
			html.append("			<p class=\"esptxt\"><input type=\"text\" id=\"esp\" oninput=\"if (error) limpiar('esp')\" /></p>\n");
			html.append("			<p class=\"esp\"><select id=\"esplst\" name=\"esp[]\" size=\"3\" tabindex=\"-1\" multiple></select></p>\n");
			html.append("			<p class=\"esp\"><input type=\"button\" value=\"Añadir\" onclick=\"addEspecialidad()\" />\n");
			html.append("			<input type=\"button\" value=\"Eliminar\" onclick=\"delEspecialidad()\" /></p>\n");
			html.append("			<p><label for=\"depto\">Departamento</label>\n");
			html.append("			<select name=\"depto\">\n");
			for (String departamento : departamentos)
				html.append(String.format("\t\t\t\t<option>%s</option>\n", departamento));
			html.append(departamentos);
			html.append("			</select></p>\n");
			html.append("		</div>\n");
			html.append("	</div><div class=\"clear\">\n");
			html.append("	<p class=\"bottom\"><input id=\"enviar\" type=\"submit\" value=\"Enviar\" /></p>\n");
			html.append("	</form>\n");
			html.append("	</div>");
		}
		HTML.enviarPagina(session, response, html.toString(), titulo, "frmingeniero", "frmingeniero");
	}
	
	public static void enviarFormUsuario(HttpSession session, HttpServletResponse response, String action, String titulo, String id, String txtSubmitting, String txtSubmit, Estado estado) throws IOException {
		StringBuilder html = new StringBuilder();
		
		html.append("	<div class=\"form\">\n");
		if (estado != Estado.OK)
			html.append(divError(estado.toString()));
		html.append(String.format("	<form action=\"%s\" method=\"post\" onsubmit=\"return validar('%s')\">", action, txtSubmitting)); 
		html.append("		<p><label for=\"id\" id=\"idlbl\">Usuario</label></p>\n");
		html.append("		<p><input type=\"text\" id=\"id\" name=\"id\" oninput=\"if (error) limpiar('id')\"");
		if (estado != Estado.OK) {
			if (id != null)
				html.append(String.format(" value=\"%s\"", id));
		}
		html.append(" /></p>\n");
		html.append("		<p><label for=\"password\" id=\"passwordlbl\">Contraseña</label></p>\n");
		html.append("		<p><input type=\"password\" id=\"password\" name=\"password\" oninput=\"if (error) limpiar('password')\"/></p>\n");
		html.append(String.format("		<p class=\"bottom\"><input id=\"enviar\" type=\"submit\" value=\"%s\" /></p>\n", txtSubmit));
		html.append("	</form>\n");
		html.append("	</div>");
		HTML.enviarPagina(session, response, html.toString(), titulo, "frmusuario", "frmusuario");
	}
	
}
