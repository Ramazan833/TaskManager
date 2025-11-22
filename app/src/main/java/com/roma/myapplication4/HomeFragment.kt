package com.roma.myapplication4

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roma.myapplication4.data.Task
import com.roma.myapplication4.ui.TaskAdapter
import com.roma.myapplication4.viewmodel.TaskViewModel
import java.util.UUID

class HomeFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var email: String
    private lateinit var adapter: TaskAdapter
    private lateinit var rvTasks: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val prefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        email = prefs.getString("email", "")!!

        rvTasks = view.findViewById(R.id.rvTasks)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        tvUserEmail.text = email // Set user email

        setupRecyclerView()

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        taskViewModel.getTasksForUser(email).observe(viewLifecycleOwner) { tasks ->
            adapter.updateTasks(tasks)
            updateEmptyState(tasks.isEmpty())
        }

        view.findViewById<ImageButton>(R.id.btnAdd).setOnClickListener {
            showAddTaskDialog()
        }

        view.findViewById<ImageButton>(R.id.tvLogout).setOnClickListener { // Changed to ImageButton
            prefs.edit().clear().apply()
            startActivity(Intent(requireActivity(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            requireActivity().finish()
        }

        return view
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(mutableListOf()) { task ->
            showUpdateDeleteDialog(task)
        }
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = adapter
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        rvTasks.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.edit_text_title)
        val etDesc = dialogView.findViewById<EditText>(R.id.edit_text_description)

        AlertDialog.Builder(requireContext())
            .setTitle("Новая задача")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val title = etTitle.text.toString().trim()
                if (title.isNotEmpty()) {
                    val newTaskId = UUID.randomUUID().toString()
                    val task = Task(id = newTaskId, email = email, title = title, description = etDesc.text.toString().trim())
                    taskViewModel.addTask(task)
                } else {
                    Toast.makeText(context, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showUpdateDeleteDialog(task: Task) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.edit_text_title)
        val etDesc = dialogView.findViewById<EditText>(R.id.edit_text_description)

        etTitle.setText(task.title)
        etDesc.setText(task.description)

        AlertDialog.Builder(requireContext())
            .setTitle("Изменить задачу")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val newTitle = etTitle.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    val updatedTask = task.copy(title = newTitle, description = etDesc.text.toString().trim())
                    taskViewModel.updateTask(updatedTask)
                } else {
                    Toast.makeText(context, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Удалить") { _, _ ->
                taskViewModel.deleteTask(task)
            }
            .setNeutralButton("Отмена", null)
            .show()
    }
}
