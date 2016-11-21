package filters;

import java.awt.image.BufferedImage;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public abstract class Filter {
    protected int number_of_threads = 8;
    public abstract BufferedImage applyFilter(BufferedImage image);
    protected int get_1d(int i, int j, int width) {
        return ((i)*(width)+(j));
    }
    protected int get_2d(int index, int width, int ij){
        return (((ij)==0)?((index)/(width)):((index)%(width)));
    }
    protected int getUnsignedByte(byte b){
        return ((b & 0b10000000) == 0b10000000) ? (b+255) : b;
    }
}
