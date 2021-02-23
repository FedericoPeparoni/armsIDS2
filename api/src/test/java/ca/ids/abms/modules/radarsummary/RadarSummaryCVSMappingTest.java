package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by c.talpa on 18/01/2017.
 */
public class RadarSummaryCVSMappingTest {

    private String[] record1;

    private String[] record2;

    private List<String[]> recordList;

    private RadarSummaryCSVMapping radarSummaryCSVMapping;

    @Before
    public void setup() {
        record1 = new String[]{"OVR","ARA104  ","161030","5NMJO EET","S","I","B738","M","2665",
            "DNMM","0438","FAOR","2300","PBN/B2O2 DOF/161029 ","NESEK      ","2321","ETOSA      ","2325","UVLUK      ",
            "2336","ATULA      ","2342","APNOP      ","0007","APKAN      ","0011","APMEX      ","0016","AVOGU      ",
            "0018"," ", " "};

        record2 = new String[]{"OVR","SAA084  ","161030","ZSSZI EET","S","I","A320","M","4323","FAOR2","0229","FCBB","2244","PBN/A1B1C1D1L1O1S1T1","BUGRO      ","0055",
            "MNV        ","0111","IBVAN      ","0116","UBTEN      ","0130","IMSUK      ","0140","AKUTA      ","0147","SOKIM      ","0148","UTUTA      ","0150",
            "RUDAS      ","0155"};

        recordList = new ArrayList<>();
        recordList.add(record1);
        recordList.add(record2);

        radarSummaryCSVMapping = new RadarSummaryCSVMapping();
    }

    @Test
    public void cvsMappingRecordTest() {
        try {
            RadarSummaryCsvViewModel radarSummaryCsvViewModel = radarSummaryCSVMapping.convertStringArrayIntoCsvModel(record1);
            assertThat(radarSummaryCsvViewModel.getRegistration()).isEqualTo("5NMJO");
        } catch (CsvRequiredFieldEmptyException | CsvConstraintViolationException | CsvDataTypeMismatchException e) {
           fail("Exception was thrown: " + e.getLocalizedMessage());
        }
    }

    @Test
    public void cvsMappingListTest(){
        try {
            List<RadarSummaryCsvViewModel> radarSummaryList=radarSummaryCSVMapping.radarSummaryMapFromRecordList(recordList);
            assertThat(radarSummaryList.size()).isEqualTo(recordList.size());
        } catch (CsvRequiredFieldEmptyException | CsvConstraintViolationException | CsvDataTypeMismatchException e) {
            fail("Exception was thrown: " + e.getLocalizedMessage());
        }
    }

    @Test
    public void cvsMappingListFromCSVFileTest(){
        try {
            String pathFile = "/ca/ids/abms/modules/radarsummary/test_40.csv";
            InputStream inputStream=this.getClass().getResourceAsStream(pathFile);
            CsvImportServiceImp<RadarSummary>  dataImportServiceCsv = new CsvImportServiceImp<>();
            CSVReader csvReader=dataImportServiceCsv.createCSVReader(inputStream,',','\'',Boolean.FALSE);
            List<String[]> records = csvReader.readAll();
            List<RadarSummaryCsvViewModel> radarSummaries=radarSummaryCSVMapping.radarSummaryMapFromRecordList(records);
            assertThat(radarSummaries.size()).isEqualTo(records.size());
        } catch (CsvRequiredFieldEmptyException | CsvConstraintViolationException | CsvDataTypeMismatchException | IOException e) {
            fail("Exception was thrown: " + e.getLocalizedMessage());
        }
    }
}
