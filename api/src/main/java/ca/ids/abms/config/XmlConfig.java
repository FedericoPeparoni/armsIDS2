package ca.ids.abms.config;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XmlConfig {
    
    @Bean
    DocumentBuilderFactory xmlDocumentBuilderFactory() {
        return DocumentBuilderFactory.newInstance();
    }
    
    @Bean
    TransformerFactory xmlTransformerFactory() {
        return TransformerFactory.newInstance();
    }

}
