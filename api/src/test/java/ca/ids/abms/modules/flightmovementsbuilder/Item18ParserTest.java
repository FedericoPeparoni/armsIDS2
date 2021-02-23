package ca.ids.abms.modules.flightmovementsbuilder;

import ca.ids.abms.modules.flightmovementsbuilder.vo.DeltaFlightVO;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Field;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Parser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by c.talpa on 22/11/2016.
 */
public class Item18ParserTest {

    @Test
    public void stsParserTest(){
        // Test Case 1
        String otherInfo="EET TMA 0020 ESTAK 0051 DEST/SIAVONGA 1628S02840E  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        String trueValue=null;
        String result= Item18Parser.parse(otherInfo, Item18Field.STS);
        Assert.assertEquals(trueValue,result);


        // Test Case 2
        otherInfo="EET TMA 0020 ESTAK 0051 STS/TESTTESTSTETSTE DEST/SIAVONGA 1628S02840E  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="TESTTESTSTETSTE";
        result= Item18Parser.parse(otherInfo, Item18Field.STS);
        Assert.assertEquals(trueValue,result);

        // Test Case 3
        otherInfo="EET TMA 0020 ESTAK 0051 STS/TEST1 TEST2 DEST/SIAVONGA 1628S02840E  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="TEST1 TEST2";
        result= Item18Parser.parse(otherInfo, Item18Field.STS);
        Assert.assertEquals(trueValue,result);

        // Test Case 4
        otherInfo="EET TMA 0020 ESTAK 0051 STS/TEST1 1628S02840E DEST/SIAVONGA 1628S02840E  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="TEST1 1628S02840E";
        result= Item18Parser.parse(otherInfo, Item18Field.STS);
        Assert.assertEquals(trueValue,result);

        // Test Case 5
        otherInfo="EET/ TMA 0020 ESTAK 0051 DEST/SIAVONGA 1628S02840E  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue=null;
        result= Item18Parser.parse(otherInfo, Item18Field.STS);
        Assert.assertEquals(trueValue,result);
    }

    @Test
    public void destParserTest(){
        // Test Case 1
        String otherInfo="EET/ TMA 0020 ESTAK 0051  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        String trueValue=null;
        String result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);


        // Test Case 2
        otherInfo="EET/ TMA 0020 ESTAK 0051 DEST/SIAVONGA 1628S02840E NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA 1628S02840E";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);

        // Test Case 3
        otherInfo="EET TMA 0020 ESTAK 0051 DEST/SIAVONGA TEST 1628S02840E SUR/NIL NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA TEST 1628S02840E";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);


        // Test Case 4
        otherInfo="EET TMA 0020 ESTAK 0051 DeSt/SIAVONGA TEST 1628S02840E SUR/NIL NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA TEST 1628S02840E";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);

        // Test Case 5
        otherInfo="EET TMA 0020 ESTAK 0051 DEST/CABOTO 1235S029 SUR/NIL NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="CABOTO 1235S029";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);

        // Test Case 6
        otherInfo="EET TMA 0020 ESTAK 0051 DEST/2449S02613E 1235S029 SUR/NIL NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="2449S02613E 1235S029";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);
    }

    @Test
    public void destParserTestWithHour(){
        // Test Case 1
        String otherInfo="NAV/GPS POMPOM0900/0910 NTSWI0920/0930 FBMN0950 0 RMK/SARNIL REFID2411MN062";
        String trueValue=null;
        String result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);

        // Test Case 2
        otherInfo="NAV/GPS DEST/TAUPAN0745/0800  POMPOM0900/0910 NTSWI0920/0930 FBMN0950 0 RMK/SARNIL REFID2411MN062";
        trueValue="TAUPAN0745/0800  POMPOM0900/0910 NTSWI0920/0930 FBMN0950";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);

        // Test Case 3
        otherInfo="NAV/GPS DEST/JAO1245 N/S DOF/170301 OPR/WAIR 6860778 RMK/SARNIL REFID0103MN012";
        trueValue="JAO1245";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);

        // Test Case 4
        otherInfo="NAV/GPS DEST/FBXB1400 O N/STOP OPR/TONIE BRITZ +27824612710 / +27824931928 RMK/SARNIL REFIDMN049";
        trueValue="FBXB1400";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);

        // Test Case 5
        otherInfo="NAV/GPS DEST/FBSV0855/0905 XARAKAI0950 O RN/STOP RMK/SARNIL REFIDMN052";
        trueValue="FBSV0855/0905 XARAKAI0950";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);

        // Test Case 6
        otherInfo="NAV/GPS DEST/CHOBE O1 DOF/171023 REG/A2DFT OPR/WILDERNESS";
        trueValue="CHOBE";
        result= Item18Parser.parse(otherInfo, Item18Field.DEST);
        Assert.assertEquals(trueValue,result);
    }


    @Test
    public void destFieldToArray(){
        // Test Case 1
        String otherInfo="NAV/GPS DEST/TAUPAN0745/0800  POMPOM0900/0910 NTSWI0920/0930 FBMN0950 0 RMK/SARNIL REFID2411MN062";
        String trueValue="TAUPAN0745/0800";
        String item= Item18Parser.parse(otherInfo, Item18Field.DEST);
        String[] array=Item18Parser.destFieldToArray(item);
        Assert.assertEquals(trueValue,array[0]);

        // Test Case 1
        otherInfo="NAV/GPS DEST/TAUPAN0745/0800  FBMN0950 0 RMK/SARNIL REFID2411MN062";
        trueValue="FBMN0950";
        item= Item18Parser.parse(otherInfo, Item18Field.DEST);
        array=Item18Parser.destFieldToArray(item);
        Assert.assertEquals(trueValue,array[1]);
    }

    @Test
    public void destFieldToMap(){
        // Test Case 1
        String otherInfo="NAV/GPS DEST/TAUPAN0745/0800  POMPOM0900/0910 NTSWI0920/0930 FBMN0950 0 RMK/SARNIL REFID2411MN062";
        String trueValue="TAUPAN";
        String item= Item18Parser.parse(otherInfo, Item18Field.DEST);
        List<DeltaFlightVO> list=Item18Parser.destFieldToMap(item);
        Assert.assertEquals(list.get(0).getIdent(),trueValue);

        // Test Case 2
        otherInfo="NAV/GPS DEST/TAUPAN0745/0800  FBMN0950 RMK/SARNIL REFID2411MN062";
        trueValue="FBMN";
        item= Item18Parser.parse(otherInfo, Item18Field.DEST);
        list=Item18Parser.destFieldToMap(item);
        Assert.assertEquals(list.get(list.size()-1).getIdent(),trueValue);
    }

    @Test
    public void depParserTest(){
        // Test Case 1
        String otherInfo="EET TMA 0020 ESTAK 0051 SUR/NIL NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        String trueValue=null;
        String result= Item18Parser.parse(otherInfo, Item18Field.DEP);
        Assert.assertEquals(trueValue,result);


        // Test Case 2
        otherInfo="EET TMA 0020 ESTAK 0051 DEP/SIAVONGA 1628S02840E SUR/NIL NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA 1628S02840E";
        result= Item18Parser.parse(otherInfo, Item18Field.DEP);
        Assert.assertEquals(trueValue,result);

        // Test Case 3
        otherInfo="EET TMA 0020 ESTAK 0051 DEP/SIAVONGA TEST 1628S02840E NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA TEST 1628S02840E";
        result= Item18Parser.parse(otherInfo, Item18Field.DEP);
        Assert.assertEquals(trueValue,result);


        // Test Case 4
        otherInfo="EET TMA 0020 ESTAK 0051 dEp/SIAVONGA TEST 1628S02840E NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA TEST 1628S02840E";
        result= Item18Parser.parse(otherInfo, Item18Field.DEP);
        Assert.assertEquals(trueValue,result);


        // Test Case 5
        otherInfo="EET TMA 0020 ESTAK 0051 DEP/LUFUPA NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="LUFUPA";
        result= Item18Parser.parse(otherInfo, Item18Field.DEP);
        Assert.assertEquals(trueValue,result);


        // Test Case 6
        otherInfo="EET TMA 0020 ESTAK 0051 DEP/LUFUPA 1412S03037E  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="LUFUPA 1412S03037E";
        result= Item18Parser.parse(otherInfo, Item18Field.DEP);
        Assert.assertEquals(trueValue,result);

        // Test Case 7
        otherInfo="EET TMA 0020 ESTAK 0051 DEP/LUFUPA 46N078W  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="LUFUPA 46N078W";
        result= Item18Parser.parse(otherInfo, Item18Field.DEP);
        Assert.assertEquals(trueValue,result);


        // Test Case 8
        // The coordinate formats is wrong DEP/KALUMBILAS1215E02525 (see rootparsr's coordinate format)
        otherInfo="EET TMA 0020 ESTAK 0051 DEP/KALUMBILAS 1215E02525   NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue=null;
        result= Item18Parser.parse(otherInfo, Item18Field.DEP);
     //   Assert.assertEquals(trueValue,result);

     // Test Case 9
        otherInfo="EET TMA 0020 ESTAK 0051 DEP/SIAVONGA1628S02840E  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA1628S02840E";
        result= Item18Parser.parse(otherInfo, Item18Field.DEP);
        Assert.assertEquals(trueValue,result);
    }


    @Test
    public void regParserTest(){
        // Test Case 1
        String otherInfo="EET TMA 0020 ESTAK 0051  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        String trueValue=null;
        String result= Item18Parser.parse(otherInfo, Item18Field.REG);
        Assert.assertEquals(trueValue,result);


        // Test Case 2
        otherInfo="EET TMA 0020 ESTAK 0051 REG/SIAVONGA  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA";
        result= Item18Parser.parse(otherInfo, Item18Field.REG);
        Assert.assertEquals(trueValue,result);

        // Test Case 3
        otherInfo="EET TMA 0020 ESTAK 0051 REG/SIAVONGA  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA";
        result= Item18Parser.parse(otherInfo, Item18Field.REG);
        Assert.assertEquals(trueValue,result);


        // Test Case 4
        otherInfo="EET TMA 0020 ESTAK 0051 rEG/SIAVONGA TEST 1628S02840E  NAV/GPS\n" +
            "RMK/PILOT E. FARMER +260 967 867 840, ZAF/ANC123MAY16 DOF/260516\n" +
            "CREATED BY SKYDEMON, SUPP INFO RQS KBLIHAEX";
        trueValue="SIAVONGA";
        result= Item18Parser.parse(otherInfo, Item18Field.REG);
        Assert.assertEquals(trueValue,result);

        // Test Case 5
        otherInfo="PBN/B3B4 DOF/160713 REG/N704PG\n" +
            " EET/FBGR0030 FVHF0038 FLFI0118 FZZA0159\n" +
            " CODE/A964B6 OPR/AIR KATANGA RMK/OVERFLIGHT  ZIMBABWE EC71316\n" +
            " ZAMBIA MET432JUN16 BOTSWANA CAABHQ1273 SOUTH AFRICA NS35916\n" +
            " E/0527 P/7 R/VE J/L A/WHT C/TBN";
        trueValue="N704PG";
        result= Item18Parser.parse(otherInfo, Item18Field.REG);
        Assert.assertEquals(trueValue,result);

        // Test Case 6
        otherInfo="PBN/B3B4 DOF/160713\n" +
            " EET/FBGR0030 FVHF0038 FLFI0118 FZZA0159\n" +
            " CODE/A964B6 OPR/AIR KATANGA RMK/OVERFLIGHT  ZIMBABWE EC71316\n" +
            " ZAMBIA MET432JUN16 BOTSWANA CAABHQ1273 SOUTH AFRICA NS35916\n" +
            " E/0527 P/7 R/VE J/L A/WHT C/TBN";
        trueValue=null;
        result= Item18Parser.parse(otherInfo, Item18Field.REG);
        Assert.assertEquals(trueValue,result);


        // Test Case 7
        otherInfo=null;
        trueValue=null;
        result= Item18Parser.parse(otherInfo, Item18Field.REG);
        Assert.assertEquals(trueValue,result);

    }


    @Test
    public void coordinateParserTest(){
        // Test Case 1
        String item18Example="LUFUPA 1412S03037E";
        String trueValue="1412S03037E";
        String result= Item18Parser.parseCoordinates(item18Example);
        Assert.assertEquals(trueValue,result);


        // Test Case 2
        item18Example="LUFUPA1412S03037E";
        trueValue="1412S03037E";
        result= Item18Parser.parseCoordinates(item18Example);
        Assert.assertEquals(trueValue,result);

        // Test Case 3
        item18Example="LUFUPA 46N078W";
        trueValue="46N078W";
        result= Item18Parser.parseCoordinates(item18Example);
        Assert.assertEquals(trueValue,result);


        // Test Case 4
        item18Example="LUFUPA 462010N0780510W";
        trueValue="462010N0780510W";
        result= Item18Parser.parseCoordinates(item18Example);
        Assert.assertEquals(trueValue,result);

        // Test Case 5
        item18Example="LUFUPA462010N0780510W";
        trueValue="462010N0780510W";
        result= Item18Parser.parseCoordinates(item18Example);
        Assert.assertEquals(trueValue,result);

        // Test Case 6
        item18Example="LUFUPA";
        trueValue=null;
        result= Item18Parser.parseCoordinates(item18Example);
        Assert.assertEquals(trueValue,result);

        // Test Case 6
        item18Example="TRSNAPOLI 405129N141651E";
        trueValue="405129N141651E";
        result= Item18Parser.parseCoordinates(item18Example);
        Assert.assertEquals(trueValue,result);
    }


    @Test
    public void oprParserTest(){
        // Test Case 1
        String otherInfo="NAV/GPS DEP/CHABWINO1345S02907E DOF/160911 EET/CTR0036 " +
            " OPR/THOMPSON 0966747959 RMK/SARNML DNR513AUG16 REFIDZPZX018";
        String trueValue="THOMPSON";
        String result= Item18Parser.parse(otherInfo, Item18Field.OPR);
        Assert.assertEquals(trueValue,result);

        // Test Case 2
        otherInfo="NAV/GPS DEP/BUSANGA PLAINS 1417S02524E DOF/160911 EET/CTA0026  OPR/STARAVIA PER/B  " +
            "RMK/SARNML CLRDNR461AUG16 ZPZX018";
        trueValue="STARAVIA";
        result= Item18Parser.parse(otherInfo, Item18Field.OPR);
        Assert.assertEquals(trueValue,result);

        // Test Case 3
        otherInfo= "NAV/GPS DEP/SIANDUNDA1732S02550E DEST/CHILANGA1532S02818E DOF/160911 " +
            "EET/CTR0011 KURMA0049 OPR/NINE JULIET LOGISTICS 0971252016 RMK/SARNIL " +
            "DNR508AUG16 REFIDZPZX003";
        trueValue="NINE JULIET LOGISTICS";
        result= Item18Parser.parse(otherInfo, Item18Field.OPR);
        Assert.assertEquals(trueValue,result);

        // Test Case 4
        otherInfo="NAV/GPS DEP/PEDZA144102S0281428E DEST/BHIGH132952S0281551E " +
            "DOF/160912 EET/ANVAM0045 OPR/J BROWNRIGG 0027836534043 " +
            "RMK/SARNIL CLRDNR507AUG16 REFIDZPZX007";

        trueValue="J BROWNRIGG";
        result= Item18Parser.parse(otherInfo, Item18Field.OPR);
        Assert.assertEquals(trueValue,result);

        // Test Case 5
        otherInfo="NAV/GPS DEP/CHABWINO1345S02907E DOF/160911 EET/CTR0036 " +
            " OPR/THOMPSON0966747959 RMK/SARNML DNR513AUG16 REFIDZPZX018";
        trueValue="THOMPSON";
        result= Item18Parser.parse(otherInfo, Item18Field.OPR);
        Assert.assertEquals(trueValue,result);

        // Test Case 6
        otherInfo="NAV/GPS DEP/CHABWINO1345S02907E DOF/160911 EET/CTR0036 " +
            "  RMK/SARNML DNR513AUG16 REFIDZPZX018";
        trueValue=null;
        result= Item18Parser.parse(otherInfo, Item18Field.OPR);
        Assert.assertEquals(trueValue,result);

        //Test Case 7
        //DOF/170223 REG/ILVP1 OPR/ALITALIA
        otherInfo="DOF/170223 REG/ILVP1 OPR/ALITALIA";
        trueValue="ALITALIA";
        result= Item18Parser.parse(otherInfo, Item18Field.OPR);
        Assert.assertEquals(trueValue,result);

    }

    @Test
    public void designatorParserTest(){

        // Test Case 1
        String item18="LUFUPA 1412S03037E";
        String trueValue="LUFUPA";
        String result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);

        // Test Case 2
        item18="LUFUPA1412S03037E";
        trueValue="LUFUPA";
        result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);

        // Test Case 3
        item18="LUFUPA 1412S03037E TEST";
        trueValue="LUFUPA";
        result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);

        // Test Case 4
        item18="1412S03037E";
        trueValue="1412S03037E";
        result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);

        // Test Case 5
        item18=null;
        trueValue=null;
        result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);

        // Test Case 6
        item18 ="TSINGARO DOF180329";
        trueValue = "TSINGARO";
        result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);
        
     // Test Case 7
        item18 ="TSINGARO S1412E03037";
        trueValue = "TSINGARO";
        result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);
        
     // Test Case 8
        item18 ="KABARAK (S00°11 E035°57)";
        trueValue = "KABARAK (S00°11 E035°57)";
        result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);
        
     // Test Case 9
        item18 ="KABARAKS0011.38E03557";
        trueValue = "KABARAKS0011.38E03557";
        result= Item18Parser.parseDesignator(item18);
        Assert.assertEquals(trueValue,result);

    }

    @Test
    public void rmkParserTest() {
        Assert.assertEquals (null, Item18Parser.parse ("", Item18Field.RMK));
        Assert.assertEquals (null, Item18Parser.parse (" ", Item18Field.RMK));
        Assert.assertEquals ("AA", Item18Parser.parse ("RMK/AA", Item18Field.RMK));
        Assert.assertEquals ("AA", Item18Parser.parse ("STS/XX RMK/AA", Item18Field.RMK));
        Assert.assertEquals ("AA BB", Item18Parser.parse ("RMK / AA\t\nBB  ", Item18Field.RMK));
        Assert.assertEquals ("AA BB", Item18Parser.parse ("RMK / AA\t\nBB STS/ CCC ", Item18Field.RMK));
        Assert.assertEquals ("AA BB", Item18Parser.parse ("RMK / AA\t\nBB STS / CCC ", Item18Field.RMK));
        Assert.assertEquals ("AA BB", Item18Parser.parse ("RMK / AA\t\nBB STS/ ", Item18Field.RMK));
        Assert.assertEquals ("AA BBSTS/", Item18Parser.parse ("RMK / AA\t\nBBSTS/ ", Item18Field.RMK));
        Assert.assertEquals (null, Item18Parser.parse ("zzzRMK/AA", Item18Field.RMK));
    }

    @Test
    public void getFirstAerodromeFromDestFieldTest() {
        Assert.assertEquals (null, Item18Parser.getFirstAerodrome(null));
        Assert.assertEquals (null, Item18Parser.getFirstAerodrome(""));
        Assert.assertEquals (null, Item18Parser.getFirstAerodrome(" "));
        Assert.assertEquals ("GOPE", Item18Parser.getFirstAerodrome("GOPE 2236S02447E"));
        Assert.assertEquals ("GOPE", Item18Parser.getFirstAerodrome("GOPE 2236S02447E BLAH BLAH BLAH"));
        Assert.assertEquals (null, Item18Parser.getFirstAerodrome(" 2236S02447E BLAH BLAH BLAH"));
        Assert.assertEquals ("KADIZORA", Item18Parser.getFirstAerodrome("KADIZORA0845/0855"));
        Assert.assertEquals ("KADIZORA", Item18Parser.getFirstAerodrome("KADIZORA0845/0855 FBMN0940"));
        Assert.assertEquals ("KADIZORA", Item18Parser.getFirstAerodrome("KADIZORA0845"));
        Assert.assertEquals ("KADIZORA", Item18Parser.getFirstAerodrome("KADIZORA0845 FBMN0940"));
    }

    @Test
    public void getFirstAerodromeOrDMSFromDestFieldTest() {
        Assert.assertEquals (null, Item18Parser.getFirstAerodromeOrDMS(null));
        Assert.assertEquals (null, Item18Parser.getFirstAerodromeOrDMS(""));
        Assert.assertEquals (null, Item18Parser.getFirstAerodromeOrDMS(" "));
        Assert.assertEquals ("GOPE", Item18Parser.getFirstAerodromeOrDMS("GOPE 2236S02447E"));
        Assert.assertEquals ("GOPE", Item18Parser.getFirstAerodromeOrDMS("GOPE 2236S02447E BLAH BLAH BLAH"));
        Assert.assertEquals ("2236S02447E", Item18Parser.getFirstAerodromeOrDMS(" 2236S02447E BLAH BLAH BLAH"));
        Assert.assertEquals ("KADIZORA", Item18Parser.getFirstAerodromeOrDMS("KADIZORA0845/0855"));
        Assert.assertEquals ("KADIZORA", Item18Parser.getFirstAerodromeOrDMS("KADIZORA0845/0855 FBMN0940"));
        Assert.assertEquals ("KADIZORA", Item18Parser.getFirstAerodromeOrDMS("KADIZORA0845"));
        Assert.assertEquals ("KADIZORA", Item18Parser.getFirstAerodromeOrDMS("KADIZORA0845 FBMN0940"));
    }

}
