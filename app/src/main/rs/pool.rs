#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.example.rs)

#include "rs_debug.rsh"


rs_allocation gin;
rs_allocation gout;

void root(const uchar4 * v_in, uchar4 * v_out, uint32_t x, uint32_t y){
    if((x%2 == 0) && (y%2 ==0)){
        float4 pixel1 = convert_float4(*v_in);

        uchar4 neighbor1 = rsGetElementAt_uchar4(gin,x+1,y);
        uchar4 neighbor2 = rsGetElementAt_uchar4(gin,x,y+1);
        uchar4 neighbor3 = rsGetElementAt_uchar4(gin,x+1,y+1);

        float4 pixel2 = convert_float4(neighbor1);
        float4 pixel3 = convert_float4(neighbor2);
        float4 pixel4 = convert_float4(neighbor3);

        float r = pixel1.r;
        float g = pixel1.g;
        float b = pixel1.b;
        float a = pixel1.a;

        if(pixel2.r>r) r = pixel2.r;
        if(pixel3.r>r) r = pixel3.r;
        if(pixel4.r>r) r = pixel4.r;

        if(pixel2.g>g) g = pixel2.g;
        if(pixel3.g>g) g = pixel3.g;
        if(pixel4.g>g) g = pixel4.g;

        if(pixel2.b>b) b = pixel2.b;
        if(pixel3.b>b) b = pixel3.b;
        if(pixel4.b>b) b = pixel4.b;

        if(pixel2.a>a) a = pixel2.a;
        if(pixel3.a>a) a = pixel3.a;
        if(pixel4.a>a) a = pixel4.a;

        float4 out = {r,g,b,a};

        uint32_t index_x = x-(x/2);
        uint32_t index_y = y-(y/2);




        rsSetElementAt_uchar4(gout, convert_uchar4(out),index_x,index_y);
     }

    //*v_out = convert_uchar4(out);

    }