import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
    private int width;
    private int height;
    private BufferedImage bufferedImage;
    private double[][] energyMatrix;

    //Constructs an image object
    public Image(String imageFilePath) throws IOException {
        bufferedImage = ImageIO.read(getClass().getResource(imageFilePath));
        width = bufferedImage.getWidth();
        energyMatrix = energyMatrix();
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

    /*public void createEnergyMatrix() {
        energyMatrix = energyMatrix();
    }*/

    //Returns an energy matrix as a 2D array of double values
    public double[][] energyMatrix() {
        double[][] energyArray;
        energyArray = new double[bufferedImage.getWidth()][bufferedImage.getHeight()];
        double energyX;
        double energyY;

        for (int x = 0; x < bufferedImage.getWidth() - 1; x++) {
            for (int y = 0; y < bufferedImage.getHeight() - 1; y++) {
                System.out.println(bufferedImage.getWidth());
                System.out.println(bufferedImage.getHeight());
                System.out.println(((-1)+100)%100);
                int l = (x - 1 + bufferedImage.getWidth())%(bufferedImage.getWidth());
                int r = (x + 1 + bufferedImage.getWidth())%(bufferedImage.getWidth());
                int a = (y + 1 + bufferedImage.getHeight())%(bufferedImage.getHeight());
                int b = (y - 1 + bufferedImage.getHeight())%(bufferedImage.getHeight());

                //These statements deal with the edges of the array
                /*if(x == 0) l = bufferedImage.getNumXTiles() - 1;
                if(y == 0) a = bufferedImage.getNumYTiles() - 1;
                if(x == bufferedImage.getNumXTiles()) r = 0;
                if(y == bufferedImage.getNumYTiles()) b = 0;*/
                System.out.println(x+" "+y+" "+l+" "+r+" "+a+" "+b);
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

    public double[][] getEnergyMatrix() {
        System.out.println("Getting");
        return energyMatrix;
    }

    public void outputEnergyMatrix(double[][] imageArray) {
        System.out.println(imageArray.length);
        System.out.println(imageArray[0].length);
        BufferedImage imgOut = new BufferedImage(imageArray.length,imageArray[0].length,BufferedImage.TYPE_BYTE_GRAY);
        for (int y=0; y < imageArray[0].length; y++) {
            for (int x=0; x < imageArray.length; x++) {
                byte pixelColour = (byte) imageArray[x][y];
                imgOut.setRGB(x,y,pixelColour);
            }
        }
        try{
           File outFile = new File ("CS1006P2/out/outputImage.png");
           ImageIO.write(imgOut,"png",outFile);
        } catch (IOException e) {
            System.out.println("Error:" + e);
        }
    }

    public double[][] compress(int seams) {
        double[][] newImage = null; //Placeholder variable
        /*
        This method should preferably call the PathFinding class,
        which will find the optimal seams and removed "seams" number of them.
        It will then return a 2D array of RGB values, which we can then use to print the image
        */
        return newImage;
    }

    public int getWidth() {
        return width;
    }
}