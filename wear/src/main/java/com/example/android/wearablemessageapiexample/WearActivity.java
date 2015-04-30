/*
 * Copyright 2015 Dejan Djurovski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wearablemessageapiexample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.codehaus.plexus.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WearActivity extends Activity {

    private final String RIGHT_SPIN_MOVE = "Right_Turn";
    private final String LEFT_SPIN_MOVE = "Left_Turn";
    private final String WALK_MOVE = "Advance";
    private final String RIGHT_MOVE = "Sidestep_Right";
    private final String LEFT_MOVE = "Sidestep_Left";

    private final String COMMAND_PATH = "/command";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private GoogleApiClient apiClient;
    private String remoteNodeId;
    private ImageButton mbtSpeak, mbtRight, mbtLeft, mbtWalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mbtSpeak = (ImageButton) findViewById(R.id.btSpeak);
        mbtLeft = (ImageButton) findViewById(R.id.btLeftSpin);
        mbtRight = (ImageButton) findViewById(R.id.btRightSpin);
        mbtWalk = (ImageButton) findViewById(R.id.btWalk);

        apiClient = apiClientFactory();
        checkVoiceRecognition();
    }

    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mbtSpeak.setEnabled(false);
            showToastMessage("Voice recognizer not present");
        }
    }

    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());

        // Given an hint to the recognizer about what the user is going to say
        //There are two form of language model available
        //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
        //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //Start the Voice recognizer activity for the result.
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    public void rightSpinMove(View view) {
        new SendMessage().execute(RIGHT_SPIN_MOVE);
    }

    public void leftSpinMove(View view) {
        new SendMessage().execute(LEFT_SPIN_MOVE);
    }

    public void walkMove(View view) {
        new SendMessage().execute(WALK_MOVE);
    }

    public void leftWalkMove(View view) {
        new SendMessage().execute(LEFT_MOVE);
    }

    public void rightWalkMove(View view) {
        new SendMessage().execute(RIGHT_MOVE);
    }

    /**
     * Allows the app to send asyncronic messages and no blocking the UI thread
     */
    private class SendMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Wearable.MessageApi.sendMessage(apiClient, remoteNodeId, COMMAND_PATH, params[0].getBytes());
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If Voice recognition is successful then it returns RESULT_OK
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (!textMatchList.isEmpty()) {
                String voiceMessage = StringUtils.join(textMatchList.iterator(), "#");
                Wearable.MessageApi.sendMessage(apiClient, remoteNodeId, COMMAND_PATH, voiceMessage.getBytes());
            }
            //Result code for various error.
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }

            //TODO: no message sent may be possible.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Helper method to show the toast message
     **/
    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check is Google Play Services available
        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (connectionResult != ConnectionResult.SUCCESS) {
            // Google Play Services is NOT available. Show appropriate error dialog
            GooglePlayServicesUtil.showErrorDialogFragment(connectionResult, this, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        } else {
            apiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        // Unregister Node and Message listeners, disconnect GoogleApiClient and disable buttons
        Wearable.NodeApi.removeListener(apiClient, null);
        apiClient.disconnect();
        super.onPause();
    }

    /**
     * Create GoogleApiClient
     */
    private GoogleApiClient apiClientFactory() {
        return new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                // Register Node and Message listeners
                Wearable.NodeApi.addListener(apiClient, null);
                // If there is a connected node, get it's id that is used when sending messages
                Wearable.NodeApi.getConnectedNodes(apiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                            remoteNodeId = getConnectedNodesResult.getNodes().get(0).getId();
                        }
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        }).addApi(Wearable.API).build();
    }
}
