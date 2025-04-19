package com.example.java_pro01.dialogs

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.example.java_pro01.models.Lutemon

class ImproveStatsDialog(private val context: Context) {
    
    fun show(lutemon: Lutemon, onStatsImproved: () -> Unit) {
        if (lutemon.getExperience() < 2) {
            Toast.makeText(context, "Not enough experience points! Need 2 points to improve stats.", Toast.LENGTH_SHORT).show()
            return
        }

        val options = arrayOf("HP (+1)", "Attack (+1)", "Defense (+1)")
        
        AlertDialog.Builder(context)
            .setTitle("Improve Stats")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> improveHP(lutemon, onStatsImproved)
                    1 -> improveAttack(lutemon, onStatsImproved)
                    2 -> improveDefense(lutemon, onStatsImproved)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun improveHP(lutemon: Lutemon, onStatsImproved: () -> Unit) {
        if (lutemon.getExperience() >= 2) {
            lutemon.improveMaxHealth(1)
            lutemon.addExperience(-2)
            showSuccessMessage("HP improved! New max HP: ${lutemon.getMaxHealth()}")
            onStatsImproved()
        } else {
            showNotEnoughExpMessage()
        }
    }

    private fun improveAttack(lutemon: Lutemon, onStatsImproved: () -> Unit) {
        if (lutemon.getExperience() >= 2) {
            lutemon.improveAttack(1)
            lutemon.addExperience(-2)
            showSuccessMessage("Attack improved! New attack: ${lutemon.getAttack()}")
            onStatsImproved()
        } else {
            showNotEnoughExpMessage()
        }
    }

    private fun improveDefense(lutemon: Lutemon, onStatsImproved: () -> Unit) {
        if (lutemon.getExperience() >= 2) {
            lutemon.improveDefense(1)
            lutemon.addExperience(-2)
            showSuccessMessage("Defense improved! New defense: ${lutemon.getDefense()}")
            onStatsImproved()
        } else {
            showNotEnoughExpMessage()
        }
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showNotEnoughExpMessage() {
        Toast.makeText(context, "Not enough experience points! Need 2 points to improve stats.", Toast.LENGTH_SHORT).show()
    }
} 