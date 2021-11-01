package com.github.dazzbourgh.avroschemagenerator.domain

fun <T> traverseFields(
    thing: T,
    typed: Typed<T>,
    typedGeneric: Typed<T>,
    docNamed: Named<T>,
    namespaceNamed: Named<T>,
    getProperties: GetProperties<T>,
    getPropertyNames: GetPropertyNames<T>,
    elementName: String? = null
): Element {
    val docName = with(docNamed) { thing.getName() }
    val namespace = with(namespaceNamed) { thing.getName() }
    val fields = with(getProperties) { thing.getProperties() }
    val fieldNames = with(getPropertyNames) { thing.getPropertyNames() }
    val getComplexElement = { name: String, field: T ->
        traverseFields(
            field,
            typed,
            typedGeneric,
            docNamed,
            namespaceNamed,
            getProperties,
            getPropertyNames,
            name
        )
    }
    val elements = fieldNames.zip(fields).map { (name, field) ->
        when (val type = with(typed) { field.getPropertyType() }) {
            is PrimitiveType -> getPrimitiveElement(name, type)
            ComplexType -> getComplexElement(name, field)
            RepeatedType -> {
                val genericElement =
                    when (val genericType = with(typedGeneric) { thing.getPropertyType() }) {
                        is PrimitiveType -> getPrimitiveElement(name, genericType)
                        is ComplexType -> getComplexElement(name, field)
                        is RepeatedType -> TODO("add support for nested collections")
                    }
                RepeatedElement(genericElement)
            }
        }
    }
    return ComplexElement(docName, namespace, elementName, elements)
}

private fun getPrimitiveElement(name: String, type: PrimitiveType): PrimitiveElement =
    when (type) {
        BooleanType -> BooleanElement(name)
        ByteType -> ByteElement(name)
        DoubleType -> DoubleElement(name)
        IntType -> IntElement(name)
        LongType -> LongElement(name)
        StringType -> StringElement(name)
    }
