/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zone.arctic.ytpplus;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

public class YTPGenerator {

    public static double MAX_STREAM_DURATION = 0.4; //default: 2s
    public static double MIN_STREAM_DURATION = 0.2; //default: 0.2s
    public static int MAX_CLIPS = 20; //default: 5 clips
    public static String INPUT_FILE; //Input video file
    public static String OUTPUT_FILE; //the video file that will be produced in the end
    
    public Utilities toolBox = new Utilities();
    
    public void configurate() {
        //add some code to load this from a .cfg file later
        toolBox.FFMPEG = "ffmpeg";
        toolBox.FFPROBE = "ffprobe";
        toolBox.MAGICK = "magick ";
        toolBox.TEMP = "D:/Documents/Projects/YTPPlus/temp/" + "job_" + System.currentTimeMillis() + "/";
        new File(toolBox.TEMP).mkdir();
        toolBox.SOURCES = "D:/Documents/Projects/YTPPlus/sources/";
        toolBox.SOUNDS = "D:/Documents/Projects/YTPPlus/sounds/";
        toolBox.MUSIC = "D:/Documents/Projects/YTPPlus/music/";
        toolBox.RESOURCES = "D:/Documents/Projects/YTPPlus/resources/";
    }
    
    EffectsFactory effectsFactory = new EffectsFactory(toolBox);
    ArrayList<String> sourceList = new ArrayList<String>();
    public volatile boolean done = false;
    public volatile double doneCount = 0;
    
    public YTPGenerator(String output) {
        this.OUTPUT_FILE = output;
        configurate();
    }
    
    public YTPGenerator(String output, double min, double max) {
        this.OUTPUT_FILE = output;
        this.MIN_STREAM_DURATION = min;
        this.MAX_STREAM_DURATION = max;
        configurate();
    }
    public YTPGenerator(String output, double min, double max, int maxclips) {
        this.OUTPUT_FILE = output;
        this.MIN_STREAM_DURATION = min;
        this.MAX_STREAM_DURATION = max;
        this.MAX_CLIPS = maxclips;
        configurate();
    }
    
    public void setMaxClips(int clips) {
        this.MAX_CLIPS = clips;
    }
    public void setMinDuration(double min) {
        this.MIN_STREAM_DURATION = min;
    }
    public void setMaxDuration(double max) {
        this.MAX_STREAM_DURATION = max;
    }
    
    public void addSource(String sourceName) {
        sourceList.add(sourceName);
    }

    public void go() {
        Thread vidThread = new Thread() {
            public void run() {
                if (sourceList.isEmpty()) {
                    System.out.println("No sources added...");
                    return;
                }

                File out = new File(OUTPUT_FILE);
                if (out.exists()) {
                    out.delete();
                }
                cleanUp();

                try {
                    PrintWriter writer = new PrintWriter(toolBox.TEMP+"concat.txt", "UTF-8");
                    for (int i = 0; i < MAX_CLIPS; i++) {
                        doneCount = (double) i/MAX_CLIPS;
                        String sourceToPick = sourceList.get(randomInt(0, sourceList.size() - 1));
                        TimeStamp boy = new TimeStamp(toolBox.getVideoLength(sourceToPick));
                        System.out.println(boy.getTimeStamp());
                        System.out.println("STARTING CLIP " + "video" + i);
                        TimeStamp startOfClip = new TimeStamp(randomDouble(0.0, boy.getLengthSec() - MAX_STREAM_DURATION));
                        //System.out.println("boy seconds = "+  boy.getLengthSec());
                        TimeStamp endOfClip = new TimeStamp(startOfClip.getLengthSec() + randomDouble(MIN_STREAM_DURATION, MAX_STREAM_DURATION));
                        System.out.println("Beginning of clip " + i + ": " + startOfClip.getTimeStamp());
                        System.out.println("Ending of clip " + i + ": " + endOfClip.getTimeStamp() + ", in seconds: ");
                        if (randomInt(0, 15) == 15) {
                            System.out.println("Tryina use a diff source");
                            toolBox.copyVideo(toolBox.SOURCES + effectsFactory.pickSource(), toolBox.TEMP+"video" + i);
                        } else {
                            toolBox.snipVideo(sourceToPick, startOfClip, endOfClip, toolBox.TEMP+"video" + i);
                        }
                        //Add a random effect to the video
                        int effect = randomInt(0, 20);
                        System.out.println("STARTING EFFECT ON CLIP " + i + " EFFECT" + effect);
                        String clipToWorkWith = toolBox.TEMP+"video" + i + ".mp4";
                        switch (effect) {
                            case 1:
                                //random sound
                                effectsFactory.effect_RandomSound(clipToWorkWith);
                                break;
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                                //random sound
                                effectsFactory.effect_RandomSoundMute(clipToWorkWith);
                                break;
                            case 6:
                                //random sound
                                effectsFactory.effect_Reverse(clipToWorkWith);
                                break;
                            case 7:
                            case 8:
                            case 9:
                                effectsFactory.effect_SpeedUp(clipToWorkWith);
                                break;
                            case 10:
                            //effectsFactory.effect_SlowDown(clipToWorkWith);
                            //break;
                            case 11:
                                effectsFactory.effect_Chorus(clipToWorkWith);
                                break;
                            case 12:
                            case 13:
                                effectsFactory.effect_Vibrato(clipToWorkWith);
                                break;
                            //effectsFactory.effect_LowPitch(clipToWorkWith);
                            //break;
                            case 14:
                                //effectsFactory.effect_Dance(clipToWorkWith);
                                break;
                            case 15:
                            case 16:
                                effectsFactory.effect_HighPitch(clipToWorkWith);
                                break;
                            case 17:
                                //effectsFactory.effect_Squidward(clipToWorkWith);
                                break;
                            default:
                                break;
                        }

                    }
                    for (int i = 0; i < MAX_CLIPS; i++) {
                        if (new File(toolBox.TEMP+"video" + i + ".mp4").exists()) {
                            writer.write("file 'video" + i + ".mp4'\n"); //writing to same folder
                        }
                    }
                    writer.close();
                    //Thread.sleep(10000);
                    toolBox.concatenateVideo(MAX_CLIPS, OUTPUT_FILE);
                    //Thread.sleep(4000);

                } catch (Exception ex) {
                }
                //for (int i=0; i<100; i++) {
                cleanUp();
                rmDir(new File(toolBox.TEMP));
                done = true;
            }
        };
        vidThread.start();
        
    }
        
    
    public boolean isDone() {
        return done;
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
    
    
    public void cleanUp() {
        //Create concatenation text file
        File text = new File(toolBox.TEMP+"concat.txt");
        if (text.exists())
            text.delete();
        File mp4 = new File(toolBox.TEMP + "temp.mp4");
        if (mp4.exists())
            mp4.delete(); 
        for (int i=0; i<MAX_CLIPS; i++) {
            File del = new File(toolBox.TEMP + "video"+i+".mp4");
            if (del.exists()) {
                System.out.println(i + " Exists");
                del.delete();
            }
        }

    }
    public void rmDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (! Files.isSymbolicLink(f.toPath())) {
                    rmDir(f);
                }
            }
        }
        file.delete();
    }
    
}
