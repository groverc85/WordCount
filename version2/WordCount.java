package WordCount;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;

public class WordCount
{
    private static String file_path = "1.txt";
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, InterruptedException
	{
		File file = new File(file_path);
		// create file channel
	    FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel();
	    // create file lock
	    FileLock lock = fileChannel.lock(0, file.length(), false);
	    // create file channel map
	    MappedByteBuffer mbBuf = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
	    String str = Charset.forName("UTF-8").decode(mbBuf).toString() + "\r\n";
		
	    // generate large file by copying 1.txt 10000 times, file size = 700MB for testing purposes 
		File file2 = new File("2.txt");
		if (file2.exists())
		{
			file2.delete();
		}	
        FileOutputStream outputFileStream = new FileOutputStream(file2 ,true);
        for (int i = 0; i < 10; i++)
        {
        	outputFileStream.write(str.getBytes("UTF-8"));
        }      
        outputFileStream.close();
        lock.release();
        fileChannel.close();

        // calculate running time
        long start = System.currentTimeMillis();
        // process file and produce result
        ProcessText fileProcess = new ProcessText(file2, 4, 1024 * 1024 * 10); // File, # of threads, partition times
        fileProcess.process();
        
		long end = System.currentTimeMillis();  
        System.out.println("Running time：" + (end - start) / 1000.0 + "seconds"); 
	}
}