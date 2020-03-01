
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
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

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataSource dataSource;
	private String titulo = "Registro de Usuarios";
	
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
			String usuario = (String) session.getAttribute("usuario");
			if (usuario.equals("admin")) {
				Estado estado;
				String id = request.getParameter("id");
				String password = request.getParameter("password");
				if (id != null && password != null)
					estado = altaUsuario(request, response, id, password);
				else
					estado = Estado.OK;
				HTML.enviarFormUsuario(session, response, "registro", titulo, id, "Registrando usuario...",
						"Registrar usuario", estado);
				return;
			}
		}
		HTML.enviarSinPermiso(response);
	}

	protected Estado altaUsuario(HttpServletRequest request, HttpServletResponse response, String id, String password)
			throws IOException {
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			String sql = String.format("insert into usuarios values ('%s', '%s')", id, HashPassword.hashBase64(password));
			statement.executeUpdate(sql);
			return Estado.OK;
		} catch (SQLIntegrityConstraintViolationException e) {
			Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
			return Estado.FALLO_REGISTRO;
		} catch (SQLException e) {
			Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
			return Estado.ERROR_REGISTRO;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e1) {
				}
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e1) {
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
				}
		}
	}

}
