package com.example.readmylnk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {


    private List<Note> items;
    private List<Note> itemsPendingRemoval;
//    private LayoutInflater inflater;
//    private View view_all;
    private Context context_all;
    private SQLHelpDB db;
    RecAdapter(Context context, List<Note> data) {
        this.itemsPendingRemoval = new ArrayList<>();
        this.items = data;
//        this.inflater = LayoutInflater.from(context);
        this.context_all = context;
        this.db = new SQLHelpDB(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        view_all = inflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LOG", String.valueOf(position));
                Intent intent = new Intent(context_all, ReadActivity.class);
                intent.putExtra("conn", items.get(position).getNoteContent());
                context_all.startActivity(intent);
            }
        });
        final Note item = items.get(position);
        if (item.getNoteImageContent() != null){
            new DownloadImageTask(viewHolder.imageContent).execute(item.getNoteImageContent());
        }else {
            viewHolder.imageContent.setImageDrawable(context_all.getResources().getDrawable(R.drawable.ic_launcher_foreground, null));
        }
        viewHolder.titleTextView.setText(item.getNoteTitle());
        viewHolder.contentTextView.setText(item.getNoteContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void remove(int position) {
        Note item = items.get(position);
        itemsPendingRemoval.remove(item);
        if (items.contains(item)) {
            db.deleteNote(item);
            items.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView contentTextView;
        ImageView imageContent;

        ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
            titleTextView = itemView.findViewById(R.id.list_name);
            contentTextView = itemView.findViewById(R.id.sub_list_name);
            imageContent = itemView.findViewById(R.id.image_on_item);
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                int targetW = bmImage.getMaxWidth();
                int targetH = bmImage.getMaxHeight();
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new URL(urldisplay).openStream(),null, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                mIcon11 = BitmapFactory.decodeStream(new URL(urldisplay).openStream(), null,bmOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result !=null){
                bmImage.setImageBitmap(result);
            }
        }
    }
}
