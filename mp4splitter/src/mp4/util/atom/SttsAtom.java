package mp4.util.atom;

/**
 * The decoding time-to-sample atom stores duration info for a media's 
 * samples providing a mapping from the time in a media to a data sample. 
 * 
 * The atom contains a table of sample entries. Each entry contains a 
 * sample count and sample duration.  The delta's are computed as:
 *   DT(n+1) = DT(n) + STTS(n) where STTS(n) is the uncompressed table
 *   entry for sample n
 * The sum of all the deltas gives the length of the media in the track.
 * The edit list atom provides the initial CT value if it is non-empty.
 */
public class SttsAtom extends TimeToSampleAtom {
  /**
   * Constructor for the time-to-sample atom
   */
  public SttsAtom() {
    super(new byte[]{'s','t','t','s'});
  }
  
  /**
   * Copy constructor
   * @param old the atom to copy
   */
  public SttsAtom(TimeToSampleAtom old) {
    super(old);
  }
  
  /**
   * Cut the atom at the specified sample.  This method creates a new 
   * stts atom with the new data.  This method searches through the table
   * looking for the appropriate sample.  Once found a new table entry
   * needs to be created, but the subsequent entries remain the same.
   * Any preceding entry is ignored.
   * 
   * @param sampleNum the sample where the atom should be cut
   * @return a new stts atom with the new data
   */
  public SttsAtom cut(long sampleNum) {
    SttsAtom cutStts = new SttsAtom();
    super.cut(sampleNum, cutStts);
    return cutStts;
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }
}