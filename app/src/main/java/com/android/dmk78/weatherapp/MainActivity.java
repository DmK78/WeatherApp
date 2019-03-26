package com.android.dmk78.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private EditText editTextName;
    private TextView textViewName;
    private TextView textViewTemp;
    private TextView textViewWeather;

    private String url = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String key = "&appid=8f99535cdea446be868e707ba8062fc0&lang=ru&units=metric";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextName = findViewById(R.id.editText);
        textViewName = findViewById(R.id.textViewName);
        textViewTemp = findViewById(R.id.textViewTemp);
        textViewWeather = findViewById(R.id.textViewWeather);


    }

    public void toKnowWeather(View view) {

        String city = editTextName.getText().toString();
        if (!city.isEmpty()) {

            DownloadJSONTask task = new DownloadJSONTask();
            try {

                String resultUrl = task.execute(url + city + key).get();

                Log.i("myURL", resultUrl);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } else Toast.makeText(this, "Введите название города или индекс", Toast.LENGTH_SHORT).show();


    }


    public class DownloadJSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder resultUrl = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    resultUrl.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return resultUrl.toString();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                JSONObject jsonObject = new JSONObject(s);


                textViewName.setText(jsonObject.getString("name"));
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject weather = jsonArray.getJSONObject(0);
                String description = weather.getString("description");
                textViewWeather.setText("На улице: " + description);
                JSONObject main = jsonObject.getJSONObject("main");
                String temp = main.getString("temp");
                textViewTemp.setText("Температура: " + temp + " C");

                // Log.i("myURL", main + ", " + description);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}