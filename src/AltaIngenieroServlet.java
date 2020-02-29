import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@WebServlet("/altaingeniero")
public class AltaIngenieroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataSource dataSource;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			InitialContext context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/ge");
			if (dataSource == null)
				throw new ServletException("DataSource desconocido");
		} catch (NamingException e) {
			Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Estado estado;
			ArrayList<String> departamentos = obtenerDepartamentos();
			if (departamentos != null) {
				Map<String, String[]> datosFormulario = request.getParameterMap();
				if (datosFormulario.isEmpty())
					estado = Estado.OK;
				else
					estado = altaIngeniero(request, response, datosFormulario);	
			}
			else
				estado = Estado.ERROR_INTERNO;
			HTML.enviarFormAltaIngenieros(session, response, estado, departamentos);
		} else {
			HTML.enviarSinPermiso(response);
		}
	}

	private ArrayList<String> obtenerDepartamentos() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("select nombre from departamentos");
			ArrayList<String> departamentos = new ArrayList<>();
			while (resultSet.next())
				departamentos.add(resultSet.getString("nombre"));
			return departamentos;
		} catch (SQLException e) {
			Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
			return null;
		} finally {
			if (resultSet != null)
				try { resultSet.close(); } catch (SQLException e1) {}
			if (statement != null)
				try { statement.close(); } catch (SQLException e1) {}
			if (connection != null)
				try { connection.close(); } catch (SQLException e) {}
		}
	}

	private Estado altaIngeniero(HttpServletRequest request, HttpServletResponse response, Map<String, String[]> datosFormulario)
			throws IOException {
		Connection connection = null;
		Statement statement = null;
		String sql = null;
		try {
			String nss = datosFormulario.get("nss")[0];
			String nombre = datosFormulario.get("nombre")[0];
			String salario = datosFormulario.get("salario")[0];
			String depto = datosFormulario.get("depto")[0];
			String[] especialidades = datosFormulario.get("esp[]");
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			sql = String.format("insert into empleados values (%s, '%s', %s)", nss, nombre, salario);
			statement.executeUpdate(sql);
			sql = String.format("insert into ingenieros values (%s, '%s')", nss, depto);
			statement.executeUpdate(sql);
			if (especialidades != null) {
				StringBuilder aux = new StringBuilder("insert into especialidades values ");
				for (int i = 0; i < especialidades.length; i++) {
					aux.append(String.format("(%s, '%s')", nss, especialidades[i]));
					if (i < especialidades.length - 1)
						aux.append(", ");
				}
				sql = aux.toString();
				statement.executeUpdate(sql);
			}
			connection.commit();
			return Estado.OK;
		} catch (SQLIntegrityConstraintViolationException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
			}
			Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
			return Estado.FALLO_ALTAING;
		} catch (SQLException e) {
			if (connection != null)
				try {
					connection.rollback();
				} catch (SQLException e1) {
					Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
				}
			Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
			return Estado.ERROR_ALTAING;
		} finally {
			if (statement != null)
				try { statement.close(); } catch (SQLException e1) {}
			if (connection != null)
				try { connection.close(); } catch (SQLException e) {}
		}

	}

}
