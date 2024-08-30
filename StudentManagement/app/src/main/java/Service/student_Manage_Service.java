package Service;

import com.zjgsu.studentmanagement.Util.student;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface student_Manage_Service {
    @POST("Students")
    Call<student> createstudents(@Body student student);

    @DELETE("Students/{id}")
    Call<Void> deletestudents(@Path("id") String id);
}
