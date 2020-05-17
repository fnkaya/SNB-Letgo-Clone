package com.example.sellnbuy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sellnbuy.R;
import com.example.sellnbuy.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private CircleImageView imgEdit, imgProfile;
    private TextInputEditText edtDisplayName, edtEmail;
    private MaterialTextView txtVerified, txtChangePassword, txtDeleteAccount;
    private Button btnUpdate;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private FirebaseUser user;

    private Bitmap bitmap = null;
    private final int TAKE_IMAGE_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        listenActions();
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadUserInfo();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar_profile);
        imgEdit = findViewById(R.id.image_edit_profile);
        imgProfile = findViewById(R.id.image_profile);
        imgProfile.setEnabled(false);
        edtDisplayName = findViewById(R.id.editText_name);
        edtEmail = findViewById(R.id.editText_email);
        txtVerified = findViewById(R.id.text_verified);
        txtChangePassword = findViewById(R.id.text_change_password);
        txtDeleteAccount = findViewById(R.id.text_delete_account);
        progressBar = findViewById(R.id.progressBar_profile);
        progressBar.setVisibility(View.GONE);
        btnUpdate = findViewById(R.id.button_update);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }



    private void loadUserInfo(){
        if ( user != null ){
            if ( user.getPhotoUrl() != null ){
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(imgProfile);
            }
            if ( user.getDisplayName() != null){
                edtDisplayName.setText(user.getDisplayName());
                edtDisplayName.setSelection(user.getDisplayName().length());
            }
            if ( user.getEmail() != null )
                edtEmail.setText(user.getEmail());

            if ( user.isEmailVerified() )
                txtVerified.setVisibility(View.GONE);
            else
                txtVerified.setText(R.string.email_not_verified);
        }
    }



    private void listenActions(){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtDisplayName.getText().toString();
                String email = edtEmail.getText().toString();
                if ( isValidate(name, email) )
                    updateProfile(name, email);
                else
                    Toast.makeText(ProfileActivity.this, R.string.fill_required_fields, Toast.LENGTH_SHORT).show();
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleProfileImage();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgProfile.setEnabled(!imgProfile.isEnabled());
                edtDisplayName.setEnabled(!edtDisplayName.isEnabled());
                edtEmail.setEnabled(!edtEmail.isEnabled());
                btnUpdate.setEnabled(!btnUpdate.isEnabled());
            }
        });

        txtVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileActivity.this, R.string.verification_mail_sent, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        txtDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });
    }



    private boolean isValidate(String name, String email){
        if ( !TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) )
            return true;
        return false;
    }



    private void updateProfile(String name, String email) {
        imgProfile.setEnabled(false);
        edtDisplayName.setEnabled(false);
        edtEmail.setEnabled(false);
        btnUpdate.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, R.string.updated_profile, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });

        // TODO: Email update
        user.updateEmail(email).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });

        if ( bitmap != null )
            uploadProfileImage(bitmap);
    }



    private void handleProfileImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if ( intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK){
            bitmap = (Bitmap) data.getExtras().get("data");
            imgProfile.setImageBitmap(bitmap);
        }
    }



    private void uploadProfileImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String uid = user.getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpeg");
        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }



    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        setUserProfileUrl(uri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }



    private void setUserProfileUrl(Uri uri){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, Objects.requireNonNull(e.getCause()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void showChangePasswordDialog(){
        View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        final TextInputEditText edtNewPassword = view.findViewById(R.id.editText_new_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.change_password_title)
                .setView(view)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeUserPassword(edtNewPassword.getText().toString());
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private  void changeUserPassword(String password){
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if ( task.isSuccessful() ){
                            Toast.makeText(ProfileActivity.this, R.string.password_changed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void showDeleteAccountDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_your_account)
                .setMessage(R.string.ru_sure_delete_account)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.VISIBLE);
                        deleteUserAccount();
                    }
                })
                .create()
                .show();
    }



    private void deleteUserAccount(){
        final String userId = user.getUid();
        if ( user != null ){
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if ( task.isSuccessful() ){
                                deleteUsersPost(userId);
                                finish();
                            }
                        }
                    });
        }
    }



    private void deleteUsersPost(String userId){
        FirebaseFirestore.getInstance()
                .collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.OWNER_ID, userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList){
                            snapshot.getReference()
                                    .delete();
                        }
                    }
                });
    }
}
