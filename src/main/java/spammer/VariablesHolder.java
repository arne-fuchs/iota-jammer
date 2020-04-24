package spammer;

import org.iota.jota.utils.SeedRandomGenerator;

public abstract class VariablesHolder {
    protected boolean DEBUG_MODE = false;
    protected boolean nodeListEnabled = false;
    protected boolean jsonListEnabled = false;
    protected boolean localPOW = false;
    protected int threadAmount = 1;
    protected long delay = 0;

    protected String seed = null;
    protected String address = null;
    protected String tag = null;
    protected String message = null;

    protected int reconnect = 0;
    protected int mwm = 0;
    protected int depth = 0;

    /**
     * Just getter for the variables with default variables if the variable was not specified
     */
    public String getSeed() {
        return seed == null ? SeedRandomGenerator.generateNewSeed() : seed;
    }

    public String getAddress() {
        return address == null ? "9FNJWLMBECSQDKHQAGDHDPXBMZFMQIMAFAUIQTDECJVGKJBKHLEBVU9TWCTPRJGYORFDSYENIQKBVSYKW9NSLGS9UW" : address;
    }

    public String getTag() {
        return tag == null ? "IOTAJAMMER" : tag;
    }

    public String getMessage() { return message == null ? "https://paesserver.de/iota-jammer.html" : message; }

    public int getReconnect() {
        return reconnect;
    }

    public int getMwm(){
        return mwm == 0 ? 14 : mwm;
    }

    public int getDepth() {
        return depth == 0 ? 4 : depth;
    }

    public boolean isLocalPOW() {
        return localPOW;
    }

    public int getThreadAmount(){ return threadAmount;}

    public boolean isDEBUG_MODE() {
        return DEBUG_MODE;
    }
}
