package hr.unipu.inf.ma.pametnigrad.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.inf.ma.pametnigrad.R;
import hr.unipu.inf.ma.pametnigrad.database.FeedReaderDbHelper;
import hr.unipu.inf.ma.pametnigrad.post.PostAdapter;
import hr.unipu.inf.ma.pametnigrad.post.PostClass;

/**
 * A simple {@link Fragment} subclass.
 */
public class SolvedFragment extends Fragment {

    private static final String EXTRA_KEY = "Username";

    RecyclerView recyclerView;
    PostAdapter adapter;

    List<PostClass> postList;
    TextView user;
    Button save;

    public SolvedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_solved, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Rješenja");

        /*String korisnik;
        user = (TextView) getView().findViewById(R.id.user);
        save = (Button) getView().findViewById(R.id.save);
        user.setVisibility(View.GONE);
        save.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            user.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            korisnik = getArguments().getString(EXTRA_KEY,"");
            user.setText(korisnik);
        }*/

        postList = new ArrayList<>();

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


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

        while(cursor.moveToNext()) {
            Log.d("baza", Long.toString(cursor.getLong(cursor.getColumnIndexOrThrow("Label"))));
            if(cursor.getLong(cursor.getColumnIndexOrThrow("Label")) == 1){
                boolean isLabel = false;

                if(cursor.getInt(cursor.getColumnIndexOrThrow("Label")) == 1)
                    isLabel = true;

                postList.add(new PostClass(
                        cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Image")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("Location")),
                        isLabel
                ));
            }
        }
        cursor.close();

        mDbHelper.close();


        adapter = new PostAdapter(getActivity(), postList);
        recyclerView.setAdapter(adapter);

        /*save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Promjene spremljene!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
}