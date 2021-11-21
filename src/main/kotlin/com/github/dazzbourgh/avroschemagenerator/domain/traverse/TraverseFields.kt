package com.github.dazzbourgh.avroschemagenerator.domain.traverse

@Throws(IllegalArgumentException::class, NotImplementedError::class)
fun <T, Traverse> traverse(
    element: T,
    traverse: Traverse,
    elementName: String? = null,
    mode: Mode = NonNull
): ComplexElement
        where Traverse : GetNamespaceName<T>,
              Traverse : GetDocName<T>,
              Traverse : GetType<T>,
              Traverse : GetProperties<T>,
              // TODO: add GetName interface for T instead
              Traverse : GetPropertyNames<T>,
              Traverse : GetMode<T>,
              Traverse : ResolveElementReference<T> =
    with(traverse) {
        val docName = element.getDocName()
        val namespace = element.getNamespaceName()
        val fields = element.getProperties()
        val fieldNames = element.getPropertyNames()

        val elements = fieldNames.zip(fields).map { (name, field) ->
            val type = field.getPropertyType()
            val fieldMode = field.getMode()
            when (type) {
                is PrimitiveType -> getPrimitiveElement(name, type, fieldMode)
                ComplexType ->
                    traverse(
                        field.resolveElementReference()
                            ?: throw NoSuchElementException(
                                """References must resolve to non-null object, however, field didn't resolve to anything: 
                                |   $field""".trimMargin()
                            ),
                        traverse,
                        name,
                        fieldMode
                    )
            }
        }

        ComplexElement(docName, namespace, elementName, elements, mode)
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
