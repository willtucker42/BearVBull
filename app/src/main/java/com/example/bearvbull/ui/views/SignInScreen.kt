package com.example.bearvbull.ui.views

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bearvbull.R
import com.example.bearvbull.ui.components.BearVBullTitle
import com.example.bearvbull.ui.theme.DeepPurple
import com.example.bearvbull.ui.theme.poppinsFontFamily
import com.example.bearvbull.util.getTimeUntilPredictionMarketClose
import com.example.bearvbull.viewmodel.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.Flow

@Composable
fun SignInScreen(mainViewModel: MainViewModel, gsc: GoogleSignInClient, sp: SharedPreferences) {
    getTimeUntilPredictionMarketClose()
    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val intent = it.data
            if (it.data != null) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(intent)
//                handleSignInResult(task)
                task.addOnSuccessListener { googleAccount ->
                    mainViewModel.googleSignInClient = gsc
                    println("Login SUCCESS ${googleAccount.email} ${googleAccount.id}")
                    mainViewModel._userId.value = googleAccount.id.toString()
                    mainViewModel.checkIfUserExistsInDb(googleAccount, sp)
                }
                task.addOnFailureListener { e ->
                    println("Login FAILURE $e")
                }
            } else {
                println("result code: ${it.resultCode}")
                println("data ${it.data}")
            }
        } else {
            println(it.resultCode)
        }
    }
    Surface {
        Box(
            modifier = Modifier.background(DeepPurple)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp)
                ) {
                    BearVBullTitle()
                    Spacer(modifier = Modifier.weight(1.0F))
                }
                Row {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "The Stock Market Prediction App",
                            fontFamily = poppinsFontFamily,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 34.sp
                        )
                        Text(
                            text = "Elo rankings, leaderboards, and something else",
                            fontFamily = poppinsFontFamily,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp
                        )
                        Button(
                            onClick = {
                                      startForResult.launch(gsc.signInIntent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google_icon),
                                contentDescription = ""
                            )
                            Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1.0F))
                }
            }
        }
    }
}