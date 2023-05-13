package com.arjundroid.upipayment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arjundroid.upipayment.databinding.ActivityMainBinding;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchApi();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paymentIntentClientSecret!=null)
                paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret,new PaymentSheet.Configuration("DoctorDesc",configuration));
                else
                    Toast.makeText(MainActivity.this, "Loading....", Toast.LENGTH_SHORT).show();
            }
        });

        paymentSheet=new PaymentSheet(this,this::onPaymentSheetResult);
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult)
    {
        if(paymentSheetResult instanceof PaymentSheetResult.Canceled){
            Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Failed){
            Toast.makeText(this, ((PaymentSheetResult.Failed)paymentSheetResult).getError().getMessage(), Toast.LENGTH_SHORT).show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
        }
    }

    public void fetchApi(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://demo.codeseasy.com/apis/stripe/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            configuration=new PaymentSheet.CustomerConfiguration(
                                    jsonObject.getString("customer"),
                                    jsonObject.getString("ephemeralKey")
                            );
                            paymentIntentClientSecret =jsonObject.getString("paymentIntent");
                            PaymentConfiguration.init(getApplicationContext(),jsonObject.getString("publishableKey"));
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Done bhai", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                Toast.makeText(MainActivity.this, "Already done", Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams(){
                Map<String, String> paramV = new HashMap<>();
                paramV.put("authKey", "abc");
                return paramV;
            }
        };
        queue.add(stringRequest);

    }
}