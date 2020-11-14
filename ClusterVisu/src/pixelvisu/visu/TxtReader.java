package pixelvisu.visu;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TxtReader {
	
    public String read(String path) {  	
        BufferedReader br = null;
        String line = "";
        int count = 0; int row = 0;
        String [] storage = new String [200000/1000];

        String json = "";
        
        
        for (int i = 0; i<storage.length;i++) storage[i] = "";
        
        try {
            br = new BufferedReader(new FileReader(path));
            
            
            while ((line = br.readLine()) != null) {

                // use comma as separator
                if(count ==1000) {
                	count =0;row++;}

                storage[row]+=line;

                count++;
            }
            
            //combining storage
            for (int i = 0; i<storage.length;i++) json+=storage[i];
            
            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return json;

    }

}
