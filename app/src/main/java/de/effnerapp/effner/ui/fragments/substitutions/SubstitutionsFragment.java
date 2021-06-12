package de.effnerapp.effner.ui.fragments.substitutions;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.dsbmobile.DSBClient;
import de.effnerapp.effner.data.dsbmobile.model.AbsentClass;
import de.effnerapp.effner.data.dsbmobile.model.Day;
import de.effnerapp.effner.data.dsbmobile.model.SClass;
import de.effnerapp.effner.data.dsbmobile.model.Substitution;
import de.effnerapp.effner.tools.ClassUtils;
import de.effnerapp.effner.tools.view.IntentHelper;
import de.effnerapp.effner.ui.fragments.substitutions.sections.Badge;
import de.effnerapp.effner.ui.fragments.substitutions.sections.Head;
import de.effnerapp.effner.ui.fragments.substitutions.sections.Item;
import de.effnerapp.effner.ui.fragments.substitutions.sections.ItemAdapter;

public class SubstitutionsFragment extends Fragment {
    private static SubstitutionsFragment instance;

    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    private final List<Head> heads = new ArrayList<>();
    private RecyclerView recyclerView;
    private final List<String> dates = new ArrayList<>();
    private List<Day> days;


    public SubstitutionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_substitutions, container, false);

        recyclerView = view.findViewById(R.id.subs_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        Spinner spinner = view.findViewById(R.id.spinner);
        TextView viewWebDataLink = view.findViewById(R.id.substitutions_view_full_plan);

        if(DSBClient.getInstance() == null) {
            requireActivity().recreate();
            return view;
        }

        // get days from substitutions
        days = DSBClient.getInstance().getDays();

        // hide progressbar container if the data has already been loaded
        if(!days.isEmpty()) {
            RelativeLayout relativeLayout = view.findViewById(R.id.loadingPanel);
            relativeLayout.setVisibility(View.GONE);
        }

        // update the spinner items
        updateSpinnerItems();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dates);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // select the default spinner item
        selectDefaultItem(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> parent, View view, int position, long id) {
                // clear list
                heads.clear();

                int size = 0;

                // set data
                for (Day day : days) {
                    if (day.getDate().equals(parent.getItemAtPosition(position))) {
                        for (SClass sClass : day.getSClasses()) {
                            // get SchoolClass from SharedPreferences
                            String userClass = sharedPreferences.getString("APP_USER_CLASS", "");

                            if (ClassUtils.validateClass(sClass.getName(), userClass)) {
                                ImageView noSubs = container.findViewById(R.id.no_subs_image);
                                noSubs.setVisibility(View.INVISIBLE);

                                for (Substitution substitution : sClass.getSubstitutions()) {
                                    size++;

                                    // set body
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

                                    // set header
                                    Head head = new Head(header.toString(), items, Color.BLACK);

                                    if(!userClass.equals(sClass.getName())) {
                                        head.addBadge(new Badge(0, sClass.getName()));
                                    }

                                    heads.add(head);
                                }
                            }
                        }
                    }
                }

                if (DSBClient.getInstance().getInformation().containsKey(parent.getItemAtPosition(position).toString())) {
                    Item item = new Item(DSBClient.getInstance().getInformation().get(parent.getItemAtPosition(position).toString()));
                    Head head = new Head(getString(R.string.substitutions_head_general_information), Collections.singletonList(item), Color.rgb(0, 150, 136), Collections.singletonList(new Badge(0, getString(R.string.substitutions_badge_school), Color.rgb(255, 93, 82))));
                    heads.add(head);
                    size++;
                }

                if (DSBClient.getInstance().getAbsentClasses().size() > 0) {
                    List<Item> items = new ArrayList<>();
                    for (AbsentClass absentClass : DSBClient.getInstance().getAbsentClasses()) {
                        if (absentClass.getDate().equals(parent.getItemAtPosition(position).toString())) {
                            Item item = new Item(absentClass.getSClass() + ": " + absentClass.getPeriod() + " Stunde");
                            items.add(item);
                        }
                    }
                    if (items.size() > 0) {
                        Head head = new Head(getString(R.string.substitutions_head_absent_classes), items, Color.rgb(255, 93, 82), Collections.singletonList(new Badge(0, getString(R.string.substitutions_badge_school), Color.rgb(255, 93, 82))));
                        heads.add(head);
                        size++;
                    }
                }

                ItemAdapter itemAdapter = new ItemAdapter(heads);
                recyclerView.setAdapter(itemAdapter);

                ImageView noSubs = container.findViewById(R.id.no_subs_image);

                if(noSubs != null) {
                    if (size == 0) {
                        noSubs.setVisibility(View.VISIBLE);
                    } else {
                        noSubs.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        viewWebDataLink.setOnClickListener(v -> IntentHelper.openView(requireContext(), DSBClient.getInstance().getUrl()));

        return view;
    }

    private void updateSpinnerItems() {
        dates.clear();
        dates.addAll(DSBClient.getInstance().getDates());
    }

    private void selectDefaultItem(Spinner spinner) {
        if(!dates.isEmpty()) {
            // select next date if time is after 14:00
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 14 || !dates.get(0).equals(format.format(new Date()))) {
                if(dates.size() >= 2) {
                    spinner.setSelection(1);
                } else {
                    spinner.setSelection(0);
                }
            } else {
                spinner.setSelection(0);
            }
        }
    }

    public void onDataLoadFinished() {
        View root = this.getView();

        // just in case
        if(root == null) return;

        Spinner spinner = root.findViewById(R.id.spinner);

        // update the spinner items
        updateSpinnerItems();

        // notify the adapter that the data set has updated
        ArrayAdapter<?> adapter = ((ArrayAdapter<?>) spinner.getAdapter());
        adapter.notifyDataSetChanged();

        // select the default spinner item
        selectDefaultItem(spinner);

        // hide progressbar container
        RelativeLayout relativeLayout = root.findViewById(R.id.loadingPanel);
        relativeLayout.setVisibility(View.GONE);
    }

    public static SubstitutionsFragment getInstance() {
        return instance;
    }
}