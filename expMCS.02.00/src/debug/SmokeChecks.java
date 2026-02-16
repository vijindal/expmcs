package debug;

import java.io.IOException;
import phase.mc.BCC.A2MCBINCE;
import phase.mc.FCC.A1MCBINCE;
import phase.mc.PHASEMCBINCE;

/**
 * Lightweight sanity checks for core lattice/index/correlation invariants.
 */
public class SmokeChecks {

    public static void main(String[] args) throws IOException {
        runA1Checks();
        runA2Checks();
        System.out.println("Smoke checks passed.");
    }

    private static void runA1Checks() throws IOException {
        double[] eFCC = {-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        PHASEMCBINCE phase = new A1MCBINCE(eFCC, 1.5, 0.5, 4, 1);

        int size = phase.getLatticeSize();
        int nlp = phase.calNLP(size);
        assertTrue(nlp == 32, "A1/FCC expected NLP=32 for size=4, got " + nlp);

        boolean[][][] visited = new boolean[size][size][size];
        for (int idx = 0; idx < nlp; idx++) {
            int[] c = phase.siteIndexTositeCoordinate(size, idx);
            assertCoordinateBounds(c, size, "A1 index " + idx);
            assertTrue(((c[0] + c[1] + c[2]) % 2) == 0,
                    "A1 coordinate parity mismatch at index " + idx);
            assertTrue(!visited[c[0]][c[1]][c[2]],
                    "A1 duplicate coordinate for index " + idx);
            visited[c[0]][c[1]][c[2]] = true;
        }

        double[] u = phase.calU();
        assertTrue(u.length == phase.getTcf(), "A1 correlation length mismatch");
        assertRange(u, -1.0, 1.0, "A1 correlation out of range");
    }

    private static void runA2Checks() throws IOException {
        double[] eBCC = {-1.0, 0.0, 0.0, 0.0, 0.0};
        PHASEMCBINCE phase = new A2MCBINCE(eBCC, 2.0, 0.5, 4, 1);

        int size = phase.getLatticeSize();
        int nlp = phase.calNLP(size);
        assertTrue(nlp == 16, "A2/BCC expected NLP=16 for size=4, got " + nlp);

        boolean[][][] visited = new boolean[size][size][size];
        for (int idx = 0; idx < nlp; idx++) {
            int[] c = phase.siteIndexTositeCoordinate(size, idx);
            assertCoordinateBounds(c, size, "A2 index " + idx);
            boolean allEven = (c[0] % 2 == 0) && (c[1] % 2 == 0) && (c[2] % 2 == 0);
            boolean allOdd = (c[0] % 2 == 1) && (c[1] % 2 == 1) && (c[2] % 2 == 1);
            assertTrue(allEven || allOdd, "A2 coordinate parity mismatch at index " + idx);
            assertTrue(!visited[c[0]][c[1]][c[2]],
                    "A2 duplicate coordinate for index " + idx);
            visited[c[0]][c[1]][c[2]] = true;
        }

        double[] u = phase.calU();
        assertTrue(u.length == phase.getTcf(), "A2 correlation length mismatch");
        assertRange(u, -1.0, 1.0, "A2 correlation out of range");
    }

    private static void assertCoordinateBounds(int[] c, int size, String label) {
        assertTrue(c.length == 3, label + " expected 3D coordinate");
        assertTrue(c[0] >= 0 && c[0] < size, label + " x out of range");
        assertTrue(c[1] >= 0 && c[1] < size, label + " y out of range");
        assertTrue(c[2] >= 0 && c[2] < size, label + " z out of range");
    }

    private static void assertRange(double[] values, double lo, double hi, String message) {
        for (int i = 0; i < values.length; i++) {
            double v = values[i];
            assertTrue(v >= lo - 1.0E-12 && v <= hi + 1.0E-12,
                    message + " at index " + i + ": " + v);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
