#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.example.rs)


rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;
uint32_t width;
uint32_t height;


void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
      float w[3][3] =
                     {
                       1,2,-1,
                       2,0.25,-2,
                       1,-2,-1
                     };
    float p1,p2,p3;
    float acc1=0,acc2=0,acc3=0,acc4=0;
    if(x>0&&y>0 && x<width-1 && y<height-1){
        for(int m=0;m<3;m++){
            for(int k=0;k<3;k++) {
                uint32_t xdim=x+k-1;
                uint32_t ydim=y+m-1;
                float4 color = convert_float4(rsGetElementAt_uchar4(gIn, xdim, ydim));
                p1=(w[m][k])*color.r;
                p2=(w[m][k])*color.g;
                p3=(w[m][k])*color.b;

                acc1+=p1;
                acc2+=p2;
                acc3+=p3;
            }
        }
        float4 c = convert_float4(rsGetElementAt_uchar4(gIn, x, y));
        acc1 = clamp(acc1, 0.0, 255.0);
        acc2 = clamp(acc2, 0.0, 255.0);
        acc3 = clamp(acc3, 0.0, 255.0);
        acc4=c.a;

        float4 pixel = {acc1,acc2, acc3, acc4};
        *v_out = convert_uchar4(pixel);
        rsSetElementAt_uchar4(gOut, convert_uchar4(pixel),x-1,y-1);
    }


}

