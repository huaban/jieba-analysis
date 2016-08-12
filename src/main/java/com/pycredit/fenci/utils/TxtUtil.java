package com.pycredit.fenci.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuming on 2016/6/6.
 */
public class TxtUtil {

    public List<String> readTxt(String path) throws IOException {
        List<String> splitResult = new ArrayList<String>();
        File file = new File(path);
        if (file.exists()) {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(inputStreamReader);
            String readLine = null;

            while ((readLine = br.readLine()) != null) {
                splitResult.add(readLine);
            }

        }
        return splitResult;
    }

    public void writeTxt(List<String> splitResult, String path) throws IOException {
        FileWriter fw = null;
        BufferedWriter bw = null;

        fw = new FileWriter(path);
        bw = new BufferedWriter(fw);
        int i;
        for (i = 0; i < splitResult.size(); i++) {
            bw.write((splitResult.get(i) + "\t"));
            bw.newLine();
        }

        bw.close();
        fw.close();


    }
}
