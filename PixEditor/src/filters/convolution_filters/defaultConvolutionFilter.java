package filters.convolution_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class DefaultConvolutionFilter extends ConvolutionFilter{
    public DefaultConvolutionFilter(int number_of_threads){
        this.weights = new float[][] {
                {1,     2,      -1},
                {2,     0.25f,  -2},
                {1,     -2,     -1}
        };
        this.radius = 1;
        this.number_of_threads = number_of_threads;
    }
}
