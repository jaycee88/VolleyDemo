package com.jaycee88.volleydemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // 注意这里拿到的RequestQueue是一个请求队列对象，它可以缓存所有的HTTP请求，然后按照一定的算法并发地发出这些请求。
    // RequestQueue内部的设计就是非常合适高并发的，因此我们不必为每一次HTTP请求都创建一个RequestQueue对象，
    // 这是非常浪费资源的，基本上在每一个需要和网络交互的Activity中创建一个RequestQueue对象就足够了。
    RequestQueue mQueue;
    ImageLoader mImageLoader;
    ImageLoader.ImageListener mImageListener;

    Button mButton;
    TextView mText;
    ImageView mImage;
    NetworkImageView mNetworkImage;

    private static final String URL_BAI_DU = "https://www.baidu.com/";
    private static final String URL_JSON_OBJECT = "https://www.metaweather.com/api/location/search/?query=london";
    private static final String URL_JSON_ARRAY = "https://www.metaweather.com/api/location/search/?query=san";
    private static final String URL_XML = "http://flash.weather.com.cn/wmaps/xml/china.xml";
    private static final String URL_GSON = "http://www.weather.com.cn/data/sk/101010100.html";
    private static final String URL_IMAGE = "http://qnimage.dancebook.com.cn/f720786f444d0973159a9ffca1d2d2c2.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = Volley.newRequestQueue(this);

        mButton = (Button) findViewById(R.id.button);
        mText = (TextView) findViewById(R.id.text);
        mImage = (ImageView) findViewById(R.id.image);
        mNetworkImage = (NetworkImageView) findViewById(R.id.network_image);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue.add(getGsonRequest());
//                mImageLoader.get(URL_IMAGE, mImageListener, 800, 500);
//                initNetworkImage();
            }
        });

        initImageLoader();
    }

    private void initImageLoader() {
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        mImageListener = ImageLoader.getImageListener(mImage, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
    }

    private void initNetworkImage() {
        mNetworkImage.setDefaultImageResId(R.mipmap.ic_launcher);
        mNetworkImage.setErrorImageResId(R.mipmap.ic_launcher);
        mNetworkImage.setImageUrl(URL_IMAGE, mImageLoader);
    }

    private ImageRequest getImageRequest() {
        return new ImageRequest(URL_IMAGE, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mImage.setImageBitmap(response);
            }
        }, 800, 500, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private XMLRequest getXMLRequest() {
        return new XMLRequest(URL_XML, new Response.Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser response) {
                try {
                    int eventType = response.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                String nodename = response.getName();
                                if ("city".equals(nodename)) {
                                    String pName = response.getAttributeValue(0);
                                    Log.d("TAG", "pName is " + pName);
                                }
                                break;
                        }
                        eventType = response.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
    }

    private GsonRequest<Weather> getGsonRequest() {
        return new GsonRequest<>(URL_GSON, Weather.class, new Response.Listener<Weather>() {
            @Override
            public void onResponse(Weather response) {
                WeatherInfo weatherInfo = response.getWeatherinfo();
                Log.d("TAG", "city is " + weatherInfo.getCity());
                Log.d("TAG", "temp is " + weatherInfo.getTemp());
                Log.d("TAG", "time is " + weatherInfo.getTime());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
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
        return new JsonArrayRequest(URL_JSON_ARRAY, new Response.Listener<JSONArray>() {
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
