import java.lang.Integer;
import java.lang.String;
import java.util.List;

public class ResolveElementClass {
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

    private int[] arr;
    private List<Integer> list;
    private <caret>SomeTestClass someTestClass;
}