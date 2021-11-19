package com.github.dazzbourgh.avroschemagenerator.domain.traverse

@Throws(IllegalArgumentException::class, NotImplementedError::class)
fun <T, Traverse> traverse(element: T, traverse: Traverse, elementName: String? = null): ComplexElement
        where Traverse : GetNamespaceName<T>,
              Traverse : GetDocName<T>,
              Traverse : GetType<T>,
              Traverse : GetProperties<T>,
              // TODO: add GetName interface for T instead
              Traverse : GetPropertyNames<T>,
              Traverse : GetMode<T>,
              Traverse : ResolveElementReference<T> =
    with(traverse) {
        val getComplexElement =
            { name: String, field: T ->
                traverse(
                    field.resolveElementReference() ?: TODO("References must resolve to non-null object"),
                    traverse,
                    name
                )
            }

        val docName = element.getDocName()
        val namespace = element.getNamespaceName()
        val fields = element.getProperties()
        val fieldNames = element.getPropertyNames()
        val mode = element.getMode()

        val elements = fieldNames.zip(fields).map { (name, field) ->
            val type = field.getPropertyType()
            val fieldMode = field.getMode()
            when (type) {
                is PrimitiveType -> getPrimitiveElement(name, type, fieldMode)
                ComplexType -> getComplexElement(name, field)
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
