import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.util.Arrays;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

public class Image {
    private int width;
    private int height;
    private BufferedImage bufferedImage;
    private double[][] energyMatrix;
    private int[][][] rgbArray;
    private int[][][] currentRGBArray;
    private final int WEIGHT_INCREASE_FACTOR = 5;



    public void setCurrentRGBArray(int[][][] currentRGBArray) {
        this.currentRGBArray = currentRGBArray;
    }

    public int[][][] getCurrentRGBArray() {
        return currentRGBArray;
    }

    //Constructs an image object
    public Image(File file) throws IOException {
        bufferedImage = ImageIO.read(new File(file.toURI()));
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        rgbArray = bufferedImageToRGBArray();
        currentRGBArray = rgbArray;
        energyMatrix = createEnergyMatrix(currentRGBArray);
        outputEnergyMatrix(energyMatrix);
    }

    /**
     * Constructs an image object from a bufferedImage.
     * This is typically used when a new image has been generated
     * such that you can add or remove seams from an image as if it
     * was the one that was initially input
     * @param newImage a buffered image that is to have seams inserted or carved
     */

    Image(BufferedImage newImage) {
        bufferedImage = newImage;
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        rgbArray = bufferedImageToRGBArray();
        currentRGBArray = rgbArray;
        energyMatrix = createEnergyMatrix(currentRGBArray);
        outputEnergyMatrix(energyMatrix);
    }

    /**
     * Takes a number of horizontal and vertical seams and removes them from the image.
     * vertical seams are removed first then the array of RGB values created by that is
     * transposed such that the same process can be repeated with horizontal seams. Then
     * the horizontal RGB array is transposed back and converted into the buffered image
     * that is returned
     * @param horizontalSeams the number of horizontal seams to be removed
     * @param verticalSeams the number of vertical seams to be removed
     * @return the buffered image that is generated from the RGB array
     */
    BufferedImage carveImage(int horizontalSeams, int verticalSeams) {
        if (horizontalSeams >= 0 && verticalSeams >= 0) {
            double[][]verticalEnergyMatrix = energyMatrix;
            currentRGBArray = removeSeams(verticalSeams,verticalEnergyMatrix,currentRGBArray);
            int[][][] horizontalRGBArray = transposeArray(currentRGBArray);
            double[][] horizontalEnergyMatrix = createEnergyMatrix(horizontalRGBArray);
            horizontalRGBArray = removeSeams(horizontalSeams,horizontalEnergyMatrix,horizontalRGBArray);
            currentRGBArray = transposeArray(horizontalRGBArray);
            bufferedImage = RGBArrayToImage();
            return bufferedImage;
        }
        return null;
    }

    /**
     * Adds a number of vertical seams to the image. This happens by finding one seam of the
     * least energy and then taking the average of that seam and a seam next to it with the
     * least energy
     * @param seams the number of vertical seams to add to the image
     * @return the buffered image generated from the RGB array
     */
    public BufferedImage addToImageVertical(int seams) {
        double[][]verticalEnergyMatrix = createEnergyMatrix(currentRGBArray);
        currentRGBArray = addSeams(seams,verticalEnergyMatrix,currentRGBArray);
        bufferedImage = RGBArrayToImage();
        return RGBArrayToImage();
    }

    /**
     * Adds a number of horizontal seams to the image. Works in the same way as {@link #addToImageVertical(int)  addToImageVertical}
     * method except it first transposes the array adds the seams then transposes it back
     * @param seams the number of horizontal seams to add to the image
     * @return the buffered image generated from the RGB array
     */
    public BufferedImage addToImageHorizontal(int seams) {
        int[][][] transposedArray = transposeArray(currentRGBArray);
        double[][]horizontalEnergyMatrix = createEnergyMatrix(transposedArray);
        transposedArray = addSeams(seams,horizontalEnergyMatrix,transposedArray);
        currentRGBArray = transposeArray(transposedArray);
        return RGBArrayToImage();

    }

    /**
     * Transposes an RGB array. This is used so that the same method that calculates the vertical
     * seams can also be used to calculate the vertical seams just using a transposed RGB array
     * @param arrayToTranspose the RGB array that is going to be transposed
     * @return the new transposed RGB array
     */
    private static int[][][] transposeArray(int[][][] arrayToTranspose) {
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

    /**
     * Finds the lowest energy seam and adds a new seam to one side of the found seam. When the lowest
     * energy seam is found {@link #addSeam(int[], int[][][], double[][])  addSeam} is used to add a new
     * seam into the image next to it
     * @param numberOfSeams the number of seams to be added
     * @param energyMatrix the energy matrix that is associated with the rgb array
     * @param rgbArray the rgb array of the image that seams are being inserted into
     * @return the new rgb array with seams added
     */
    private int[][][] addSeams(int numberOfSeams, double[][] energyMatrix, int[][][] rgbArray) {
        double[][] weightArray = initializeWeights(energyMatrix);
        double[][] currentEnergyMatrix = energyMatrix;
        for (int k = 0; k < numberOfSeams; k++) {
            int yLength = weightArray[0].length;
            int xLength = weightArray.length;
            int currentX = 0;
            int[] seam = new int[yLength];
            double lowestWeight = Double.MAX_VALUE;
            //In the first for loop the location that the seam starts from is found at the bottom of the array
            for (int i = 0; i < xLength; i++) {
                if (weightArray[i][yLength - 1] <= lowestWeight) {
                    lowestWeight = weightArray[i][yLength - 1];
                    currentX = i;
                }
            }
            //The seam with the least energy is found
            seam = findSeam(currentX,weightArray);
            //A seam is added next to the one detected by this method
            rgbArray = addSeam(seam,rgbArray,energyMatrix);
            //The energy matrix is recalculated for the new array
            currentEnergyMatrix = recalculateEnergyMatrixAdded(seam,energyMatrix,rgbArray);
            weightArray = initializeWeights(currentEnergyMatrix);
        }
        return rgbArray;
    }

    /**
     * Finds the seam either side of the inputted seam with the lowest energy. This is done by
     * getting the sum of all the energies in the seams either side of the seam.
     * @param seam the seam that the seams are found either side of
     * @param energyMatrix the energy matrix for the image
     * @return the seam with the lowest total energy
     */
    public int[] compareSeams(int[] seam,double[][] energyMatrix) {
        double positiveSideSeamTotal = 0;
        double negativeSideSeamTotal = 0;
        int[] positiveSideSeam = new int[energyMatrix[0].length];
        int[] negativeSideSeam = new int[energyMatrix[0].length];
        for (int y = 0; y < energyMatrix[0].length; y++) {
            positiveSideSeam[y] = seam[y];
            negativeSideSeam[y] = seam[y];
            if ((seam[y]+1) < energyMatrix.length) {
                positiveSideSeam[y] = seam[y]+1;
                positiveSideSeamTotal = energyMatrix[positiveSideSeam[y]][y];
            } else if ((seam[y]-1) >= 0) {
                negativeSideSeam[y] = seam[y]-1;
                negativeSideSeamTotal = energyMatrix[negativeSideSeam[y]][y];
            }
        }
        if (positiveSideSeamTotal <= negativeSideSeamTotal) {
            return positiveSideSeam;
        } else {
            return negativeSideSeam;
        }
    }

    /**
     * The average RGB of the two adjacent seams is found.
     * @param seam1 the first seam to be compared
     * @param seam2 a seam adjacent to the first seam
     * @param rgbArray the rgb array containing the rgb values which the average rgb
     *                 values can be taken from
     * @return an array containing the R,G and B values that are to be inserted on each
     * y line of the RGB array
     */
    public int[][] getAverageSeam (int[] seam1, int[] seam2, int[][][] rgbArray) {
        int[][] newRGBSeam = new int[rgbArray[0].length][3];
        for (int y = 0; y < rgbArray[0].length; y++) {
            newRGBSeam[y][0] = (rgbArray[seam1[y]][y][0]+rgbArray[seam2[y]][y][0])/2;
            newRGBSeam[y][1] = (rgbArray[seam1[y]][y][1]+rgbArray[seam2[y]][y][1])/2;
            newRGBSeam[y][2] = (rgbArray[seam1[y]][y][2]+rgbArray[seam2[y]][y][2])/2;
        }

        return newRGBSeam;
    }

    /**
     * The new seam with rgb values are inserted into the rgb array for the image
     * @param seam the seam next to which the average seam will be inserted
     * @param rgbArray the rgb array for the image
     * @param energyMatrix the energy matrix for the image
     * @return the rgb array with the new seam added to it
     */
    public int[][][] addSeam(int[] seam,int[][][] rgbArray, double[][] energyMatrix ) {
        int[] seamToInsert = compareSeams(seam,energyMatrix);
        int[][] rgbSeamToInsert = getAverageSeam(seam,seamToInsert,rgbArray);
        int[][][] newRGBArray = new int[rgbArray.length+1][rgbArray[0].length][3];

        for (int y = 0; y < rgbArray[0].length; y++) {
            int offset = 0;
            for (int x = 0; x < rgbArray.length; x++) {
                if (seamToInsert[y] + 1 == x) {
                    newRGBArray[x][y] = rgbSeamToInsert[y];
                    offset++;
                }
                    newRGBArray[x+offset][y] = rgbArray[x][y];
            }
        }
        return newRGBArray;
    }

    /**
     * Removes a number of seams from the rgbArray
     * @param numberOfSeams the number of seams to be removed
     * @param energyMatrix the energy matrix associated with the image
     * @param rgbArray the RGB array associated with the image
     * @return the rgb array with the number of seams removed
     */
    public int[][][] removeSeams(int numberOfSeams, double[][] energyMatrix, int[][][] rgbArray) {
        double[][] weightArray = initializeWeights(energyMatrix);
        double[][] currentEnergyMatrix = energyMatrix;
        for (int k = 0; k < numberOfSeams; k++) {
            int yLength = weightArray[0].length;
            int xLength = weightArray.length;
            int currentX = 0;
            int[] seam;
            double lowestWeight = Double.MAX_VALUE;
            //In the first for loop the location that the seam starts from is found at the bottom of the array
            for (int i = 0; i < xLength; i++) {
                if (weightArray[i][yLength - 1] <= lowestWeight) {
                    lowestWeight = weightArray[i][yLength - 1];
                    currentX = i;
                }
            }
            //The seam with the least energy is found
            seam = findSeam(currentX,weightArray);
            //The rgb array is updated to reflect the removal of a seam
            rgbArray = updateCurrentRGBArray(seam,rgbArray);
            //The energy matrix is recalculated with the seams removed
            currentEnergyMatrix = recalculateEnergyMatrixRemoved(seam,currentEnergyMatrix,rgbArray);
            weightArray = initializeWeights(currentEnergyMatrix);
        }
        return rgbArray;
    }

    /**
     * this method finds the seam with the lowest energy from the weight array. It does this by
     * starting from the cell at the bottom of the image with the lowest weight and then finding
     * the cell above it with the lowest weight and adding that to the seam
     * @param currentX the x value that the seam should start from
     * @param weightArray the weight array that the seam is calculated using
     * @return the seam that is to be used
     */
    private int[] findSeam(int currentX, double[][] weightArray) {
        int yLength = weightArray[0].length;
        int xLength = weightArray.length;
        int[] seam = new int[yLength];
        //For each pixel in the seam this loop checks the three weights above it to find the lowest weight
        //this pixel is selected to be part of the seam
        for (int y = yLength - 1; y >= 0; y--) {
            double lowestWeight = Double.MAX_VALUE;
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
        }
        return seam;
    }

    /**
     * Removes a given seam from an array
     * @param seam the seam to be removed
     * @param inputArray the array that the seam is to be removed from
     * @return the input array with the seam removed from it
     */
    public static double[][] removeSeam(int[] seam, double[][] inputArray) {
        double[][] outputArray = new double[inputArray.length - 1][inputArray[0].length];
        for (int y = 0; y < inputArray[0].length; y++) {
            int offset = 0;
            for (int x = 0; x < inputArray.length; x++) {
                if (x != seam[y]) {
                    outputArray[x + offset][y] = inputArray[x][y];
                } else {
                    //Offset is decremented when a seam is located so that all of the elements after the seam
                    //are shifted one position to the left in the array
                    offset--;
                }
            }
        }
        return outputArray;
    }

    /**
     * recalculates the energy matrix for the new seams that have been added
     * @param seam the new seam to be added
     * @param energyMatrix the energy matrix associated with the image
     * @param carvedRGB the new rgb array
     * @return the new energy matrix with changed energies
     */
    public double[][] recalculateEnergyMatrixAdded(int[] seam, double[][] energyMatrix, int[][][] carvedRGB) {
        int[] seamToInsert = compareSeams(seam,energyMatrix);
        return changeEnergiesForAdding(seamToInsert,energyMatrix,carvedRGB);
    }

    /**
     * Recalculates the energies but only for the pixels that will have been changed.
     * this is because only 3 different energies that will have changed when you add a new
     * seam in for each y.
     * @param seam the seam that has been added
     * @param energyMatrix the energy matrix to be recalculated
     * @param carvedRGB the RGB array from which the new energies will be calculated
     * @return the newly calculated energy matrix
     */
    public double[][] changeEnergiesForAdding(int[] seam, double[][] energyMatrix, int[][][] carvedRGB) {
        for (int y = 0; y < energyMatrix[0].length; y++) {
            for (int x = 0; x < energyMatrix.length; x++) {
                if (seam[y] == x || seam[y]+1 == x || seam[y]-1 == x) {
                    //The newly calculated weight is multiplied by some factor to stop it from adding too many
                    //new seams in the same space although this is at somepoint ineviatble
                    energyMatrix[x][y] = WEIGHT_INCREASE_FACTOR*getCellEnergy(carvedRGB,x,y);
                }
            }
        }
        return energyMatrix;
    }

    /**
     * Recalculates the energy matrix when a seam has been removed
     * @param seam the seam that has been removed
     * @param energyMatrix the energy matrix associated with the image
     * @param carvedRGB the rgb array associated witht he image with the energies removed
     * @return the new energy matrix
     */
    public double[][] recalculateEnergyMatrixRemoved(int[] seam, double[][] energyMatrix, int[][][] carvedRGB) {
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
        return energyMatrix;
    }

    /**
     * Removes one seam from the rgb array
     * @param seam the seam to be removed
     * @param RGBArray the rgb array from which the
     * @return
     */
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

    private static double[][] initializeWeights(double[][] energyMatrix) {
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
            }
        }
        return weightArray;
    }


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

    private BufferedImage RGBArrayToImage() {
        BufferedImage outputImage = new BufferedImage(currentRGBArray.length,currentRGBArray[0].length,TYPE_4BYTE_ABGR);
        for (int y = 0; y < currentRGBArray[0].length; y++) {
            for (int x = 0; x < currentRGBArray.length; x++) {
                outputImage.setRGB(x,y,RGBToARBG(currentRGBArray[x][y]));
            }
        }
        return outputImage;
    }

    public static void outputEnergyMatrix(double[][] imageArray) {
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
        try {
            String outPath = (System.getProperty("user.dir") + "/CS1006P2/out/outputImage.png");
            outPath = outPath.replaceAll("CS1006P2/src/", "");
            outFile = new File(outPath);
            ImageIO.write(imgOut,"png",outFile);
        } catch (IOException e) {
            System.out.println("Error:" + e);
        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return  height;
    }

}