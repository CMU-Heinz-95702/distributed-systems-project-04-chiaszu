
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Servlet to handle recipe search requests
 * @author Chiaszu Kuo (chiaszuk)
 */
@WebServlet(name = "RecipeSearchServlet", urlPatterns = {"/recipe/search"})
public class RecipeSearchServlet extends HttpServlet {
    private MealDBClient mealDBClient;
    private MongoLogger mongoLogger;
    private static final int MAX_SEARCH_TERM_LENGTH = 100;
    
    @Override
    public void init() throws ServletException {
        super.init();
        mealDBClient = new MealDBClient();
        mongoLogger = new MongoLogger("mongodb+srv://chiaszuk:aHxXfdm5a77da7Ah@cluster0.u3zw0.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        String searchType = "";
        String searchTerm = "";
        String deviceInfo = "";
        
        try {
            // Get and validate search parameters
            searchType = validateSearchType(request.getParameter("type")); // search for recipe by name, ingredient, or category
            searchTerm = validateSearchTerm(request.getParameter("term")); // terms to search for
            deviceInfo = request.getHeader("User-Agent");
            
            if (deviceInfo == null || deviceInfo.trim().isEmpty()) {
                deviceInfo = "Unknown Device";
            }

            // Perform search based on type
            List<Recipe> recipes;
            try {
                recipes = performSearch(searchType, searchTerm);
            } catch (UnknownHostException | SocketTimeoutException e) { // refer to LLM
                // Third-party API unavailable
                sendErrorResponse(response, 
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE, // Returns HTTP 503
                    "Recipe service is currently unavailable. Please try again later.",
                    "API_UNAVAILABLE");
                return;
            } catch (IOException e) {
                // Other API errors
                sendErrorResponse(response, 
                    HttpServletResponse.SC_BAD_GATEWAY, // Returns HTTP 502
                    "Error communicating with recipe service: " + e.getMessage(),
                    "API_ERROR");
                return;
            }

            // Handle empty or invalid API response
            if (recipes == null || recipes.isEmpty()) {
                sendErrorResponse(response,
                    HttpServletResponse.SC_NOT_FOUND, // Returns HTTP 404
                    "No recipes found for: " + searchTerm,
                    "NO_RESULTS");
                return;
            }

            // Create JSON response
            JSONObject jsonResponse = createJsonResponse(recipes, searchType);
            
            // Log the successful search
            long responseTime = System.currentTimeMillis() - startTime;
            try {
                mongoLogger.logSearch(searchType, searchTerm, recipes.size(), deviceInfo, responseTime);
            } catch (Exception e) {
                // Log to console if MongoDB logging fails, but don't interrupt the response
                System.err.println("MongoDB logging failed: " + e.getMessage());
            }

            // Send success response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
            out.flush();

        } catch (IllegalArgumentException e) {
            // Invalid input parameters
            sendErrorResponse(response,
                HttpServletResponse.SC_BAD_REQUEST,
                e.getMessage(),
                "INVALID_INPUT");
        } catch (Exception e) {
            // Unexpected server errors
            sendErrorResponse(response,
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                "SERVER_ERROR");
            // Log the error for debugging
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String validateSearchType(String searchType) {
        if (searchType == null || searchType.trim().isEmpty()) {
            throw new IllegalArgumentException("Search type is required");
        }
        searchType = searchType.trim().toLowerCase();
        if (!isValidSearchType(searchType)) {
            throw new IllegalArgumentException("Invalid search type. Use 'name', 'ingredient', or 'category'");
        }
        return searchType;
    }

    private String validateSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term is required");
        }
        searchTerm = searchTerm.trim();
        if (searchTerm.length() > MAX_SEARCH_TERM_LENGTH) {
            throw new IllegalArgumentException("Search term is too long (max " + MAX_SEARCH_TERM_LENGTH + " characters)");
        }
        if (!searchTerm.matches("^[a-zA-Z0-9\\s-]+$")) {
            throw new IllegalArgumentException("Search term contains invalid characters");
        }
        return searchTerm;
    }

    private List<Recipe> performSearch(String searchType, String searchTerm) throws IOException {
        switch (searchType) {
            case "name":
                return mealDBClient.searchByName(searchTerm);
            case "ingredient":
                return mealDBClient.searchByIngredient(searchTerm);
            case "category":
                return mealDBClient.searchByCategory(searchTerm);
            default:
                return mealDBClient.searchByName(searchTerm);
        }
    }

    private JSONObject createJsonResponse(List<Recipe> recipes, String searchType) {
        JSONObject jsonResponse = new JSONObject();
        JSONArray recipesArray = new JSONArray();
        
        for (Recipe recipe : recipes) {
            JSONObject recipeJson = new JSONObject();
            recipeJson.put("id", recipe.getId());
            recipeJson.put("name", recipe.getName());
            recipeJson.put("category", recipe.getCategory());
            recipeJson.put("thumbnail", recipe.getThumbnail());
            // Only include instructions and ingredients if searching by name
            if (searchType.equals("name")) {
                recipeJson.put("instructions", recipe.getInstructions());
                recipeJson.put("ingredients", recipe.getIngredients());
                recipeJson.put("measurements", recipe.getMeasurements());
            }
            recipesArray.put(recipeJson);
        }
        
        jsonResponse.put("recipes", recipesArray);
        jsonResponse.put("count", recipes.size());
        return jsonResponse;
    }

    private boolean isValidSearchType(String searchType) {
        return searchType.equals("name") || 
               searchType.equals("ingredient") || 
               searchType.equals("category");
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message, String errorCode) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        
        JSONObject error = new JSONObject();
        error.put("error", message);
        error.put("errorCode", errorCode);
        error.put("status", statusCode);
        
        PrintWriter out = response.getWriter();
        out.print(error.toString());
        out.flush();
    }
} 