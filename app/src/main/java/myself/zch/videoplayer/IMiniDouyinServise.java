package myself.zch.videoplayer;

import myself.zch.videoplayer.JavaBeans.FeedResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IMiniDouyinServise {
    @GET("minidouyin/feed")
    Call<FeedResponse> getFeed();
}
