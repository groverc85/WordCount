import java.util.*;  
import java.util.regex.*;  
import java.io.*;  

public class WordCount 
{
    private static String file_path = "1.txt";
    private static HashMap<String, Integer> result = new HashMap<String, Integer>();

    public static void main( String[] args ) throws IOException {
        File file = new File(file_path);
        String current_line = null;
        String[] current_words = null;
        BufferedReader br = null;
        BufferedWriter bw = null;

        br = new BufferedReader(new FileReader(file.getPath()));

        while((current_line = br.readLine()) != null) {
            current_words = current_line.split("[^a-zA-Z']+");
            for(int i=0; i<current_words.length; i++) {       
                if(current_words[i].equals(""))
                    continue;           
                if (result.get(current_words[i].toLowerCase()) == null){
                    result.put(current_words[i].toLowerCase(), 1);
                }
                else {
                    result.put(current_words[i].toLowerCase(), result.get(current_words[i])+1);
                }
            }
        }

        bw =new BufferedWriter(new FileWriter("result.txt"));  
        
        for (HashMap.Entry<String, Integer> entry : result.entrySet()) 
        {
            bw.write(entry.getKey() + " : " + entry.getValue());
            bw.newLine();  
        }

        bw.close();  
        br.close();
    }
}