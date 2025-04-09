
/**
 * Model class representing a recipe from TheMealDB API
 * @author Chiaszu Kuo (chiaszuk)
 */
public class Recipe {
    private String id;
    private String name;
    private String category;
    private String instructions;
    private String thumbnail;
    private String[] ingredients;
    private String[] measurements;

    public Recipe() {
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String[] getIngredients() { return ingredients; }
    public void setIngredients(String[] ingredients) { this.ingredients = ingredients; }

    public String[] getMeasurements() { return measurements; }
    public void setMeasurements(String[] measurements) { this.measurements = measurements; }
} 