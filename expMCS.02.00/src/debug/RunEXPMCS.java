package debug;

import java.io.IOException;
import mcSampler.mcData;
import mcSampler.mcSampler;
import phase.mc.BCC.A2MCBINCE;
import phase.mc.FCC.A1MCBINCE;
import phase.mc.FCC.L10MCBINCE;
import phase.mc.FCC.L12MCBINCE;
import phase.mc.PHASEMCBINCE;

/**
 * Application entry point.
 *
 * Default mode opens the Swing GUI. Use --cli to run the legacy command-line
 * demo flow.
 */
public class RunEXPMCS {

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            runLegacyCliDemo();
            return;
        }
        ExpMcsGui.launch();
    }

    public static mcData runSimulation(String phaseName, double temperature, double xB,
            int latticeSize, int latticeType, int mcss, int warmup, boolean printToFile)
            throws IOException {

        PHASEMCBINCE phase = createPhase(phaseName, temperature, xB, latticeSize, latticeType);
        phase.printPhaseInfo();

        mcSampler sampler = new mcSampler(mcss, warmup, phase);
        sampler.printSamplerInfo();

        mcData data = sampler.runMC();
        data.calStat();
        data.printStat(printToFile);
        return data;
    }

    public static PHASEMCBINCE createPhase(String phaseName, double temperature, double xB,
            int latticeSize, int latticeType) throws IOException {

        String normalized = phaseName.toUpperCase();

        if ("A2".equals(normalized)) {
            double[] eBCC = {-1.0, 0.0, 0.0, 0.0, 0.0};
            return new A2MCBINCE(eBCC, temperature, xB, latticeSize, latticeType);
        }
        if ("A1".equals(normalized)) {
            double[] eFCC = {-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            return new A1MCBINCE(eFCC, temperature, xB, latticeSize, latticeType);
        }
        if ("L12".equals(normalized)) {
            double[] eFCC = {1.0, -0.001, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            return new L12MCBINCE(eFCC, temperature, xB, latticeSize, latticeType);
        }
        if ("L10".equals(normalized)) {
            double[] eFCC = {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            return new L10MCBINCE(eFCC, temperature, xB, latticeSize, latticeType);
        }

        throw new IllegalArgumentException("Unsupported phase: " + phaseName);
    }

    private static void runLegacyCliDemo() throws IOException {
        long beg = System.currentTimeMillis();
        runSimulation("L10", 1.73, 0.5, 32, 2, 4000, 1000, true);
        long end = System.currentTimeMillis();
        System.out.println("#MC run took " + (double) (end - beg) / 1000 + " sec");
    }
}
