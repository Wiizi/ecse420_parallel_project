import filters.Filter;
import filters.FilteredImage;
import filters.RectificationFilter;
import filters.convolution_filters.BadBlurConvolutionFilter;
import filters.convolution_filters.EdgeDetectConvolutionFilter;
import filters.convolution_filters.SharpenConvolutionFilter;
import image_utilities.ImageRW;

import java.awt.image.BufferedImage;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public class Main {
    private static final int number_of_threads = 8;
    public static void main(String[] args){
        BufferedImage image_in, image_out;
        image_in = ImageRW.readImage("grumpy");

        Filter rectificationFilter = new RectificationFilter(number_of_threads);
        Filter convolutionFilter = new SharpenConvolutionFilter(number_of_threads, 1f);

        long t1 = System.currentTimeMillis();
        FilteredImage filteredImage = new FilteredImage(image_in, convolutionFilter);
        long t2 = System.currentTimeMillis();

        System.out.println("Time spent: [" +(t2-t1) +"]");

        image_out = filteredImage.getFilteredImage();

        ImageRW.saveImage(image_out, "out");
    }
}
