/**
 * created by Thaipad 2018
 */

package pro.thaipad;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ReportGenerator {


    public static void main(String[] argv) {

        if (argv.length < 2) {
            System.out.println("Parameters error!");
            System.out.println("Use minimum 2 parameters: [settings xml-file] [data file]");
            return;
        }
        String fileXml = argv[0];
        String fileData = argv[1];
        String fileReport = "report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYmmddHHMM")) + ".txt";
        if (argv.length > 2) {
            fileReport = argv[2];
        }

        ReportSettings reportSettings;
        try {
            reportSettings = new ReportSettings(fileXml);
        } catch (XMLStreamException | IOException e) {
            System.out.println("Error of reading xml");
            return;
        }

        try (MultiStringStream multiStringStream = new MultiStringStream(fileData, reportSettings);
                ReportPrint reportPrint = new ReportPrint(fileReport, reportSettings)){

                    reportPrint.print(multiStringStream);

        } catch (IOException e) {
            System.out.println("File error!");
        }

    }

}