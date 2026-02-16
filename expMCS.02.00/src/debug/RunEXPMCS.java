package debug;

import java.io.IOException;
import mcSampler.mcData;
import mcSampler.mcSampler;
import phase.mc.BCC.A2MCBINCE;
import phase.mc.BCC.BCCMCBINCE;
import phase.mc.FCC.A1MCBINCE;
import phase.mc.FCC.FCCMCBINCE;
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

    private static final double[] DEFAULT_EBCC = {-1.0, 0.0, 0.0, 0.0, 0.0};
    private static final double[] DEFAULT_EFCC_A1 = {-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static final double[] DEFAULT_EFCC_L10 = {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static final double[] DEFAULT_EFCC_L12 = {1.0, -0.001, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

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
        return runSimulation(phaseName, defaultEcdisForPhase(phaseName), temperature, xB,
                latticeSize, latticeType, mcss, warmup, printToFile);
    }

    public static mcData runSimulation(String phaseName, double[] ecdis, double temperature, double xB,
            int latticeSize, int latticeType, int mcss, int warmup, boolean printToFile)
            throws IOException {
        PHASEMCBINCE phase = createPhase(phaseName, ecdis, temperature, xB, latticeSize, latticeType);
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
        return createPhase(phaseName, defaultEcdisForPhase(phaseName), temperature, xB, latticeSize, latticeType);
    }

    public static PHASEMCBINCE createPhase(String phaseName, double[] ecdis, double temperature, double xB,
            int latticeSize, int latticeType) throws IOException {
        String normalized = phaseName.toUpperCase();
        validateEcdisSize(normalized, ecdis);
        if ("A2".equals(normalized)) {
            return new A2MCBINCE(ecdis, temperature, xB, latticeSize, latticeType);
        }
        if ("A1".equals(normalized)) {
            return new A1MCBINCE(ecdis, temperature, xB, latticeSize, latticeType);
        }
        if ("L12".equals(normalized)) {
            return new L12MCBINCE(ecdis, temperature, xB, latticeSize, latticeType);
        }
        if ("L10".equals(normalized)) {
            return new L10MCBINCE(ecdis, temperature, xB, latticeSize, latticeType);
        }
        throw new IllegalArgumentException("Unsupported phase: " + phaseName);
    }

    public static int expectedEcdisSizeForPhase(String phaseName) {
        String normalized = phaseName.toUpperCase();
        if ("A2".equals(normalized)) {
            return BCCMCBINCE.getEcdisSize();
        }
        if ("A1".equals(normalized) || "L10".equals(normalized) || "L12".equals(normalized)) {
            return FCCMCBINCE.getEcdisSize();
        }
        throw new IllegalArgumentException("Unsupported phase: " + phaseName);
    }

    public static double[] defaultEcdisForPhase(String phaseName) {
        String normalized = phaseName.toUpperCase();
        if ("A2".equals(normalized)) {
            return DEFAULT_EBCC.clone();
        }
        if ("A1".equals(normalized)) {
            return DEFAULT_EFCC_A1.clone();
        }
        if ("L12".equals(normalized)) {
            return DEFAULT_EFCC_L12.clone();
        }
        if ("L10".equals(normalized)) {
            return DEFAULT_EFCC_L10.clone();
        }
        throw new IllegalArgumentException("Unsupported phase: " + phaseName);
    }

    private static void validateEcdisSize(String phaseName, double[] ecdis) {
        int expected = expectedEcdisSizeForPhase(phaseName);
        if (ecdis == null || ecdis.length != expected) {
            throw new IllegalArgumentException("Invalid ecdis size for phase " + phaseName
                    + ". Expected " + expected + " values.");
        }
    }

    private static void runLegacyCliDemo() throws IOException {
        long beg = System.currentTimeMillis();
        runSimulation("L10", defaultEcdisForPhase("L10"), 1.73, 0.5, 32, 2, 4000, 1000, true);
        long end = System.currentTimeMillis();
        System.out.println("#MC run took " + (double) (end - beg) / 1000 + " sec");
    }
}
