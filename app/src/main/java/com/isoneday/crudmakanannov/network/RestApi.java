package com.isoneday.crudmakanannov.network;


import com.isoneday.crudmakanannov.model.ModelLogin;
import com.isoneday.crudmakanannov.model.ModelRegisterPojo;
import com.isoneday.crudmakanannov.model.ResponseKategoriMakanan;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Blackswan on 9/12/2017.
 */

public interface RestApi {


    @FormUrlEncoded
    @POST("registeruser.php/")
    Call<ModelRegisterPojo> registerUser(
            @Field("vsnama") String strnama,
            @Field("vsalamat") String stralamat,
            @Field("vsnotelp") String strnotelp,
            @Field("vsusername") String strusername,
            @Field("vspassword") String strpassword,
            @Field("vsjenkel") String strjenkel,
            @Field("vslevel") String strlevel
    );

    @FormUrlEncoded
    @POST("loginuser.php/")
    Call<ModelLogin> loginUser(
            @Field("edtusername") String strusername,
            @Field("edtpassword") String strpassword,
            @Field("vslevel") String strlevel
    );
////
//    @FormUrlEncoded
//    @POST("getdatamakanan.php/")
//    Call<ResponseDataMakanan>   getdatamakanan(
//            @Field("vsiduser") String striduser,
//            @Field("vsidkastrkategorimakanan") String strkartmakaan
//    );
////
//    @FormUrlEncoded
//    @POST("deletedatamakanan.php/")
//    Call<ResponseRegister> deletedata(
//            @Field("vsidmakanan") String stridmakanan
//    );
////
////
    @GET("kategorimakanan.php/")
    public Call<ResponseKategoriMakanan> getkategorimakanan();
//
////    @Multipart
////    @POST("uploadmakanan1.php")
////    Call<ServerResponse> uploadFile(@Part MultipartBody.Part file, @Part("image") RequestBody name);
////

}
