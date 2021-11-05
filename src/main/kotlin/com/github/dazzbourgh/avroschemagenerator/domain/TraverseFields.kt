package com.github.dazzbourgh.avroschemagenerator.domain

fun <T, Traverse> traverseFields(clazz: T, traverse: Traverse, elementName: String? = null): Element
        where Traverse : GetNamespaceName<T>,
              Traverse : GetDocName<T>,
              Traverse : GetType<T>,
              Traverse : GetGenericType<T>,
              Traverse : GetProperties<T>,
              Traverse : GetPropertyNames<T> {
    val getComplexElement = { name: String, field: T -> traverseFields(field, traverse, name) }

    val docName = with(traverse) { clazz.getDocName() }
    val namespace = with(traverse) { clazz.getNamespaceName() }
    val fields = with(traverse) { clazz.getProperties() }
    val fieldNames = with(traverse) { clazz.getPropertyNames() }

    val elements = fieldNames.zip(fields).map { (name, field) ->
        when (val type = with(traverse) { field.getPropertyType() }) {
            is PrimitiveType -> getPrimitiveElement(name, type)
            ComplexType -> getComplexElement(name, field)
            RepeatedType -> {
                val genericElement =
                    when (val genericType = with(traverse) { clazz.getGenericType() }) {
                        is PrimitiveType -> getPrimitiveElement(name, genericType)
                        is ComplexType -> getComplexElement(name, field)
                        is RepeatedType -> TODO("Nested collections are currently not supported")
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
