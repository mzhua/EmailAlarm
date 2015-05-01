package im.mz.EmailAlarm.entity;

/**
 * Created by mzhua_000 on 2015/1/13.
 */
public class FileSelectorEntity {
    private String fileName;
    private String filePath;
    private boolean isFolder;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }
}
