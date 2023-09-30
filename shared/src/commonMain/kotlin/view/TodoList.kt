package view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import common.di.DependencyProvider
import domain.TodoItem

@Composable
fun TodoListView(viewModel: TodosListViewModel = DependencyProvider.todosListViewModel) {
    val todos = viewModel.todos.collectAsState().value
    val currentlyEditingTodo = viewModel.currentTodo.value
    TodoListViewStateless(
        todos,
        currentlyEditingTodo,
        onTodoCompletedChange = { viewModel.toggleTodoCompletion(it) },
        onTodoTextChange = { todo, text -> viewModel.setCurrentTodoItem(todo.copy(text = text)) },
        onSelectTodo = { viewModel.setCurrentTodoItem(it) },
        onClearCurrentTodo = { viewModel.clearCurrentTodoItem() },
        onRemoveTodo = { viewModel.removeTodo(it) },
        onTodoAdded = { viewModel.addTodo(it) }
    )
}

@Composable
private fun TodoListViewStateless(
    todos: List<TodoItem>,
    currentlyEditingTodo: TodoItem,
    onTodoCompletedChange: (TodoItem) -> Unit = {},
    onTodoTextChange: (TodoItem, String) -> Unit = { _, _ -> },
    onSelectTodo: (TodoItem) -> Unit = {},
    onClearCurrentTodo: () -> Unit = {},
    onRemoveTodo: (TodoItem) -> Unit = {},
    onTodoAdded: (TodoItem) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    var currentlyEditingTodoTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                currentlyEditingTodo.text,
                selection = TextRange(currentlyEditingTodo.text.length)
            )
        )
    }
    LazyColumn {
        items(todos) { todo ->
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 16.dp)) {
                Checkbox(checked = todo.done, onCheckedChange = {
                    onTodoCompletedChange(todo)
                })
                TextField(
                    value = if (currentlyEditingTodo.id == todo.id)
                        currentlyEditingTodoTextFieldValue
                    else TextFieldValue(todo.text),
                    onValueChange = {
                        onTodoTextChange(currentlyEditingTodo, it.text)
                        currentlyEditingTodoTextFieldValue = it
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledTextColor = LocalContentColor.current.copy(LocalContentAlpha.current)
                    ),
                    modifier = Modifier.clickable {
                        onSelectTodo(todo)
                        currentlyEditingTodoTextFieldValue = TextFieldValue(
                            todo.text,
                            selection = TextRange(todo.text.length)
                        )
                    }.align(Alignment.CenterVertically)
                        .padding(start = 16.dp)
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onGloballyPositioned {
                            focusRequester.requestFocus()
                        },
                    enabled = currentlyEditingTodo.id == todo.id,
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onGo = {
                            onClearCurrentTodo()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go)
                )
                if (currentlyEditingTodo == todo) {
                    IconButton(
                        onClick = { onRemoveTodo(todo) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            }

        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 32.dp).clickable {
                    onTodoAdded(TodoItem())
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Add, null)
                Text("Add a todo", modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}
