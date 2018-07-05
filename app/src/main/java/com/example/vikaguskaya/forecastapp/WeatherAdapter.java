package com.example.vikaguskaya.forecastapp;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WeatherAdapter extends BaseAdapter{
    Context context;
    LayoutInflater lInflater;
    ArrayList<Weather> forecast;


    public WeatherAdapter(Context context, ArrayList<Weather> forecast)
    {
        this.context = context;
        this.forecast = forecast;
        this.lInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return forecast.size();
    }

    @Override
    public Object getItem(int position) {
        return forecast.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    Weather getWeather(int position) {
        return ((Weather) getItem(position));
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item, parent, false);
        }
        Weather weather = getWeather(position);


        ((TextView) view.findViewById(R.id.dayTextView)).setText(weather.date);
        ((TextView) view.findViewById(R.id.descriptionTextView)).setText(weather.description);
        ((TextView) view.findViewById(R.id.hiTempTextView)).setText("Max: "  + weather.hiTemp);
        ((TextView) view.findViewById(R.id.lowTempTextView)).setText("Min: " + weather.lowTemp);
        ((TextView) view.findViewById(R.id.humidityTextView)).setText("Humidity: " + weather.humidity);
        return view;
    }


}

