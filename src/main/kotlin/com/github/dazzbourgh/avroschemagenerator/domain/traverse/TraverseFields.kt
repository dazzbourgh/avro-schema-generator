package com.github.dazzbourgh.avroschemagenerator.domain.traverse

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.BooleanElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.BooleanType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ByteElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ByteType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.CharacterElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.CharacterType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ComplexType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.DoubleElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.DoubleType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.EnumElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.EnumType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FloatElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.FloatType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.IntElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.IntegerType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.LongElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.LongType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Mode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.NonNull
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.PrimitiveElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.PrimitiveType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ShortElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.ShortType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.StringElement
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.StringType
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetElementDeclaration
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetEnumValues
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetType

/**
 * Traverses selected parent element and all its fields recursively to build typed structure that can then be
 * transformed to a schema.
 *
 * @param T generic element type that can be traversed.
 * @param Traverse alias for a module type that contains all required dependencies to traverse [T].
 * @param element generic element to be traversed.
 * @param traverse module with all required dependencies.
 * @param elementName name of the element to be traversed. Normally traversal starts with a top level structure (class),
 * which does not have an [elementName], because [elementName] is only defined for fields of a structure.
 * @param mode a [Mode] for the element to be traversed. For top level structure default is [NonNull].
 * @return an instance of [com.github.dazzbourgh.avroschemagenerator.domain.traverse.model.Element] that can be used to
 * create a specific schema.
 */
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
              Traverse : GetElementDeclaration<T>,
              Traverse : GetEnumValues<T> =
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
                        field.getElementDeclaration()
                            ?: throw NoSuchElementException(
                                """References must resolve to non-null object, however, field didn't resolve to anything: 
                                |   $field""".trimMargin()
                            ),
                        traverse,
                        name,
                        fieldMode
                    )
                EnumType -> EnumElement(
                    field.getDocName(),
                    field.getNamespaceName(),
                    name,
                    field.getEnumValues(),
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
