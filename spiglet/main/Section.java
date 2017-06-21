package spiglet.main;

public class Section {
    public int tempID, start, end;
    public boolean hasCrossedCall;

    public Section(int tempID_, int start_, int end_) {
        tempID = tempID_;
        start = start_;
        end = end_;
        hasCrossedCall = false;
    }

    //Sort the section according to 'start'
    public int compareAsStart(Section compareSection) {
        return this.start - compareSection.start;
    }

    //Sort the section according to 'end'
    public int compareAsEnd(Section compareSection) {
        return this.end - compareSection.end;
    }

}
