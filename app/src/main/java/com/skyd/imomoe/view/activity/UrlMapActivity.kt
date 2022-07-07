package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.database.entity.UrlMapEntity
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.ext.plus
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.AnimeTopBarStyle
import com.skyd.imomoe.view.component.compose.BackIcon
import com.skyd.imomoe.view.component.compose.TopBarIcon
import com.skyd.imomoe.view.fragment.DataSourceMarketFragment
import com.skyd.imomoe.viewmodel.UrlMapViewModel

class UrlMapActivity : BaseComponentActivity() {
    private val viewModel: UrlMapViewModel by viewModels()

    companion object {
        const val ENABLED = "enabled"
        const val JSON_DATA = "jsonData"
        const val AUTO_ADD = "autoAdd"
        const val AUTO_ADD_AND_FINISH = "autoAddAndFinish"
        const val COMPLETED = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jsonData = intent.getStringExtra(JSON_DATA)
        viewModel.autoAdd = intent.getBooleanExtra(AUTO_ADD, false)
        viewModel.autoAddAndFinish = intent.getBooleanExtra(AUTO_ADD_AND_FINISH, false)
        if (intent.getBooleanExtra(ENABLED, false)) {
            com.skyd.imomoe.net.urlMapEnabled = true
            urlMapEnabled = true
        }
        if (!jsonData.isNullOrBlank()) {
            if (viewModel.autoAdd || viewModel.autoAddAndFinish) {
                viewModel.setUrlMap(jsonData)
            } else {
                jsonDialogData.value = jsonData
                showJsonDialog.value = true
            }
        }
        viewModel.requestFinish.collectWithLifecycle(this) {
            if (it) {
                DataSourceMarketFragment.needRefresh.tryEmit(true)
                finish()
            }
        }
        setContentBase {
            UrlMapScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlMapScreen(viewModel: UrlMapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    Scaffold(topBar = {
        AnimeTopBar(
            style = AnimeTopBarStyle.Small,
            title = {
                Text(text = stringResource(R.string.url_map_activity_title))
            },
            navigationIcon = {
                BackIcon(
                    onClick = { context.activity.finish() }
                )
            },
            actions = {
                TopBarIcon(
                    painter = painterResource(id = R.drawable.ic_playlist_add_24),
                    contentDescription = stringResource(id = R.string.url_map_activity_add_by_script),
                    onClick = { showJsonDialog.value = true }
                )
            }
        )
    }, floatingActionButton = {
        ExtendedFloatingActionButton(
            modifier = Modifier.navigationBarsPadding(),
            text = {
                Text(text = stringResource(id = R.string.add))
            },
            icon = {
                Icon(Icons.Rounded.Add, null)
            },
            onClick = {
                showEditDialog.value = true
            },
        )
    }) {
        UrlMapList(it)
        if (showEditDialog.value) {
            EditDialog(
                title = stringResource(id = R.string.add),
                onConfirm = { oldUrl, newUrl ->
                    if (oldUrl.isNotBlank() && newUrl.isNotBlank()) {
                        viewModel.setUrlMap(oldUrl, newUrl)
                    }
                }
            )
        }
        if (showJsonDialog.value) {
            JsonDialog(
                title = stringResource(id = R.string.url_map_activity_add_by_script),
                onConfirm = { jsonData ->
                    if (jsonData.isNotBlank()) {
                        viewModel.setUrlMap(jsonData)
                    }
                }
            )
        }
    }
}

/**
 * URL替换总开关
 */
private var urlMapEnabled by mutableStateOf(com.skyd.imomoe.net.urlMapEnabled)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlMapEnabledCard() {
    Card(
        modifier = Modifier
            .padding(vertical = 7.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    com.skyd.imomoe.net.urlMapEnabled = !urlMapEnabled
                    urlMapEnabled = !urlMapEnabled
                }
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 15.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.url_map_activity_enable),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier.padding(top = 7.dp),
                    text = stringResource(id = R.string.url_map_activity_enable_disadvantage),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = urlMapEnabled,
                onCheckedChange = {
                    com.skyd.imomoe.net.urlMapEnabled = it
                    urlMapEnabled = it
                }
            )
        }
    }
}

/**
 * 展示列表
 */
@Composable
fun UrlMapList(paddingValues: PaddingValues) {
    val viewModel: UrlMapViewModel = hiltViewModel()
    val urlMapListState by viewModel.urlMapList.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues + WindowInsets.navigationBars.asPaddingValues() +
                PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    ) {
        item {
            UrlMapEnabledCard()
        }
        when (urlMapListState) {
            is DataState.Success -> {
                val list = urlMapListState.read()
                items(list.size) { index ->
                    UrlMapItem(list[index])
                }
            }
            else -> {}
        }
    }
}

/**
 * 列表的每一项
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UrlMapItem(urlMapEntity: UrlMapEntity) {
    val viewModel: UrlMapViewModel = hiltViewModel()
    var menuExpanded by remember { mutableStateOf(false) }
    val enabledData = urlMapEntity.enabled
    var enabled by remember { mutableStateOf(enabledData) }
    Card(
        modifier = Modifier
            .padding(vertical = 7.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(
                    onLongClick = { menuExpanded = true },
                    onClick = {
                        if (urlMapEnabled) {
                            enabled = !enabled
                            viewModel.enabledUrlMap(urlMapEntity.oldUrl, enabled)
                        }
                    }
                )
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 15.dp)
            ) {
                Text(
                    text = urlMapEntity.oldUrl,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    modifier = Modifier.padding(top = 17.dp),
                    text = stringResource(R.string.url_map_activity_new, urlMapEntity.newUrl),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = {
                    enabled = it
                    viewModel.enabledUrlMap(urlMapEntity.oldUrl, it)
                },
                enabled = urlMapEnabled
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.edit)) },
                onClick = {
                    showEditDialog.value = true
                    editDialogData.value = urlMapEntity.oldUrl to urlMapEntity.newUrl
                    menuExpanded = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Edit,
                        contentDescription = null
                    )
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.delete)) },
                onClick = {
                    showDeleteDialog.value = true
                    deleteDialogOldUrl.value = urlMapEntity.oldUrl
                    menuExpanded = false
                },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null
                    )
                })
        }
    }
    if (showDeleteDialog.value && deleteDialogOldUrl.value == urlMapEntity.oldUrl) {
        DeleteDialog()
    }
    if (showEditDialog.value &&
        editDialogData.value?.first == urlMapEntity.oldUrl &&
        editDialogData.value?.second == urlMapEntity.newUrl
    ) {
        EditDialog(
            title = stringResource(id = R.string.edit),
            onConfirm = { oldUrl, newUrl ->
                if (oldUrl.isNotBlank() && newUrl.isNotBlank()) {
                    viewModel.editUrlMap(
                        old = urlMapEntity.oldUrl to urlMapEntity.newUrl,
                        new = oldUrl to newUrl,
                        enabled = urlMapEntity.enabled
                    )
                }
            }
        )
    }
}

val showEditDialog = mutableStateOf(false)
val editDialogData = mutableStateOf<Pair<String, String>?>(null)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditDialog(
    title: String,
    onConfirm: (oldUrl: String, newUrl: String) -> Unit
) {
    var oldUrl by rememberSaveable { mutableStateOf(editDialogData.value?.first.orEmpty()) }
    var newUrl by rememberSaveable { mutableStateOf(editDialogData.value?.second.orEmpty()) }

    AlertDialog(
        onDismissRequest = {
            showEditDialog.value = false
            editDialogData.value = null
        },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                val keyboardController = LocalSoftwareKeyboardController.current
                val focusManager = LocalFocusManager.current
                TextField(
                    value = oldUrl,
                    singleLine = true,
                    onValueChange = { oldUrl = it },
                    label = {
                        Text(text = stringResource(id = R.string.url_map_activity_input_old))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.moveFocus(FocusDirection.Next)
                    })
                )
                TextField(
                    modifier = Modifier.padding(top = 12.dp),
                    value = newUrl,
                    singleLine = true,
                    onValueChange = { newUrl = it },
                    label = {
                        Text(text = stringResource(id = R.string.url_map_activity_input_new))
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    })
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = oldUrl.isNotBlank() && newUrl.isNotBlank(),
                onClick = {
                    onConfirm.invoke(oldUrl, newUrl)
                    showEditDialog.value = false
                    editDialogData.value = null
                }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showEditDialog.value = false
                    editDialogData.value = null
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

val showDeleteDialog = mutableStateOf(false)
val deleteDialogOldUrl = mutableStateOf<String?>(null)

@Composable
fun DeleteDialog(viewModel: UrlMapViewModel = hiltViewModel()) {
    AlertDialog(
        onDismissRequest = {
            showDeleteDialog.value = false
            deleteDialogOldUrl.value = null
        },
        title = {
            Text(text = stringResource(id = R.string.warning))
        },
        text = {
            Text(text = stringResource(id = R.string.url_map_activity_delete))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    deleteDialogOldUrl.value?.let {
                        viewModel.deleteUrlMap(it)
                    }
                    showDeleteDialog.value = false
                    deleteDialogOldUrl.value = null
                }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showDeleteDialog.value = false
                    deleteDialogOldUrl.value = null
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

val showJsonDialog = mutableStateOf(false)
val jsonDialogData = mutableStateOf<String?>(null)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun JsonDialog(
    title: String,
    onConfirm: (jsonData: String) -> Unit
) {
    var jsonData by rememberSaveable { mutableStateOf(jsonDialogData.value.orEmpty()) }
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = {
            showJsonDialog.value = false
            jsonDialogData.value = null
        },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                Text(text = stringResource(id = R.string.url_map_activity_json_warning))
                TextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .wrapContentHeight(),
                    value = jsonData,
                    onValueChange = { jsonData = it },
                    label = {
                        Text(text = stringResource(id = R.string.url_map_activity_input_json))
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = jsonData.isNotBlank() && jsonData.isNotBlank(),
                onClick = {
                    onConfirm.invoke(jsonData)
                    showJsonDialog.value = false
                    jsonDialogData.value = null
                }
            ) {
                Text(stringResource(id = R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showJsonDialog.value = false
                    jsonDialogData.value = null
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}