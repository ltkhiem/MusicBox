package com.nimah.khiem.musicbox;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.ArrayRes;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Khiem on 8/12/2016.
 */
public class GetSongListTask extends AsyncTask<String, Void, ArrayList<Song>> {
    Context mContext;
    ListSong listsong;

    public GetSongListTask(Context mContext) {
        this.mContext = mContext;
        listsong = (ListSong) mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<Song> doInBackground(String... params) {
        String reqId = getIdFromUrl(params[0]);
        ArrayList<Song> SongList = new ArrayList<>();
        String title, singer, sourceUrl, detailUrl, imageUrl;
        try {
            JSONObject data = getJSONFromUrl("http://nhacso.net/albums/ajax-get-detail-album?dataId=" + reqId);
            JSONArray songs = data.getJSONArray("songs");
            for (int i=0; i<songs.length(); i++)
            {
                JSONObject o = songs.getJSONObject(i);
                title = o.getString("name");
                singer = o.getJSONArray("singer").getJSONObject(0).getString("alias");
                sourceUrl = o.getString("link_mp3");
                detailUrl = o.getString("link_detail");
                imageUrl = o.getString("link_image");
                Song song = new Song(title, singer, sourceUrl, imageUrl, detailUrl);
                SongList.add(song);
            }
        } catch (MalformedURLException e) {
            Log.d("error", "error1");
        } catch (IOException e) {
            Log.d("error", "error2");
        } catch (JSONException e) {
            Log.d("error", "error3");
        }
        return SongList;
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
            Log.d("JSON", json);
            JSONObject root=new JSONObject(json);
            return root;
        }
        return null;
    }

    private String getIdFromUrl(String s) {
        boolean flag = false;
        String res = "";
        for (int i=s.length()-1; i>0; i--){
            if (flag && s.charAt(i)=='.') break;
            if (flag) res = s.charAt(i)+ res;
            if (s.charAt(i)=='.') flag=true;
        }
        return res;
    }

    @Override
    protected void onPostExecute(ArrayList<Song> songlist) {
        super.onPostExecute(songlist);
        listsong.getSongList(songlist);
    }

    interface ListSong{
        public void getSongList(ArrayList<Song> songlist);
    }
}
