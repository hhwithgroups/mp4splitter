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
  
  public long getEntries() {
    return data.getUnsignedInt(ENTRIES_OFFSET);
  }
  
  public long getDuration() {
    return data.getUnsignedInt(TABLE_OFFSET + TRACK_DURATION);
  }
  
  public long getTime() {
    return data.getUnsignedInt(TABLE_OFFSET + MEDIA_TIME);
  }
  
  public long getRate() {
    return data.getUnsignedInt(TABLE_OFFSET + MEDIA_RATE);
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this);
  }
}