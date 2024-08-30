package Service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    public static final String baseUrl="http://10.61.1.102:3000/";
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()) // 如果使用Gson进行数据转换
            .build();
    public static admin_login_Service adminLoginService(){
        return retrofit.create(admin_login_Service.class);
    }
    public static student_Manage_Service studentManageService(){
        return retrofit.create(student_Manage_Service.class);
    }
}
