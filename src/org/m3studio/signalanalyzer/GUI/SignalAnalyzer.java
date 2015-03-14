package org.m3studio.signalanalyzer.GUI;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.m3studio.signalanalyzer.DSP.*;
import org.m3studio.signalanalyzer.Report.PDFPrintException;
import org.m3studio.signalanalyzer.Report.ReportBuilder;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by madmax on 06.03.15.
 */
public class SignalAnalyzer extends JFrame implements HeterodinSelectorView.HeterodinCallback, SignalGeneratorView.GeneratorCallback {
    private String signalName;

    private Signal sourceSignal;
    private Signal signal;
    private SignalCutter cutter;
    private Signal heterodinedSignal;
    private Spectrum spectrum;

    private SignalView signalView;
    private SpectrumView spectrumView;

    private HeterodinSelectorView heterodinPanel;
    private SpectrumMeasureView spectrumMeasurePanel;

    private Box cutterPanel;

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("res/GUI", Locale.getDefault());


    public SignalAnalyzer(boolean autoAnalyzeEnabled) {
        super("Signal Analyzer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        //Signal
        signalView = new SignalView(resourceBundle.getString("signal"));
        spectrumView = new SpectrumView(resourceBundle.getString("spectrum"));

        //Heterodin Panel
        heterodinPanel = new HeterodinSelectorView();
        heterodinPanel.setCallback(this);

        //Spectrum Measure
        spectrumMeasurePanel = new SpectrumMeasureView();

        //Signal Cutters
        cutterPanel = Box.createVerticalBox();

        //Controls
        Box controlsPanel = Box.createHorizontalBox();

        JButton addCutterButton = new JButton(resourceBundle.getString("addCutHarmonicButton"));
        addCutterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CosineGeneratorView view = new CosineGeneratorView();

                cutterPanel.add(view);
                cutter.addSignal(view.getSignalParameters());
                view.setCallback(SignalAnalyzer.this);

                validate();
                repaint();
            }
        });
        controlsPanel.add(addCutterButton);

        JButton resetButton = new JButton(resourceBundle.getString("clearButton"));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCutters();
            }
        });
        controlsPanel.add(resetButton);

        //Menu
        JMenuBar menu = new JMenuBar();

        JMenu fileMenu = new JMenu(resourceBundle.getString("menuFile"));

        JMenuItem openItem = new JMenuItem(resourceBundle.getString("menuOpenFile"));
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory())
                            return true;
                        else
                            return f.getName().endsWith(".dat");
                    }

                    @Override
                    public String getDescription() {
                        return "Signal Files";
                    }
                });

                if (chooser.showOpenDialog(SignalAnalyzer.this) != JFileChooser.APPROVE_OPTION)
                    return;

                try {
                    sourceSignal = Signal.loadFile(chooser.getSelectedFile().getAbsolutePath());
                    signalName = chooser.getSelectedFile().getName();

                    createSignals();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

            }
        });
        fileMenu.add(openItem);

        JMenuItem saveReportItem = new JMenuItem(resourceBundle.getString("menuSaveReport"));
        saveReportItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();

                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory())
                            return true;
                        else
                            return f.getName().endsWith(".pdf");
                    }

                    @Override
                    public String getDescription() {
                        return "PDF Document";
                    }
                });

                if (chooser.showSaveDialog(SignalAnalyzer.this) != JFileChooser.APPROVE_OPTION)
                    return;

                ReportBuilder builder = new ReportBuilder(signalName, cutter);
                try {
                    PDDocument doc = builder.buildPDFReport();

                    doc.save(chooser.getSelectedFile());
                } catch (PDFPrintException e1) {
                    e1.printStackTrace();
                } catch (COSVisitorException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        fileMenu.add(saveReportItem);

        JMenu autoAnalyzeMenu = new JMenu(resourceBundle.getString("menuAutoAnalyze"));

        JMenuItem autoAnalyzerItem = new JMenuItem(resourceBundle.getString("menuDoAnalyze"));
        autoAnalyzerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AutoSignalAnalyzer analyzer = new AutoSignalAnalyzer(sourceSignal, 0.05, 0.05, 1, 1);

                ArrayList<SynthesizableSignal> signals = analyzer.detectHarmonics();

                cutter.removeAll();
                cutter.reset();
                cutterPanel.removeAll();

                for (SynthesizableSignal s : signals) {
                    cutter.addSignal(s);
                    CosineGeneratorView generatorView = new CosineGeneratorView();
                    generatorView.setSignalParameters(s);
                    generatorView.setCallback(SignalAnalyzer.this);
                    cutterPanel.add(generatorView);
                }

                cutter.recutAll();

                signalView.updateSignalView();
                signalView.repaint();

                onHeterodinChanged();

                validate();
                repaint();
            }
        });
        autoAnalyzeMenu.add(autoAnalyzerItem);

        JMenu helpMenu = new JMenu(resourceBundle.getString("menuHelp"));

        JMenuItem aboutItem = new JMenuItem(resourceBundle.getString("menuAbout"));
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(SignalAnalyzer.this, resourceBundle.getString("aboutText"), resourceBundle.getString("menuAbout"), JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(aboutItem);

        JMenuItem licenseAgreementItem = new JMenuItem(resourceBundle.getString("menuLicense"));
        licenseAgreementItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        helpMenu.add(licenseAgreementItem);


        menu.add(fileMenu);
        menu.add(autoAnalyzeMenu);
        menu.add(helpMenu);

        setJMenuBar(menu);

        add(signalView);
        add(spectrumView);
        add(Box.createVerticalStrut(GUIOptions.verticalStrut));
        add(heterodinPanel);
        add(Box.createVerticalStrut(GUIOptions.verticalStrut));
        add(spectrumMeasurePanel);
        add(Box.createVerticalStrut(GUIOptions.verticalStrut));
        add(cutterPanel);
        add(Box.createVerticalStrut(GUIOptions.verticalStrut));
        add(controlsPanel);

        sourceSignal = new Signal(1024);
        signalName = "";
        createSignals();

        pack();
        validate();
        repaint();
    }

    @Override
    public void onParametersChanged(SignalGeneratorView view) {
        cutter.regenerateSignal(view.getSignalParameters());
        cutter.recutAll();

        signalView.updateSignalView();
        signalView.repaint();

        onHeterodinChanged();
    }

    @Override
    public void onRemoveGenerator(SignalGeneratorView view) {
        cutterPanel.remove(view);
        cutter.removeSignal(view.getSignalParameters());

        validate();
        repaint();

        cutter.recutAll();
        signalView.updateSignalView();
        signalView.repaint();

        onHeterodinChanged();
    }

    @Override
    public void onHeterodinChanged() {
        heterodinedSignal.set(signal).multiply(heterodinPanel.getHeterodin());
        spectrum.recalc();
        spectrumView.updateSpectrumView();

        spectrumView.repaint();

        spectrumMeasurePanel.updateMeasure();
    }

    public void clearCutters() {
        cutterPanel.removeAll();

        if (cutter != null) {
            cutter.removeAll();

            cutter.recutAll();
            signalView.updateSignalView();
            signalView.repaint();

            onHeterodinChanged();
        }

        validate();
        repaint();
    }

    public void createSignals() {
        signal = new Signal(sourceSignal);
        heterodinedSignal = new Signal(sourceSignal);

        clearCutters();

        cutter = new SignalCutter(sourceSignal, signal);
        spectrum = new Spectrum(heterodinedSignal);
        heterodinPanel.changeHeterodinLength(signal.getLength());
        spectrumMeasurePanel.setSpectrum(spectrum);

        signalView.setSignal(signal);
        spectrumView.setSpectrum(spectrum);

        signalView.updateSignalView();
        spectrumView.updateSpectrumView();

    }

    public static void main(String args[]) {
        boolean autoAnalyze = false;
        if (args.length == 1)
            if (args[0].equals("autoAnalyzerEnabled"))
                autoAnalyze = true;

        SignalAnalyzer app = new SignalAnalyzer(autoAnalyze);

        app.setVisible(true);
    }
}
