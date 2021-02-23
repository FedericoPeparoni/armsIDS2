package ca.ids.abms.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
public class XmlDocumentSerializer {
    
    public XmlDocumentSerializer (final TransformerFactory transformerFactory) {
        this.transformerFactory = transformerFactory;
    }
    
    public String toString (final Document doc) {
        try {
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            write (doc, stream);
            return stream.toString("UTF-8");
        }
        catch (final UnsupportedEncodingException x) {
            throw new RuntimeException (x);
        }
    }
    
    public void write (final Document doc, final OutputStream outputStream) {
        try {
            final Transformer t = transformerFactory.newTransformer();
            final Result output = new StreamResult (outputStream);
            final Source input = new DOMSource (doc);
            
            t.setOutputProperty (OutputKeys.INDENT, "yes");
            t.setOutputProperty ("{http://xml.apache.org/xslt}indent-amount", "2");            
            // Log the XML
            if (logger.isTraceEnabled()) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final Result logOutput = new StreamResult (baos);
                t.transform (input, logOutput);
                logger.trace ("XML:\n{}", baos.toString("UTF-8"));
            }
            t.transform (input, output);
        }
        catch (final TransformerException | IOException x) {
            throw new RuntimeException (x);
        }
    }
    
    private final TransformerFactory transformerFactory;
    private final Logger logger = LoggerFactory.getLogger (XmlDocumentSerializer.class);
}
