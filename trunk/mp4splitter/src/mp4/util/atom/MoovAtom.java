/**
 * 
 */
package mp4.util.atom;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The movie atom is a top-level atom.  It contains the metadata for a presentation.
 */
public class MoovAtom extends ContainerAtom {
  // the movie header atom
  private MvhdAtom mvhd;
  // initial object descriptor 
  private IodsAtom iods;
  // the user data atom
  private UdtaAtom udta;
  // the list of tracks
  private List<TrakAtom> traks;
  
  /**
   * Constructor for movie atom
   */
  public MoovAtom() {
    super(new byte[]{'m','o','o','v'});
  }
  
  /**
   * Copy constructor for movie atom.  Performs a deep copy.
   * @param old the movie atom to copy
   */
  public MoovAtom(MoovAtom old) {
    super(old);
    mvhd = new MvhdAtom(old.mvhd);
    if (iods != null) {
      iods = new IodsAtom(old.iods);
    }
    if (udta != null) {
      udta = new UdtaAtom(old.udta);
    }
    traks = new LinkedList<TrakAtom>();
    for (Iterator<TrakAtom> i = old.getTracks(); i.hasNext(); ) {
      traks.add(new TrakAtom(i.next()));
    }
  }
  
  /**
   * Return the movie header atom
   * @return the movie header atom
   */
  public MvhdAtom getMvhd() {
    return mvhd;
  }
  
  /**
   * Return the initial object descriptor atom
   * @return the initial object descriptor atom
   */
  public IodsAtom getIods() {
    return iods;
  }
  
  /**
   * Return the user data atom
   * @return the user data atom
   */
  public UdtaAtom getUdta() {
    return udta;
  }
  
  /**
   * Return an iterator with the media's tracks.  For most movies, there are two tracks, the sound 
   * track and the video track.
   * @return an iterator with the movie traks.
   */
  public Iterator<TrakAtom> getTracks() {
    return traks.iterator();
  }
  
  /**
   * Add a child atom to the moov atom.  If the atom is not recognized as a child of moov
   * then a run-time exception is thrown.
   * @param atom the atom to add
   */
  @Override
  public void addChild(Atom atom) {
    if (atom instanceof MvhdAtom) {
      mvhd = (MvhdAtom) atom;
    }
    else if (atom instanceof IodsAtom) {
      iods = (IodsAtom) atom;
    }
    else if (atom instanceof UdtaAtom) {
      udta = (UdtaAtom) atom;
    }
    else if (atom instanceof TrakAtom) {
      if (traks == null) {
        traks = new LinkedList<TrakAtom>();
      }
      traks.add((TrakAtom) atom);
    }
    else {
      throw new AtomError("Can't add " + atom + " to moov");
    }
  }
  
  /**
   * Recompute the size of the moov atom, which needs to be done if
   * any of the child atom sizes have changed.
   */
  @Override
  protected void recomputeSize() {
    long newSize = mvhd.size();
    for (Iterator<TrakAtom> i = getTracks(); i.hasNext(); ) {
      newSize += i.next().size();
    }
    if (iods != null) {
      newSize += iods.size();
    }
    if (udta != null) {
      newSize += udta.size();
    }
    setSize(ATOM_HEADER_SIZE + newSize);
  }

  /**
   * Cut the movie atom at the specified time
   * @param time the time at which the cut is performed.  Must be converted to movie time.
   * @return the new movie atom
   */
  public MoovAtom cut(long time) {
    long movieTimeScale = mvhd.getTimeScale();
    long duration = mvhd.getDuration();
    
    System.out.println("DBG: Movie time " + (duration/movieTimeScale) + " sec, cut at " + time + "sec");
    System.out.println("\tDBG: ts " + movieTimeScale + " cut at " + (time * movieTimeScale));
    
    MoovAtom cutMoov = new MoovAtom();
    cutMoov.mvhd = mvhd.cut();
    if (iods != null) {
      cutMoov.iods = iods.cut();
    }
    if (udta != null) {
      cutMoov.udta = udta.cut();
    }
    cutMoov.traks = new LinkedList<TrakAtom>();
    // iterate over each track and cut the track
    for (Iterator<TrakAtom> i = getTracks(); i.hasNext(); ) {
      TrakAtom cutTrak = i.next().cut(time, movieTimeScale);
      cutMoov.traks.add(cutTrak);
      // need to convert the media timescale to the movie timescale
      long timeScaleRatio = movieTimeScale / cutTrak.getMdia().getMdhd().getTimeScale();
      long cutDuration = cutTrak.getMdia().getMdhd().getDuration() * timeScaleRatio;
      cutTrak.fixupDuration(cutDuration);
      if (cutDuration > cutMoov.mvhd.getDuration()) {
        cutMoov.mvhd.setDuration(cutDuration);
      }
    }
    cutMoov.recomputeSize();
    return cutMoov;
  }
  
  /**
   * Return the byte offset of the first data in the mdat atom.  
   * This is computed by looking at the first entry in the stco atom,
   * which contains mdat offset values.  This method returns the smallest
   * value of any of the tracks.
   * @return the byte offset of the first data.
   */
  public long firstDataByteOffset() {
    long offset = Long.MAX_VALUE;
    for (Iterator<TrakAtom> i = getTracks(); i.hasNext(); ) {
      StcoAtom stco = i.next().getMdia().getMinf().getStbl().getStco();
      if (stco.getChunkOffset(1) < offset) {
        offset = stco.getChunkOffset(1);
      }
    }
    return offset;
  }
  
  @Override
  public void accept(AtomVisitor v) throws AtomException {
    v.visit(this);
  }

  /**
   * Write the moov atom data to the specified output
   * @param out where the data goes
   * @throws IOException if there is an error writing the data
   */
  @Override
  public void writeData(DataOutput out) throws IOException {
    writeHeader(out);
    mvhd.writeData(out);
    if (iods != null) {
      iods.writeData(out);
    }
    for (Iterator<TrakAtom> i = getTracks(); i.hasNext(); ) {
      i.next().writeData(out);
    }
    if (udta != null) {
      udta.writeData(out);
    }
  }
  
  /**
   * Update the fixed offset values in the atom.  This needs to be done if
   * the file contents change.
   * @param delta the change in file size
   */
  public void fixupOffsets(long delta) {
    for (Iterator<TrakAtom> i = getTracks(); i.hasNext(); ) {
      i.next().fixupOffsets(delta);
    }
  }
}