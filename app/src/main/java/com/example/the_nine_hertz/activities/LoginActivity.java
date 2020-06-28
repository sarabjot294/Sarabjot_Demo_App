package com.example.the_nine_hertz.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_nine_hertz.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Button nextPageBtn;
    LoginButton fbLoginButton;
    SignInButton googleBtn;
    CircleImageView circleImageView;
    TextView name;
    GoogleApiClient googleApiClient;
    private static final int SIGN_IN = 1;
    private CallbackManager callbackManager;
    private static final String TAG = "LoginActivity";
    boolean signedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fbLoginButton = findViewById(R.id.facebookBtnn);
        googleBtn = findViewById(R.id.googleBtn);
        name = findViewById(R.id.name);
        circleImageView = findViewById(R.id.profilePic);
        nextPageBtn = findViewById(R.id.nextBtn);

        FacebookSignIn();
        GoogleSignIn();
        if(signedIn)
            nextPageBtn.setText("Proceed");
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signedIn)
                    goToNextActivity(LoginActivity.this);
                else
                    Toast.makeText(LoginActivity.this,"Please Sign In first",Toast.LENGTH_SHORT).show();
            }
        });
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken == null) {
                LoggedOut();
            }
            else
                loadUserProfile(currentAccessToken);
        }
    };

    public void FacebookSignIn()
    {
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        checkAccessToken();
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    public void GoogleSignIn()
    {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(LoginActivity.this).enableAutoManage(LoginActivity.this,LoginActivity.this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Google Butto Clicked");

                OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
                if(optionalPendingResult.isDone())
                {
                    Log.d(TAG, "last sign in found");
                    GoogleSignInResult result = optionalPendingResult.get();
                    loadGoogleUserProfile(result);
                }
                else
                    {
                        Log.d(TAG, "New sign in");

                        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                        startActivityForResult(intent, SIGN_IN);
                    }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                loadGoogleUserProfile(result);
            }
            else
                Log.e(TAG, "onActivityResult: Google Login Failed" );
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void loadGoogleUserProfile(GoogleSignInResult result)
    {
        GoogleSignInAccount account = result.getSignInAccount();
        String AccountName = account.getDisplayName();
        Uri url = account.getPhotoUrl();
        name.setText(AccountName);
        Glide.with(LoginActivity.this).load(url).into(circleImageView);
        signedInSuccess();
    }
    public void loadUserProfile(AccessToken accessToken)
    {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    name.setText(first_name + " " + last_name);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();
                    Glide.with(LoginActivity.this).load(image_url).into(circleImageView);
                    signedInSuccess();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle param = new Bundle();
        param.putString("fields", "first_name, last_name, email,id");
        graphRequest.setParameters(param);
        graphRequest.executeAsync();
    }

    private void checkAccessToken()
    {
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            //Already logged in
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }

    private void goToNextActivity(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private void signedInSuccess()
    {
        signedIn = true;
        nextPageBtn.setText("Proceed");
        Toast.makeText(LoginActivity.this, "Successfully Singed In", Toast.LENGTH_SHORT).show();
    }

    private void LoggedOut()
    {
        name.setText("Facebook/Google's Name");
        circleImageView.setImageResource(0);
        Toast.makeText(LoginActivity.this,"User logged out", Toast.LENGTH_LONG).show();
        signedIn = false;
        nextPageBtn.setText("Sign In First");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
