package fs.ndt.karmapp.cards;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fs.ndt.karmapp.R;
import fs.ndt.karmapp.rest.REST;
import me.everything.providers.android.calendar.Event;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM '@' hh:mm");
    private ArrayList<Event> mDataset;
    private Resources resources;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtResume;
        public ImageView mapView;
        public TextView txtTime;
        public TextView txtWeather;
        public TextView txtMin;
        public TextView txtMax;
        public SeekBar barEvents;
        public SeekBar barParking;
        public SeekBar barAlergies;
        public FloatingActionButton fab;

        public ViewHolder(View v) {
            super(v);
            txtResume = (TextView) v.findViewById(R.id.txtResume);
            txtMax = (TextView) v.findViewById(R.id.txtMax);
            txtMin = (TextView) v.findViewById(R.id.txtMin);
            mapView = (ImageView) v.findViewById(R.id.mapView);
            txtTime = (TextView) v.findViewById(R.id.txtTime);
            txtWeather = (TextView) v.findViewById(R.id.txtWeather);
            barEvents = (SeekBar) v.findViewById(R.id.barEvents);
            barParking = (SeekBar) v.findViewById(R.id.barParking);
            barAlergies = (SeekBar) v.findViewById(R.id.barAlergies);
            fab = (FloatingActionButton) v.findViewById(R.id.fab);
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
    public CardAdapter(ArrayList<Event> myDataset, Resources resources) {
        this.mDataset = myDataset;
        this.resources = resources;
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
        Date dateStart = new Date(mDataset.get(position).dTStart);
        Date dateEnd = new Date(mDataset.get(position).dTend);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String name = mDataset.get(position).title;
        String location = mDataset.get(position).eventLocation;
        String date = formatter.format(dateStart);

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(dateStart);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dateEnd);

        // fill the card
        holder.txtResume.setText(date + " - " + name);
        holder.txtTime.setText("Desde las " + calStart.get(Calendar.HOUR_OF_DAY) + ":0" + calStart.get(Calendar.MINUTE)
                + " hasta las " + calEnd.get(Calendar.HOUR_OF_DAY) + ":0" + calEnd.get(Calendar.MINUTE));
        location = location == null ? "Madrid" : location.replace(" ", "%20");

        // TODO: Transform parkings, etc into coordinates
        String path = "/event";
        JSONObject body = new JSONObject();
        try {
            body.put("date", "" + mDataset.get(position).dTStart);
            body.put("direccion", location);
            body.put("id", "" + mDataset.get(position).id);
            REST.post(path, body, new FetchHandler(location, holder));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // on card click, navigate to detail viewcalEnd
        holder.mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Show extended view
                //remove(name);
            }
        });
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

    private void buildWeather(JSONObject weather, ViewHolder holder) throws JSONException {
        holder.txtWeather.setText(weather.getString("forecast"));
        holder.txtMax.setText("" + weather.getInt("max") + "º");
        holder.txtMin.setText("" + weather.getInt("min") + "º");
    }

    private void buidThermometer(JSONObject thermometer, ViewHolder holder) throws JSONException {
        holder.barParking.setProgress(thermometer.getInt("parkings"));
        holder.barAlergies.setProgress(thermometer.getInt("alergies"));
        holder.barEvents.setProgress(thermometer.getInt("events"));
    }

    private void buildAlert(int alert, ViewHolder holder) throws JSONException {
        switch (alert) {
            case 0: {
                holder.fab.setImageResource(R.drawable.ic_thumb_up_white_48dp);
                holder.fab.setBackgroundTintList(resources.getColorStateList(R.color.alert_ok));
                break;
            }
            case 1: {
                holder.fab.setImageResource(R.drawable.ic_info_white_48dp);
                holder.fab.setBackgroundTintList(resources.getColorStateList(R.color.alert_warn));
                break;
            }
            case 2: {
                holder.fab.setImageResource(R.drawable.ic_warning_white_48dp);
                holder.fab.setBackgroundTintList(resources.getColorStateList(R.color.alert_kaos));
                break;
            }
        }
    }

    private void buidMap(JSONArray events, JSONArray parkings, ViewHolder holder) throws JSONException {
        String eventsStr = "&markers=color:red%7Clabel:E%7C";
        boolean first = true;
        for (int index = 0; index<events.length(); index++) {
            try {
                if (!first) {
                    eventsStr += "|";
                } else {
                    first = false;
                }
                eventsStr += ((JSONObject) events.get(index)).getDouble("lat") + ","
                        + ((JSONObject) events.get(index)).getDouble("lon");
            } catch (Exception e){}
        }
        eventsStr+="&markers=color:blue%7Clabel:P%7C";
        first = true;
        for (int index = 0; index<parkings.length(); index++) {
            try {
                if (!first) {
                    eventsStr += "|";
                } else {
                    first = false;
                }
                eventsStr += ((JSONObject) parkings.get(index)).getDouble("lat") + ","
                        + ((JSONObject) parkings.get(index)).getDouble("lon");
            } catch (Exception e){}
        }
        String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center="+"Madrid"+"&zoom=13&size=640x390&scale=2&maptype=roadmap";
        imageUrl+=eventsStr;
        imageUrl+="&key=AIzaSyDiiwz46tDsceV4AIrD0wm7sWLAhD2pK54";

        // show The Image in a ImageView
        new DownloadImageTask(holder.mapView)
                .execute(imageUrl);

        System.out.println(imageUrl);
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
            try {
                buidThermometer((JSONObject) response.get("thermometer"), holder);
                buildWeather((JSONObject) response.get("weather"), holder);
                buildAlert(response.getInt("alert"), holder);
                buidMap((JSONArray) response.get("events"),(JSONArray) response.get("parkings"), holder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
        }
    }
}
