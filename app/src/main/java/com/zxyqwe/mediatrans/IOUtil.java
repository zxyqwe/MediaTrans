package com.zxyqwe.mediatrans;

import java.io.File;
import java.text.DecimalFormat;


class IOUtil {

    static String exchangeFileSize(String path) {
        File f = new File(path);
        DecimalFormat df = new DecimalFormat("#.##");
        char[] prefix = {' ', 'K', 'M', 'G'};
        double di = (double) f.length();
        int j = 0;
        while (di > 1024) {
            j++;
            di /= 1024;
        }
        return df.format(di) + prefix[j] + 'B';
    }
}
