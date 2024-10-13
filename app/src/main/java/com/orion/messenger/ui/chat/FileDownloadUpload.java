package com.orion.messenger.ui.chat;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public interface FileDownloadUpload {

    @Multipart
    @POST("/save-avatar")
    Call<Map<String, String>> sendFile(@PartMap Map<String, RequestBody> params,
                                                         @Part MultipartBody.Part file);
}