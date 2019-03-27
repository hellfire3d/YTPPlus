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
public class YTPGenerator {

    public static double MAX_STREAM_DURATION = 2; //default: 2s
    public static double MIN_STREAM_DURATION = 0.2; //default: 0.2s
    public static int MAX_CLIPS = 5; //default: 5 clips
    public static String INPUT_FILE; //Input video file
    public static String OUTPUT_FILE; //the video file that will be produced in the end
    public static String TEMP_DIRECTORY = "temp/"; //directory for holding temp videos
    
    FFmpegUtils toolBox = new FFmpegUtils();
    
    public YTPGenerator(String input, String output) {
        this.INPUT_FILE = input;
        this.OUTPUT_FILE = output;
    }
    public YTPGenerator(String input, String output, double min, double max) {
        this.INPUT_FILE = input;
        this.OUTPUT_FILE = output;
        this.MIN_STREAM_DURATION = min;
        this.MAX_STREAM_DURATION = max;
    }
    public YTPGenerator(String input, String output, double min, double max, int maxclips) {
        this.INPUT_FILE = input;
        this.OUTPUT_FILE = output;
        this.MIN_STREAM_DURATION = min;
        this.MAX_STREAM_DURATION = max;
        this.MAX_CLIPS = maxclips;
    }

    public void go() {
        TimeStamp boy = new TimeStamp(toolBox.getVideoLength(INPUT_FILE));
        System.out.println(boy.getTimeStamp());
        
        File out = new File(OUTPUT_FILE);
        if (out.exists())
            out.delete();
        cleanUp();
        
        try {
            PrintWriter writer = new PrintWriter("concat.txt", "UTF-8");
            for (int i=0; i<MAX_CLIPS; i++) {
                TimeStamp startOfClip = new TimeStamp(randomDouble(0.0, boy.getLengthSec() - MAX_STREAM_DURATION));
                System.out.println("boy seconds = "+  boy.getLengthSec());
                TimeStamp endOfClip = new TimeStamp(startOfClip.getLengthSec() + randomDouble(MIN_STREAM_DURATION, MAX_STREAM_DURATION));
                System.out.println("Beginning of clip " + i + ": " + startOfClip.getTimeStamp());
                System.out.println("Ending of clip " + i + ": " + endOfClip.getTimeStamp());
                if (randomInt(0, 30)==30) {
                    //1 in 30 chance of a random source being used instead (broken)
                    File sourceToCopy = new File("sources/" + pickSource());
                    System.out.println("Cool source: " + sourceToCopy.getName());
                    Files.copy(sourceToCopy.toPath(), new File("video" + i + ".mp4").toPath());
                }
                else {
                    toolBox.snipVideo(INPUT_FILE, startOfClip, endOfClip, "video" + i);
                    if (new File("video" + i + ".mp4").exists()) {
                        writer.write("file 'video" + i + ".mp4'\n");
                    }
                }
                //Add a random effect to the video
                int effect = randomInt(0,12);
                System.out.println("Effect: " + effect);
                switch (effect)
                {
                    case 1:
                        //random sound
                        effect_RandomSound("video" + i +".mp4");
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        //random sound
                        effect_RandomSoundMute("video" + i +".mp4");
                        break;
                    case 6:
                        //random sound
                        effect_Reverse("video" + i +".mp4");
                        break;
                    case 7:
                        effect_SpeedUp("video" + i +".mp4");
                        break;
                    case 8:
                        effect_SlowDown("video" + i +".mp4");
                        break;
                    case 9:
                        effect_Chorus("video" + i +".mp4");
                        break;
                    case 10:
                        effect_Vibrato("video" + i +".mp4");
                        break;
                    case 11:
                        effect_LowPitch("video" + i +".mp4");
                        break;
                    case 12:
                        effect_HighPitch("video" + i +".mp4");
                        break;
                    default:
                        break;
                }
                
            }
            writer.close();
            //Thread.sleep(10000);
            toolBox.concatenateVideo("concat.txt", OUTPUT_FILE);
            //Thread.sleep(4000);
            
        } catch (Exception ex) {}
        //for (int i=0; i<100; i++) {
        cleanUp();
        
    }
    
    
    public static double randomDouble(double min, double max) {
        double finalVal = -1;
        while (finalVal<0) {
            double x = (Math.random() * ((max - min) + 0)) + min;
            finalVal=Math.round(x * 100.0) / 100.0; 
        }
        return finalVal;
    }
    
    public static int randomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
    
    
    public static void cleanUp() {
        //Create concatenation text file
        File text = new File("concat.txt");
        if (text.exists())
            text.delete();
        File mp4 = new File(TEMP_DIRECTORY + "temp.mp4");
        if (mp4.exists())
            mp4.delete(); 
        for (int i=0; i<MAX_CLIPS; i++) {
            File del = new File(TEMP_DIRECTORY + "video"+i+".mp4");
            if (del.exists()) {
                System.out.println(i + " Exists");
                del.delete();
            }
        }
    }

    
}
