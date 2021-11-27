package com.github.dazzbourgh.avroschemagenerator.ui.dialogs

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class SuccessDialog : DialogWrapper(true) {
    init {
        title = "Success"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())

        val label = JLabel("Schema has been copied to clipboard")
        dialogPanel.add(label, BorderLayout.CENTER)

        return dialogPanel
    }

    override fun createActions(): Array<Action> {
        return arrayOf(myOKAction)
    }
}