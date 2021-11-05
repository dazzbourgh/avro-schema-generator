package com.github.dazzbourgh.avroschemagenerator.domain.traverse

import com.intellij.openapi.vfs.VirtualFileFilter
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.IdeaTestUtil
import com.intellij.testFramework.JavaPsiTestCase
import com.intellij.testFramework.PsiTestUtil
import junit.framework.TestCase
import java.io.File

internal class TraverseModuleTest : JavaPsiTestCase() {
    override fun getTestDataPath() = "src/test/testData/traverse/psi/getDocName"

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        val root = File(testDataPath).absolutePath
        PsiTestUtil.removeAllRoots(myModule, IdeaTestUtil.getMockJdk17())
        createTestProjectStructure(root)
    }

    fun `test psiGetDocName`() {
        PsiManagerEx.getInstanceEx(project).setAssertOnFileLoadingFilter(VirtualFileFilter.ALL, testRootDisposable)
        val clazz = myJavaFacade.findClass("SomeTestClass", GlobalSearchScope.allScope(myProject))!!
        val expectedDocName = "SomeTestClass"
        val actualDocName = with(psiGetDocName) { clazz.getDocName() }
        TestCase.assertEquals(expectedDocName, actualDocName)
    }
}