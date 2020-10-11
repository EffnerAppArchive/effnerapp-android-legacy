package de.effnerapp.effner.ui.activities.timetable;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.effnerapp.effner.R;
import de.effnerapp.effner.data.model.TDay;
import de.effnerapp.effner.data.utils.ApiClient;
import de.effnerapp.effner.json.Status;
import de.effnerapp.effner.services.Authenticator;
import de.effnerapp.effner.ui.models.timetableview.Schedule;
import de.effnerapp.effner.ui.models.timetableview.Time;
import de.effnerapp.effner.ui.models.timetableview.TimetableView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;


public class TimetableActivity extends AppCompatActivity {
    private final int APP_CAMERA_PERMISSION_REQUEST_CODE = 10;
    private final int APP_CAMERA_PICTURE_ID = 11;
    private final Gson gson = new Gson();

    private TimetableView timetable;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());
        if (sharedPreferences.getBoolean("APP_DESIGN_DARK", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        timetable = findViewById(R.id.timetable);
        TDay[] timetableDays = new Gson().fromJson(ApiClient.getInstance().getData().getTimetable().getValue(), TDay[].class);
        if (timetableDays != null && timetableDays.length != 0 && !isEmpty(timetableDays)) {
            Log.d("TA", "Generating timetable!");

            ArrayList<Schedule> schedules = new ArrayList<>();

            for (TDay day : timetableDays) {
                int dayI = day.getId() - 1;
                for (int i = 1; i <= 10; i++) {
                    String text = null;
                    try {
                        text = Objects.requireNonNull(day.getClass().getField("s" + i).get(day)).toString();
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    assert text != null;
                    if (!text.equals("-")) {
                        Schedule schedule = new Schedule();
                        schedule.setSubject(text);
                        schedule.setDay(dayI);
                        schedule.setStartTime(new Time(i, 0));
                        schedule.setEndTime(new Time(i + 1, 0));
                        schedules.add(schedule);
                    }
                }
            }
            timetable.add(schedules);
            if (sharedPreferences.contains("APP_TIMETABLE_COLOR")) {
                backButton.getBackground().setColorFilter(sharedPreferences.getInt("APP_TIMETABLE_COLOR", -14200620), PorterDuff.Mode.SRC_ATOP);
                backButton.setTextColor(sharedPreferences.getInt("APP_TIMETABLE_TEXTCOLOR", Color.WHITE));
                for (int i = 0; i < timetable.getStickerViewSize(); i++) {
                    timetable.setColor(i, sharedPreferences.getInt("APP_TIMETABLE_COLOR", -14200620));
                }
                timetable.setTextColor(sharedPreferences.getInt("APP_TIMETABLE_TEXTCOLOR", Color.WHITE));
            }
        } else {
            Log.d("TA", "Timetable empty!");
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Kein Stundenplan!")
                    .setMessage("Für deine Klasse wurde noch kein Stundenplan eingereicht!\nMöchtest du einen Stundenplan hochlanden?")
                    .setCancelable(false)
                    .setPositiveButton("Stundenplan hochladen", (dialogInterface, i) -> uploadTimetable())
                    .setNegativeButton("Abbrechen", (dialogInterface, i) -> finish());
            dialog.show();
        }

    }

    private void uploadTimetable() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, APP_CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, APP_CAMERA_PICTURE_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == APP_CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, APP_CAMERA_PICTURE_ID);
            } else {
                Toast.makeText(this, "Um diese Funktion zu nutzen, musst du der App Berechtigungen auf die Kamera geben.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_CAMERA_PICTURE_ID && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            AccountManager accountManager = AccountManager.get(this);
            String token = accountManager.getPassword(accountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE)[0]);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] bitmapData = stream.toByteArray();

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addPart(Headers.of("Content-Disposition", "form-data;"), RequestBody.create(bitmapData, MediaType.parse("image/png")))
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.effnerapp.de:45890/data/timetable/upload?token=" + token)
                    .post(requestBody)
                    .build();

            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Lade hoch...");
            dialog.setMessage("Der Stundenplan wird hochgeladen...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Status status = gson.fromJson(Objects.requireNonNull(response.body()).string(), Status.class);
                    String message = status.getMsg();
                    if (message.equals("UPLOAD_SUCCESS")) {
                        runOnUiThread(() -> {
                            Toast.makeText(TimetableActivity.this, "Stundenplan erfolgreich hochgeladen", Toast.LENGTH_SHORT).show();
                            dialog.hide();
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(TimetableActivity.this, "Fehler beim Hochladen", Toast.LENGTH_SHORT).show();
                            dialog.hide();
                            finish();
                        });
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(TimetableActivity.this, "Fehler beim Hochladen", Toast.LENGTH_SHORT).show();
                        dialog.hide();
                        finish();
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timetable_nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int id = item.getItemId();

        if (id == R.id.navigation_timetable_color) {
            ColorPickerDialog.Builder dialog = new ColorPickerDialog.Builder(this, THEME_DEVICE_DEFAULT_DARK)
                    .setTitle("Wähle eine Farbe aus!")
                    .setNegativeButton("Abbrechen", null)
                    .setPositiveButton("OK", (ColorEnvelopeListener) (envelope, fromUser) -> {
                        int textColor = getTextColor(envelope.getColor());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("APP_TIMETABLE_COLOR", envelope.getColor());
                        editor.putInt("APP_TIMETABLE_TEXTCOLOR", textColor);
                        editor.apply();
                        backButton.getBackground().setColorFilter(envelope.getColor(), PorterDuff.Mode.SRC_ATOP);
                        backButton.setTextColor(textColor);
                        for (int i = 0; i < timetable.getStickerViewSize(); i++) {
                            timetable.setColor(i, envelope.getColor());
                        }
                        timetable.setTextColor(textColor);
                    });
            dialog.show();
        } else if (id == R.id.navigation_timetable_settings_reset) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("APP_TIMETABLE_COLOR").apply();
            timetable.updateStickerColor();
            backButton.getBackground().setColorFilter(-14200620, PorterDuff.Mode.SRC_ATOP);
            backButton.setTextColor(Color.WHITE);
            timetable.setTextColor(Color.WHITE);
            Toast.makeText(this, "Einstellungen zurückgesetzt!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.navigation_timetable_upload_timetable) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("Neuer Stundenplan!")
                    .setMessage("Du kannst einen neuen Stundenplan hochladen, wenn es Änderungen oder Fehler in der aktuellen Version gibt!")
                    .setPositiveButton("Stundenplan hochladen", (dialogInterface, i) -> uploadTimetable())
                    .setNegativeButton("Abbrechen", null);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private int getTextColor(int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        double brightness = (0.21 * red) + (0.72 * green) + (0.07 * blue);
        if (brightness >= 0.9) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }

    private boolean isEmpty(TDay[] timetable) {
        for (TDay day : timetable) {
            for (int i = 1; i <= 10; i++) {
                String text = null;
                try {
                    text = Objects.requireNonNull(day.getClass().getField("s" + i).get(day)).toString();
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (text != null && !text.isEmpty() && !text.equals("-")) {
                    return false;
                }
            }
        }
        return true;
    }
}
