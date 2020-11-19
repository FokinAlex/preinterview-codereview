package export;

// Project imports:
//      Document
//      SpecificDocumentType

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;

public class Converter {

    private static final String DELIMETER = ";";
    private static final String END_OF_LINE = "\n";

    private static final String A_B_SIDE_VALUE             = "Constant business value";
    private static final String A_B_OPTION_TYPE_VALUE      = "Constant business value";
    private static final String B_IS_DELIVERABLE_VALUE     = "Constant business value";
    private static final String B_OPTION_STYLE_VALUE       = "Constant business value";
    private static final String A_B_VERSION_VALUE          = "Constant business value";
    private static final String A_B_OPTION_KIND_VALUE      = "Constant business value";
    private static final String B_UPPER_BARRIER_TYPE_VALUE = "Constant business value";
    private static final String B_LOWER_BARRIER_TYPE_VALUE = "Constant business value";

    // Date formats
    private static final FastDateFormat YYYY_MM_DD_HH_MM_SS_DATE_FORMAT = FastDateFormat.getInstance("yyyy-mm-dd hh:mm:ss");
    private static final FastDateFormat DD_MM_YYYY_DATE_FORMAT          = FastDateFormat.getInstance("dd.MM.yyyy");

    /**
     * CSV-line with metadata:
     * <pre>
     * №   Field name and business comment
     * ———————————————————————————————————
     * 1.  TradeID            Business comment
     * 2.  Side               Business comment
     * 3.  TradeDate          Business comment
     * 4.  ValueDate          Business comment
     * 5.  Put/Call Ccy1      Business comment
     * 6.  Ccy1               Business comment
     * 7.  Ccy2               Business comment
     * 8.  Rate               Business comment
     * 9.  Premium            Business comment
     * 10. PremiumCcy         Business comment
     * 11. Delta              Business comment
     * 12. IsDeliverable      Business comment
     * 13. OptionStyle        Business comment
     * 14. Barrier1           Business comment
     * 15. Barrier2           Business comment
     * 16. Version            Business comment
     * 17. SettCcy            Business comment
     * 18. OptionKind         Business comment
     * 19. BarrierStartDate   Business comment
     * 20. BarrierEndDate     Business comment
     * 21. Barrier1Typeid     Business comment
     * 22. Barrier2Typeid     Business comment
     * 23. CurrencyPair       Business comment
     * 24. Amount             Business comment
     * 25. DocumentType       Business comment
     * 26. ApplicationStatus  Business comment
     * </pre>
     */
    public static final String META_LINE = "TradeID;Side;TradeDate;ValueDate;Put/Call Ccy1;Ccy1;Ccy2;Rate;Premium;PremiumCcy;Delta;IsDeliverable;OptionStyle;Barrier1;Barrier2;Version;SettCcy;OptionKind;BarrierStartDate;BarrierEndDate;Barrier1Typeid;Barrier2Typeid;CurrencyPair;Amount;StructuralDepositType;ApplicationStatus" + END_OF_LINE;

    private static String emptyFieldValue = "";

    /**
     * Returns CSV-line for document
     *
     * @param document    Document
     * @param depositType Document type
     *
     * @return CSV-line
     */
    public static String depositToCSVLine(Document document, DepositType depositType) {
        String[] values = {                                       // №   Field name
                getTradeIdValue(document),                        // 1.  TradeID
                getSideValue(),                                   // 2.  Side
                getTradeDateValue(document),                      // 3.  TradeDate
                getValueDateValue(document),                      // 4.  ValueDate
                getOptionTypeValue(document),                     // 5.  Put/Call Ccy1
                getDepositCcy1ISOValue(document),                 // 6.  Ccy1
                getDepositCcy2ISOValue(document, depositType),    // 7.  Ccy2
                getRateValue(document),                           // 8.  Rate
                getPremiumValue(document, depositType),           // 9.  Premium
                getPremiumCcyValue(document, depositType),        // 10. PremiumCcy
                getDeltaValue(document, depositType),             // 11. Delta
                getIsDeliverableValue(depositType),               // 12. IsDeliverable
                getOptionStyleValue(depositType),                 // 13. OptionStyle
                getBarrier1Value(document, depositType),          // 14. Barrier1
                getBarrier2Value(document, depositType),          // 15. Barrier2
                getVersionValue(),                                // 16. Version
                getSettCcyValue(document, depositType),           // 17. SettCcy
                getOptionKindValue(),                             // 18. OptionKind
                getBarrierStartDateValue(document, depositType),  // 19. BarrierStartDate
                getBarrierEndDateValue(document, depositType),    // 20. BarrierEndDate
                getBarrier1TypeIdValue(depositType),              // 21. Barrier1Typeid
                getBarrier2TypeIdValue(depositType),              // 22. Barrier2Typeid
                getCurrencyPairValue(document),                   // 23. CurrencyPair
                getAmountValue(document),                         // 24. Amount
                depositType.getDepositType(),                     // 25. DocumentType
                getApplicationStatusValue(document)               // 26. ApplicationStatus
        };
        return StringUtils.join(values, DELIMETER) + END_OF_LINE;
    }

    /**  Java doc with business comment */
    private static String getTradeIdValue(Document document) {
        return document.getFieldValue(SpecificDocumentType.FIELD_STRUCTUREID).toString();
    }

    /**  Java doc with business comment */
    private static String getSideValue() {
        return A_B_SIDE_VALUE;
    }

    /**  Java doc with business comment */
    private static String getTradeDateValue(Document document) {
        return YYYY_MM_DD_HH_MM_SS_DATE_FORMAT.format(document.getFieldValue(SpecificDocumentType.FIELD_CONTRACTDATE));
    }

    /**  Java doc with business comment */
    private static String getValueDateValue(Document document) {
        return DD_MM_YYYY_DATE_FORMAT.format(document.getFieldValue(SpecificDocumentType.FIELD_DEPOSITTERMENDDATE));
    }

    /**  Java doc with business comment */
    private static String getOptionTypeValue(Document document) {
        return A_B_OPTION_TYPE_VALUE + " " + getDepositCcy1ISOValue(document);
    }

    /**  Java doc with business comment */
    private static String getDepositCcy1ISOValue(Document document) {
        return document.getStringFieldValue(SpecificDocumentType.FIELD_EPOSITSUMCURRCODEISO);
    }

    /**  Java doc with business comment */
    private static String getDepositCcy2ISOValue(Document document, DepositType typeDepositId) {
        switch (typeDepositId) untitled{
            case A:
                return document.getStringFieldValue(SpecificDocumentType.FIELD_DEPOSITSUMALTCURRCODEISO);
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getRateValue(Document document) {
        return document.getFieldValue(SpecificDocumentType.FIELD_BASEINTERESTRATE).toString();
    }

    /**  Java doc with business comment */
    private static String getPremiumValue(Document document, DepositType typeDepositId) {
        switch (typeDepositId) {
            case A:
                return document.getFieldValue(SpecificDocumentType.FIELD_PREMIUM).toString();
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getPremiumCcyValue(Document document, DepositType typeDepositId) {
        switch (typeDepositId) {
            case A:
                return document.getStringFieldValue(SpecificDocumentType.FIELD_PREMIUMCURRCODEISO);
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getDeltaValue(Document document, DepositType typeDepositId) {
        switch (typeDepositId) {
            case A:
                return document.getFieldValue(SpecificDocumentType.FIELD_DELTA).toString();
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getIsDeliverableValue(DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return B_IS_DELIVERABLE_VALUE;
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getOptionStyleValue(DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return B_OPTION_STYLE_VALUE;
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getBarrier1Value(Document document, DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return document.getFieldValue(SpecificDocumentType.FIELD_STRIKERANGELOWERBOUND).toString();
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getBarrier2Value(Document document, DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return document.getFieldValue(SpecificDocumentType.FIELD_STRIKERANGEUPPERBOUND).toString();
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getVersionValue() {
        return A_B_VERSION_VALUE;
    }

    /**  Java doc with business comment */
    private static String getSettCcyValue(Document document, DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return document.getStringFieldValue(SpecificDocumentType.FIELD_DEPOSITSUMCURRCODEISO);
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getOptionKindValue() {
        return A_B_OPTION_KIND_VALUE;
    }

    /**  Java doc with business comment */
    private static String getBarrierStartDateValue(Document document, DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return DD_MM_YYYY_DATE_FORMAT.format(document.getFieldValue(SpecificDocumentType.FIELD_CONTRACTDATE));
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getBarrierEndDateValue(Document document, DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return DD_MM_YYYY_DATE_FORMAT.format(document.getFieldValue(SpecificDocumentType.FIELD_DEPOSITTERMENDDATE));
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getBarrier1TypeIdValue(DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return B_UPPER_BARRIER_TYPE_VALUE;
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getBarrier2TypeIdValue(DepositType typeDepositId) {
        switch (typeDepositId) {
            case B:
                return B_LOWER_BARRIER_TYPE_VALUE;
            default:
                return emptyFieldValue;
        }
    }

    /**  Java doc with business comment */
    private static String getCurrencyPairValue(Document document) {
        return document.getStringFieldValue(SpecificDocumentType.FIELD_BASEASSET);
    }

    /**  Java doc with business comment */
    private static String getAmountValue(Document document) {
        return document.getFieldValue(SpecificDocumentType.FIELD_DEPOSITSUM).toString();
    }

    /**  Java doc with business comment */
    private static String getApplicationStatusValue(Document document) {
        return document.getBankDocState().getSystemName();
    }

    public static void setEmptyFieldValue(String _emptyFieldValue) {
        emptyFieldValue = _emptyFieldValue;
    }
}
