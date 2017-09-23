package lab1code;

import lab1code.XmlUtils;

import java.io.IOException;
import java.util.StringTokenizer;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;

public class TopTenReducer extends Reducer<NullWritable, Text, NullWritable, Text> {
    // Stores a map of user reputation to the record
    private TreeMap<Integer, Text> repToRecordMap = new TreeMap<Integer, Text>();
    private Logger logger = Logger.getLogger(TopTenReducer.class);

    public void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        for (Text value : values) {
            Map<String, String> parsed = XmlUtils.parse(value.toString());

            try {
              Integer reputation = Integer.valueOf(parsed.get("Reputation"));
              repToRecordMap.put(reputation, new Text(value));
            } catch(NumberFormatException e) {
              logger.warn("Id: "+ parsed.get("Id") + " invalid reputation" + parsed.get("Reputation"));
            }

            // If we have more than ten records, remove the one with the lowest reputation.
            if (repToRecordMap.size() > 10) {
                repToRecordMap.pollFirstEntry();
            }
        }

        for (Text t : repToRecordMap.descendingMap().values()) {
            // Output our ten records to the file system with a null key
            context.write(NullWritable.get(), t);
        }
    }
}
