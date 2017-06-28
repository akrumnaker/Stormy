package com.example.aaron.stormy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaron.stormy.R;
import com.example.aaron.stormy.weather.Day;

public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days){
        mContext = context;
        mDays = days;
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;   // we aren't going to use this. Tag items for easy reference
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            // brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureHighLowLabel);
            holder.precipChanceLabel = (TextView) convertView.findViewById(R.id.dailyPrecipLabel);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        Day day = mDays[position];

        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getHighTemperature() + "\u2109\n" + day.getLowTemperature() + "\u2109");
        holder.dayLabel.setText(day.getDayOfWeek());
        holder.precipChanceLabel.setText(day.getPrecipChance() + "%");

        return convertView;
    }

    private static class ViewHolder{
        ImageView iconImageView;
        TextView dayLabel;
        TextView temperatureLabel;
        TextView precipChanceLabel;
    }
}
