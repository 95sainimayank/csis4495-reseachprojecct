package com.example.mixbox.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mixbox.databinding.FragmentUpload2Binding;
import com.example.mixbox.model.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class UploadFragment extends Fragment {
   FragmentUpload2Binding binding;
   Uri songAudioUri;
   Uri songImageUri;
   StorageReference storage;
   FirebaseDatabase db;
   FirebaseAuth auth;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      binding = FragmentUpload2Binding.inflate(inflater, container, false);
      binding.progressB.setVisibility(View.INVISIBLE);

      db = FirebaseDatabase.getInstance();
      auth = FirebaseAuth.getInstance();

      //select song file
      binding.btnSelectFile.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            //Open the dialog
            Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            data.setType("audio/*");
            data = Intent.createChooser(data, "Choose a file: ");
            resultLauncher.launch(data);
         }

      });

      //Select image for the song
      binding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            //Open the dialog
            Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            data.setType("image/*");
            data = Intent.createChooser(data, "Choose a file: ");
            resultLauncher.launch(data);
         }
      });

      //uploading files
      binding.btnUpload.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            ArrayList<String> songCategories = new ArrayList<>();
            if(binding.rnbCheck.isChecked()) songCategories.add("rnb");
            if(binding.rockCheck.isChecked()) songCategories.add("rock");
            if(binding.edmCheck.isChecked()) songCategories.add("edm");
            String[] songNameSplit = songAudioUri.getLastPathSegment().split("/");

           // String[] audioNameSplit = songNameSplit[songNameSplit.length - 1].split("\\.");
            updateInDb(songNameSplit[songNameSplit.length - 1], songCategories);

            if(songAudioUri == null)
               Toast.makeText(getActivity(), "Please choose a audio file!", Toast.LENGTH_SHORT).show();
            else if (songImageUri == null)
               Toast.makeText(getActivity(), "Please choose a image file!", Toast.LENGTH_SHORT).show();
            else if (!binding.edmCheck.isChecked() && !binding.rnbCheck.isChecked()  && !binding.rockCheck.isChecked())
               Toast.makeText(getActivity(), "Please select at least one category!", Toast.LENGTH_SHORT).show();
            else{
               disableViews();

               storage = FirebaseStorage.getInstance().getReference();
//               String[] songNameSplit = songAudioUri.getLastPathSegment().split("/");
               Toast.makeText(getActivity(), "Audio file upload started.", Toast.LENGTH_SHORT).show();

               storage.child("music").child(songNameSplit[songNameSplit.length - 1]).putFile(songAudioUri)
                 .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     Toast.makeText(getActivity(), "Audio uploaded successfully!", Toast.LENGTH_SHORT).show();
                     binding.progressB.setVisibility(View.INVISIBLE);

                     String[] audioNameSplit = songNameSplit[songNameSplit.length - 1].split("\\.");
                     String audioName = audioNameSplit[0];
                     String[] imageNameSplit = getFileName(songImageUri).split("\\.");
                     String imageName = audioName + "." + imageNameSplit[imageNameSplit.length - 1];

                     ArrayList<String> songCategories = new ArrayList<>();
                     if(binding.rnbCheck.isChecked()) songCategories.add("rnb");
                     if(binding.rockCheck.isChecked()) songCategories.add("rock");
                     if(binding.edmCheck.isChecked()) songCategories.add("edm");

                     uploadImage(imageName);

                  }
               }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                     Log.e("Audio upload failed", e.getMessage());
                     Toast.makeText(getActivity(), "Failed to upload file. Please try again later!", Toast.LENGTH_SHORT).show();
                     enableViews();
                  }
               }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                     binding.progressB.setVisibility(View.VISIBLE);
                  }
               });


            }
         }
      });

      return binding.getRoot();
   }


   private void uploadImage(String imageName) {
      Toast.makeText(getActivity(), "Image upload started", Toast.LENGTH_SHORT).show();

      storage.child("albumcover").child(imageName).putFile(songImageUri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              binding.progressB.setVisibility(View.INVISIBLE);
              Toast.makeText(getActivity(), "Upload successfully completed", Toast.LENGTH_SHORT).show();

              enableViews();
           }
        })
        .addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
              Toast.makeText(getActivity(), "Image failed to upload", Toast.LENGTH_SHORT).show();
              Log.e("Image upload failed", e.getMessage());
              enableViews();
           }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
              binding.progressB.setVisibility(View.VISIBLE);
           }
        });

   }

   public ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
     new ActivityResultContracts.StartActivityForResult(),
     new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
           if (result.getResultCode() == Activity.RESULT_OK) {
              Intent data = result.getData();

              Uri uri = data.getData();
              String fileType = getMimeType(uri);

              if(fileType.equals("audio"))
                 songAudioUri = uri;
              else if(fileType.equals("image"))
                 songImageUri = uri;
              else
                 Toast.makeText(getActivity(), "Choose correct file!", Toast.LENGTH_SHORT).show();
           }
        }
     }
   );

   public String getMimeType(Uri uri) {
      String mimeType = null;
      if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
         ContentResolver cr = getActivity().getContentResolver();
         mimeType = cr.getType(uri);
      } else {
         String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
         mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
      }

      if(mimeType.contains("audio"))
         return "audio";
      else if (mimeType.contains("image"))
         return "image";
      else
         return "incorrect";

   }

   public String getFileName(Uri uri) {
      String result = null;
      if (uri.getScheme().equals("content")) {
         Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
         try {
            if (cursor != null && cursor.moveToFirst()) {
               result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
         } finally {
            cursor.close();
         }
      }
      if (result == null) {
         result = uri.getPath();
         int cut = result.lastIndexOf('/');
         if (cut != -1) {
            result = result.substring(cut + 1);
         }
      }
      return result;
   }

   public void disableViews(){
      binding.btnUpload.setEnabled(false);
      binding.btnSelectImage.setEnabled(false);
      binding.btnSelectFile.setEnabled(false);
      binding.rockCheck.setEnabled(false);
      binding.edmCheck.setEnabled(false);
      binding.rnbCheck.setEnabled(false);
   }

   public void enableViews(){
      binding.btnUpload.setEnabled(true);
      binding.btnSelectImage.setEnabled(true);
      binding.btnSelectFile.setEnabled(true);
      binding.rockCheck.setEnabled(true);
      binding.edmCheck.setEnabled(true);
      binding.rnbCheck.setEnabled(true);
   }

   //working
   private void updateInDb(String s, ArrayList<String> inputGenres) {
      db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (task.isSuccessful()) {
               DataSnapshot snapshot = task.getResult();
               HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
               HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

               boolean boo = false;

               for (Object value : allUsers.values()) {
                  HashMap<String, Object> eachUser = (HashMap<String, Object>) value;

                  HashMap<String, Object> eachUserSongs = (HashMap<String, Object>) eachUser.get("songs");

                  if (eachUserSongs != null) {
                     for (Object song : eachUserSongs.values()) {
                        HashMap<String, Object> eachSong = (HashMap<String, Object>) song;

                        if(eachSong.get("songName").equals(s)){
                           boo = true;
                        }
                     }
                  }
               }

               if(boo){
                  Toast.makeText(getActivity(), "Song name exists already! Please change file name!", Toast.LENGTH_SHORT).show();
               }
               else{
                  db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                           DataSnapshot snapshot = task.getResult();
                           HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
                           HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

                           Object[] allUsersKeys = allUsers.keySet().toArray();

                           for(Object eachUserKey : allUsersKeys){
                              HashMap<String, Object> eachUser = (HashMap<String, Object>) allUsers.get(eachUserKey);

                              if(eachUser.get("email").equals(auth.getCurrentUser().getEmail())){
                                 Song song = new Song(s, 0, LocalDateTime.now(), null);

                                 db.getReference().child("allUsers").child(eachUserKey.toString()).child("songs").push().setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                          Log.e("---", "Song Added successfully");
                                       }
                                       else{
                                          Log.e("---", "Failed to add song");
                                       }
                                    }
                                 });

                                 db.getReference().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                       if (task.isSuccessful()) {
                                          DataSnapshot snapshot = task.getResult();
                                          HashMap<String, Object> outerMap = (HashMap<String, Object>) snapshot.getValue();
                                          HashMap<String, Object> allUsers = (HashMap<String, Object>) outerMap.get("allUsers");

                                          for (Object value : allUsers.values()) {
                                             HashMap<String, Object> eachUser = (HashMap<String, Object>) value;

                                             HashMap<String, Object> eachUserSongs = (HashMap<String, Object>) eachUser.get("songs");

                                             if (eachUserSongs != null) {
                                                for (Object song : eachUserSongs.values()) {
                                                   HashMap<String, Object> eachSong = (HashMap<String, Object>) song;

                                                   if(eachSong.get("songName").equals(s)){

                                                      //genres
                                                      HashMap<String, Object> genres = new HashMap<>();

                                                      for(String g : inputGenres){
                                                         genres.put(UUID.randomUUID().toString(), g);
                                                      }

                                                      eachSong.put("genre", genres);

                                                      db.getReference().child("allUsers").updateChildren(allUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                               Toast.makeText(getActivity(), "Successfully Updated genres", Toast.LENGTH_SHORT).show();
                                                               Log.e("genre", "succc");
                                                            } else {
                                                               Toast.makeText(getActivity(), "failed to update Genres", Toast.LENGTH_SHORT).show();
                                                               Log.e("--", task.getException().getMessage());
                                                            }
                                                         }
                                                      });

                                                      break;
                                                   }

                                                }
                                             }
                                          }
                                       } else {
                                          Log.e("---", task.getException().toString());
                                       }
                                    }
                                 });

                                 break;
                              }

                           }

                        }
                     }
                  });
               }

            } else {
               Log.e("---", task.getException().toString());
            }
         }
      });

      clearValuesAfterUpload();

   }

   public void clearValuesAfterUpload(){
      binding.edmCheck.setChecked(false);
      binding.rockCheck.setChecked(false);
      binding.rnbCheck.setChecked(false);

      songAudioUri = null;
      songImageUri = null;
   }
}











































