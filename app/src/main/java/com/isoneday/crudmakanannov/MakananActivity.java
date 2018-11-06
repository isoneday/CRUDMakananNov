package com.isoneday.crudmakanannov;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.isoneday.crudmakanannov.helper.MyConstant;
import com.isoneday.crudmakanannov.helper.MyFunction;
import com.isoneday.crudmakanannov.helper.SessionManager;
import com.isoneday.crudmakanannov.model.DataKategoriItem;
import com.isoneday.crudmakanannov.model.ResponseKategoriMakanan;
import com.isoneday.crudmakanannov.network.MyRetrofitClient;
import com.isoneday.crudmakanannov.network.RestApi;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakananActivity extends MyFunction {

    @BindView(R.id.spincarimakanan)
    Spinner spincarimakanan;
    @BindView(R.id.listmakanan)
    RecyclerView listmakanan;
    @BindView(R.id.refreshlayout)
    SwipeRefreshLayout refreshlayout;
    private Dialog dialog;
    private TextInputEditText edtnamamakanan;
    private Button btnuploadmakanan;
    private ImageView imgpreview;
    private Button btninsert;
    private Button btnreset;
    private Spinner spinnercarikategori;
    private Uri filepath;
    private Bitmap bitmap;
    private List<DataKategoriItem> datakategori;
    private String strnamamakan;
    private String strkategorimakan;
    private SessionManager sessionManager;
    private String striduser;
    private String strtime;
    private String strpath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makanan);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestpermissions();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(MakananActivity.this);
                dialog.setContentView(R.layout.tambahmakanan);
                dialog.setTitle("data makanan");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                edtnamamakanan = (TextInputEditText) dialog.findViewById(R.id.edtnamamakanan);
                btnuploadmakanan = (Button) dialog.findViewById(R.id.btnuploadmakanan);
                imgpreview = (ImageView) dialog.findViewById(R.id.imgupload);
                btninsert = (Button) dialog.findViewById(R.id.btninsert);

                btnreset = (Button) dialog.findViewById(R.id.btnreset);
                spinnercarikategori = (Spinner) dialog.findViewById(R.id.spincarikategori);
                btnuploadmakanan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      showfilechooser(MyConstant.REQ_FILE_CHOOSE);
                    }
                });
                getdatakategorimakanan(spinnercarikategori);
                btninsert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        strnamamakan = edtnamamakanan.getText().toString();
                        if (TextUtils.isEmpty(strnamamakan)) {
                            edtnamamakanan.setError("nama makanan tidak boleh kosong");
                            edtnamamakanan.requestFocus();
                            myanimation(edtnamamakanan);
                        } else if (imgpreview.getDrawable() == null) {
                            myToast("gambar harus dipilih");
                        } else {
                            insertdatamakanan(strkategorimakan);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void insertdatamakanan(String strkategorimakan) {
//mengambil path dari gmbar yang d i upload
        sessionManager = new SessionManager(MakananActivity.this);
        try {

            strpath = getPath(filepath);
            striduser = sessionManager.getIdUser();
//            MaxSizeImage(strpath);

        } catch (Exception e) {
            myToast("gambar terlalu besar \n silahkan pilih gambar yang lebih kecil");
            e.printStackTrace();
        }
        /**
         * Sets the maximum time to wait in milliseconds between two upload attempts.
         * This is useful because every time an upload fails, the wait time gets multiplied by
         * {@link UploadService#BACKOFF_MULTIPLIER} and it's not convenient that the value grows
         * indefinitely.
         */
        strtime = currentDate();
        try {
            new MultipartUploadRequest(c, MyConstant.UPLOAD_URL)
                    .addFileToUpload(strpath, "image")
                    .addParameter("vsiduser", striduser)
                    .addParameter("vsnamamakanan", strnamamakan)
                    .addParameter("vstimeinsert", strtime)
                    .addParameter("vskategori", strkategorimakan)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();

         //   getDataMakanan(strkategori);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            myToast(e.getMessage());
        } catch (FileNotFoundException e) {
            myToast(e.getMessage());
            e.printStackTrace();
        }
    }

    private String getPath(Uri filepath) {
        Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void getdatakategorimakanan(final Spinner spinnercarikategori) {
        RestApi api = MyRetrofitClient.getInstaceRetrofit();
        Call<ResponseKategoriMakanan> makananCall = api.getkategorimakanan();
        makananCall.enqueue(new Callback<ResponseKategoriMakanan>() {
            @Override
            public void onResponse(Call<ResponseKategoriMakanan> call, Response<ResponseKategoriMakanan> response) {
                if (response.isSuccessful()){
                    datakategori =response.body().getDataKategori();
                    String [] idmakanan = new String[datakategori.size()];
                    String [] namakategori = new String[datakategori.size()];
                    for (int i =0 ; i<datakategori.size();i++){
                        idmakanan[i]=datakategori.get(i).getIdKategori();
                        namakategori[i]=datakategori.get(i).getNamaKategori();
                    }
                    ArrayAdapter adapter = new ArrayAdapter(MakananActivity.this, android.R.layout.simple_spinner_item, namakategori);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    //isi adapterke view
                    spinnercarikategori.setAdapter(adapter);
                    spinnercarikategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            strkategorimakan =adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseKategoriMakanan> call, Throwable t) {

            }
        });

    }

    private void requestpermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        107);
            }
            return;
        }
    }

    private void showfilechooser(int reqFileChoose) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"pilih gambar"),reqFileChoose);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==MyConstant.REQ_FILE_CHOOSE&&resultCode==RESULT_OK){
            filepath =data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                imgpreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
