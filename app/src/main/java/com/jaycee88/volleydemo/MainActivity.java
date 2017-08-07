package com.jaycee88.volleydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // 注意这里拿到的RequestQueue是一个请求队列对象，它可以缓存所有的HTTP请求，然后按照一定的算法并发地发出这些请求。
    // RequestQueue内部的设计就是非常合适高并发的，因此我们不必为每一次HTTP请求都创建一个RequestQueue对象，
    // 这是非常浪费资源的，基本上在每一个需要和网络交互的Activity中创建一个RequestQueue对象就足够了。
    RequestQueue mQueue;

    Button mButton;
    TextView mText;

    private static final String URL_BAI_DU = "https://www.baidu.com/";
    private static final String URL_JSON_OBJECT = "https://www.metaweather.com/api/location/search/?query=london";
    private static final String URL_JSON_ARRAY = "https://www.metaweather.com/api/location/search/?query=san";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);

        mButton = (Button) findViewById(R.id.button);
        mText = (TextView) findViewById(R.id.text);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue.add(getJsonArrayRequest());
            }
        });
    }

    private StringRequest getGetRequest() {
        return new StringRequest(URL_BAI_DU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mText.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mText.setText(error.getMessage());
            }
        });
    }

    private StringRequest getPostRequest() {
        return new StringRequest(Request.Method.POST, URL_BAI_DU, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mText.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mText.setText(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("params1", "value1");
                map.put("params2", "value2");
                return map;
            }
        };
    }

    private JsonObjectRequest getJsonObjectRequest() {
        return new JsonObjectRequest(URL_JSON_OBJECT, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mText.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mText.setText(error.getMessage());
            }
        });
    }

    private JsonArrayRequest getJsonArrayRequest() {
        return new JsonArrayRequest(URL_JSON_OBJECT, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                mText.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mText.setText(error.getMessage());
            }
        });
    }
}
