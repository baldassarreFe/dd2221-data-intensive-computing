package lab1code;

import lab1code.XmlUtils;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

public class TopTenMapper extends Mapper<Object, Text, NullWritable, Text> {
    // Stores a map of user reputation to the record
    private TreeMap<Integer, Text> repToRecordMap = new TreeMap<Integer, Text>();
    private Logger logger = Logger.getLogger(TopTenMapper.class);

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        Map<String, String> parsed = XmlUtils.parse(value.toString());
        if (!parsed.containsKey("Id"))
          return;

        try {
          Integer reputation = Integer.valueOf(parsed.get("Reputation"));
          repToRecordMap.put(reputation, new Text(value));
        } catch(NumberFormatException e) {
          logger.warn("Id: "+ parsed.get("Id") + " invalid reputation " + parsed.get("Reputation"));
          return;
        }

        // If we have more than ten records, remove the one with the lowest reputation.
        if (repToRecordMap.size() > 10) {
            repToRecordMap.pollFirstEntry();
        }
    }

    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Output our ten records to the reducers with a null key
        for (Text t : repToRecordMap.values())
            context.write(NullWritable.get(), t);
    }
}
