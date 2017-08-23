package indi.shinado.piping.pipes.entity;

import android.annotation.TargetApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import indi.shinado.piping.launcher.Console;
import indi.shinado.piping.pipes.BasePipe;

public class Pipe implements Comparable<Pipe>, Displayable, Parcelable {

    public static final int TYPE_SEARCHABLE = 100;

    public static final int TYPE_ACTION = 10;

    private String displayName = "";

    private String desc;

    private int id;

    private SearchableName searchableName = new SearchableName("");

    private List<Pipe> acceptableParams = new ArrayList<>();

    private PreviousPipes previous;

    private Instruction instruction;

    private String executable = "";

    private BasePipe basePipe;

    private int keyIndex;

    private int frequency;

    //deprecated,
    private int typeIndex = TYPE_ACTION;

    private boolean ignoreInput = false;

    //for analysis
    public String executeBody = "";

    public Pipe() {
    }

    public Pipe(String exe) {
        this.executable = exe;
    }

    public Pipe(int id) {
        this();
        this.id = id;
    }

    public Pipe(int id, String simple) {
        this(id);
        this.displayName = simple;
        this.searchableName = new SearchableName(simple);
        this.executable = simple;
    }

    public Pipe(int id, Instruction instruction) {
        this(id);
        this.instruction = instruction;
    }

    public Pipe(int id, String displayName, SearchableName searchableName) {
        this.id = id;
        this.displayName = displayName;
        this.searchableName = searchableName;
    }

    public Pipe(int id, String displayName, SearchableName searchableName, String executable) {
        this.id = id;
        this.displayName = displayName;
        this.searchableName = searchableName;
        this.executable = executable;
    }

    public SearchableName getSearchableName() {
        return searchableName;
    }

    public void setSearchableName(SearchableName searchableName) {
        this.searchableName = searchableName;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction value) {
        this.instruction = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDisplayName(String displayName, String desc) {
        this.displayName = displayName;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PreviousPipes getPrevious() {
        return previous == null ? new PreviousPipes() : previous;
    }

    public void setPrevious(PreviousPipes previous) {
        if (previous == null) {
            this.previous = new PreviousPipes();
        } else {
            this.previous = previous;
        }
    }

    public BasePipe getBasePipe() {
//        if (basePipe == null) {
//            return new NullPipe(-1);
//            throw new BasePipeNotSetException(getClass().getSimpleName() + " must set BasePipe in getDefaultPipe() in BasePipe");
//        }
        return basePipe;
    }

    public boolean startExecution(BasePipe.OutputCallback callback) {
        if (basePipe != null) {
            basePipe.startExecution(this, callback);
            return true;
        } else {
            return false;
        }
    }

    public boolean startExecution() {
        if (basePipe != null) {
            basePipe.startExecution(this);
            return true;
        } else {
            return false;
        }
    }

    public void setBasePipe(BasePipe basePipe) {
        this.basePipe = basePipe;
    }

    public void input() {
        getBasePipe().getConsole().input(displayName);
    }

    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public int getKeyIndex() {
        return keyIndex;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean ignoreInput() {
        return ignoreInput;
    }

    /**
     * when set true, the input pipe will not execute
     * therefore it gets an empty string as input
     */
    public void setIgnoreInput(boolean ignoreInput) {
        this.ignoreInput = ignoreInput;
    }

    @Override
    public int compareTo(@NonNull Pipe another) {
        //search results always ahead of action
//        int compare = another.typeIndex - typeIndex;
        //same type
//        if (compare == 0) {

        if (another.getExecutable().equals(executable)) {
            return 0;
        }

        int compare = keyIndex - another.keyIndex;

        //same key index
        if (compare == 0) {
            compare = another.frequency - frequency;

            //same frequency
            if (compare == 0) {
                compare = another.getSearchableName().toString().compareTo(
                        searchableName.toString());
                if (compare == 0) {
                    compare = another.getExecutable().compareTo(executable);
                }
            }
        }
//        }
        return compare;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pipe) {
            return executable.equals(((Pipe) o).executable);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return executable.hashCode();
    }

    public void addFrequency() {
        this.frequency++;
    }

    public List<Pipe> getAcceptableParams() {
        return acceptableParams;
    }

    public void setAcceptableParams(List<Pipe> acceptableParams) {
        this.acceptableParams = acceptableParams;
    }

    public void setAcceptableParams(Pipe... params) {
        acceptableParams = new ArrayList<>();
        Collections.addAll(acceptableParams, params);
    }

    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @TargetApi(12)
    public void onSelectedAsStart() {
        BasePipe basePipe = getBasePipe();
        if (basePipe != null) {
            basePipe.onSelectedAsStart(this);
        }
    }

    @TargetApi(12)
    public void onUnselectedAsStart() {
        BasePipe basePipe = getBasePipe();
        if (basePipe != null) {
            basePipe.onUnselectedAsStart(this);
        }
    }

    @TargetApi(5)
    public void onSelectedAsPrevious() {
        //NullPointerException caused by uninstall and re-install
        BasePipe basePipe = getBasePipe();
        if (basePipe != null) {
            basePipe.onSelectedAsPrevious(this);
            //TODO think about it
//            Console console = basePipe.getConsole();
//            if (console != null) console.setIndicator(getDisplayName());
        }
    }

    @TargetApi(10)
    public void onPreviousDeselected() {
        BasePipe basePipe = getBasePipe();
        if (basePipe != null) {
            basePipe.onPreviousDeselected(this);
            Console console = basePipe.getConsole();
            if (console != null) {
                Log.d("Indicator", "when previous unselected");
                console.setIndicator("");
            }
        }
    }

    @Override
    public int describeContents() {
        return id;
    }


    public static class PreviousPipes {
        private TreeSet<Pipe> previous;
        private int pointer = 0;

        public PreviousPipes() {
            //default
        }

        public PreviousPipes(Pipe pipe) {
            previous = new TreeSet<>();
            previous.add(pipe);
        }

        public PreviousPipes(PreviousPipes prev) {
            if (prev == null) {
                return;
            }
            this.previous = new TreeSet<>();
            this.previous.addAll(prev.getPrevious());
            this.pointer = prev.getPointer();
        }

        public PreviousPipes(TreeSet<Pipe> previous, int pointer) {
            this.previous = new TreeSet<>();
            this.previous.addAll(previous);
            this.pointer = pointer;
        }

        public TreeSet<Pipe> getPrevious() {
            return previous;
        }

        public void setPrevious(TreeSet<Pipe> previous) {
            this.previous = previous;
        }

        public int getPointer() {
            return pointer;
        }

        public void setPointer(int pointer) {
            this.pointer = pointer;
        }

        public void clear() {
            if (previous != null) {
                previous.clear();
            }
            pointer = 0;
        }

        public Pipe get() {
            return previous == null || previous.size() == 0 ? null :
                    (Pipe) previous.toArray()[pointer];
        }

        //TODO
        public TreeSet<Pipe> getMultiple() {
            return previous;
        }

        public TreeSet<Pipe> getAll() {
            return previous;
        }

        public boolean isEmpty() {
            return previous == null || previous.isEmpty();
        }
    }

    public Pipe(Pipe another) {
        this.setBasePipe(another.getBasePipe());
        this.setIgnoreInput(another.ignoreInput());
        this.setDisplayName(another.getDisplayName());
        this.setExecutable(another.getExecutable());
        this.setFrequency(another.getFrequency());
        this.setId(another.getId());
        this.setInstruction(another.getInstruction());
        this.setKeyIndex(another.getKeyIndex());
        this.setPrevious(another.getPrevious());
        this.setSearchableName(another.getSearchableName());
        this.setTypeIndex(another.getTypeIndex());
        this.setAcceptableParams(another.getAcceptableParams());
    }

    //--------------------- parcel ---------------------//
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(executable);
        dest.writeString(displayName);
    }

    public Pipe(Parcel in) {
        id = in.readInt();
        executable = in.readString();
        displayName = in.readString();
    }

    public static final Creator CREATOR = new Creator() {
        public Pipe createFromParcel(Parcel in) {
            return new Pipe(in);
        }

        @Override
        public Pipe[] newArray(int size) {
            return new Pipe[size];
        }
    };

    public static Pipe ofScript(int id, String exe) {
        Pipe pipe = new Pipe(id);
        pipe.executable = exe;
        return pipe;
    }

}