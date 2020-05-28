package com.ilya.infovk;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.ilya.infovk.utils.NetworkUtils.generateURL;
import static com.ilya.infovk.utils.NetworkUtils.getResponseFromUrl;

public class MainActivity extends AppCompatActivity {
    private EditText searchField;
    private Button searchButton;
    private TextView result;
    private TextView errorMessage;
    private ProgressBar loadingEdicator;

    // показать результат result - видимый, errorMessage - невидимый
    private void showResultTextView(){
        result.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }
    // показать результат result - невидимый, errorMessage - видимый
    private void showErrorTextView(){
        result.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    // многопоточность. Вложенный класс
    class VKQuery extends AsyncTask<URL, Void, String> {

        // показываем загрузку
        @Override
        protected void onPreExecute(){
            loadingEdicator.setVisibility(View.VISIBLE);
        }

        // переопределяем метод doInBackground
        @Override
        protected String doInBackground(URL... urls) { // метод ожидает массив объекта URL
            String response = null;
            try {
                response = getResponseFromUrl(urls[0]); // generateURL - 1ый url в массиве. передаем в метод getResponseFromUrl
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response; // получаем json строку запроса
        }

        @Override
        protected void onPostExecute(String response) {  // принимает на вход response от doInBackground
            String firstName = null;
            String lastName = null;
            int id;

            if (response != null && !response.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(response); // парсим json строку
                    JSONArray jsonArray = jsonResponse.getJSONArray("response"); // достаем массим по ключу
                    String resultString = "";

                    if(jsonArray.length() != 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject userInfo = jsonArray.getJSONObject(i); // достаем значения
                            firstName = userInfo.getString("first_name");
                            lastName = userInfo.getString("last_name");
                            id = userInfo.getInt("id");

                            if (userInfo != null) {
                                if (firstName.equals("DELETED")) {
                                    resultString += "id: " + id + "\n" + "Контакт удален!" + "\n\n";

                                } else {
                                    resultString += "id: " + id + "\n" + "Имя: " + firstName + "\n" + "Фамилия: " + lastName + "\n\n";

                                }
                            }
                        }
                    }

                    else {
                        resultString += "Введите корректно данные!";
                    }
                    result.setText(resultString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showResultTextView();
            }
            else {
                showErrorTextView();
            }
            loadingEdicator.setVisibility(View.INVISIBLE);
        }
    }

    // основной поток
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchField = findViewById(R.id.et_search_field);
        searchButton = findViewById(R.id.b_search_vk);
        result = findViewById(R.id.tv_result);
        errorMessage = findViewById(R.id.tv_error_message);
        loadingEdicator = findViewById(R.id.pd_loading_indicator);

        // через анонимный класс, переопред. метод.
        View.OnClickListener onClickListener = new View.OnClickListener() { // вложенный интерфейс
            @Override
            public void onClick(View v) {
                URL generateURL = generateURL(searchField.getText().toString()); // generateURL хранит в себе url networkutils

                new VKQuery().execute(generateURL); // Метод execute() вызывается в основном потоке

                // result.setText(generateURL.toString());

            }
        };
        searchButton.setOnClickListener(onClickListener);
    }
}





// Метод execute() вызывается в основном потоке, чтобы начать выполнение задачи.
// В него можно передать набор данных определенного типа.
// Если что-то передаём, то этот тип будет указан первым в угловых скобках при создании наследника AsyncTask.