/**
 * created by Thaipad 2018
 */

/**
 * This class works like a stream reader, but
 * it can read a hole row with wrapped content of columns
 * (implement by returning List of strings)
 */

package pro.thaipad;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MultiStringStream implements Closeable {

    private static final String DELIMITER = "\t";

    private final BufferedReader reader;
    private final ReportSettings reportSettings;

    public MultiStringStream(String fileName, ReportSettings reportSettings) throws IOException {
        this(fileName, reportSettings, StandardCharsets.UTF_16);
    }

    public MultiStringStream(String fileName, ReportSettings reportSettings, Charset charset) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName) , StandardCharsets.UTF_16));
        this.reportSettings = reportSettings;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public List<String> readRow() throws IOException {
        String colDelimiter = reportSettings.getColumnDelimiter();
        List<StringBuilder> multiLine = new ArrayList<>();
        String line = readLine();
        String[] fieldsOfLine = line.split(DELIMITER);
        StringBuilder blankLine = new StringBuilder();
        for (int column = 0; column < fieldsOfLine.length; ++column) {
            if (column <= reportSettings.getColumnsSettings().size()) {
                int widthColumn = reportSettings.getColumnsSettings().get(column).getWidth();
                List<String> wrapColumn =
                        getWrapString(fieldsOfLine[column], widthColumn)
                        .stream()
                        .map(s-> colDelimiter + " " + String.format("%-" + (widthColumn + 1) +"s", s))
                        .collect(Collectors.toList());
                if (column == 0) {
                    wrapColumn.forEach(s->multiLine.add(new StringBuilder(s)));
                } else {
                    for (int i = 0; i < wrapColumn.size(); ++i) {
                        if (i >= multiLine.size()) {
                            multiLine.add(new StringBuilder(blankLine));
                        }
                        multiLine.get(i).append(wrapColumn.get(i));
                    }
                }
                blankLine.append(colDelimiter + " " + String.format("%-" + (widthColumn + 1) +"s", " "));
            }
        }
        return multiLine.stream().map(s-> s.append(colDelimiter).toString()).collect(Collectors.toList());
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public boolean ready() throws IOException {
        return reader.ready();
    }

    private List<String> getWrapString(String original, int width) {
        Pattern pattern = Pattern.compile("[^(A-Za-zА-Яа-я0-9)]+"); // regex = any char except letters and digits
        Matcher matcher = pattern.matcher(original);
        List<String> setOfWords = new ArrayList<>();
        List<String> wrappedString = new ArrayList<>();

//      form list of separate words including spaces and others (or parts of long words)
        int begin = 0;
        while (matcher.find()) {
            String word = original.substring(begin, matcher.start());
            while (word.length() > width) {
                setOfWords.add(word.substring(0, width));
                word = word.substring(width);
            }
            setOfWords.add(word);
            setOfWords.add(original.substring(matcher.start(), matcher.end()));
            begin = matcher.end();
        }
        String word = original.substring(begin);
        while (word.length() > width) {
            setOfWords.add(word.substring(0, width));
            word = word.substring(width);
        }
        setOfWords.add(word);

//      union short words to one string
        String newstr = "";
        for (String str : setOfWords) {
            newstr = newstr.replaceFirst("^\\s+",""); // trim leading spaces
            if (!newstr.isEmpty() || !str.trim().isEmpty()) {
                if (str.length() > width - newstr.length()) {
                    wrappedString.add(newstr);
                    newstr = "";
                }
                newstr += str;
            }
        }
        if (!newstr.isEmpty()) {
            wrappedString.add(newstr);
        }

        return wrappedString;
    }
}
