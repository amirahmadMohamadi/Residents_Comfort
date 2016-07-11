package com.mohammadi.medical.shifter.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map.Entry;

import org.joda.time.LocalDate;

import com.mohammadi.medical.shifter.entities.DaySchedule;
import com.mohammadi.medical.shifter.entities.Schedule;
import com.mohammadi.medical.shifter.schedule.ScheduleRequest;

import ir.huri.jcal.JalaliCalendar;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelUtil
{

    private static WritableCellFormat timesBoldUnderline;
    private static WritableCellFormat times;

    public static void writeSchedule(Schedule schedule)
            throws RowsExceededException, WriteException, IOException
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), schedule.getName() + ".xls");
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Schedule", 0);
        WritableSheet excelSheet = workbook.getSheet(0);

        createLabel(excelSheet);

        for (int i = 0; i < schedule.getSites().size(); i++)
        {
            addLabel(excelSheet, i + 2, 1, schedule.getSites().get(i).toString());
        }

        int row = 2;
        for (Entry<LocalDate, DaySchedule> entry : schedule.getMap().entrySet())
        {
            JalaliCalendar jalali = new JalaliCalendar(entry.getKey());
            addLabel(excelSheet, 1, row, jalali.toString());
            if (entry.getValue().getMap().isEmpty() == false)
                for (int i = 0; i < schedule.getSites().size(); i++)
                {
                    if (entry.getValue().getMap().get(schedule.getSites().get(i)) != null)
                        addLabel(excelSheet, i + 2, row,
                                Utils.listToString(entry.getValue().getMap().get(schedule.getSites().get(i)))
                        );
                }
            row++;
        }

        workbook.write();
        workbook.close();

    }

    private static void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException
    {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private static void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException
    {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    private static void createLabel(WritableSheet sheet) throws WriteException
    {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(true);

        // create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE
        );
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        // Write a few headers
        addCaption(sheet, 0, 0, "Header 1");
        // addCaption(sheet, 1, 0, "This is another header");

    }
}