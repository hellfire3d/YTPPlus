package zone.arctic.ytpplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Random;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

/**
 * FFMPEG utilities toolbox for YTP+
 *
 * @author benb
 */
public class Utilities {

    public String FFPROBE;
    public String FFMPEG;
    public String MAGICK;

    public String TEMP = "";
    public String SOURCES = "";
    public String SOUNDS = "";
    public String MUSIC = "";
    public String RESOURCES = "";
    private Random random = new Random();

    public String getLength(String file) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(new String[] {
                FFPROBE,
                "-i", file,
                "-show_entries", "format=duration",
                "-v", "error",
                "-of", "csv=p=0"
            });
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String s;
            proc.waitFor();
            while ((s = stdInput.readLine()) != null) {
                return s;
            }
        } catch (Exception ex) {System.out.println(ex); return "";}
        return "";
    }

    /**
     * Snip a video file between the start and end time, and save it to an output file.
     *
     * @param video input video filename to work with
     * @param startTime start time (in TimeStamp format, e.g. new TimeStamp(seconds);)
     * @param endTime start time (in TimeStamp format, e.g. new TimeStamp(seconds);)
     * @param output output video filename to save the snipped clip to
     */
    public void snipVideo(String video, TimeStamp startTime, TimeStamp endTime, String output){
        try {
            int exitValue = execFFmpeg(
                "-ss", startTime.getTimeStamp(),
                "-i", video,
                "-to", endTime.getTimeStamp(),
                "-copyts",
                "-ac", "1",
                "-ar", "44100",
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-y", output
            );
            if (exitValue==1) {
                System.out.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.out.println(ex);}
    }

    /**
     * Copies a video and encodes it in the proper format without changes.
     *
     * @param video input video filename to work with
     * @param output output video filename to save the snipped clip to
     */
    public void copyVideo(String video, String output){
        try {
            int exitValue = execFFmpeg(
                "-i", video,
                "-ac", "1",
                "-ar", "44100",
                "-vf", "scale=640x480,setsar=1:1,fps=fps=30",
                "-y", output
            );
            if (exitValue==1) {
                System.out.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.out.println(ex);}
    }

    /**
     * Concatenate videos by count
     *
     * @param count number of input videos to concatenate
     * @param out output video filename
     */
    public void concatenateVideo(int count, String out) {
        try {
            File export = new File(out);

            if (export.exists())
                export.delete();

            int realcount = 0;
            CommandLine cmdLine = new CommandLine(FFMPEG);
            for (int i = 0; i < count; i++) {
                File vid = new File(TEMP + "video" + i + ".mp4");
                if (vid.exists()) {
                    cmdLine.addArgument("-i", false);
                    cmdLine.addArgument(vid.getPath(), false);
                    ++realcount;
                }
            }
            String filter = new String();
            for (int i=0; i < realcount; i++)
                filter += "[" + i + ":v:0][" + i + ":a:0]";

            cmdLine.addArguments(new String[] {
                "-filter_complex", filter + "concat=n=" + realcount + ":v=1:a=1[outv][outa]",
                "-map", "[outv]",
                "-map", "[outa]",
                "-y", out
            }, false);
            System.out.println(cmdLine);
            new DefaultExecutor().execute(cmdLine);
        } catch (Exception ex) {System.out.println(ex);}
    }

    public static int exec(String what, String ...args) throws Exception {
        CommandLine cmdLine = new CommandLine(what);
        cmdLine.addArguments(args, false);
        System.out.println("Command: " + cmdLine);
        return new DefaultExecutor().execute(cmdLine);
    }

    public int execFFmpeg(String ...args) throws Exception {
        return exec(FFMPEG, args);
    }

    public int execMagick(String ...args) throws Exception {
        return exec(MAGICK, args);
    }

    public synchronized int randomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public int randomInt() {
        return randomInt(0, (1 << 30) - 1);
    }

    public String getTempVideoName() {
        return TEMP + randomInt() + "-temp.mp4";
    }
}
