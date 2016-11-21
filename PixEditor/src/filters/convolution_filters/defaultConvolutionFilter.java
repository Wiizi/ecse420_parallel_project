package filters.convolution_filters;

import filters.ConvolutionFilter;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class defaultConvolutionFilter extends ConvolutionFilter{
    public defaultConvolutionFilter(int number_of_threads){
        this.weights = new float[][] {
                        {1,2,-1},
                        {2,0.25f,-2},
                        {1,-2,-1}
        };
        this.number_of_threads = number_of_threads;
    }

    private void multiply(float[][] vals, float multiplier){
        for (int i = 0; i < vals.length; i++){
            for (int j = 0; j < vals[i].length; j++){
                vals[i][j] *= multiplier;
            }
        }
    }
}
