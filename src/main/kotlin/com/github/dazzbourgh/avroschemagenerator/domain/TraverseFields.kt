package com.github.dazzbourgh.avroschemagenerator.domain

fun <T, Traverse> traverse(clazz: T, traverse: Traverse, elementName: String? = null): Element
        where Traverse : GetNamespaceName<T>,
              Traverse : GetDocName<T>,
              Traverse : GetType<T>,
              Traverse : GetProperties<T>,
              Traverse : GetPropertyNames<T>,
              Traverse : GetMode<T> {
    val getComplexElement = { name: String, field: T -> traverse(field, traverse, name) }

    val docName = with(traverse) { clazz.getDocName() }
    val namespace = with(traverse) { clazz.getNamespaceName() }
    val fields = with(traverse) { clazz.getProperties() }
    val fieldNames = with(traverse) { clazz.getPropertyNames() }
    val mode = with(traverse) { clazz.getMode() }

    val elements = fieldNames.zip(fields).map { (name, field) ->
        val type = with(traverse) { field.getPropertyType() }
        val fieldMode = with(traverse) { field.getMode() }
        when (type) {
            is PrimitiveType -> getPrimitiveElement(name, type, fieldMode)
            ComplexType -> getComplexElement(name, field)
        }
    }

    return ComplexElement(docName, namespace, elementName, elements, mode)
}

private fun getPrimitiveElement(name: String, type: PrimitiveType, mode: Mode): PrimitiveElement =
    when (type) {
        BooleanType -> BooleanElement(name, mode)
        ByteType -> ByteElement(name, mode)
        DoubleType -> DoubleElement(name, mode)
        IntegerType -> IntElement(name, mode)
        LongType -> LongElement(name, mode)
        StringType -> StringElement(name, mode)
        CharacterType -> CharacterElement(name, mode)
        FloatType -> FloatElement(name, mode)
        ShortType -> ShortElement(name, mode)
    }
