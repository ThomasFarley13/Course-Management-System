package com.COMP3004.CMS;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@ComponentScan("com")
public class Stats {

    @Autowired
    private final UserDatabase repository;

    public Stats(UserDatabase repository) {
        this.repository = repository;
    }

    public XSSFWorkbook getStats() throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        birthdayStats(workbook);
        genderStats(workbook);


        return workbook;

    }
    public void birthdayStats(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.createSheet("BirthdayStats");
        int[] months = new int[12];
        for(int i=0; i< 11; i+=1){
            months[i] = 0;
        }
        for (User user : repository.findByBirthdateIsNotNull()) {
            String birthdate = user.getBirthdate();
            String[] birthdata = birthdate.split("-");
            switch(birthdata[1]){
                case "01":
                    months[0] +=1;
                    break;
                case "02":
                    months[1] +=1;
                    break;
                case "03":
                    months[2] +=1;
                    break;
                case "04":
                    months[3] +=1;
                    break;
                case "05":
                    months[4] +=1;
                    break;
                case "06":
                    months[5] +=1;
                    break;
                case "07":
                    months[6] +=1;
                    break;
                case "08":
                    months[7] +=1;
                    break;
                case "09":
                    months[8] +=1;
                    break;
                case "10":
                    months[9] +=1;
                    break;
                case "11":
                    months[10] +=1;
                    break;
                case "12":
                    months[11] +=1;
                    break;
            }
        }

        Object[][] genderData = {
                {"January", months[0]},
                {"February", months[1]},
                {"March", months[2]},
                {"April", months[3]},
                {"May", months[4]},
                {"June", months[5]},
                {"July", months[6]},
                {"August", months[7]},
                {"September", months[8]},
                {"October", months[9]},
                {"November", months[10]},
                {"December", months[11]},
        };

        int rowCount = 0;

        for (Object[] gender : genderData) {

            Row row = sheet.createRow(++rowCount);

            int columnCount = 0;

            for (Object field : gender) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }

        }
    }
    public void genderStats(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.createSheet("GenderStats");
        Object[][] genderData = {
                {"Male", repository.findByGender("male").size()},
                {"Female", repository.findByGender("female").size()},
                {"Other", repository.findByGender("other").size()},
        };

        int rowCount = 0;

        for (Object[] gender : genderData) {

            Row row = sheet.createRow(++rowCount);

            int columnCount = 0;

            for (Object field : gender) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }

        }
    }

}
