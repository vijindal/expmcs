/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mcSampler;

/**
 *
 * @author metallurgy
 */
public class statsFCC {

    int noCorrFunc;
    int noDataPoints;
    double[] avgCorrFunc;
    public double[] errorCorrFunc;
    double[][] corrFunc;

    public statsFCC(double[][] corrFunc) {
        this.noCorrFunc = corrFunc[1].length;
        this.noDataPoints = corrFunc.length;
        this.avgCorrFunc = new double[noCorrFunc];
        this.errorCorrFunc = new double[noCorrFunc];
        this.corrFunc = corrFunc;
        //System.out.println("noCorrFunc:"+noCorrFunc+" ,noDataPoints:"+noDataPoints);
    }

    public double[] meanCorrFunc() {
        for (int j1 = 1; j1 < noCorrFunc; j1++) {
            avgCorrFunc[j1] = 0;
            errorCorrFunc[j1] = 0;
            for (int i1 = 0; i1 < noDataPoints; i1++) {
                avgCorrFunc[j1] = avgCorrFunc[j1] + corrFunc[i1][j1];
                //System.out.println(corrFunc[i1][j1]+","+avgCorrFunc[j1]);
            }
            avgCorrFunc[j1] = avgCorrFunc[j1] / noDataPoints;
            for (int i1 = 0; i1 < noDataPoints; i1++) {
                errorCorrFunc[j1] = errorCorrFunc[j1] + Math.pow((corrFunc[i1][j1] - avgCorrFunc[j1]), 2);
            }
            errorCorrFunc[j1] = Math.sqrt(errorCorrFunc[j1] / (noDataPoints * (noDataPoints - 1)));
            System.out.println("u[" + j1 + "]=" + avgCorrFunc[j1] + ", error=" + errorCorrFunc[j1]);
        }
        return (avgCorrFunc);
    }
}