package es.upm.proyecto.jade.recomendador.behaviours;

import es.upm.proyecto.jade.recomendador.models.Anime;
import es.upm.proyecto.jade.recomendador.models.NameItem;
import es.upm.proyecto.jade.recomendador.models.UserPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HeuristicCalculator {

    private static final Logger logger = LoggerFactory.getLogger(HeuristicCalculator.class);

    private static final int Max_score_points = 30;
    private static final int Max_rank_points = 20;
    private static final int Max_years_points = 15;
    private static final int Max_episodes_points = 10;
    private static final int Max_duration_points = 10;
    private static final int Max_status_points = 10;
    private static final int Max_keyword_points = 15;
    private static final int Max_themes_points = 10;

    private static final int Min_votos = 1000;

    private double Media;

    private final UserPreferences preferences;

    public HeuristicCalculator(UserPreferences preferences) {
        this.preferences = preferences;
    }

    //media del batch actual
    public void calcularScores(List<Anime> animes) {
        this.Media = animes.stream()
                .filter(anime -> anime.getScore() > 0 && anime.getScoredBy() > 0)
                .mapToDouble(Anime::getScore)
                .average()
                .orElse(7.0);

        logger.debug("Media: {}", Media);

        for (Anime anime : animes) {
            int score = calcularScore(anime);
            anime.setHeuristicScore(score);
            logger.debug("Anime '{}' -> heuristicScore: {}", anime.getTitle(), score);
        }
    }

    //suma de todos los parámetros para obtener calificación total
    private int calcularScore(Anime anime) {
        int total = 0;
        total += scoreByMalScore(anime);
        total += scoreByRank(anime);
        total += scoreByYear(anime);
        total += scoreByEpisodes(anime);
        total += scoreByDuration(anime);
        total += scoreByStatus(anime);
        total += scoreByKeywords(anime);
        total += scoreByThemes(anime);
        return total;
    }

    private int scoreByThemes(Anime anime) {
        int themeId = preferences.getThemes();
        if(themeId <= 0){
            return Max_themes_points;
        }

        List<NameItem> themes = anime.getThemes();
        if(themes == null || themes.isEmpty()){
            return 0;
        }

        boolean hayThemes = themes.stream().anyMatch(g -> g.getId() == themeId);

        if(hayThemes){
            return Max_themes_points;
        }
        else{
            return 0;
        }
    }

    private int scoreByKeywords(Anime anime) {
        List<String> keywords = preferences.getKeywords();

        if(keywords == null || keywords.isEmpty()){
            return 0;
        }

        String titulo = "";
        if(anime.getTitle() != null){
            titulo = anime.getTitle().toLowerCase();
        }

        String sinopsis = "";
        if(anime.getSynopsis() != null){
            sinopsis = anime.getSynopsis().toLowerCase();
        }

        int encontrado = 0;
        for(String keyword : keywords){
            String keywordLower = keyword.toLowerCase();
            if(titulo.contains(keywordLower) || sinopsis.contains(keywordLower)){
                encontrado++;
            }
        }
        return Math.min(encontrado, 3) * 5;
    }

    private int scoreByStatus(Anime anime) {
        String status = preferences.getStatus();

        if(status == null || status.isBlank()){
            return Max_status_points;
        }

        boolean emision = anime.isAiring();
        boolean prefEmision = status.toLowerCase().contains("airing") && !status.toLowerCase().contains("finished");

        if(emision == prefEmision){
            return Max_status_points;
        }
        else{
            return 0;
        }
    }

    private int campana_gauss(int valor, Integer min, Integer max, double sigma, int maxPuntos) {
        if ((min == null) && (max == null)) {
            return maxPuntos;
        }

        if (valor <= 0) {
            return 0;
        }

        int diff = 0;

        if (min != null && max != null) {
            if (valor < min) {
                diff = min - valor;
            } else if (valor > max) {
                diff = valor - max;
            }
        }
        else if (min != null) {
            if (valor < min) {
                diff = min - valor;
            }
        }
        else{
            if(valor > max){
                diff = valor - max;
            }
        }

        double punctuation = maxPuntos * Math.exp(-(diff * diff) / (2 * sigma * sigma));
        return (int) Math.round(punctuation);
    }

    private int parseDurationMinutes(String duration) {
        if (duration == null || duration.isBlank()) return 0;
        try {
            int total = 0;
            java.util.regex.Matcher hrMatcher = java.util.regex.Pattern
                    .compile("(\\d+)\\s*hr").matcher(duration);
            if (hrMatcher.find()) {
                total += Integer.parseInt(hrMatcher.group(1)) * 60;
            }
            java.util.regex.Matcher minMatcher = java.util.regex.Pattern
                    .compile("(\\d+)\\s*min").matcher(duration);
            if (minMatcher.find()) {
                total += Integer.parseInt(minMatcher.group(1));
            }
            return total;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int scoreByDuration(Anime anime) {
        int duration = parseDurationMinutes(anime.getDuration());

        String type;

        if (preferences.getType() != null && !preferences.getType().isBlank()) {
            type = preferences.getType();
        } else {
            type = anime.getType();
        }

        double sigma;

        if(type != null && type.equalsIgnoreCase("movie")){
            sigma = 20.0;
        }
        else if (type != null && (type.equalsIgnoreCase("ona") || type.equalsIgnoreCase("ova"))){
            sigma = 15.0;
        }
        else{
            sigma = 8.0;
        }

        return campana_gauss(duration, preferences.getDurationMin(), preferences.getDurationMax(), sigma, Max_duration_points);
    }

    private int scoreByEpisodes(Anime anime) {
        return campana_gauss(anime.getEpisodes(), preferences.getEpisodesMin(), preferences.getEpisodesMax(), 12.0, Max_episodes_points);
    }

    private int scoreByYear(Anime anime) {
        return campana_gauss(anime.getYear(), preferences.getYearMin(), preferences.getYearMax(), 5.0, Max_years_points);
    }

    private int scoreByRank(Anime anime) {
        if(anime.getRank() <= 0){
            return 0;
        }
        double result = Max_rank_points / (1 + Math.log10(anime.getRank()));
        return (int) Math.round(result);
    }

    private int scoreByMalScore(Anime anime) {
        double score = anime.getScore();
        int votos = anime.getScoredBy();

        if(score <= 0 || votos <= 0){
            return 0;
        }

        double min = Min_votos;
        double media = Media;

        double media_bayes = (votos / (votos + min)) * score + (min / (votos + min)) * media;
        return (int) Math.round((media_bayes/ 10.0) * Max_score_points);
    }

}

