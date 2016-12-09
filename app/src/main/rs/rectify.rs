#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.example.rs)

rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    float4 color = convert_float4(*v_in);

    if(color.r < 127) color.r = 127;
    if(color.g < 127) color.g = 127;
    if(color.b < 127) color.b = 127;
    float4 pixel = {color.r, color.g, color.b, color.a};


    *v_out = convert_uchar4(pixel);
}
