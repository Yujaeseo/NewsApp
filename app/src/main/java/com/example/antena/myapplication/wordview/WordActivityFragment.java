package com.example.antena.myapplication.wordview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.antena.myapplication.R;
import com.example.antena.myapplication.mainview.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.ERROR;
/**
 * A placeholder fragment containing a simple view.
 */
public class WordActivityFragment extends Fragment {

    private DialogInterface.OnClickListener listener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private View view;

    private WordCardAdapter arrayAdapter;
    private ArrayList <Wordcard> al;
    private int i;
    private  SwipeFlingAdapterView flingContainer;

    private TextToSpeech tts;
    private Toast speakerToast ;
    public WordActivityFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userRef = rootRef.child("users").child(mFirebaseUser.getUid());

        //http://sharp57dev.tistory.com/27

        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR){
                    tts.setLanguage(Locale.US);
                    tts.setPitch(1.0f);
                    tts.setSpeechRate(1.0f);
                }
            }
        });

        listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE: {
                        firebaseGetWord();
                        break;
                    }
                    case DialogInterface.BUTTON_NEGATIVE:{
                        // activity 이동
                        Intent i = new Intent(getActivity(),MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().finish();
                        break;
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.soundtoast,container);
        speakerToast = new Toast(getActivity());
        speakerToast.setGravity(Gravity.CENTER_VERTICAL,0,400);
        speakerToast.setView(layout);
        speakerToast.setDuration(Toast.LENGTH_SHORT);

        return inflater.inflate(R.layout.fragment_word, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        al = new ArrayList<>();
        flingContainer = (SwipeFlingAdapterView)view.findViewById(R.id.frame);
        arrayAdapter = new WordCardAdapter(WordActivityFragment.this.view.getContext(), R.layout.wordcard, al);

        firebaseGetWord();
        // get data


       // arrayAdapter = new WordCardAdapter(WordActivityFragment.this.view.getContext(), R.layout.wordcard, al);
        //flingContainer.setAdapter(arrayAdapter);



        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(final Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                // 왼쪽으로 카드를 넘기는 것 -> 다 외우지 못한 것 -> 외운 횟수를 나타내는 count 변수를 +1 하고 다시 데이터베이스에 저장한다.
                userRef.child("word").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Wordcard currentWordCard = (Wordcard)dataObject;

                        Word newWord = new Word(currentWordCard.getMeaning(),currentWordCard.getCount() + 1);
                        userRef.child("word").child(currentWordCard.getWord()).setValue(newWord);

                        /*
                        for (DataSnapshot data : dataSnapshot.getChildren()){

                            Word word = data.getValue(Word.class);

                            if (data.getKey().equals(currentWordCard.getWord())) {
                                word.setCount(word.getCount() + 1);
                                userRef.child("word").setValue(word).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // ...
                                    }
                                });
                            }
                        }*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    // ,,,
                    }
                });
                //Toast.makeText(getActivity(), "못외움!",Toast.LENGTH_SHORT).show();
            }

            // 외운 단어는 데이터베이스에서 지운다.
            @Override
            public void onRightCardExit(Object dataObject) {

                Toast.makeText(getActivity(), "단어 삭제",Toast.LENGTH_SHORT).show();

                final Wordcard wordcard = (Wordcard) dataObject;

                userRef.child("word").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(wordcard.getWord()).exists()){
                            userRef.child("word").child(wordcard.getWord()).setValue(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                //al.add("XML ".concat(String.valueOf(i)));
                //arrayAdapter.notifyDataSetChanged();

                if (al.size() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("한번 더 외우시겠습니까?").setNegativeButton("아니오",listener).setPositiveButton("네",listener).show();
                }

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                // 음성 읽기
                Wordcard currentWordCard = (Wordcard) dataObject;
                tts.speak((CharSequence)currentWordCard.getWord(), TextToSpeech.QUEUE_FLUSH,null,null);
                speakerToast.show();
                //Toast.makeText(getActivity(), "Clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void firebaseGetWord () {

        userRef.child("word").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot data : dataSnapshot.getChildren()){

                    Word word = data.getValue(Word.class);

                    Log.w("test",word.getMeaning());
                    Log.w("test",data.getKey());
                    Log.w("test",Integer.toString(word.getCount()));

                    al.add(new Wordcard(data.getKey(),word.getMeaning(),word.getCount()));
                }

                //https://www.hellojava.com/article/13209
                Collections.sort(al,new Comparator<Wordcard>(){

                    @Override
                    public int compare(Wordcard o1, Wordcard o2) {
                        if (o1.getCount() == o2.getCount())
                            return 0;
                        return o1.getCount() < o2.getCount() ? -1 : 1;
                    }
                });

                flingContainer.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (tts != null){
            tts.stop();
            tts.shutdown();
            tts=null;
        }
    }
}
