package com.somago.gamestation.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.somago.gamestation.GamesAdapter;
import com.somago.gamestation.MainActivity;
import com.somago.gamestation.Models.Game;
import com.somago.gamestation.R;
import com.somago.gamestation.Widget.GamesWidget;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment {

    private FirebaseFirestore db;
    public GamesAdapter gamesAdapter;
    private String new_text, all_text, used_text, ps4, xbox, pc;
    private RecyclerView games_recycler;
    private List<Game> games, filterdList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);

        //get Strings from Resources
        new_text = getContext().getResources().getString(R.string.new_text);
        used_text = getContext().getResources().getString(R.string.used_text);
        all_text = getContext().getResources().getString(R.string.all_text);
        ps4 = getContext().getResources().getString(R.string.ps4);
        xbox = getContext().getResources().getString(R.string.xbox_one);
        pc = getContext().getResources().getString(R.string.pc);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        games_recycler = rootView.findViewById(R.id.games_recycler);
        games_recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        gamesAdapter = new GamesAdapter(new ArrayList<Game>(), getActivity());
        games_recycler.setAdapter(gamesAdapter);

        ((MainActivity) getActivity()).conditonString = all_text;
        ((MainActivity) getActivity()).consoleString = ps4;

        getGames();

        ((MainActivity) getActivity()).conditionRadios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checked = radioGroup.getCheckedRadioButtonId();
                switch (checked) {
                    case R.id.all_radio:
                        ((MainActivity) getActivity()).conditonString = all_text;
                        applyFilters();
                        break;
                    case R.id.new_radio:
                        ((MainActivity) getActivity()).conditonString = new_text;
                        applyFilters();
                        break;
                    case R.id.used_radio:
                        ((MainActivity) getActivity()).conditonString = used_text;
                        applyFilters();
                        break;
                }
            }
        });

        ((MainActivity) getActivity()).viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ((MainActivity) getActivity()).consoleString = ps4;
                } else if (position == 1) {
                    ((MainActivity) getActivity()).consoleString = xbox;
                } else if (position == 2) {
                    ((MainActivity) getActivity()).consoleString = pc;
                }

                if (games != null) {
                    applyFilters();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return rootView;
    }

    public void getGames() {
        db.collection(getResources().getString(R.string.collection_games))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!=null){
                            Log.d(e.getLocalizedMessage(),e.getMessage());
                        }
                        else {
                            games = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                games.add(doc.toObject(Game.class));
                            }
                            GamesWidget.sendRefreshBroadcast(getContext(), new ArrayList<>(games), 0);
                            applyFilters();
                        }
                    }

                });
    }

    public void applyFilters() {
        filterdList = new ArrayList<>();
        if (!games.isEmpty()) {
            for (Game game : games) {
                if (game.getConsole().equals(((MainActivity) getActivity()).consoleString)) {
                    if (((MainActivity) getActivity()).conditonString.equals(all_text)) {
                        filterdList.add(game);
                    } else {
                        if (game.getCondition().equals(((MainActivity) getActivity()).conditonString)) {
                            filterdList.add(game);
                        }
                    }
                }
            }
            List<Fragment> gamesFragments = getActivity().getSupportFragmentManager().getFragments();
            for (Fragment f : gamesFragments) {
                if (f instanceof GamesFragment) {
                    ((GamesFragment) f).gamesAdapter.setGames(filterdList);
                    ((GamesFragment) f).gamesAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
