package com.example.all_in_won;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class ExchangeRatesService extends Service {
    HandlerThread exchangeRateThread = null;
    Handler exchangeRatesHandler = null;
    final int GET_EXCHANGE_RATE = 20160548;
    LocalBinder localBinder = new LocalBinder();

    private double reqCurrency_exchangeRate;
    private int exchangedAmount;
    boolean isConnected = false;

    public class LocalBinder extends Binder {
        ExchangeRatesService getExchangeRatesService(){return ExchangeRatesService.this;}
    }

    class GetExchangeRateParamsObj{ // 작업 스레드로 작업을 넘길 때 전달인자로 사용하는 객체
        public String currency;
        public Calendar date;
        public TextView currencyExchangeTextView;
        public double amount;

        GetExchangeRateParamsObj(double amount, String currency, Calendar date, TextView currencyExchangeTextView){
            this.currency = currency;
            this.date = date;
            this.currencyExchangeTextView = currencyExchangeTextView;
            this.amount = amount;
        }
    }

    public void getExchangeRate(final double amount, final String currency, final Calendar date, TextView currencyExchangeTextView){

        if(currency.equals("KRW")){ // 선택한 통화가 원화이면 환율을 가져올 필요가 없다
            reqCurrency_exchangeRate = 1;
            setExchangedAmount(amount);
            setTextAtCurrencyExchangeTextView(currencyExchangeTextView);
        } else{
            // 환율 정보를 받아온다
            // 이 작업은 네트워크를 사용하기 때문에 작업 스레드에서 진행한다
            exchangeRatesHandler.sendMessage(Message.obtain(exchangeRatesHandler, GET_EXCHANGE_RATE,new GetExchangeRateParamsObj(amount, currency, date, currencyExchangeTextView)));
        }

        Log.i("req_currency", reqCurrency_exchangeRate + "");
        return;
    }

    private void setTextAtCurrencyExchangeTextView(TextView currencyExchangeTextView){
        currencyExchangeTextView.setText(exchangedAmount + "");
    }

    private void setReqCurrencyExchangeRate(Double EUR_ReqCurrency_exchangeRate){
        this.reqCurrency_exchangeRate = EUR_ReqCurrency_exchangeRate;
    }

    private void setExchangedAmount(double amount){
        int tmpExchangedAmount = (int)(reqCurrency_exchangeRate * amount);

        // 일의자리 반올림
        if(tmpExchangedAmount % 10 < 5) exchangedAmount = (tmpExchangedAmount / 10) * 10;
        else exchangedAmount = ((tmpExchangedAmount + 10) / 10) * 10;
    }

    // 외부 api를 이용해 환율 정보를 가져온다
    private double getExchangeRateUsingAPI(final String currency, final Calendar requestedDate){
        double requestedExchangeRate = 1.0;

        // 현재 날짜를 문자열에 저장
        Calendar c = Calendar.getInstance(); // java.util.Calendar
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH));
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String today = year + month + day;
        int nToday = Integer.parseInt(today); // 입력된 날짜와의 날짜 비교를 위해 정수형으로 변환

        // 입력된 날짜를 문자열에 저장
        String inputYear = requestedDate.get(Calendar.YEAR) + "";
        String inputMonth = (requestedDate.get(Calendar.MONTH) + 1) + "";
        String inputDay = requestedDate.get(Calendar.DAY_OF_MONTH) + "";
        String inputDate = inputYear + inputMonth + inputDay;
        int nInputDate = Integer.parseInt(inputDate); // 현재 날짜와의 날짜 비교를 위해 정수형으로 변환

        // Create URL
        String date = "latest";
        String apiURL = "https://api.exchangeratesapi.io/";

        // 입력된 날짜가 미래의 날짜이면 해당 날짜의 환율 정보가 없으므로 오늘 날짜로 정보를 받아온다
        if(nInputDate <= nToday && nInputDate >= 19990104){ // api에서 1999년 1월 4일 이후의 환율부터 제공해줌
            date = inputYear + "-" + inputMonth + "-" + inputDay;
        }
        // 선택된 통화를 baseCurrency로 설정한다
        String baseCurrency = "?base=" + currency;

        try{
            URL githubEndpoint = new URL(apiURL + date + baseCurrency);
            // Create connection
            HttpsURLConnection myConnection = (HttpsURLConnection) githubEndpoint.openConnection();
            myConnection.setRequestProperty("User-Agent", "test-v0.1");

            int httpsResponseCode = myConnection.getResponseCode();
            Log.w("httpsResponseCode", httpsResponseCode + "");

            if (httpsResponseCode == 200) {
                isConnected = true;
                Log.w("connection", "ok");
                // Success
                // Further processing here
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key
                    if (key.equals("rates")) { // Check if desired key
                        // Do something with the value
                        // ...
                        jsonReader.beginObject();
                        while(jsonReader.hasNext()){
                            String NextCurrency = jsonReader.nextName();
                            Log.w("json_loop", NextCurrency);
                            if(NextCurrency.equals("KRW")){
                                requestedExchangeRate = jsonReader.nextDouble();
                                Log.w("json_loop", "req");
                            }else{
                                jsonReader.skipValue();
                                Log.w("json_loop", "skip_value");
                            }
                        }
                        jsonReader.endObject();

                        break; // Break out of the loop
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }

                jsonReader.close();
                myConnection.disconnect();

            } else {
                // Error handling code goes here
                isConnected = false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return requestedExchangeRate;
    }

    public ExchangeRatesService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(exchangeRateThread == null){
            exchangeRateThread = new HandlerThread("ExchangeRateThread");
            exchangeRateThread.start();
            exchangeRatesHandler = new Handler(exchangeRateThread.getLooper()){
                @Override
                public void handleMessage(Message msg){
                    switch (msg.what){
                        case GET_EXCHANGE_RATE:
                            final GetExchangeRateParamsObj paramsObj = (GetExchangeRateParamsObj) msg.obj;
                            // 환율 정보를 받아온다
                            setReqCurrencyExchangeRate(getExchangeRateUsingAPI(paramsObj.currency, paramsObj.date));
                            if(isConnected) {
                                setExchangedAmount(paramsObj.amount);
                                paramsObj.currencyExchangeTextView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 환전된 금액 텍스트뷰를 갱신한다
                                        setTextAtCurrencyExchangeTextView(paramsObj.currencyExchangeTextView);
                                    }
                                });
                            } else{
                                //
                            }
                            break;
                    }
                }
            };
        }

        return localBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        if(exchangeRateThread != null){
            exchangeRateThread.interrupt();
            exchangeRateThread = null;
            exchangeRatesHandler = null;
        }

        return super.onUnbind(intent);
    }
}
