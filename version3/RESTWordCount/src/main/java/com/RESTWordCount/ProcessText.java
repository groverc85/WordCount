package com.RESTWordCount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


public class ProcessText 
{
    private File file = null;
    private int threadNum;
    // thread table
    private Vector<WordsThread> listCountWordsThreads = null;
    private long splitSize;
    // current File position
    private long currentPos;
   
	
    public ProcessText(File file, int threadNum, long splitSize)
    {
       	// make sure at least 1 thread
    	if (threadNum < 1)
    		threadNum = 1;
    	// limit thread number to 10 for memory reason
    	if (threadNum > 10)
    		threadNum = 10;
    	// min split size: 1M
    	if (splitSize < 1024*1024)
    		splitSize = 1024*1024;
    	// max split size: 10M
    	if (splitSize > 1024 * 1024 * 10)
    		splitSize = 1024 * 1024 * 10;
    	
        this.file       = file;
        this.threadNum  = threadNum;
        this.splitSize  = splitSize;
        this.currentPos = 0;
        this.listCountWordsThreads = new Vector<WordsThread>();
    }
    
    // Add path for search value(prefex)
    @Path("{prefix}")
    @GET
    public Response process(@PathParam("prefix") String prefix) throws IOException, InterruptedException
    {
    	// create a reusable thread pool with certain number of threads
    	ExecutorService pool = Executors.newFixedThreadPool(threadNum);
    	// write file handle
        BufferedWriter bw = null;
        bw =new BufferedWriter(new FileWriter("result.txt"));
        String returnString = "";

    	while (currentPos < this.file.length())
    	{
    		WordsThread countWordsThread = null;
			
			if (currentPos + splitSize < file.length())
			{
				RandomAccessFile raf = new RandomAccessFile(file,"r");
				raf.seek(currentPos + splitSize);
				int offset = 0;				
				
				while(true)
				{
					char ch = (char)raf.read();
					// end of file
					if (-1 == ch)
						break;
					if(false == Character.isLetter(ch) && '\'' != ch)
						break;
					offset++;
				}	
				
				countWordsThread = new WordsThread(file, currentPos, splitSize + offset);
				currentPos += splitSize + offset;
				
				raf.close();
			}
			else
			{
				countWordsThread = new WordsThread(file, currentPos, file.length() - currentPos);
				currentPos = file.length();
			}
			
			Thread thread = new Thread(countWordsThread);
			pool.execute(thread);
			listCountWordsThreads.add(countWordsThread);
    	}
    	
    	pool.shutdown(); 
    	
    	// if waiting time is longer than 1 second, close the thread pool
        while(!pool.isTerminated())
        	pool.awaitTermination(1,TimeUnit.SECONDS); 
       
        // final count process, use TreeMap to order result alphabetically(better option for prefix search)
        TreeMap<String, Integer> tMap = new TreeMap<String, Integer>();
    	
    	for (int loop = 0; loop < listCountWordsThreads.size(); loop++)
    	{
    		Map<String, Integer> hMap = listCountWordsThreads.get(loop).getResultMap();
            
            Set<String> keys = hMap.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                
                if(key.equals(""))
					continue;			
			    if (tMap.get(key) == null)
			    	tMap.put(key, hMap.get(key));
			    else
			    	tMap.put(key, tMap.get(key) + hMap.get(key));
            }
    	}
    	
    	Set<String> keys = tMap.keySet();
    	Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (key.startsWith(prefix)) {
            	bw.write(key + " : " + tMap.get(key));
                bw.newLine();
                returnString = returnString + key + " : " + tMap.get(key).toString() + "\n"; 
            }
        }
        System.out.println(returnString);
        
        bw.close();
        
        // HTTP response
		return Response.status(200).entity(returnString).build();
    }
}