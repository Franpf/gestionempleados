
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session != null)
			response.sendRedirect("/ge/");
		else {
			String id = request.getParameter("id");
			String password = request.getParameter("password");
			if (id != null && password != null) {
				Estado estado = iniciarSesion(request, response, id, password);
				if (estado == Estado.OK)
					response.sendRedirect("/ge/");
				else
					HTML.enviarFormUsuario(null, response, "login", "Inicio de Sesión", id, "Iniciando sesión...", "Iniciar sesión", estado);
			}
			else
				HTML.enviarFormUsuario(null, response, "login", "Inicio de Sesión", id, "Iniciando sesión...", "Iniciar sesión", Estado.OK);
		}
	}
	
	private Estado iniciarSesion(HttpServletRequest request, HttpServletResponse response,
			String id, String password) throws IOException, ServletException {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String sql = null;
		
		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			sql = String.format("select password from usuarios where id='%s'", id);
			resultSet = statement.executeQuery(sql);
			if (resultSet.next()) {
				String hash = resultSet.getString("password");
				byte[] salt = Base64.getDecoder().decode(hash.substring(21, 53));
				String hashBD = hash.substring(0, 21) + hash.substring(53);
				SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 0xffff, 1024);
				String hashFRM = Base64.getEncoder().encodeToString(Arrays.copyOf(factory.generateSecret(spec).getEncoded(), 129));
				if (hashBD.equals(hashFRM)) {
					request.getSession().setAttribute("usuario", id);
					return Estado.OK;
				} else
					return Estado.FALLO_LOGIN;
			} else 
				return Estado.FALLO_LOGIN;
		} catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchElementException e) {
			Logger.getLogger(AltaIngenieroServlet.class.getName()).log(Level.SEVERE, null, e);
			return Estado.ERROR_LOGIN;
		} finally {
			if (resultSet != null)
				try { resultSet.close(); } catch (SQLException e1) {}
			if (statement != null)
				try { statement.close(); } catch (SQLException e1) {}
			if (connection != null)
				try { connection.close(); } catch (SQLException e) {}
		}
	}
	
}
