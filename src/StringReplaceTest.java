import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.lang3.StringUtils;

public class StringReplaceTest {

    public static void main(String[] args)  throws Exception {
        File dir = new File("/Users/przemyslaw/Documents/intersect/ap11/ap11_samifier/test/resources/merger");
        File[] items = dir.listFiles();
        for (File item : items) {
            if (item.isDirectory()) continue;
            System.out.println(item.getName());
            if (item.getName().endsWith(".faa")) processFaa(item); 
        }

    }

    private static void processFaa(File item) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(item));
        String newFileName = item.getName().replaceAll(".faa", "_rev.faa");
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(item.getParent() +"/"+ newFileName )));
        String header = reader.readLine();
        writer.write(header+"\n");
        StringBuffer buff = new StringBuffer();
        String line = reader.readLine();
        
        while (line != null) {
            buff.append(line+"\n");
            line = reader.readLine();
        }
        String out = buff.reverse().toString();
        out = StringUtils.replaceChars(out, "ACGT", "TGCA");
        writer.write(out);
        writer.flush();
        reader.close();
        writer.close();
        
    }
}
