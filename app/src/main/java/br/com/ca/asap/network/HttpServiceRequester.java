package br.com.ca.asap.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.ca.asap.vo.MessageVo;

/**
 * HttpServiceRequester
 *
 * Implements a method that do an http request and returns a String response.
 *
 * This class is used for rest requests
 *
 * @author Rodrigo Carvalho
 */
public class HttpServiceRequester {

    Context context;

    /**
     * HttpServiceRequester
     *
     * Constructor receives app context object
     *
     * @param context
     */
    public HttpServiceRequester(Context context){
        this.context = context;
    }

    /**
     * executeHttpGetRequest
     *
     * Receives an URL and executes an http request returning a String
     *
     * @param urlString
     * @return
     */
    public String executeHttpGetRequest(String urlString) throws DeviceNotConnectedException {

        String returnString;

        List<MessageVo> messageVoList;
        MessageVo messageVo;

        Gson gson = new Gson();

        messageVoList = new ArrayList<>();

        messageVo = new MessageVo();
        messageVo.setText("Rodrigo");
        messageVoList.add(messageVo);
        messageVo = new MessageVo();
        messageVo.setText("Carvalho");
        messageVoList.add(messageVo);
        messageVo = new MessageVo();
        messageVo.setText("dos Santos");
        messageVoList.add(messageVo);

        //serialize generic type for List of MessageVo
        Type messagesListType = new TypeToken<List<MessageVo>>() {}.getType(); //this is necessary because we are deserializing a generic class type

        returnString = gson.toJson(messageVoList, messagesListType);

        return returnString;

        /*
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        StringBuffer stringBuffer = null;
        String returnString = null;

        //class that can check if the device has a valid internet connection
        CheckInternetConnection checkInternetConnection = new CheckInternetConnection(context);

        // if the device is not connected to Internet trow an custom exception
        if (!checkInternetConnection.deviceIsConnected()) {
            throw new DeviceNotConnectedException();
        }

        Log.d("HttpServiceRequester", "ok, device has an network connection");

        try {

            //String urlString = "http://192.168.0.8:8080/AsapServer/sendMessage?msg=" + URLEncoder.encode(msg, "UTF-8");

            //URL encoded text
            URL url = new URL(urlString);

            //open connection
            conn = (HttpURLConnection) url.openConnection();

            //prepare request parameters
            conn.setReadTimeout(50000);// milliseconds
            conn.setConnectTimeout(50000);// milliseconds
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("GET");

            //starts the http request
            Log.d("HttpServiceRequester", "trying to connect using created HttpURLConnection");
            int responseCode = conn.getResponseCode();
            Log.d("HttpServiceRequester", "The response code is: " + responseCode);

            //read input stream
            Log.d("HttpServiceRequester", "trying to get input stream using conn.getInputStream");
            inputStream = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            stringBuffer = new StringBuffer();
            // Convert the InputStream into a String
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            returnString = stringBuffer.toString();
            Log.d("HttpServiceRequester", "read from http connection: " + returnString);

        } catch (Exception e) {

        } finally {
            // makes sure that the InputStream is closed after the app is
            // finished using it.
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (java.io.IOException e) {
                Log.d("HttpServiceRequester", e.getMessage());
            }
        }

        return returnString;
        */

    }
}