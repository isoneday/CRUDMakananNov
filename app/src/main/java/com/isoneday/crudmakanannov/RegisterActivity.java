package com.isoneday.crudmakanannov;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.isoneday.crudmakanannov.helper.MyFunction;
import com.isoneday.crudmakanannov.model.ModelRegisterPojo;
import com.isoneday.crudmakanannov.network.MyRetrofitClient;
import com.isoneday.crudmakanannov.network.RestApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends MyFunction {

    @BindView(R.id.edtnama)
    EditText edtnama;
    @BindView(R.id.edtalamat)
    EditText edtalamat;
    @BindView(R.id.edtnotelp)
    EditText edtnotelp;
    @BindView(R.id.spinjenkel)
    Spinner spinjenkel;
    @BindView(R.id.edtusername)
    EditText edtusername;
    @BindView(R.id.edtpassword)
    TextInputEditText edtpassword;
    @BindView(R.id.edtpasswordconfirm)
    TextInputEditText edtpasswordconfirm;
    @BindView(R.id.regAdmin)
    RadioButton regAdmin;
    @BindView(R.id.regUserbiasa)
    RadioButton regUserbiasa;
    @BindView(R.id.btnregister)
    Button btnregister;
    String strnama, stralamat, strnotelp, strjenkel, strusername, strpassword, strconpassword, strlevel;
    String jenkel[] = {"laki - laki", "perempuan"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setjenkel();

        setlevel();
    }

    private void setlevel() {
        if (regAdmin.isChecked()) {
            strlevel = "admin";
        } else {
            strlevel = "user biasa";
        }
    }

    private void setjenkel() {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, jenkel);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        //isi adapterke view
        spinjenkel.setAdapter(adapter);
        spinjenkel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strjenkel = jenkel[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @OnClick({R.id.regAdmin, R.id.regUserbiasa, R.id.btnregister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.regAdmin:
                strlevel = "admin";
                break;
            case R.id.regUserbiasa:
                strlevel = "user biasa";
                break;
            case R.id.btnregister:
                strnama = edtnama.getText().toString();
                stralamat = edtalamat.getText().toString();
                strnotelp = edtnotelp.getText().toString();
                strusername = edtusername.getText().toString();
                strpassword = edtpassword.getText().toString();
                strconpassword = edtpasswordconfirm.getText().toString();
                if (TextUtils.isEmpty(strnama)) {
                    edtnama.setError("nama tidak boleh kosong");
                    edtnama.requestFocus();
                    myanimation(edtnama);
                } else if (TextUtils.isEmpty(stralamat)) {
                    edtalamat.requestFocus();
                    edtalamat.setError("alamat tidak boleh kosong");
                    myanimation(edtalamat);
                } else if (TextUtils.isEmpty(strnotelp)) {
                    edtnotelp.requestFocus();
                    myanimation(edtnotelp);
                    edtnotelp.setError("no hp tidak boleh kosong");
                } else if (TextUtils.isEmpty(strusername)) {
                    edtusername.requestFocus();
                    myanimation(edtusername);
                    edtusername.setError("username tidak boleh kosong");
                } else if (TextUtils.isEmpty(strpassword)) {
                    edtpassword.requestFocus();
                    myanimation(edtpassword);
                    edtpassword.setError("password tidak boleh kosong");
                } else if (strpassword.length() < 6) {
                    myanimation(edtpassword);
                    edtpassword.setError("password minimal 6 karakter");
                } else if (TextUtils.isEmpty(strconpassword)) {
                    edtpasswordconfirm.requestFocus();
                    myanimation(edtpasswordconfirm);
                    edtpasswordconfirm.setError("password confirm tidak boleh kosong");
                } else if (!strpassword.equals(strconpassword)) {
                    edtpasswordconfirm.requestFocus();
                    myanimation(edtpasswordconfirm);
                    edtpasswordconfirm.setError("password tidak sama");
                } else {
                    registeruser();
                }
                break;
        }
    }

    private void registeruser() {
        ProgressDialog dialog = ProgressDialog.show(this, "proses register user", "loading . . . .");
        RestApi api = MyRetrofitClient.getInstaceRetrofit();
        Call<ModelRegisterPojo> registerPojoCall = api.registerUser(
                strnama,
                stralamat,
                strnotelp,
                strusername,
                strpassword,
                strjenkel,
                strlevel
        );
        //untuk cek response dari webser
        registerPojoCall.enqueue(new Callback<ModelRegisterPojo>() {
            @Override
            public void onResponse(Call<ModelRegisterPojo> call, Response<ModelRegisterPojo> response) {
                if (response.isSuccessful()) {
                    String res = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (res.equals("1")) {
                        myToast(msg);
                        myIntent(LoginActivity.class);
                        finish();
                    } else {
                        myToast(msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelRegisterPojo> call, Throwable t) {
                myToast("masalah jaringan" + t.getMessage());
            }
        });

    }
}
