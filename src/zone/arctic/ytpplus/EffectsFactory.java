/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.arctic.ytpplus;

import java.io.File;
import java.util.Random;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import static zone.arctic.ytpplus.YTPGenerator.TEMP_DIRECTORY;

/**
 *
 * @author benb
 */
public class EffectsFactory {
    
    public String pickSound() {
        File[] files = new File("sounds/").listFiles();
        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        return file.getName();
    }
    public String pickSource() {
        File[] files = new File("sources/").listFiles();
        Random rand = new Random();
        File file = files[rand.nextInt(files.length)];
        return file.getName();
    }
    
    /* EFFECTS */
    public void effect_RandomSound(String video){
        try {
            File in = new File(video);
            File temp = new File(TEMP_DIRECTORY + "temp.mp4");
            if (in.exists())
                in.renameTo(temp);
            String randomSound = pickSound();
            String command = FFMPEG
                    + " -i " + TEMP_DIRECTORY + "temp.mp4"
                    + " -i sounds/"+randomSound
                    + " -filter_complex \"[1:a]volume=1,apad[A];[0:a][A]amerge[out]\""
                    + " -ac 2"
                    + " -c:v copy"
                    + " -map 0:v"
                    + " -map [out]"
                    + " -y " + video;
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            temp.delete();
        } catch (Exception ex) {System.out.println(ex);}
    }   
    public void effect_RandomSoundMute(String video){
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
    public void effect_Reverse(String video){
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
    public void effect_SpeedUp(String video){
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
    
    public void effect_SlowDown(String video){
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
    
    public void effect_Chorus(String video){
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

    public void effect_Vibrato(String video){
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
    
    public void effect_LowPitch(String video) {
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

    public void effect_HighPitch(String video) {
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
