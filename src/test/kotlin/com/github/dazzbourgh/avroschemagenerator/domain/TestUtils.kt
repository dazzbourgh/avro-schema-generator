package com.github.dazzbourgh.avroschemagenerator.domain

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.BooleanElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ByteElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.CharacterElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.DoubleElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.EnumElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FloatElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.IntElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.LongElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Nullable
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Repeated
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ShortElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.StringElement

object TestUtils {
    val complexElement =
        ComplexElement(
            "TypesTestClass",
            "",
            null,
            listOf(
                ByteElement("bytePrimitive", NonNull),
                ShortElement("shortPrimitive", NonNull),
                IntElement("integerPrimitive", NonNull),
                LongElement("longPrimitive", NonNull),
                FloatElement("floatPrimitive", NonNull),
                DoubleElement("doublePrimitive", NonNull),
                CharacterElement("characterPrimitive", NonNull),
                BooleanElement("boolPrimitive", NonNull),

                ByteElement("byteBoxed", Nullable),
                IntElement("integerBoxed", Nullable),
                ShortElement("shortBoxed", Nullable),
                LongElement("longBoxed", Nullable),
                FloatElement("floatBoxed", Nullable),
                DoubleElement("doubleBoxed", Nullable),
                CharacterElement("characterBoxed", Nullable),
                BooleanElement("boolBoxed", Nullable),

                StringElement("string", Nullable),

                LongElement("bigInteger", Nullable),
                DoubleElement("bigDecimal", Nullable),
                DoubleElement("localDateTime", Nullable),
                DoubleElement("date", Nullable),

                IntElement("arr", Repeated),
                IntElement("list", Repeated),

                ComplexElement(
                    "SomeTestClass",
                    "",
                    "someTestClass",
                    listOf(
                        StringElement("field", Nullable)
                    ),
                    Nullable
                ),
                ComplexElement(
                    "SomeTestClass",
                    "",
                    "someTestClassList",
                    listOf(
                        StringElement("field", Nullable)
                    ),
                    Repeated
                ),
                EnumElement("TestEnum", "", "testEnum", listOf("ONE", "TWO"), Nullable),
                EnumElement("TestEnum", "", "testEnumSet", listOf("ONE", "TWO"), Repeated)
            ),
            NonNull
        )
}