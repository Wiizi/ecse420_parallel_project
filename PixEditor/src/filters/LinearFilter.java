package filters;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Andrei-ch on 2016-11-20.
 */
public abstract class LinearFilter extends Filter {
    @Override
    public BufferedImage applyFilter(BufferedImage image){
        BufferedImage output_image = image.getSubimage(0,0,image.getWidth(),image.getHeight());
        filter(image, output_image, this.number_of_threads);
        return output_image;
    }

    public abstract int applyFilterOnPixel(int val);

    public void filter(BufferedImage image, BufferedImage output_image, int number_of_threads){
        int width_in, height_in, total_pixels, pixels_per_thread, leftover;
        width_in = image.getWidth();
        height_in = image.getHeight();
        total_pixels = width_in * height_in;
        pixels_per_thread = total_pixels / (number_of_threads);
        leftover = total_pixels - number_of_threads * pixels_per_thread;

        RectificationThread runnables[] = new RectificationThread[number_of_threads];
        Thread threads[] = new Thread[number_of_threads];

        // set up thread args
        for (int i = 0; i < number_of_threads && i < total_pixels; i++) {
            runnables[i] = new RectificationThread(i,image, output_image, pixels_per_thread, pixels_per_thread * i);
        }
        //System.out.println("leftover " + leftover);
        // if image length is not a multiple of number of threads there will be some leftover bits
        if (leftover > 0){
            runnables[0].length += leftover;
            for (int i = 1; i < number_of_threads; i++) {
                runnables[i].length_offset += leftover;
            }
        }
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
    }

    private class RectificationThread implements Runnable{
        private int thread_id;
        private final int BYTES_PER_PIXEL = 4;
        protected BufferedImage image_buffer, output_buffer;
        protected int length, length_offset;
        public RectificationThread(int thread_id, BufferedImage image_buffer, BufferedImage output_buffer, int length, int length_offset){
            this.thread_id = thread_id;
            this.output_buffer = output_buffer;
            this.image_buffer = image_buffer;
            this.length = length;
            this.length_offset = length_offset;
        }
        public void run(){
            byte[] bytes = new byte[4];
            ByteBuffer buffer;
            buffer = ByteBuffer.wrap(bytes);
            int i,j,pixel_rgba = 0;
            int width = image_buffer.getWidth();
            for (int index = length_offset; index < (length_offset + length); index ++) {
                i = get_2d(index, width, 1);
                j = get_2d(index, width, 0);
                pixel_rgba = image_buffer.getRGB(i, j);
                buffer.putInt(0, pixel_rgba);
                int val;
                // ARGB format for the pixel data
                for (int rgba_index = 0; rgba_index < BYTES_PER_PIXEL; rgba_index++){
                    if (rgba_index != 0) {
                        val = getUnsignedByte(buffer.get(rgba_index));
                        val = applyFilterOnPixel(val);
                    } else {
                        val = 255;
                    }
                    buffer.put(rgba_index, (byte) val);
                }
                val = buffer.getInt(0);
                output_buffer.setRGB(i,j,val);
            }
        }
    }
}
