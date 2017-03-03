package com.nimah.khiem.musicbox;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Khiem on 8/12/2016.
 */
public class GetAlbumTask extends AsyncTask<String, Void, ArrayList<Album>> {
    Context mContext;
    ListAlbum listAlbum;

    public GetAlbumTask(Context mContext) {
        this.mContext = mContext;
        listAlbum = (ListAlbum) mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Album> doInBackground(String... params) {
        ArrayList<Album> AlbumList = new ArrayList<>();
        String title, detailUrl, imageUrl, singer;
        try {
            Document doc = Jsoup.connect(params[0]).userAgent("Chrome").get();
            Elements elements = doc.body().select("div.playlist");
            //Log.d("aaaa", elements.toString());
            for (Element data : elements) {
                title = data.select("div.thumb div.global-figure a").attr("title");
                detailUrl = "http://nhacso.net" + data.select("div.thumb div.global-figure a").attr("href");
                imageUrl = decodeImageUrl(data.select("div.thumb div.global-figure a span").attr("style"));
                singer = data.select("div.caption h2 a").get(1).text();
                Album album = new Album(title,singer,imageUrl,detailUrl);
                //Log.d("test", title + "-----" + singer + "-----" + detailUrl + "-----" + imageUrl);
                AlbumList.add(album);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return AlbumList;
    }

    private String decodeImageUrl(String s) {
        boolean flag=false;
        String res="";
        for (int i=0; i<s.length(); i++){
            if (s.charAt(i)==')') break;
            if (flag) res=res+s.charAt(i);
            if (s.charAt(i)=='(') flag=true;
        }
        return res;
    }

    @Override
    protected void onPostExecute(ArrayList<Album> albumlist) {
        super.onPostExecute(albumlist);
        listAlbum.getAlbumList(albumlist);
    }

    interface ListAlbum{
        void getAlbumList(ArrayList<Album> albumList);
    }
}
