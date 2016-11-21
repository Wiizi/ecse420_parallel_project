package filters;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public abstract class ConvolutionFilter extends Filter {
    protected float[][] weights;

    @Override
    public BufferedImage applyFilter(BufferedImage image){
        //image = image.getSubimage(512,512,2,2);
        BufferedImage output_image = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        filter(image, output_image, this.number_of_threads);
        return output_image;
    }

    public void filter(BufferedImage image, BufferedImage output_image, int number_of_threads){
        int width_in, height_in, total_pixels, pixels_per_thread, leftover;
        width_in = image.getWidth();
        height_in = image.getHeight();
        total_pixels = width_in * height_in;
        pixels_per_thread = total_pixels / number_of_threads;
        leftover = total_pixels - number_of_threads * pixels_per_thread;

        ConvolutionThread runnables[] = new ConvolutionThread[number_of_threads];
        Thread threads[] = new Thread[number_of_threads];

        // set up thread args
        for (int i = 0; i < number_of_threads && i < total_pixels; i++) {
            runnables[i] = new ConvolutionThread(i,image, output_image, pixels_per_thread, pixels_per_thread * i);
        }
        System.out.println("leftover " + leftover);
        // if image length is not a multiple of number of threads there will be some leftover bits
        if (leftover > 0){
            runnables[0].length += leftover;
            for (int i = 1; i < number_of_threads; i++) {
                runnables[i].length_offset += leftover;
            }
        }
        long t1 = System.currentTimeMillis();
        // start threads
        for (int i = 0; i < number_of_threads; i++) {
            threads[i] = new Thread(runnables[i]);
            threads[i].start();
        }
        // join threads
        for (int i = 0; i < number_of_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Time spent inside filter: [" +(t2-t1) +"]");
        System.out.println("Filtering complete.");
    }

    protected float[][] rotateMatrix90(float[][] matrix, int rotations){
        rotations %= 4;
        if (rotations == 0)
            return matrix;
        int n = matrix.length;
        float[][] ret = new float[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                ret[i][j] = matrix[n - j - 1][i];
            }
        }
        return rotateMatrix90(ret, rotations-1);
    }

    private class ConvolutionThread implements Runnable{
        private int thread_id;
        private final int BYTES_PER_PIXEL = 4;
        protected BufferedImage image_buffer, output_buffer;
        protected int length, length_offset;
        public ConvolutionThread(int thread_id, BufferedImage image_buffer, BufferedImage output_buffer, int length, int length_offset){
            this.thread_id = thread_id;
            this.output_buffer = output_buffer;
            this.image_buffer = image_buffer;
            this.length = length;
            this.length_offset = length_offset;
        }
        public void run(){
            byte[] bytes_in = new byte[4];
            byte[] bytes_out = new byte[4];
            ByteBuffer buffer_in = ByteBuffer.wrap(bytes_in);
            ByteBuffer buffer_out = ByteBuffer.wrap(bytes_out);
            int i,j, imageWidth, imageHeight, pixel_rgba;
            imageWidth = image_buffer.getWidth();
            imageHeight = image_buffer.getHeight();
            i = get_2d(length_offset, imageWidth, 1);
            j = get_2d(length_offset, imageWidth, 0);
            System.out.println("Thread" + this.thread_id + " working on " + this.length + " pixels starting at offset " + this.length_offset + "; ["+i+","+j+"]");
            int counter = 0;
            while (this.length > 0){
                counter++;
                int cA, cR, cG, cB;
                cA = 255;
                cR = cG = cB = 0;
                for (int jj = -1; jj < 2; jj++){
                    for (int ii = -1; ii < 2; ii++){
                        int x,y;
                        x = i + ii;
                        y = j + jj;
                        if (x < 0 || y < 0 || x >= imageWidth || y >= imageHeight)
                            continue;
                        pixel_rgba = image_buffer.getRGB(x,y);
                        buffer_in.putInt(0,pixel_rgba);
                        cR += weights[ii+1][jj+1] * getUnsignedByte(buffer_in.get(1));
                        cG += weights[ii+1][jj+1] * getUnsignedByte(buffer_in.get(2));
                        cB += weights[ii+1][jj+1] * getUnsignedByte(buffer_in.get(3));
                    }
                }
                cR = (cR < 0) ? 0 : cR;
                cR = (cR > 255) ? 255 : cR;
                cG = (cG < 0) ? 0 : cG;
                cG = (cG > 255) ? 255 : cG;
                cB = (cB < 0) ? 0 : cB;
                cB = (cB > 255) ? 255 : cB;
                buffer_out.put(0, (byte)cA);
                buffer_out.put(1, (byte)cR);
                buffer_out.put(2, (byte)cG);
                buffer_out.put(3, (byte)cB);

                pixel_rgba = buffer_out.getInt(0);
                output_buffer.setRGB(i,j, pixel_rgba);
                i++;
                if (i == imageWidth){ // preincrement when on the edge of the image
                    j++;
                    i=0;
                }
                this.length--;
            }
            System.out.println("thread" + this.thread_id + ": counter " + counter);
        }
    }
}