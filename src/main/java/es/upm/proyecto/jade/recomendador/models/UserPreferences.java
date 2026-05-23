package es.upm.proyecto.jade.recomendador.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties
public class UserPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    private int genre;
    private String status;
    private String type;

    @JsonProperty("year_min")
    private Integer yearMin;
    @JsonProperty("year_max")
    private Integer yearMax;

    @JsonProperty("episodes_min")
    private Integer episodesMin;
    @JsonProperty("episodes_max")
    private Integer episodesMax;

    @JsonProperty("duration_min")
    private Integer durationMin;
    @JsonProperty("duration_max")
    private Integer durationMax;

    private int themes;
    private List<String> keywords;
    private List<String> personalizado;


    public int getGenre() {
        return genre;
    }
    public void setGenre(int genre){
        this.genre = genre;
    }

    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public Integer getYearMin(){
        return yearMin;
    }
    public void setYearMin(Integer yearMin){
        this.yearMin = yearMin;
    }

    public Integer getYearMax() {
        return yearMax;
    }
    public void setYearMax(Integer yearMax){
        this.yearMax = yearMax;
    }

    public Integer getEpisodesMin(){
        return episodesMin;
    }
    public void setEpisodesMin(Integer episodesMin){
        this.episodesMin = episodesMin;
    }
    public Integer getEpisodesMax(){
        return episodesMax;
    }
    public void setEpisodesMax(Integer episodesMax){
        this.episodesMax = episodesMax;
    }

    public Integer getDurationMin(){
        return durationMin;
    }
    public void setDurationMin(Integer durationMin){
        this.durationMin = durationMin;
    }

    public Integer getDurationMax(){
        return durationMax;
    }
    public void setDurationMax(Integer durationMax){
        this.durationMax = durationMax;
    }

    public int getThemes(){
        return themes;
    }
    public void setThemes(int themes){
        this.themes = themes;
    }

    public List<String> getKeywords(){
        return keywords;
    }
    public void setKeywords(List<String> keywords){
        this.keywords = keywords;
    }

    public List<String> getPersonalizado(){
        return personalizado;
    }
    public void setPersonalizado(List<String> personalizado){
        this.personalizado = personalizado;
    }
}


