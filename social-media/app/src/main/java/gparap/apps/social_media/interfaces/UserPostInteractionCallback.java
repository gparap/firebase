package gparap.apps.social_media.interfaces;

public interface UserPostInteractionCallback {

    /** Checks if a user has interacted with a post. */
    void onExistingInteraction(boolean isInteractionExisting);
}
