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
        TimeStamp boy = new TimeStamp(getVideoLength(INPUT_FILE));
        System.out.println(boy.getTimeStamp());
        
        File out = new File("outputvid.mp4");
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
                    snipVideo(INPUT_FILE, startOfClip, endOfClip, "video" + i);
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
            concatenateVideo();
            //Thread.sleep(4000);
            
        } catch (Exception ex) {}
        //for (int i=0; i<100; i++) {
        cleanUp();
        
    }
    
    public static String getVideoLength(String video){
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("lib/ffprobe -v error -sexagesimal -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 " + video);
            BufferedReader stdInput = new BufferedReader(new 
            InputStreamReader(proc.getInputStream()));
            String s;
            
            while ((s = stdInput.readLine()) != null) {
                return s;
            }
        } catch (Exception ex) {System.out.println(ex); return "";}
        return "";
    }   
    
    public static void snipVideo(String video, TimeStamp startTime, TimeStamp endTime, String output){
        try {
            String command1 = "lib/ffmpeg.exe -i "+video+" -ss "+startTime.getTimeStamp()+" -to "+endTime.getTimeStamp()+" "+output+".mp4";
            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            if (exitValue==1) {
                System.out.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.out.println(ex);}
    }   
    
    public static void concatenateVideo() {
        try {
            String video = "outputvideo.mp4";
            File in = new File(video);
            File temp = new File("tempy.mp4");
            if (in.exists())
                in.delete();
            if (temp.exists())
                temp.delete();
            
            String command1 = "lib/ffmpeg.exe -f concat -i concat.txt -c copy tempy.mp4";
            String command2 = "lib/ffmpeg -i tempy.mp4 -c:v libx264 -crf 28 -preset fast -ac 1 " + video;
            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            
            cmdLine = CommandLine.parse(command2);
            executor = new DefaultExecutor();
            exitValue = executor.execute(cmdLine);
            
            temp.delete();
            
        } catch (Exception ex) {System.out.println(ex);}
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
        File mp4 = new File("temp.mp4");
        if (mp4.exists())
            mp4.delete(); 
        for (int i=0; i<MAX_CLIPS; i++) {
            File del = new File("video"+i+".mp4");
            if (del.exists()) {
                System.out.println(i + " Exists");
                del.delete();
            }
        }
    }

    public static String pickSound() {
        File[] files = new File("sounds/").listFiles();
        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        return file.getName();
    }
    public static String pickSource() {
        File[] files = new File("sources/").listFiles();
        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        return file.getName();
    }
    
    /* EFFECTS */
    public static void effect_RandomSound(String video){
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String randomSound = pickSound();
            String command = "lib/ffmpeg -i temp.mp4 -i sounds/"+randomSound+" -filter_complex \"[1:a]volume=1,apad[A];[0:a][A]amerge[out]\" -ac 2 -c:v copy -map 0:v -map [out] -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }   
    public static void effect_RandomSoundMute(String video){
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            File temp2 = new File("temp2.mp4");
            if (in.exists())
                in.renameTo(temp);
            if (temp2.exists())
                temp2.delete();
            String randomSound = pickSound();
            String command1 = "lib/ffmpeg -i temp.mp4 -vcodec copy -af \"volume=0\" temp2.mp4";
            String command2 = "lib/ffmpeg -i temp2.mp4 -i sounds/"+randomSound+" -filter_complex \"[1:a]volume=1,apad[A]; [0:a][A]amerge[out]\" -ac 2 -c:v copy -map 0:v -map [out] -y " + video;
            //String command = "lib/ffmpeg -i temp.mp4 -i sounds/"+randomSound+" -c:v copy -map 0:v:0 -map 1:a:0 -shortest "+ video;
            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            
            cmdLine = CommandLine.parse(command2);
            executor = new DefaultExecutor();
            exitValue = executor.execute(cmdLine);
            
            temp.delete();
            temp2.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }   
    public static void effect_Reverse(String video){
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            File temp2 = new File("temp2.mp4");
            if (in.exists())
                in.renameTo(temp);
            if (temp2.exists())
                temp2.delete();
            String randomSound = pickSound();
            String command1 = "lib/ffmpeg -i temp.mp4 -map 0 -c:v copy -af \"areverse\" temp2.mp4";
            String command2 = "lib/ffmpeg -i temp2.mp4 -vf reverse " + video;
            //String command = "lib/ffmpeg -i temp.mp4 -i sounds/"+randomSound+" -c:v copy -map 0:v:0 -map 1:a:0 -shortest "+ video;
            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            
            cmdLine = CommandLine.parse(command2);
            executor = new DefaultExecutor();
            exitValue = executor.execute(cmdLine);
            
            temp.delete();
            temp2.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }  
    public static void effect_SpeedUp(String video){
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = "lib/ffmpeg -i temp.mp4 -filter:v setpts=0.5*PTS -filter:a atempo=2.0 " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }   
    
    public static void effect_SlowDown(String video){
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = "lib/ffmpeg -i temp.mp4 -filter:v setpts=2*PTS -filter:a atempo=0.5 " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }   
    
    public static void effect_Chorus(String video){
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = "lib/ffmpeg -i temp.mp4 -af chorus=0.5:0.9:50|60|40:0.4|0.32|0.3:0.25|0.4|0.3:2|2.3|1.3 " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }   

    public static void effect_Vibrato(String video){
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = "lib/ffmpeg -i temp.mp4 -af vibrato=f=7.0:d=0.5 " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(ex);}
    } 
    
    public static void effect_LowPitch(String video) {
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = "lib/ffmpeg -i temp.mp4 -filter:v setpts=2*PTS -af asetrate=44100*0.5,aresample=44100 " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }

    public static void effect_HighPitch(String video) {
        try {
            File in = new File(video);
            File temp = new File("temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String command = "lib/ffmpeg -i temp.mp4 -filter:v setpts=0.5*PTS -af asetrate=44100*2,aresample=44100 " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }
}
