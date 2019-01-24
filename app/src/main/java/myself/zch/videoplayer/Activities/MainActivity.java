package myself.zch.videoplayer.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import myself.zch.videoplayer.IMiniDouyinServise;
import myself.zch.videoplayer.JavaBeans.Feed;
import myself.zch.videoplayer.JavaBeans.FeedResponse;
import myself.zch.videoplayer.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRv = findViewById(R.id.rv);
        btn = findViewById(R.id.btn_fetch);
        initRecyclerView();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new MainActivity.MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;

                String url = mFeeds.get(i).getImage_url();
                Glide.with(iv.getContext()).load(url).into(iv);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
                        intent.putExtra("VIDEO_URL", mFeeds.get(i).getVideo_url());
                        intent.putExtra("STUDENT_ID", mFeeds.get(i).getStudent_id());
                        intent.putExtra("USER_NAME", mFeeds.get(i).getUser_name());

                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mFeeds.size();
            }
        });
    }

    private void getResponseFromMiniDouyin(Callback<FeedResponse> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.108.10.39:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(IMiniDouyinServise.class).getFeed().
                enqueue(callback);
    }

    public void fetchFeed(View view) {
        btn.setText("requesting...");
        btn.setEnabled(false);

        getResponseFromMiniDouyin(new Callback<FeedResponse>() {

            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                Toast.makeText(MainActivity.this.getApplicationContext(), "REQUEST SUCCESS", Toast.LENGTH_LONG).show();
                mFeeds = response.body().getFeeds();
                mRv.getAdapter().notifyDataSetChanged();
                Log.d("REQUEST", mFeeds.toString());
                btn.setEnabled(true);
                btn.setText("FETCH");
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                Log.d("FAIL TO FETCH", t.toString());
                Toast.makeText(MainActivity.this.getApplicationContext(), "FAILED TO REQUEST", Toast.LENGTH_LONG).show();
                btn.setEnabled(true);
                btn.setText("FETCH");
            }
        });
    }
}
