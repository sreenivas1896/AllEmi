package com.sreenivasn.loanemi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sreenivasn.loanemi.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class TableActivity extends AppCompatActivity {


    double[] principal, interest, balance, dataArray;
    TextView topText;
    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    double totalInt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.adMob_inter_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                TableActivity.super.onBackPressed();
            }
        });

        Bundle bP = this.getIntent().getExtras();
        Bundle bI = this.getIntent().getExtras();
        Bundle bB = this.getIntent().getExtras();
        principal = bP.getDoubleArray("P");
        interest = bI.getDoubleArray("I");
        balance = bB.getDoubleArray("B");

        RecyclerView recyclerView = findViewById(R.id.lv);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        CustomRecyclerAdapter customAdapter = new CustomRecyclerAdapter();
        recyclerView.setAdapter(customAdapter);

        Bundle bData = this.getIntent().getExtras();
        dataArray = bData.getDoubleArray("keyData");
        topText = findViewById(R.id.topText);
        topText.setText(Html.fromHtml("<b>" + "<font color=#ffff00>" + "Principal Amount:" + formatter.format(dataArray[0]) + "</b>" + "<br/>"
                + "Rate of interest:" + dataArray[1] + "%" + "<br/>"
                + "Tenure: " + (int) dataArray[2] + " Months" + "<br/>"
                + "Total Interest: " + formatter.format(dataArray[4]) + "(" + dataArray[6] + "%" + ")" + "<br/>"
                + "Total Amount: " + formatter.format(dataArray[5]) + "<br/>"
                + "<b>" + "<font color=#ffff00>" + "EMI:" + formatter.format(dataArray[3]) + "</font>" + "</b>"
        ));

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TableActivity", "The interstitial wasn't loaded yet.");
                    finish();
                }

                //super.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
            finish();
        }


    }

    //  *************************** recycler view *****************************
    /*########## Recycle view Adapter class ###########*/
    public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> {

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {

            holder.sno.setText("" + (position + 1));
            holder.inter.setText("" + formatter.format(interest[position]));
            holder.prinp.setText("" + formatter.format(principal[position]));
            holder.bala.setText("" + formatter.format(balance[position]));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getApplicationContext(), ""+(position+1), Toast.LENGTH_SHORT).show();
                    totalInt = 0;
                    int i;
                    for (i = 0; i < position; i++) {
                        totalInt = totalInt + interest[i];
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(TableActivity.this);
                    builder.setMessage(Html.fromHtml("<b>" + "Installment:" + (position + 1) + "</b>"
                            + "<font color=blue>" + "<br/>" + "<b>" + "EMI(P+I): " + formatter.format(dataArray[3]) + "</b>"
                            + "<br/>" + "Interest: " + formatter.format(interest[position])
                            + "<br/>" + "Principal on EMI: " + formatter.format(principal[position]) + "</font>"
                            + "<br/>" + "<font color=red>" + "Balance: " + formatter.format(balance[position]) + "</font>"
                            + "<br/>" + "<br/>" + "<b>" + "<font color=#186A3B>" + "Total Interest" + "(1st-" + (position + 1) + "th month):" + formatter.format(totalInt)
                            + "<br/>" + "Total EMI" + "(1st-" + (position + 1) + "th month):" + formatter.format(dataArray[3] * (position + 1))
                            + "<br/>" + "Principal Amount:" + formatter.format(dataArray[0]) + "</font>" + "</b>"))
                            .setCancelable(false)
                            .setPositiveButton("SHARE", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Installment:" + (position + 1)
                                            + "\n" + "EMI(P+I): " + formatter.format(dataArray[3])
                                            + "\n" + "Interest: " + formatter.format(interest[position])
                                            + "\n" + "Principal on EMI: " + formatter.format(principal[position])
                                            + "\n" + "Balance: " + formatter.format(balance[position])
                                            + "\n" + "\n" + "Total Interest" + "(1st-" + (position + 1) + "th month):" + formatter.format(totalInt)
                                            + "\n" + "Total EMI" + "(1st-" + (position + 1) + "th month):" + formatter.format(dataArray[3] * (position + 1))
                                            + "\n" + "Principal Amount" + formatter.format(dataArray[0]));
                                    sendIntent.setType("text/plain");
                                    startActivity(sendIntent);

                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.setTitle("Your Loan Data");
                    alert.show();
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);

                    //dialog end
                }
            });

        }

        @Override
        public int getItemCount() {
            return principal.length;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView inter, sno, prinp, bala;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                inter = itemView.findViewById(R.id.inter);
                sno = itemView.findViewById(R.id.sno);
                prinp = itemView.findViewById(R.id.princip);
                bala = itemView.findViewById(R.id.balan);

            }
        }
    }
    /*################### end #################### */


}