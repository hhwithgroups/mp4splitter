package mp4.util.atom;

/**
 * The time-to-sample atom is shared by the ctts and stts atoms, which
 * are similar in functionality.  The stts atom is the decoding
 * time-to-sample mapping atom.  The decoding time (DT) atom
 * gives the deltas between successive decoding times.  The ctts atom
 * is the composition time-to-sample atom.  The composition time (CT) atom
 * provides composition times.  When the decoding and composition times are 
 * the same, then the ctts atom is not present.
 */
public abstract class TimeToSampleAtom extends LeafAtom {
  protected static final int ENTRIES_OFFSET = 4;
  protected static final int TABLE_OFFSET = 8;
  protected static final int SAMPLE_COUNT = 0;
  protected static final int SAMPLE_DURATION = 4;
  protected static final int ENTRY_SIZE = 8;

  protected TimeToSampleAtom(byte[] type) {
    super(type);
  }
  
  protected TimeToSampleAtom(TimeToSampleAtom old) {
    super(old);
  }
  /**
   * Allocate space for the data in the atom
   * @param numEntries the number of entries in the atom
   */
  @Override
  public void allocateData(long numEntries) {
    long size = TABLE_OFFSET + (numEntries * ENTRY_SIZE);
    super.allocateData(size);
  }

  /**
   * Return the number of entries in the table.
   * @return the number of entries in the table
   */
  public final long getNumEntries() {
    return data.getUnsignedInt(ENTRIES_OFFSET);
  }
  
  /**
   * Set the number of entries in the stts table
   * @param numEntries the number of entries
   */
  public final void setNumEntries(long numEntries) {
    data.addUnsignedInt(ENTRIES_OFFSET, numEntries);
  }
  
  /**
   * Return the sample count for the specified index
   * @param index the index into the stts table
   * @return the sample count
   */
  public final long getSampleCount(int index) {
    return data.getUnsignedInt(TABLE_OFFSET + (index * ENTRY_SIZE) + SAMPLE_COUNT);
  }
  
  /**
   * Set the sample count for the specified entry
   * @param index the index in the table
   * @param sc the sample count value
   */
  public final void setSampleCount(int index, long sc) {
    data.addUnsignedInt(TABLE_OFFSET + (index * ENTRY_SIZE) + SAMPLE_COUNT, sc);
  }
  
  /**
   * Return the sample duration at the specified index
   * @param index the index into the stts table
   * @return the sample duration
   */
  public final long getSampleDuration(int index) {
    return data.getUnsignedInt(TABLE_OFFSET + (index * ENTRY_SIZE) + SAMPLE_DURATION);
  }
  /**
   * Set the sample duration for the specified entry
   * @param index the table index
   * @param duration the duration value for the specified entry
   */
  public final void setSampleDuration(int index, long duration) {
    data.addUnsignedInt(TABLE_OFFSET + (index * ENTRY_SIZE) + SAMPLE_DURATION, duration);
  }
  
  /**
   * Given a time in the media return the data sample.
   * @param time the media time value
   * @return the sample number for the specified time
   */
  public long timeToSample(long time) {
    long entries = getNumEntries();
    long lowerBoundTime = 0;
    long lowerBoundSample = 0;
    for (int i = 0; i < entries; i++) {
      long count = getSampleCount(i);
      long duration = getSampleDuration(i);
      if ((time - lowerBoundTime) < (count * duration)) {
        return ((time - lowerBoundTime) / duration) + lowerBoundSample;
      }
      lowerBoundTime += count * duration;
      lowerBoundSample += count;
    }
    return 0;    
  }
  
  protected void cut(long sampleNum, TimeToSampleAtom cutAtom) {
    // search the table for the specified sample 
    long numEntries = getNumEntries();
    long upperBoundSample = 0;
    int i;
    for (i = 0; i < numEntries; i++) {
      long count = getSampleCount(i);
      upperBoundSample += count;
      if (sampleNum <= upperBoundSample) {
        // we've found the stts entry that contains this sample
        // create a new stts entry with the new count
        break;
      }
    }
    // create the new table
    long newCount = upperBoundSample - sampleNum + 1;
    long newNumEntries = numEntries - i;
    // add the new number of entries to the table
    cutAtom.allocateData(newNumEntries);
    cutAtom.setNumEntries(newNumEntries);
    // add the new first entry 
    int entryNumber = 0;
    cutAtom.setSampleCount(entryNumber, newCount);
    cutAtom.setSampleDuration(entryNumber, getSampleDuration(i));
    entryNumber++;
    // copy the rest of the entries from the old table to the new table
    for (i++; i < numEntries; i++, entryNumber++) {
      cutAtom.setSampleCount(entryNumber, getSampleCount(i));
      cutAtom.setSampleDuration(entryNumber, getSampleDuration(i));
    }    
  }

  public abstract void accept(AtomVisitor v) throws AtomException;

  /**
   * Compute the duration of the samples in the track.  This atom contains
   * the duration of each sample.  This method iterates over the table 
   * to compute the duration of all the samples
   * @return the duration of the track
   */
  public long computeDuration() {
    long duration = 0;
    for (long i = 0; i < getNumEntries(); i++) {
      duration += (getSampleCount((int)i) * getSampleDuration((int)i));
    }
    return duration;
  }

}
