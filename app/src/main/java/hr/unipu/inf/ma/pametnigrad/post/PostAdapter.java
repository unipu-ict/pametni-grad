package hr.unipu.inf.ma.pametnigrad.post;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import hr.unipu.inf.ma.pametnigrad.GlobalVariables;
import hr.unipu.inf.ma.pametnigrad.R;
import hr.unipu.inf.ma.pametnigrad.activities.MainActivity;
import hr.unipu.inf.ma.pametnigrad.database.FeedReaderDbHelper;
import hr.unipu.inf.ma.pametnigrad.fragments.DashboardFragment;
import hr.unipu.inf.ma.pametnigrad.fragments.SolvedFragment;
import hr.unipu.inf.ma.pametnigrad.user.User;

/**
 * Created by petra on 24.5.2018..
 */

@TargetApi(19)
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context mCtx;
    private List<PostClass> postList;

    public PostAdapter(Context mCtx, List<PostClass> postList) {
        this.mCtx = mCtx;
        this.postList = postList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_layout, null);
        PostViewHolder holder = new PostViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        final PostClass post = postList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewDescription.setText(post.getDescription());
        holder.textViewLocation.setText(post.getLocation());

        try {
            /*final int takeFlags =  (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mCtx.getContentResolver().takePersistableUriPermission(Uri.parse(post.getImage()), takeFlags);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mCtx.getContentResolver(), Uri.parse(post.getImage()));
            holder.imageView.setImageBitmap(bitmap);*/

            Log.d("slike", post.getImage());

            final Uri imageUri = Uri.parse(post.getImage());
            final InputStream imageStream =  mCtx.getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            holder.imageView.setImageBitmap(selectedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.checkBox.setChecked(post.isLabel());

        if(post.isLabel()){
            holder.textLabel.setTextColor(Color.GREEN);
        } else {
            holder.textLabel.setTextColor(Color.RED);
        }

        if(holder.checkBox.isChecked()){
            holder.textLabel.setTextColor(Color.GREEN);
        } else {
            holder.textLabel.setTextColor(Color.RED);
        }

        if(!GlobalVariables.admin){
            holder.checkBox.setEnabled(false);
            holder.checkBox.setVisibility(View.GONE);
        } else {
            holder.checkBox.setEnabled(true);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("baza", "post id: " + post.getId());
                MainActivity.setSolved((post.getId()), post.isLabel());

                if(post.isLabel()){
                    holder.textLabel.setTextColor(Color.GREEN);
                } else {
                    holder.textLabel.setTextColor(Color.RED);
                }

                if(holder.checkBox.isChecked()){
                    holder.textLabel.setTextColor(Color.GREEN);
                } else {
                    holder.textLabel.setTextColor(Color.RED);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textViewTitle, textViewDescription, textViewLocation, textLabel;
        CheckBox checkBox;
        User user;

        public PostViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image);
            textViewTitle = itemView.findViewById(R.id.title);
            textViewDescription = itemView.findViewById(R.id.description);
            textViewLocation = itemView.findViewById(R.id.location);
            checkBox = itemView.findViewById(R.id.label);
            textLabel = itemView.findViewById(R.id.label_text);

            //user = new User("PERO","pero");
        }
    }
}
