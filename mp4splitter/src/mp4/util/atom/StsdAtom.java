/**
 * 
 */
package mp4.util.atom;

/**
 * The sample description atom.
 */
public class StsdAtom extends LeafAtom {
  private static final int ENTRY_OFFSET = 4;
  
  /**
   * Constructor that creates an empty stsd atom.
   */
  public StsdAtom() {
    super(new byte[]{'s','t','s','d'});
  }
  
  /**
   * Copy constructor.  Performs a deep copy
   * @param old the version to copy
   */
  public StsdAtom(StsdAtom old) {
    super(old);
  }
  
  /**
   * Cut the stsd atom at the specified time.  Nothing changes for the sample
   * description atom
   * @return a new stsd atom 
   */
  public StsdAtom cut() {
    return new StsdAtom(this);
  }
  
  /**
   * Return the number of entries in the stsd atom.
   * @return the number of entries in the stsd atom
   */
  public long getNumEntries() {
    return data.getUnsignedInt(ENTRY_OFFSET);
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
}