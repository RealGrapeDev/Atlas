package me.charlotte.atlas.utils;

import me.charlotte.atlas.Atlas;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author Charlotte (charlotte@frozengames.cc)
 * 10/4/2021 / 6:55 PM
 * Atlas / me.charlotte.atlas.utils
 */
public enum Locale {

    NO_PERMISSION("MESSAGES.NO-PERMISSION"),
    PROFILE_EXISTS("MESSAGES.PROFILE-EXISTS"),
    PROFILE_CREATED("MESSAGES.PROFILE-CREATED"),
    PROFILE_SELECTED("MESSAGES.PROFILE-SELECTED"),
    PROFILE_ALREADY_SELECTED("MESSAGES.PROFILE-ALREADY-SELECTED"),
    PROFILE_DELETED("MESSAGES.PROFILE-DELETED"),
    PROFILE_DELETE_CANCELLED("MESSAGES.PROFILE-DELETE-CANCELLED"),
    PROFILE_RENAMED("MESSAGES.PROFILE-RENAMED"),
    PROFILE_RENAME_PROCEDURE("MESSAGES.PROFILE-RENAME-PROCEDURE"),
    PROFILE_CANT_DELETE("MESSAGES.PROFILE-CANT-DELETE"),
    PROFILE_LOADED("MESSAGES.PROFILE-LOADED"),
    TOO_MANY_PROFILES("MESSAGES.MAX-PROFILES"),
    NEW_PROFILE_PROCEDURE("MESSAGES.NEW-PROFILE-PROCEDURE");


    private String path;

    private Atlas atlas = Atlas.getPlugin(Atlas.class);

    Locale(String path) {
        this.path = path;
    }

    public String get(Object... objects) {
        return new MessageFormat(Objects.requireNonNull(atlas.getLang().getString(path))).format(objects);
    }

}
