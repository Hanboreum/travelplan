package com.example.all_in_won;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    public AlarmService() {
    }

    @Override
    public void onCreate(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = new SettingSharedPreferences(this).getSettingSharedPreferences();
        boolean isAlarmON = sharedPreferences.getBoolean("isAlarmON", false);
        String title = sharedPreferences.getString("alarmAccountBookTitle", null);

        Log.w("AlarmService", "AlarmService----------------------------------------------------------------");
        //showToast("AlarmService");

        if(isAlarmON && title != null) {

            String alarmMessage = getAlarmMessage(title);


            //Toast.makeText(this, "Alarm Test", Toast.LENGTH_SHORT).show();

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent activityIntent = new Intent(); // 알림창 클릭시 호출할 액티비티
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ComponentName componentName = new ComponentName(
                    "com.example.all_in_won",
                    "com.example.all_in_won.MainActivity"
            );
            activityIntent.setComponent(componentName);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//OREO API 26 이상에서

                builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


                String channelName = "All in WON 가계부 알림";
                String description = "매일 정해진 시간에 가계부 내용 알림.";
                int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

                NotificationChannel channel = new NotificationChannel("default", channelName, importance);
                channel.setDescription(description);

                if (notificationManager != null) {
                    // 노티피케이션 채널을 시스템에 등록
                    notificationManager.createNotificationChannel(channel);
                }
            } else {
                builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
            }
            builder.setAutoCancel(true);
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
            builder.setWhen(System.currentTimeMillis());

            builder.setTicker("{All in WON 가계부 알림}");
            builder.setContentTitle(title);
            builder.setContentText(alarmMessage);
            builder.setContentInfo("매일 정해진 시간에 가계부 내용 알림.");
            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();
            if (notificationManager != null) {
                // 노티피케이션 동작시킴
                notificationManager.notify(20160548, notification);
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String getAlarmMessage(String title){ // 알림에 쓸 메세지 생성
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String sql = null;
        try{
            DBHelper helper = new DBHelper(this);
            db = helper.getReadableDatabase();
            sql = "select amount from tb_accountBook where isBudget = 1 and tag = 'title' and title = \'" + title + "\' order by _id asc";
            cursor = db.rawQuery(sql, null);
        } catch (Exception e){
            e.printStackTrace();//에러메시지 토스트로 출력
            return null;
        }

        // 지출, 잔액 보여줘야함. 총 예산과 지출 총합 필요
        int balance = 0; // 잔액
        int totalExchangedAmount = 0; // 지출 총액
        int totalBudget = 0;

        cursor.moveToNext();
        try{
            totalExchangedAmount = cursor.getInt(0);
            Budgets budgets = new Budgets(this, title);
            totalBudget = budgets.getTotalBudget();
            balance = totalBudget - totalExchangedAmount;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        cursor.close();
        db.close();


        return "예산 " + totalBudget  + "원 중 "+ totalExchangedAmount +"원 사용. " + balance + "원 남았습니다.";
    }


    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
