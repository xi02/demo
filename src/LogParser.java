import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogParser {
    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        String filename = "./src/test2.txt";
        if (args.length > 0) {
        	filename = args[0];
        }

        String answer = parseFile(filename);
        System.out.println(answer);
    }

    static String parseFile(String filename)
            throws FileNotFoundException, IOException {
        /*
    	 *  Don't modify this function
    	 */
        BufferedReader input = new BufferedReader(new FileReader(filename));
        List<String> allLines = new ArrayList<String>();
        String line;
        while ((line = input.readLine()) != null) {
            allLines.add(line);
        }
        input.close();

        return parseLines(allLines.toArray(new String[allLines.size()]));
    }

    private static Date convertDate(String dateString) {
	    DateFormat df = new SimpleDateFormat("MM/dd/yyyy-hh:mm:ss");
	    Date date = new Date();
	    try {
	      date = df.parse(dateString);
	    } catch (ParseException ignored) {}
	    return date;
	}
    
    static String parseLines(String[] lines) {
        // save all the status in hashmap
        // connected > 0
        // disconnected < 0
        Map<String, Integer> map = new HashMap<>();
        
        map.put("START", 0);
        map.put("CONNECTED", 1);
        map.put("DISCONNECTED", -1);
        map.put("SHUTDOWN", -1);
        
        List<Date> times = new ArrayList<>(); // a list of time
        List<String> status = new ArrayList<>(); // a list of status
        
        for (int i = 0; i < lines.length; i++){
            String[] line = lines[i].split(" :: ");
            if (!map.containsKey(line[1])){
                continue; // error log message
            }
            times.add(convertDate(line[0].substring(1, line[0].length() - 1))); // add the time
            status.add(line[1]); // add the status
        }
        
        long totalTime = times.get(times.size() - 1).getTime() - times.get(0).getTime(); // total time in log message
        long connectedTime = 0; // network connected time
        long lastTime = 0; // last connected time
        
        for (int i = 1; i < times.size(); i ++){
            String currentStatus = status.get(i);
            long currentTime = times.get(i).getTime();
            if (map.get(currentStatus) > 0){
                lastTime = currentTime; // network start to be connected. change the lastTime
            }else if (lastTime > 0){ // either disconnected or shutdown, calculate the connectedTime
                connectedTime += currentTime - lastTime;
                lastTime = -1; // disconnect
            
            }
        }
        double ratio = (double)connectedTime / totalTime * 100;
        return String.format("%d%s", (int)ratio, "%");
    }
}
