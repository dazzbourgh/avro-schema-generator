package com.github.dazzbourgh.avroschemagenerator.actions

import com.github.dazzbourgh.avroschemagenerator.dialogs.ErrorDialog
import com.github.dazzbourgh.avroschemagenerator.dialogs.SuccessDialog
import com.github.dazzbourgh.avroschemagenerator.domain.schema.avro.AvroSchemaGenerator.generateSchema
import com.github.dazzbourgh.avroschemagenerator.domain.schema.avro.AvroSchemaStringify
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.psi.PsiTraverse.PsiTraverseModule
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.traverse
import com.intellij.designer.clipboard.SimpleTransferable
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import java.awt.datatransfer.DataFlavor


class CreateAvroSchemaAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val clazz: PsiClass? = event.getData(CommonDataKeys.PSI_FILE)?.let { (it as PsiJavaFile).classes[0] }
        val lang = event.getData(CommonDataKeys.PSI_FILE)?.language
        if (clazz != null && JavaLanguage.INSTANCE == lang) {
            val element = try {
                traverse(clazz, PsiTraverseModule)
            } catch (e: Throwable) {
                val project: Project? = event.getData(CommonDataKeys.PROJECT)
                project?.also {
                    ErrorDialog(
                        "Unable to generate schema. Reason:",
                        e.javaClass.name,
                        e.message ?: ""
                    ).show()
                }
                return
            }
            val schema = generateSchema(element)
            val string = with(AvroSchemaStringify) { schema.stringify() }
            val transferable = SimpleTransferable(string, DataFlavor.stringFlavor)
            CopyPasteManager.getInstance().setContents(transferable)
            SuccessDialog().show()
        }
    }
}