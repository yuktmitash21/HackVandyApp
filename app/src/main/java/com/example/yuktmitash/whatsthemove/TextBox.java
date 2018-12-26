package com.example.yuktmitash.whatsthemove;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TextBox extends AppCompatActivity {
    private TextView textView;
    private EditText editText;
    private Button button;
    private static final int maxChar = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_box);

        textView = findViewById(R.id.charCount);
        editText = findViewById(R.id.commentText);
        button = findViewById(R.id.updateComments);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comm = editText.getText().toString();

                if (comm.equals("")) {
                    Toast.makeText(TextBox.this, "Please enter some comments", Toast.LENGTH_SHORT).show();
                } else {
                    String id = getIntent().getStringExtra("id");
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("comments").child(id).setValue(comm);
                    Toast.makeText(TextBox.this, "Text Uploaded!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Comments.class);
                    intent.putExtra("partyId", id);
                    String name = getIntent().getStringExtra("name");
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        });

        textView.setText("Characters Remaining: " + maxChar);

        TextWatcher mTextEditorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int count = maxChar - charSequence.length();
                textView.setText("Characters Remaining: " + count);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editText.addTextChangedListener(mTextEditorWatcher);

    }
}
