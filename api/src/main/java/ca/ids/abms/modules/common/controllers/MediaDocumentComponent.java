package ca.ids.abms.modules.common.controllers;

import ca.ids.abms.modules.common.dto.MediaDocument;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.function.Supplier;

public abstract class MediaDocumentComponent {

    /**
     * Create a response containing the media document file as a named attachment from the provided callback supplier.
     *
     * @param callback A function that returns a media document result.
     */
    protected final ResponseEntity<ByteArrayResource> doCreateBinaryResponse(final Supplier<MediaDocument> callback) {
        return doCreateBinaryResponse(callback.get());
    }
    protected final ResponseEntity<? extends Resource> doCreateResponse(final Supplier<MediaDocument> callback) {
        return doCreateResource(callback.get());
    }

    /**
     * Create a response containing the media document file as a named attachment.
     *
     * @param mediaDocument A media document that contains the name, type and bytes to generate a response entity
     *                      byte array resource from.
     */
    protected final ResponseEntity<ByteArrayResource> doCreateBinaryResponse(final MediaDocument mediaDocument) {

    	if (mediaDocument != null && mediaDocument.data() != null)
            return ResponseEntity.ok()
                .contentType(MediaType.valueOf(mediaDocument.contentType()))
                .header("Content-Disposition", "attachment; filename=\"" + mediaDocument.fileName() + "\"")
                .body(new ByteArrayResource( 	mediaDocument.data() ));
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    protected final ResponseEntity<StreamingResponseBody> doCreateStreamingResponse(final MediaDocument mediaDocument) {

    	if (mediaDocument != null && mediaDocument.data() != null)
            return ResponseEntity.ok()
                .contentType(MediaType.valueOf(mediaDocument.contentType()))
                .header("Content-Disposition", "attachment; filename=\"" + mediaDocument.fileName() + "\"")
                .body(( buildOutputStreamFromDocument(mediaDocument)));
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    protected final ResponseEntity<FileSystemResource> doCreateResource(final MediaDocument mediaDocument) {
    	FileSystemResource resource;
		try {
			resource = new FileSystemResource(mediaDocument.getFile());
			if (mediaDocument != null && mediaDocument.data() != null)
	            return ResponseEntity.ok()
	                .contentType(MediaType.valueOf(mediaDocument.contentType()))
	                .header("Content-Disposition", "attachment; filename=\"" + mediaDocument.fileName() + "\"")
	                .body(resource);
	        else
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

    }
    
    
    private StreamingResponseBody buildOutputStreamFromDocument(final MediaDocument mediaDocument) {

        return new StreamingResponseBody() {
          @Override
          public void writeTo(OutputStream outputStream) throws IOException {
        	  try (
        				RandomAccessFile reader = mediaDocument.getReader()){
        		  //casting of length fails if file is bigger than 2147483647 bytes.
        		  byte[] bytes = new byte[(int) reader.length()];
                  int length;
                  while ((length = reader.read(bytes)) >= 0) { //NOPMD
                	  outputStream.write(bytes, 0, length);
                  }
        	    
        	    	 } catch (IOException e) {
        	 			// TODO Auto-generated catch block
        	 			e.printStackTrace();
        	      }
          }
        };
        }
    
    
        
    
    
    }

