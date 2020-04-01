package com.kiran.sqlitedb.view;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kiran.sqlitedb.R;
import com.kiran.sqlitedb.database.model.Fruit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FruitsAdapter extends RecyclerView.Adapter<FruitsAdapter.MyViewHolder> {

    private Context context;
    private List<Fruit> fruitsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView fruit;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            fruit = view.findViewById(R.id.fruit);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }


    public FruitsAdapter(Context context, List<Fruit> fruitsList) {
        this.context = context;
        this.fruitsList = fruitsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fruit_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Fruit fruit = fruitsList.get(position);

        holder.fruit.setText(fruit.getFruit());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(fruit.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return fruitsList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
