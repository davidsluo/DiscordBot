package commands.meme;

import java.sql.Timestamp;

/**
 * Created by David on 9/14/2016.
 */
public class Meme {

    private int       id;
    private String    name;
    private String       link;
    private String    owner;
    private Timestamp timestamp;

    public Meme(int id, String name, String link, String owner, Timestamp timestamp) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.owner = owner;
        this.timestamp = timestamp;
    }

    public Meme(String name, String link, String owner) {
        this.name = name;
        this.link = link;
        this.owner = owner;
    }


    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof Meme &&
                ((Meme) object).getId() == this.id &&
                ((Meme) object).getName().equals(this.name) &&
                ((Meme) object).getLink().equals(this.link) &&
                ((Meme) object).getOwner().equals(this.owner) &&
                ((Meme) object).getTimestamp().equals(this.timestamp)
        );
    }

    @Override
    public String toString() {
        return String.format("**%s** by **%s** on %s\n%s", getName(), getOwner(), getTimestamp(), getLink());
    }
}
