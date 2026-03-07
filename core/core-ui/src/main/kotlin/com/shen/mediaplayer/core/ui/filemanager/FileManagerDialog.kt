package com.shen.mediaplayer.core.ui.filemanager

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.InputType
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.ui.databinding.DialogFileManagerBinding

class FileManagerDialog(
    private val activity: Activity,
    private val mediaFile: MediaFile,
    private val onDelete: (MediaFile) -> Unit,
    private val onRename: (MediaFile, String) -> Unit,
    private val onMove: (MediaFile) -> Unit,
    private val onHide: (MediaFile) -> Unit,
    private val onFavoriteToggle: (MediaFile) -> Unit,
    private val isFavorite: Boolean
) {

    fun show() {
        val binding = DialogFileManagerBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity)
        dialog.setContentView(binding.root)

        // Set current filename
        binding.tvFileName.text = mediaFile.displayName
        binding.cbFavorite.isChecked = isFavorite

        // Set click listeners
        binding.btnShare.setOnClickListener {
            shareFile(mediaFile)
            dialog.dismiss()
        }

        binding.btnRename.setOnClickListener {
            dialog.dismiss()
            showRenameDialog(mediaFile)
        }

        binding.btnMove.setOnClickListener {
            dialog.dismiss()
            onMove(mediaFile)
        }

        binding.btnDelete.setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmDialog(mediaFile)
        }

        binding.btnHide.setOnClickListener {
            dialog.dismiss()
            onHide(mediaFile)
        }

        binding.cbFavorite.setOnCheckedChangeListener { _, checked ->
            dialog.dismiss()
            onFavoriteToggle(mediaFile)
        }

        dialog.show()
    }

    private fun shareFile(mediaFile: MediaFile) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, mediaFile.uri)
            type = mediaFile.mimeType
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        activity.startActivity(Intent.createChooser(shareIntent, "分享文件"))
    }

    private fun showRenameDialog(mediaFile: MediaFile) {
        val currentName = mediaFile.displayName
        val input = android.widget.EditText(activity).apply {
            setText(currentName)
            inputType = InputType.TYPE_CLASS_TEXT
            setSelection(0, currentName.lastIndexOf('.'))
        }

        AlertDialog.Builder(activity)
            .setTitle("重命名文件")
            .setView(input)
            .setPositiveButton("确定") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty() && newName != currentName) {
                    onRename(mediaFile, newName)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteConfirmDialog(mediaFile: MediaFile) {
        AlertDialog.Builder(activity)
            .setTitle("删除文件")
            .setMessage("确定要删除 \"${mediaFile.displayName}\" 吗？此操作不可撤销。")
            .setPositiveButton("删除") { _, _ ->
                onDelete(mediaFile)
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
