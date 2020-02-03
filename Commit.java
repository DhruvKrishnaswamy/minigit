package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This is the commit class.
 *
 * @author Dhruv Krishnaswamy
 **/
@SuppressWarnings("unchecked")
public class Commit implements Serializable {

    /**
     * This is the commit constructor.
     *
     * @param filelst This is the filelist
     * @param message This is a message of a commit
     * @param par This is the parent
     **/

    Commit(ArrayList<String> filelst, String message, Commit par) {
        _toremove = new ArrayList<>();
        _files = new ArrayList();
        _files = filelst;
        if (message.equals("initial commit")) {
            _time = "Wed Dec 31 16:00:00 1969 -0800";
        } else {
            Date dat = new Date();
            SimpleDateFormat c = new
                    SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
            _time = c.format(dat);

        }
        _mssg = message;
        _parent = par;
        _shabyte = new HashMap<String, byte[]>();
        if (filelst != null) {
            for (String s : filelst) {

                File gen = Utils.join(".gitlet", "staging", s);
                File trackedfiles = Utils.join(".gitlet", "Files", s);
                if (gen.exists()) {
                    byte[] gener = Utils.readContents(gen);
                    _shabyte.put(s, gener);
                } else if (trackedfiles.exists()) {
                    byte[] generpar = Utils.readContents(trackedfiles);
                    _shabyte.put(s, generpar);
                }
            }
        }
        this.sha1 = obtainsha();
    }


    /**
     * Initial commit null constructor.
     **/
    Commit() {
        this(null, "initial commit", null);
    }


    /**
     * @return This obtains the SHA code of a commit.
     **/
    String obtainsha() {
        if (this._mssg.equals("initial commit")) {
            return Utils.sha1(message());
        } else {
            return Utils.sha1(this._mssg);
        }
    }

    /**
     * This empties your list of files to be removed from child.
     **/

    void emptyremovelist() {
        _toremove = new ArrayList<>();
    }

    /**
     * @return This returns the list of files associated with a commit.
     **/
    ArrayList<String> getFilelist() {
        return _files;
    }

    /**
     * @return This returns a Hashmap mapping file names to its sha code.
     **/
    HashMap<String, byte[]> getFiles() {
        return this._shabyte;
    }

    /**
     * Returns the message associated with a commit.
     *
     * @return returns the message as a string
     **/
    String message() {
        return this._mssg;
    }

    /**
     * Returns the timestamp of a given commit.
     *
     * @return the string time
     **/
    String time() {
        return this._time;
    }


    /**
     * Returns the SHA code of the parent of a commit.
     *
     * @return parent commit
     **/
    Commit getPar() {
        return this._parent;
    }

    /**
     * This adds a commit to arraylist of files to be removed.
     * @param file String file name
     **/

    void settoRemove(String file) {
        _toremove.add(file);
    }

    /**
     * This gets the list of files to be removed.
     *
     * @return Arraylist of to be removed files
     **/
    ArrayList<String> gettoRemove() {
        return _toremove;
    }

    /**
     * This is the SHA-1 code of a given commit.
     **/

    private String sha1;


    /**
     * This is the commit message.
     **/

    private String _mssg;


    /**
     * This is the pointer to the parent commit.
     **/

    private Commit _parent;

    /**
     * This is the timestamp of the commit.
     **/
    private String _time;

    /**
     * This is the Hashmap associating the file name to its SHA-code.
     **/

    private HashMap<String, byte[]> _shabyte;

    /**
     * List of files associated with each commit.
     **/

    private ArrayList<String> _files;


    /**
     * This arraylist keeps track of all files that.
     * will need to be removed on the next commit.
     **/
    private ArrayList<String> _toremove;

}
