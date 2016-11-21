package filters.convolution_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class EdgeDetectConvolutionFilter extends ConvolutionFilter{
    public EdgeDetectConvolutionFilter(int number_of_threads, float strength){
        this.weights = new float[][] {
                {0,             1*strength,         0},
                {1*strength,    1+ -5*strength,     1*strength},
                {0,             1*strength,         0}
        };
        this.radius = 1;
        this.number_of_threads = number_of_threads;
    }
}
