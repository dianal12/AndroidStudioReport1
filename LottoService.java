package com.example.homework1;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LottoService extends Service {
    private final Messenger serviceMessenger = new Messenger(new IncomingHandler());

    private static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) { // Код 1 - запрос лотерейных номеров
                Messenger clientMessenger = msg.replyTo;
                if (clientMessenger != null) {
                    Message replyMessage = Message.obtain(null, 2); // Код 2 - ответ
                    replyMessage.obj = generateLottoNumbers();
                    try {
                        clientMessenger.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                super.handleMessage(msg);
            }
        }

        private String generateLottoNumbers() {
            Set<Integer> numbers = new HashSet<>();
            Random random = new Random();
            while (numbers.size() < 6) {
                numbers.add(random.nextInt(45) + 1);
            }
            int bonus = random.nextInt(45) + 1;
            return "Lotto Numbers: " + numbers + " | Bonus: " + bonus;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }
}
