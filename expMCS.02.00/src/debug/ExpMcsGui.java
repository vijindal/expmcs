package debug;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import mcSampler.mcData;

/**
 * Simple Swing front-end for running expMCS simulations.
 */
public class ExpMcsGui extends JFrame {

    private final JComboBox<String> phaseCombo = new JComboBox<>(new String[]{"L10", "L12", "A1", "A2"});
    private final JTextField temperatureField = new JTextField("1.73");
    private final JTextField compositionField = new JTextField("0.50");
    private final JTextField latticeSizeField = new JTextField("32");
    private final JTextField latticeTypeField = new JTextField("2");
    private final JTextField mcssField = new JTextField("4000");
    private final JTextField warmupField = new JTextField("1000");
    private final JTextField ecdisField = new JTextField();
    private final JCheckBox writeFileBox = new JCheckBox("Write result file", true);
    private final JButton runButton = new JButton("Run Simulation");
    private final JTextArea outputArea = new JTextArea(16, 72);

    public ExpMcsGui() {
        super("expMCS Monte Carlo GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Simulation Parameters"));
        form.add(new JLabel("Phase"));
        form.add(phaseCombo);
        form.add(new JLabel("Temperature (T)"));
        form.add(temperatureField);
        form.add(new JLabel("Composition (xB)"));
        form.add(compositionField);
        form.add(new JLabel("Lattice Size"));
        form.add(latticeSizeField);
        form.add(new JLabel("Lattice Type"));
        form.add(latticeTypeField);
        form.add(new JLabel("Production MCSS"));
        form.add(mcssField);
        form.add(new JLabel("Warmup MCSS"));
        form.add(warmupField);
        form.add(new JLabel("ECIs (comma-separated ecdis)"));
        form.add(ecdisField);
        form.add(new JLabel("Output"));
        form.add(writeFileBox);

        JPanel bottom = new JPanel(new BorderLayout(6, 6));
        bottom.add(runButton, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        bottom.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        add(form, BorderLayout.NORTH);
        add(bottom, BorderLayout.CENTER);

        phaseCombo.addActionListener(e -> updateEcdisHint());
        runButton.addActionListener(e -> runSimulationFromForm());

        updateEcdisHint();
        pack();
        setLocationRelativeTo(null);
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            ExpMcsGui gui = new ExpMcsGui();
            gui.setVisible(true);
        });
    }

    private void updateEcdisHint() {
        String phase = String.valueOf(phaseCombo.getSelectedItem());
        ecdisField.setText(formatEcdis(RunEXPMCS.defaultEcdisForPhase(phase)));
    }

    private String formatEcdis(double[] ecdis) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ecdis.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ecdis[i]);
        }
        return sb.toString();
    }

    private void runSimulationFromForm() {
        final String phase = String.valueOf(phaseCombo.getSelectedItem());
        final double temperature;
        final double xB;
        final int latticeSize;
        final int latticeType;
        final int mcss;
        final int warmup;
        final double[] ecdis;
        final boolean writeToFile = writeFileBox.isSelected();

        try {
            temperature = Double.parseDouble(temperatureField.getText().trim());
            xB = Double.parseDouble(compositionField.getText().trim());
            latticeSize = Integer.parseInt(latticeSizeField.getText().trim());
            latticeType = Integer.parseInt(latticeTypeField.getText().trim());
            mcss = Integer.parseInt(mcssField.getText().trim());
            warmup = Integer.parseInt(warmupField.getText().trim());
            ecdis = parseEcdis(ecdisField.getText(), phase);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        runButton.setEnabled(false);
        outputArea.setText("Running simulation...\n");
        outputArea.append("Phase=" + phase + ", T=" + temperature + ", xB=" + xB
                + ", ecdis length=" + ecdis.length + "\n");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws IOException {
                long beg = System.currentTimeMillis();
                RunEXPMCS.runSimulation(
                        phase, ecdis, temperature, xB, latticeSize, latticeType, mcss, warmup, writeToFile);
                long end = System.currentTimeMillis();
                String file = (mcData.fileName == null || mcData.fileName.length() == 0)
                        ? "(no file generated)" : mcData.fileName;
                return "Simulation completed in " + ((end - beg) / 1000.0)
                        + " sec.\nOutput file: " + file + "\nSee console for detailed MC trace.";
            }

            @Override
            protected void done() {
                runButton.setEnabled(true);
                try {
                    outputArea.append(get() + "\n");
                } catch (Exception ex) {
                    outputArea.append("Simulation failed: " + ex.getMessage() + "\n");
                }
            }
        };

        worker.execute();
    }

    private double[] parseEcdis(String text, String phase) {
        String raw = text == null ? "" : text.trim();
        if (raw.length() == 0) {
            throw new IllegalArgumentException("ECIs cannot be empty.");
        }
        String[] tokens = raw.split(",");
        int expected = RunEXPMCS.expectedEcdisSizeForPhase(phase);
        if (tokens.length != expected) {
            throw new IllegalArgumentException("Phase " + phase + " expects " + expected
                    + " ECI values (ecdis), but got " + tokens.length + ".");
        }
        double[] values = new double[tokens.length];
        try {
            for (int i = 0; i < tokens.length; i++) {
                values[i] = Double.parseDouble(tokens[i].trim());
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("ECIs must be comma-separated numeric values.");
        }
        return values;
    }
}