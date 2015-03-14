package org.m3studio.signalanalyzer.Report;

import org.apache.pdfbox.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.m3studio.signalanalyzer.DSP.Complex;
import org.m3studio.signalanalyzer.DSP.Signal;
import org.m3studio.signalanalyzer.DSP.SignalCutter;
import org.m3studio.signalanalyzer.DSP.Spectrum;
import org.m3studio.signalanalyzer.GUI.SignalGeneratorView;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by madmax on 07.03.15.
 */
public class ReportBuilder {
    private String signalName;
    private SignalCutter cutter;

    private static final float scale = 5.0f;

    private static final float headerFontSize = 9.0f * scale;
    private static final float textFontSize = 6.0f * scale;
    private static final float colontitleFontsize = 3.0f * scale;

    private static final int harmonicsWindow = 10;

    private static final int chartWidth = (int) (150.0f * scale);
    private static final int chartHeight = (int) (105.0f * scale);
    private static final float tableSpacing = 1.5f * scale;

    private static final float pageWidth = 210.0f * scale;
    private static final float pageHeight = 297.0f * scale;
    private static final float marginSides = 15.0f * scale;
    private static final float marginTopBottom = 10.0f * scale;
    private static final float interval = 3.0f * scale;

    private static final String latexSigmaString =
            "\\sigma =\\sqrt{\\frac{\\sum _{i=1}^n \\left(x_i-\\bar{x}\\right){}^2}{n}}";

    private static final ResourceBundle reportBundle = ResourceBundle.getBundle("res/report", Locale.getDefault());

    public ReportBuilder(String signalName, SignalCutter cutter) {
        this.signalName = signalName;
        this.cutter = cutter;
    }

    public ReportBuilder(SignalCutter cutter) {
        this("", cutter);
    }

    public PDDocument buildPDFReport() throws PDFPrintException {
        PDDocument document = new PDDocument();

        PDFPrintHelper helper = new PDFPrintHelper(document, new PDRectangle(pageWidth, pageHeight), marginTopBottom, marginSides, interval);

        PDFont arial = null;
        try {
            arial = PDTrueTypeFont.loadTTF(document, "res/Arial.ttf");
            arial.setFontEncoding(new WinAnsiEncoding());
        } catch (IOException e) {
            throw new PDFPrintException("Exception occured while loading font!", e);
        }

        final PDFont headerFont = arial;
        final PDFont textFont = arial;

        /*helper.setColontitleFont(textFont);
        helper.setColontitleFontsize(colontitleFontsize);
        helper.setColontitle("Created by Signal Analyzer");
        helper.setPrintColontitle(true);*/

        helper.printString(headerFont, headerFontSize, reportBundle.getString("reportStringHeader"), PDFPrintHelper.Alignment.CENTER);
        helper.printString(headerFont, headerFontSize, signalName, PDFPrintHelper.Alignment.CENTER);

        /*helper.lineBreaks(textFont, textFontSize, 2);

        helper.printString(headerFont, headerFontSize, reportBundle.getString("signalString"), PDFPrintHelper.Alignment.CENTER);
        helper.drawImage(renderSignal(sourceSignal), PDFPrintHelper.Alignment.CENTER);

        Signal currentSignal = new Signal(sourceSignal);
        Signal heterodin = new Signal(currentSignal.getLength());
        Signal heterodinedSignal = new Signal(currentSignal);
        Spectrum spectrum = new Spectrum(heterodinedSignal);

        helper.printString(headerFont, headerFontSize, reportBundle.getString("spectrumString"), PDFPrintHelper.Alignment.CENTER);
        helper.drawImage(renderSpectrum(spectrum), PDFPrintHelper.Alignment.CENTER);

        for (SignalGeneratorView cut : cuts) {
            int harmonicIndex = (int) Math.ceil(cut.getFrequency());
            double heterodinFrequency = harmonicIndex - cut.getFrequency();

            int harmonicMin = Math.max(0, harmonicIndex - harmonicsWindow);
            int harmonicMax = Math.min(spectrum.getLength(), harmonicIndex + harmonicsWindow);


            helper.printString(textFont, textFontSize, setTheHeterodinString + " " + String.valueOf(heterodinFrequency));

            heterodin.heterodinate(heterodinFrequency);
            heterodinedSignal.set(currentSignal).multiply(heterodin);
            spectrum.recalc();

            helper.drawImage(renderSpectrumPart(spectrum, harmonicMin, harmonicMax), PDFPrintHelper.Alignment.CENTER);

            helper.printString(textFont, textFontSize, reportBundle.getString("evidentString + ": " + String.valueOf(harmonicIndex));

            helper.printString(textFont"), textFontSize, foundFrequencyString + ":");
            helper.printString(textFont, textFontSize, "Amplitude is " + String.valueOf(cut.getAmplitude()));
            helper.printString(textFont, textFontSize, "Starting phase is " + String.valueOf(cut.getPhase()));
            helper.printString(textFont, textFontSize, reportBundle.getString(""Frequency is " + cut.getFrequency());

            helper.lineBreak(textFont"), textFontSize);

            helper.printString(textFont, textFontSize, reportBundle.getString("cutItString);

            currentSignal.minus(cut.getSignal());

            helper.drawImage(renderSignal(currentSignal)"), PDFPrintHelper.Alignment.CENTER);

            helper.lineBreak(textFont, textFontSize);
        }

        heterodinedSignal.set(currentSignal);
        spectrum.recalc();

        helper.drawImage(renderSpectrum(spectrum), PDFPrintHelper.Alignment.CENTER);

        helper.printString(textFont, textFontSize, reportBundle.getString("noiseString);
        helper.printString(textFont"), textFontSize, calcDeviationString);

        helper.drawImage(renderLatexFormula(latexSigmaString), PDFPrintHelper.Alignment.CENTER);

        helper.printString(textFont, textFontSize, standardDeviationString + " is " + String.valueOf(currentSignal.standardDeviation()));

        String tableData[][] = new String[cuts.size() + 1][];

        tableData[0] = new String[] {
                "Harmonic",
                "Amplitude",
                "Phase"
        };

        for (int i = 0; i < cuts.size(); i++) {
            tableData[i + 1] = new String[] {
                String.valueOf(cuts.get(i).getFrequency()),
                String.valueOf(cuts.get(i).getAmplitude()),
                String.valueOf(cuts.get(i).getPhase()),
            };
        }

        helper.drawTable(textFont, textFontSize, tableSpacing, tableData, PDFPrintHelper.Alignment.CENTER);
        */

        helper.close();


        return document;
    }

    private BufferedImage renderSignal(Signal signal) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries realPart = new XYSeries("Re");
        XYSeries imaginaryPart = new XYSeries("Im");

        for (int i = 0; i < signal.getLength(); i++) {
            Complex c = signal.get(i);

            realPart.add(i, c.re());
            imaginaryPart.add(i, c.im());
        }

        dataset.addSeries(realPart);
        dataset.addSeries(imaginaryPart);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Сигнал",
                "t",
                "Сигнал",
                dataset
        );

        return chart.createBufferedImage(chartWidth, chartHeight);
    }

    private BufferedImage renderSpectrumPart(Spectrum spectrum, int minHarmonic, int maxHarmonic) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries amplitudePart = new XYSeries("Амплитудный спектр");

        for (int i = minHarmonic; i < maxHarmonic; i++) {
            amplitudePart.add(i, spectrum.getAbs(i));
        }

        dataset.addSeries(amplitudePart);

        JFreeChart chart = ChartFactory.createXYBarChart(
                "Спектр",
                "Частота",
                false,
                "Энергия",
                dataset
        );

        return chart.createBufferedImage(chartWidth, chartHeight);
    }

    private BufferedImage renderSpectrum(Spectrum spectrum) {
        return renderSpectrumPart(spectrum, 0, spectrum.getLength());
    }

    private BufferedImage renderLatexFormula(String latexString) {
        TeXFormula fomule = new TeXFormula(latexString);
        TeXIcon ti = fomule.createTeXIcon(
                TeXConstants.STYLE_DISPLAY, 5 * scale);
        BufferedImage b = new BufferedImage(ti.getIconWidth(), ti
                .getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        ti.paintIcon(new JLabel(), b.getGraphics(), 0, 0);

        return b;
    }

}
