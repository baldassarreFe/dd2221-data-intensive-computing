package lab1code;

import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class XmlUtils {
  private static Logger logger = Logger.getLogger(XmlUtils.class);

  public static Map<String, String> parse(String xml) {
      xml = xml.trim();
      Map<String, String> map = new HashMap<String, String>();
      try {
          String[] tokens = xml.substring(5, xml.length() - 3).split("\"");
          for (int i = 0; i < tokens.length - 1; i += 2) {
              String key = tokens[i].trim();
              String val = tokens[i + 1];
              map.put(key.substring(0, key.length() - 1), val);
          }
      } catch (StringIndexOutOfBoundsException e) {
          logger.warn(e + " at line " + xml);
          return new HashMap<String, String>();
      }
      return map;
  }
}
