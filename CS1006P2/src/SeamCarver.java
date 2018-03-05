import java.util.Arrays;
import java.util.Comparator;

public class SeamCarver {
    private static double[][] verticalWeights;
    private static double[][] horizontalWeights;

    public static void initializeWeights(Image imageToCarve) {
        double[][] energyMatrix = imageToCarve.getEnergyMatrix();
        for (int y=0; y < energyMatrix[0].length; y++) {
            for (int x=0; x < energyMatrix.length; x++) {
                if(y == 0) {
                    verticalWeights[x][y] = energyMatrix[x][y];
                } else {
                    double aboveLeft = verticalWeights[((x-1)+verticalWeights.length)%verticalWeights.length][y-1];
                    double above = verticalWeights[x][y-1];
                    double aboveRight = verticalWeights[((x+1)+verticalWeights.length)%verticalWeights.length][y-1];
                    double[] choices = {aboveLeft,above,aboveRight};
                    Arrays.sort(choices);
                    verticalWeights[x][y] = choices[0] + energyMatrix[x][y];
                }
            }
        }
    }

    public static double[] getSeam() {
        int currentx = 0;
        double[] seam = new double[verticalWeights.length-1];
        double lowestWeight = Double.MAX_VALUE;
        for (int i = 0; i < verticalWeights[0].length; i++) {
            if (verticalWeights[verticalWeights[0].length-1][i] < lowestWeight) {
                lowestWeight = verticalWeights[verticalWeights[0].length-1][i];
                currentx = i;
            }
        }
        for (int j = 0; j < verticalWeights.length-1; j++) {
            lowestWeight = Double.MAX_VALUE;
            for (int k = currentx-1; k <= currentx+1; k++) {
                if (verticalWeights[j][(k+verticalWeights.length)%verticalWeights.length] <= lowestWeight) {
                    currentx = (k+verticalWeights.length)%verticalWeights.length;
                    lowestWeight = verticalWeights[j][k];
                    //Index is y value is x
                    seam[(verticalWeights.length-1)-j] = k;
                }
            }
        }
        return seam;
    }
}
