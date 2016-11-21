package filters.convolution_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class SharpenConvolutionFilter extends ConvolutionFilter{
    public SharpenConvolutionFilter(int number_of_threads, float strength){
        this.weights = new float[][] {
                {0,             -1*strength,        0           },
                {-1*strength,    1+4*strength,      -1*strength },
                {0,             -1*strength,        0           }
        };
        this.radius = 1;
        this.number_of_threads = number_of_threads;
    }
}