package flickster.jm.com.flickster.models;

/**
 * Created by Jared12 on 3/12/17.
 */

public class Trailer {
    String id;
    String key;
    String name;
    String site;
    String type;

    public Trailer() {
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    int size;
}
