import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
    private int width;
    private int height;
    private BufferedImage bufferedImage;
    private double[][] energymatrix;

    //Constructs an image object
    public Image(File imageFile) throws IOException{
        bufferedImage = ImageIO.read(imageFile);
        energymatrix = energyMatrix();
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
    private double[][] energyMatrix() {
        double[][] energyArray;
        energyArray = new double[bufferedImage.getNumXTiles()][bufferedImage.getNumYTiles()];
        double energyX;
        double energyY;

        for (int x = 0; x < bufferedImage.getNumXTiles() - 1; x++) {
            for (int y = 0; y < bufferedImage.getNumYTiles() - 1; y++) {
                int l = x - 1;
                int r = x + 1;
                int a = y + 1;
                int b = y - 1;

                //These statements deal with the edges of the array
                if(x == 0) l = bufferedImage.getNumXTiles() - 1;
                if(y == 0) a = bufferedImage.getNumYTiles() - 1;
                if(x == bufferedImage.getNumXTiles()) r = 0;
                if(y == bufferedImage.getNumYTiles()) b = 0;

                int[] localRGBleft = getPixelColours(l,y);
                int[] localRGBright = getPixelColours(r,y);
                int[] localRGBabove = getPixelColours(x,a);
                int[] localRGBbelow = getPixelColours(x,b);

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
                energyArray[x][y] = Math.sqrt(energyX + energyY);
            }
        }
        return energyArray;
    }

    public double[][] getEnergymatrix() {
        return energymatrix;
    }

}