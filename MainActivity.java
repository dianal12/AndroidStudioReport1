package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.*;
import android.os.*;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Messenger serviceMessenger = null;
    private boolean isBound = false;
    private TextView lottoTextView;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
            isBound = false;
        }
    };

    private final Handler responseHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) { // Код 2 - получение результата
                String lottoNumbers = (String) msg.obj;
                lottoTextView.setText(lottoNumbers);
            }
        }
    };

    private final Messenger clientMessenger = new Messenger(responseHandler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lottoTextView = findViewById(R.id.lottoTextView);

        Intent intent = new Intent(this, LottoService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    public void requestLottoNumbers(View view) {
        if (isBound) {
            Message msg = Message.obtain(null, 1); // Код 1 - запрос номеров
            msg.replyTo = clientMessenger;
            try {
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Service not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
