package Service;

import com.zjgsu.studentmanagement.Util.admin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface admin_login_Service {
    @GET("admin/{id}")
    Call<admin> getadmin(@Path("id") int id);
    @POST("admin")
    Call<admin> createadmin(@Body admin admin);
    @PATCH("admin/{id}")
    Call<admin> modifyadmin(@Path("id") int id, @Body admin admin);
}
