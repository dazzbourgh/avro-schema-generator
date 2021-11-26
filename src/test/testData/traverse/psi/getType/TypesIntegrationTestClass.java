import java.lang.Integer;
import java.lang.String;
import java.util.List;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class TypesIntegrationTestClass {
    private byte bytePrimitive;
    private short shortPrimitive;
    private int integerPrimitive;
    private long longPrimitive;
    private float floatPrimitive;
    private double doublePrimitive;
    private char characterPrimitive;
    private boolean boolPrimitive;

    private Byte byteBoxed;
    private Integer integerBoxed;
    private Short shortBoxed;
    private Long longBoxed;
    private Float floatBoxed;
    private Double doubleBoxed;
    private Character characterBoxed;
    private Boolean boolBoxed;

    private String string;

    private BigInteger bigInteger;
    private BigDecimal bigDecimal;
    private LocalDateTime localDateTime;
    private Date date;

    private int[] arr;
    private List<Integer> list;

    private SomeTestClass someTestClass;
    private List<SomeTestClass> someTestClassList;

    private TestEnum testEnum;
    private List<TestEnum> testEnumSet;
}
