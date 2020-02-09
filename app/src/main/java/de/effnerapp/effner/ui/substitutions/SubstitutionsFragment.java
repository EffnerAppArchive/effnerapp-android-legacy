package de.effnerapp.effner.ui.substitutions;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.effnerapp.effner.MainActivity;
import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.data.dsbmobile.model.Klasse;
import de.effnerapp.effner.data.dsbmobile.model.Tag;
import de.effnerapp.effner.data.dsbmobile.model.Vertretung;
import de.effnerapp.effner.ui.substitutions.sections.Head;
import de.effnerapp.effner.ui.substitutions.sections.Item;
import de.effnerapp.effner.ui.substitutions.sections.ItemAdapter;

public class SubstitutionsFragment extends Fragment {
    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    private List<Head> heads = new ArrayList<>();
    private RecyclerView recyclerView;
    private List<Tag> table;


    public SubstitutionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_substitutions, container, false);
        MainActivity.pageTextView.setText(R.string.title_substitutions);
        //Wait for DSBMobile
        while (SplashActivity.getVertretungen() == null || SplashActivity.getVertretungen().getTable() == null) {
            Log.d("VerFrag", "Wait...");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //Set dates
        String[] dates = {"today", "tomorrow"};

        table = SplashActivity.getVertretungen().getTable();
        int i = 0;
        for (Tag tag : table) {
            dates[i] = tag.getDatum();
            i++;
        }

        // Spinner
        Spinner spinner = view.findViewById(R.id.spinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, dates);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        int a = 0;
        for (String date : dates) {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 13) {
                if (date.equals(format.format(new Date()))) {
                    spinner.setSelection(a);
                    break;
                }
            } else {
                if (date.equals(format.format(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24))))) {
                    spinner.setSelection(a);
                    break;
                }
            }
            a++;
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> parent, View view, int position, long id) {
                Log.d("VertretungsSpinner", "Item: " + parent.getItemAtPosition(position));

                //Clear list
                heads.clear();

                int size = 0;

                //set data
                for (Tag tag : table) {
                    if (tag.getDatum().equals(parent.getItemAtPosition(position))) {
                        for (Klasse klasse : tag.getKlassen()) {

                            //Get Klasse from SharedPreferences
                            String persoKlasse = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");
                            if (klasse.getName().contains(persoKlasse)) {
                                ImageView noSubs = container.findViewById(R.id.no_subs_image);
                                noSubs.setVisibility(View.INVISIBLE);

                                for (Vertretung vertretung : klasse.getVertretungen()) {
                                    size++;
                                    //set body
                                    List<Item> items = new ArrayList<>();
                                    items.add(new Item("Ausfall: " + vertretung.getLehrkraft()));
                                    if(!vertretung.getRaum().equals("")) {
                                        items.add(new Item("Raum: " + vertretung.getRaum()));
                                    }
                                    if (!vertretung.getInfo().equals("")) {
                                        items.add(new Item("Info: " + vertretung.getInfo()));
                                    }

                                    StringBuilder header = new StringBuilder();
                                    header.append(vertretung.getStunde()).append(". Stunde");
                                    if (!vertretung.getVertretung().equals("")) {
                                        header.append(" vertreten durch ").append(vertretung.getVertretung());
                                    } else {
                                        if(!vertretung.getInfo().equals("")) {
                                            header.append(": ").append(vertretung.getInfo());
                                        } else {
                                            header.append(": keine Info");
                                        }
                                    }
                                    //set header
                                    Head head = new Head(header.toString(), items, Color.BLACK);

                                    heads.add(head);
                                }
                            }
                        }
                    }
                }

                if(SplashActivity.getVertretungen().getMainInformation().size() >= position + 1 && SplashActivity.getVertretungen().getMainInformation().get(position).size() > 0) {
                    size++;
                    List<Item> items = new ArrayList<>();
                    StringBuilder sb = new StringBuilder();
                    for(String info : SplashActivity.getVertretungen().getMainInformation().get(position)) {
                        sb.append(info).append("\n");
                    }
                    items.add(new Item(sb.toString()));
                    Head head = new Head("Informationen für die ganze Schule", items, Color.rgb(0, 150, 136));

                    heads.add(head);
                }

                ImageView noSubs = container.findViewById(R.id.no_subs_image);
                if (size == 0) {
                    noSubs.setVisibility(View.VISIBLE);
                } else {
                    noSubs.setVisibility(View.INVISIBLE);
                }

                ItemAdapter itemAdapter = new ItemAdapter(heads);
                recyclerView.setAdapter(itemAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Useless?
                Log.w("VertretungsSpinner", "Item: No Item 404");
            }
        });

        return view;
    }

}
