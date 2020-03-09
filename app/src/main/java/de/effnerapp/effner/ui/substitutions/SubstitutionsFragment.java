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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.effnerapp.effner.R;
import de.effnerapp.effner.SplashActivity;
import de.effnerapp.effner.data.dsbmobile.model.Day;
import de.effnerapp.effner.data.dsbmobile.model.SClass;
import de.effnerapp.effner.data.dsbmobile.model.Substitution;
import de.effnerapp.effner.ui.substitutions.sections.Head;
import de.effnerapp.effner.ui.substitutions.sections.Item;
import de.effnerapp.effner.ui.substitutions.sections.ItemAdapter;

public class SubstitutionsFragment extends Fragment {
    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    private List<Head> heads = new ArrayList<>();
    private RecyclerView recyclerView;
    private List<Day> days;


    public SubstitutionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_substitutions, container, false);
        //Wait for DSBMobile
        while (SplashActivity.getSubstitutions() == null || SplashActivity.getSubstitutions().getDays() == null) {
            Log.d("SubstitutionsFragment", "Wait...");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        recyclerView = view.findViewById(R.id.subs_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //Set dates
        String[] dates = {"0", "1"};

        days = SplashActivity.getSubstitutions().getDays();
        int i = 0;
        for (Day day : days) {
            dates[i] = day.getDate();
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
                Log.d("SubstitutionSpinner", "Item: " + parent.getItemAtPosition(position));

                //Clear list
                heads.clear();

                int size = 0;

                //set data
                for (Day day : days) {
                    if (day.getDate().equals(parent.getItemAtPosition(position))) {
                        for (SClass sClass : day.getSClasses()) {

                            //Get SchoolClass from SharedPreferences
                            String userClass = SplashActivity.sharedPreferences.getString("APP_USER_CLASS", "");
                            if (sClass.getName().contains(userClass)) {
                                ImageView noSubs = container.findViewById(R.id.no_subs_image);
                                noSubs.setVisibility(View.INVISIBLE);

                                for (Substitution substitution : sClass.getSubstitutions()) {
                                    size++;
                                    //set body
                                    List<Item> items = new ArrayList<>();
                                    items.add(new Item("Ausfall: " + substitution.getTeacher()));
                                    if (!substitution.getRoom().isEmpty()) {
                                        items.add(new Item("Raum: " + substitution.getRoom()));
                                    }
                                    if (!substitution.getInfo().isEmpty()) {
                                        items.add(new Item("Info: " + substitution.getInfo()));
                                    }

                                    StringBuilder header = new StringBuilder();
                                    header.append(substitution.getPeriod()).append(". Stunde");
                                    if (!substitution.getSubTeacher().isEmpty()) {
                                        header.append(" vertreten durch ").append(substitution.getSubTeacher());
                                    } else {
                                        if (!substitution.getInfo().isEmpty()) {
                                            header.append(": ").append(substitution.getInfo());
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

                if (SplashActivity.getSubstitutions().getInformation().containsKey(parent.getItemAtPosition(position).toString())) {
                    Item item = new Item(SplashActivity.getSubstitutions().getInformation().get(parent.getItemAtPosition(position).toString()));
                    Head head = new Head("Informationen für die ganze Schule", Collections.singletonList(item), Color.rgb(0, 150, 136));

                    heads.add(head);
                    size++;
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
                Log.w("SubstitutionSpinner", "Nothing selected!");
            }
        });

        return view;
    }

}
