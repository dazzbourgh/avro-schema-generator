package com.github.dazzbourgh.avroschemagenerator.ui.actions

import com.github.dazzbourgh.avroschemagenerator.domain.schema.module.SchemaGenerationModule
import com.github.dazzbourgh.avroschemagenerator.domain.schema.typeclasses.avro.GenerateAvroSchema
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.modules.PsiTraverseModule
import com.github.dazzbourgh.avroschemagenerator.domain.traverse.traverse
import com.github.dazzbourgh.avroschemagenerator.misc.typeclasses.stringify.AvroSchemaStringify
import com.github.dazzbourgh.avroschemagenerator.ui.dialogs.ErrorDialog
import com.github.dazzbourgh.avroschemagenerator.ui.dialogs.SuccessDialog
import com.intellij.designer.clipboard.SimpleTransferable
import com.intellij.ide.highlighter.JavaFileType
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
            try {
                val element = traverse(clazz, PsiTraverseModule)
                // This can be extracted to generate various schemas once support added.
                val (generateSchema, stringify) = SchemaGenerationModule(GenerateAvroSchema, AvroSchemaStringify)
                val schema = with(generateSchema) { element.generateSchema() }
                val string = with(stringify) { schema.stringify() }
                val transferable = SimpleTransferable(string, DataFlavor.stringFlavor)
                CopyPasteManager.getInstance().setContents(transferable)
                SuccessDialog().show()
            } catch (e: Throwable) {
                val project: Project? = event.getData(CommonDataKeys.PROJECT)
                project?.also {
                    ErrorDialog(
                        "Unable to generate schema. Reason:",
                        e.javaClass.name,
                        e.message ?: ""
                    ).show()
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        e.presentation.isVisible = psiFile?.fileType is JavaFileType
    }
}
