package io.scal.openarchive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import io.scal.openarchive.db.Media;

/**
 * Created by micahjlucas on 1/20/15.
 */
public class MediaAdapter extends ArrayAdapter<Media> {

    Context mContext;
    int layoutResourceId;
    Media data[] = null;

    public MediaAdapter(Context context, int layoutResourceId, Media[] data) {
        super(context, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        rowView = inflater.inflate(layoutResourceId, parent, false);

        ImageView ivIcon = (ImageView)rowView.findViewById(R.id.ivIcon);
        TextView tvTitle = (TextView)rowView.findViewById(R.id.tvTitle);

        tvTitle.setText(data[position].getTitle());

        return rowView;
    }
}
