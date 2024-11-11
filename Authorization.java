import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class Authorization extends HttpServlet {

    // Simulating user role from session
    private boolean isAdmin(HttpServletRequest request) {
        String userRole = (String) request.getSession().getAttribute("userRole");
        return "admin".equals(userRole);
    }

    // Handling GET requests
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // Admin access control
        if ("viewAdminPage".equals(action)) {
            if (isAdmin(request)) {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<h1>Admin Page</h1>");
                out.println("<p>Welcome to the admin area.</p>");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this page.");
            }
        } 
        // IDOR test: Allow users to view their own profile
        else if ("viewUserProfile".equals(action)) {
            String userId = request.getParameter("userId");
            if (userId == null || !userId.equals(request.getSession().getAttribute("userId"))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access to this profile is forbidden.");
            } else {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<h1>User Profile</h1>");
                out.println("<p>Your user profile info here.</p>");
            }
        } 
        // Default action
        else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h1>Welcome to Authorization Servlet!</h1>");
        }
    }

    // Handling POST requests for login simulation
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Simple login logic
        if ("admin".equals(username) && "admin123".equals(password)) {
            request.getSession().setAttribute("userRole", "admin");
            request.getSession().setAttribute("userId", "adminUser");
            response.sendRedirect("index.jsp");
        } else if ("user".equals(username) && "user123".equals(password)) {
            request.getSession().setAttribute("userRole", "user");
            request.getSession().setAttribute("userId", "regularUser");
            response.sendRedirect("index.jsp");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials.");
        }
    }
}
