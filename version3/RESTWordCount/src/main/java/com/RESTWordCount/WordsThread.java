package com.RESTWordCount;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// thread class
public class WordsThread implements Runnable
{
    private FileChannel fileChannel = null;
    private FileLock lock = null;
    private MappedByteBuffer mbBuf = null;
    private Map<String, Integer> hashMap = null;
     
    @SuppressWarnings("resource")
	public WordsThread(File file, long start, long size) // File, start position, map file size
    {
        try 
        {
        	// Get current file channel
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
            // lock part of current file
            lock = fileChannel.lock(start, size, false);
            // create memory map for the current file parts, split if needed
            mbBuf = fileChannel.map(FileChannel.MapMode.READ_ONLY, start, size);
            // create HashMap to store result
            hashMap = new HashMap<String,Integer>();
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
	@Override
    public void run() 
    {
        String str = Charset.forName("UTF-8").decode(mbBuf).toString();
        Stream<String> stream = Stream.of(str.toLowerCase());
        // split words and store results in HashMap
        hashMap=stream.flatMap(s -> Stream.of(s.split("[^a-zA-Z']+")))
        		.filter(word -> word.length() > 0)
                .collect(Collectors.toMap(s -> s, s -> 1, Integer::sum)); 
        try 
        {
            lock.release();
            fileChannel.close();
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        return;
    }
     
	// get result of current thread
    public Map<String, Integer> getResultMap()
    {
        return hashMap;
    }
}