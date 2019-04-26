package com.example.joao.apppollagot.Interface;

import com.example.joao.apppollagot.Profile;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFireLoadSuccess(List<Profile> listProfiles);
    void onFirebaseLoadFailed(String message);
}
