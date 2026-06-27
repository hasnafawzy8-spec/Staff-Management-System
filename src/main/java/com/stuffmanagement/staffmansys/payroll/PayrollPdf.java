package com.stuffmanagement.staffmansys.payroll;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PayrollPdf {

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Professional blue
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);      // Dark blue-gray
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);       // Green for totals
    private static final Color LIGHT_GRAY = new Color(236, 240, 241);        // Light background
    private static final Color BORDER_COLOR = new Color(189, 195, 199);      // Subtle borders

    public static byte[] render(PayrollRecord p) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // Fonts
            Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD);
            titleFont.setColor(PRIMARY_COLOR);

            Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            sectionFont.setColor(SECONDARY_COLOR);

            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            labelFont.setColor(SECONDARY_COLOR);

            Font valueFont = new Font(Font.HELVETICA, 10);
            Font totalFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            totalFont.setColor(Color.WHITE);

            // Header Section
            addHeader(doc, titleFont);
            doc.add(new Paragraph(" "));

            // Employee Information Box
            addEmployeeInfo(doc, p, sectionFont, labelFont, valueFont);
            doc.add(new Paragraph(" "));

            // Earnings Section
            addEarningsSection(doc, p, sectionFont, labelFont, valueFont);
            doc.add(new Paragraph(" "));

            // Deductions Section
            addDeductionsSection(doc, p, sectionFont, labelFont, valueFont);
            doc.add(new Paragraph(" "));

            // Summary Section
            addSummarySection(doc, p, sectionFont, totalFont);
            doc.add(new Paragraph(" "));

            // Footer
            addFooter(doc);

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private static void addHeader(Document doc, Font titleFont) throws DocumentException {
        Paragraph title = new Paragraph("PAYROLL SLIP", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        doc.add(title);

        // Date line
        Font dateFont = new Font(Font.HELVETICA, 9);
        dateFont.setColor(Color.GRAY);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        Paragraph dateLine = new Paragraph("Generated on: " + date, dateFont);
        dateLine.setAlignment(Element.ALIGN_CENTER);
        dateLine.setSpacingAfter(10);
        doc.add(dateLine);

        // Separator line
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBackgroundColor(PRIMARY_COLOR);
        lineCell.setFixedHeight(3);
        lineCell.setBorder(Rectangle.NO_BORDER);
        line.addCell(lineCell);
        doc.add(line);
    }

    private static void addEmployeeInfo(Document doc, PayrollRecord p, Font sectionFont, Font labelFont, Font valueFont) throws DocumentException {
        Paragraph section = new Paragraph("Employee Information", sectionFont);
        section.setSpacingBefore(5);
        section.setSpacingAfter(8);
        doc.add(section);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 2, 1, 2});

        table.addCell(createInfoCell("Employee:", labelFont, false));
        table.addCell(createInfoCell(p.getEmployee().getUsername(), valueFont, false));
        table.addCell(createInfoCell("Pay Period:", labelFont, false));
        table.addCell(createInfoCell(p.getPeriodMonth() + "/" + p.getPeriodYear(), valueFont, false));

        doc.add(table);
    }

    private static void addEarningsSection(Document doc, PayrollRecord p, Font sectionFont, Font labelFont, Font valueFont) throws DocumentException {
        Paragraph section = new Paragraph("Earnings", sectionFont);
        section.setSpacingBefore(5);
        section.setSpacingAfter(8);
        doc.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 2});

        // Header row
        table.addCell(createHeaderCell("Description", labelFont));
        table.addCell(createHeaderCell("Amount (LKR)", labelFont));

        // Data rows
        table.addCell(createDataCell("Basic Salary", valueFont, false));
        table.addCell(createDataCell(formatCurrency(p.getBasicSalary()), valueFont, true));

        table.addCell(createDataCell("Attendance Hours: " + p.getAttendanceHours(), valueFont, false));
        table.addCell(createDataCell("—", valueFont, true));

        table.addCell(createDataCell("Leave Hours: " + p.getLeaveHours(), valueFont, false));
        table.addCell(createDataCell("—", valueFont, true));

        table.addCell(createDataCell("Net Hours: " + p.getNetHours(), valueFont, false));
        table.addCell(createDataCell("—", valueFont, true));

        table.addCell(createDataCell("Overtime Hours: " + p.getOvertimeHours() + " @ " + formatCurrency(p.getOvertimeRate()), valueFont, false));
        table.addCell(createDataCell(formatCurrency(p.getOvertimeHours().multiply(p.getOvertimeRate())), valueFont, true));

        table.addCell(createDataCell("Allowances", valueFont, false));
        table.addCell(createDataCell(formatCurrency(p.getAllowances()), valueFont, true));

        // Gross Total
        PdfPCell grossLabel = createDataCell("Gross Salary", labelFont, false);
        grossLabel.setBackgroundColor(LIGHT_GRAY);
        table.addCell(grossLabel);

        PdfPCell grossValue = createDataCell(formatCurrency(p.getGrossSalary()), labelFont, true);
        grossValue.setBackgroundColor(LIGHT_GRAY);
        table.addCell(grossValue);

        doc.add(table);
    }

    private static void addDeductionsSection(Document doc, PayrollRecord p, Font sectionFont, Font labelFont, Font valueFont) throws DocumentException {
        Paragraph section = new Paragraph("Deductions", sectionFont);
        section.setSpacingBefore(5);
        section.setSpacingAfter(8);
        doc.add(section);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 2});

        // Header row
        table.addCell(createHeaderCell("Description", labelFont));
        table.addCell(createHeaderCell("Amount (LKR)", labelFont));

        // Data row
        table.addCell(createDataCell("Total Deductions", valueFont, false));
        table.addCell(createDataCell(formatCurrency(p.getDeductions()), valueFont, true));

        doc.add(table);
    }

    private static void addSummarySection(Document doc, PayrollRecord p, Font sectionFont, Font totalFont) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 2});

        PdfPCell labelCell = new PdfPCell(new Phrase("NET SALARY", totalFont));
        labelCell.setBackgroundColor(ACCENT_COLOR);
        labelCell.setPadding(12);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(formatCurrency(p.getNetSalary()), totalFont));
        valueCell.setBackgroundColor(ACCENT_COLOR);
        valueCell.setPadding(12);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);

        doc.add(table);
    }

    private static void addFooter(Document doc) throws DocumentException {
        Font footerFont = new Font(Font.HELVETICA, 8);
        footerFont.setColor(Color.GRAY);

        Paragraph footer = new Paragraph("This is a computer-generated document. No signature is required.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        doc.add(footer);

        Paragraph confidential = new Paragraph("CONFIDENTIAL - For recipient use only", footerFont);
        confidential.setAlignment(Element.ALIGN_CENTER);
        doc.add(confidential);
    }

    private static PdfPCell createHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(PRIMARY_COLOR);
        font.setColor(Color.WHITE);
        cell.setPadding(8);
        cell.setBorderColor(BORDER_COLOR);
        cell.setBorderWidth(1);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private static PdfPCell createDataCell(String text, Font font, boolean rightAlign) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBorderColor(BORDER_COLOR);
        cell.setBorderWidth(0.5f);
        cell.setHorizontalAlignment(rightAlign ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
        return cell;
    }

    private static PdfPCell createInfoCell(String text, Font font, boolean header) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(header ? LIGHT_GRAY : Color.WHITE);
        return cell;
    }

    private static String formatCurrency(java.math.BigDecimal amount) {
        return String.format("%,.2f", amount);
    }
}