

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.mongodb.client.FindIterable;
import com.mongodb.client.AggregateIterable;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet to handle dashboard requests
 * @author Chiaszu Kuo
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    private MongoLogger mongoLogger;

    @Override
    public void init() throws ServletException {
        super.init();
        mongoLogger = new MongoLogger("mongodb+srv://chiaszuk:aHxXfdm5a77da7Ah@cluster0.u3zw0.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
    }

    // handles HTTP GET requests
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get analytics data
            double avgResponseTime = mongoLogger.getAverageResponseTime();
            
            // Get most searched terms for each search type
            AggregateIterable<Document> mostSearchedNames = mongoLogger.getMostSearchedTerms("name");
            AggregateIterable<Document> mostSearchedIngredients = mongoLogger.getMostSearchedTerms("ingredient");
            AggregateIterable<Document> mostSearchedCategories = mongoLogger.getMostSearchedTerms("category");
            
            // Get recent logs
            FindIterable<Document> recentLogs = mongoLogger.getAllLogs();
            
            // Convert MongoDB cursors to Lists for JSP
            List<Document> namesList = new ArrayList<>();
            List<Document> ingredientsList = new ArrayList<>();
            List<Document> categoriesList = new ArrayList<>();
            List<Document> logsList = new ArrayList<>();

            // refer to LLM
            mostSearchedNames.forEach(namesList::add);
            mostSearchedIngredients.forEach(ingredientsList::add);
            mostSearchedCategories.forEach(categoriesList::add);
            recentLogs.forEach(logsList::add);
            
            // Set attributes for JSP
            request.setAttribute("avgResponseTime", String.format("%.2f", avgResponseTime));
            request.setAttribute("mostSearchedNames", namesList);
            request.setAttribute("mostSearchedIngredients", ingredientsList);
            request.setAttribute("mostSearchedCategories", categoriesList);
            request.setAttribute("recentLogs", logsList);
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Log error and show error page
            System.err.println("Dashboard error: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Error loading dashboard: " + e.getMessage());
        }
    }
} 