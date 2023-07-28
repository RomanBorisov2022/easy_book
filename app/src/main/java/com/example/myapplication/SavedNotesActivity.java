import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.ArrayList;

public class SavedNotesActivity extends AppCompatActivity {

    private LinearLayout layoutSavedNotes;
    private TextView textViewSavedNotes;
    private ScrollView scrollView;
    private ArrayList<String> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_notes);

        layoutSavedNotes = findViewById(R.id.linearLayoutNotes);
        textViewSavedNotes = findViewById(R.id.textViewSavedNotes);
        scrollView = findViewById(R.id.scrollView);

        notesList = getIntent().getStringArrayListExtra("notesList");
        if (notesList != null && !notesList.isEmpty()) {
            updateNotes();
        }
    }

    public void editNote(int position) {
        // Так как вам нужно редактировать заметку, добавим эту функцию
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

    private void updateNotes() {
        // Очистите LinearLayout и TextView с заметками
        layoutSavedNotes.removeAllViews();
        textViewSavedNotes.setText("");

        // Получите данные из SharedPreferences и отобразите их в TextView
        SharedPreferences sharedPreferences = getSharedPreferences("notes", MODE_PRIVATE);
        int noteCount = sharedPreferences.getInt("noteCount", 0);
        StringBuilder notes = new StringBuilder();
        for (String noteData : notesList) {
            notes.append(noteData).append("\n\n");
            // Создайте TextView для каждой заметки и добавьте его в LinearLayout
            TextView textViewNote = new TextView(this);
            textViewNote.setText(noteData);
            textViewNote.setTag(noteCount); // Сохраняем позицию заметки в теге TextView
            textViewNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    editNote(position);
                }
            });
            layoutSavedNotes.addView(textViewNote);
            noteCount++;
        }
        textViewSavedNotes.setText(notes.toString());

        // Обновите прокручиваемую область, чтобы отобразить все заметки
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }
}
