package hr.unipu.inf.ma.pametnigrad.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import hr.unipu.inf.ma.pametnigrad.R;
import hr.unipu.inf.ma.pametnigrad.database.FeedReaderDbHelper;
import hr.unipu.inf.ma.pametnigrad.post.PostClass;
import xdroid.core.Global;

public class DashboardFragment extends Fragment {

    private static final String EXTRA_KEY = "Poslano";

    PieChart pieChart;
    CardView cardViewLeft;
    CardView cardViewRight;
    TextView postsText;
    TextView solvedText;

    float rijesenoDa = 0;
    float rijesenoNe = 0;

    public DashboardFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.
                inflate(R.layout.fragment_dashboard, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String postSent = "";

        Bundle bundle = this.getArguments();
        if(bundle != null){
            postSent = getArguments().getString(EXTRA_KEY,"");
            if(postSent.matches("Poslano"))
                Snackbar.make(view, "Prijava uspješno poslana!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        }

        FloatingActionButton fab= (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);

        final FragmentTransaction transaction = getFragmentManager().beginTransaction();

        cardViewLeft = (CardView) getView().findViewById(R.id.card_left);
        cardViewRight = (CardView) getView().findViewById(R.id.card_right);
        postsText = (TextView) getView().findViewById(R.id.posts_text);
        solvedText = (TextView) getView().findViewById(R.id.solved_text);


        cardViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewFragment viewFragment = new ViewFragment();
                transaction.replace(R.id.mainContainer, viewFragment).commit();
            }
        });

        cardViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SolvedFragment solvedFragment = new SolvedFragment();
                transaction.replace(R.id.mainContainer, solvedFragment).commit();
            }
        });

        pieChart = (PieChart) getView().findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> percentage = new ArrayList<>();

        //udio riješenih prijava

        PostotakRijesenih();

        percentage.add(new PieEntry(rijesenoDa)); // Riješeno
        percentage.add(new PieEntry(rijesenoNe)); // Neriješeno

        postsText.setText(Float.toString(rijesenoDa + rijesenoNe));
        solvedText.setText(Float.toString(rijesenoDa));

       /* Description description = new Description();
        description.setText("Za građane koji žele upravljati svojim gradom ili kvartom.");
        description.setTextSize(12);
        pieChart.setDescription(description);*/

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(percentage, "Udio riješenih prijava");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
    }

    public void PostotakRijesenih(){
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "Post",   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List labels = new ArrayList<>();

        int brojRijesenih = 0;

        while(cursor.moveToNext()) {
            long labelStatus = cursor.getLong(cursor.getColumnIndexOrThrow("Label"));
            labels.add(labelStatus);

            if (labelStatus == 1)
                brojRijesenih++;
        }
        cursor.close();

        if(brojRijesenih == 0){
            rijesenoDa = 0;
            rijesenoNe = labels.size() - brojRijesenih;
        } else {
            rijesenoDa = brojRijesenih;
            rijesenoNe = labels.size() - brojRijesenih;
        }

        mDbHelper.close();
    }
}
