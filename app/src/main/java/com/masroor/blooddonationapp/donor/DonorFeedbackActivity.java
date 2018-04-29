package com.masroor.blooddonationapp.donor;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.masroor.blooddonationapp.R;

import java.util.ArrayList;
import java.util.Locale;


public class DonorFeedbackActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SPEECH_INPUT=222;

    Spinner spinner_subject;
    EditText editText_message;
    Button btnProceed;
    ImageView mic;
    String mail_subject, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_feedback);
        final String[] subject={"General","Report an Issue","Feature Request","Other"};

        referViewElements();

        ArrayAdapter<String> adpt=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,subject);
        spinner_subject.setAdapter(adpt);

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    //proceed
//                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//                    emailIntent.setData(Uri.parse("mailto:blooddonation@gmail.com"));
//                    emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
//                    emailIntent.putExtra(Intent.EXTRA_TEXT,message);
                        composeEmail(new String[]{"blooddonation@gmail.com"},mail_subject,message);
                }
            }
        });
    }

    public void composeEmail(String[] addresses, String subject,String mail_body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Please speak your message");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                   "Speech not supported.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText_message.setText(result.get(0));
                    message=editText_message.getText().toString();
                }
                break;
            }
        }
    }


    public boolean validate(){
        if(TextUtils.isEmpty(spinner_subject.getSelectedItem().toString())){
            Toast.makeText(this,"Specifiy Subject in Drop Down List.",Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            mail_subject=spinner_subject.getSelectedItem().toString();
        }
        if(TextUtils.isEmpty(editText_message.getText())){
            editText_message.setError("Message cannot be empty");
            return false;
        }else{
            editText_message.setError(null);
        }
        return true;
    }

    private void referViewElements() {
        spinner_subject=findViewById(R.id.spinner_subject);
        editText_message=findViewById(R.id.edittext_feedback_message);
        btnProceed=findViewById(R.id.button_proceed);
        mic=findViewById(R.id.imageview_mic);
    }

}
