import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import java.util.Iterator;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class Image {
    private int width;
    private int height;
    private BufferedImage bufferedImage;
    private double[][] energyMatrix;
    private double[][] horizontalEnergyMatrix;
    private int[][][] rgbArray;
    public int[][][] currentRGBArray;
    private double[][] verticalWeights;
    //private int[][][] horizontalRGBArray;

    public void setCurrentRGBArray(int[][][] currentRGBArray) {
        this.currentRGBArray = currentRGBArray;
    }

    //Constructs an image object
    public Image(String imageFilePath) throws IOException {

        bufferedImage = ImageIO.read(getClass().getResource(imageFilePath));
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        rgbArray = bufferedImageToRGBArray();
        currentRGBArray = rgbArray;
        //energyMatrix = createEnergyMatrix(currentRGBArray);
        //verticalWeights = initializeWeights(energyMatrix);
    }

    public BufferedImage carveImage(int horizontalSeams, int verticalSeams) {
        if (horizontalSeams > 0 && verticalSeams > 0) {
            double[][]verticalEnergyMatrix = createEnergyMatrix(currentRGBArray);
            currentRGBArray = removeSeams(verticalSeams,verticalEnergyMatrix,currentRGBArray);
            System.out.println("Horizontal is done");
            int[][][] horizontalRGBArray = transposeArray(currentRGBArray);
            System.out.println("vertical x = "+currentRGBArray.length+", y = "+currentRGBArray[0].length);

            double[][] horizontalEnergyMatrix = createEnergyMatrix(horizontalRGBArray);
            horizontalRGBArray = removeSeams(horizontalSeams,horizontalEnergyMatrix,horizontalRGBArray);
            currentRGBArray = transposeArray(horizontalRGBArray);
            System.out.println("horizontal x = "+horizontalRGBArray.length+", y = "+horizontalRGBArray[0].length);
            return RGBArrayToImage();
        } /*else if (verticalSeams > 0) {
            //horizontalSeams = this.removeSeams()
        } else if (horizontalSeams > 0) {

        }*/
        return null;

    }

    public static int[][][] transposeArray(int[][][] arrayToTranspose) {
        int maxX = arrayToTranspose.length;
        int maxY = arrayToTranspose[0].length;
        int[][][] newArray = new int[maxY][maxX][3];
        for (int x = 0; x < maxY; x++) {
            for (int y = 0; y < maxX; y++) {
                newArray[x][y] = arrayToTranspose[y][x];
            }
        }
        return newArray;
    }

    public static double[][] transposeArray(double[][] arrayToTranspose) {
        int maxX = arrayToTranspose.length;
        int maxY = arrayToTranspose[0].length;
        double[][] newArray = new double[maxY][maxX];
        for (int x = 0; x < maxY; x++) {
            for (int y = 0; y < maxX; y++) {
                newArray[x][y] = arrayToTranspose[y][x];
            }
        }
        return newArray;
    }

    public int[][][] removeSeams(int numberOfSeams, double[][] energyMatrix, int[][][] rgbArray) {
        double[][] weightArray = initializeWeights(energyMatrix);
        double[][] currentEnergyMatrix = energyMatrix;
        Queue<int[]> seamsToRemove = new LinkedList<>();
        for (int k = 0; k < numberOfSeams; k++) {
            int yLength = weightArray[0].length;
            int xLength = weightArray.length;
            int currentX = 0;
            int[] seam = new int[yLength];
            double lowestWeight = Double.MAX_VALUE;
            for (int i = 0; i < xLength; i++) {
                if (weightArray[i][yLength - 1] <= lowestWeight) {
                    lowestWeight = weightArray[i][yLength - 1];
                    currentX = i;
                }
            }
            for (int y = yLength - 1; y >= 0; y--) {
                lowestWeight = Double.MAX_VALUE;
                int marker = currentX;
                if (marker == 0) {
                    marker = 1;
                } else if (marker == weightArray.length - 1) {
                    marker = weightArray.length - 2;
                }
                for (int x = marker - 1; x <= marker + 1; x++) {
                    if (weightArray[(x + xLength) % xLength][y] <= lowestWeight) {
                        currentX = (x + xLength) % xLength;
                        lowestWeight = weightArray[(x + xLength) % xLength][y];
                        seam[y] = (x + xLength) % xLength;
                    }
                }
            }//0 = vertical 1 = horizontal
            rgbArray = updateCurrentRGBArray(seam,rgbArray);
           // System.out.println("RGBARRAY width = "+rgbArray.length+" height = "+rgbArray[0].length);
            currentEnergyMatrix = this.updateCurrentEnergyMatrix(seam,currentEnergyMatrix,rgbArray);
            //System.out.println("Energy array width = "+energyMatrix.length+" height = "+energyMatrix[0].length);
            weightArray = initializeWeights(currentEnergyMatrix);
            //System.out.println("Weight array width = "+weightArray.length+" height = "+weightArray[0].length);
            //seamsToRemove.add(seam);
        }
        return rgbArray;
    }

    public static double[][] removeSeam(int[] seam, double[][] inputArray) {
        double[][] outputArray = new double[inputArray.length - 1][inputArray[0].length];
        for (int y = 0; y < inputArray[0].length; y++) {
            int offset = 0;
            for (int x = 0; x < inputArray.length; x++) {
                if (x != seam[y]) {
                    outputArray[x + offset][y] = inputArray[x][y];
                } else {
                    offset--;
                }
            }
        }
        return outputArray;
    }

    public static int[][][] removeSeam(int[] seam, int[][][] inputArray) {
        int[][][] outputArray = new int[inputArray.length - 1][inputArray[0].length][3];
        for (int y = 0; y < inputArray[0].length; y++) {
            int offset = 0;
            for (int x = 0; x < inputArray.length; x++) {
                if (x != seam[y]) {
                    //System.out.println(x+offset);
                    //System.out.println("("+seam[y]+","+y+")");
                    outputArray[x + offset][y] = inputArray[x][y];
                } else {
                    //System.out.println("Changing offset at x="+x+"y="+y);
                    offset--;
                }
            }
        }
        return outputArray;
    }

    public double[][] updateCurrentEnergyMatrix(int[] seam,double[][] energyMatrix,int[][][] carvedRGB) {
        //int[][][] carvedRGB = SeamCarver.removeSeam(seam,currentRGBArray);
        System.out.println(energyMatrix.length);
        energyMatrix = removeSeam(seam,energyMatrix);

        for (int y = 0; y < energyMatrix[0].length; y++) {
            for (int x = 0; x < energyMatrix.length; x++) {
                if (seam[y]==x) {
                    energyMatrix[x][y] = getCellEnergy(carvedRGB,x,y);
                    if(x>0) {
                        energyMatrix[x - 1][y] = getCellEnergy(carvedRGB, x - 1, y);
                    }
                }
            }
        }
        System.out.println("1"+energyMatrix.length);
        return energyMatrix;
    }

    public int[][][] updateCurrentRGBArray(int[] seam,int[][][] RGBArray) {
        int[][][] currentRGBArray = RGBArray;
        int[][][] newRGBArray = new int[currentRGBArray.length-1][currentRGBArray[0].length][3];
        for (int y = 0; y < currentRGBArray[0].length; y++) {
            int offset = 0;
            for (int x = 0; x < currentRGBArray.length; x++) {
                if (x != seam[y]) {
                    newRGBArray[x+offset][y] = currentRGBArray[x][y];
                } else {
                    offset--;
                }
            }
        }
        return newRGBArray;
    }

    public static double[][] initializeWeights(double[][] energyMatrix) {
        double[][] weightArray = new double[energyMatrix.length][energyMatrix[0].length];
        int maxX = weightArray.length;
        int maxY = weightArray[0].length;
        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {
                if (y == 0) {
                    weightArray[x][y] = energyMatrix[x][y];
                } else {
                    double[] choices = {weightArray[((x - 1) + maxX) % maxX][y - 1],
                            weightArray[x][y - 1],
                            weightArray[((x + 1) + maxX) % maxX][y - 1]};
                    Arrays.sort(choices);
                    weightArray[x][y] = (choices[0] + energyMatrix[x][y]);
                }
                //System.out.print("("+x+","+y+")="+weightArray[x][y]+",");
            }
            //System.out.println("");
        }
        return weightArray;
    }

    //public static double[][] updateWeights(double[][] energyMatrix);

    public int[][][] bufferedImageToRGBArray() {
        int[][][] outputRGBarray = new int[bufferedImage.getWidth()][bufferedImage.getHeight()][3];
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                outputRGBarray[x][y] = getPixelColours(x,y);
            }
        }
        return outputRGBarray;
    }

    //Returns an array with the RGB in that order of a pixel (x,y)
    private int[] getPixelColours(int x,int y) {
        int[] rgbArray = new int[3];
        //.getRGB returns the colour in an ARGB format so we have to manipulate argbColour to get the individual RGB
        int argbColour = bufferedImage.getRGB(x,y);
        //For all of the colours we use bitwise and and then bitshift the value by an amount so that the individual RGB
        //colours can be isolated in each pixel
        rgbArray[0] = (argbColour & 0x00ff0000) >>16; //Red
        rgbArray[1] = (argbColour & 0x0000ff00) >>8; //Green
        rgbArray[2] = (argbColour & 0x000000ff); //Blue
        return rgbArray;
    }


    //Returns an energy matrix as a 2D array of double values
    public double[][] createEnergyMatrix(int[][][] RGBArray) {
        int yLength = RGBArray[0].length;
        int xLength = RGBArray.length;
        double[][] energyArray;

        energyArray = new double[xLength][yLength];
        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                energyArray[x][y] = getCellEnergy(RGBArray,x,y);
            }
        }
        return energyArray;
    }

    private double getCellEnergy(int[][][] RGBArray,int x,int y) {
        int yLength = RGBArray[0].length;
        int xLength = RGBArray.length;
        double energyX;
        double energyY;

        int l = (x - 1 + xLength)%(xLength);
        int r = (x + 1 + xLength)%(xLength);
        int a = (y + 1 + yLength)%(yLength);
        int b = (y - 1 + yLength)%(yLength);

        //These statements deal with the edges of the array
        int[] localRGBleft = RGBArray[l][y];
        int[] localRGBright = RGBArray[r][y];
        int[] localRGBabove = RGBArray[x][b];
        int[] localRGBbelow = RGBArray[x][a];




        //The difference between the left and right pixel are squared and added together
        //This value is then added to the difference between the above and below pixel and averaged
        energyX = (
                (localRGBright[0] - localRGBleft[0])*(localRGBright[0] - localRGBleft[0]) +
                        (localRGBright[2] - localRGBleft[2])*(localRGBright[2] - localRGBleft[2]) +
                        (localRGBright[1] - localRGBleft[1])*(localRGBright[1] - localRGBleft[1]));
        energyY = (
                (localRGBabove[0] - localRGBbelow[0])*(localRGBabove[0] - localRGBbelow[0]) +
                        (localRGBabove[2] - localRGBbelow[2])*(localRGBabove[2] - localRGBbelow[2]) +
                        (localRGBabove[1] - localRGBbelow[1])*(localRGBabove[1] - localRGBbelow[1]));
        return Math.sqrt(energyX + energyY);
    }

    public double[][] getEnergyMatrix() {
        return energyMatrix;
    }

    private int RGBToARBG (int[] inputRGB) {
        int ARGBOut = 0xff000000 | (inputRGB[0] << 16) | (inputRGB[1] << 8) | (inputRGB[2]);
        return ARGBOut;
    }

    public BufferedImage RGBArrayToImage() {
        BufferedImage outputImage = new BufferedImage(currentRGBArray.length,currentRGBArray[0].length,TYPE_4BYTE_ABGR);
        for (int y = 0; y < currentRGBArray[0].length; y++) {
            for (int x = 0; x < currentRGBArray.length; x++) {
                outputImage.setRGB(x,y,RGBToARBG(currentRGBArray[x][y]));
            }
        }
        return outputImage;
    }

    public static void outputEnergyMatrix(double[][] imageArray) {
        //System.out.println(imageArray.length);
        //System.out.println(imageArray[0].length);
        BufferedImage imgOut = new BufferedImage(imageArray.length,imageArray[0].length,BufferedImage.TYPE_BYTE_GRAY);
        File outFile;
        for (int y=0; y < imageArray[0].length; y++) {
            for (int x=0; x < imageArray.length; x++) {
                byte pixelColour = (byte) imageArray[x][y];
                int pixelColourInt = Color.HSBtoRGB(0, 0, pixelColour);
                pixelColourInt *= 150;
                if (imageArray[x][y] == 0 || pixelColourInt > 0) {
                    pixelColourInt = 0;
                }
                pixelColourInt *= -1;
                imgOut.setRGB(x,y,pixelColourInt);
            }
        }
        try{
           outFile = new File ("CS1006P2/out/outputImage.png");
           ImageIO.write(imgOut,"png",outFile);
        } catch (IOException e) {
            System.out.println("Error:" + e);
        }
    }


    /*private int RGBToARBG (int[] inputRGB) {
        int ARGBOut = 0xff000000 | (inputRGB[0] << 16) | (inputRGB[1] << 8) | (inputRGB[2]);
        return ARGBOut;
    }*/

    /*public BufferedImage RGBArrayToImage() {
        BufferedImage outputImage = new BufferedImage(currentRGBArray.length,currentRGBArray[0].length,TYPE_4BYTE_ABGR);
        for (int y = 0; y < currentRGBArray[0].length; y++) {
            for (int x = 0; x < currentRGBArray.length; x++) {
                outputImage.setRGB(x,y,RGBToARBG(currentRGBArray[x][y]));
            }
        }
        return outputImage;
    }*/

    /*private BufferedImage imageArrayToImage(int[][] imageArray) {
        BufferedImage outputImage = new BufferedImage(imageArray.length,imageArray[0].length,BufferedImage.TYPE_4BYTE_ABGR);
        for (int y = 0; y <imageArray[0].length; y++) {
            for (int x = 0; x < imageArray.length; x++) {
                outputImage.setRGB(x,y,imageArray[x][y]);
            }
        }
        return outputImage;
    }*/



    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return  height;
    }

    /*public void printRGBArray () {
        for (int y = 0; y < currentRGBArray[0].length; y++) {
            for (int x = 0; x < currentRGBArray.length; x++) {
                //System.out.println(String.format("0x%08X",RGBToARBG(currentRGBArray[x][y])));
            }
            //System.out.println("");
        }
    }*/

}