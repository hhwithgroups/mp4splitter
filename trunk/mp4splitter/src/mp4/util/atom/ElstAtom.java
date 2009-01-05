/**
 * 
 */
package mp4.util.atom;

/**
 * The edit list atom.
 */
public class ElstAtom extends LeafAtom {
  private static final int ENTRIES_OFFSET = 4;
  private static final int TABLE_OFFSET = 8;
  
  private static final int TRACK_DURATION = 0;
  private static final int MEDIA_TIME = 4;
  private static final int MEDIA_RATE = 8;
  
  private static final int ENTRY_SIZE = 12;
  
  /**
   * Construct an empty elst atom.
   */
  public ElstAtom() {
    super(new byte[]{'e','l','s','t'});
  }
  
  /**
   * Copy constructor.  Performs a deep copy.
   * @param old the version to copy
   */
  public ElstAtom(ElstAtom old) {
    super(old);
  }
  /**
   * Set a new duration for each elst entry.
   * @param duration the new duration value
   */
  public void setDuration(long duration) {
    for (int i = 0; i < getNumEntries(); i++) {
      setDuration(i, duration);
    }
  }

  /**
   * Return the number of entries in the edit list
   * @return the number of entries in the edit list
   */
  public long getNumEntries() {
    return data.getUnsignedInt(ENTRIES_OFFSET);
  }
  
  /**
   * Return the track duration for the specified index
   * @param index the table index
   * @return the track duration for the specified index
   */
  public long getDuration(int index) {
    return data.getUnsignedInt(TABLE_OFFSET + (index * ENTRY_SIZE) + TRACK_DURATION);
  }
  
  /**
   * Set the track duration for the specified edit list entry
   * @param index the table index
   * @param val the new duration
   */
  public void setDuration(int index, long val) {
    data.addUnsignedInt(TABLE_OFFSET + (index * ENTRY_SIZE) + TRACK_DURATION, val);
  }
  
  /**
   * Return the media time for the specified index
   * @param index the table index
   * @return the media time for the specified index
   */
  public long getMediaTime(int index) {
    return data.getUnsignedInt(TABLE_OFFSET + (index * ENTRY_SIZE) + MEDIA_TIME);
  }
  
  /**
   * Return the media rate for the specified index.  The media rate is a 
   * fixed point value.  The first 16-bits is the integer and the next 16-bits
   * is the fraction.
   * @param index the table index
   * @return the media rate for the specified index
   */
  public double getMediaRate(int index) {
    return data.getFixedPoint(TABLE_OFFSET + (index * ENTRY_SIZE) + MEDIA_RATE);
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this);
  }
}