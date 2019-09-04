package com.somago.gamestation;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.somago.gamestation.Models.Game;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class SellActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ConstraintLayout addImage;
    private Bitmap photo;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 500;
    private static int RESULT_LOAD_IMAGE = 1;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Toolbar sell_toolbar;
    private Button sellButton;
    private EditText gameName, gamePrice;
    private RadioGroup groupConsole, groupCondition;
    private CoordinatorLayout sellActivityLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Game game;
    private ImageView add_image;
    private TextView add_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        intiUI();
        setSupportActionBar(sell_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog pickerDialog = new Dialog(SellActivity.this, R.style.dialog);
                pickerDialog.setContentView(R.layout.dialog_image_picker);
                pickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pickerDialog.setCancelable(true);
                pickerDialog.show();

                TextView fromGallery = pickerDialog.findViewById(R.id.dialog_picker_gallery);
                fromGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickerDialog.dismiss();
                        OpenGallery();
                    }
                });
                TextView withCamera = pickerDialog.findViewById(R.id.dialog_picker_Camera);
                withCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickerDialog.dismiss();
                        OpenCameraActivity();
                    }
                });
            }
        });
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishGame();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void intiUI() {
        sell_toolbar = findViewById(R.id.toolbar_sell);
        sellActivityLayout = findViewById(R.id.sell_activity_layout);
        addImage = findViewById(R.id.addImageSell);
        sellButton = findViewById(R.id.publish_game_btn);
        groupCondition = findViewById(R.id.game_condition_sell_radio_group);
        groupConsole = findViewById(R.id.game_console_radio_group);
        gameName = findViewById(R.id.game_name_et);
        gamePrice = findViewById(R.id.game_price_et);
        add_image = findViewById(R.id.add_icon);
        add_text = findViewById(R.id.add_text);
    }

    private void OpenGallery(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SellActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        } else {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    private void OpenCameraActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SellActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    public void publishGame() {
        game = new Game();
        game.setUserID(mAuth.getCurrentUser().getUid());
        int gamedataFlag = 0;

        String conditionString = null;
        String consoleString = null;

        int condition = groupCondition.getCheckedRadioButtonId();
        if (condition == -1) {
            gamedataFlag++;
        } else {
            conditionString = ((RadioButton) findViewById(condition)).getText().toString();
            game.setCondition(conditionString);
        }
        int console = groupConsole.getCheckedRadioButtonId();
        if (console == -1) {
            gamedataFlag++;
        } else {
            consoleString = ((RadioButton) findViewById(console)).getText().toString();
            game.setConsole(consoleString);
        }
        String name = gameName.getText().toString().trim();
        if (name.isEmpty()) {
            gamedataFlag++;
        } else {
            game.setName(name);
        }
        String price = gamePrice.getText().toString().trim();
        if (price.isEmpty()) {
            gamedataFlag++;
        } else {
            game.setPrice(price);
        }
        if (photo == null) {
            gamedataFlag++;
        } else {
            uploadImageToStorage(photo);
        }

        if (gamedataFlag > 0) {
            showSnackbar(getResources().getString(R.string.game_data_missed));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                showSnackbar(getResources().getString(R.string.camera_permission_denied));
            }
        }
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            } else {
                showSnackbar(getResources().getString(R.string.storage_permission_denied));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            changeAddImageLayout();
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            photo = BitmapFactory.decodeFile(picturePath);
            changeAddImageLayout();
        }
    }

    private void changeAddImageLayout(){
        add_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
        add_image.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccentSecondary), android.graphics.PorterDuff.Mode.MULTIPLY);
        add_text.setText(getResources().getString(R.string.image_added_text));
        add_text.setTextColor(getResources().getColor(R.color.colorAccentSecondary));
    }

    private void uploadImageToStorage(Bitmap photo) {
        final Dialog pleaseWaitDialog = new Dialog(this, R.style.dialog);
        pleaseWaitDialog.setContentView(R.layout.dialog_upload_game);
        pleaseWaitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pleaseWaitDialog.setCancelable(false);
        pleaseWaitDialog.show();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        final StorageReference ref = storageReference.child(getResources().getString(R.string.image_temp_dir) + UUID.randomUUID().toString());
        final UploadTask uploadTask = ref.putBytes(bitmapdata);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(SellActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    game.setImage(downloadUri.toString());
                    db.collection(getResources().getString(R.string.collection_games)).add(game).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            showSnackbar(getResources().getString(R.string.publish_game_success));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showSnackbar(getResources().getString(R.string.publish_game_error));
                        }
                    });
                } else {
                    Toast.makeText(SellActivity.this, getResources().getString(R.string.image_upload_error) + task.getException(), Toast.LENGTH_SHORT).show();
                }
                pleaseWaitDialog.dismiss();
            }
        });
    }

    public void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(sellActivityLayout, message, Snackbar.LENGTH_LONG);
        TextView tv = (TextView) (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextSize(18);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.sansregular);
        tv.setTypeface(Typeface.create(typeface, Typeface.BOLD));
        tv.setTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }
}
