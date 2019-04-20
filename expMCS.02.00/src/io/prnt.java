package io;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @author: Shivam Updated: SU: 11.02.2012 : added method list() for printing 2D
 * boolean array
 *
 */
public class prnt {

    public static int pr2F = 3;
    public static int logLevelParam = 0;
    public static String nl = System.getProperty("line.separator");
    static DecimalFormat df = new DecimalFormat("#.######");
    public static char nt = '\t';
    //public static final int pr2F = 1;// 0 for nothing, 1 for console, 2 for file, 3 for both console & file

    public static void write(String str, int loglevel) throws IOException {
        if (loglevel <= logLevelParam) {
            System.out.print(str);
        }
    }// closed Method writeln()

    public static void write(String str) throws IOException {
        prnt.write(str, 0);// If no loglevel is given then priority is 0
    }

    public static void write(String str, String infile) throws IOException {
        try (FileWriter fl = new FileWriter(infile, true)) {
            fl.write(str);
        }
    }//

    public static void writeln(String str, int loglevel) throws IOException {
        prnt.write(str + nl, loglevel);
    }// closed Method writeln() 1

    public static void writeln(int loglevel) throws IOException {
        prnt.write(nl, loglevel);
    }// closed Method writeln() 2

    public static void writeln(String str) throws IOException {
        prnt.write(str + nl, 0);// If no loglevel is given then priority is 0
    }// closed Method writeln() 1

    public static void writeln(String str, String infile) throws IOException {
        prnt.write(str + nl, infile);// If no loglevel is given then priority is 0
    }

    public static void writeln() throws IOException {
        prnt.write(nl, 0);// If no loglevel is given then priority is 0
    }// closed Method writeln() 2

// 0D List Printing
    public static void list(int array, String arrayname) throws IOException {//vj-2012-07-09//for loglevel 0 printing
        prnt.write(arrayname + ":" + array, 0);
        prnt.writeln(0);
    }

    public static void list(int array, String arrayname, String infile) throws IOException {//vj-2012-12-26
        prnt.writeln(arrayname + ":" + array, infile);
    }

    public static void list(double array, String arrayname) throws IOException {//vj-2012-07-09//for loglevel 0 printing
        prnt.write(arrayname + ":" + array, 0);
        prnt.writeln(0);
    }

    public static void list(double array, String arrayname, String infile) throws IOException {//vj-2012-12-26
        prnt.writeln(arrayname + ":" + array, infile);
    }

    public static void list(String array, String arrayname) throws IOException {//vj-2012-07-09//for loglevel 0 printing
        prnt.write(arrayname + ":" + array, 0);
        prnt.writeln(0);
    }

    public static void list(String array, String arrayname, String infile) throws IOException {//vj-2012-12-26
        prnt.writeln(arrayname + ":" + array, infile);
    }
// 1D list Printing Starts

    public static void list(boolean[] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(1D) (boolean) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.write(" " + array[i], loglevel);
        }
        prnt.writeln(loglevel);
    }

    public static void list(char[] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(1D) (char) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.write(" " + array[i], loglevel);
        }
        prnt.writeln(loglevel);
    }

    public static void list(double[] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(1D) (double) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.write(" " + array[i], loglevel);
        }
        prnt.writeln(loglevel);
    }

    public static void list(double[] array, String arrayname) throws IOException {//vj-2012-07-09//for loglevel 0 printing
        prnt.write(arrayname + ":" + "{", 0);
        for (int i = 0; i < array.length - 1; i++) {
            prnt.write(array[i] + ",", 0);
        }
        prnt.write(array[array.length - 1] + "}", 0);
        prnt.writeln(0);
    }

    public static void list(double[] array, String arrayname, String infile) throws IOException {//vj-2012-12-06
        DecimalFormat df88 = new DecimalFormat("#.######");
        prnt.write(arrayname + ":" + "{", infile);
        for (int i = 0; i < array.length - 1; i++) {
            prnt.write(df88.format(array[i]) + ",", infile);
        }
        prnt.writeln(df88.format(array[array.length - 1]) + "}", infile);
    }

    public static void list(int[] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(1D) (int) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.write(" " + array[i], loglevel);
        }
        prnt.writeln(loglevel);
    }

    public static void list(int[] array, String arrayname) throws IOException {//vj-2012-07-09//for loglevel 0 printing
        prnt.write(arrayname + ":" + "{", 0);
        for (int i = 0; i < array.length - 1; i++) {
            prnt.write(array[i] + ",", 0);
        }
        prnt.write(array[array.length - 1] + "}", 0);
        prnt.writeln(0);
    }

    public static void list(int[] array, String arrayname, String infile) throws IOException {//vj-2012-12-06
        prnt.write(arrayname + ":" + "{", infile);
        for (int i = 0; i < array.length - 1; i++) {
            prnt.write(array[i] + ",", infile);
        }
        prnt.writeln(array[array.length - 1] + "}", infile);
    }

    public static void list(String[] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(1D) (String) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.write(" " + array[i], loglevel);
        }
        prnt.writeln(loglevel);
    }
// 2D list Printing Starts

    public static void list(boolean[][] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(2D) (boolean) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                prnt.write("  " + array[i][j], loglevel);
            }
            prnt.writeln(loglevel);
        }
    }

    public static void list(double[][] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(2D) (double) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                prnt.write("  " + array[i][j], loglevel);
            }
            prnt.writeln(loglevel);
        }
    }

    public static void list(double[][] array, String arrayname) throws IOException {//vj-2012-07-09//for loglevel 0 printing
        prnt.write(arrayname + ":", 0);
        prnt.write("{", 0);
        for (int i = 0; i < array.length - 1; i++) {
            prnt.write("{", 0);
            for (int j = 0; j < array[i].length - 1; j++) {
                prnt.write(array[i][j] + ",", 0);
            }
            prnt.write(array[i][array[i].length - 1] + "}", 0);
            prnt.write(",", 0);
        }
        prnt.write("{", 0);
        for (int j = 0; j < array[array.length - 1].length - 1; j++) {
            prnt.write(array[array.length - 1][j] + ",", 0);
        }
        prnt.write(array[array.length - 1][array[array.length - 1].length - 1] + "}", 0);
        prnt.write("}", 0);
        prnt.writeln(0);
    }

    public static void list(double[][] array, String arrayname, String infile) throws IOException {//vj-2012-07-09//for loglevel 0 printing
        prnt.write(arrayname + ":", infile);
        prnt.write("{", infile);
        for (int i = 0; i < array.length - 1; i++) {
            prnt.write("{", infile);
            for (int j = 0; j < array[i].length - 1; j++) {
                prnt.write(array[i][j] + ",", infile);
            }
            prnt.write(array[i][array[i].length - 1] + "}", infile);
            prnt.write(",", infile);
        }
        prnt.write("{", infile);
        for (int j = 0; j < array[array.length - 1].length - 1; j++) {
            prnt.write(array[array.length - 1][j] + ",", infile);
        }
        prnt.write(array[array.length - 1][array[array.length - 1].length - 1] + "}", infile);
        prnt.writeln("}", infile);
    }

    public static void list(int[][] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(2D) (int) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                prnt.write("  " + array[i][j], loglevel);
            }
            prnt.writeln(loglevel);
        }
    }

    public static void list(int[][] array, String arrayname) throws IOException {//vj-2012-07-09//for loglevel 0 printing
        prnt.write(arrayname + ":", 0);
        prnt.write("{", 0);
        for (int i = 0; i < array.length - 1; i++) {
            prnt.write("{", 0);
            for (int j = 0; j < array[i].length - 1; j++) {
                prnt.write(array[i][j] + ",", 0);
            }
            prnt.write(array[i][array[i].length - 1] + "}", 0);
            prnt.write(",", 0);
        }
        prnt.write("{", 0);
        for (int j = 0; j < array[array.length - 1].length - 1; j++) {
            prnt.write(array[array.length - 1][j] + ",", 0);
        }
        prnt.write(array[array.length - 1][array[array.length - 1].length - 1] + "}", 0);
        prnt.write("}", 0);
        prnt.writeln(0);
    }

    public static void list(String[][] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(2D) (String) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                prnt.write("  " + array[i][j], loglevel);
            }
            prnt.writeln(loglevel);
        }
    }

// 3D list Printing Starts
    public static void list(double[][][] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(3D) (double) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.writeln("3D Iterator " + i + " :", loglevel);
            for (int j = 0; j < array[i].length; j++) {
                for (int k = 0; k < array[i][j].length; k++) {
                    prnt.write(" " + array[i][j][k], loglevel);
                }
                prnt.writeln(loglevel);
            }
            prnt.writeln(loglevel);
        }
    }

    public static void list(int[][][] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(3D) (int) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.writeln("3D Iterator " + i + " :", loglevel);
            for (int j = 0; j < array[i].length; j++) {
                for (int k = 0; k < array[i][j].length; k++) {
                    prnt.write(" " + array[i][j][k], loglevel);
                }
                prnt.writeln(loglevel);
            }
            prnt.writeln(loglevel);
        }
    }

    public static void list(String[][][] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(3D) (String) " + arrayname + " : ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.writeln("3D Iterator " + i + " :", loglevel);
            for (int j = 0; j < array[i].length; j++) {
                for (int k = 0; k < array[i][j].length; k++) {
                    prnt.write(" " + array[i][j][k], loglevel);
                }
                prnt.writeln(loglevel);
            }
            prnt.writeln(loglevel);
        }
    }
// 4D list Printing Starts

    public static void list(double[][][][] array, String arrayname, int loglevel) throws IOException {
        prnt.writeln("(4D) (double) " + arrayname + " : ", loglevel);
        prnt.writeln("{ ", loglevel);
        for (int i = 0; i < array.length; i++) {
            prnt.write(" {", loglevel);
            for (int j = 0; j < array[i].length; j++) {
                prnt.write("  {", loglevel);
                for (int k = 0; k < array[i][j].length; k++) {
                    prnt.write(" {", loglevel);
                    for (int l = 0; l < array[i][j][k].length; l++) {
                        prnt.write(array[i][j][k][l] + ", ", loglevel);
                    }// l loop closed
                    prnt.write("},", loglevel);
                }// k loop closed
                prnt.writeln(" }", loglevel);
            }// j loop closed
            prnt.writeln(" }", loglevel);
        }// i loop closed
        prnt.writeln("}//4D closed", loglevel);
    }

    public static void drawLine(String outfile) throws IOException {
        writeln("===============================================================", outfile);
    }

    public static void line() throws IOException {
        prnt.writeln("===============================================================");
    }
}
