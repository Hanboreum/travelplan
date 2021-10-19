package com.example.all_in_won;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ExcelService extends Service {
//    private Thread saveExcelThread = null;

    public ExcelService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String title = intent.getStringExtra("title");

        saveExcel(title);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void saveExcel(String title){

        int rowIndex = 0;
        String date = null;
        String whereToUse = null;
        String detail = null;
        String tag = null;
        int exchangedAmount = 0;

        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select date, whereToUse, detail, exchangedAmount, tag from tb_accountBook where isBudget = 0 and title  = \'" + title + "\' order by date asc";
        Cursor cursor = db.rawQuery(sql, null);

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row row = sheet.createRow(rowIndex);
        Cell cell;

        //날짜 열 추가
        cell = row.createCell(0);
        cell.setCellValue("날짜");

        //사용처 열 추가
        cell = row.createCell(1);
        cell.setCellValue("사용처");

        //사용내역 열 추가
        cell = row.createCell(2);
        cell.setCellValue("사용내역");

        //금액 열 추가
        cell = row.createCell(3);
        cell.setCellValue("금액");

        //분류 열 추가
        cell = row.createCell(4);
        cell.setCellValue("분류");

        while(cursor.moveToNext()){
            date = cursor.getString(0);
            whereToUse = cursor.getString(1);
            detail = cursor.getString(2);
            exchangedAmount = cursor.getInt(3);
            tag = cursor.getString(4);

            CellStyle style = workbook.createCellStyle();
            CreationHelper creationHelper = workbook.getCreationHelper();
            style.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd"));

            // 새로운 행 추가
            row = sheet.createRow(++rowIndex);
            cell = row.createCell(0); // 날짜
            cell.setCellValue(stringToCalendar(date));
            cell.setCellStyle(style);
            cell = row.createCell(1); // 사용처
            cell.setCellValue(whereToUse);
            cell = row.createCell(2); // 사용내역
            cell.setCellValue(detail);
            cell = row.createCell(3); // 현금
            cell.setCellValue(exchangedAmount);
            cell = row.createCell(4); // 분류
            cell.setCellValue(tag);
        }

        File excelFile = new File(getExternalFilesDir(null),"All_in_WON_" + title + ".xls");
        try{
            FileOutputStream os = new FileOutputStream(excelFile);
            workbook.write(os);
        } catch(IOException e){
            e.printStackTrace();
        }

        final String filePath = excelFile.getAbsolutePath();
        Toast.makeText(getApplicationContext(),filePath +"에 저장되었습니다",Toast.LENGTH_LONG).show();
    }

    private Calendar stringToCalendar(String date){ // 문자열에 저장된 날짜를 Calendar 객체로 옮겨주는 함수
        int year, month, day;
        Calendar calendar = Calendar.getInstance();

        year = Integer.parseInt(date.substring(0,4));
        month = Integer.parseInt(date.substring(5,7));
        day = Integer.parseInt(date.substring(8,10));

        calendar.set(year, month - 1, day, 0, 0, 0);

        return calendar;
    }

}
