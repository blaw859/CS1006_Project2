import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.util.Queue;
import java.util.Iterator;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class Image {
    private int width;
    private int height;
    private BufferedImage bufferedImage;
    private double[][] energyMatrix;
    private int[][][] rgbArray;
    public int[][][] currentRGBArray;

    //private BufferedImage energyMatrixImage;
    //private File energyMatrixImage;

    //Constructs an image object
    public Image(String imageFilePath) throws IOException {
        System.out.println(imageFilePath);
        bufferedImage = ImageIO.read(getClass().getResource(imageFilePath));
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();

        rgbArray = bufferedImageToRGBArray();
        currentRGBArray = rgbArray;
        energyMatrix = createEnergyMatrix(currentRGBArray);
        outputEnergyMatrix(energyMatrix);
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
        //System.out.println("X"+x);
        //System.out.println("Y"+y);
        int argbColour = bufferedImage.getRGB(x,y);
        //For all of the colours we use bitwise and and then bitshift the value by an amount so that the individual RGB
        //colours can be isolated in each pixel
        rgbArray[0] = (argbColour & 0x00ff0000) >>16; //Red
        rgbArray[1] = (argbColour & 0x0000ff00) >>8; //Green
        rgbArray[2] = (argbColour & 0x000000ff); //Blue
        return rgbArray;
    }

    public double[][] updateCurrentRGB(int[] seam) {
        System.out.println("UpdatingRGB the current array lengths is: "+currentRGBArray.length);
        int[][][] carvedRGB = SeamCarver.removeSeam(seam,currentRGBArray);
        energyMatrix = SeamCarver.removeSeam(seam,energyMatrix);
        System.out.println("CarvedRGB width is:"+carvedRGB.length);
        System.out.println();
        for (int y = 0; y < carvedRGB[0].length; y++) {
            for (int x = 0; x < carvedRGB.length; x++) {
                if (seam[y]==x) {
                    energyMatrix[x][y] = getCellEnergy(currentRGBArray,x,y);
                    if(x>0) {
                        energyMatrix[x - 1][y] = getCellEnergy(currentRGBArray, x - 1, y);
                    }
                }
            }
        }
        currentRGBArray = carvedRGB;
        return energyMatrix;
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
        //System.out.println(bufferedImage.getWidth());
        //System.out.println(bufferedImage.getHeight());
        //System.out.println(((-1)+100)%100);
        int l = (x - 1 + xLength)%(xLength);
        int r = (x + 1 + xLength)%(xLength);
        int a = (y + 1 + yLength)%(yLength);
        int b = (y - 1 + yLength)%(yLength);

        //These statements deal with the edges of the array
                /*if(x == 0) l = bufferedImage.getNumXTiles() - 1;
                if(y == 0) a = bufferedImage.getNumYTiles() - 1;
                if(x == bufferedImage.getNumXTiles()) r = 0;
                if(y == bufferedImage.getNumYTiles()) b = 0;*/
        //System.out.println(x+" "+y+" "+l+" "+r+" "+a+" "+b);
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
        //System.out.println("Getting");
        return energyMatrix;
    }

    public static void outputEnergyMatrix(double[][] imageArray) {
        //System.out.println(imageArray.length);
        //System.out.println(imageArray[0].length);
        BufferedImage imgOut = new BufferedImage(imageArray.length,imageArray[0].length,BufferedImage.TYPE_BYTE_GRAY);
        File outFile = null;
        for (int y=0; y < imageArray[0].length; y++) {
            for (int x=0; x < imageArray.length; x++) {
                byte pixelColour = (byte) imageArray[x][y];
                //int argb = (pixelColour << 24);
                //argb |= pixelColour << 24;
                //int pixelColourInt = pixelColour;
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
        //energyMatrixImage = imgOut;
        //energyMatrixImage = outFile;
    }

    /*public BufferedImage carveVertical(int numberSeams) {
        *//*BufferedImage newImage =  //Placeholder variable
        *//**//*
        This method should preferably call the PathFinding class,
        which will find the optimal seams and removed "seams" number of them.
        It will then return a 2D array of RGB values, which we can then use to print the image
        *//**//*
        return newImage;*//*
    }*/

    /*public BufferedImage removeSeams(Queue<int[]> q) {
        int[][] newImage = null;
        int off;
        int[][] seam;
        int xRGB = 0;
        for (int[] iterate: q) {
            if (newImage != null) {
                newImage = new int[newImage.length - 1][newImage[0].length];
            } else {
                newImage = new int[bufferedImage.getWidth() - 1][bufferedImage.getHeight()];
            }
            for (int y = 0, y1 = 0; y < newImage[0].length; y++) {
                off = 0;
                for (int x = 0, x1 = 0; x < newImage.length; x++) {
                    xRGB = x1 - off;
                    if (xRGB < 0) {
                        xRGB = 0;
                    }
                    if (!(iterate[y] == x)) {
                        newImage[x1][y1] = bufferedImage.getRGB(xRGB,y);
                        x1++;
                    } else {
                        off++;
                    }
                }
                y1++;
            }
        }
        return imageArrayToImage(newImage);
    }*/

    /*public int[][][] removeSeams(Queue<int[]> q) {
        System.out.println("Number of seams: "+q.size());
        //BufferedImage currentImage = bufferedImage;
        //int[][][] currentImageRGB = currentRGBArray;
        int initialSize = q.size();
        for (int k = 0; k < initialSize; k++) {
            BufferedImage nextImage = new BufferedImage(currentRGBArray.length-1,currentRGBArray[0].length,TYPE_4BYTE_ABGR);
            int[] currentSeam = q.remove();
            System.out.println(q.size());
            //System.out.println("__________NEW SEAM__________");
            for (int y = 0; y < currentRGBArray[0].length; y++) {
                int offset = 0;
                //System.out.println("("+currentSeam[y]+","+y+")");
                for (int x = 0; x < currentRGBArray.length; x++) {

                    if(x != currentSeam[y]) {
                        nextImage.setRGB(x+offset,y,currentImage.getRGB(x,y));
                    } else {
                        offset--;
                    }
                }
            }
            currentImage = nextImage;
        }
        return currentImage;
    }*/
    //rgbArray[0] = (argbColour & 0x00ff0000) >>16; //Red
    //rgbArray[1] = (argbColour & 0x0000ff00) >>8; //Green
    //rgbArray[2] = (argbColour & 0x000000ff); //Blue

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

    private BufferedImage imageArrayToImage(int[][] imageArray) {
        BufferedImage outputImage = new BufferedImage(imageArray.length,imageArray[0].length,BufferedImage.TYPE_4BYTE_ABGR);
        for (int y = 0; y <imageArray[0].length; y++) {
            for (int x = 0; x < imageArray.length; x++) {
                outputImage.setRGB(x,y,imageArray[x][y]);
            }
        }
        return outputImage;
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

    public void printRGBArray () {
        for (int y = 0; y < currentRGBArray[0].length; y++) {
            for (int x = 0; x < currentRGBArray.length; x++) {
                //System.out.print("("+currentRGBArray[x][y][0]+","+currentRGBArray[x][y][1]+","+currentRGBArray[x][y][2]+")");
                System.out.println(String.format("0x%08X",RGBToARBG(currentRGBArray[x][y])));

            }
            System.out.println("");
        }
    }

}