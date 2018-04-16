package com.notely.pratiksha.model;

/**
 * Created by pratiksha on 4/11/18.
 */

public class Notely {

    private long id;
    private String title;
    private String gist;
    private boolean isFavourite;
    private boolean isStarred;
    private String lastUpdated;

    public Notely(String title, String gist, boolean isFavourite, boolean isStarred, String lastUpdated){
        this.id = id;
        this.title = title;
        this.gist = gist;
        this.isFavourite = isFavourite;
        this.isStarred = isStarred;
        this.lastUpdated = lastUpdated;

    }

    public Notely(){}

    public long getId() {
        return id;
    }

    public Notely setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Notely setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getGist() {
        return gist;
    }

    public Notely setGist(String gist) {
        this.gist = gist;
        return this;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public Notely setFavourite(boolean favourite) {
        isFavourite = favourite;
        return this;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public Notely setStarred(boolean starred) {
        isStarred = starred;
        return this;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public Notely setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }
}
