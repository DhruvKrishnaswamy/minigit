package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the Gtree class.
 *
 * @author Dhruv Krishnaswamy
 **/
@SuppressWarnings("unchecked")
public class Gtree implements Serializable {

    /**
     * This is the Gtree constructor.
     * @param comm commit
     **/
    Gtree(Commit comm) {
        if (comm == null) {
            _head = null;
        } else {
            _head = comm;
        }
        _currentbranch = "master";
        _branches = new HashMap<String, Commit>();
        _commitlist = new ArrayList<Commit>();

    }

    /**
     * This is the null constructor for a new Gtree.
     **/

    Gtree() {
        this(null);
    }

    /**
     * This gives you the head commit of the tree.
     * @return head commit
     **/

    Commit gethead() {
        return _head;
    }

    /**
     * This sets the new head commit.
     * @param c -  commit
     **/

    void sethead(Commit c) {
        _head = c;
    }

    /**
     * This method adds a branch to the gtree.
     * @param b branch
     **/
    void addBranch(String b) {
        _branches.put(b, _head);
    }

    /**
     * This commit adds a commit to the commit list.
     * @param c - commit
     **/
    void addCommit(Commit c) {
        _commitlist.add(c);
        sethead(c);
    }

    /**
     * This method ensures that your branches are set in order.
     * @param t -  gtree
     **/

    static void updatebz(Gtree t) {
        if (t._branches.containsKey(t._currentbranch)) {
            t._branches.remove(t._currentbranch);
            t._branches.put(t._currentbranch, t.gethead());
        }
    }

    /**
     * Updates the current branch of the tree.
     * @param b - branch
     **/
    void setbranch(String b) {
        if (_branches.containsKey(b)) {
            _branches.remove(b);
        }
        _branches.put(b, gethead());
        _currentbranch = b;
    }


    /**
     * Returns the current branch.
     * @return current branch
     **/
    String getcurrentbranch() {
        return _currentbranch;
    }

    /**
     * Deletes the branch s from the hashmap.
     * @param s branch name
     **/
    void deleteBranch(String s) {
        _branches.remove(s);
    }

    /**
     * Returns the branches of the Gtree.
     * @return hashmap mapping branch string to commit
     **/
    HashMap<String, Commit> obtainbranches() {
        return _branches;
    }


    /**
     * Returns all the commits ever made.
     * @return Arraylist of all commits
     **/
    ArrayList<Commit> allcommits() {
        if (_commitlist.size() > 0) {
            return _commitlist;
        } else {
            return new ArrayList<Commit>();
        }
    }

    /**
     * This is a hashmap mapping branch names to commit SHA codes.
     **/
    private HashMap<String, Commit> _branches;

    /**
     * This is an arraylist of commits.
     **/
    private ArrayList<Commit> _commitlist;

    /**
     * This is the current commit.
     **/
    private Commit _head;

    /**
     * This is the current branch that the user is on.
     **/
    private String _currentbranch;
}





