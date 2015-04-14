package net.opendasharchive.openarchive;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.opendasharchive.openarchive.db.Media;

import java.util.List;

/**
 * Created by micahjlucas on 1/20/15.
 */
public class MediaAdapter extends ArrayAdapter<Media> {

    Context mContext;
    int layoutResourceId;
    List<Media> data;

    public MediaAdapter(Context context, int layoutResourceId, List<Media> data) {
        super(context, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        rowView = inflater.inflate(layoutResourceId, parent, false);

        Media currentMedia = data.get(position);

        ImageView ivIcon = (ImageView)rowView.findViewById(R.id.ivIcon);
        TextView tvTitle = (TextView)rowView.findViewById(R.id.tvTitle);
        TextView tvCreateDate = (TextView)rowView.findViewById(R.id.tvCreateDate);

        ivIcon.setImageBitmap(currentMedia.getThumbnail(mContext));
        tvTitle.setText(currentMedia.getTitle());
        tvCreateDate.setText(currentMedia.getFormattedCreateDate());

        return rowView;
    }
}