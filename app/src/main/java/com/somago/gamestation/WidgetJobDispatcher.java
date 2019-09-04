package com.somago.gamestation;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.somago.gamestation.Models.Game;
import com.somago.gamestation.Widget.GamesWidget;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WidgetJobDispatcher extends JobService {

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.d("WidgetJobDipatcher", "Start");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getResources().getString(R.string.collection_games))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    ArrayList<Game> games = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        games.add(doc.toObject(Game.class));
                    }
                    GamesWidget.sendRefreshBroadcast(getApplicationContext(), games, 0);
                }
            }
        });
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.d("WidgetJobDipatcher", "Stop");
        return false;
    }
}
