package filters.convolution_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class EmbossConvolutionFilter extends ConvolutionFilter{
    public EmbossConvolutionFilter(int number_of_threads, float strength){
        this.weights = new float[][] {
                {-2*strength,   -1*strength,    0},
                {-1*strength,   1,              1*strength},
                {0,             1*strength,      2*strength}
        };
        this.radius = 1;
        this.number_of_threads = number_of_threads;
    }
}
