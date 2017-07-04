package com.leer.ZhihuDailyEasyRead.http;

import java.util.ArrayList;

/**
 * Created by Leer on 2017/6/30.
 */

public class ZhihuLatest {
    public String date;
    public ArrayList<Story> stories;
    public ArrayList<TopStory> top_stories;

    public class TopStory {
        public String image;
        public int type;
        public long id;
        public String title;
    }

}
