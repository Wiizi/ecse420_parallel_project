package filters.linear_filters;

/**
 * Created by Andrei-ch on 2016-11-21.
 */
public class SaturationLinearFilter extends LinearFilter{
    private float hue, saturation, lightness;
    public SaturationLinearFilter(int number_of_threads, float hue, float saturation, float lightness){
        this.number_of_threads = number_of_threads;
        this.hue = hue - 1f;
        this.saturation = saturation - 1f;
        this.lightness = lightness - 1f;
    }

    @Override
    public int applyFilterOnPixel(int val){
        int a = ( val >> 24 ) & 0xff;
        int r = ( val >> 16 ) & 0xff;
        int g = ( val >> 8 ) & 0xff;
        int b = val & 0xff;
        int hsl[] = convertRGBToHSL(r,g,b);
        hsl[0] += this.hue * 360;
        hsl[1] += this.saturation * 255;
        hsl[2] += this.lightness * 255;
        // clamp
        hsl[0] = (hsl[0] < 0) ? 0 : hsl[0];
        hsl[1] = (hsl[1] < 0) ? 0 : hsl[1];
        hsl[2] = (hsl[2] < 0) ? 0 : hsl[2];
        hsl[0] %= 360;
        hsl[1] = (hsl[1] > 255) ? 255 : hsl[1];
        hsl[2] = (hsl[2] > 255) ? 255 : hsl[2];
        int rgb[] = convertHSLToRGB(hsl[0], hsl[1], hsl[2]);
        r = rgb[0];
        g = rgb[1];
        b = rgb[2];
        val = ( a << 24 ) | ( r << 16 ) | ( g << 8 ) | b;
        return val;
    }

    private int[] convertRGBToHSL(int rval,int gval, int bval) {
        float r, g, b;
        r = rval / 255.0f;
        g = gval / 255.0f;
        b = bval / 255.0f;
        float maxColor = Math.max(r, Math.max(g, b));
        float minColor = Math.min(r, Math.min(g, b));
        // hue, saturation, lightness
        float h, s, l;
        if((r == g)&&(g == b)) {
            h = 0;
            s = 0;
            l = r;
        } else {
            l = (minColor + maxColor) / 2;
            float d = maxColor - minColor;
            if (l < 0.5f)
                s = d / (maxColor + minColor);
            else
                s = d / (2.0f - maxColor - minColor);
            if (r == maxColor)
                h = (g - b) / (maxColor - minColor);
            else if (g == maxColor)
                h = 2.0f + (b - r) / (maxColor - minColor);
            else
                h = 4.0f + (r - g) / (maxColor - minColor);
            h /= 6; // normalize
            if(h < 0)
                h++;
        }
        int hval, sval, lval;
        hval = (int)(h * 360.0f);
        sval = (int)(s * 255.0f);
        lval = (int)(l * 255.0f);
        return new int[] {hval,sval,lval};
    }

    private int[] convertHSLToRGB(int hval, int sval, int lval) {
        float r, g, b, h, s, l;
        h = hval / 360.0f;
        s = sval / 255.0f;
        l = lval / 255.0f;
        float temp1, temp2, tempr, tempg, tempb;
        if(s == 0){
            r = l;
            g = l;
            b = l;
        }
        else {
            //Set the temporary values
            if(l < 0.5)
                temp2 = l * (1 + s);
            else
                temp2 = (l + s) - (l * s);
            temp1 = 2 * l - temp2;
            tempr = h + 1.0f / 3.0f;
            if (tempr > 1)
                tempr--;
            tempg = h;
            tempb = h - 1.0f / 3.0f;
            if (tempb < 0)
                tempb++;

            //Red
            if (tempr < 1.0f / 6.0f)
                r = temp1 + (temp2 - temp1) * 6.0f * tempr;
            else if (tempr < 0.5f)
                r = temp2;
            else if (tempr < 2.0f / 3.0f)
                r = temp1 + (temp2 - temp1) * ((2.0f / 3.0f) - tempr) * 6.0f;
            else
                r = temp1;

            //Green
            if (tempg < 1.0f / 6.0f)
                g = temp1 + (temp2 - temp1) * 6.0f * tempg;
            else if (tempg < 0.5)
                g = temp2;
            else if(tempg < 2.0 / 3.0) g = temp1 + (temp2 - temp1) * ((2.0f / 3.0f) - tempg) * 6.0f;
            else g = temp1;

            //Blue
            if (tempb < 1.0f / 6.0f)
                b = temp1 + (temp2 - temp1) * 6.0f * tempb;
            else if (tempb < 0.5f)
                b = temp2;
            else if (tempb < 2.0f / 3.0f)
                b = temp1 + (temp2 - temp1) * ((2.0f / 3.0f) - tempb) * 6.0f;
            else
                b = temp1;
        }
        int rval, gval, bval;
        rval = (int)(r * 255.0f);
        gval = (int)(g * 255.0f);
        bval = (int)(b * 255.0f);
        return new int[] {rval, gval, bval};
    }
}
