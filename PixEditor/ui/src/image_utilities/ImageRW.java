package image_utilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public class ImageRW {
    public static BufferedImage readImage(String filename){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filename + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return image;
    }

    public static void saveImage(BufferedImage image, String filename){
        try 
        {
            File outputfile = new File(filename + ".png");
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) 
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
