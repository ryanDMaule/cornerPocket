package com.example.cornerpocket.Utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import com.example.cornerpocket.R
import com.google.android.material.button.MaterialButton

object DialogUtils {

    fun returnToMenuDialog(context : Context, nc : NavController, destination : Int) {
        // Inflate the dialog layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.two_button_dialog, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Initialize dialog views
        val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)
        val dialogDescription: TextView = dialogView.findViewById(R.id.dialog_description)
        val dialogButton1: MaterialButton = dialogView.findViewById(R.id.dialog_button_1)
        val dialogButton2: MaterialButton = dialogView.findViewById(R.id.dialog_button_2)

        dialogTitle.text = context.getString(R.string.return_to_menu)
        dialogDescription.text = context.getString(R.string.return_to_menu_content)
        dialogButton1.text = context.getString(R.string.cancel)
        dialogButton2.text = context.getString(R.string.exit)

        dialogButton1.setOnClickListener {
            dialog.dismiss()
        }

        dialogButton2.setOnClickListener {
            nc.navigate(destination)
            dialog.dismiss()
        }

        //prevents showing solid whit in the corners where the edges are rounded
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Show the dialog
        dialog.show()
    }

    fun methodOfVictoryDialog(context : Context) {
        // Inflate the dialog layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.one_button_dialog, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Initialize dialog views
        val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)
        val dialogDescription: TextView = dialogView.findViewById(R.id.dialog_description)
        val dialogButton1: MaterialButton = dialogView.findViewById(R.id.dialog_button)

        dialogTitle.text = context.getString(R.string.method_of_victory)
        dialogDescription.text = context.getString(R.string.method_of_victory_content)
        dialogButton1.text = context.getString(R.string.close)

        dialogButton1.setOnClickListener {
            dialog.dismiss()
        }

        //prevents showing solid whit in the corners where the edges are rounded
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Show the dialog
        dialog.show()
    }

    fun permissionRejectedDialog(context : Context) {
        // Inflate the dialog layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.two_button_dialog, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Initialize dialog views
        val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)
        val dialogDescription: TextView = dialogView.findViewById(R.id.dialog_description)
        val dialogButton1: MaterialButton = dialogView.findViewById(R.id.dialog_button_1)
        val dialogButton2: MaterialButton = dialogView.findViewById(R.id.dialog_button_2)

        dialogTitle.text = context.getString(R.string.permission_required)
        dialogDescription.text = context.getString(R.string.permission_required_details)
        dialogButton1.text = context.getString(R.string.close)
        dialogButton2.text = context.getString(R.string.settings)

        dialogButton1.setOnClickListener {
            dialog.dismiss()
        }

        dialogButton2.setOnClickListener {
            HelperFunctions.openAppSettings(context)
        }

        //prevents showing solid whit in the corners where the edges are rounded
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Show the dialog
        dialog.show()
    }

    fun loadingDialog(context : Context) : AlertDialog  {
        // Inflate the dialog layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        //prevents showing solid whit in the corners where the edges are rounded
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

}