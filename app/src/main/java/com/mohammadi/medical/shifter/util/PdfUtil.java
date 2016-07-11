package com.mohammadi.medical.shifter.util;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.joda.time.LocalDate;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mohammadi.medical.shifter.entities.DaySchedule;
import com.mohammadi.medical.shifter.entities.Resident;
import com.mohammadi.medical.shifter.entities.Schedule;
import com.mohammadi.medical.shifter.schedule.AbstractScheduleRequest;

import ir.huri.jcal.JalaliCalendar;

public class PdfUtil
{

    public static void writeSchedule(Schedule schedule) throws DocumentException, IOException
    {
        BaseFont base = BaseFont.createFont("assets/X Nazanin.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(base, 14f, Font.NORMAL);

        FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), schedule.getName()+".pdf"));
        Document document = new Document(PageSize.A4.rotate(), 0f, 0f, 50f, 50f);
        PdfWriter.getInstance(document, stream);
        document.setMarginMirroring(true);
        // document.setMargins(0f, 0f, 50f, 50f);
        document.open();
        addMetaData(document);
        PdfPTable table = new PdfPTable(schedule.getSites().size() + 2);

        PdfPCell c1 = new PdfPCell(new Phrase(""));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setColspan(2);
        c1.setBorder(0);
        table.addCell(c1);

        for (int i = 0; i < schedule.getSites().size(); i++)
        {
            c1 = new PdfPCell(new Phrase(schedule.getSites().get(i).toString(), font));
            c1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
        }
        table.setHeaderRows(1);

        int row = 0;
        for (Entry<LocalDate, DaySchedule> entry : schedule.getMap().entrySet())
        {
            BaseColor backgroundColor = row++ % 2 == 0 ? new BaseColor(0.84f, 0.94f, 1f) : BaseColor.WHITE;

            JalaliCalendar jalali = new JalaliCalendar(entry.getKey());
            c1 = new PdfPCell(new Phrase(jalali.toString(), font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(backgroundColor);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(jalali.getDayOfWeekString(), font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            c1.setBackgroundColor(backgroundColor);
            table.addCell(c1);

            if (entry.getValue().getMap().isEmpty() == false)
                for (int i = 0; i < schedule.getSites().size(); i++)
                {
                    if (entry.getValue().getMap().get(schedule.getSites().get(i)) != null)
                    {
                        c1 = new PdfPCell(new Phrase(
                                Utils.listToString(entry.getValue().getMap().get(schedule.getSites().get(i))), font));
                        c1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        c1.setMinimumHeight(40f);
                        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                        c1.setVerticalAlignment(Element.ALIGN_CENTER);
                        c1.setBackgroundColor(backgroundColor);
                        table.addCell(c1);
                    }
                    else
                    {
                        c1 = new PdfPCell();
                        c1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                        c1.setMinimumHeight(40f);
                        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                        c1.setVerticalAlignment(Element.ALIGN_CENTER);
                        c1.setBackgroundColor(backgroundColor);
                        table.addCell(c1);

                    }
                }
            else
                for (int i = 0; i < schedule.getSites().size(); i++)
                {
                    c1 = new PdfPCell();
                    c1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    c1.setMinimumHeight(40f);
                    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c1.setVerticalAlignment(Element.ALIGN_CENTER);
                    c1.setBackgroundColor(backgroundColor);
                    table.addCell(c1);
                }

        }

        document.add(table);
        document.close();

    }

    public static void writeNightShiftSchedule(Schedule schedule)
            throws DocumentException, IOException
    {
        BaseFont base = BaseFont.createFont("assets/X Nazanin.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(base, 14f, Font.NORMAL);

        Document document = new Document(PageSize.A4, 0f, 0f, 50f, 50f);
        FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), schedule.getName()+"-night.pdf"));
        PdfWriter.getInstance(document, stream);
        document.setMarginMirroring(true);
        // document.setMargins(0f, 0f, 50f, 50f);
        document.open();
        addMetaData(document);
        PdfPTable table = new PdfPTable(3);

        PdfPCell c1 = new PdfPCell(new Phrase("تاریخ"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setMinimumHeight(40f);
        c1.setBorder(0);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("روز"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(0);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("نام"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setBorder(0);
        table.addCell(c1);

        table.setHeaderRows(1);

        int row = 0;
        for (Entry<LocalDate, List<Resident>> entry : schedule.getNightShifts().entrySet())
        {
            BaseColor backgroundColor = row++ % 2 == 0 ? new BaseColor(0.84f, 0.94f, 1f) : BaseColor.WHITE;

            JalaliCalendar jalali = new JalaliCalendar(entry.getKey());
            c1 = new PdfPCell(new Phrase(jalali.toString(), font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setBackgroundColor(backgroundColor);
            c1.setMinimumHeight(40f);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(jalali.getDayOfWeekString(), font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            c1.setBackgroundColor(backgroundColor);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase(Utils.listToString(entry.getValue()), font));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setVerticalAlignment(Element.ALIGN_CENTER);
            c1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            c1.setBackgroundColor(backgroundColor);
            table.addCell(c1);
        }

        document.add(table);
        document.close();
    }

    private static void addMetaData(Document document)
    {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Schedule, Resident, Site");
        document.addAuthor("Amir Ahmad Mohammadi");
        document.addCreator("Amir Ahmad Mohammadi");
    }

 }