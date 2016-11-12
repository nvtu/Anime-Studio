package com.example.tuvanninh.anime;

import java.io.Serializable;

/**
 * Created by Tu Van Ninh on 11/11/2016.
 */
public class FilmInfo implements Serializable{
    String name;
    String filmURL;
    String numEps;
    String imgURL;
    String desFilm;
    String IMDB;
    String yearFilm;
    int position;
    boolean isSearchAnime;

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    String tmp;


    public FilmInfo(String name, String filmURL,  String numEps, String imgURL, String desFilm, int position, boolean isSearchAnime){
        this.name = name;
        this.filmURL = filmURL;
        this.numEps = numEps;
        this.imgURL = imgURL;
        this.desFilm = desFilm;
        this.position = position;
        this.isSearchAnime = isSearchAnime;
    }

    public String getIMDB() {
        return IMDB;
    }

    public void setIMDB(String IMDB) {
        this.IMDB = IMDB;
    }

    public String getDesFilm() {
        return desFilm;
    }

    public void setDesFilm(String desFilm) {
        this.desFilm = desFilm;
    }

    public String getYearFilm() {
        return yearFilm;
    }

    public void setYearFilm(String yearFilm) {
        this.yearFilm = yearFilm;
    }

    public FilmInfo(String name, String filmURL, String numEps, String imgURL, String desFilm, String yearFilm, int position, boolean isSearchAnime) {

        this.name = name;
        this.filmURL = filmURL;
        this.numEps = numEps;
        this.imgURL = imgURL;
        this.desFilm = desFilm;
        this.yearFilm = yearFilm;
        this.position = position;
        this.isSearchAnime = isSearchAnime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilmURL() {
        return filmURL;
    }

    public void setFilmURL(String filmURL) {
        this.filmURL = filmURL;
    }

    public String getNumEps() {
        return numEps;
    }

    public void setNumEps(String numEps) {
        this.numEps = numEps;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

}
