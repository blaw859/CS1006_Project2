import java.util.*;

public class SeamCarver {
    public static double[][] verticalWeights;
    public static double[][] horizontalWeights;

    public static void setEnergyMatrices(Image imageToCarve) {
        double[][] energyMatrix = imageToCarve.getEnergyMatrix();
        verticalWeights = initializeWeights(energyMatrix);
        horizontalWeights = initializeWeights(transposeArray(energyMatrix));
    }

    public static double[][] initializeWeights(double[][] energyMatrix) {
        double[][] weightArray = new double[energyMatrix.length][energyMatrix[0].length];
        int maxX = weightArray.length;
        int maxY = weightArray[0].length;
        for (int y=0; y < maxY; y++) {
            for (int x=0; x < maxX; x++) {
                if(y==0) {
                    weightArray[x][y] = energyMatrix[x][y];
                } else {
                    double[] choices = {weightArray[((x-1)+maxX)%maxX][y-1],
                            weightArray[x][y-1],
                            weightArray[((x+1)+maxX)%maxX][y-1]};
                    Arrays.sort(choices);
                    weightArray[x][y] = (choices[0] + energyMatrix[x][y]);
                }
            }
        }
        return weightArray;
    }

    public static double[][] transposeArray(double[][] arrayToTranspose) {
        int maxX = arrayToTranspose.length;
        int maxY = arrayToTranspose[0].length;
        double[][] newArray = new double[maxY][maxX];
        for (int x = 0; x < maxY;x++) {
            for (int y = 0;y < maxX;y++) {
                newArray[x][y] = arrayToTranspose[y][x];
            }
        }
        return newArray;
    }

    public static Queue<int[]> findSeams(double[][] initialWeightArray, int numberOfSeams) {
        double[][] weightArray = initialWeightArray;
        Queue<int[]> seamsToRemove = new LinkedList<>();
        for(int k = 0; k < numberOfSeams; k++) {

            int yLength = weightArray[0].length;
            int xLength = weightArray.length;
            int currentX = 0;
            int[] seam = new int[yLength];
            double lowestWeight = Double.MAX_VALUE;

            for (int i = 0; i < xLength; i++) {
                if (weightArray[i][yLength-1] < lowestWeight) {
                    lowestWeight = weightArray[i][yLength-1];
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
                //System.out.println("New Three Options");
                for (int x = marker - 1; x <= marker + 1; x++) {
                    //int funX = (x + maxX) % maxX;
                    //System.out.println("MAX X is:"+maxX);
                    //System.out.println(y+" The x is: " + funX   );
                    //System.out.println(y+" The original x is: " + x   );
                    //System.out.println("The Y is:"+ y);
                    if (weightArray[(x + xLength) % xLength][y] <= lowestWeight) {
                        currentX = (x + xLength) % xLength;
                        lowestWeight = weightArray[(x + xLength) % xLength][y];
                        seam[y] = (x + xLength) % xLength;
                    }
                }
                //System.out.println(x+ " Was chosen");
            }
            weightArray = removeSeam(seam,weightArray);
            seamsToRemove.add(seam);
        }
        return seamsToRemove;
    }

    private static double[][] removeSeam(int[] seam, double[][] weightArray) {
        double[][] outputArray = new double[weightArray.length-1][weightArray[0].length];
        for (int y = 0; y < weightArray[0].length; y++) {
            int offset = 0;
            for (int x = 0; x < weightArray.length; x++) {
                if (x != seam[y]) {
                    outputArray[x+offset][y] = weightArray[x][y];
                } else {
                    offset--;
                }
            }
        }
        return outputArray;
    }

}

/*public class SeamCarver {
    public static double[][] verticalWeights;
    public static double[][] horizontalWeights;
    //public static double[][] currentVerticalWeights;
    //public static double[][] currentHorizontalWeights;


    public static void setEnergyMatrices(Image imageToCarve) {
        double[][] energyMatrix = imageToCarve.getEnergyMatrix();
        System.out.println("Energy width: "+ imageToCarve.getEnergyMatrix().length);
        System.out.println("Energy height: "+ imageToCarve.getEnergyMatrix()[0].length);
        verticalWeights = initializeWeights(energyMatrix);
        //System.out.println("done");
        horizontalWeights = initializeWeights(transposeArray(energyMatrix));
    }

    private static double[][] initializeWeights(double[][] energyMatrix) {
        double[][] weightArray = new double[energyMatrix.length][energyMatrix[0].length];
        int maxX = weightArray.length;
        int maxY = weightArray[0].length;
        for (int y=0; y < maxY; y++) {
            for (int x=0; x < maxX; x++) {
                if(y==0) {
                    weightArray[x][y] = energyMatrix[x][y];
                } else {
                    double[] choices = {weightArray[((x-1)+maxX)%maxX][y-1],
                                        weightArray[x][y-1],
                                        weightArray[((x+1)+maxX)%maxX][y-1]};
                    Arrays.sort(choices);
                    weightArray[x][y] = (choices[0] + energyMatrix[x][y]);
                }
                //System.out.print(weightArray[x][y]+",");
            }
            //System.out.println("");
        }
        return weightArray;
    }

    public static ArrayList<int[]> getSeams(double[][] initialWeightArray,int seamsToRemove) {
        //boolean[][] arrayMask = inputMask.getImageMask();
        double[][] weightArray = initialWeightArray;//cutWeightArray(initialWeightArray,arrayMask);
        ArrayList<int[]> outputSeams = new ArrayList<>();
        for(int k = 0; k < seamsToRemove;k++) {
            System.out.println("__________NEW SEAM__________");
            int maxX = weightArray.length;
            int maxY = weightArray[0].length;
            int currentx = 0;
            int[] seam = new int[maxY];
            double lowestWeight = Double.MAX_VALUE;
            *//*for (int i = 0; i < maxY; i++) {
                if (weightArray[maxX ][i] < lowestWeight) {
                    lowestWeight = weightArray[maxX - 1][i];
                    currentx = i;
                }
            }*//*
            for (int y = maxY - 1; y >= 0; y--) {
                lowestWeight = Double.MAX_VALUE;
                //System.out.println("New Three");
                int marker = currentx;
                if (marker == 0) {
                    marker = 1;
                } else if (marker == weightArray.length - 1) {
                    marker = weightArray.length - 2;
                }
                //System.out.println("New Three Options");
                for (int x = marker - 1; x <= marker + 1; x++) {
                    //int funX = (x + maxX) % maxX;
                    //System.out.println("MAX X is:"+maxX);
                    //System.out.println(y+" The x is: " + funX   );
                    //System.out.println(y+" The original x is: " + x   );
                    //System.out.println("The Y is:"+ y);
                    if (weightArray[(x + maxX) % maxX][y] <= lowestWeight) {
                        currentx = (x + maxX) % maxX;
                        lowestWeight = weightArray[(x + maxX) % maxX][y];
                        seam[y] = (x + maxX) % maxX;
                    }
                }
                //System.out.println(x+ " Was chosen");
            }
            weightArray = removeSeam(seam,weightArray);
            outputSeams.add(seam);
        }
        //System.out.println("Seam length is "+ seam.length);
        return outputSeams;
    }

    private static double[][] removeSeam(int[] seam, double[][] weightArray) {
        double[][] outputArray = new double[weightArray.length-1][weightArray[0].length];
        for (int y = 0; y < weightArray[0].length; y++) {
            int offset = 0;
            for (int x = 0; x < weightArray.length; x++) {
                if (x == seam[y]) {
                    outputArray[x+offset][y] = weightArray[x][y];
                } else {
                    offset--;
                }
            }
        }
        return outputArray;
    }

    //Cuts all of the already removed seams from the weight array
    *//*private static double[][] cutWeightArray(double[][] initialWeightArray, boolean[][] arrayMask) {
        double[][] outputArray = new double[initialWeightArray.length][initialWeightArray[0].length];
        for (int y = 0; y < initialWeightArray[0].length; y++) {
            int offset = 0;
            for (int x = 0; x < initialWeightArray.length; x++) {
                if (arrayMask[x][y] == FALSE) {
                    outputArray[x+offset][y] = initialWeightArray[x][y];
                } else {
                    offset--;
                }
            }
        }
        return outputArray;
    }*//*

    //This method is used for transposing the array so that the same method can be used to get both horizontal and vertical seams
    public static double[][] transposeArray(double[][] arrayToTranspose) {
        int maxX = arrayToTranspose.length;
        int maxY = arrayToTranspose[0].length;
        double[][] newArray = new double[maxY][maxX];
        for (int x = 0; x < maxY;x++) {
            for (int y = 0;y < maxX;y++) {
                newArray[x][y] = arrayToTranspose[y][x];
            }
        }
        return newArray;
    }*/

