package filters;

import java.awt.image.BufferedImage;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public class FilteredImage {

    BufferedImage filtered_image;

    public FilteredImage(BufferedImage image, IFilter filter){
        this.filtered_image = filter.applyFilter(image);
    }

    public BufferedImage getFilteredImage(){
        return this.filtered_image;
    }


}
