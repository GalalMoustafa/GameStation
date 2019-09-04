package com.somago.gamestation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.somago.gamestation.Models.Game;
import com.somago.gamestation.Models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar details_toolbar;
    private ImageView game_image_details;
    private TextView console_text, price_text, location_text, seller_name, seller_mobile, condition_text;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        db = FirebaseFirestore.getInstance();
        init_ui();
        setSupportActionBar(details_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().getExtras() != null){
            Bundle b = getIntent().getExtras();
            Game game = (Game) b.getSerializable(getResources().getString(R.string.game_bundle_name));
            if (game != null){
                setUI(game);
            }
        }
    }

    private void setUI(Game game) {
        details_toolbar.setTitle(game.getName());
        price_text.setText(game.getPrice());
        console_text.setText(game.getConsole());
        condition_text.setText(game.getCondition());
        Picasso.get().load(game.getImage()).placeholder(R.drawable.placeholder_games_cover).into(game_image_details);
        getUserData(game.getUserID());
    }

    private void getUserData(String uID) {
        db.collection(getResources().getString(R.string.collection_users)).whereEqualTo(getResources().getString(R.string.id_data_field), uID).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<User> user = queryDocumentSnapshots.toObjects(User.class);
                    if (!user.isEmpty()){
                        seller_name.setText(user.get(0).getName());
                        if (user.get(0).getAddress().isEmpty()){
                            location_text.setText(getResources().getString(R.string.not_available));
                        }else {
                            location_text.setText(user.get(0).getAddress());
                        }
                        if (user.get(0).getMobile().isEmpty()){
                            seller_mobile.setText(getResources().getString(R.string.not_available));
                        }else {
                            seller_mobile.setText(user.get(0).getMobile());
                        }
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    location_text.setText(getResources().getString(R.string.not_available));
                    seller_mobile.setText(getResources().getString(R.string.not_available));
                    seller_name.setText(getResources().getString(R.string.not_available));
                }
            });
    }

    private void init_ui(){
        details_toolbar = findViewById(R.id.toolbar_details);
        game_image_details = findViewById(R.id.game_image_details);
        console_text = findViewById(R.id.console_text_details_view);
        price_text = findViewById(R.id.price_details);
        location_text = findViewById(R.id.location_details);
        seller_mobile = findViewById(R.id.seller_mobile_details);
        seller_name = findViewById(R.id.seller_name_details);
        condition_text = findViewById(R.id.game_status_details);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
