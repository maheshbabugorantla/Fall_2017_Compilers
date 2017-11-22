import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Mahesh Babu Gorantla (mgorantl) & Rahul Patni (rpatni) on Nov 21, 2017.
 */
public class GenKillNode {

    private int index;
    private HashSet<String> GenSet;
    private HashSet<String> KillSet;

    public GenKillNode(int index) {
        this.index = index;
        this.GenSet = new HashSet<String>();
        this.KillSet = new HashSet<String>();
    }

    public void addToGenSet(String str) {
        GenSet.add(str);
    }

    public void removeFromGenSet(String str) {
        if (GenSet.contains(str)) {
            GenSet.remove(str);
        }
    }

    public void addToKillSet(String str) {
        KillSet.add(str);
    }

    public void removeFromKillSet(String str) {
        if (KillSet.contains(str)) {
            KillSet.remove(str);
        }
    }

    @Override
    public String toString() {
        return index + " Gen: " + getHashSetString(GenSet).trim() + " Kill: " + getHashSetString(KillSet).trim();
    }

    public void printGenSet() {
        System.out.println(getHashSetString(GenSet).trim());
    }

    public void printKillSet() {
        System.out.println(getHashSetString(KillSet).trim());
    }

    private String getHashSetString(HashSet<String> set) {
        String ret = "";
        for (String val : set) {
            ret += val + ", ";
        }
        return ret;
    }
}
