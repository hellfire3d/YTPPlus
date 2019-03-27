/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.arctic.ytpplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;


/**
 *
 * @author bebn
 */
public class MainApp {

    public static double MAX_STREAM_DURATION = 2;
    public static double MIN_STREAM_DURATION = 0.2;
    public static int MAX_CLIPS = 5;
    public static String INPUT_FILE = "toys.mp4";
    
    public static void main(String[] args) {
        YTPGenerator generatorMain = new YTPGenerator("toys.mp4", "outputfile.mp4");
        generatorMain.go();
    }
}
