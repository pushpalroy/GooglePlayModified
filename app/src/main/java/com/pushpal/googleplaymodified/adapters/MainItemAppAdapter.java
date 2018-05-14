package com.pushpal.googleplaymodified.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.bumptech.glide.Glide;
import com.pushpal.googleplaymodified.R;
import com.pushpal.googleplaymodified.activities.AppActivity;
import com.pushpal.googleplaymodified.models.MainItemAppModel;

import java.util.ArrayList;

public class MainItemAppAdapter extends RecyclerView.Adapter<MainItemAppAdapter.MyViewHolder> {

    private final ArrayList<MainItemAppModel> mArrayList;
    private Context mcontext;

    MainItemAppAdapter(ArrayList<MainItemAppModel> mArrayList) {
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mcontext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_app, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        Glide.with(mcontext)
                .load(mArrayList.get(position).getAppImage())
                .into(holder.iv_app_image);

        holder.tv_app_name.setText(mArrayList.get(position).getAppName());
        holder.tv_app_rating.setText(mArrayList.get(position).getAppRating());
        holder.btn_install.setText(mArrayList.get(position).getInstallStatus());

        switch (mArrayList.get(position).getInstallStatus()) {
            case "INSTALL":
                holder.btn_install.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.install_button_selector));
                break;
            case "UPDATE":
                holder.btn_install.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.update_button_selector));
                break;
            case "OPEN":
                holder.btn_install.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.open_button_selector));
                break;
        }

        final Handler handler = new Handler();

        holder.btn_install.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {

                if (holder.btn_install.getText().equals("OPEN")) {
                    PackageManager pm = mcontext.getPackageManager();
                    Intent launchIntent = pm.getLaunchIntentForPackage("com.udacity.android");
                    if (launchIntent != null) {
                        mcontext.startActivity(launchIntent);
                    } else {
                        Toast.makeText(mcontext, "You forgot this is a clone! Udacity App is not installed in your device!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    new AsyncTask<Integer, Void, Void>() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            holder.progress.setVisibility(View.VISIBLE);

                            switch (holder.btn_install.getText().toString()) {
                                case "INSTALL":
                                    holder.progress.setProgressColor(ContextCompat.getColor(mcontext, R.color.colorGreenDark));
                                    break;
                                case "UPDATE":
                                    holder.progress.setProgressColor(ContextCompat.getColor(mcontext, R.color.colorYellowDark));
                                    break;
                            }

                            holder.btn_install.setText("INSTALLING");
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            holder.progress.setVisibility(View.INVISIBLE);
                            holder.btn_install.setText("OPEN");
                            holder.btn_install.setBackground(ContextCompat.getDrawable(mcontext, R.drawable.open_button_selector));
                        }

                        @Override
                        protected Void doInBackground(final Integer... progressStatus) {
                            int progress = progressStatus[0];
                            while (progress < 100) {
                                progress += 1;

                                try {
                                    Thread.sleep(25);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                final int finalProgress = progress;
                                handler.post(new Runnable() {
                                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                    @Override
                                    public void run() {
                                        holder.progress.setProgress(finalProgress);
                                        Log.d("MyAdapterProgress", "progress: " + progressStatus[0]);
                                    }
                                });
                            }

                            return null;
                        }
                    }.execute(1);
                }
            }
        });

        holder.cardViewLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(mcontext);
                dialog.setContentView(R.layout.dialog_app_content);
                dialog.findViewById(R.id.install_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mcontext, AppActivity.class);
                        mcontext.startActivity(intent);
                    }
                });
                dialog.show();
                return true;
            }
        });

        Log.d("MyAdapter", "position: " + position);
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView iv_app_image;
        private final TextView tv_app_name;
        private final TextView tv_app_rating, btn_install;
        private final LinearLayout cardViewLayout;
        RoundCornerProgressBar progress;

        MyViewHolder(View view) {
            super(view);

            iv_app_image = view.findViewById(R.id.iv_app_image);
            tv_app_name = view.findViewById(R.id.tv_app_name);
            tv_app_rating = view.findViewById(R.id.tv_app_rating);
            cardViewLayout = view.findViewById(R.id.cardViewLayout);
            btn_install = view.findViewById(R.id.install_button);
            progress = view.findViewById(R.id.progress);

            cardViewLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mcontext, AppActivity.class);
            mcontext.startActivity(intent);
        }
    }
}
