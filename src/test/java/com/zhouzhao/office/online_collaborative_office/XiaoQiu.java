package com.zhouzhao.office.online_collaborative_office;

import java.text.DecimalFormat;

public class XiaoQiu {


    public static void main(String[] args) {
        double a = 100;
        int b = 10;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        System.out.println("opt(a,b) = " + decimalFormat.format(opt(a, b)));
    }

    public static double opt(double height, int times) {

        if (times == 1) {
            return height;
        }
        return height + height / 2 + opt(height / 2, --times);
    }

}
