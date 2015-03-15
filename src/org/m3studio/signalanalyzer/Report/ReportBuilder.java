package org.m3studio.signalanalyzer.Report;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.m3studio.signalanalyzer.DSP.*;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

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

    private static final PDFont headerFont = PDType1Font.TIMES_BOLD;
    private static final PDFont textFont = PDType1Font.TIMES_ROMAN;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("res/GUI", Locale.getDefault());
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

        helper.setColontitleFont(textFont);
        helper.setColontitleFontsize(colontitleFontsize);
        helper.setColontitle(reportBundle.getString("reportColontitle"));
        helper.setPrintColontitle(true);

        helper.printString(headerFont, headerFontSize, reportBundle.getString("reportStringHeader"), PDFPrintHelper.Alignment.CENTER);
        helper.printString(headerFont, headerFontSize, signalName, PDFPrintHelper.Alignment.CENTER);

        helper.lineBreaks(textFont, textFontSize, 2);

        cutter.reset();

        helper.printString(headerFont, headerFontSize, reportBundle.getString("signalString"), PDFPrintHelper.Alignment.CENTER);
        helper.drawImage(renderSignal(cutter.getCurrentSignal()), PDFPrintHelper.Alignment.CENTER);

        SynthesizableComplexExponent heterodinParameter = new SynthesizableComplexExponent();
        heterodinParameter.setProperty("frequency", 0.0);
        Signal heterodin = new Signal(cutter.getCurrentSignal().getLength());
        Signal heterodinedSignal = new Signal(cutter.getCurrentSignal());
        Spectrum spectrum = new Spectrum(heterodinedSignal);

        helper.printString(headerFont, headerFontSize, reportBundle.getString("spectrumString"), PDFPrintHelper.Alignment.CENTER);
        helper.drawImage(renderSpectrum(spectrum), PDFPrintHelper.Alignment.CENTER);

        for (int i = 0; i < cutter.getCuttersCount(); i++) {
            SynthesizableSignal cut = cutter.getCurrentCutter();

            int harmonicIndex = (int) Math.ceil((Double) cut.getProperty("frequency"));
            double heterodinFrequency = harmonicIndex - (Double) cut.getProperty("frequency");

            int harmonicMin = Math.max(0, harmonicIndex - harmonicsWindow);
            int harmonicMax = Math.min(spectrum.getLength(), harmonicIndex + harmonicsWindow);


            helper.printString(textFont, textFontSize, reportBundle.getString("setTheHeterodinString") + " " + String.valueOf(heterodinFrequency));

            heterodinParameter.setProperty("frequency", heterodinFrequency);
            heterodinParameter.synthesizeIn(heterodin);
            heterodinedSignal.set(cutter.getCurrentSignal()).multiply(heterodin);
            spectrum.recalc();

            helper.drawImage(renderSpectrumPart(spectrum, harmonicMin, harmonicMax), PDFPrintHelper.Alignment.CENTER);

            helper.printString(textFont, textFontSize, reportBundle.getString("evidentString") + " " + reportBundle.getString("isString") + " " + String.valueOf(harmonicIndex));

            Set<String> propertiesNames = cut.getProperties();

            for (String key : propertiesNames) {
                helper.printString(textFont, textFontSize, reportBundle.getString(key + "Property") + " " + reportBundle.getString("isString") + " " + cut.getProperty(key).toString());
            }

            helper.lineBreak(textFont, textFontSize);

            helper.printString(textFont, textFontSize, reportBundle.getString("cutItString"));

            cutter.cutNext();

            helper.drawImage(renderSignal(cutter.getCurrentSignal()), PDFPrintHelper.Alignment.CENTER);

            helper.lineBreak(textFont, textFontSize);
        }

        heterodinedSignal.set(cutter.getCurrentSignal());
        spectrum.recalc();

        helper.drawImage(renderSpectrum(spectrum), PDFPrintHelper.Alignment.CENTER);

        helper.printString(textFont, textFontSize, reportBundle.getString("noiseString"));
        helper.printString(textFont, textFontSize, reportBundle.getString("calcDeviationString"));

        helper.drawImage(renderLatexFormula(reportBundle.getString("latexSigmaString")), PDFPrintHelper.Alignment.CENTER);

        helper.printString(textFont, textFontSize, reportBundle.getString("standardDeviationString") + " " + reportBundle.getString("isString") + " " + String.valueOf(cutter.getCurrentSignal().standardDeviation()));

        String tableData[][] = new String[cutter.getCuttersCount() + 1][];

        tableData[0] = new String[] {
                reportBundle.getString("frequencyProperty"),
                reportBundle.getString("amplitudeProperty"),
                reportBundle.getString("phaseProperty")
        };

        cutter.reset();
        for (int i = 0; i < cutter.getCuttersCount(); i++) {
            SynthesizableSignal cut = cutter.getCurrentCutter();

            tableData[i + 1] = new String[] {
                String.valueOf(cut.getProperty("frequency")),
                String.valueOf(cut.getProperty("amplitude")),
                String.valueOf(cut.getProperty("phase"))
            };

            cutter.cutNext();
        }

        helper.drawTable(textFont, textFontSize, tableSpacing, tableData, PDFPrintHelper.Alignment.CENTER);

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
                resourceBundle.getString("signal"),
                "t",
                resourceBundle.getString("signal"),
                dataset
        );

        return chart.createBufferedImage(chartWidth, chartHeight);
    }

    private BufferedImage renderSpectrumPart(Spectrum spectrum, int minHarmonic, int maxHarmonic) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries amplitudePart = new XYSeries(resourceBundle.getString("amplitudeSpectrum"));

        for (int i = minHarmonic; i < maxHarmonic; i++) {
            amplitudePart.add(i, spectrum.getAbs(i));
        }

        dataset.addSeries(amplitudePart);

        JFreeChart chart = ChartFactory.createXYBarChart(
                resourceBundle.getString("spectrum"),
                resourceBundle.getString("frequency"),
                false,
                resourceBundle.getString("amplitude"),
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
