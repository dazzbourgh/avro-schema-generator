package com.github.dazzbourgh.avroschemagenerator.domain

class DelegatingTraverseModule<T>(
    getType: GetType<T>,
    getDocName: GetDocName<T>,
    getNamespaceName: GetNamespaceName<T>,
    getProperties: GetProperties<T>,
    getPropertyNames: GetPropertyNames<T>,
    getMode: GetMode<T>
) : GetType<T> by getType,
    GetDocName<T> by getDocName,
    GetNamespaceName<T> by getNamespaceName,
    GetProperties<T> by getProperties,
    GetPropertyNames<T> by getPropertyNames,
    GetMode<T> by getMode
