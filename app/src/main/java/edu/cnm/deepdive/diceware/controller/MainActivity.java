package edu.cnm.deepdive.diceware.controller;

import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.snackbar.Snackbar.Callback;
import edu.cnm.deepdive.diceware.R;
import edu.cnm.deepdive.diceware.service.DicewareService;
import edu.cnm.deepdive.diceware.service.GoogleSignInService;
import edu.cnm.deepdive.diceware.view.PassphraseAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });
    RecyclerView passphraseList = findViewById(R.id.keyword_list);
    GoogleSignInService.getInstance().getAccount().observe(this, (account) -> {
      if (account != null) {
        String token = getString(R.string.oauth_header, account.getIdToken());
        Log.d("Oauth2.0 token", token);  //FIXME Remove before Shipping
        DicewareService.getInstance().getAll(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((passphrases) -> {
              PassphraseAdapter adapter = new PassphraseAdapter(this, passphrases,
                  (view, poistion, passphrase) -> {
                Log.d("Passphrase click", passphrase.getKey());
                  });
              passphraseList.setAdapter(adapter);
            });
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);    //makes it display, but not do anything
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled= true;
    switch (item.getItemId()){
      case R.id.action_settings:
        break;
      case R.id.sign_out:
        GoogleSignInService.getInstance().signOut()           //Android creates the instance, not us
            .addOnCompleteListener((task) -> {
              Intent intent  = new Intent(this, LoginActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
            });
        break;
      default: super.onOptionsItemSelected(item);
    }
    return handled;
  }
}
