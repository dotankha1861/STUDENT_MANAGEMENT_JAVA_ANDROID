package com.example.studentmanagement.api;

import com.airbnb.lottie.L;
import com.example.studentmanagement.adapter.PracticalClassAdapter;
import com.example.studentmanagement.models.entity.Course;
import com.example.studentmanagement.models.entity.CreditClass;
import com.example.studentmanagement.models.entity.DetailCreditClass;
import com.example.studentmanagement.models.entity.Faculty;
import com.example.studentmanagement.models.entity.Lecturer;
import com.example.studentmanagement.models.entity.PracticalClass;
import com.example.studentmanagement.models.entity.Student;
import com.example.studentmanagement.models.requestbody.RequestBodyChangePassword;
import com.example.studentmanagement.models.requestbody.RequestBodyLogin;
import com.example.studentmanagement.models.responsebody.ResponseBodyLogin;
import com.example.studentmanagement.models.responsebody.ScoreCreditClass;
import com.example.studentmanagement.models.responsebody.ScoreStatistic;
import com.example.studentmanagement.models.responsebody.ScoreStudent;
import com.example.studentmanagement.models.view.CourseItem;
import com.example.studentmanagement.models.view.CreditClassItem;
import com.example.studentmanagement.models.view.FacultyItem;
import com.example.studentmanagement.models.view.LecturerItem;
import com.example.studentmanagement.models.view.PracticalClassItem;
import com.example.studentmanagement.models.view.SemesterItem;
import com.example.studentmanagement.models.view.StudentItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Authentication
    @POST("auth/signin")
    Call<ResponseObject<ResponseBodyLogin>> login(@Body RequestBodyLogin requestBodyLogin);
//
//    @POST("auth/signout")
//    Call<ResponseObject<Object>> logout(@Header("Authorization") String jwt);
//
    @PUT("admin/updatePassword")
    Call<ResponseObject<Object>> changePassword(@Header("Authorization") String jwt, @Body RequestBodyChangePassword requestBodyChangePassword);
//
    // Giảng Viên
    @GET("admin/giangVien/khoa/{maKhoa}?page=0&size=999999")
    Call<ResponseObject<List<LecturerItem>>> getAllLecturerByFacultyCode(@Header("Authorization") String jwt, @Path("maKhoa") String maKhoa);
    @GET("admin/giangVien/{id}")
    Call<ResponseObject<Lecturer>> getLecturerById(@Header("Authorization") String jwt, @Path("id") String id);
    @POST("admin/giangVien")
    Call<ResponseObject<Lecturer>> createLecturer(@Header("Authorization") String jwt, @Body Lecturer lecturer);
    @PUT("admin/giangVien")
    Call<ResponseObject<Lecturer>> updateLecturer(@Header("Authorization") String jwt, @Body Lecturer lecturer);
    @HTTP(method = "DELETE", path = "admin/giangVien", hasBody = true)
    Call<ResponseObject<List<String>>> removeLecturer(@Header("Authorization") String jwt, @Body List<String> listMaGiangVien);
//    //Khoa
    @GET("admin/khoa")
    Call<ResponseObject<List<List<FacultyItem>>>> getAllFaculty(@Header("Authorization") String jwt);
    @GET("admin/khoa/{id}")
    Call<ResponseObject<Faculty>> getFacultyById(@Header("Authorization") String jwt, @Path("id") String id);
    @POST("admin/khoa")
    Call<ResponseObject<Faculty>> createFaculty(@Header("Authorization") String jwt, @Body Faculty faculty);
    @PUT("admin/khoa")
    Call<ResponseObject<Faculty>> updateFaculty(@Header("Authorization") String jwt, @Body Faculty faculty);
    @HTTP(method = "DELETE", path = "admin/khoa", hasBody = true)
    Call<ResponseObject<List<String>>> removeFaculty(@Header("Authorization") String jwt, @Body List<String> listFacultyCode);
    // Lớp
    @GET("admin/lop/khoa/{maKhoa}")
    Call<ResponseObject<List<PracticalClassItem>>> getAllPracticalClassByFacultyCode(@Header("Authorization") String jwt, @Path("maKhoa") String maKhoa);
    @POST("admin/lop")
    Call<ResponseObject<PracticalClass>> createPracticalClass(@Header("Authorization") String jwt, @Body PracticalClass practicalClass);
    @PUT("admin/lop")
    Call<ResponseObject<PracticalClass>> updatePracticalClass(@Header("Authorization") String jwt, @Body PracticalClass practicalClass);
    @HTTP(method = "DELETE", path = "admin/lop", hasBody = true)
    Call<ResponseObject<List<String>>> removePracticalClass(@Header("Authorization") String jwt, @Body List<String> listMaLop);
    @GET("admin/lop/{id}")
    Call<ResponseObject<PracticalClass>> getPracticalClassById(@Header("Authorization") String jwt, @Path("id") String id);
    @GET("admin/lop")
    Call<ResponseObject<List<List<PracticalClassItem>>>> getAllPracticalClass(@Header("Authorization") String jwt);
//    // Học phần
    @GET("admin/monHoc")
    Call<ResponseObject<List<List<CourseItem>>>> getAllCourse(@Header("Authorization") String jwt);
    @GET("admin/monHoc")
    Call<ResponseObject<List<List<Course>>>> getAllCourseFull(@Header("Authorization") String jwt);
    @GET("admin/monHoc/khoa/{maKhoa}?page=0&size=999999")
    Call<ResponseObject<List<CourseItem>>> getAllCourseByFacultyCode(@Header("Authorization") String jwt, @Path("maKhoa") String maKhoa);
    @GET("admin/monHoc/{id}")
    Call<ResponseObject<Course>> getCourseById(@Header("Authorization") String jwt, @Path("id") String id);
    @POST("admin/monHoc")
    Call<ResponseObject<Course>> createCourse(@Header("Authorization") String jwt, @Body Course course);
    @PUT("admin/monHoc")
    Call<ResponseObject<Course>> updateCourse(@Header("Authorization") String jwt, @Body Course course);
    @HTTP(method = "DELETE", path = "admin/monHoc", hasBody = true)
    Call<ResponseObject<List<String>>> removeCourse(@Header("Authorization") String jwt, @Body List<String> listCourseCode);

    //Sinh viên
    @GET("admin/sinhVien/{id}")
    Call<ResponseObject<Student>> getStudentById(@Header("Authorization") String jwt, @Path("id") String id);
    @GET("admin/sinhVien/lop/{maLop}?page=0&size=999999")
    Call<ResponseObject<List<StudentItem>>> getAllStudentByPracticalClassCode(@Header("Authorization") String jwt, @Path("maLop") String maLop);
    @POST("admin/sinhVien")
    Call<ResponseObject<Student>> createStudent(@Header("Authorization") String jwt, @Body Student student);
    @PUT("admin/sinhVien")
    Call<ResponseObject<Student>> updateStudent(@Header("Authorization") String jwt, @Body Student student);
    @HTTP(method = "DELETE", path = "admin/sinhVien", hasBody = true)
    Call<ResponseObject<List<String>>> removeStudent(@Header("Authorization") String jwt, @Body List<String> listMaSinhVien);

    // Kế hoạch
    @GET("admin/keHoachNam")
    Call<ResponseObject<List<List<SemesterItem>>>> getAllScheme(@Header("Authorization") String jwt);

    // Lớp tín chỉ
    @GET("admin/dsLopTc/monHoc")
    Call<ResponseObject<List<CreditClassItem>>> getAllCreditClassByCourseCode(@Header("Authorization") String jwt, @Query("maKeHoach") String maKeHoach, @Query("maMh") String maMh);
    @GET("admin/dsLopTc")
    Call<ResponseObject<List<CreditClassItem>>> getAllCreditClassByPracticalClass(@Header("Authorization") String jwt, @Query("maKeHoach") String maKeHoach, @Query("maLop") String maLop);
//    @GET("admin/dsLopTc")
//    Call<ResponseObject<List<LopTCItemLv>>> getDsLopTcByMaKHMaLop(@Header("Authorization") String jwt, @Query("maKeHoach") String maKeHoach, @Query("maLop") String maLop);
    @GET("admin/dsLopTc/{id}")
    Call<ResponseObject<CreditClass>> getCreditClassById(@Header("Authorization") String jwt, @Path("id") String id);
//    @POST("admin/dsLopTc/giangVien/{maGV}")
//    Call<ResponseObject<List<LopTCItemLv>>> getDsLTCByMaGVMaKH(@Header("Authorization") String jwt, @Path("maGV") String maGV, @Query("maKeHoach") String maKeHoach);
//    //Chi tiet LTC
    @GET("admin/chiTietLopTc/lopTc/{maLTC}")
    Call<ResponseObject<List<DetailCreditClass>>> getDetailByCreditClassCode(@Header("Authorization") String jwt, @Path("maLTC") String maLTC);
    // Lấy thống kê điểm 1 lớp TC theo cột
    @GET("admin/diem/thong-ke")
    Call<ResponseObject<List<ScoreStatistic>>> getScoreStatisticByCreditClassCode(@Header("Authorization") String jwt, @Query("idLopTc") String idLopTc, @Query("col") String col);
    //Diểm
//    @GET("admin/diem/lopTc/detail/{maLTC}")
//    Call<ResponseObject<List<DiemSinhVien>>> getDiemByMaLTC(@Header("Authorization") String jwt, @Path("maLTC") String maLTC);
//
//    @POST("admin/diem/{maSV}")
//    Call<ResponseObject<List<DiemSinhVien2>>> getDiemByMaSVMaKH(@Header("Authorization") String jwt, @Path("maSV") String maSV, @Query("maKeHoach") String maKeHoach);
//
//    // Thời khóa biểu
//    @GET("admin/tkb/sinhVien/{maSV}")
//    Call<ResponseObject<List<TKBItem>>> getTKBSVByTuan(@Header("Authorization") String jwt, @Path("maSV") String maSV, @Query("tuan") int tuan);
    // Điểm
    @POST("admin/diem/{maSV}")
    Call<ResponseObject<List<ScoreStudent>>> getScoreByStudentCode(@Header("Authorization") String jwt, @Path("maSV") String maSV, @Query("maKeHoach") String maKeHoach);
    @GET("admin/diem/lopTc/detail/{maLTC}")
    Call<ResponseObject<List<ScoreCreditClass>>> getScoreByCreditClassCode(@Header("Authorization") String jwt, @Path("maLTC") String maLTC);
}
