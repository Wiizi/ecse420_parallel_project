package filters;

import java.awt.image.BufferedImage;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public interface IFilter {

    public BufferedImage applyFilter(BufferedImage image);

}
