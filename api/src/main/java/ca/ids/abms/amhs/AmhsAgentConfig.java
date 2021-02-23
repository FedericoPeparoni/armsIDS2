package ca.ids.abms.amhs;

import java.util.List;

public class AmhsAgentConfig {
    
    public static class File {
        private final String filename;
        private final String content;
        private final boolean merge;
        
        public String getFilename() {
            return filename;
        }
        public String getContent() {
            return content;
        }
        public boolean isMerge() {
            return merge;
        }
        public File (final String filename, final String content) {
            this (filename, content, false);
        }
        public File (final String filename, final String content, final boolean merge) {
            this.filename = filename;
            this.content = content;
            this.merge = merge;
        }
    }
    
    
    public AmhsAgentHostConfig getHost() {
        return host;
    }
    public List<File> getFiles() {
        return files;
    }
    
    public AmhsAgentConfig (final AmhsAgentHostConfig host, final List <File> files) {
        this.host = host;
        this.files = files;
    }
    
    private final AmhsAgentHostConfig host;
    private final List <File> files;

}
