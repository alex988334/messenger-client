package com.orion.messenger.ui.chat;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.messenger.MainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.MediaType;


//  класс является оберткой нижнего уровня для работы с WS сервером
public class WSThread extends Thread {

    public static final String RESPONSE_KEY_STATUS = "status";
    public static final String RESPONSE_KEY_FILES = "files";
    public static final String RESPONSE_KEY_FILE_NAME = "file_name";
    public static final String RESPONSE_KEY_OPERATION = "operation";

    public static final int RESPONSE_OPERATION_SAVE = 1;
    public static final int RESPONSE_OPERATION_GET = 2;

    public static WSThread wsThread;

    private IBufferWS dataBuf;
    private MyWebSocket server = null;

    private Runnable callbackOnOpenConnection;
    private Runnable callbackOnFileUpload;
    boolean stop;

    public WSThread(IBufferWS data, Runnable callbackOnOpenConnection){
        super();
        this.dataBuf = data;
        WSThread.wsThread = this;
        this.callbackOnOpenConnection = callbackOnOpenConnection;
    }

    public boolean isConnected(){
        return server.isConnected();
    }

    public void StopWS() {
        this.stop = true;
    }

    @Override
    public void run() {
        super.run();

        int interation = 0;
        stop = false;
        do {
            try {
                if (server == null || !server.isConnected()){
                    createWSServer();
                    runWSServer();
                    Log.e(ConstWSThread.LOG_TAG, "WS BLOCK server == null || !server.isConnected()");
                }

                sleep(2000);
            } catch (InterruptedException e) {
                Log.e(ConstWSThread.LOG_TAG, "WSThread don't stopped!");
            } catch (URISyntaxException e) {
                Log.e(ConstWSThread.LOG_TAG, "BAD URL ADDRESS, WORK STOPED!");
                break;
            }

            interation++;
        //   Log.e(ConstWSThread.LOG_TAG,"INteration WSThread => " + interation);
        }while (!stop);
    }

    public void sendMessage(Map<String, String> data) {

        if (this.server != null && !this.server.isConnected()) {
            return;
        }

        String msg = new Gson().toJson(data);
        server.send(msg);
        String st = data.get("Status");
        Type type = new TypeToken<StatusRequest>(){}.getType();
        StatusRequest s = new Gson().fromJson(st, type);

        Log.d(ConstWSThread.LOG_TAG, "Sended data "
                + StatusRequest.getDescriptionOperation(s.Operation) +" -> " + msg);
    }

    public void onConnectedServer() {

        if (callbackOnOpenConnection != null) {
            Log.d(ConstWSThread.LOG_TAG, "WSThread onConnectedServer()!");
            callbackOnOpenConnection.run();
        }
    }

    private void createWSServer() throws URISyntaxException {
        server = new MyWebSocket(
                new URI(Config.WSS  + Config.DOMAIN  + ":" + Config.WS_PORT + "/" + Config.CHAT),
                this);
    }

    private void runWSServer(){
        server.connect();

        File f = dataBuf.getSendFile();
        if (f == null) {
            return;
        }
        if (f != null || !f.exists()) {
            Log.e("", "ERROR WSThread! Not found file => " + f.getName()
                    + "; path => " + f.getAbsolutePath());
            return;
        }

       // Request r = createSendFileRequestBody(f);
      //  saveAvatar(r.mapParams, r.body);
    }



    private void saveAvatar(File file) {

     /*   Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.HTTPS + Config.DOMAIN + "/" + Config.AVATAR)
                .addConverterFactory(GsonConverterFactory.create())
                //                .client(okClient.build())
                .build();

     //   RequestBody size = RequestBody.create(String.valueOf(file.length()), MediaType.parse("text/plain"));
        RequestBody contentType = RequestBody.create("application/pdf", MediaType.parse("text/plain"));

        // Create a multipart request body part for the file
        MultipartBody.Pa filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("application/pdf"), file));
        // Make the API call
        FileDownloadUpload getResponse = retrofit.create(FileDownloadUpload.class);

        Call<Map<String, String>> call = getResponse.sendFile(filePart, contentType);

// Asynchronously execute the API call
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                Log.e("Response", "response --: " + response.body());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.i("onEmptyResponse", "Returned empty response");
                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (Exception e ) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("failure", "message = " + t.getMessage());
                Log.d("failure", "cause = " + t.getCause());
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();


            }
        });

       /* WSThread t = this;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.HTTPS + Config.DOMAIN + "/" + Config.AVATAR)
                .addConverterFactory(GsonConverterFactory.create())
                //                .client(okClient.build())
                .build();

        final FileDownloadUpload client = retrofit.create(FileDownloadUpload.class);

        client.sendFile(map, body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call,
                                   Response<Map<String, String>> response) {

                if (response.code() == 200 && response.body() != null) {
                    if (Integer.parseInt(response.body().get(RESPONSE_KEY_STATUS)
                            .get(0).get(RESPONSE_KEY_STATUS)) == 1) {

                        Map<String, String> map = response.body().get(RESPONSE_KEY_FILES).get(0);
                        String fileName = map.get(RESPONSE_KEY_FILE_NAME);
                        Log.d("GRADINAS", "Загрузка -> успех, file_name => " + fileName);

                        t.callbackOnFileUpload.run();
                    } else {
                        Log.d("GRADINAS", "200 MESSAGE => " + response.body().toString());
                    }
                } else {
                    Log.d("GRADINAS", "!=null MESSAGE => " + ", ERROR => " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.d("GRADINAS", "EXCEPTION24242421244 => " + t.toString());
            }
        });*/
    }

    class Request {

        public LinkedHashMap<String, okhttp3.RequestBody> mapParams;
        public MultipartBody.Part body;

        public Request(LinkedHashMap<String, okhttp3.RequestBody> mapParams, MultipartBody.Part body){
            this.mapParams = mapParams;
            this.body = body;
        }
    }

    private Request createSendFileRequestBody(File file){

        RequestBody rb = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData(RESPONSE_KEY_FILE_NAME, file.getName(), rb);

        LinkedHashMap<String, RequestBody> mp = new LinkedHashMap<>();
        rb = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(RESPONSE_OPERATION_SAVE));
        mp.put(RESPONSE_KEY_OPERATION, rb);
        Log.d("", "SEND AVATAR map => " + mp.toString());

        return new Request(mp, body);
    }

    public void newResponse(ResponseServer responseServer) {
        dataBuf.addResponseData(responseServer);
    }

    public void setCallbackOnOpenConnection(Runnable runnable){
        this.callbackOnOpenConnection = runnable;
    }
}


