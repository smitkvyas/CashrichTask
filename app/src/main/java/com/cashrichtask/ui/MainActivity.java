package com.cashrichtask.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cashrichtask.customview.EquityRateView;
import com.cashrichtask.model.EquityData;
import com.cashrichtask.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ArrayList<EquityData> mEquityData;
    private RecyclerView recyclerView;
    private Context mContext;
    private TextView txtPoint;
    private View mainView;
    private EquityRateView equityRateView;
    private TextView txtHeading;
    private TextView txtShareValue, txtFixValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext = this;
        initComponents();
        setUpView();
        loadData();
    }

    private void initComponents() {
        recyclerView = findViewById(R.id.recyclerView);
        txtPoint = findViewById(R.id.txtPoint);
        mainView = findViewById(R.id.mainView);
        equityRateView = findViewById(R.id.equityRateView);
        txtHeading = findViewById(R.id.txtHeading);

        txtShareValue = findViewById(R.id.txtShareValue);
        txtFixValue = findViewById(R.id.txtFixValue);
    }

    private void loadData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://demo0312305.mockable.io/testCashRich", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            mEquityData = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                mEquityData.add(new Gson().fromJson(jsonArray.get(i).toString(), EquityData.class));
                            }
                            setDateRecycler();
                        } else errorGettingData();
                    } catch (JSONException e) {
                        errorGettingData();
                        e.printStackTrace();
                    }
                } else errorGettingData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorGettingData();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void errorGettingData() {
        new AlertDialog.Builder(mContext)
                .setTitle("Error")
                .setMessage("Error occurred while loading data from server. Please retry")
                .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        loadData();
                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setUpView() {
        startHeadingTimerTask();
        setMainViewColor(true);
        setShareValue(0, 50);
        setFixedValue(0, 50);
    }

    private void setShareValue(int startValue, int endvalue) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startValue, endvalue);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                txtShareValue.setText(String.format("%s%%", valueAnimator.getAnimatedValue().toString()));
            }
        });
        valueAnimator.start();
    }

    private void setFixedValue(int startValue, int endvalue) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startValue, endvalue);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                txtFixValue.setText(String.format("%s%%", valueAnimator.getAnimatedValue().toString()));
            }
        });
        valueAnimator.start();
    }

    private void startHeadingTimerTask() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            boolean isSIPHeading = true;

            @Override
            public void run() {
                String headline = getString(R.string.dynamic_sip);
                if (isSIPHeading) {
                    isSIPHeading = false;
                    headline = getString(R.string.earn_more_than);
                } else isSIPHeading = true;
                updateHeadline(headline);
            }
        }, 3000, 3000);
    }

    private void updateHeadline(final String headline) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
                txtHeading.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        txtHeading.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_bottom));
                        txtHeading.setText(headline);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


            }
        });
    }

    private void setMainViewColor(boolean isNormal) {
        mainView.setBackgroundColor(ContextCompat.getColor(this, isNormal ? android.R.color.holo_blue_dark : android.R.color.holo_orange_light));
    }

    private void setDateRecycler() {
        DateAdapter dateAdapter = new DateAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(dateAdapter);
        dateAdapter.notifyDataSetChanged();
        updateView(mEquityData.get(0));
    }

    class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

        private int selectedIndex = 0;

        @NonNull
        @Override
        public DateAdapter.DateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new DateViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_dates, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DateAdapter.DateViewHolder dateViewHolder, int i) {
            final int position = i;
            final EquityData equityData = mEquityData.get(i);
            dateViewHolder.txtDate.setText(equityData.getDate());
            dateViewHolder.txtDate.getBackground().setColorFilter(ContextCompat.getColor(mContext, i == selectedIndex ? android.R.color.holo_blue_light : android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            dateViewHolder.txtDate.setTextColor(ContextCompat.getColor(mContext, i == selectedIndex ? android.R.color.background_light : android.R.color.background_dark));

            dateViewHolder.txtDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedIndex = position;
                    updateView(equityData);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEquityData.size();
        }

        public class DateViewHolder extends RecyclerView.ViewHolder {

            private final TextView txtDate;

            public DateViewHolder(@NonNull View itemView) {
                super(itemView);
                txtDate = itemView.findViewById(R.id.txtDate);
            }
        }
    }

    private void updateView(EquityData equityData) {
        int fixedValue = 100 - Integer.parseInt(equityData.getEquity());

        setShareValue(Integer.parseInt(txtShareValue.getText().toString().replace("%", "")), Integer.parseInt(equityData.getEquity()));
        setFixedValue(Integer.parseInt(txtFixValue.getText().toString().replace("%", "")), fixedValue);
        txtPoint.setText(equityData.getPoint());

        equityRateView.updateValues(Integer.parseInt(equityData.getEquity()));
        setMainViewColor(fixedValue < 50);
    }
}
