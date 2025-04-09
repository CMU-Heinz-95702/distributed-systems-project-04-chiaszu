
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to handle TheMealDB API calls
 * @author Chiaszu Kuo (chiaszuk)
 */
public class MealDBClient {
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";

    /**
     * Search recipes by name
     * @param name Recipe name to search for
     * @return List of recipes matching the search term
     */
    public List<Recipe> searchByName(String name) throws IOException {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String endpoint = BASE_URL + "search.php?s=" + encodedName;
        return executeSearch(endpoint);
    }

    /**
     * Search recipes by main ingredient
     * @param ingredient Main ingredient to search for
     * @return List of recipes containing the ingredient
     */
    public List<Recipe> searchByIngredient(String ingredient) throws IOException {
        String encodedIngredient = URLEncoder.encode(ingredient, StandardCharsets.UTF_8);
        String endpoint = BASE_URL + "filter.php?i=" + encodedIngredient;
        return executeSearch(endpoint);
    }

    /**
     * Search recipes by category
     * @param category Category to search for
     * @return List of recipes in the category
     */
    public List<Recipe> searchByCategory(String category) throws IOException {
        String encodedCategory = URLEncoder.encode(category, StandardCharsets.UTF_8);
        String endpoint = BASE_URL + "filter.php?c=" + encodedCategory;
        return executeSearch(endpoint);
    }

    private List<Recipe> executeSearch(String endpoint) throws IOException {
        URL url = URI.create(endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray meals = jsonResponse.getJSONArray("meals");
        List<Recipe> recipes = new ArrayList<>();

        for (int i = 0; i < meals.length(); i++) {
            JSONObject meal = meals.getJSONObject(i);
            Recipe recipe = new Recipe();
            recipe.setId(meal.getString("idMeal"));
            recipe.setName(meal.getString("strMeal"));
            if (!meal.isNull("strCategory")) {
                recipe.setCategory(meal.getString("strCategory"));
            }
            if (!meal.isNull("strInstructions")) {
                recipe.setInstructions(meal.getString("strInstructions"));
            }
            if (!meal.isNull("strMealThumb")) {
                recipe.setThumbnail(meal.getString("strMealThumb"));
            }

            // Extract ingredients and measurements
            List<String> ingredients = new ArrayList<>();
            List<String> measurements = new ArrayList<>();
            for (int j = 1; j <= 20; j++) {
                String ingredient = meal.optString("strIngredient" + j);
                String measure = meal.optString("strMeasure" + j);
                if (ingredient != null && !ingredient.trim().isEmpty()) {
                    ingredients.add(ingredient);
                    measurements.add(measure);
                }
            }
            recipe.setIngredients(ingredients.toArray(new String[0]));
            recipe.setMeasurements(measurements.toArray(new String[0]));
            
            recipes.add(recipe);
        }

        return recipes;
    }
} 