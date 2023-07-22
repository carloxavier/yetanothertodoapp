import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun TodoListView() {
    val todos = TodosState.todos.collectAsState().value
    val currentlyEditingTodo = TodosState.currentTodo
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                currentlyEditingTodo.value.text,
                selection = TextRange(currentlyEditingTodo.value.text.length)
            )
        )
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Column {
            todos.forEach { todo ->
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 16.dp)) {
                    Checkbox(checked = todo.done, onCheckedChange = {
                        TodosState.toggleTodoCompletion(todo)
                    })
                    if (currentlyEditingTodo.value.id == todo.id) {
                        OutlinedTextField(
                            value = textFieldValue,
                            onValueChange = {
                                TodosState.updateCurrentTodoItem(it.text)
                                textFieldValue = it
                            },
                            modifier = Modifier.clickable {
                                Navigator.navigate(
                                    Screen.TodoDetails,
                                    todo
                                )
                            }.align(Alignment.CenterVertically)
                                .padding(start = 16.dp)
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onGloballyPositioned {
                                    focusRequester.requestFocus()
                                },
                            enabled = true,
                            singleLine = true,
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    TodosState.clearCurrentTodoItem()
                                }
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go)
                        )
                    } else {
                        Text(
                            todo.text,
                            modifier = Modifier.clickable {
                                TodosState.setCurrentTodoItem(todo)
                                textFieldValue = TextFieldValue(
                                    todo.text,
                                    selection = TextRange(todo.text.length)
                                )
                            }.align(Alignment.CenterVertically).weight(1f).padding(start = 16.dp),
                        )
                    }
                    if (currentlyEditingTodo.value == todo) {
                        IconButton(
                            onClick = { TodosState.removeTodo(todo) },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                }
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 32.dp).clickable {
                TodosState.addTodo(TodoItem())
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Add, null)
            Text("Add a todo", modifier = Modifier.padding(start = 16.dp))
        }
    }
}

@Composable
fun TodoDetails(param: TodoItem? = null) {
    Column {
        TopAppBar(title = { Text("Todo Details") }, navigationIcon = {
            IconButton(onClick = { Navigator.goBack() }) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        })
        Text(param?.text ?: "")
    }
}
