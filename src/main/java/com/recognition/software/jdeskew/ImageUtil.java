/**
 * <a url=http://www.jdeskew.com/>JDeskew</a>
 */
package com.recognition.software.jdeskew;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    /**
     * Whether the pixel is black.
     *
     * @param image source image
     * @param x
     * @param y
     * @return
     */
    public static boolean isBlack(BufferedImage image, int x, int y) {
        if (image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
            WritableRaster raster = image.getRaster();
            int pixelRGBValue = raster.getSample(x, y, 0);
            return pixelRGBValue == 0;
        }

        int luminanceValue = 140;
        return isBlack(image, x, y, luminanceValue);
    }

    /**
     * Whether the pixel is black.
     *
     * @param image source image
     * @param x
     * @param y
     * @param luminanceCutOff
     * @return
     */
    public static boolean isBlack(BufferedImage image, int x, int y, int luminanceCutOff) {
        int pixelRGBValue;
        int r;
        int g;
        int b;
        double luminance = 0.0;

        // return white on areas outside of image boundaries
        if (x < 0 || y < 0 || x > image.getWidth() || y > image.getHeight()) {
            return false;
        }

        try {
            pixelRGBValue = image.getRGB(x, y);
            r = (pixelRGBValue >> 16) & 0xff;
            g = (pixelRGBValue >> 8) & 0xff;
            b = (pixelRGBValue) & 0xff;
            luminance = (r * 0.299) + (g * 0.587) + (b * 0.114);
        } catch (Exception e) {
            // ignore.
        }

        return luminance < luminanceCutOff;
    }

    /**
     * Rotates image.
     *
     * @param image source image
     * @param angle by degrees
     * @param cx x-coordinate of pivot point
     * @param cy y-coordinate of pivot point
     * @return rotated image
     */
    public static BufferedImage rotate(BufferedImage image, double angle, int cx, int cy) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        int minX, minY, maxX, maxY;
        minX = minY = maxX = maxY = 0;

        int[] corners = {0, 0, width, 0, width, height, 0, height};

        double theta = Math.toRadians(angle);
        for (int i = 0; i < corners.length; i += 2) {
            int x = (int) (Math.cos(theta) * (corners[i] - cx)
                    - Math.sin(theta) * (corners[i + 1] - cy) + cx);
            int y = (int) (Math.sin(theta) * (corners[i] - cx)
                    + Math.cos(theta) * (corners[i + 1] - cy) + cy);

            if (x > maxX) {
                maxX = x;
            }

            if (x < minX) {
                minX = x;
            }

            if (y > maxY) {
                maxY = y;
            }

            if (y < minY) {
                minY = y;
            }

        }

        cx = (cx - minX);
        cy = (cy - minY);

        BufferedImage bi = new BufferedImage((maxX - minX), (maxY - minY),
                image.getType());
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.setBackground(Color.white);
        g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());

        AffineTransform at = new AffineTransform();
        at.rotate(theta, cx, cy);

        g2.setTransform(at);
        g2.drawImage(image, -minX, -minY, null);
        g2.dispose();

        return bi;
    }

    // SetGrayscale
    public static void setGrayscale(String input) throws IOException {
        try {
            //System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
            loadLibraries();
            //input = new File("digital_image_processing.jpg");
            //BufferedImage image = ImageIO.read(new File(input));

            Mat source = Imgcodecs.imread(input);
            Mat destination = new Mat();
            //Mat destination = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC1);
            Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);

            Imgcodecs.imwrite("grayscale.jpg", destination);
            System.out.println("The image is successfully to Grayscale");

            /*
            byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
            mat.put(0, 0, data);

            Mat mat1 = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC1);
            Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2GRAY);

            byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int)(mat1.elemSize())];
            mat1.get(0, 0, data1);
            BufferedImage image1 = new BufferedImage(mat1.cols(),mat1.rows(), BufferedImage.TYPE_BYTE_GRAY);
            image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);

            File ouptut = new File("grayscale.jpg");
            ImageIO.write(image1, "jpg", ouptut);
            */

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void loadLibraries() {

        try {
            InputStream in = null;
            File fileOut = null;
            String osName = System.getProperty("os.name");
            String opencvpath = System.getProperty("user.dir");
            if(osName.startsWith("Windows")) {
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if(bitness == 32) {
                    opencvpath=opencvpath+"\\opencv\\x86\\";
                }
                else if (bitness == 64) {
                    //opencvpath=opencvpath+"\\opencv\\x64\\";
                    opencvpath = "C:\\Users\\fitim\\Downloads\\opencv\\build\\java\\x64\\";
                } else {
                    opencvpath=opencvpath+"\\opencv\\x86\\";
                }
            }
            else if(osName.equals("Mac OS X")){
                opencvpath = opencvpath+"Your path to .dylib";
            }
            System.out.println(opencvpath);
            System.load(opencvpath + "opencv_java430.dll");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library", e);
        }
    }

}
