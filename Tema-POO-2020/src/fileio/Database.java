package fileio;

import actor.ActorsAwards;
import entertainment.Season;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.LinkedHashMap;

@SuppressWarnings("unchecked")
public class Database {
    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<Movie> movies = new ArrayList<>();
    private final ArrayList<Serial> serials = new ArrayList<>();
    private final ArrayList<Actor> actors = new ArrayList<>();
    private final Input input;
    static final int FIRST = 1;
    static final int SECOND = 2;
    static final int THIRD = 3;

    public Database(final Input input) {
        this.input = input;
    }

    /**
     *
     * @param fileWriter fileWriter
     * @param arrayResult arrayResult
     * @throws IOException IOexception
     */
    public void init(final Writer fileWriter, final org.json.simple.JSONArray arrayResult)
            throws IOException {
        for (UserInputData data : input.getUsers()) {
            User user = new User(data.getUsername(), data.getSubscriptionType(), data.getHistory(),
                                 data.getFavoriteMovies());
            users.add(user);
        }
        for (MovieInputData data : input.getMovies()) {
            Movie movie = new Movie(data.getTitle(), data.getCast(), data.getGenres(),
                                    data.getYear(), data.getDuration());
            movies.add(movie);
        }
        for (SerialInputData data : input.getSerials()) {
            Serial serial = new Serial(data.getTitle(), data.getCast(), data.getGenres(),
                    data.getNumberSeason(), data.getSeasons(), data.getYear());
            serials.add(serial);
        }
        for (ActorInputData data : input.getActors()) {
            Actor actor = new Actor(data.getName(), data.getCareerDescription(),
                                    data.getFilmography(), data.getAwards());
            actors.add(actor);
        }
        for (ActionInputData data : input.getCommands()) {
            if (data.getActionType().equals("command")) {
                if (data.getType().equals("favorite")) {
                    if (favorite(data.getUsername(), data.getTitle()) == FIRST) {
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "a",
                        "success -> " + data.getTitle() + " was added as favourite"));
                    } else if (favorite(data.getUsername(), data.getTitle()) == SECOND) {
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "a",
                        "error -> " + data.getTitle() + " is already in favourite list"));
                    } else if (favorite(data.getUsername(), data.getTitle()) == THIRD) {
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "a",
                        "error -> " + data.getTitle() + " is not seen"));
                    }
                } else if (data.getType().equals("view")) {
                    arrayResult.add(fileWriter.writeFile(data.getActionId(), "a",
                    view(data.getUsername(), data.getTitle())));
                } else if (data.getType().equals("rating")) {
                    if (data.getSeasonNumber() == 0) {
                        if (ratingMovie(data.getUsername(), data.getTitle(),
                                data.getGrade()) == FIRST) {
                            arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", "success -> " + data.getTitle() + " was rated"
                            + " with " + data.getGrade() + " by " + data.getUsername()));
                        } else if (ratingMovie(data.getUsername(), data.getTitle(),
                                data.getGrade()) == THIRD) {
                            arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", "error -> " + data.getTitle() + " is not seen"));
                        } else if (ratingMovie(data.getUsername(), data.getTitle(),
                                data.getGrade()) == SECOND) {
                            arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", "error -> " + data.getTitle()
                                            + " has been already rated"));
                        }
                    } else {
                        if (ratingShow(data.getUsername(), data.getTitle(), data.getGrade(),
                                data.getSeasonNumber()) == FIRST) {
                            arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", "success -> " + data.getTitle()
                            + " was rated with " + data.getGrade() + " by " + data.getUsername()));
                        } else if (ratingShow(data.getUsername(), data.getTitle(),
                                data.getGrade(), data.getSeasonNumber()) == THIRD) {
                            arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", "error -> " + data.getTitle() + " is not seen"));
                        } else if (ratingShow(data.getUsername(), data.getTitle(),
                                data.getGrade(), data.getSeasonNumber()) == SECOND) {
                            arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", "error -> " + data.getTitle()
                                            + " has been already rated"));
                        }
                    }
                }
            } else if ("query".equals(data.getActionType())) {
                if (data.getObjectType().equals("actors")) {
                    if ("average".equals(data.getCriteria())) {
                        arrayResult.add(fileWriter.writeFile(data.getActionId(),
                        "a", "Query result: " + averageActors(data.getNumber(),
                                        data.getSortType())));
                    } else if (data.getCriteria().equals("awards")) {
                        arrayResult.add(fileWriter.writeFile(data
                                .getActionId(), "a", "Query result: "
                                + awardsActors(data.getFilters().get(THIRD),
                                data.getSortType())));
                    } else if (data.getCriteria().equals("filter_description")) {
                        arrayResult.add(fileWriter.writeFile(
                                data.getActionId(), "a", "Query result: "
                                        + filterDescriptionActors(data.getFilters().get(2),
                                        data.getSortType())));
                    }
                } else if (data.getObjectType().equals("movies")) {
                    if (data.getCriteria().equals("ratings")) {
                        arrayResult.add(fileWriter.writeFile(data
                                .getActionId(), "a", "Query result: "
                                + queryRatedMovies(data.getNumber(), data.getFilters()
                                .get(0), data.getFilters().get(1), data.getSortType())));
                    } else if (data.getCriteria().equals("favorite")) {
                        arrayResult.add(fileWriter.writeFile(data
                                .getActionId(), "a", "Query result: "
                                + queryFavoriteMovies(data.getNumber(), data.getFilters()
                                .get(0), data.getFilters().get(1), data.getSortType())));
                    } else if (data.getCriteria().equals("longest")) {
                        arrayResult.add(fileWriter.writeFile(data
                                .getActionId(), "a", "Query result: "
                                + queryLongestMovies(data.getNumber(), data.getFilters()
                                .get(0), data.getFilters().get(1), data.getSortType())));
                    } else if (data.getCriteria().equals("most_viewed")) {
                        arrayResult.add(fileWriter.writeFile(data
                                .getActionId(), "a", "Query result: "
                                + queryMostViewedMovies(data.getNumber(), data
                                        .getFilters().get(0), data.getFilters().get(1),
                                data.getSortType())));
                    }
                } else if (data.getObjectType().equals("shows")) {
                    if (data.getCriteria().equals("ratings")) {
                        arrayResult.add(fileWriter.writeFile(data
                                .getActionId(), "a", "Query result: "
                                + queryRatedShows(data.getNumber(), data.getFilters()
                                .get(0), data.getFilters().get(1), data.getSortType())));
                    } else if (data.getCriteria().equals("favorite")) {
                        arrayResult.add(fileWriter.writeFile(data
                                .getActionId(), "a", "Query result: "
                                + queryFavoriteShows(data.getNumber(), data.getFilters()
                                .get(0), data.getFilters().get(1), data.getSortType())));
                    } else if (data.getCriteria().equals("longest")) {
                        arrayResult.add(fileWriter.writeFile(data
                                .getActionId(), "a", "Query result: "
                                + queryLongestShows(data.getNumber(), data.getFilters()
                                .get(0), data.getFilters().get(1), data.getSortType())));
                    } else if (data.getCriteria().equals("most_viewed")) {
                        arrayResult.add(fileWriter.writeFile(data.
                                getActionId(), "a", "Query result: "
                                + queryMostViewedShows(data.getNumber(), data.getFilters()
                                .get(0), data.getFilters().get(1), data.getSortType())));
                    }
                } else if (data.getObjectType().equals("users")) {
                    if (data.getCriteria().equals("num_ratings")) {
                        arrayResult.add(fileWriter.writeFile(data.getActionId(), "a",
                                "Query result: " + queryUsers(data.getNumber(),
                                        data.getSortType())));
                    }
                }
            } else if ("recommendation".equals(data.getActionType())) {
                if (data.getType().equals("standard")) {
                    arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", standardRecommendation(data.getUsername())));
                } else if (data.getType().equals("best_unseen")) {
                    arrayResult.add(fileWriter.writeFile(data
                            .getActionId(), "a", bestUnseenRecommendation(data
                            .getUsername())));
                } else if (data.getType().equals("popular")) {
                    arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", popularRecommendation(data.getUsername())));
                } else if (data.getType().equals("favorite")) {
                    arrayResult.add(fileWriter.writeFile(data.getActionId(),
                            "a", favoriteRecommendation(data.getUsername())));
                } else if (data.getType().equals("search")) {
                    arrayResult.add(fileWriter.writeFile(data.getActionId(),
                    "a", (String) searchRecommendation(data.getUsername(), data.getGenre())));
                }
            }
        }
    }

    /**
     * Metoda pentru a adauga un video la favorite
     * @param username username-ul cerut
     * @param title titlul video-ului
     * @return returneaza 1 numar
     * Daca returneaza 1, adaugam clipul in lista de favorite.
     * Daca returneaza 2, video-ul se afla in lista de favorite si afisam mesaj de eroare
     * Daca returneaza 3, video-ul nu este vizionat, deci afisam mesaj de eroare specific
     */
    public int favorite(final String username, final String title) {
        User user = null;
        int ok = 2;

        // Parcurgem toti userii pana cand username-ul din comanda este egal cu username-ul din
        // baza de date
        for (User a : users) {
            if (a.getUsername().equals(username)) {
                user = a;
            }
        }

        assert user != null;
        // Verificam daca video-ul se afla in lista de vizionate, cu ajutorul unei variabile
        // Daca ramane nemodificata, inseamna ca se afla in lista de vizionate
        for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
            String name = entry.getKey();
            if (name.equals(title)) {
                ok = 2;
                break;
            } else {
                ok = 1;
            }
        }

        // Verificam daca video-ul se afla in lista de favorite, cu ajutorul altei variabile
        // Daca ramane nemodificata, inseamna ca se afla in lista de favorite
        if (ok == 2) {
            int ok1 = 1;
            for (String b : user.getFavoriteMovies()) {
                if (b.equals(title)) {
                    ok1 = 1;
                    break;

                } else {
                    ok1 = 2;
                }
            }

            if (ok1 == 2) {
                // adauga video-ul in lista de favorite a user-ului
                user.getFavoriteMovies().add(title);
                return 1;
            } else {
                // returneaza un mesaj de eroare, video-ul se afla deja in lista de favorite
                return 2;
            }
        } else {
            // returneaza mesaj de eroare, video-ul nu este vizionat
            return THIRD;
        }

    }

    /**
     * Metoda pentru a viziona un video
     * @param username username-ul cerut
     * @param title titlul video-ului
     * @return returneaza un String
     */
    public String view(final String username, final String title) {
        User user = null;

        for (User a : users) {
            if (a.getUsername().equals(username)) {
                user = a;
            }
        }

        assert user != null;
        // Parcurgem lista de vizionate a userului pana cand gasim video-ul cerut
        // Daca video-ul se gaseste in lista de vizionate, se incrementeaza numarul de view-uri
        // si returnam mesajul specific
        // Daca video-ul nu se gaseste in lista de vizionate, il adaugam si returnam
        // mesajul specific
        for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
            String name = entry.getKey();
            Integer number = entry.getValue();
            if (name.equals(title)) {
                number++;
                user.getHistory().put(name, number);
                return "success -> " + title + " was viewed with total views of " + number;
            }
        }
        user.getHistory().put(title, 1);
        return "success -> " + title + " was viewed with total views of " + 1;

    }

    /**
     * Metoda pentru a acorda rating la un film
     * @param username username-ul cerut
     * @param title titlul filmului
     * @param grade nota pe care utilizatorul o acorda filmului
     * @return returneaza 1 numar
     */
    public int ratingMovie(final String username, final String title, final Double grade) {
        User user = null;
        for (User a : users) {
            if (a.getUsername().equals(username)) {
                user = a;
            }
        }

        Movie movie = null;
        for (Movie a : movies) {
            if (a.getTitle().equals(title)) {
                movie = a;
            }
        }

        int ok = 0;
        if (user != null) {
            for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
                String name = entry.getKey();
                if (name.equals(title)) {
                    ok = 0;
                    break;
                } else {
                    ok = 1;
                }
            }
        }

        if (ok == 0) {
            int ok1 = 0;

            assert user != null;
            if (user.getMoviesGrades().size() == 0) {
                user.getMoviesGrades().add(title);
                assert movie != null;
                movie.getMoviesNotes().add(grade);
                return 1; // adaugam rating pentru serialul respectiv
            } else {
                for (String a : user.getMoviesGrades()) {
                    if (a.equals(title)) {
                        ok1 = 0;
                        break; // has been already rated
                    } else {
                        ok1 = 1; // dam rating
                    }
                }
                if (ok1 == 0) {
                    return SECOND; // sezonulului din serialul respectiv i-a fost deja dat rating
                } else {
                    user.getMoviesGrades().add(title);
                    assert movie != null;
                    movie.getMoviesNotes().add(grade);
                    return FIRST; // adaugam rating pentru serialul respectiv
                }
            }
        } else {
            // returnam mesaj de eroare, nu se poate da rating la un video care nu a fost vizualizat
            return THIRD;
        }
    }

    /**
     * Metoda pentru a acorda rating la un serial
     * @param username username-ul cerut
     * @param title titlul serialului
     * @param grade nota pe care utilizatorul o acorda serialului
     * @param seasonNumber numarul sezonului caruia utilizatorul ii acorda nota
     * @return returneaza int
     */
    public int ratingShow(final String username, final String title, final Double grade,
                          final int seasonNumber) {
        User user = null;

        for (User a : users) {
            if (a.getUsername().equals(username)) {
                user = a;
            }
        }

        Serial serial = null;
        for (Serial a : serials) {
            if (a.getTitle().equals(title)) {
                serial = a;
            }
        }

        assert user != null;
        assert serial != null;
        int ok = 0;

        for (Map.Entry<String, Integer> entry : user.getHistory().entrySet()) {
            String name = entry.getKey();
            if (name.equals(title)) {
                ok = 0;
                break;
            } else {
                ok = 1;
            }
        }

        if (ok == 0) {
            int ok1 = 0;

            if (user.getSeriesGradesTitle().size() == 0 && user.getSeriesGradesSeasonNr()
                    .size() == 0) {
                user.getSeriesGradesTitle().add(title);
                user.getSeriesGradesSeasonNr().add(seasonNumber);
                serial.getSerialNotesSeasonNr().add(seasonNumber);
                serial.getSerialNotesRatings().add(grade);

                return 1; // adaugam rating pentru serialul respectiv
            } else {
                for (int i = 0; i < user.getSeriesGradesTitle().size(); i++) {
                    for (int j = 0; j < user.getSeriesGradesSeasonNr().size(); j++) {
                        if (user.getSeriesGradesTitle().get(i).equals(title) && user
                                .getSeriesGradesSeasonNr().get(j).equals(seasonNumber)) {
                            ok1 = 0;
                            break; // has been already rated
                        } else {
                            ok1 = 1; // dam rating
                        }
                    }
                }
            }
            if (ok1 == 0) {
                return 2; // sezonulului din serialul respectiv i-a fost deja dat rating
            } else {
                user.getSeriesGradesTitle().add(title);
                user.getSeriesGradesSeasonNr().add(seasonNumber);
                serial.getSerialNotesSeasonNr().add(seasonNumber);
                serial.getSerialNotesRatings().add(grade);

                return 1; // adaugam rating pentru serialul respectiv
            }

        } else {
            // returnam mesaj de eroare, nu se poate da rating la un video care nu a fost vizualizat
            return THIRD;
        }
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param sortType Tipul de sortare ceruta(ascendenta sau descendenta)
     * @return returneaza ArrayList
     */
    public ArrayList<String> averageActors(final int number, final String sortType) {
        ArrayList<String> finalActorAverageList = new ArrayList<>();
        ArrayList<String> actorFinalList = new ArrayList<>();
        ArrayList<Double> actorFinalNotes = new ArrayList<>();
        Map<String, Double> actorAverageList = new HashMap<>();
        ArrayList<String> finalList1 = new ArrayList<>();


        //Calculez ratingul total al fiecarui film
        for (Movie a : movies) {
            Double sum = 0.0;
            int count = 0;
            for (int i = 0; i < a.getMoviesNotes().size(); i++) {
                sum += a.getMoviesNotes().get(i);
                count++;
            }
            if (count != 0) {
                a.setFinalMovieNote(sum / count);
            } else {
                a.setFinalMovieNote(0.0);
            }
        }

        //Calculez ratingul total al fiecarui serial, ca suma a rating-urilor pe fiecare sezon
        for (Serial a : serials) {
            Double sum = 0.0;
            for (int i = 0; i < a.getSerialNotesRatings().size(); i++) {
                if (a.getFinalSeasonNotesRatings().size() == 0 && a.getFinalSeasonNotesSeasonNr()
                        .size() == 0) {
                    a.getFinalSeasonNotesSeasonNr().add(a.getSerialNotesSeasonNr().get(i));
                    a.getFinalSeasonNotesRatings().add(a.getSerialNotesRatings().get(i));
                } else {
                    for (int j = 0; j < a.getFinalSeasonNotesRatings().size(); j++) {
                        if (a.getFinalSeasonNotesSeasonNr().get(j)
                                .equals(a.getSerialNotesSeasonNr().get(i))) {
                            a.getFinalSeasonNotesSeasonNr().set(j, a.getSerialNotesSeasonNr()
                                    .get(i));
                            a.getFinalSeasonNotesRatings().set(j, (a.getSerialNotesRatings().get(i)
                                    + a.getFinalSeasonNotesRatings().get(j)) / 2);
                            break;
                        } else {
                            a.getFinalSeasonNotesSeasonNr().add(a.getSerialNotesSeasonNr().get(i));
                            a.getFinalSeasonNotesRatings().add(a.getSerialNotesRatings().get(i));
                        }
                    }
                }
            }
            for (int z = 0; z < a.getFinalSeasonNotesRatings().size(); z++) {
                sum += a.getFinalSeasonNotesRatings().get(z);
            }
            a.finalSerialNote = (sum / a.getNumberOfSeasons());
        }

        // Calculez ratingul total al fiecarui actor si retin intr-o mapa numele si ratingul
        for (Actor b : actors) {
            double sum1 = 0.0;
            int count1 = 0;

            for (Movie c : movies) {
                for (int i = 0; i < b.getFilmography().size(); i++) {
                    if (b.getFilmography().get(i).equals(c.getTitle())) {
                        if (c.getFinalMovieNote() != 0) {
                            sum1 += c.getFinalMovieNote();
                            count1++;
                        }
                    }
                }
            }
            for (Serial d : serials) {
                for (int i = 0; i < b.getFilmography().size(); i++) {
                    if (b.getFilmography().get(i).equals(d.getTitle())) {
                        if (d.finalSerialNote != 0) {
                            sum1 += d.finalSerialNote;
                            count1++;
                        }
                    }
                }
            }
            if (count1 != 0) {
                b.ratingActor = (sum1 / count1);
                actorAverageList.put(b.getName(), b.ratingActor);
            }
        }

        // Sortez mapa ascendent in functie de valori, iar apoi sortez alfabetic numele actorilor,
        // daca acestia au acelasi rating, folosind 2 ArrayList-uri auxiliare
        actorAverageList = sortByValue(actorAverageList);
        int count1 = 0;
        for (Map.Entry<String, Double> entry : actorAverageList.entrySet()) {

            Double note = entry.getValue();
            String name = entry.getKey();
            if (actorFinalNotes.size() == 0) {
                actorFinalNotes.add(note);
                actorFinalList.add(name);
            } else {
                if (Double.compare(note, actorFinalNotes.get(count1)) == 0) {
                    actorFinalList.add(name);
                    actorFinalNotes.add(note);
                    count1++;
                } else if (Double.compare(note, actorFinalNotes.get(count1)) != 0) {
                    Collections.sort(actorFinalList);
                    finalActorAverageList.addAll(actorFinalList);
                    actorFinalList.clear();
                    actorFinalNotes.clear();
                    actorFinalList.add(name);
                    actorFinalNotes.add(note);
                    count1 = 0;
                }
            }

        }
        Collections.sort(actorFinalList);
        finalActorAverageList.addAll(actorFinalList);

        if (sortType.equals("asc")) {

            if (number > finalActorAverageList.size()) {
                return finalActorAverageList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalActorAverageList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalActorAverageList);
            if (number > finalActorAverageList.size()) {
                return finalActorAverageList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalActorAverageList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     * Metoda care sorteaza o mapa de String si Double ascendent, in functie de valori
     * @param unsortMap Mapa nesortata
     * @return returneaza o mapa sortata
     */
    private static Map<String, Double> sortByValue(final Map<String, Double> unsortMap) {
        List<Map.Entry<String, Double>> list =
                new LinkedList<>(unsortMap.entrySet());

        list.sort(Map.Entry.comparingByValue());

        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
    }

    /**
     * Metoda care sorteaza o mapa de String si Integer ascendent, in functie de valori
     * @param unsortMap Mapa nesortata
     * @return a sorted Map
     */
    private static Map<String, Integer> sortByValue1(final Map<String, Integer> unsortMap) {
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(unsortMap.entrySet());

        list.sort(Map.Entry.comparingByValue());

        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     *
     * @param awards Premiile specificate
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> awardsActors(final List<String> awards, final String sortType) {
        Map<String, Integer> actorsList = new HashMap<>();
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<Integer> actorFinalAwards = new ArrayList<>();
        ArrayList<String> actorFinalList = new ArrayList<>();
        for (Actor a : actors) {
            int ok1 = 0;
            for (String award : awards) {
                int ok = 0;
                for (Map.Entry<ActorsAwards, Integer> entry : a.getAwards().entrySet()) {
                    ActorsAwards currAward = entry.getKey();
                    if (currAward.toString().equals(award)) {
                        ok = 1;
                    }
                }
                if (ok == 0) {
                    ok1 = 1;
                    break; // nu s-au gasit toate Award-urile mentionate
                }
            }
            if (ok1 == 0) {
                int sum = 0;
                for (Map.Entry<ActorsAwards, Integer> entry : a.getAwards().entrySet()) {
                    Integer numberAwards = entry.getValue();
                    sum += numberAwards;
                }
                actorsList.put(a.getName(), sum);
            }
        }
        actorsList = sortByValue1(actorsList);
        int count1 = 0;
        for (Map.Entry<String, Integer> entry : actorsList.entrySet()) {

            Integer nrAwards = entry.getValue();
            String name = entry.getKey();
            if (actorFinalAwards.size() == 0) {
                actorFinalAwards.add(nrAwards);
                actorFinalList.add(name);
            } else {
                if (nrAwards.equals(actorFinalAwards.get(count1))) {
                    actorFinalList.add(name);
                    actorFinalAwards.add(nrAwards);
                    count1++;
                } else if (!nrAwards.equals(actorFinalAwards.get(count1))) {
                    Collections.sort(actorFinalList);
                    finalList.addAll(actorFinalList);
                    actorFinalList.clear();
                    actorFinalAwards.clear();
                    actorFinalList.add(name);
                    actorFinalAwards.add(nrAwards);
                    count1 = 0;
                }
            }

        }
        Collections.sort(actorFinalList);
        finalList.addAll(actorFinalList);

        if (sortType.equals("asc")) {
            return finalList;
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
        }
        return finalList;
    }

    /**
     *
     * @param words Cuvintele specificate
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> filterDescriptionActors(final List<String> words,
                                                       final String sortType) {
        ArrayList<String> actorList = new ArrayList<>();
        for (Actor a : actors) {
            String[] wordSplit = a.getCareerDescription().split("[^a-zA-Z]+");
            int ok1 = 0;

            for (String word : words) {
                int ok = 0;
                for (String s : wordSplit) {
                    if (s.toLowerCase().equals(word)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 0) {
                    ok1 = 1;
                    break; // nu s-au gasit toate cuvintele mentionate
                }
            }
            if (ok1 == 0) {
                actorList.add(a.getName());
            }
        }
        Collections.sort(actorList);
        if (sortType.equals("asc")) {
            return actorList;
        } else if (sortType.equals("desc")) {
            Collections.reverse(actorList);
        }
        return actorList;
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param year anul cerut
     * @param genres genul cerut
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return return ArrayList
     */
    public ArrayList<String> queryRatedMovies(final int number, final List<String> year,
                                                final List<String> genres, final String sortType) {
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        for (Movie a : movies) {
            Double sum = 0.0;
            int count = 0;
            for (int i = 0; i < a.getMoviesNotes().size(); i++) {
                sum += a.getMoviesNotes().get(i);
                count++;
            }
            if (count != 0) {
                a.setFinalMovieNote(sum / count);
            } else {
                a.setFinalMovieNote(0.0);
            }
        }

        for (Movie a : movies) {
            if (a.getYear() == Integer.parseInt(year.get(0))) {
                int ok = 0;
                for (String b : a.getGenres()) {
                    if (b.equals(genres.get(0))) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1 && a.getFinalMovieNote() != 0) {
                    finalList.add(a.getTitle());
                }
            }
        }
        Collections.sort(finalList);
        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param year anul cerut
     * @param genres genul cerut
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> queryRatedShows(final int number, final List<String> year,
                                               final List<String> genres, final String sortType) {
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        for (Serial a : serials) {
            Double sum = 0.0;
            for (int i = 0; i < a.getSerialNotesRatings().size(); i++) {
                if (a.getFinalSeasonNotesRatings().size() == 0 && a.getFinalSeasonNotesSeasonNr()
                        .size() == 0) {
                    a.getFinalSeasonNotesSeasonNr().add(a.getSerialNotesSeasonNr().get(i));
                    a.getFinalSeasonNotesRatings().add(a.getSerialNotesRatings().get(i));
                } else {
                    for (int j = 0; j < a.getFinalSeasonNotesRatings().size(); j++) {
                        if (a.getFinalSeasonNotesSeasonNr().get(j).equals(a.getSerialNotesSeasonNr()
                                .get(i))) {
                            a.getFinalSeasonNotesSeasonNr().set(j, a.getSerialNotesSeasonNr()
                                    .get(i));
                            a.getFinalSeasonNotesRatings().set(j, (a.getSerialNotesRatings().get(i)
                                    + a.getFinalSeasonNotesRatings().get(j)) / 2);
                            break;
                        } else {
                            a.getFinalSeasonNotesSeasonNr().add(a.getSerialNotesSeasonNr()
                                    .get(i));
                            a.getFinalSeasonNotesRatings().add(a.getSerialNotesRatings().get(i));
                        }
                    }
                }
            }
            for (int z = 0; z < a.getFinalSeasonNotesRatings().size(); z++) {
                sum += a.getFinalSeasonNotesRatings().get(z);
            }
            a.finalSerialNote = (sum / a.getNumberOfSeasons());
        }

        for (Serial a : serials) {
            if (year.get(0) == null) {
                int ok = 0;
                for (String b : a.getGenres()) {
                    if (b.equals(genres.get(0))) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1 && a.finalSerialNote != 0) {
                    finalList.add(a.getTitle());
                }
            } else {
                if (a.getYear() == Integer.parseInt(year.get(0))) {
                    int ok = 0;
                    for (String b : a.getGenres()) {
                        if (b.equals(genres.get(0))) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 1 && a.finalSerialNote != 0) {
                        finalList.add(a.getTitle());
                    }
                }
            }
        }
        // Daca numarul cerut este mai mare decat dimensiunea arrayList-ului meu, atunci se va afisa
        // tot arrayList-ul. Daca este mai mic, se vor retine elementele in alt arrayList, care
        // va avea dimensiunea egala cu numarul cerut
        Collections.sort(finalList);
        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param year anul cerut
     * @param genres genul cerut
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> queryFavoriteMovies(final int number, final List<String> year,
                                                 final List<String> genres, final String sortType) {
        ArrayList<String> moviesList = new ArrayList<>();
        Map<String, Integer> moviesMap = new HashMap<>();
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> moviesListNames = new ArrayList<>();
        ArrayList<Integer> moviesListNr = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        for (Movie a : movies) {
            if (year.get(0) == null) {
                int ok = 0;
                if (genres.get(0) == null) {
                    moviesList.add(a.getTitle());
                } else {
                    for (String b : a.getGenres()) {
                        if (b.equals(genres.get(0))) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 1) {
                        moviesList.add(a.getTitle());
                    }
                }
            } else {
                if (a.getYear() == Integer.parseInt(year.get(0))) {
                    int ok = 0;
                    if (genres.get(0) == null) {
                        moviesList.add(a.getTitle());
                    } else {
                        for (String b : a.getGenres()) {
                            if (b.equals(genres.get(0))) {
                                ok = 1;
                                break;
                            }
                        }
                        if (ok == 1) {
                            moviesList.add(a.getTitle());
                        }
                    }
                }
            }
        }

        for (String a : moviesList) {
            int count = 0;
            for (User b : users) {
                for (String c : b.getFavoriteMovies()) {
                    if (a.equals(c)) {
                        count++;
                        moviesMap.put(a, count);
                    }
                }
             }
        }

        moviesMap = sortByValue1(moviesMap);
        int count1 = 0;
        for (Map.Entry<String, Integer> entry : moviesMap.entrySet()) {
            String name = entry.getKey();
            Integer number1 = entry.getValue();
            if (moviesListNames.size() == 0) {
                moviesListNr.add(number1);
                moviesListNames.add(name);
            } else {
                if (number1.equals(moviesListNr.get(count1))) {
                    moviesListNames.add(name);
                    moviesListNr.add(number1);
                    count1++;
                } else if (!number1.equals(moviesListNr.get(count1))) {
                    Collections.sort(moviesListNames);
                    finalList.addAll(moviesListNames);
                    moviesListNames.clear();
                    moviesListNr.clear();
                    moviesListNames.add(name);
                    moviesListNr.add(number1);
                    count1 = 0;
                }
            }

        }

        Collections.sort(moviesListNames);
        finalList.addAll(moviesListNames);

        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param year anul cerut
     * @param genres genul cerut
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> queryFavoriteShows(final int number, final List<String> year,
                                                 final List<String> genres, final String sortType) {
        ArrayList<String> serialList = new ArrayList<>();
        Map<String, Integer> serialMap = new HashMap<>();
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> serialListNames = new ArrayList<>();
        ArrayList<Integer> serialListNr = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        for (Serial a : serials) {
            if (year.get(0) == null) {
                int ok = 0;
                if (genres.get(0) == null) {
                    serialList.add(a.getTitle());
                } else {
                    for (String b : a.getGenres()) {
                        if (b.equals(genres.get(0))) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 1) {
                        serialList.add(a.getTitle());
                    }
                }
            } else {
                if (a.getYear() == Integer.parseInt(year.get(0))) {
                    int ok = 0;
                    if (genres.get(0) == null) {
                        serialList.add(a.getTitle());
                    } else {
                        for (String b : a.getGenres()) {
                            if (b.equals(genres.get(0))) {
                                ok = 1;
                                break;
                            }
                        }
                        if (ok == 1) {
                            serialList.add(a.getTitle());
                        }
                    }
                }
            }
        }

        for (String a : serialList) {
            int count = 0;
            for (User b : users) {
                for (String c : b.getFavoriteMovies()) {
                    if (a.equals(c)) {
                        count++;
                        serialMap.put(a, count);
                    }
                }
            }
        }

        serialMap = sortByValue1(serialMap);
        int count1 = 0;
        for (Map.Entry<String, Integer> entry : serialMap.entrySet()) {
            String name = entry.getKey();
            Integer number1 = entry.getValue();
            if (serialListNames.size() == 0) {
                serialListNr.add(number1);
                serialListNames.add(name);
            } else {
                if (number1.equals(serialListNr.get(count1))) {
                    serialListNames.add(name);
                    serialListNr.add(number1);
                    count1++;
                } else if (!number1.equals(serialListNr.get(count1))) {
                    Collections.sort(serialListNames);
                    finalList.addAll(serialListNames);
                    serialListNames.clear();
                    serialListNr.clear();
                    serialListNames.add(name);
                    serialListNr.add(number1);
                    count1 = 0;
                }
            }

        }
        Collections.sort(serialListNames);
        finalList.addAll(serialListNames);

        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                Collections.sort(finalList);
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param year anul cerut
     * @param genres genul cerut
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> queryLongestMovies(final int number, final List<String> year,
                                                 final List<String> genres, final String sortType) {
        Map<String, Integer> moviesMap = new HashMap<>();
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> moviesListNames = new ArrayList<>();
        ArrayList<Integer> moviesListNr = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        for (Movie a : movies) {
            if (year.get(0) == null) {
                int ok = 0;
                if (genres.get(0) == null) {
                    moviesMap.put(a.getTitle(), a.getDuration());
                } else {
                    for (String b : a.getGenres()) {
                        if (b.equals(genres.get(0))) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 1) {
                        moviesMap.put(a.getTitle(), a.getDuration());
                    }
                }
            } else {
                if (a.getYear() == Integer.parseInt(year.get(0))) {
                    int ok = 0;
                    if (genres.get(0) == null) {
                        moviesMap.put(a.getTitle(), a.getDuration());
                    } else {
                        for (String b : a.getGenres()) {
                            if (b.equals(genres.get(0))) {
                                ok = 1;
                                break;
                            }
                        }
                        if (ok == 1) {
                            moviesMap.put(a.getTitle(), a.getDuration());
                        }
                    }
                }
            }
        }

        moviesMap = sortByValue1(moviesMap);
        int count1 = 0;
        for (Map.Entry<String, Integer> entry : moviesMap.entrySet()) {
            String name = entry.getKey();
            Integer number1 = entry.getValue();
            if (moviesListNames.size() == 0) {
                moviesListNr.add(number1);
                moviesListNames.add(name);
            } else {
                if (number1.equals(moviesListNr.get(count1))) {
                    moviesListNames.add(name);
                    moviesListNr.add(number1);
                    count1++;
                } else if (!number1.equals(moviesListNr.get(count1))) {
                    Collections.sort(moviesListNames);
                    finalList.addAll(moviesListNames);
                    moviesListNames.clear();
                    moviesListNr.clear();
                    moviesListNames.add(name);
                    moviesListNr.add(number1);
                    count1 = 0;
                }
            }

        }

        Collections.sort(moviesListNames);
        finalList.addAll(moviesListNames);

        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param year anul cerut
     * @param genres genul cerut
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> queryLongestShows(final int number, final List<String> year,
                                                 final List<String> genres, final String sortType) {
        Map<String, Integer> durations = new HashMap<>();
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> serialListNames = new ArrayList<>();
        ArrayList<Integer> serialListNr = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        for (Serial a : serials) {
            int totalDuration = 0;
            for (Season b : a.getSeasons()) {
                totalDuration += b.getDuration();
            }
            a.serialDuration = totalDuration;
        }

        for (Serial a : serials) {
            if (year.get(0) == null) {
                int ok = 0;
                if (genres.get(0) == null) {
                    durations.put(a.getTitle(), a.serialDuration);
                } else {
                    for (String b : a.getGenres()) {
                        if (b.equals(genres.get(0))) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 1) {
                        durations.put(a.getTitle(), a.serialDuration);
                    }
                }
            } else {
                if (a.getYear() == Integer.parseInt(year.get(0))) {
                    int ok = 0;
                    if (genres.get(0) == null) {
                        durations.put(a.getTitle(), a.serialDuration);
                    } else {
                        for (String b : a.getGenres()) {
                            if (b.equals(genres.get(0))) {
                                ok = 1;
                                break;
                            }
                        }
                        if (ok == 1) {
                            durations.put(a.getTitle(), a.serialDuration);
                        }
                    }
                }
            }
        }


        durations = sortByValue1(durations);
        int count1 = 0;
        for (Map.Entry<String, Integer> entry : durations.entrySet()) {
            String name = entry.getKey();
            Integer number1 = entry.getValue();
            if (serialListNames.size() == 0) {
                serialListNr.add(number1);
                serialListNames.add(name);
            } else {
                if (number1.equals(serialListNr.get(count1))) {
                    serialListNames.add(name);
                    serialListNr.add(number1);
                    count1++;
                } else if (!number1.equals(serialListNr.get(count1))) {
                    Collections.sort(serialListNames);
                    finalList.addAll(serialListNames);
                    serialListNames.clear();
                    serialListNr.clear();
                    serialListNames.add(name);
                    serialListNr.add(number1);
                    count1 = 0;
                }
            }

        }

        Collections.sort(serialListNames);
        finalList.addAll(serialListNames);

        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;

    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param year anul cerut
     * @param genres genul cerut
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> queryMostViewedMovies(final int number, final List<String> year,
                                                 final List<String> genres, final String sortType) {
        ArrayList<String> moviesList = new ArrayList<>();
        Map<String, Integer> moviesMap = new HashMap<>();
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> moviesListNames = new ArrayList<>();
        ArrayList<Integer> moviesListNr = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        for (Movie a : movies) {
            if (year.get(0) == null) {
                int ok = 0;
                if (genres.get(0) == null) {
                    moviesList.add(a.getTitle());
                } else {
                    for (String b : a.getGenres()) {
                        if (b.equals(genres.get(0))) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 1) {
                        moviesList.add(a.getTitle());
                    }
                }
            } else {
                if (a.getYear() == Integer.parseInt(year.get(0))) {
                    int ok = 0;
                    if (genres.get(0) == null) {
                        moviesList.add(a.getTitle());
                    } else {
                        for (String b : a.getGenres()) {
                            if (b.equals(genres.get(0))) {
                                ok = 1;
                                break;
                            }
                        }
                        if (ok == 1) {
                            moviesList.add(a.getTitle());
                        }
                    }
                }
            }
        }

        for (String b : moviesList) {
            int count = 0;
            for (User a : users) {
                for (Map.Entry<String, Integer> entry : a.getHistory().entrySet()) {
                    String name = entry.getKey();
                    Integer nrViews = entry.getValue();
                    if (name.equals(b)) {
                        count += nrViews;
                        moviesMap.put(b, count);
                    }
                }
            }
        }
        moviesMap = sortByValue1(moviesMap);
        int count1 = 0;
        for (Map.Entry<String, Integer> entry : moviesMap.entrySet()) {
            String name = entry.getKey();
            Integer number1 = entry.getValue();
            if (moviesListNames.size() == 0) {
                moviesListNr.add(number1);
                moviesListNames.add(name);
            } else {
                if (number1.equals(moviesListNr.get(count1))) {
                    moviesListNames.add(name);
                    moviesListNr.add(number1);
                    count1++;
                } else if (!number1.equals(moviesListNr.get(count1))) {
                    Collections.sort(moviesListNames);
                    finalList.addAll(moviesListNames);
                    moviesListNames.clear();
                    moviesListNr.clear();
                    moviesListNames.add(name);
                    moviesListNr.add(number1);
                    count1 = 0;
                }
            }

        }

        Collections.sort(moviesListNames);
        finalList.addAll(moviesListNames);

        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param year anul cerut
     * @param genres genul cerut
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> queryMostViewedShows(final int number, final List<String> year,
                                                 final List<String> genres, final String sortType) {
        ArrayList<String> serialList = new ArrayList<>();
        Map<String, Integer> serialMap = new HashMap<>();
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> serialListNames = new ArrayList<>();
        ArrayList<Integer> serialListNr = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        for (Serial a : serials) {
            if (year.get(0) == null) {
                int ok = 0;
                if (genres.get(0) == null) {
                    serialList.add(a.getTitle());
                } else {
                    for (String b : a.getGenres()) {
                        if (b.equals(genres.get(0))) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 1) {
                        serialList.add(a.getTitle());
                    }
                }
            } else {
                if (a.getYear() == Integer.parseInt(year.get(0))) {
                    int ok = 0;
                    if (genres.get(0) == null) {
                        serialList.add(a.getTitle());
                    } else {
                        for (String b : a.getGenres()) {
                            if (b.equals(genres.get(0))) {
                                ok = 1;
                                break;
                            }
                        }
                        if (ok == 1) {
                            serialList.add(a.getTitle());
                        }
                    }
                }
            }
        }

        for (String b : serialList) {
            int count = 0;
            for (User a : users) {
                for (Map.Entry<String, Integer> entry : a.getHistory().entrySet()) {
                    String name = entry.getKey();
                    Integer nrViews = entry.getValue();
                    if (name.equals(b)) {
                        count += nrViews;
                        serialMap.put(b, count);
                    }
                }
            }
        }
        serialMap = sortByValue1(serialMap);
        int count1 = 0;
        for (Map.Entry<String, Integer> entry : serialMap.entrySet()) {
            String name = entry.getKey();
            Integer number1 = entry.getValue();
            if (serialListNames.size() == 0) {
                serialListNr.add(number1);
                serialListNames.add(name);
            } else {
                if (number1.equals(serialListNr.get(count1))) {
                    serialListNames.add(name);
                    serialListNr.add(number1);
                    count1++;
                } else if (!number1.equals(serialListNr.get(count1))) {
                    Collections.sort(serialListNames);
                    finalList.addAll(serialListNames);
                    serialListNames.clear();
                    serialListNr.clear();
                    serialListNames.add(name);
                    serialListNr.add(number1);
                    count1 = 0;
                }
            }

        }

        Collections.sort(serialListNames);
        finalList.addAll(serialListNames);

        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     *
     * @param number numarul care este specificat in query
     * @param sortType Tipul de sortare ceruta(ascendent sau descendent)
     * @return returneaza ArrayList
     */
    public ArrayList<String> queryUsers(final int number, final String sortType) {
        ArrayList<String> finalList = new ArrayList<>();
        ArrayList<String> finalList1 = new ArrayList<>();
        Map<String, Integer> userMap = new HashMap<>();
        ArrayList<String> userListNames = new ArrayList<>();
        ArrayList<Integer> userListNr = new ArrayList<>();
        for (User a : users) {
            if (a.getSeriesGradesTitle().size() + a.getMoviesGrades().size() != 0) {
                userMap.put(a.getUsername(), a.getSeriesGradesTitle().size()
                        + a.getMoviesGrades().size());
            }
        }

        userMap = sortByValue1(userMap);
        int count1 = 0;
        for (Map.Entry<String, Integer> entry : userMap.entrySet()) {
            String name = entry.getKey();
            Integer number1 = entry.getValue();
            if (userListNames.size() == 0) {
                userListNr.add(number1);
                userListNames.add(name);
            } else {
                if (number1.equals(userListNr.get(count1))) {
                    userListNames.add(name);
                    userListNr.add(number1);
                    count1++;
                } else if (!number1.equals(userListNr.get(count1))) {
                    Collections.sort(userListNames);
                    finalList.addAll(userListNames);
                    userListNames.clear();
                    userListNr.clear();
                    userListNames.add(name);
                    userListNr.add(number1);
                    count1 = 0;
                }
            }

        }

        Collections.sort(userListNames);
        finalList.addAll(userListNames);

        if (sortType.equals("asc")) {
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        } else if (sortType.equals("desc")) {
            Collections.reverse(finalList);
            if (number > finalList.size()) {
                return finalList;
            } else {
                for (int i = 0; i < number; i++) {
                    finalList1.add(finalList.get(i));
                }
            }
        }
        return finalList1;
    }

    /**
     *
     * @param username username-ul cerut
     * @return returneaza un String
     */
    public String standardRecommendation(final String username) {
        String finalString;
        for (User a : users) {
            if (a.getUsername().equals(username)) {
                for (Movie b : movies) {
                    int ok = 0;
                    for (Map.Entry<String, Integer> entry : a.getHistory().entrySet()) {
                        String name = entry.getKey();
                        if (b.getTitle().equals(name)) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 0) {
                        finalString = b.getTitle();
                        return "StandardRecommendation result: " + finalString;
                    }
                }
            }
        }
        return "StandardRecommendation cannot be applied!";
    }

    /**
     *
     * @param username username-ul cerut
     * @return returneaza un String
     */
    public String bestUnseenRecommendation(final String username) {
        String finalString = null;
        Map<String, Double> videosMap = new HashMap<>();

        for (Movie a : movies) {
            Double sum = 0.0;
            int count = 0;
            for (int i = 0; i < a.getMoviesNotes().size(); i++) {
                sum += a.getMoviesNotes().get(i);
                count++;
            }
            if (count != 0) {
                a.setFinalMovieNote(sum / count);
            } else {
                a.setFinalMovieNote(0.0);
            }
        }

        for (Serial a : serials) {
            Double sum = 0.0;
            for (int i = 0; i < a.getSerialNotesRatings().size(); i++) {
                if (a.getFinalSeasonNotesRatings().size() == 0 && a.getFinalSeasonNotesSeasonNr()
                        .size() == 0) {
                    a.getFinalSeasonNotesSeasonNr().add(a.getSerialNotesSeasonNr().get(i));
                    a.getFinalSeasonNotesRatings().add(a.getSerialNotesRatings().get(i));
                } else {
                    for (int j = 0; j < a.getFinalSeasonNotesRatings().size(); j++) {
                        if (a.getFinalSeasonNotesSeasonNr().get(j).equals(a.getSerialNotesSeasonNr()
                                .get(i))) {
                            a.getFinalSeasonNotesSeasonNr().set(j, a.getSerialNotesSeasonNr()
                                    .get(i));
                            a.getFinalSeasonNotesRatings().set(j, (a.getSerialNotesRatings().get(i)
                                    + a.getFinalSeasonNotesRatings().get(j)) / 2);
                            break;
                        } else {
                            a.getFinalSeasonNotesSeasonNr().add(a.getSerialNotesSeasonNr().get(i));
                            a.getFinalSeasonNotesRatings().add(a.getSerialNotesRatings().get(i));
                        }
                    }
                }
            }
            for (int z = 0; z < a.getFinalSeasonNotesRatings().size(); z++) {
                sum += a.getFinalSeasonNotesRatings().get(z);
            }
            a.finalSerialNote = (sum / a.getNumberOfSeasons());
        }

        for (User a : users) {
            if (a.getUsername().equals(username)) {
                for (Movie b : movies) {
                    int ok = 0;
                    for (Map.Entry<String, Integer> entry : a.getHistory().entrySet()) {
                        String name = entry.getKey();
                        if (b.getTitle().equals(name)) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 0) {
                        videosMap.put(b.getTitle(), b.getFinalMovieNote());
                    }
                }
                for (Serial c : serials) {
                    int ok = 0;
                    for (Map.Entry<String, Integer> entry : a.getHistory().entrySet()) {
                        String name1 = entry.getKey();
                        if (c.getTitle().equals(name1)) {
                            ok = 1;
                            break;
                        }
                    }
                    if (ok == 0) {
                        videosMap.put(c.getTitle(), c.finalSerialNote);
                    }
                }
            }
        }

        videosMap = sortByValue(videosMap);

        if (videosMap.size() == 0) {
            return "BestRatedUnseenRecommendation cannot be applied!";
        }

        LinkedHashMap<String, Double> reverseSortedMap = new LinkedHashMap<>();

        videosMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        Map<String, Double> auxiliarMap = new HashMap<>();
        Double auxiliar = 0.0;

        for (Map.Entry<String, Double> entry : reverseSortedMap.entrySet()) {
                String name = entry.getKey();
                Double nr = entry.getValue();
                if (auxiliarMap.size() == 0) {
                    auxiliarMap.put(name, nr);
                    auxiliar = nr;
                }
        }

        for (Map.Entry<String, Double> entry : reverseSortedMap.entrySet()) {
            String name = entry.getKey();
            Double nr = entry.getValue();
            if (nr.equals(auxiliar)) {
                auxiliarMap.put(name, nr);
            }
        }

        for (Movie c : movies) {
            int ok = 0;
            for (Map.Entry<String, Double> entry : auxiliarMap.entrySet()) {
                String name = entry.getKey();
                if (c.getTitle().equals(name)) {
                    ok = 1;
                    break;
                }
            }
            if (ok == 1) {
                finalString = c.getTitle();
                break;
            }
        }
        return "BestRatedUnseenRecommendation result: " + finalString;
    }

    /**
     *
     * @param username username-ul cerut
     * @return returneaza un String
     */
    public String popularRecommendation(final String username) {
        String finalString;
        Map<String, Integer> moviesMap = new HashMap<>();
        Map<String, Integer> serialMap = new HashMap<>();
        Map<String, Integer> genreMap = new HashMap<>();
        User user = null;
        for (User a : users) {
            if (a.getUsername().equals(username)) {
                user = a;
            }
        }
        assert user != null;

        for (Movie b : movies) {
            int count = 0;
            for (User a : users) {
                for (Map.Entry<String, Integer> entry : a.getHistory().entrySet()) {
                    String name = entry.getKey();
                    Integer nrViews = entry.getValue();
                    if (name.equals(b.getTitle())) {
                        count += nrViews;
                        moviesMap.put(b.getTitle(), count);
                        break;
                    }
                }
            }
        }

        for (Movie b : movies) {
            for (Map.Entry<String, Integer> entry : moviesMap.entrySet()) {
                String name = entry.getKey();
                Integer number = entry.getValue();
                if (b.getTitle().equals(name)) {
                    b.finalMovieViews = number;
                    break;
                }
            }
        }

        for (Serial b : serials) {
            int count = 0;
            for (User a : users) {
                for (Map.Entry<String, Integer> entry : a.getHistory().entrySet()) {
                    String name = entry.getKey();
                    Integer nrViews = entry.getValue();
                    if (name.equals(b.getTitle())) {
                        count += nrViews;
                        serialMap.put(b.getTitle(), count);
                        break;
                    }
                }
            }
        }

        for (Serial b : serials) {
            for (Map.Entry<String, Integer> entry : serialMap.entrySet()) {
                String name = entry.getKey();
                Integer number = entry.getValue();
                if (b.getTitle().equals(name)) {
                    b.finalSerialViews = number;
                    break;
                }
            }
        }

        if (user.getSubscriptionType().equals("BASIC")) {
            return "PopularRecommendation cannot be applied!";
        } else {
            for (Movie a : movies) {
                for (String b : a.getGenres()) {
                    if (genreMap.containsKey(b)) {
                        genreMap.put(b, a.finalMovieViews + genreMap.get(b));
                    } else {
                        genreMap.put(b, a.finalMovieViews);
                    }
                }
            }
            for (Serial a : serials) {
                for (String b : a.getGenres()) {
                    if (genreMap.containsKey(b)) {
                        genreMap.put(b, a.finalSerialViews + genreMap.get(b));
                    } else {
                        genreMap.put(b, a.finalSerialViews);
                    }
                }
            }
        }

        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

        genreMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        for (Map.Entry<String, Integer> entry : reverseSortedMap.entrySet()) {
            String name = entry.getKey();
            for (Movie e : movies) {
                int ok = 0;
                for (String f : e.getGenres()) {
                    if (f.equals(name)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    int ok1 = 0;
                    for (Map.Entry<String, Integer> entry1 : user.getHistory().entrySet()) {
                        String seenVideo = entry1.getKey();
                        if (e.getTitle().equals(seenVideo)) {
                            ok1 = 1;
                            break;
                        }
                    }
                    if (ok1 == 0) {
                        finalString = e.getTitle();
                        return "PopularRecommendation result: " + finalString;
                    }
                }
            }
            for (Serial e : serials) {
                int ok = 0;
                for (String f : e.getGenres()) {
                    if (f.equals(name)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 1) {
                    int ok1 = 0;
                    for (Map.Entry<String, Integer> entry1 : user.getHistory().entrySet()) {
                        String seenVideo = entry1.getKey();
                        if (e.getTitle().equals(seenVideo)) {
                            ok1 = 1;
                            break;
                        }
                    }
                    if (ok1 == 0) {
                        finalString = e.getTitle();
                        return "PopularRecommendation result: " + finalString;
                    }
                }
            }
        }
        return "PopularRecommendation cannot be applied!";
    }

    /**
     *
     * @param username username-ul cerut
     * @return returneaza un String
     */
    public String favoriteRecommendation(final String username) {
        String finalString;
        Map<String, Integer> videoMap = new HashMap<>();
        User user = null;
        for (User a : users) {
            if (a.getUsername().equals(username)) {
                user = a;
            }
        }
        assert user != null;

        for (Movie a : movies) {
            int count = 0;
            for (User b : users) {
                for (String c : b.getFavoriteMovies()) {
                    if (c.equals(a.getTitle())) {
                        count++;
                        break;
                    }
                }
            }
            if (count != 0) {
                videoMap.put(a.getTitle(), count);
            }
        }

        for (Serial a : serials) {
            int count = 0;
            for (User b : users) {
                for (String c : b.getFavoriteMovies()) {
                    if (c.equals(a.getTitle())) {
                        count++;
                        break;
                    }
                }
            }
            if (count != 0) {
                videoMap.put(a.getTitle(), count);
            }
        }

        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

        videoMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        if (user.getSubscriptionType().equals("BASIC")) {
            return "PopularRecommendation cannot be applied!";
        } else {
            for (Map.Entry<String, Integer> entry : reverseSortedMap.entrySet()) {
                String name = entry.getKey();
                int ok = 0;
                for (Map.Entry<String, Integer> entry1 : user.getHistory().entrySet()) {
                    String seenVideo = entry1.getKey();
                    if (seenVideo.equals(name)) {
                        ok = 1;
                        break;
                    }
                }
                if (ok == 0) {
                    finalString = name;
                    return "FavoriteRecommendation result: " + finalString;
                }
            }
        }
        return "FavoriteRecommendation cannot be applied!";
    }

    /**
     *
     * @param username username-ul cerut
     * @param genre genul cerut
     * @return returneaza fie un String, fie un ArrayList
     */
    public Serializable searchRecommendation(final String username, final String genre) {
        ArrayList<String> finalVideoList = new ArrayList<>();
        Map<String, Double> videoMap = new HashMap<>();
        ArrayList<String> videoNames = new ArrayList<>();
        ArrayList<Double> videoNr = new ArrayList<>();
        ArrayList<String> videoFinalList = new ArrayList<>();
        User user = null;
        for (User a : users) {
            if (a.getUsername().equals(username)) {
                user = a;
            }
        }
        assert user != null;

        for (Movie a : movies) {
            Double sum = 0.0;
            int count = 0;
            for (int i = 0; i < a.getMoviesNotes().size(); i++) {
                sum += a.getMoviesNotes().get(i);
                count++;
            }
            if (count != 0) {
                a.setFinalMovieNote(sum / count);
            } else {
                a.setFinalMovieNote(0.0);
            }
        }

        for (Serial a : serials) {
            Double sum = 0.0;
            for (int i = 0; i < a.getSerialNotesRatings().size(); i++) {
                if (a.getFinalSeasonNotesRatings().size() == 0 && a.getFinalSeasonNotesSeasonNr()
                        .size() == 0) {
                    a.getFinalSeasonNotesSeasonNr().add(a.getSerialNotesSeasonNr().get(i));
                    a.getFinalSeasonNotesRatings().add(a.getSerialNotesRatings().get(i));
                } else {
                    for (int j = 0; j < a.getFinalSeasonNotesRatings().size(); j++) {
                        if (a.getFinalSeasonNotesSeasonNr().get(j).equals(a.getSerialNotesSeasonNr()
                                .get(i))) {
                            a.getFinalSeasonNotesSeasonNr().set(j, a.getSerialNotesSeasonNr()
                                    .get(i));
                            a.getFinalSeasonNotesRatings().set(j, (a.getSerialNotesRatings().get(i)
                                    + a.getFinalSeasonNotesRatings().get(j)) / 2);
                            break;
                        } else {
                            a.getFinalSeasonNotesSeasonNr().add(a.getSerialNotesSeasonNr().get(i));
                            a.getFinalSeasonNotesRatings().add(a.getSerialNotesRatings().get(i));
                        }
                    }
                }
            }
            for (int z = 0; z < a.getFinalSeasonNotesRatings().size(); z++) {
                sum += a.getFinalSeasonNotesRatings().get(z);
            }
            a.finalSerialNote = (sum / a.getNumberOfSeasons());
        }

        for (Movie a : movies) {
                videoMap.put(a.getTitle(), a.getFinalMovieNote());
        }
        for (Serial a : serials) {
                videoMap.put(a.getTitle(), a.finalSerialNote);
        }

        videoMap = sortByValue(videoMap);
        int count1 = 0;
        for (Map.Entry<String, Double> entry : videoMap.entrySet()) {

            Double note = entry.getValue();
            String name = entry.getKey();
            if (videoNr.size() == 0) {
                videoNr.add(note);
                videoNames.add(name);
            } else {
                if (Double.compare(note, videoNr.get(count1)) == 0) {
                    videoNames.add(name);
                    videoNr.add(note);
                    count1++;
                } else if (Double.compare(note, videoNr.get(count1)) != 0) {
                    Collections.sort(videoNames);
                    videoFinalList.addAll(videoNames);
                    videoNames.clear();
                    videoNr.clear();
                    videoNames.add(name);
                    videoNr.add(note);
                    count1 = 0;
                }
            }

        }
        Collections.sort(videoNames);
        videoFinalList.addAll(videoNames);


        if (user.getSubscriptionType().equals("PREMIUM")) {
            for (String a : videoFinalList) {
                for (Movie b : movies) {
                    if (a.equals(b.getTitle())) {
                        for (String c : b.getGenres()) {
                            if (c.equals(genre)) {
                                int ok = 0;
                                for (Map.Entry<String, Integer> entry : user.getHistory()
                                        .entrySet()) {
                                    String name = entry.getKey();
                                    if (name.equals(a)) {
                                        ok = 1;
                                        break;
                                    }
                                }
                                if (ok == 0) {
                                    finalVideoList.add(a);
                                }
                            }
                        }
                    }
                }
                for (Serial b : serials) {
                    if (a.equals(b.getTitle())) {
                        for (String c : b.getGenres()) {
                            if (c.equals(genre)) {
                                int ok = 0;
                                for (Map.Entry<String, Integer> entry : user.getHistory()
                                        .entrySet()) {
                                    String name = entry.getKey();
                                    if (name.equals(a)) {
                                        ok = 1;
                                        break;
                                    }
                                }
                                if (ok == 0) {
                                    finalVideoList.add(a);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (finalVideoList.size() == 0) {
            return "SearchRecommendation cannot be applied!";
        }
        return "SearchRecommendation result: " + finalVideoList;
    }
}


