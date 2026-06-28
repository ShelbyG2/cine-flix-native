package tech.unrealistic.cineflix.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsBottomSheet(
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    MediaId: Int,
    sheetState: SheetState
) {
    if (showBottomSheet) {


        ModalBottomSheet(
            onDismissRequest,
            modifier = Modifier.fillMaxHeight(0.9f),
            sheetState = sheetState
        ) {

            Column(

            ) {
                Text(
                    "Film of Id : $MediaId Details here!",
                    color = MaterialTheme.colorScheme.primary
                )

            }
        }
    }
}