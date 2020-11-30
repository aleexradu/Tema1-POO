package fileio;

import java.util.ArrayList;

public final class Movie extends Video {
    private final int duration;
    // retine notele acordate filmului respectiv
    private final ArrayList<Double> moviesNotes = new ArrayList<>();
    private Double finalMovieNote; // retine rating-ul total acordat filmului
    public int finalMovieViews;
    public int favoriteMovieCount;

    public Movie(final String title, final ArrayList<String> cast, final ArrayList<String> genres,
                  final int year, final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
    }

    public int getFavoriteMovieCount() {
        return favoriteMovieCount;
    }

    public Double getFinalMovieNote() {
        return finalMovieNote;
    }

    public void setFinalMovieNote(Double finalMovieNote) {
        this.finalMovieNote = finalMovieNote;
    }

    public ArrayList<Double> getMoviesNotes() {
        return moviesNotes;
    }

    public int getDuration() {
        return duration;
    }
}
