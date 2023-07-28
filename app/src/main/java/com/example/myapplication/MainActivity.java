package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> notesList;
    private EditText editTextNote;
    private TextView textViewNotes;

    private EditText editTextBrand;
    private EditText editTextModel;
    private EditText editTextMileage;
    private ImageView imageViewCarPhoto;
    private TextView textViewPhotoPath;

    private Uri selectedImageUri;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Найдите ImageView по его идентификатору
        imageViewCarPhoto = findViewById(R.id.imageViewCarPhoto);
        textViewPhotoPath = findViewById(R.id.textViewPhotoPath);
        notesList = new ArrayList<>();
        editTextNote = findViewById(R.id.editTextNote);
        editTextBrand = findViewById(R.id.editTextBrand);
        editTextModel = findViewById(R.id.editTextModel);
        editTextMileage = findViewById(R.id.editTextMileage);

        textViewNotes = findViewById(R.id.textViewNotes);

        Button buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);
        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            selectedImageUri = data.getData();
            imageViewCarPhoto.setImageURI(selectedImageUri);

            // Получите путь к выбранному изображению
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                photoPath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                photoPath = null;
            }

            // Обновите TextView с путем к фото
            if (photoPath != null) {
                textViewPhotoPath.setText("Путь к фото: " + photoPath);
            } else {
                textViewPhotoPath.setText("Путь к фото: ");
            }
        }
    }

    public void saveNote(View view) {
        // Получите текст из EditText для марки, модели, пробега и заметки
        String brand = editTextBrand.getText().toString();
        String model = editTextModel.getText().toString();
        String mileage = editTextMileage.getText().toString();
        String note = editTextNote.getText().toString();

        // Создайте новый SharedPreferences для сохранения заметок
        SharedPreferences sharedPreferences = getSharedPreferences("notes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Получите текущее количество сохраненных заметок
        int noteCount = sharedPreferences.getInt("noteCount", 0);

        // Создайте строку с данными о заметке
        String noteData = "Марка: " + brand + "\nМодель: " + model + "\nПробег: " + mileage;

        // Сохраните данные заметки в SharedPreferences
        editor.putString("note" + noteCount, noteData);

        // Увеличьте количество сохраненных заметок на 1
        editor.putInt("noteCount", noteCount + 1);

        // Примените изменения
        editor.apply();

        // Очистите EditText для марки, модели, пробега и заметки
        editTextNote.getText().clear();
        editTextBrand.getText().clear();
        editTextModel.getText().clear();
        editTextMileage.getText().clear();

        // Добавьте новую заметку в notesList
        notesList.add(noteData);

        // Обновите список заметок
        updateNotes();
    }



    private void updateNotes() {
        StringBuilder notes = new StringBuilder();
        for (String note : notesList) {
            notes.append(note).append("\n\n");
        }
        textViewNotes.setText(notes.toString());
    }

    public void openSavedNotesActivity(View view) {
        Intent intent = new Intent(this, com.example.myapplication.SavedNotesActivity.class);
        startActivity(intent);
    }

    public void editNote(int position) {
        // Получите данные из SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("notes", MODE_PRIVATE);
        int noteCount = sharedPreferences.getInt("noteCount", 0);

        // Проверьте, что указанная позиция меньше, чем количество сохраненных заметок
        if (position >= 0 && position < noteCount) {
            // Получите текущие данные о заметке
            String noteData = sharedPreferences.getString("note" + position, "");

            // Создайте диалоговое окно для редактирования заметки
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Редактировать заметку");

            // Создайте EditText внутри диалогового окна
            final EditText editText = new EditText(this);
            editText.setText(noteData);
            builder.setView(editText);

            // Добавьте кнопки "Сохранить" и "Отмена" для диалогового окна
            builder.setPositiveButton("Сохранить", (dialog, which) -> {
                // Получите текст из EditText
                String editedNoteData = editText.getText().toString();

                // Обновите данные заметки в SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("note" + position, editedNoteData);
                editor.apply();

                // Обновите список заметок
                updateNotes();
            });

            builder.setNegativeButton("Отмена", (dialog, which) -> {
                // Ничего не делаем, просто закрываем диалоговое окно
            });

            // Отобразите диалоговое окно
            builder.show();
        }
    }

}
