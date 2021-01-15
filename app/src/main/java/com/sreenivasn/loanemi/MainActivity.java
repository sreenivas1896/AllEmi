package com.sreenivasn.loanemi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    Button submit, table;
    TextInputEditText P, I, T;
    RadioButton r1, r2;
    RadioGroup radioGroup;
    TextView tv1, tv2, percent,termsConditions,privacyPolicy,copyYear;
    ProgressBar progressBarFr, progressBarBack, mainProgress;
    String time2;
    int terms;
    double principal, interest, Emi, p1, p2, ratio, ratepermonth, Emi1, totalInterest, totalPrincipal,
            totalAmount, interestOnPr,Time;
    double[] principalArray, interestArray, balanceArray,allData;
    DecimalFormat df;
    ConstraintLayout rl;
    private AdView mAdView;
    TextInputLayout pl, il, tl;
    Animation connectingAnimation;
    LinearLayout linearLayout1,ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);


        mAdView = findViewById(R.id.adView);
        linearLayout1=findViewById(R.id.lin_lay1);

        pl = findViewById(R.id.pl);
        il = findViewById(R.id.il);
        tl = findViewById(R.id.tl);

        P = findViewById(R.id.principal);
        P.setRawInputType(Configuration.KEYBOARD_12KEY);
        I = findViewById(R.id.rate);
        T = findViewById(R.id.time);
        termsConditions = findViewById(R.id.terms_conditions);
        privacyPolicy = findViewById(R.id.privacy);
        copyYear = findViewById(R.id.copy_year);
        copyYear.setText(Html.fromHtml("\u00a9 "+ Calendar.getInstance().get(Calendar.YEAR)));

        submit = findViewById(R.id.submit);
        table = findViewById(R.id.table);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        ll = findViewById(R.id.lv);
        radioGroup = findViewById(R.id.rg);
        r1 = findViewById(R.id.month);
        r2 = findViewById(R.id.year);

        mainProgress = findViewById(R.id.main_bar);
        mainProgress.setVisibility(View.GONE);
        r1.setChecked(true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        progressBarFr = findViewById(R.id.stats);
        progressBarBack = findViewById(R.id.background_progressbar);
        rl = findViewById(R.id.rl);
        percent = findViewById(R.id.per);

        //connectingAnimation= AnimationUtils.loadAnimation(MainActivity.this, R.anim.alpha_scale_animation);
        //table.startAnimation(connectingAnimation);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.month:
                        if (T.getText().toString().length() != 0) {
                            table.setVisibility(View.GONE);
                            ll.setVisibility(View.GONE);
                            rl.setVisibility(View.GONE);

                            double t2 = Double.parseDouble(T.getText().toString());
                            T.setText("" + t2 * 12);
                        }
                        break;
                    case R.id.year:
                        if (T.getText().toString().length() != 0) {
                            table.setVisibility(View.GONE);
                            ll.setVisibility(View.GONE);
                            rl.setVisibility(View.GONE);
                            double t1 = Double.parseDouble(T.getText().toString());
                            T.setText("" + t1 / 12);
                        }
                        break;
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (TextUtils.isEmpty(P.getText().toString())) {
                    P.setError("Enter Principal");
                    P.requestFocus();
                } else if (Long.parseLong(P.getText().toString()) > 1000000000) {
                    P.setError("Enter Less than 100 Crore");
                    P.requestFocus();
                } else if (TextUtils.isEmpty(I.getText().toString())) {
                    I.setError("Enter Rate of Interest");
                    I.requestFocus();
                } else if (Double.parseDouble(I.getText().toString()) > 30) {
                    I.setError("Rate of Interest less than 30%");
                    I.requestFocus();
                } else if (TextUtils.isEmpty(T.getText().toString())) {
                    T.setError("Enter Time/Tenure");
                    T.requestFocus();
                } else if (Double.parseDouble(T.getText().toString()) > 360 && r1.isChecked()) {
                    T.setError("Enter less than 360 Months");
                    T.requestFocus();
                } else if (Double.parseDouble(T.getText().toString()) > 30 && r2.isChecked()) {
                    T.setError("Enter less than 30 Years");
                    T.requestFocus();

                } else {
                    receiveETextData();
                }

            }
        });
        table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*loadData1();
                table.clearAnimation();
                connectingAnimation.cancel();
                connectingAnimation.reset();*/
                mainProgress.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (principalArray != null && interestArray != null && balanceArray != null) {
                            Intent in = new Intent(MainActivity.this, TableActivity.class);
                            in.putExtra("P", principalArray);
                            in.putExtra("I", interestArray);
                            in.putExtra("B", balanceArray);
                            allData = new double[]{principal, interest, terms, Emi1, totalInterest, totalAmount, interestOnPr};
                            in.putExtra("keyData", allData);
                            //in.putExtra("keyEmi",Emi1);
                            startActivity(in);
                        } else {
                            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                        mainProgress.setVisibility(View.GONE);
                    }
                }, 1000);


            }
        });

        termsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://sites.google.com/view/loanemicalculatorapp/terms-conditions"));
                startActivity(viewIntent);
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://sites.google.com/view/loanemicalculatorapp/privacy-policy"));
                startActivity(viewIntent);
            }
        });
    }

    public void loadData1() {
        for (int i = 0; i <= terms - 1; i++) {

            interestArray[i] = (principal * ratepermonth);
            principalArray[i] = (Emi - interestArray[i]);
            balanceArray[i] = (principal - principalArray[i]);
            principal = balanceArray[i];

        }
        loadTotalInterest();

    }

    public void loadData() {

        principal = Double.parseDouble(P.getText().toString());
        interest = Double.parseDouble(I.getText().toString());

        terms = (int) Time;

        principalArray = new double[terms];
        interestArray = new double[terms];
        balanceArray = new double[terms];
        df = new DecimalFormat("##########.##");
        ratio = p1 / p2;
        ratepermonth = interest / (12 * 100);
        Emi = (principal * ratepermonth * Math.pow(1 + ratepermonth, Time)) / (Math.pow(1 + ratepermonth, Time) - 1);
        Emi1 = Double.parseDouble(df.format(Emi));
        table.setVisibility(View.VISIBLE);
        ll.setVisibility(View.VISIBLE);
        rl.setVisibility(View.VISIBLE);

    }

    public void loadTotalInterest() {
        totalInterest = 0;
        totalPrincipal = 0;
        for (double num : interestArray) {
            totalInterest = totalInterest + num;
        }
        for (double num : principalArray) {
            totalPrincipal = totalPrincipal + num;
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        interestOnPr = Double.parseDouble(df.format((totalInterest / totalPrincipal) * 100));
        principal = Double.parseDouble(P.getText().toString());
        tv1.setText(Html.fromHtml("Principal Amount: " + formatter.format(principal) + "<br/>"
                + "Rate of Interest: " + interest + " %" + "<br/>"
                + "Tenure: " + time2));

        totalAmount = principal + totalInterest;
        tv2.setText(Html.fromHtml("<font color=red><b>EMI: " + formatter.format(Emi1) + "</b></font><br/>"
                + "<font color=blue><b>Total Interest: " + formatter.format(totalInterest) + "</b>(" + df.format(interestOnPr) + " %)</font><br/>"
                + "<font color=#008000><b>Total Amount: " + formatter.format(totalAmount) + "</b>(" + df.format(100 + interestOnPr) + " %)</font>"));
    }

    public void receiveETextData() {
        if (r1.isChecked()) {
            Time = Double.parseDouble(T.getText().toString());
            time2 = Time + " Months";
            loadData();

        } else if (r2.isChecked()) {

            double time = Double.parseDouble(T.getText().toString());
            Time = time * 12;
            time2 = time + " Years";
            loadData();

        } else {
            Toast.makeText(MainActivity.this, "Please select month or year", Toast.LENGTH_LONG).show();
        }
        loadData1();
        progressBarFr.setProgress((int) interestOnPr);
        percent.setText(interestOnPr + "%");
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want close the Application?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getString(R.string.app_name));
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.reset:
                P.setText("");
                I.setText("");
                T.setText("");
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                return true;
            case R.id.about:
                Intent in=new Intent(MainActivity.this,AboutActivity.class);
                startActivity(in);
                return true;
            default:super.onOptionsItemSelected(menuItem);
        }

        return true;
    }
}