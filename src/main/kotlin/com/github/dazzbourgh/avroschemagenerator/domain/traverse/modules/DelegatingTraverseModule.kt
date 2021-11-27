package com.github.dazzbourgh.avroschemagenerator.domain.traverse.modules

import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetDocName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetElementDeclaration
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetEnumValues
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetMode
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetNamespaceName
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetProperties
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetPropertyNames
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.typeclasses.GetType

class DelegatingTraverseModule<T>(
    getType: GetType<T>,
    getDocName: GetDocName<T>,
    getNamespaceName: GetNamespaceName<T>,
    getProperties: GetProperties<T>,
    getPropertyNames: GetPropertyNames<T>,
    getMode: GetMode<T>,
    getElementDeclaration: GetElementDeclaration<T>,
    getEnumValues: GetEnumValues<T>
) : GetType<T> by getType,
    GetDocName<T> by getDocName,
    GetNamespaceName<T> by getNamespaceName,
    GetProperties<T> by getProperties,
    GetPropertyNames<T> by getPropertyNames,
    GetMode<T> by getMode,
    GetElementDeclaration<T> by getElementDeclaration,
    GetEnumValues<T> by getEnumValues
