/**
 * 
 */
package mp4.util.atom;

import java.io.DataOutput;
import java.io.IOException;

/**
 * The container atom for a single track of a presentation. Movie presentation typically
 * have two tracks, one for sound and one for video.
 */
public class TrakAtom extends ContainerAtom {
  // the track header
  private TkhdAtom tkhd;
  // the media information
  private MdiaAtom mdia;
  // the edit list container
  private EdtsAtom edts;
  // user data
  private UdtaAtom udta;
  
  /**
   * Constructor
   */
  public TrakAtom() {
    super(new byte[]{'t','r','a','k'});
  }
  
  /**
   * Copy constructor.  Perofrms a deep copy.
   * @param old the version to copy
   */
  public TrakAtom(TrakAtom old) {
    super(old);
    tkhd = new TkhdAtom(old.tkhd);
    mdia = new MdiaAtom(old.mdia);
    if (old.edts != null) {
      edts = new EdtsAtom(old.edts);
    }
    if (old.udta != null) {
      udta = new UdtaAtom(old.udta);
    }
  }

  public TkhdAtom getTkhd() { 
    return tkhd; 
  }

  public MdiaAtom getMdia() { 
    return mdia; 
  }

  public EdtsAtom getEdts() {
    return edts;
  }
  
  public UdtaAtom getUdta() {
    return udta;
  }
  
  /**
   * Add an atom to the container.  If the atom is not recognized, then
   * a run-time error is thrown.
   * @param child the atom to add to the trak atom
   */
  @Override
  public void addChild(Atom child) {
    if (child instanceof TkhdAtom) {
      tkhd = (TkhdAtom) child;
    }
    else if (child instanceof MdiaAtom) {
      mdia = (MdiaAtom) child;
    }
    else if (child instanceof EdtsAtom) {
      edts = (EdtsAtom) child;
    }
    else if (child instanceof UdtaAtom) {
      udta = (UdtaAtom) child;
    }
    else {
      throw new AtomError("Can't add " + child + " to trak");
    }
  }
  
  /**
   * Recompute the size of the track atom, which needs to be done if
   * the contents change.
   */
  @Override
  protected void recomputeSize() {
    long newSize = tkhd.size() + mdia.size();
    if (edts != null) {
      newSize += edts.size();
    }
    if (udta != null) {
      newSize += udta.size();
    }
    setSize(ATOM_HEADER_SIZE + newSize);
  }

  /**
   * Cut the trak atom at the specified time (seconds).  The time needs to be normalized
   * to the media's timescale.
   * @param time the time in seconds to cut the track 
   * @param movieTimeScale the timescale for the movie
   * @return a new trak atom that has been cut
   */
  public TrakAtom cut(long time, long movieTimeScale) {
    TrakAtom cutTrak = new TrakAtom();
    long mediaTimeScale = mdia.getMdhd().getTimeScale();
    long mediaTime = time * mediaTimeScale;
    System.out.println("DBG: media time " + mediaTime);
    if (edts != null) {
      //mediaTime = edts.editTime(time, mediaTimeScale, movieTimeScale);
      //System.out.println("DBG: media time after edit " + mediaTime);
    }
    cutTrak.tkhd = tkhd.cut(mediaTime);
    cutTrak.mdia = mdia.cut(mediaTime);
    if (edts != null) {
      cutTrak.edts = edts.cut();
    }
    if (udta != null) {
      cutTrak.udta = udta.cut();
    }
    cutTrak.recomputeSize();
    return cutTrak;
  }

  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this); 
  }

  /**
   * Write the trak atom data to the specified output
   * @param out where the data goes
   * @throws IOException if there is an error writing the data
   */
  @Override
  public void writeData(DataOutput out) throws IOException {
    writeHeader(out);
    tkhd.writeData(out);
    mdia.writeData(out);
    if (edts != null) {
      edts.writeData(out);
    }
    if (udta != null) {
      udta.writeData(out);
    }
  }
  
  /**
   * Change the duration of the track.  This requires changing the duration in the track
   * header and the edit list.
   * @param duration the new track duration.
   */
  public void fixupDuration(long duration) {
    tkhd.setDuration(duration);
    if (edts != null) {
      edts.getElst().setDuration(duration);
    }
  }

  /**
   * Fixup the chunk offsets values located in the stco atom.  This needs
   * to be done if the size of any atoms has changed since the chunk offset
   * values are absolute values from the start of the file.
   * @param delta the amount to update each chunk offset.
   */
  public void fixupOffsets(long delta) {
    getMdia().getMinf().getStbl().getStco().fixupOffsets(delta);
  }
}