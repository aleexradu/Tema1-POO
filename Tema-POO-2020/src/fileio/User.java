package fileio;

import java.util.ArrayList;
import java.util.Map;

public final class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final ArrayList<String> favoriteMovies;
    // retine titlul serialul caruia i-a fost acordat un rating
    public ArrayList<String> seriesGradesTitle = new ArrayList<>();
    // Retine numarul sezonului caruia i-a fost deja acordat un rating
    public ArrayList<Integer> seriesGradesSeasonNr = new ArrayList<>();
    // retine titlul filmului caruia i-a fost deja acordat un rating
    public ArrayList<String> moviesGrades = new ArrayList<>();

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history, final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
    }

    public ArrayList<String> getMoviesGrades() {
        return moviesGrades;
    }

    public ArrayList<String> getSeriesGradesTitle() {
        return seriesGradesTitle;
    }

    public ArrayList<Integer> getSeriesGradesSeasonNr() {
        return seriesGradesSeasonNr;
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }
}
