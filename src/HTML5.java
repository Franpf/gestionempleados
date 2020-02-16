import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class HTML5 {

	static void enviarPagina(HttpServletResponse response, String contenido, String titulo, String css, String js) throws IOException {
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.println("<!DOCTYPE html>"); 
			out.println("<html>"); 
			out.println("<head>"); 
			out.println("<meta charset=\"UTF-8\">"); 
			out.println("<title>" + titulo + "</title>");
			if (css != null)
				out.printf("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/%s.css\" media=\"screen\" />\n", css);
			if (js != null)
				out.printf("<script type=\"text/javascript\" src=\"js/%s.js\"></script>\n", js);
			out.println("</head>"); 
			out.println("<body onload=\"load()\">");
			out.print(contenido);
			out.println("</body>");
			out.println("</html>");
		} finally {
			if (out != null)
				out.close();
		}
	}
	
}
