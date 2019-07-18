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
import java.util.stream.IntStream;

public class YTPGenerator {

    public static double MAX_STREAM_DURATION = 0.4; //default: 2s
    public static double MIN_STREAM_DURATION = 0.2; //default: 0.2s
    public static int MAX_CLIPS = 20; //default: 5 clips
    public static String INPUT_FILE; //Input video file
    public static String OUTPUT_FILE; //the video file that will be produced in the end

    public boolean effect1;
    public boolean effect2;
    public boolean effect3;
    public boolean effect4;
    public boolean effect5;
    public boolean effect6;
    public boolean effect7;
    public boolean effect8;
    public boolean effect9;
    public boolean effect10;
    public boolean effect11;
    public boolean insertTransitionClips;

    public Utilities toolBox = new Utilities();

    public void configurate() {
        //add some code to load this from a .cfg file later
        toolBox.FFMPEG = "ffmpeg";
        toolBox.FFPROBE = "ffprobe";
        toolBox.MAGICK = "magick";
        toolBox.TEMP = "temp/job_" + System.currentTimeMillis() + "/";
        new File(toolBox.TEMP).mkdir();
        toolBox.SOURCES = "sources/";
        toolBox.SOUNDS = "sounds/";
        toolBox.MUSIC = "music/";
        toolBox.RESOURCES = "resources/";

        effect1=true;
        effect2=true;
        effect3=true;
        effect4=true;
        effect5=true;
        effect6=true;
        effect7=true;
        effect8=true;
        effect9=true;
        effect10=true;
        effect11=true;

        insertTransitionClips=true;
    }

    EffectsFactory effectsFactory = new EffectsFactory(toolBox);
    ArrayList<String> sourceList = new ArrayList<String>();
    public volatile boolean done = false;
    public volatile double doneCount = 0;

    public YTPGenerator(String output) {
        this.OUTPUT_FILE = output;
        //configurate();
    }

    public YTPGenerator(String output, double min, double max) {
        this.OUTPUT_FILE = output;
        this.MIN_STREAM_DURATION = min;
        this.MAX_STREAM_DURATION = max;
        //configurate();
    }
    public YTPGenerator(String output, double min, double max, int maxclips) {
        this.OUTPUT_FILE = output;
        this.MIN_STREAM_DURATION = min;
        this.MAX_STREAM_DURATION = max;
        this.MAX_CLIPS = maxclips;
        //configurate();
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
        System.out.println("My FFMPEG is: " + toolBox.FFMPEG);
        System.out.println("My FFPROBE is: " + toolBox.FFPROBE);
        System.out.println("My MAGICK is: " + toolBox.MAGICK);
        System.out.println("My TEMP is: " + toolBox.TEMP);
        System.out.println("My SOUNDS is: " + toolBox.SOUNDS);
        System.out.println("My SOURCES is: " + toolBox.SOURCES);
        System.out.println("My MUSIC is: " + toolBox.MUSIC);
        System.out.println("My RESOURCES is: " + toolBox.RESOURCES);
        Thread vidThread = new Thread() {
            public void run() {
                if (sourceList.isEmpty()) {
                    System.out.println("No sources added...");
                    return;
                }

                System.out.println("poop_1");
                File out = new File(OUTPUT_FILE);
                if (out.exists()) {
                    out.delete();
                }
                cleanUp();

                try {
                    PrintWriter writer = new PrintWriter(toolBox.TEMP+"concat.txt", "UTF-8");
                    IntStream.range(0, MAX_CLIPS).parallel().forEach(i -> {
                        String sourceToPick = sourceList.get(toolBox.randomInt(0, sourceList.size() - 1));
                        System.out.println(sourceToPick);
                        System.out.println(toolBox.getLength(sourceToPick));
                        TimeStamp boy = new TimeStamp(Double.parseDouble(toolBox.getLength(sourceToPick)));
                        System.out.println(boy.getTimeStamp());
                        System.out.println("STARTING CLIP " + "video" + i);
                        TimeStamp startOfClip = new TimeStamp(randomDouble(0.0, boy.getLengthSec() - MAX_STREAM_DURATION));
                        //System.out.println("boy seconds = "+  boy.getLengthSec());
                        TimeStamp endOfClip = new TimeStamp(startOfClip.getLengthSec() + randomDouble(MIN_STREAM_DURATION, MAX_STREAM_DURATION));
                        System.out.println("Beginning of clip " + i + ": " + startOfClip.getTimeStamp());
                        System.out.println("Ending of clip " + i + ": " + endOfClip.getTimeStamp() + ", in seconds: ");
                        String clipToWorkWith = toolBox.TEMP+"video" + i + ".mp4";
                        if (toolBox.randomInt(0, 15) == 15 && insertTransitionClips==true) {
                            System.out.println("Tryina use a diff source");
                            toolBox.copyVideo(effectsFactory.pickSource(), clipToWorkWith);
                        } else {
                            toolBox.snipVideo(sourceToPick, startOfClip, endOfClip, clipToWorkWith);
                        }
                        //Add a random effect to the video
                        int effect = toolBox.randomInt(0, 16);
                        System.out.println("STARTING EFFECT ON CLIP " + i + " EFFECT" + effect);
                        switch (effect) {
                            case 1:
                                //random sound
                                if (effect1==true)
                                effectsFactory.effect_RandomSound(clipToWorkWith);
                                break;
                            case 2:
                                if (effect2==true)
                                //random sound
                                effectsFactory.effect_RandomSoundMute(clipToWorkWith);
                                break;
                            case 3:
                                if (effect3==true)
                                effectsFactory.effect_Reverse(clipToWorkWith);
                                break;
                            case 4:
                                if (effect4==true)
                                effectsFactory.effect_SpeedUp(clipToWorkWith);
                                break;
                            case 5:
                                if (effect5==true)
                                effectsFactory.effect_SlowDown(clipToWorkWith);
                                break;
                            case 6:
                                if (effect6==true)
                                effectsFactory.effect_Chorus(clipToWorkWith);
                                break;
                            case 7:
                                if (effect7==true)
                                effectsFactory.effect_Vibrato(clipToWorkWith);
                                break;
                            case 8:
                                if (effect8==true)
                                effectsFactory.effect_HighPitch(clipToWorkWith);
                                break;
                            case 9:
                                if (effect9==true)
                                effectsFactory.effect_LowPitch(clipToWorkWith);
                                break;
                            case 10:
                                if (effect10==true)
                                effectsFactory.effect_Dance(clipToWorkWith);
                                break;
                            case 11:
                                if (effect11==true && toolBox.randomInt(0, 99) < 50)
                                effectsFactory.effect_Squidward(clipToWorkWith);
                                break;
                            default:
                                //toolBox.convertVideo(clipToWorkWith);
                                break;
                        }
                        doneCount += 1.0 / MAX_CLIPS;
                    });
                    for (int i = 0; i < MAX_CLIPS; i++) {
                        if (new File(toolBox.TEMP+"video" + i + ".mp4").exists()) {
                            writer.write("file 'video" + i + ".mp4'\n"); //writing to same folder
                        }
                    }
                    writer.close();
                    //Thread.sleep(10000);
                    toolBox.concatenateVideo(MAX_CLIPS, OUTPUT_FILE);
                    //Thread.sleep(4000);

                } catch (Exception ex) { ex.printStackTrace();
                }
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
