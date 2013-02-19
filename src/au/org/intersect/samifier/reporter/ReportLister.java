package au.org.intersect.samifier.reporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReportLister {
    private Properties reportProperties;

    public ReportLister(String path) {
        reportProperties = new Properties();
        try {
            reportProperties.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getQueryByReportId(String repId) {
        String id = "rep." + repId;
        return reportProperties.get(id).toString();
    }
}
