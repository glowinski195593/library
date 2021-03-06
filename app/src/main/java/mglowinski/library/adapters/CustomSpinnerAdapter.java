package mglowinski.library.adapters;

/**
 * Created by macglo on 27.09.17.
 */

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mglowinski.library.R;

/**
 * Created by macglo on 21.08.17.
 */

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> data;
    private LayoutInflater inflater;

    public CustomSpinnerAdapter(Context context, List<String> objects) {
        super(context, R.layout.row_spinner_category, objects);

        this.context = context;
        data = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.row_spinner_category, parent, false);
        TextView category = view.findViewById(R.id.bookCategory);
        category.setText(data.get(position).toString());
        return view;
    }
}