package com.example.studentmanagement.api;

import com.example.studentmanagement.models.requestbody.RequestBodyLogin;
import com.example.studentmanagement.models.responsebody.ResponseBodyLogin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
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
//    @PUT("admin/updatePassword")
//    Call<ResponseObject<Object>> changePassword(@Header("Authorization") String jwt, @Body ChangePwdInfor changePwdInfor);
//
//    // Giảng Viên
//    @GET("admin/giangVien/khoa/{maKhoa}")
//    Call<ResponseObject<List<GVItemLv>>> getAllGVByMaKhoa(@Header("Authorization" ) String jwt, @Path("maKhoa") String maKhoa);
//    @GET("admin/giangVien/{id}")
//    Call<ResponseObject<GiangVien>> getGVByID(@Header("Authorization") String jwt, @Path("id") String idGV);
//
//    //Sinh Viên
//    @GET("admin/sinhVien/{id}")
//    Call<ResponseObject<SinhVien>> getSVByID(@Header("Authorization") String jwt, @Path("id") String idGV);
//
//    @GET("admin/sinhVien/lop/{maLop}")
//    Call<ResponseObject<List<SVItemLv>>> getAllSVByMaLop(@Header("Authorization") String jwt, @Path("maLop") String maLop);
//
//    //Khoa
//    @GET("admin/khoa")
//    Call<ResponseObject<List<List<Khoa>>>> getAllKhoa(@Header("Authorization") String jwt);
//    @GET("admin/khoa/{id}")
//    Call<ResponseObject<Khoa>> getKhoaById(@Header("Authorization") String jwt, @Path("id") String id);
//    // Lớp
//    @GET("admin/lop/khoa/{maKhoa}")
//    Call<ResponseObject<List<Lop>>> getLopByMaKhoa(@Header("Authorization") String jwt, @Path("maKhoa") String maKhoa);
//    @GET("admin/lop")
//    Call<ResponseObject<List<List<Lop>>>> getAllLop(@Header("Authorization") String jwt);
//    @GET("admin/lop/{id}")
//    Call<ResponseObject<Lop>> getLopById(@Header("Authorization") String jwt, @Path("id") String id);
//    // Học phần
//    @GET("admin/monHoc/khoa/{maKhoa}")
//    Call<ResponseObject<List<HPItemLv>>> getHPByMaKhoa(@Header("Authorization") String jwt, @Path("maKhoa") String maKhoa);
//    @GET("admin/monHoc")
//    Call<ResponseObject<List<List<HocPhan>>>> getAllHocPhan(@Header("Authorization") String jwt);
//    @GET("admin/monHoc/{id}")
//    Call<ResponseObject<HocPhan>> getHPById(@Header("Authorization") String jwt, @Path("id") String id);
//    // Kế hoạch
//    @GET("admin/keHoachNam")
//    Call<ResponseObject<List<List<KyItemSpinner>>>> getAllKeHoach(@Header("Authorization") String jwt);
//
//    // Lớp tín chỉ
//    @GET("admin/dsLopTc")
//    Call<ResponseObject<List<LopTCItemLv>>> getDsLopTcByMaKHMaLop(@Header("Authorization") String jwt, @Query("maKeHoach") String maKeHoach, @Query("maLop") String maLop);
//    @GET("admin/dsLopTc/{id}")
//    Call<ResponseObject<LopTinChi>> getLopTCById(@Header("Authorization") String jwt, @Path("id") String id);
//    @POST("admin/dsLopTc/giangVien/{maGV}")
//    Call<ResponseObject<List<LopTCItemLv>>> getDsLTCByMaGVMaKH(@Header("Authorization") String jwt, @Path("maGV") String maGV, @Query("maKeHoach") String maKeHoach);
//    //Chi tiet LTC
//    @GET("admin/chiTietLopTc/lopTc/{maLTC}")
//    Call<ResponseObject<List<CTLopTC>>> getCTLTCByMaLTC(@Header("Authorization") String jwt, @Path("maLTC") String maLTC);
//    // Lấy thống kê điểm 1 lớp TC theo cột
//    @GET("admin/diem/thong-ke")
//    Call<ResponseObject<List<ScoreStatistic>>> getTKDiemLopTCByCol(@Header("Authorization") String jwt, @Query("idLopTc") String idLopTc, @Query("col") String col);
//    //Diểm
//    @GET("admin/diem/lopTc/detail/{maLTC}")
//    Call<ResponseObject<List<DiemSinhVien>>> getDiemByMaLTC(@Header("Authorization") String jwt, @Path("maLTC") String maLTC);
//
//    @POST("admin/diem/{maSV}")
//    Call<ResponseObject<List<DiemSinhVien2>>> getDiemByMaSVMaKH(@Header("Authorization") String jwt, @Path("maSV") String maSV, @Query("maKeHoach") String maKeHoach);
//
//    // Thời khóa biểu
//    @GET("admin/tkb/sinhVien/{maSV}")
//    Call<ResponseObject<List<TKBItem>>> getTKBSVByTuan(@Header("Authorization") String jwt, @Path("maSV") String maSV, @Query("tuan") int tuan);
}
