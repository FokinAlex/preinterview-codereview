package export;

// Project imports:
//      Document
//      DictCriteriaQuery
//      EqConditionExpression
//      NotCondition
//      NullConditionExpression
//      DictionaryService
//      FieldSet
//      SpecificDocumentType
//      SpecificDictionary

public enum  DepositType {
    A (
        "A_TypeID",
        "A_Type",
        "A_Name"
    ),

    B (
        "B_TypeID",
        "B_Type",
        "B_Name"
    );

    private final String depositTypeId;
    private final String depositType;
    private final String depositName;

    DepositType(String typeId, String type, String name) {
        this.depositTypeId = typeId;
        this.depositType = type;
        this.depositName = name;
    }

    public String getDepositTypeId() {
        return depositTypeId;
    }

    /**  Java doc with business comment */
    public String getDepositType() {
        return depositType;
    }

    /**  Java doc with business comment */
    public String getDepositName() {
        return depositName;
    }

    public static DepositType getDepositType(Document document, DictionaryService dictionaryService) {
        final Long id = document.getFieldValue(SpecificDocumentType.FIELD_DEPOSITTYPEID);
        DictCriteriaQuery query = new DictCriteriaQuery(SpecificDictionary.DICT_NAME);
        query.addCondition(new EqConditionExpression(DictCriteriaQuery.ID, id));
        query.addCondition(new NotCondition(new NullConditionExpression(SpecificDictionary.DEPOSITSTRUCTURALTYPESDICT_TYPEDEPOSITID)));
        FieldSet fieldSet = dictionaryService.getFirst(query);
        if (null != fieldSet) {
            return StructuralDepositType.getStructuralDepositTypeByTypeId(fieldSet.getStringFieldValue(SpecificDictionary.TYPEDEPOSITID));
        } else {
            throw new IllegalStateException(String.format("Deposit type with id = %s cannot be received", id));
        }
    }

    private static DepositType getDepositTypeByTypeId(String depositTypeId) {
        for(DepositType value : values()) {
            if (value.getDepositTypeId().equalsIgnoreCase(depositTypeId)) {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("There is no value for %s", depositTypeId));
    }
}
