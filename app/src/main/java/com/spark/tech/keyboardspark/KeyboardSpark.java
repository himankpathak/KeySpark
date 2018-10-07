package com.spark.tech.keyboardspark;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static java.net.Proxy.Type.HTTP;


public class KeyboardSpark extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private boolean caps = false;

    RequestQueue queue;
    String url ="http://10.0.2.2:5000/"; //10.0.2.2:5000 - android localhost
    int count=0;
    String charcount="";
    String resultstring;
    String globalglobal="";

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.keys_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);

        if(queue == null)
            queue = Volley.newRequestQueue(this);
        return keyboardView;

    }


    public void detectAP(final String word){

        Log.e("enter",word);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"detect/"+word,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("kya",response);
                        translateAP(response, word);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("That didn't work!","hell");
            }
        });
            if(queue == null)
                Log.e("error","hai");
            queue.add(stringRequest);
    }
    public void translateAP(String n,String word){

        Log.e("enter",n);

        if (n.contains("en")){
            Log.e("kya","angrezi hai");
            globalglobal=word;

        } else{
            Log.e("kya","hindi hai");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"translate/"+word+"/en/hi",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            inputConnection.commitText(resultstring, 1);
                            Log.e("kya",response);
                            globalglobal=response;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("That didn't work!","hell");
                }
            });
            queue.add(stringRequest);
        }
    }


    @Override
    public void onPress(int i) {

        count++;
        charcount+=String.valueOf((char)i);
        InputConnection inputConnection = getCurrentInputConnection();
        String popul= String.valueOf((char)i);
        Log.e(Integer.toString(i),popul);
        switch(i) {
            case 32:

                detectAP(charcount);
                charcount="";

        }
        if(globalglobal!="") {
            inputConnection.deleteSurroundingText(count, 1);
            count=0;
            inputConnection.commitText(" "+globalglobal+" ", 1);
            globalglobal="";
        }

    }

    @Override
    public void onRelease(int i) {

    }


    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            switch(primaryCode) {

                case Keyboard.KEYCODE_DELETE :
                    CharSequence selectedText = inputConnection.getSelectedText(0);

                    if (TextUtils.isEmpty(selectedText)) {
                        try {
                            charcount = charcount.substring(0, charcount.length() - 2);
                        }catch (IndexOutOfBoundsException e){}
                        inputConnection.deleteSurroundingText(1, 0);
                        Log.e("new","first");

                    } else {
                        inputConnection.commitText("", 1);
                    }
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    caps = !caps;
                    keyboard.setShifted(caps);
                    keyboardView.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DONE:
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

                    break;
                default :
                    char code = (char) primaryCode;
                    if(Character.isLetter(code) && caps){
                        code = Character.toUpperCase(code);
                    }
                    inputConnection.commitText(String.valueOf(code), 1);

            }
        }

    }


    @Override
    public void onText(CharSequence charSequence) {
        Log.e("himank",charSequence.toString());

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
