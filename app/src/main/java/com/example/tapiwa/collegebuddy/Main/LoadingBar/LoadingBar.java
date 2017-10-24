package com.example.tapiwa.collegebuddy.Main.LoadingBar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.Main.MainFrontPage;
import com.example.tapiwa.collegebuddy.R;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

/**
 * Created by tapiwa on 10/23/17.
 */

public class LoadingBar {

    int total;
    int completed;
    int screenWidth;
    View horizontalBar;
    TextView completionPercentage;

    public LoadingBar() {

    }

    public LoadingBar(int completed, int uncompleted, View view, TextView completionPercentage) {
        this.total = completed + uncompleted;
        this.completed = completed;
        this.horizontalBar = view;
        this.completionPercentage = completionPercentage;
        this.screenWidth = view.getWidth();
    }

    public void setCompletionBar() {
        changeLengthCompletionBar();
        setColor();
        percentageCompleted();
    }

    public void updateCompletionBar(int completed, int uncompleted, int initialLength, View view, TextView pcntage) {
        this.completed = completed;
        this.completionPercentage = pcntage;
        this.horizontalBar = view;
        this.screenWidth = initialLength;
        this.total = completed + uncompleted;
        setCompletionBar();
    }

    public void percentageCompleted() {
        int pcnt = (int) Math.floor(((double) completed / total) * 100);
        String percentage = pcnt + "%";
        completionPercentage.setText(percentage);
    }



    public int calculateCompletedScreenWidth() {
        double x = ((double) completed / total) * screenWidth;
        int newLoadingBarWdith = (int) Math.floor(x);
        return newLoadingBarWdith;
    }

    public void changeLengthCompletionBar() {
        horizontalBar.setLayoutParams(new LinearLayout
                .LayoutParams(calculateCompletedScreenWidth(), horizontalBar.getHeight()));
    }



    public void reset() {
        completed = 0;
        total = 0;
        changeLengthCompletionBar();
        percentageCompleted();
        setColor();
    }

    public void updateTotal() {
        ++total;
        setCompletionBar();
    }


    public void setColor() {

       if(completed <= (0.10 * total)) {
           horizontalBar.setBackgroundColor(Color.rgb(208,35,35));
           completionPercentage.setTextColor(Color.rgb(208,35,35));
           return;
       }

       if(completed < (0.20 * total)) {
           horizontalBar.setBackgroundColor(Color.rgb(191,32,69));
           completionPercentage.setTextColor(Color.rgb(191,32,69));
           return;
       }

        if(completed < (0.30 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(170,28,103));
            completionPercentage.setTextColor(Color.rgb(170,28,103));
            return;
        }

        if(completed < (0.40 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(147,25,148));
            completionPercentage.setTextColor(Color.rgb(147,25,148));
            return;
        }

        if(completed < (0.50 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(97,30,139));
            completionPercentage.setTextColor(Color.rgb(97,30,139));
            return;
        }

        if(completed < (0.60 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(78,34,142));
            completionPercentage.setTextColor(Color.rgb(78,34,142));
            return;
        }

        if(completed < (0.70 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(54,40,144));
            completionPercentage.setTextColor(Color.rgb(54,40,144));
            return;
        }

       if(completed < (0.80 * total)) {
           horizontalBar.setBackgroundColor(Color.rgb(36,64,140));
           completionPercentage.setTextColor(Color.rgb(36,64,140));
           return;
       }


       if(completed < (0.90 * total)) {
           horizontalBar.setBackgroundColor(Color.rgb(31,84,135));
           completionPercentage.setTextColor(Color.rgb(31,84,135));
           return;
       }

        if(completed < (0.95 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(42,143,92));
            completionPercentage.setTextColor(Color.rgb(42,143,92));
            return;
        }

       horizontalBar.setBackgroundColor(Color.rgb(35,169,28));
        completionPercentage.setTextColor(Color.rgb(35,169,28));
    }
}