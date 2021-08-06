package ca.ids.abms.modules.common.dto;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Media document file content, name and MIME type
 */
public class MediaDocument {

    private final String fileName;

    private final String contentType;

//    private  byte[] data;
    
    private final String uuidAsString;
    private final String tmpdir = System.getProperty("java.io.tmpdir");
    private FileOutputStream outputStream;

    public MediaDocument (final String fileName, final String contentType, final byte[] data) {
        this.contentType = contentType;
        this.fileName = fileName;
        UUID uuid = UUID.randomUUID();
         uuidAsString = uuid.toString();
        
    
        Paths.get(tmpdir,uuidAsString);
        try {
			outputStream = new FileOutputStream(Paths.get(tmpdir,uuidAsString).toString());
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
     //   this.data = data;
        try {
			outputStream.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public String contentType() {
        return contentType;
    }

    public int contentLength() {
  // return new CountingOutputStream(outputStream).getCount();
    	try {
			return (int) getReader().length();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
    }

    public String fileName() {
        return fileName;
    }

    public byte[] data() {
//       new CountingOutputStream(outputStream);;
    	 try (
			RandomAccessFile reader = getReader()){
			 byte[] document = new byte[(int) reader.length()];
			 reader.readFully(document);
			return document;
    
    	 } catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
      }return null;													
    }
    
    public void appendData(final byte[] data) {
    	
    	try {
			outputStream.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   //   this.data = new byte[data.length + data1.length];
    }
    
 
    public FileOutputStream getOutputStream() {
        return outputStream;
    }
//    
    public RandomAccessFile getReader() throws FileNotFoundException {
    	 return  new RandomAccessFile(Paths.get(tmpdir,uuidAsString).toString(), "r");
    }
//    
    public File getFile() throws FileNotFoundException {
   	 return (Paths.get(tmpdir,uuidAsString).toFile());
   }
}
