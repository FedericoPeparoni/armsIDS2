package ca.ids.abms.modules.common.controllers;

import ca.ids.abms.modules.common.dto.MediaDocument;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
                .body(new ByteArrayResource(mediaDocument.data()));
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
