/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package phase;

import java.io.IOException;

/**
 *
 * @author JEDIABJ77
 */
public interface PHASEBINCE {

    //set methods
    public void setEdis(double[] ei_In);

    public void setT(double T_local) throws IOException;

    public void setX(double x0_local) throws IOException;

    public String getPhaseTag();

    public double calHmc() throws IOException, ArithmeticException;

    public void printPhaseInfo() throws IOException;
//public void calDTce(double[] dyda_local,double[] moddata, int i)throws IOException;
}
