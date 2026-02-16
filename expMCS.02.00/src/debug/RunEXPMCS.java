/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author metallurgy
 */
public class RunEXPMCS {

    public static void main(String[] args) throws IOException {
        long beg = System.currentTimeMillis();
        double xB = 0.5;
        for (int i = 0; i < 1; i++) {
            debugRunL10(1.73, xB);
            xB = xB + 0.01;
        }
        long end = System.currentTimeMillis();
        System.out.println("#MC run took " + (double) (end - beg) / 1000 + " sec");
    }

    private static void debugRunA2() throws IOException {
        int size = 32;
        int latticeType = 1;
        //double eBCC[] = {-250.0, -166.6666667, 0.0, 25.0, 0.0};
        //double R = 8.3144;
        //double T = 284.0;
        double eBCC[] = {-1.0, 0.0, 0.0, 0.0, 0.0};
        double R = 1.0;
        double T = 6.20;
        double xB = 0.5;
        PHASEMCBINCE a2mcbince = new A2MCBINCE(eBCC, T, xB, size, latticeType);
        a2mcbince.printPhaseInfo();
        mcSampler mcsampler = new mcSampler(4000, 1000, a2mcbince);
        mcsampler.printSamplerInfo();
        mcData mcdata = mcsampler.runMC();
        mcdata.calStat();
        mcdata.printStat(true);
    }

    private static void debugRunA1(double T_In, double x_In) throws IOException {
        int size = 32;
        int latticeType = 1;
        double eFCC[] = {-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double R = 1.0;
        double T = T_In;
        double xB = x_In;
        PHASEMCBINCE a1mcbince = new A1MCBINCE(eFCC, T, xB, size, latticeType);
        a1mcbince.printPhaseInfo();
        mcSampler mcsampler = new mcSampler(4000, 1000, a1mcbince);
        mcsampler.printSamplerInfo();
        mcData mcdata = mcsampler.runMC();
        mcdata.calStat();
        mcdata.printStat(true);
    }

    private static void debugRunL10(double T_In, double x_In) throws IOException {
        int size = 32;
        int latticeType = 2;
        double eFCC[] = {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double R = 1.0;
        double T = T_In;
        double xB = x_In;
        PHASEMCBINCE l10mcbince = new L10MCBINCE(eFCC, T, xB, size, latticeType);
        l10mcbince.printPhaseInfo();
        mcSampler mcsampler = new mcSampler(100, 100, l10mcbince);
        //mcSampler mcsampler = new mcSampler(4000, 1000, l10mcbince);
        mcsampler.printSamplerInfo();
        mcData mcdata = mcsampler.runMC();
        mcdata.calStat();
        mcdata.printStat(true);
    }

    private static void debugRunL12(double T_In, double x_In) throws IOException {
        int size = 32;
        int latticeType = 1;
        double eFCC[] = {1.0, -0.001, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double R = 1.0;
        double T = T_In;
        double xB = x_In;
        PHASEMCBINCE l12mcbince = new L12MCBINCE(eFCC, T, xB, size, latticeType);
        l12mcbince.printPhaseInfo();
        mcSampler mcsampler = new mcSampler(4000, 1000, l12mcbince);
        mcsampler.printSamplerInfo();
        mcData mcdata = mcsampler.runMC();
        mcdata.calStat();
        mcdata.printStat(true);
    }
}
