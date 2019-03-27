package zone.arctic.ytpplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

/**
 * FFMPEG utilities toolbox for YTP+
 *
 * @author benb
 */
public class FFmpegUtils {
    
    public String FFPROBE;
    public String FFMPEG;
    
    public FFmpegUtils(String ffmpeg, String ffprobe) {
        this.FFMPEG = ffmpeg;
        this.FFPROBE = ffprobe;
    }
    /**
     * Return the length of a video (in seconds)
     *
     * @param video input video filename to work with
     * @return Video length as a string (output from ffprobe)
     */
    public String getVideoLength(String video){
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(FFPROBE 
                    + " -v error"
                    + " -sexagesimal"
                    + " -show_entries format=duration"
                    + " -of default=noprint_wrappers=1:nokey=1"
                    + " " + video);
            BufferedReader stdInput = new BufferedReader(new 
            InputStreamReader(proc.getInputStream()));
            String s;
            
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
            String command1 = FFMPEG 
                    + " -i " + video
                    + " -ss " + startTime.getTimeStamp()
                    + " -to " + endTime.getTimeStamp()
                    + " " + output + ".mp4";
            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            if (exitValue==1) {
                System.out.println("ERROR");
                System.exit(0);
            }
        } catch (Exception ex) {System.out.println(ex);}
    }   
    
    /**
     * Concatenate videos from a concatenation .txt document.
     *
     * @param in output filename in which the concatenated video will reside
     * @param out input filename of concatenation txt
     */
    public void concatenateVideo(String in, String out) {
        try {
            File export = new File(out);
            File temp = new File("temp/concatenate_temp_"+System.currentTimeMillis()+".mp4");
            if (export.exists())
                export.delete();
            if (temp.exists())
                temp.delete();
            
            String command1 = FFMPEG 
                    + " -f concat"
                    + " -i " + in
                    + " -c copy" 
                    + " " + temp.getName();
            String command2 = FFMPEG
                    + " -i " + temp.getName()
                    + " -c:v libx264"
                    + " -crf 28"
                    + " -preset fast"
                    + " -ac 1"
                    + " " + out;
            CommandLine cmdLine = CommandLine.parse(command1);
            DefaultExecutor executor = new DefaultExecutor();
            int exitValue = executor.execute(cmdLine);
            
            cmdLine = CommandLine.parse(command2);
            executor = new DefaultExecutor();
            exitValue = executor.execute(cmdLine);
            
            temp.delete();
            
        } catch (Exception ex) {System.out.println(ex);}
    }
}
