package com.nimah.khiem.musicbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.JarOutputStream;

/**
 * Created by Khiem on 8/13/2016.
 */
public class GetSongQueryTask extends AsyncTask<String, Void, ArrayList<Song>> {
    Context mContext;
    QuerySong querySong;
    ProgressDialog progressDialog;

    public GetSongQueryTask(Context mContext) {
        this.mContext = mContext;
        querySong = (QuerySong) mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showLoadingDialog("Finding Songs ...");
    }

    @Override
    protected ArrayList<Song> doInBackground(String... params) {
        ArrayList<Song> SongList = new ArrayList<>();
        String title, singer, imgUrl, sourceUrl, detailUrl;
        JSONObject obj;
        try {
            Document doc = (Document) Jsoup.connect("http://nhacso.net/tim-kiem-bai-hat.html?keyName="+params[0]).userAgent("Chrome").get();
            Elements elements = doc.body().select("ul.widget-song-list li");
            for (Element data: elements){
                title = data.select("h1").text();
                singer = data.select("h2").text();
                imgUrl = decodeImageUrl(data.select("img").attr("style"));
                obj = getJSONFromUrl("http://nhacso.net/songs/ajax-get-detail-song?dataId=" + data.select("button").attr("object-id"));
                sourceUrl = obj.getJSONArray("songs").getJSONObject(0).getString("link_mp3");
                detailUrl = obj.getJSONArray("songs").getJSONObject(0).getString("link_detail");
                Song song = new Song(title,singer,sourceUrl,imgUrl,detailUrl);
                SongList.add(song);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return SongList;
    }


    private String decodeImageUrl(String s) {
        boolean flag=false;
        String res="";
        for (int i=0; i<s.length(); i++){
            if (s.charAt(i)=='?') break;
            if (flag) res=res+s.charAt(i);
            if (s.charAt(i)=='(') flag=true;
        }
        return res;
    }

    private JSONObject getJSONFromUrl(String s) throws IOException, JSONException {
        URL url=new URL(s);
        HttpURLConnection con= (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int statuscode=con.getResponseCode();
        if(statuscode==HttpURLConnection.HTTP_OK)
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb=new StringBuilder();
            String line=br.readLine();
            while(line!=null)
            {
                sb.append(line);
                line=br.readLine();
            }
            String json=sb.toString();
            JSONObject root=new JSONObject(json);
            return root;
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Song> songlist) {
        super.onPostExecute(songlist);
        querySong.getSongQueryResult(songlist);
        progressDialog.dismiss();
    }

    private void showLoadingDialog(String message) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    interface QuerySong{
        public void getSongQueryResult (ArrayList<Song> songlist);
    }

}
