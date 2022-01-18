package ca.ids.abms.modules.common.dto;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.UUID;

import ca.ids.abms.modules.flightmovements.FlightMovementController;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Media document file content, name and MIME type
 */
public class MediaDocument {
    private static final Logger LOG = LoggerFactory.getLogger(MediaDocument.class);

    private final String fileName;

    private final String contentType;
    
    private File file;

    private final String tmpdir = System.getProperty("java.io.tmpdir");
    private FileOutputStream outputStream;

    public MediaDocument (final String fileName, final String contentType, final byte[] data) {
        this.contentType = contentType;
        this.fileName = fileName;

        LOG.debug("Size byte" + data.length);
        //TODO: use tempFile
        //Creo un file Temploraneo
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        this.file = Paths.get(tmpdir,uuidAsString).toFile();
        try {
			outputStream = new FileOutputStream(Paths.get(tmpdir,uuidAsString).toString());
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        //eseguo la scrittura nel buffer
        try {
			outputStream.write(data);			
            outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public MediaDocument(final String fileName, final String contentType, final FileOutputStream outputStream,File file) {
    	  this.contentType = contentType;
          this.fileName = fileName;
          this.outputStream = outputStream;
          this.file = file;
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

    @Deprecated
    public byte[] data() {
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

    //Append data
    public void appendData(final byte[] data) {
        LOG.debug("Size byte" + data.length);
    	try {
			outputStream.write(data);
            outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    public FileOutputStream getOutputStream() {
        return outputStream;
    }

    public RandomAccessFile getReader() throws FileNotFoundException {
    	 return  new RandomAccessFile(this.file.toPath().toString(), "r");
    }

    public File getFile() throws FileNotFoundException {
   	 return this.file;
   }
}
