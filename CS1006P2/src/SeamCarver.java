import java.util.Arrays;
import java.util.Comparator;

public class SeamCarver {
    private static double[][] verticalWeights;
    private static double[][] horizontalWeights;

    public static void initializeWeights(Image imageToCarve) {
        double[][] energyMatrix = imageToCarve.getEnergyMatrix();
        verticalWeights = new double[energyMatrix.length][energyMatrix[0].length];
        //horizontalWeights = new double[energyMatrix.length][energyMatrix[0].length];
        System.out.println(energyMatrix.length);
        System.out.println(energyMatrix[0].length);

        for (int y=0; y < energyMatrix[0].length; y++) {
            //System.out.println(y);
            for (int x=0; x < energyMatrix.length; x++) {
                if(y == 0) {
                    //System.out.println(x);
                    verticalWeights[x][y] = energyMatrix[x][y];
                } else {
                    double aboveLeft = verticalWeights[((x-1)+verticalWeights.length)%verticalWeights.length][y-1];
                    double above = verticalWeights[x][y-1];
                    double aboveRight = verticalWeights[((x+1)+verticalWeights.length)%verticalWeights.length][y-1];
                    double[] choices = {aboveLeft,above,aboveRight};
                    Arrays.sort(choices);
                    //if(x == 328){System.out.println("OIIII");}
                    verticalWeights[x][y] = (choices[0] + energyMatrix[x][y]);
                }
                System.out.print(verticalWeights[x][y]+",");
            }
            System.out.println("");
        }
    }

    public static double[] getSeam() {
        int currentx = 0;
        double[] seam = new double[verticalWeights[0].length];
        double lowestWeight = Double.MAX_VALUE;
        for (int i = 0; i < verticalWeights[0].length; i++) {
            if (verticalWeights[verticalWeights[0].length-1][i] < lowestWeight) {
                lowestWeight = verticalWeights[verticalWeights[0].length-1][i];
                currentx = i;
            }
        }
        for (int y = verticalWeights[0].length-1; y >= 0 ; y--) {
            //System.out.println("Vertical Weight" + verticalWeights.length);
            lowestWeight = Double.MAX_VALUE;
            for (int x = currentx-1; x <= currentx+1; x++) {
                //System.out.println(verticalWeights[0].length);
                //System.out.println(y);
                //System.out.println("Oi oi"+(x+verticalWeights.length)%verticalWeights.length);
                if (verticalWeights[(x+verticalWeights.length)%verticalWeights.length][y] <= lowestWeight) {
                    currentx = (x+verticalWeights.length)%verticalWeights.length;
                    lowestWeight = verticalWeights[(x+verticalWeights.length)%verticalWeights.length][y];
                    //Index is y value is x
                    seam[y] = verticalWeights[currentx][y];
                }
            }
        }
        return seam;
    }
}
