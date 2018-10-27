/**
 * created by Thaipad 2018
 */

package pro.thaipad;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReportPrint implements Closeable {

    private final BufferedWriter writer;
    private final ReportSettings reportSettings;
    private String header;
    private String delimiterLine;

    public ReportPrint(String fileName, ReportSettings reportSettings) throws IOException {
        this(fileName, reportSettings, StandardCharsets.UTF_16);
    }

    public ReportPrint(String fileName, ReportSettings reportSettings, Charset charset) throws IOException {
        this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), charset));
        this.reportSettings = reportSettings;

        StringBuilder builder = new StringBuilder(reportSettings.getColumnDelimiter());
        reportSettings.getColumnsSettings().forEach(s-> builder
                .append(" ")
                .append(String.format("%-" + s.getWidth() + "s", s.getTitle()))
                .append(" ")
                .append(reportSettings.getColumnDelimiter()));
        header = builder.toString();

        delimiterLine = String.format("%" + header.length() + "s"," ").replaceAll(" ",reportSettings.getRowDelimiter());

    }

    public void print(MultiStringStream multiStringStream) throws IOException {

        int restRowPage = reportSettings.getHeight() - 1;
        writer.write(getHeader());
        while (multiStringStream.ready()) {
            List<String> multiLine = multiStringStream.readRow();
            if (restRowPage < (multiLine.size() + 1)) {
                getLine(reportSettings.getPageDelimiter());
                getLine(getHeader());
                restRowPage = reportSettings.getHeight() - 2;
            }
            restRowPage -= (multiLine.size() + 1);
            getLine(getDelimeterLine());
            for (String oneLine : multiLine) {
                getLine(oneLine);
            }
        }
    }

    private void getLine(String line) throws IOException {
        writer.newLine();
        writer.write(line);
    }

    private String getHeader() {
        return header;
    }

    private String getDelimeterLine() {
        return delimiterLine;
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
