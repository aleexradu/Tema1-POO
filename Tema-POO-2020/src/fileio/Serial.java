package fileio;

import entertainment.Season;

import java.util.ArrayList;

public final class Serial extends Video {
    private final int numberOfSeasons;
    private final ArrayList<Season> seasons;
    // retine rating-ul acordat pt fiecare sezon, de la fiecare user in parte
    public ArrayList<Integer> serialNotesSeasonNr = new ArrayList<>();
    public ArrayList<Double> serialNotesRatings = new ArrayList<>();
    // retine rating-ul total pentru fiecare sezon in parte
    public ArrayList<Integer> finalSeasonNotesSeasonNr = new ArrayList<>();
    public ArrayList<Double> finalSeasonNotesRatings = new ArrayList<>();
    public Double finalSerialNote; // retine rating-ul total acordat serialului
    public int serialDuration;
    public int finalSerialViews;
    public Serial(final String title, final ArrayList<String> cast, final ArrayList<String> genres,
                  final int numberOfSeasons, final ArrayList<Season> seasons, final int year) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public ArrayList<Integer> getSerialNotesSeasonNr() {
        return serialNotesSeasonNr;
    }

    public int getFinalSerialViews() {
        return finalSerialViews;
    }

    public ArrayList<Double> getSerialNotesRatings() {
        return serialNotesRatings;
    }

    public ArrayList<Integer> getFinalSeasonNotesSeasonNr() {
        return finalSeasonNotesSeasonNr;
    }

    public ArrayList<Double> getFinalSeasonNotesRatings() {
        return finalSeasonNotesRatings;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }
}
