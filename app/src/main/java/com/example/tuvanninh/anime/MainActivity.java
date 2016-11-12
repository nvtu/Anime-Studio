package com.example.tuvanninh.anime;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gigamole.navigationtabbar.ntb.NavigationTabBar;
import com.roger.catloadinglibrary.CatLoadingView;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{

    LoadFilmList loadFilmList;
    ArrayList<String> linkCategory;
    ArrayList<FilmInfo>[] filmList;
    RecycleAdapter[] recycleAdapter;
    EditText tSearchAnime, tSearchMovie;
    Button btnSearchAnime, btnSearchMovie;
    int[] curPage = new int[5];
    String animeLink = "http://animehay.com/the-loai/phim-anime?page=";
    String cartoonLink = "http://animehay.com/the-loai/phim-hoat-hinh?page=";
    String longLink = "http://xuongphim.tv/danh-sach/phim-le/trang-";
    String shortLink = "http://xuongphim.tv/danh-sach/phim-bo/trang-";
    int isSearchAnime = 0;
    CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initComponents() {
        mView = new CatLoadingView();
        mView.show(getSupportFragmentManager(), "");
        linkCategory = new ArrayList<>();
        filmList = new ArrayList[5];
        recycleAdapter = new RecycleAdapter[5];
        for (int i=0; i<5; i++){
            filmList[i] = new ArrayList<>();
            recycleAdapter[i] = new RecycleAdapter(filmList[i]);
            curPage[i] = 1;
        }
        linkCategory.add(animeLink);
        linkCategory.add(cartoonLink);
        linkCategory.add("");
        linkCategory.add(longLink);
        linkCategory.add(shortLink);
        loadData(linkCategory.get(0) + String.valueOf(1), 0);
        loadData(linkCategory.get(1) + String.valueOf(1), 1);
        loadData(linkCategory.get(3) + String.valueOf(1) + ".html", 3);
        loadData(linkCategory.get(4) + String.valueOf(1) + ".html", 4);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.dismiss();
            }
        }, 3000);
    }

    private void loadData(String url, int position) {
        loadFilmList = new LoadFilmList(this, position);
        loadFilmList.execute(url);
    }

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        assert viewPager != null;
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view;
                final RecyclerView recyclerView;
                if (position == 2) {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.activity_search, null, false);
                    recyclerView = (RecyclerView) view.findViewById(R.id.rvs);
                    tSearchAnime = (EditText) view.findViewById(R.id.tsearchAnime);
                    btnSearchAnime = (Button) view.findViewById(R.id.searchAnime);
                    btnSearchAnime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tSearchAnime.getText().length() == 0) return;
                            isSearchAnime = 1;
                            String query = "http://animehay.com/tim-kiem?q=" + tSearchAnime.getText().toString().replace(' ', '+');
                            filmList[2].clear();
                            loadData(query, 2);
                        }
                    });
                    tSearchMovie = (EditText) view.findViewById(R.id.tsearchFilm);
                    btnSearchMovie = (Button) view.findViewById(R.id.searchFilm);
                    btnSearchMovie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tSearchMovie.getText().length() == 0) return;
                            isSearchAnime = 2;
                            String query = "http://xuongphim.tv/tim-kiem/" + tSearchMovie.getText().toString().replace(' ', '-') + ".html";
                            filmList[2].clear();
                            loadData(query, 2);
                        }
                    });
                } else {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.item_vp_list, null, false);
                    recyclerView = (RecyclerView) view.findViewById(R.id.rv);

                }
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(recycleAdapter[position]);


                if (position != 2){
                    recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        private boolean loading = false;
                        int pastVisiblesItems, visibleItemCount, totalItemCount;

                        private synchronized void loadMore(int position){
                            curPage[position]++;
                            if (position>2){
                                loadData(linkCategory.get(position) + String.valueOf(curPage[position]) +".html", position);
                            }
                            else if (position < 2) loadData(linkCategory.get(position) + String.valueOf(curPage[position]), position);
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            if(dy > 0) //check for scroll down
                            {
                                visibleItemCount = linearLayoutManager.getChildCount();
                                totalItemCount = linearLayoutManager.getItemCount();
                                pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) loading = true;

                                if (loading)
                                {
                                    loading = false;
                                    Log.v("abc", "Last Item Wow !");
                                    //Do pagination.. i.e. fetch new data
                                    loadMore(position);
                                }
                            }
                        }

                    });
                }


                container.addView(view);
                return view;
            }
        });

        final String[] colors = getResources().getStringArray(R.array.default_preview);
        initComponents();


        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .title("Anime")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[1]))
                        .title("Cartoon")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[2]))
                        .title("Search")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fourth),
                        Color.parseColor(colors[3]))
                        .title("Movie")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fifth),
                        Color.parseColor(colors[4]))
                        .title("Series")
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);


        navigationTabBar.post(new Runnable() {
            @Override
            public void run() {
                final View viewPager = findViewById(R.id.vp_horizontal_ntb);
                ((ViewGroup.MarginLayoutParams) viewPager.getLayoutParams()).topMargin =
                        (int) -navigationTabBar.getBadgeMargin();
                viewPager.requestLayout();
            }
        });

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });

    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>{


        ArrayList<FilmInfo> filmList;

        public RecycleAdapter(ArrayList<FilmInfo> listFilm) {
            filmList = listFilm;
        }


        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            String desFilm = filmList.get(position).getName() + "\n" + filmList.get(position).getNumEps();
            if (position < 3){
                desFilm +=  "\n" + filmList.get(position).getYearFilm();
            }
            holder.tDesFilm.setText(desFilm);
            Picasso.with(MainActivity.this).load(filmList.get(position).getImgURL()).fit().into(holder.filmView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("film", filmList.get(position));
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return filmList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tDesFilm;
            public ImageView filmView;

            public ViewHolder(final View itemView) {
                super(itemView);
                tDesFilm = (TextView) itemView.findViewById(R.id.desFilm);
                filmView = (ImageView) itemView.findViewById(R.id.filmView);
            }
        }
    }

    private class LoadFilmList extends LoadDataTask{

        int position;


        public LoadFilmList(Context context, int position) {
            super(context);
            this.position = position;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            org.jsoup.nodes.Document doc = Jsoup.parse(s);
            if (position == 0 || position == 1 || isSearchAnime == 1){
                Elements film = doc.body().select("div.film-block").select("div.col-film");
                for (org.jsoup.nodes.Element it: film){
                    String yearFilm = it.select("div.year-film").text();
                    String desFilm = it.select("div.des-film").text();
                    String filmURL = it.select("div.w").select("a.thumb").attr("href");
                    String name = it.select("div.w").select("a.thumb").attr("title");
                    String imgURL = it.select("div.w").select("a.thumb").select("img").attr("src");
                    String numEps = it.select("div.w").select("span.no").text();
                    imgURL = imgURL.substring(imgURL.indexOf("&url=")+5, imgURL.length());
                    FilmInfo filmInfo = new FilmInfo(name, filmURL, numEps, imgURL, desFilm, yearFilm, position, true);
                    filmList[position].add(filmInfo);
                }
//                    mainList.clear();
//                    mainList.addAll(filmList[position]);
                recycleAdapter[position].notifyDataSetChanged();
                if (filmList[position].size() == 0) {
                    Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_SHORT).show();
                }

            }
            else if (position == 3 || position == 4 || isSearchAnime == 2){
                Elements film;
                if (position == 2){
                    film = doc.body().select("ul.view-thumb-res").select("div.tn-bxitem");
                }
                else film = doc.body().select("ul#cat_tatca").select("div.tn-bxitem");
                for (org.jsoup.nodes.Element it: film){
                    if (position == 2){
                        String imgURL = it.select("a").select("img").attr("src");
                        String filmURL = "http://xuongphim.tv" + it.select("a").attr("href");
                        String name = it.select("p.bxitem-txt").text();
                        FilmInfo filmInfo = new FilmInfo(name, filmURL, "", imgURL, "", position, false);
                        filmList[position].add(filmInfo);
                        Log.d("abcd", imgURL + " " + filmURL + " " + name);
                    }
                    else{
                        String desFilm = it.select("div").select("div.tn-contentdecs.mb10").text();
                        String filmURL = "http://xuongphim.tv" + it.select("a").attr("href");
                        String name = it.select("div").select("p.name-vi").text()
                                + '\n' + it.select("div").select("p.name-en").text();
                        String imgURL = it.select("a").select("img").attr("src");
                        String numEps = it.select("div").select("p").text();
                        if (position == 4){
                            int start = numEps.indexOf("Số Tập: ");
                            int end = numEps.length();
                            numEps = numEps.substring(start, end);
                        }
                        else if (position == 3){
                            int start = numEps.indexOf("Thời lượng: ");
                            int end = numEps.length();
                            numEps = numEps.substring(start, end);
                        }
                        FilmInfo filmInfo = new FilmInfo(name, filmURL, numEps, imgURL, desFilm, position, false);
                        filmList[position].add(filmInfo);
                    }
                    if (filmList[position].size() == 0) {
                        Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_SHORT).show();
                    }

                }
                recycleAdapter[position].notifyDataSetChanged();
                isSearchAnime = 0;
            }

        }

    }

}
