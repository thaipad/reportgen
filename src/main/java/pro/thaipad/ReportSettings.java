/**
 * created by Thaipad 2018
 */

/**
 * class for parsing settings of report from xml and their storing
 * has also a set of characters for drawing vertical and horisontal lines and other
 * (I think we can put these delimiters to xml too)
 */
package pro.thaipad;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportSettings {

    private final static String PAGE = "page";
    private final static String PAGE_WIDTH = "width";
    private final static String PAGE_HEIGHT = "height";
    private final static String COLUMNS = "columns";
    private final static String COLUMN = "column";
    private final static String COLUMN_TITLE = "title";
    private final static String COLUMN_WIDTH = "width";
    public final static String COLUNM_DELIMITER = "|";
    public final static String ROW_DELIMITER = "-";
    public final static String PAGE_DELIMITER = "~";

    private int width;
    private int height;
    private final List<ColumnSettings> columnsSettings;
    private String columnDelimiter = COLUNM_DELIMITER;
    private String rowDelimiter = ROW_DELIMITER;
    private String pageDelimiter = PAGE_DELIMITER;

    private final XMLStreamReader reader;

    public class ColumnSettings {
        private String title;
        private int width;

        String getTitle() {
            return title;
        }

        void setTitle(String title) {
            this.title = title;
        }

        int getWidth() {
            return width;
        }

        void setWidth(int width) {
            this.width = width;
        }
    }

    public ReportSettings(String xmlFile) throws XMLStreamException, IOException {
        width = 0;
        height = 0;
        columnsSettings = new ArrayList<>();

        reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(xmlFile));

        if (doUntil(PAGE)) {
            if (doUntil(PAGE_WIDTH)) {
                reader.next();
                width = Integer.parseInt(reader.getText());
            }
            if (doUntil(PAGE_HEIGHT)) {
                reader.next();
                height = Integer.parseInt(reader.getText());
            }
        }

        int sumOfColunmWidths = 10;
        if (doUntil(COLUMNS)) {
            ColumnSettings columnSettings;
            while ((columnSettings = getColumnSettings()) != null) {
                columnsSettings.add(columnSettings);
                sumOfColunmWidths += columnSettings.getWidth();
            }
        }

        if (sumOfColunmWidths > width) {
            System.out.println("Sum of columns width (" + sumOfColunmWidths + ") is more than page width (" + width + ")");
            System.out.println("Page width will be set in new value");
        }
        reader.close();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<ColumnSettings> getColumnsSettings() {
        return columnsSettings;
    }

    public String getColumnDelimiter() {
        return columnDelimiter;
    }

    public void setColumnDelimiter(String columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
    }

    public String getRowDelimiter() {
        return rowDelimiter;
    }

    public void setRowDelimiter(String rowDelimiter) {
        this.rowDelimiter = rowDelimiter;
    }

    public String getPageDelimiter() {
        return pageDelimiter;
    }

    public void setPageDelimiter(String pageDelimiter) {
        this.pageDelimiter = pageDelimiter;
    }

    private ColumnSettings getColumnSettings() throws XMLStreamException {
        ColumnSettings columnSettings = new ColumnSettings();

        if (doUntil(COLUMN)) {
            if (doUntil(COLUMN_TITLE)) {
                reader.next();
                columnSettings.setTitle(reader.getText());
            } else {
                return null;
            }
            if (doUntil(COLUMN_WIDTH)) {
                reader.next();
                columnSettings.setWidth(Integer.parseInt(reader.getText()));
            } else {
                return null;
            }
        } else {
            return null;
        }
        return columnSettings;
    }

    private boolean doUntil(String value) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLEvent.START_ELEMENT && value.equals(getValue())) {
                return true;
            }
        }
        return false;
    }

    private String getValue() throws XMLStreamException {
        return (reader.getEventType() == XMLEvent.CHARACTERS) ? reader.getText() : reader.getLocalName();
    }

}
