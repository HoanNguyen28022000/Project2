package com.example.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAaphvw7Y:APA91bHYORk_yW3Wpsk5k4VLCDdEio_avKeI44VpvCdKhgxQlmSq1-6aNA9yh-LcvUUFGDg8i1jM50y4Uh421HbMx8bGRQWbNMMtrWDtNHM_b7V2gLXU2UbCRqj8EtNr89Yq3Sax24ny" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender sender);
}

