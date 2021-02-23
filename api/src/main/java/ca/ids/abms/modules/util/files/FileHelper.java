package ca.ids.abms.modules.util.files;

public class FileHelper {

  private FileHelper() {
    throw new IllegalStateException("Helper class, do not instantiate a new instance.");
  }

  public static String doGetDownloadContentType(final String declaredMimeType) {
    if (declaredMimeType == null || declaredMimeType.isEmpty()) {
      return "application/octet-stream";
    }
    return declaredMimeType;
  }

  public static String doGetDownloadFileName(final String declaredFileName) {
    if (declaredFileName == null || declaredFileName.isEmpty()) {
      return "invoice.dat";
    }
    return declaredFileName;
  }

}
