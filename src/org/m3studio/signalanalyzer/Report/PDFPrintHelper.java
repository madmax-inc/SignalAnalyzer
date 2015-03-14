package org.m3studio.signalanalyzer.Report;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by madmax on 07.03.15.
 */
public class PDFPrintHelper {
    private PDDocument document;
    private PDRectangle pageSize;
    private PDPage currentPage;
    private PDPageContentStream stream;

    private float currentMarginTop;

    private float marginTopBottom;
    private float marginLeftRight;
    private float lineInterval;

    private String colontitle;
    private PDFont colontitleFont;
    private float colontitleFontsize;
    private boolean printColontitle;

    public PDFPrintHelper(PDDocument doc, PDRectangle pageSize, float marginTopBottom, float marginLeftRight, float lineInterval) throws PDFPrintException {
        document = doc;
        this.pageSize = pageSize;
        this.marginTopBottom = marginTopBottom;
        this.marginLeftRight = marginLeftRight;
        this.lineInterval = lineInterval;

        this.colontitle = "";

        this.printColontitle = false;

        try {
            newPage();
        } catch (PDFPrintException e) {
            throw new PDFPrintException("Exception occured while creating document!", e);
        }
    }

    private void newPage() throws PDFPrintException {
        if (printColontitle)
            printColontitle();

        currentPage = new PDPage(pageSize);
        document.addPage(currentPage);
        currentMarginTop = marginTopBottom;

        try {
            if (stream != null) {
                stream.close();
            }

            stream = new PDPageContentStream(document, currentPage);
        } catch (IOException e) {
            throw new PDFPrintException("Exception occured while creating page!", e);
        }
    }

    private void checkMarginTop(float desiredMargin) throws PDFPrintException {
        if (desiredMargin >= (pageSize.getHeight() - marginTopBottom)) {
            newPage();
        }
    }

    private float stringHeight(PDFont font, float fontSize) {
        return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
    }

    private float stringWidth(PDFont font, float fontSize, String string) throws IOException {
        return font.getStringWidth(string) / 1000 * fontSize;
    }

    private int lineSymbols(PDFont font, float fontSize) throws IOException {
        return (int) (pageSize.getWidth() * (stringWidth(font, fontSize, "ABCDE") / 5));
    }

    private float getMarginLeft(float objectSize, Alignment align) {
        switch (align) {
            case LEFT:
                return marginLeftRight;
            case CENTER:
                return (pageSize.getWidth() - objectSize) / 2;
            default:
            case RIGHT:
                return (pageSize.getWidth() - marginLeftRight - objectSize);
        }
    }

    private float getMarginBottom(float objectSize) {
        return pageSize.getHeight() - currentMarginTop - objectSize;
    }

    public void setColontitle(String colontitle) {
        this.colontitle = colontitle;
    }

    public void setPrintColontitle(boolean print) {
        this.printColontitle = print;
    }

    public void setColontitleFont(PDFont colontitleFont) {
        this.colontitleFont = colontitleFont;
    }

    public void setColontitleFontsize(float colontitleFontsize) {
        this.colontitleFontsize = colontitleFontsize;
    }

    public void lineBreaks(PDFont font, float fontSize, int amount) throws PDFPrintException {
        float textHeight = stringHeight(font, fontSize);

        currentMarginTop += amount * (textHeight + lineInterval);

        checkMarginTop(currentMarginTop);
    }

    public void lineBreak(PDFont font, float fontSize) throws PDFPrintException {
        lineBreaks(font, fontSize, 1);
    }

    public void printString(PDFont font, float fontSize, String text) throws PDFPrintException {
        printString(font, fontSize, text, Alignment.LEFT);
    }

    public void printString(PDFont font, float fontSize, String text, Alignment align) throws PDFPrintException {
        float stringHeight = stringHeight(font, fontSize);

        float desiredMargin = currentMarginTop + stringHeight + lineInterval;

        try {
            checkMarginTop(desiredMargin);

            stream.beginText();
            stream.setFont(font, fontSize);
            stream.moveTextPositionByAmount(getMarginLeft(stringWidth(font, fontSize, text), align), getMarginBottom(stringHeight));
            stream.drawString(text);
            stream.endText();

            currentMarginTop += stringHeight + lineInterval;
        } catch (Exception e) {
            throw new PDFPrintException("Exception occured while printing text!", e);
        }
    }

    public void drawImage(BufferedImage image) throws PDFPrintException {
        drawImage(image, Alignment.LEFT);
    }

    public void drawImage(BufferedImage image, Alignment align) throws PDFPrintException {
        float desiredMargin = currentMarginTop + image.getHeight() + lineInterval;

        try {
            checkMarginTop(desiredMargin);
            stream.drawImage(new PDJpeg(document, image, 0.9f), getMarginLeft(image.getWidth(), align), getMarginBottom(image.getHeight()));
            currentMarginTop += image.getHeight() + lineInterval;
        } catch (Exception e) {
            throw new PDFPrintException("Exception occured while inserting image!", e);
        }
    }

    public void drawTable(PDFont font, float fontSize, float spacing, Object values[][]) throws PDFPrintException {
        drawTable(font, fontSize, spacing, values, Alignment.LEFT);
    }

    public void drawTable(PDFont font, float fontSize, float spacing, Object values[][], Alignment align) throws PDFPrintException {
        int rows = values.length;
        int columns = values[0].length;

        float columnWidths[] = new float[columns];
        float tableWidth = 0.0f;

        for (int i = 0; i < columns; i++)
            columnWidths[i] = 0.0f;

        try {
            for (int i = 0; i < rows; i++) {
                float rowWidth = 0.0f;

                for (int j = 0; j < columns; j++) {
                    float columnWidth = stringWidth(font, fontSize, values[i][j].toString()) + 2 * spacing;

                    if (columnWidth > columnWidths[j])
                        columnWidths[j] = columnWidth;

                    rowWidth += columnWidth;
                }

                if (rowWidth > tableWidth)
                    tableWidth = rowWidth;
            }

            for (int i = 0; i < rows; i++) {
                drawTableRow(font, fontSize, values[i], columnWidths, tableWidth, spacing, align);
            }

            currentMarginTop += lineInterval;
        } catch (Exception e) {
            throw new PDFPrintException("Exception occured while drawing table!", e);
        }

    }

    private void drawTableRow(PDFont font, float fontSize, Object row[], float columnWidths[], float tableWidth, float spacing, Alignment align) throws PDFPrintException {
        float cellHeight = stringHeight(font, fontSize) + 2 * spacing;
        float rowWidth = 0.0f;

        try {
            float desiredMargin = currentMarginTop + cellHeight;

            checkMarginTop(desiredMargin);

            float rowXStart = getMarginLeft(tableWidth, align);

            float rowYStart = getMarginBottom(0.0f);
            float rowYEnd = getMarginBottom(cellHeight);

            stream.drawLine(rowXStart, rowYStart, rowXStart + tableWidth, rowYStart);
            stream.drawLine(rowXStart, rowYEnd, rowXStart + tableWidth, rowYEnd);
            stream.drawLine(rowXStart, rowYStart, rowXStart, rowYEnd);

            for (int j = 0; j < row.length; j++) {
                String text = row[j].toString();

                stream.beginText();
                stream.setFont(font, fontSize);
                stream.moveTextPositionByAmount(rowXStart + rowWidth + spacing, rowYEnd + spacing);
                stream.drawString(text);
                stream.endText();

                rowWidth += columnWidths[j];

                stream.drawLine(rowXStart + rowWidth, rowYStart, rowXStart + rowWidth, rowYEnd);
            }
        } catch (IOException e) {
            throw new PDFPrintException("Exception occured while drawing table row!", e);
        }

        currentMarginTop += cellHeight;
    }

    private void printColontitle() throws PDFPrintException {
        float textHeight = stringHeight(colontitleFont, colontitleFontsize);

        float textStartY = (marginTopBottom - textHeight) / 2 + textHeight;

        try {
            stream.beginText();
            stream.setFont(colontitleFont, colontitleFontsize);
            stream.moveTextPositionByAmount(getMarginLeft(stringWidth(colontitleFont, colontitleFontsize, colontitle), Alignment.CENTER), textStartY);
            stream.drawString(colontitle);
            stream.endText();
        } catch (IOException e) {
            throw new PDFPrintException("Exception occured while printing colontitle!", e);
        }
    }

    public void close() throws PDFPrintException {
        if (printColontitle)
            printColontitle();

        try {
            stream.close();
        } catch (IOException e) {
            throw new PDFPrintException("Exception occured while closing stream!", e);
        }
    }

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }
}
