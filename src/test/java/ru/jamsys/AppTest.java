package ru.jamsys;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import ru.jamsys.http.HttpClientNewImpl;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Arrays;

public class AppTest {

    @BeforeAll
    static void beforeAll() {
        String[] args = new String[]{};
        App.context = SpringApplication.run(App.class, args);
    }

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = {MESSAGING_SCOPE};

    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("security/sign2-c83a7-2aebe21240ce.json"))
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refresh();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    @Test
    public void sendNotification2() throws FirebaseMessagingException {
        String registrationToken = "c_9RqGHKT8KWJoE6pvjuxC:APA91bFNS7rhkt60rmk4ScCUTcMuBFko9P06-DJke7bgwt-oZ9nBKRypq4OEekC6Os9SVLc9GS5GJ-3piQlszZenFMg-VdMa-sD_3m4p4_SXGg7wUyj6-D3gBHvKyG8rZ5edRPSThk2l";

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("the day")
                        .setBody("the day.")
                        .build()
                )
                .putData("score", "850")
                .putData("time", "2:45")
                .setToken(registrationToken)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println(response);
    }

    @Test
    public void sendNotification3() throws IOException {
        HttpClientNewImpl x =  new HttpClientNewImpl();
        x.setUrl("https://fcm.googleapis.com/v1/projects/sign2-c83a7/messages:send");
        x.setRequestHeader("Content-type", "application/json");
        x.setRequestHeader("Authorization", "Bearer " + getAccessToken());
        x.setPostData("""
                {
                    "message": {
                      "token": "c_9RqGHKT8KWJoE6pvjuxC:APA91bFNS7rhkt60rmk4ScCUTcMuBFko9P06-DJke7bgwt-oZ9nBKRypq4OEekC6Os9SVLc9GS5GJ-3piQlszZenFMg-VdMa-sD_3m4p4_SXGg7wUyj6-D3gBHvKyG8rZ5edRPSThk2l",
                      "notification": {
                        "body": "This is an FCM notification message!",
                        "title": "FCM Message"
                      }
                   }
                }
                """.getBytes());
        x.exec();
        System.out.println(x);
    }

    public void sendNotification() throws IOException {


        HttpClient client = HttpClientBuilder.create().build();

        HttpPost post = new HttpPost("https://fcm.googleapis.com/v1/projects/sign2-c83a7/messages:send");
        post.setHeader("Content-type", "application/json");
        post.setHeader("Authorization", "Bearer " + getAccessToken());

        JSONObject message = new JSONObject();
        message.put("to", "c_9RqGHKT8KWJoE6pvjuxC:APA91bFNS7rhkt60rmk4ScCUTcMuBFko9P06-DJke7bgwt-oZ9nBKRypq4OEekC6Os9SVLc9GS5GJ-3piQlszZenFMg-VdMa-sD_3m4p4_SXGg7wUyj6-D3gBHvKyG8rZ5edRPSThk2l");
        message.put("priority", "high");

        JSONObject notification = new JSONObject();
        notification.put("title", "Java");
        notification.put("body", "Hello from Java");

        message.put("notification", notification);

        post.setEntity(new StringEntity(message.toString(), "UTF-8"));
        HttpResponse response = client.execute(post);
        System.out.println(response);
        System.out.println(message);
    }
}
