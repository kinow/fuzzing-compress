package br.eti.kinoshita.fuzzing_compress.zip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class TestZip {

    public native void nativeCrash();

    // based on https://github.com/floyd-fuh/AFL_GCJ_Fuzzing_Simple/blob/master/Testcase.java
    public static void main(String[] args) {
        byte[] buffer = new byte[1024];
        File zipFile = null;
        try {
            zipFile = File.createTempFile("compress-fuzzer", "FILE");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        try (BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
                FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos);) {
            String s = consoleIn.readLine();
            File tmpFile = File.createTempFile("compress-fuzzer", "INPUT");
            FileUtils.writeStringToFile(tmpFile, s);
            ZipEntry ze = new ZipEntry(tmpFile.getName());
            zos.putNextEntry(ze);
            try (FileInputStream in = new FileInputStream(tmpFile);) {
                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        } catch (Throwable t) {
            new TestZip().nativeCrash();
        }
    }
}
