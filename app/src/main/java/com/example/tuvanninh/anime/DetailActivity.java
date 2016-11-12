package com.example.tuvanninh.anime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import info.hoang8f.widget.FButton;

public class DetailActivity extends AppCompatActivity {

    FilmInfo filmInfo;
    TextView vName, vDesFilm, vLength;
    ImageView vImage;
    FButton btnWatchFilm;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        receiveData();
        initLayout();
        setupLayout();
    }


    private void setupLayout() {
        vName.setText(filmInfo.getName());
        vDesFilm.setText(filmInfo.getDesFilm());
        vLength.setText(filmInfo.getNumEps());
        Picasso.with(this).load(filmInfo.getImgURL()).fit().into(vImage);
        btnWatchFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadFilmList(DetailActivity.this).execute(filmInfo.filmURL);
            }
        });
    }

    private void initLayout() {
        vName = (TextView) findViewById(R.id.fname);
        vDesFilm = (TextView) findViewById(R.id.desfilm);
        vLength = (TextView) findViewById(R.id.numeps);
        vImage = (ImageView) findViewById(R.id.filmimg);
        btnWatchFilm = (FButton) findViewById(R.id.xemphim);
        intent = new Intent(this, WatchActivity.class);
    }

    private void receiveData() {
        filmInfo = (FilmInfo) getIntent().getSerializableExtra("film");
    }

    private class LoadFilmList extends LoadDataTask {

        public LoadFilmList(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            org.jsoup.nodes.Document doc = Jsoup.parse(s);
            if (filmInfo.position < 2 || filmInfo.isSearchAnime){
                Elements film = doc.body().select("a.button_film_watch");
                filmInfo.tmp = film.attr("href");
                intent.putExtra("filmURL", filmInfo);
                startActivity(intent);
            }
            else if (filmInfo.position > 2 || !filmInfo.isSearchAnime){
                filmInfo.tmp = filmInfo.filmURL;
                intent.putExtra("filmURL", filmInfo);
                startActivity(intent);
            }
        }
    }
}
