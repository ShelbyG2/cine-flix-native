package tech.unrealistic.cineflix.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun HomeScreen (onNavigate :(()-> Unit)? =null){
    Box(Modifier.fillMaxSize()){
        Text("This is Home")
    }


}