import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/frmingeniero")
public class FormAltaIngenieroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Map<String, String[]> frm = request.getParameterMap();
    	if (frm.isEmpty())
    		enviarFormulario(response);
    	else
    		altaIngeniero(response, frm);
    }
    
    protected void altaIngeniero(HttpServletResponse response, Map<String, String[]> frm) throws IOException {
    	Connection connection = null;
    	String sql = null;
    	try {
			String nss = frm.get("nss")[0];
			String nombre = frm.get("nombre")[0];
			String salario = frm.get("salario")[0];
			String depto = frm.get("depto")[0];
			String [] especialidades = frm.get("esp[]");
			
			connection = DriverManager.getConnection("jdbc:mysql://localhost/Gestión de Empleados?user=julio&password=practicas");
			Statement statement = connection.createStatement();

			sql = String.format("insert into empleados values (%s, '%s', %s)", nss, nombre, salario);
			statement.executeUpdate(sql);
			sql = String.format("insert into ingenieros values (%s, '%s')", nss, depto);
			statement.executeUpdate(sql);
			if (especialidades != null) {
				StringBuilder aux = new StringBuilder("insert into especialidades values ");
				for (int i=0; i<especialidades.length; i++) {
					aux.append(String.format("(%s, '%s')", nss, especialidades[i]));
					if (i < especialidades.length - 1)
						aux.append(", ");
				}
				sql = aux.toString();
				statement.executeUpdate(sql);
			}
			response.sendRedirect("frmingeniero");
		} catch (SQLException e) {
			StringBuilder contenido = new StringBuilder();
			contenido.append("<p>Se ha producido un error al intentar actualizar la base de datos:</p>");
			if (sql != null) {
				contenido.append("<p>Sentencia: ");
				contenido.append(sql);
				contenido.append("</p>");
			}
			contenido.append("<p>" + e.getLocalizedMessage() + "</p>");
			contenido.append("<p><a href=\"frmingeniero\">Retornar al formulario</a></p>");
			HTML5.enviarPagina(response, contenido.toString(), "Error", null, null);
		} finally {	
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
				}
		}
		
    }
    
    protected void enviarFormulario(HttpServletResponse response) throws IOException {
		StringBuilder contenido = new StringBuilder();
		contenido.append("<header>\n");
		contenido.append("	<img src=\"img/ingenieros.png\" />\n");
		contenido.append("	<h1>Gestión de Empleados</h1>\n");
		contenido.append("	<h2>Altas de Ingenieros</h2>\n");
		contenido.append("</header>\n");
		contenido.append("<div class=\"clear\"></div>\n");
		contenido.append("<form action=\"frmingeniero\" method=\"get\" onsubmit=\"return validar()\">\n");
		contenido.append("	<div id=\"out\"><div class=\"out\">\n");
		contenido.append("		<div class=\"in\">\n");
		contenido.append("			<p><label for=\"nss\" id=\"nsslbl\">Número de la Seguridad Social</label></p>\n");
		contenido.append("			<p><input type=\"text\" id=\"nss\" name=\"nss\" oninput=\"if (error) limpiar('nss')\"/></p>\n");
		contenido.append("			<p><label for=\"nombre\" id=\"nombrelbl\">Nombre</label></p>\n");
		contenido.append("			<p><input type=\"text\" id=\"nombre\" name=\"nombre\" oninput=\"if (error) limpiar('nombre')\"/></p>\n");
		contenido.append("			<p><label for=\"salario\" id=\"salariolbl\">Salario</label></p>\n");
		contenido.append("			<p><input type=\"text\" id=\"salario\" name=\"salario\" oninput=\"if (error) limpiar('salario')\"/></p>\n");
		contenido.append("		</div>\n");
		contenido.append("	</div>\n");
		contenido.append("	<div class=\"out\">\n");
		contenido.append("		<div class=\"in\">\n");
		contenido.append("			<p><label for=\"espTxt\" id=\"esplbl\">Especialidades</label></p>\n");
		contenido.append("			<p class=\"esptxt\"><input type=\"text\" id=\"esp\" oninput=\"if (error) limpiar('esp')\" /></p>\n");
		contenido.append("			<p class=\"esp\"><select id=\"esplst\" name=\"esp[]\" size=\"3\" tabindex=\"-1\" multiple></select></p>\n");
		contenido.append("			<p class=\"esp\"><input type=\"button\" value=\"Añadir\" onclick=\"addEspecialidad()\" />\n");
		contenido.append("			<input type=\"button\" value=\"Eliminar\" onclick=\"delEspecialidad()\" /></p>\n");
		contenido.append("			<p><label for=\"depto\">Departamento</label>\n");
		contenido.append("			<select name=\"depto\">\n");
		contenido.append(listaDepartamentos());
		contenido.append("			</select></p>\n");
		contenido.append("		</div>\n");
		contenido.append("	</div><div class=\"clear\"></div></div>\n");
		contenido.append("	<p id=\"enviar\"><input type=\"submit\" value=\"Enviar\" /></p>\n");
		contenido.append("</form>\n");
		HTML5.enviarPagina(response, contenido.toString(), "Altas de ingenieros", "frmingeniero", "frmingeniero");
	}

	private String listaDepartamentos() {
		StringBuilder sb = new StringBuilder();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/Gestión de Empleados?user=julio&password=practicas");
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("select nombre from departamentos");
			while (resultSet.next()) {
				sb.append("\t\t\t\t<option>");
				sb.append(resultSet.getString("nombre"));
				sb.append("</option>\n");
			}
		} catch (SQLException e) {
		}
		return sb.toString();
	}
	
}
