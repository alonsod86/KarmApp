package fs.ndt.karmapp.cards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import fs.ndt.karmapp.R;
import fs.ndt.karmapp.rest.REST;
import me.everything.providers.android.calendar.Event;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM '@' hh:mm");
    private ArrayList<Event> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtResume;
        public ImageView mapView;

        public ViewHolder(View v) {
            super(v);
            txtResume = (TextView) v.findViewById(R.id.txtResume);
            mapView = (ImageView) v.findViewById(R.id.mapView);
        }
    }

    public void add(int position, Event item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardAdapter(ArrayList<Event> myDataset) {
        mDataset = myDataset;
        Comparator<Event> comparator = new Comparator<Event>() {
            public int compare(Event c1, Event c2) {
                return new Long(c1.dTStart).compareTo(c2.dTStart);
            }
        };

        Collections.sort(mDataset, comparator);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = mDataset.get(position).title;
        String location = mDataset.get(position).eventLocation;
        String date = formatter.format(new Date(mDataset.get(position).dTStart));

        // fill the card
        holder.txtResume.setText(date + " - " + name);
        location = location == null ? "Madrid" : location.replace(" ", "%20");

        // TODO: Transform parkings, etc into coordinates
        String path = "/fetch?";
        path = path.concat("date=" + mDataset.get(position).dTStart);
        path = path.concat("location=" + location);
        REST.get(path, null, new FetchHandler(location, holder));

        // on card click, navigate to detail view
        holder.mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Show extended view
                //remove(name);
            }
        });
    }

    private void buildCard(String location, ViewHolder holder) {
        String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center="+location+"&zoom=14&size=640x390&scale=2&maptype=roadmap";
        imageUrl+="&markers=color:blue%7Clabel:S%7C40.431295,-3.691917";
        imageUrl+="&key=AIzaSyDiiwz46tDsceV4AIrD0wm7sWLAhD2pK54";

        // show The Image in a ImageView
        new DownloadImageTask(holder.mapView)
                .execute(imageUrl);

        System.out.println(imageUrl);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * Async image downloader for Google Maps
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Resolves the visualization of a CardView with the location provided
     */
    private class FetchHandler extends JsonHttpResponseHandler {
        private String location;
        private ViewHolder holder;

        public FetchHandler (String location, ViewHolder holder) {
            this.location = location;
            this.holder = holder;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            buildCard(location, holder);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            buildCard(location, holder);
        }
    }
}
