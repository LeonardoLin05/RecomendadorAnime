package es.upm.proyecto.jade.recomendador.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Anime {
	
	@JsonProperty("mal_id")
	private int id;
	private String title;
	private Images images;
	
	private int episodes;
	private boolean airing;
	private String duration;
	
	private double score;
	@JsonProperty("scored_by")
	private int scoredBy;
	private int rank;
	private int popularity;
	private int members;
	private int favourites;
	
	private String synopsis;
	
	private int year;
	
	private List<NameItem> studios;
	private List<NameItem> genres;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getEpisodes() {
		return episodes;
	}
	
	public void setEpisodes(int episodes) {
		this.episodes = episodes;
	}
	
	public boolean isAiring() {
		return airing;
	}
	
	public void setAiring(boolean airing) {
		this.airing = airing;
	}
	
	public String getDuration() {
		return duration;
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public int getScoredBy() {
		return scoredBy;
	}
	
	public void setScoredBy(int scoredBy) {
		this.scoredBy = scoredBy;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public int getPopularity() {
		return popularity;
	}
	
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	
	public int getMembers() {
		return members;
	}
	
	public void setMembers(int members) {
		this.members = members;
	}
	
	public int getFavourites() {
		return favourites;
	}
	
	public void setFavourites(int favourites) {
		this.favourites = favourites;
	}
	
	public String getSynopsis() {
		return synopsis;
	}
	
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public List<NameItem> getStudios() {
		return studios;
	}
	
	public void setStudios(List<NameItem> studios) {
		this.studios = studios;
	}
	
	public List<NameItem> getGenres() {
		return genres;
	}
	
	public void setGenres(List<NameItem> genres) {
		this.genres = genres;
	}

	public Images getImages() {
		return images;
	}

	public void setImages(Images images) {
		this.images = images;
	}
}
