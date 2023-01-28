package com.example.ict602_project.ui.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ict602_project.R;

public class ListViewNews extends ArrayAdapter<String> {

    private final Context context;
    private final String[] maintitle;
    private final String[] subtitle;
    private final Integer[] imgid;

    public ListViewNews(Context context, String[] maintitle, String[] subtitle, Integer[] imgid) {
        super(context, R.layout.list_view_news, maintitle);

        this.context = context;
        this.maintitle = maintitle;
        this.subtitle = subtitle;
        this.imgid = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_view_news, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);

        titleText.setText(maintitle[position]);
        imageView.setImageResource(imgid[position]);
        subtitleText.setText(subtitle[position]);

        return rowView;
    };
}

