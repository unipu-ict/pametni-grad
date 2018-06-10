package hr.unipu.inf.ma.pametnigrad.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import hr.unipu.inf.ma.pametnigrad.database.FeedReaderDbHelper;
import hr.unipu.inf.ma.pametnigrad.R;
import static android.app.Activity.RESULT_OK;

public class EditFragment extends Fragment {

    Button photoButton;
    Button locationButton;
    Button sendButton;

    TextView locationText;
    ImageView photoView;

    EditText titleText;
    EditText descriptionText;

    String imageData;

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    int PLACE_PICKER_REQUEST = 1;
    int PICK_IMAGE = 2;
    private static final String EXTRA_KEY = "Poslano";


    public EditFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getContext(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Prijava");

        FloatingActionButton fab= (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.GONE);

        photoButton = getView().findViewById(R.id.photo_button);
        photoView = getView().findViewById(R.id.photo);
        locationButton = getView().findViewById(R.id.location_button);
        locationText = getView().findViewById(R.id.location);
        sendButton = getView().findViewById(R.id.send);


        titleText = getView().findViewById(R.id.titleText);
        descriptionText = getView().findViewById(R.id.description);

        photoButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               //intent.setType("image/*");
               //intent.setAction(Intent.ACTION_PICK);
               //startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
               startActivityForResult(intent, PICK_IMAGE);*/

               Intent intent = new Intent();
               intent.setType("image/*");
               intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
               startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
           }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               placePicker();
           }
       });

        sendButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               savePost();
               FragmentTransaction transaction = getFragmentManager().beginTransaction();

               Bundle bundle = new Bundle();
               bundle.putString(EXTRA_KEY, "Poslano");

               DashboardFragment dashboardFragment = new DashboardFragment();
               dashboardFragment.setArguments(bundle);
               transaction.replace(R.id.mainContainer, dashboardFragment).commit();
           }
        });

    }


    public void placePicker(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;

        try{
            intent = builder.build((Activity) getContext());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        }catch (GooglePlayServicesRepairableException e){
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Log.d("baza", data.getDataString());
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                photoView.setImageBitmap(selectedImage);
                imageData = data.getData().toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK){
            Place place = PlacePicker.getPlace(getContext(), data);
            locationText.setText(place.getAddress());
        }

    }

    public void savePost(){
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContext());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Image", imageData);
        values.put("Title", titleText.getText().toString());
        values.put("Description", descriptionText.getText().toString());
        values.put("Location", locationText.getText().toString());
        values.put("Label", 0);


        long newRowId = db.insert("Post", null, values);

        db.close();
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /*public String getRealPathFromURI(Uri uri) {
        Cursor cursor = this.query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }*/
}
